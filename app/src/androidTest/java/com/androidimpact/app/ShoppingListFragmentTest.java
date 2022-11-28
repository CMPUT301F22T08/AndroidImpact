package com.androidimpact.app;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.androidimpact.app.activities.LoginActivity;
import com.androidimpact.app.ingredients.StoreIngredientViewAdapter;
import com.androidimpact.app.shopping_list.ShopIngredientAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

/**
 * Intent testing for Shopping List Activity
 * NOTE: these test were generated using Espresso Test Recorder
 *
 * NOTE: these tests are FLAKEY sometimes. Nothing is changed and it works most of the time but
 * sometimes doesn't. Try re running if it doesn't work.
 *
 * NOTE: for some reason when running all the tests at once, this test takes forever to run.
 * it should work when run individually
 * @version 1.0
 * @author Curtis Kan
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ShoppingListFragmentTest {

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
     * Tests to make sure we're on the right fragment
     */
    @Test
    public void A_checkFragmentTitle() {

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.cart_icon), withContentDescription("Shopping List"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction textView = onView(
                allOf(withText("Shopping List"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView.check(matches(withText("Shopping List")));
    }

    /**
     * Tests adding an item to the ingredient storage
     */
    @Test
    public void B_addToIngredientStorage() {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.cart_icon), withContentDescription("Shopping List"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

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

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.shopping_item_addEdit_description),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ingredient_store_description_layout),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("Ken Wong's Ponytail"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.shopping_item_addEdit_amount),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("5"), closeSoftKeyboard());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.shopping_item_addEdit_unit),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                9),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.shopping_item_addEdit_unit),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                9),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatCheckedTextView2.perform(click());

        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.shopping_item_addEdit_category),
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
                .atPosition(2);
        appCompatCheckedTextView3.perform(click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.shopping_item_addEdit_amount), withText("5"),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText6.perform(pressImeActionButton());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.shopping_item_addEdit_confirmBtn), withText("Confirm"),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                6),
                        isDisplayed()));
        materialButton2.perform(click());
    }

    /**
     * Tests adding a shopping list ingredient
     */
    @Test
    public void C_addShoppingListIngredient() {
        // delete shopping list items
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        CollectionReference shoppingListRef = db.collection("userData")
                .document(user.getUid()).collection("shoppingList");

        shoppingListRef.whereEqualTo("description",  "Shopping List Item 1").get()
                .addOnCompleteListener(task -> {
                    List<Task<?>> futures = new ArrayList<>();
                    QuerySnapshot docs = task.getResult();
                    for (QueryDocumentSnapshot doc : docs) {
                        futures.add(shoppingListRef.document(doc.getId()).delete());
                    }
                    Log.i("addShoppingListIngredient", "Deleting " + futures.size() + " elements");
                    Tasks.whenAll(futures).addOnSuccessListener(aVoid -> {
                        ViewInteraction bottomNavigationItemView = onView(
                                allOf(withId(R.id.cart_icon), withContentDescription("Shopping List"),
                                        childAtPosition(
                                                childAtPosition(
                                                        withId(R.id.bottom_navigation_view),
                                                        0),
                                                3),
                                        isDisplayed()));
                        bottomNavigationItemView.perform(click());

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

                        ViewInteraction appCompatEditText4 = onView(
                                allOf(withId(R.id.shopping_item_addEdit_description),
                                        childAtPosition(
                                                childAtPosition(
                                                        withId(R.id.ingredient_store_description_layout),
                                                        0),
                                                0),
                                        isDisplayed()));
                        appCompatEditText4.perform(replaceText("Shopping List Item 1"), closeSoftKeyboard());

                        ViewInteraction appCompatEditText5 = onView(
                                allOf(withId(R.id.shopping_item_addEdit_amount),
                                        childAtPosition(
                                                allOf(withId(R.id.content),
                                                        childAtPosition(
                                                                withId(android.R.id.content),
                                                                0)),
                                                4),
                                        isDisplayed()));
                        appCompatEditText5.perform(replaceText("1"), closeSoftKeyboard());

                        ViewInteraction appCompatSpinner = onView(
                                allOf(withId(R.id.shopping_item_addEdit_unit),
                                        childAtPosition(
                                                allOf(withId(R.id.content),
                                                        childAtPosition(
                                                                withId(android.R.id.content),
                                                                0)),
                                                9),
                                        isDisplayed()));
                        appCompatSpinner.perform(click());

                        DataInteraction appCompatCheckedTextView = onData(anything())
                                .inAdapterView(childAtPosition(
                                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                                        0))
                                .atPosition(1);
                        appCompatCheckedTextView.perform(click());

                        ViewInteraction appCompatSpinner2 = onView(
                                allOf(withId(R.id.shopping_item_addEdit_category),
                                        childAtPosition(
                                                allOf(withId(R.id.content),
                                                        childAtPosition(
                                                                withId(android.R.id.content),
                                                                0)),
                                                0),
                                        isDisplayed()));
                        appCompatSpinner2.perform(click());

                        DataInteraction appCompatCheckedTextView2 = onData(anything())
                                .inAdapterView(childAtPosition(
                                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                                        0))
                                .atPosition(1);
                        appCompatCheckedTextView2.perform(click());

                        ViewInteraction appCompatEditText6 = onView(
                                allOf(withId(R.id.shopping_item_addEdit_amount), withText("1"),
                                        childAtPosition(
                                                allOf(withId(R.id.content),
                                                        childAtPosition(
                                                                withId(android.R.id.content),
                                                                0)),
                                                4),
                                        isDisplayed()));
                        appCompatEditText6.perform(pressImeActionButton());

                        ViewInteraction materialButton2 = onView(
                                allOf(withId(R.id.shopping_item_addEdit_confirmBtn), withText("Confirm"),
                                        childAtPosition(
                                                allOf(withId(R.id.content),
                                                        childAtPosition(
                                                                withId(android.R.id.content),
                                                                0)),
                                                6),
                                        isDisplayed()));
                        materialButton2.perform(click());

                        // See if the new ingredientDescription is in the list
                        // Check if the ingredient is in the list
                        onView(withId(R.id.shop_ingredient_description))
                                .perform(RecyclerViewActions.actionOnHolderItem(
                                        shopIngredientVHMatcher(equalTo("Shopping List Item 1")),
                                        scrollTo()
                                )).check(matches(hasDescendant(withText("Shopping List Item 1"))));
                    });

                });

    }


    /**
     * this is a helper function that matches an item in the RecyclerView by its description.
     * @param descriptionMatcher
     * @return
     */
    public static Matcher<ShopIngredientAdapter.IngredientViewHolder> shopIngredientVHMatcher(Matcher<String> descriptionMatcher){
        return new TypeSafeMatcher<>(){
            /**
             * @param ingredientViewHolder
             * @return
             */
            @Override
            public boolean matchesSafely(ShopIngredientAdapter.IngredientViewHolder ingredientViewHolder) {
                Log.i("shopIngredientVHMatcher", ingredientViewHolder.getDescription() + " = " + descriptionMatcher.toString());
                return descriptionMatcher.matches(ingredientViewHolder.getDescription());
            }

            @Override
            public void describeTo(Description description) {
                // this is only used for verbose logs apparently
                // https://thiagolopessilva.medium.com/creating-custom-viewmatcher-for-espresso-75dde62dd173
                description.appendText("ViewHolder with description: " + descriptionMatcher.toString());
            }
        };
    }

    /**
     * Tests getting an item from meal plan
     */
    @Test
    public void D_getFromMealPlan() {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.recipe_icon), withContentDescription("Recipe"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

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

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.cancel_button), withText("Cancel"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recipe_layout),
                                        8),
                                0),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.cart_icon), withContentDescription("Shopping List"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.navbarFAB), withContentDescription("Multipurpose FAB in Navbar"),
                        childAtPosition(
                                allOf(withId(R.id.combined_bottom),
                                        childAtPosition(
                                                withId(R.id.main_activity_layout),
                                                2)),
                                1),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.shopping_item_addEdit_cancelBtn), withText("Cancel"),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                5),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.meal_icon), withContentDescription("Meal Plan"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                4),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.navbarFAB), withContentDescription("Multipurpose FAB in Navbar"),
                        childAtPosition(
                                allOf(withId(R.id.combined_bottom),
                                        childAtPosition(
                                                withId(R.id.main_activity_layout),
                                                2)),
                                1),
                        isDisplayed()));
        floatingActionButton3.perform(click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.cancel_button), withText("Cancel"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction bottomNavigationItemView4 = onView(
                allOf(withId(R.id.cart_icon), withContentDescription("Shopping List"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView4.perform(click());

        ViewInteraction floatingActionButton4 = onView(
                allOf(withId(R.id.navbarFAB), withContentDescription("Multipurpose FAB in Navbar"),
                        childAtPosition(
                                allOf(withId(R.id.combined_bottom),
                                        childAtPosition(
                                                withId(R.id.main_activity_layout),
                                                2)),
                                1),
                        isDisplayed()));
        floatingActionButton4.perform(click());
    }

    /**
     * Test sorting meal plan
     */
    @Test
    public void E_testSort() {

        // Click on spinner
        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.sort_shopping_spinner),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutShop),
                                        childAtPosition(
                                                withId(R.id.shop_ingredient_input),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatSpinner3.perform(click());

        // Select 2nd item in spinner
        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView2.perform(click());
    }


    /**
     * javadocs_final
     * @param parentMatcher
     * @param position
     * @return
     */
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            /**
             * @param view
             * @return
             */
            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
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
}
