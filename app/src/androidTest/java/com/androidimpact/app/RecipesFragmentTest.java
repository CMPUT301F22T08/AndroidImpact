package com.androidimpact.app;


import static androidx.test.espresso.Espresso.onData;
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
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.LoginActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This tests the recipes fragment functionality, like add/edit recipe, add/edit ingredient to recipe
 * Written using the built in espresso test recorder
 * @version 1.0
 * @author Curtis Kan
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipesFragmentTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

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
     */
    @Test
    public void A_rightFragmentTest() {

        // Click on bottom navbar button
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.recipe_icon), withContentDescription("Recipe"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Make sure on IngredientStorage fragment
        ViewInteraction textView = onView(
                allOf(withText("Recipe List"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView.check(matches(withText("Recipe List")));
    }

    @Test
    public void B_recipesFragmentTest() {

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

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.recipe_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recipe_title_layout),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Aadvark soup"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.recipe_prep),
                        childAtPosition(
                                allOf(withId(R.id.recipe_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("3"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.recipe_servings),
                        childAtPosition(
                                allOf(withId(R.id.recipe_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("4"), closeSoftKeyboard());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.add_ingredient), withContentDescription("Title"),
                        childAtPosition(
                                allOf(withId(R.id.recipe_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                8),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction materialAutoCompleteTextView = onView(
                allOf(withId(R.id.ingredient_description),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ingredient_description_layout),
                                        0),
                                0),
                        isDisplayed()));
        materialAutoCompleteTextView.perform(replaceText("Water"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.ingredient_amount),
                        childAtPosition(
                                allOf(withId(R.id.ingredient_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("2"), closeSoftKeyboard());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.recipe_ingredient_unit),
                        childAtPosition(
                                allOf(withId(R.id.ingredient_layout),
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
                .atPosition(6);
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.recipe_ingredient_category),
                        childAtPosition(
                                allOf(withId(R.id.ingredient_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.recipe_ingredient_category),
                        childAtPosition(
                                allOf(withId(R.id.ingredient_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatSpinner3.perform(click());

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView2.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.confirm_button), withText("Confirm"),
                        childAtPosition(
                                allOf(withId(R.id.ingredient_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                6),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction appCompatSpinner4 = onView(
                allOf(withId(R.id.recipe_category_spinner),
                        childAtPosition(
                                allOf(withId(R.id.recipe_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                11),
                        isDisplayed()));
        appCompatSpinner4.perform(click());

        DataInteraction appCompatCheckedTextView3 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView3.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.confirm_button), withText("Confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recipe_layout),
                                        9),
                                1),
                        isDisplayed()));
        materialButton3.perform(click());
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
