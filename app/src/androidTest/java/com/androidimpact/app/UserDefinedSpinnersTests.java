package com.androidimpact.app;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.androidimpact.app.activities.LoginActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests adding user defined spinners
 * Note: these tests are FLAKEY sometimes. Nothing is changed and it works most of the time but
 * sometimes doesn't. Try re running if it doesn't work.
 * @version 1.0
 * @author Curtis Kan
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class UserDefinedSpinnersTests {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);


    /**
     * Navigate to add ingredient screen which has all the user defined spinners needed
     * Result: get to add ingredient screen
     */
    @Before
    public void goToSpinners() throws InterruptedException {

        logout();

        // Fill out username
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.username),
                        childAtPosition(
                                allOf(withId(R.id.login_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("test@gmail.com"), closeSoftKeyboard());

        // Fill out password
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                allOf(withId(R.id.login_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("qwerty"), closeSoftKeyboard());

        // Login
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
        // Add ingredient
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
    }

    /**
     * Tests adding a location to the location spinner
     * Result: a location "testLocation" is added
     */
    @Test
    public void locationSpinnerTest() {

        // Click on edit locations button
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.ingredientStoreAdd_editLocationsBtn),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                8),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        // Change edit text for new location
        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.location_editText),
                        childAtPosition(
                                allOf(withId(R.id.editIngredientLocations_root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("testLocation"), closeSoftKeyboard());

        // Confirm adding the location
        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.addLocationBtn), withText("Add Location"),
                        childAtPosition(
                                allOf(withId(R.id.editIngredientLocations_root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        materialButton2.perform(click());

        // Check to make sure location was added
        ViewInteraction textView = onView(
                allOf(withId(R.id.recycler_view_item_title), withText("testLocation"),
                        withParent(allOf(withId(R.id.recycler_view_item_container),
                                withParent(withId(R.id.location_listview)))),
                        isDisplayed()));
        textView.check(matches(withText("testLocation")));
    }

    /**
     * Tests adding unit to the unit spinner
     * Result: a unit "testUnit" is added
     */
    @Test
    public void unitSpinnerTest() {

        // Click on edit units button
        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.ingredientStoreAdd_editUnitsBtn),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                11),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        // Change edit text for new unit
        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.edit_units_editText),
                        childAtPosition(
                                allOf(withId(R.id.editIngredientLocations_root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("testUnit"), closeSoftKeyboard());

        // Add unit button
        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.addUnitBtn), withText("Add Unit"),
                        childAtPosition(
                                allOf(withId(R.id.editIngredientLocations_root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        materialButton4.perform(click());

        // Check to make sure unit was added
        ViewInteraction textView = onView(
                allOf(withId(R.id.recycler_view_item_title), withText("testUnit"),
                        withParent(allOf(withId(R.id.recycler_view_item_container),
                                withParent(withId(R.id.edit_units_listview)))),
                        isDisplayed()));
        textView.check(matches(withText("testUnit")));
    }

    /**
     * Tests adding a category to the category spinner
     * Result: a category "testCategory" is added
     */
    @Test
    public void categorySpinnerTest() {

        // Click on edit category button
        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.ingredientStoreAdd_editCategoryBtn),
                        childAtPosition(
                                allOf(withId(R.id.content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        // Modify edit text to new category
        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.category_editText),
                        childAtPosition(
                                allOf(withId(R.id.editCategories_root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("testCategory"), closeSoftKeyboard());

        // Confirm the add
        ViewInteraction materialButton7 = onView(
                allOf(withId(R.id.addCategoryBtn), withText("Add Category"),
                        childAtPosition(
                                allOf(withId(R.id.editCategories_root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        materialButton7.perform(click());

        // Check to make sure category was added
        ViewInteraction textView = onView(
                allOf(withId(R.id.recycler_view_item_title), withText("testCategory"),
                        withParent(allOf(withId(R.id.recycler_view_item_container),
                                withParent(withId(R.id.category_listview)))),
                        isDisplayed()));
        textView.check(matches(withText("testCategory")));
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
