package com.androidimpact.app;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.LoginActivity;
import com.androidimpact.app.activities.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * This tests the ingredient storage fragment, adding, and editing an ingredient
 * Written using the built in espresso test recorder
 * @version 1.0
 * @author Curtis Kan
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4.class)
public class IngredientStorageFragmentActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

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
     */
    @Test
    public void A_rightFragmentTest() {

        signup();

        // Make sure on IngredientStorage fragment
        ViewInteraction textView = onView(
                allOf(withText("Ingredient Storage"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView.check(matches(withText("Ingredient Storage")));
    }

    /**
     * Test add ingredient
     */
    @Test
    public void B_addIngredientTest() throws InterruptedException {

        signup();

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
        appCompatEditText.perform(replaceText("Abalone"), closeSoftKeyboard());

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

        // Check if Cheese is in the list
        ViewInteraction textView3 = onView(
                allOf(withId(R.id.store_ingredient_description), withText("Abalone"),
                        withParent(withParent(withId(R.id.linearLayout))),
                        isDisplayed()));
        textView3.check(matches(withText("Abalone")));
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
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }

    }

    /**
     * Test editing an ingredient
     */
    @Test
    public void C_editIngredientTest() throws InterruptedException {

        signup();
        Thread.sleep(1000);
        // Click on first ingredient in list, should be the item just added


        ViewInteraction clickItem = onView(withId(R.id.ingredient_listview));
        clickItem.perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.edit_store_ingredient)));

        Thread.sleep(1000);

        // Change description to Apples
        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.ingredientStoreAdd_description),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ingredient_store_description_layout),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText("Apples"));


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

        // See if Apples is in the list
        ViewInteraction textView4 = onView(
                allOf(withId(R.id.store_ingredient_description), withText("Apples"),
                        withParent(withParent(withId(R.id.linearLayout))),
                        isDisplayed()));
        textView4.check(matches(withText("Apples")));

    }
    /**
     * Test deleting an ingredient
     */
    @Test
    public void D_deleteIngredientTest() {

        // https://stackoverflow.com/questions/56578699/espressotest-swipe-to-delete-item-of-recyclerview-inside-viewpager
        // ricocarpe Oct 19, 2019
        signup();

        onView(withId(R.id.ingredient_listview)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, new GeneralSwipeAction(
                        Swipe.SLOW, GeneralLocation.BOTTOM_RIGHT, GeneralLocation.BOTTOM_LEFT,
                        Press.FINGER)));

        //TODO: Finish delete testing
        //onView(withId(R.id.ingredient_listview)).check(matches(not(hasItem(hasDescendant(withText("Product"))))));
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
