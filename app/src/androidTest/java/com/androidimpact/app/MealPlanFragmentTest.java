package com.androidimpact.app;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
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
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.LoginActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * This tests the meal plan fragment, adding, editing and deleting one
 * Written using the built in espresso test recorder
 * @version 1.0
 * @author Curtis Kan
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MealPlanFragmentTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Login beforehand with test account, move to meal plan fragment
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

        // Go to meal plan fragment
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.meal_icon), withContentDescription("Meal Plan"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                4),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());
    }

    /**
     * Test to make sure we're in the right fragment for future tests
     */
    @Test
    public void A_mealPlanFragmentTest() {

        // Make sure on IngredientStorage fragment
        ViewInteraction textView = onView(
                allOf(withText("Meal Plan"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView.check(matches(withText("Meal Plan")));
    }

    /**
     * Test adding a recipe and ingredient to a new meal plan
     * Result: a new meal plan for Day 301 is added with a recipe and an ingredient
     */
    @Test
    public void B_mealPlanAddTest() throws InterruptedException {

        // Click on global fab to add recipe
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

        // Change meal plan title to Day 301
        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.editTextMealPlanTitle),
                        childAtPosition(
                                allOf(withId(R.id.mealPlanTitleLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("Day 301"), closeSoftKeyboard());

        // Click on add a breakfast ingredient button
        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.add_breakfast_ingredient), withText("Add Ingredient"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        materialButton2.perform(click());

        Thread.sleep(1000);

        // Click on the first item in the list
        ViewInteraction recyclerView = onView(
                allOf(isDisplayed(), withId(R.id.ingredient_listview)));
        recyclerView.perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Thread.sleep(1000);

        // Change servings to 3
        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.editTextServings),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("3"), closeSoftKeyboard());

        // Confirm adding the ingredient
        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.confirm_button), withText("Confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                1),
                        isDisplayed()));
        materialButton3.perform(click());

        // Add a recipe to breakfast
        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.add_breakfast_recipe), withText("Add Recipe"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        materialButton4.perform(click());

        // Select the first item on the recycler view
        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.recipe_listview),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        // Set the servings to 2
        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.editTextServings),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("2"), closeSoftKeyboard());

        // Confirm adding the recipe
        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.confirm_button), withText("Confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                1),
                        isDisplayed()));
        materialButton5.perform(click());

        // Add the meal plan
        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.add_button), withText("Add"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton6.perform(click());
    }

    /**
     * Test editing a meal plan
     * Result:
     */
    @Test
    public void C_mealPlanEditTest() {

    }

    /**
     * Test deleting a meal plan
     * Result:
     */
    @Test
    public void D_mealPlanDeleteTest() {

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
