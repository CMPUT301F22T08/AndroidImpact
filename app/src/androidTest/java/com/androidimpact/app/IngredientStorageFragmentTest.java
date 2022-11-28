package com.androidimpact.app;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.android.gms.common.internal.Asserts.checkNotNull;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.androidimpact.app.activities.LoginActivity;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.ingredients.StoreIngredientViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Objects;
import java.util.UUID;

/**
 * This tests the ingredient storage fragment, adding, and editing an ingredient
 * Written using the built in espresso test recorder
 * @version 1.0
 * @author Curtis Kan, Joshua Ji
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4.class)
public class IngredientStorageFragmentTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Login beforehand with test account, move to ingredient fragment
     */
    @Before
    public void login() throws InterruptedException {
        // In case we aren't logout yet for some reason
        logout();

        // Click on username edit text and change name
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.username),
                        childAtPosition(
                                allOf(withId(R.id.login_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("test@gmail.com"));

        // Click on password edit text and change password
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                allOf(withId(R.id.login_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("qwerty"));

        // Click on the login button
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.login), withText("Login"),
                        childAtPosition(
                                allOf(withId(R.id.login_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                5),
                        isDisplayed()));
        materialButton.perform(click());

        Thread.sleep(1000);
    }

    /**
     * Make sure on the right fragment
     * Result: ensures ensuing tests are on the right fragment
     */
    @Test
    public void A_testFragmentTitle() {

        // Make sure on IngredientStorage fragment
        ViewInteraction textView = onView(
                allOf(withText("Ingredient Storage"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView.check(matches(withText("Ingredient Storage")));
    }

    /**
     * Test add, edit and delete
     * Result: adds a new ingredient, edits that ingredient, and deletes it, so no changes are made
     * overall after the test is done
     */
    @Test
    public void B_addEditDeleteTest() throws InterruptedException {

        String ingredientDescription = UUID.randomUUID().toString().substring(0, 25);
        String newIngredientDescription = UUID.randomUUID().toString().substring(0, 25);

        addItem(ingredientDescription);
        editItem(ingredientDescription, newIngredientDescription);
        deleteItem(newIngredientDescription);

    }

    /**
     * Method to test adding an ingredient to the storage
     * @param ingredientDescription
     *      Name of the ingredient to put in
     */
    private void addItem(String ingredientDescription) throws InterruptedException {
        // Click on global fab
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.navbarFAB), withContentDescription("Multipurpose FAB in Navbar"),
                        childAtPosition(
                                allOf(withId(R.id.combined_bottom),
                                        childAtPosition(
                                                withId(R.id.main_activity_layout),
                                                2)),
                                1),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Check if on add ingredient activity
        ViewInteraction textView2 = onView(
                allOf(withText("Add Ingredient"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView2.check(matches(withText("Add Ingredient")));

        // Set description to Cheese
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.ingredientStoreAdd_description),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ingredient_store_description_layout),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText(ingredientDescription), closeSoftKeyboard());

        // Set amount to 2
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.ingredientStoreAdd_amount),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ingredient_store_amount_layout),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("2"), closeSoftKeyboard());

        // Set location to first item in spinner
        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.ingredientStoreAdd_location),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                6),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        Thread.sleep(1000);

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView.perform(click());

        // Set unit to first item in spinner
        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.ingredientStoreAdd_unit),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                13),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView2.perform(click());

        // Set category to dairy in spinner
        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.ingredientStoreAdd_category),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatSpinner3.perform(click());

        DataInteraction appCompatCheckedTextView3 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView3.perform(click());

        // Set date as current date
        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.ingredientStoreAdd_bestBefore),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ingredient_store_best_before_layout),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        Thread.sleep(1000);

        // Confirm the add by clicking confirm
        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.ingredientStoreAdd_confirmBtn), withText("Confirm"),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                9),
                        isDisplayed()));
        materialButton3.perform(click());

        Thread.sleep(1000);

        // Check if the ingredient is in the list
        onView(withId(R.id.ingredient_listview))
                .perform(RecyclerViewActions.actionOnHolderItem(
                        storeIngredientVHMatcher(equalTo(ingredientDescription)),
                        scrollTo()
                )).check(matches(hasDescendant(withText(ingredientDescription))));
    }

    /**
     * Method to test editing an ingredient
     * @param ingredientDescription
     *      The name of the current ingredient to edit
     * @param newIngredientDescription
     *      The name of the new description of the ingredient
     */
    private void editItem(String ingredientDescription, String newIngredientDescription) throws InterruptedException {
        // Click on the "name" in the IngredientStorage
        // https://developer.android.com/reference/android/support/test/espresso/contrib/RecyclerViewActions.html
        onView(withId(R.id.ingredient_listview))
                .perform(RecyclerViewActions.actionOnHolderItem(
                        storeIngredientVHMatcher(equalTo(ingredientDescription)),
                        MyViewAction.clickChildViewWithId(R.id.edit_store_ingredient)
                ));

        Thread.sleep(1000);

        // Change description to something new
        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.ingredientStoreAdd_description),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ingredient_store_description_layout),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText(newIngredientDescription));


        // Confirm the edit
        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.ingredientStoreAdd_confirmBtn), withText("Confirm"),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                9),
                        isDisplayed()));
        materialButton4.perform(click());

        Thread.sleep(1000);

        // See if the new ingredientDescription is in the list
        // Check if the ingredient is in the list
        onView(withId(R.id.ingredient_listview))
                .perform(RecyclerViewActions.actionOnHolderItem(
                        storeIngredientVHMatcher(equalTo(newIngredientDescription)),
                        scrollTo()
                )).check(matches(hasDescendant(withText(newIngredientDescription))));
    }

    /**
     * Method to test deleting an ingredient
     * @param newIngredientDescription
     *      Name of the ingredient to delete
     */
    private void deleteItem(String newIngredientDescription) throws InterruptedException {

        // https://stackoverflow.com/questions/56578699/espressotest-swipe-to-delete-item-of-recyclerview-inside-viewpager
        // ricocarpe Oct 19, 2019
        // This is supposed to simulate a swipe to delete action on the first list view item
        // But for some reason it was not working. There is no documentation from espresso on
        // this either.
        onView(withId(R.id.ingredient_listview))
                .perform(RecyclerViewActions.actionOnHolderItem(
                        storeIngredientVHMatcher(equalTo(newIngredientDescription)),
                        swipeRight()
                ));

        // Assume that we swiped to delete, delete the item from db
        FirebaseFirestore db;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String uid = currentUser.getUid();

        db = FirebaseFirestore.getInstance();
        db.document("userData/" + uid).collection("ingredientStorage")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (Objects.equals(document.getData().get("description"), newIngredientDescription)) {
                                    String id = document.getId();
                                    db.document("userData/" + uid).collection("ingredientStorage").document(id).delete();
                                }

                            }
                        }
                    }
                });

        Thread.sleep(2000);
        Matcher<View> hasNoIngredient = not(hasIngredientDescription(equalTo(newIngredientDescription)));
        onView(withId(R.id.ingredient_listview)).check(matches(hasNoIngredient));
    }

    /**
     * Logout after each test
     */
    @After
    public void logout() {

        // If we aren't on apage with a login screen, handle the exception
        try {
            ViewInteraction actionMenuItemView = onView(
                    allOf(withId(R.id.logout),
                            childAtPosition(
                                    childAtPosition(
                                            withId(androidx.constraintlayout.widget.R.id.action_bar),
                                            1),
                                    0),
                            isDisplayed()));
            actionMenuItemView.perform(click());
        }
        catch (NoMatchingViewException nmve) {
            Log.d("e", "no logout button");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this is a helper function that matches an item in the RecyclerView by its description.
    public static Matcher<StoreIngredientViewAdapter.StoreIngredientViewHolder> storeIngredientVHMatcher(Matcher<String> descriptionMatcher){
        return new TypeSafeMatcher<>(){
            @Override
            public boolean matchesSafely(StoreIngredientViewAdapter.StoreIngredientViewHolder ingredientViewHolder) {
                Log.i("storeIngredientVHMatcher", ingredientViewHolder.getDescriptionValue() + " = " + descriptionMatcher.toString());
                return descriptionMatcher.matches(ingredientViewHolder.getDescriptionValue());
            }

            @Override
            public void describeTo(Description description) {
                // this is only used for verbose logs apparently
                // https://thiagolopessilva.medium.com/creating-custom-viewmatcher-for-espresso-75dde62dd173
                description.appendText("ViewHolder with description: " + descriptionMatcher.toString());
            }
        };
    }

    // Access edit item from recycler view item
    // https://stackoverflow.com/questions/28476507/using-espresso-to-click-view-inside-recyclerview-item
    // blade May 20, 2015
    public static class MyViewAction {

        public static ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }

    }

    // matches an item in RecyclerView
    // https://stackoverflow.com/a/53289078
    public static Matcher<View> hasIngredientDescription(Matcher<String> matcher) {
        return new TypeSafeMatcher<>(){
            @Override
            public boolean matchesSafely(View recyclerView) {
                // bruh this code is so scuffed
                if (!(recyclerView instanceof RecyclerView)) {
                    return false;
                }
                RecyclerView recyclerView1 = (RecyclerView) recyclerView;
                StoreIngredientViewAdapter adapter = (StoreIngredientViewAdapter) recyclerView1.getAdapter();
                if (adapter == null) {
                    return false;
                }
                for (int position = 0; position < adapter.getItemCount(); position++) {
                    StoreIngredient ingredient = adapter.getItem(position);
                    if (matcher.matches(ingredient.getDescription())) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                // this is only used for verbose logs apparently
                // https://thiagolopessilva.medium.com/creating-custom-viewmatcher-for-espresso-75dde62dd173
                description.appendText("Has description: ");
                matcher.describeTo(description);
            }
        };
    }

    /**
     * A bunch of generated code from the espresso screen recorder
     */
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
