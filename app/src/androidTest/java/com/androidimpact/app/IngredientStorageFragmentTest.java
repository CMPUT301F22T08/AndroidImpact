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

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.DataInteraction;
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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

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
     * Signup in the app beforehand
     * Result: goes to the ingredient storage fragment
     */
    @Before
    public void signup() {
        // Click on the signup button
        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.signup),
                        childAtPosition(
                                allOf(withId(R.id.login_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        materialButton2.perform(click());
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
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("2"), closeSoftKeyboard());

        // Set location to Fridge
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

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatCheckedTextView.perform(click());

        // Set unit to ounce
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
                .atPosition(6);
        appCompatCheckedTextView2.perform(click());

        // Set category to dairy
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
                .atPosition(9);
        appCompatCheckedTextView3.perform(click());

        // Set date as current date
        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.ingredientStoreAdd_bestBefore),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                5),
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
     * TODO: What is this josh
     */
    @Test
    public void pp() throws InterruptedException {
        Thread.sleep(1000);

        ViewInteraction recyclerView = onView(withId(R.id.ingredient_listview));

        String newIngredientDescription = "Water bottle";
        Matcher<View> hasIngredient = hasIngredientDescription(equalTo(newIngredientDescription));
        recyclerView.check(matches(hasIngredient));

        recyclerView.perform(RecyclerViewActions.scrollTo(
                hasDescendant(withText(newIngredientDescription))
        ));

        onView(withText(newIngredientDescription)).check(matches(isDisplayed()));

//        recyclerView.perform(RecyclerViewActions.actionOnHolderItem(
//                storeIngredientVHMatcher(equalTo(newIngredientDescription)),
//                swipeRight()
//        )).check(matches(not(hasIngredient)));

        recyclerView.perform(swipeRight());

        Thread.sleep(1000);
//
//        onView(withId(R.id.ingredient_listview)).check(matches(not(hasIngredient)));
    }

    /**
     * Method to test deleting an ingredient
     * @param newIngredientDescription
     *      Name of the ingredient to delete
     */
    private void deleteItem(String newIngredientDescription) {
        // https://stackoverflow.com/questions/56578699/espressotest-swipe-to-delete-item-of-recyclerview-inside-viewpager
        // ricocarpe Oct 19, 2019

        onView(withId(R.id.ingredient_listview))
                .perform(RecyclerViewActions.actionOnHolderItem(
                        storeIngredientVHMatcher(equalTo(newIngredientDescription)),
                        swipeRight()
                ));

        Matcher<View> hasNoIngredient = not(hasIngredientDescription(equalTo(newIngredientDescription)));
        onView(withId(R.id.ingredient_listview)).check(matches(hasNoIngredient));
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
