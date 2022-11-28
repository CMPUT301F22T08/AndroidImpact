package com.androidimpact.app;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Signs up, and checks that the user is correctly logged in
 * NOTE: for some reason when running all the tests at once, this test takes forever to run.
 * it should work when run individually
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class A_SignupAndLoginActivityTest {

    /**
     * creates rule for activity scenario
     */
    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Login implementation
     */
    @BeforeClass
    public static void setUpBeforeClass() {
    }

    /**
     * Ensure we are using firebase emulators
     */
    @Before
    public void setup() {
        // logout if we haven't done that already
        logout();
    }

    /**
     * Method testing the login button
     * with invalid credentials
     * Result: stays on the login screen
     */
    @Test
    public void failedLoginTest() {

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
        appCompatEditText.perform(replaceText("bad user"));

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

        // Assert that the title is still there, meaning login wasn't successful
        ViewInteraction textView = onView(
                allOf(withId(R.id.login_title), withText("Android Impact"),
                        withParent(allOf(withId(R.id.login_layout),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("Android Impact")));
    }

    /**
     * Sets test for sign up and login
     * @throws InterruptedException
     *     for thread sleep
     */
    @Test
    public void signupAndLoginActivityTest() throws InterruptedException {
        try {
            // If for some reason this account already exists, delete first
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (user == null) {
                throw new Exception("not logged in");
            }
            String uid = user.getUid();
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Signup and Login Task", "User account deleted.");
                        }
                        // now, delete user document
                        db.document("userData/" + uid).delete().addOnSuccessListener(aVoid -> {
                            Log.i("Signup test", "deleted user doc");
                        });
                    });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.signup), withText("Signup"),
                        childAtPosition(
                                allOf(withId(R.id.login_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.person_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Jane Doh"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.email),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("janedoh@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.signup), withText("Sign Up!"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        4),
                                1),
                        isDisplayed()));
        materialButton2.perform(click());

        Thread.sleep(1000);

        ViewInteraction textView = onView(
                allOf(withText("Ingredient Storage"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView.check(matches(withText("Ingredient Storage")));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.logout),
                        childAtPosition(
                                childAtPosition(
                                        withId(androidx.constraintlayout.widget.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        Thread.sleep(1000);

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.login_title), withText("Android Impact"),
                        withParent(allOf(withId(R.id.login_layout),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView2.check(matches(withText("Android Impact")));

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.username),
                        childAtPosition(
                                allOf(withId(R.id.login_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("janedoh@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                allOf(withId(R.id.login_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.login), withText("Login"),
                        childAtPosition(
                                allOf(withId(R.id.login_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                5),
                        isDisplayed()));
        materialButton3.perform(click());

        Thread.sleep(2000);

        ViewInteraction textView3 = onView(
                allOf(withText("Ingredient Storage"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView3.check(matches(withText("Ingredient Storage")));

        // now, delete user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        assert user != null;
        String uid =  user.getUid();
        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Signup and Login Task", "User account deleted.");
                    }
                    // now, delete user document
                    db.document("userData/" + uid).delete().addOnSuccessListener(aVoid -> {
                        Log.i("Signup test", "deleted user doc");
                    });
                });
    }

    /**
     * This takes the position of the child
     * @param parentMatcher
     * @param position
     * @return
     */
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        /**
         * Sets the description of the child and parent position
         */
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            /**
             * Boolean if matched
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
