package com.androidimpact.app.activities;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.androidimpact.app.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the titles of the action bars when clicking around the fragments in MainActivity
 *
 * Note: This test was autogenerated by Espresso
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityFragmentTitlesTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void mainActivityFragmentTitlesTest() {
        ViewInteraction textView = onView(
                allOf(withText("Ingredient Storage"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView.check(matches(withText("Ingredient Storage")));

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.recipe_icon), withContentDescription("recipe"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withText("Recipe List"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView2.check(matches(withText("Recipe List")));

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.cart_icon), withContentDescription("shopping list"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withText("Shopping List"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView3.check(matches(withText("Shopping List")));

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.meal_icon), withContentDescription("meal plan"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                4),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withText("Meal Plan"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView4.check(matches(withText("Meal Plan")));
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