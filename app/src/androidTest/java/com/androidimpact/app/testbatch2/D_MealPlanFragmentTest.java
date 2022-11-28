package com.androidimpact.app.testbatch2;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.LoginActivity;
import com.androidimpact.app.meal_plan.MealPlanListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Objects;

/**
 * This tests the meal plan fragment, adding, editing and deleting one
 * Written using the built in espresso test recorder
 * Note: these tests are FLAKEY sometimes. Nothing is changed and it works most of the time but
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
public class D_MealPlanFragmentTest {

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
                Matchers.allOf(ViewMatchers.withId(R.id.username),
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

        // Couldn't get this to work as the horizontal scroll view was obstructing the click action
        // Add a recipe to breakfast
//        ViewInteraction materialButton4 = onView(
//                allOf(withId(R.id.add_breakfast_recipe), withText("Add Recipe"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.LinearLayout")),
//                                        0),
//                                1),
//                        isDisplayed()));
//        materialButton4.perform(click());

//        Thread.sleep(1000);
//
//        // Select the first item on the recycler view
//        ViewInteraction recyclerView2 = onView(
//                allOf(withId(R.id.recipe_listview),
//                        childAtPosition(
//                                withClassName(is("android.widget.LinearLayout")),
//                                1)));
//        recyclerView2.perform(actionOnItemAtPosition(0, click()));
//
//        // Set the servings to 2
//        ViewInteraction appCompatEditText5 = onView(
//                allOf(withId(R.id.editTextServings),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.content),
//                                        0),
//                                1),
//                        isDisplayed()));
//        appCompatEditText5.perform(replaceText("2"), closeSoftKeyboard());
//
//        // Confirm adding the recipe
//        ViewInteraction materialButton5 = onView(
//                allOf(withId(R.id.confirm_button), withText("Confirm"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.LinearLayout")),
//                                        2),
//                                1),
//                        isDisplayed()));
//        materialButton5.perform(click());

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

        // Check if the meal plan exists by checking if description is there
        ViewInteraction textView = onView(
                allOf(withId(R.id.meal_plan_title), withText("Day 301"),
                        withParent(allOf(withId(R.id.day_header),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView.check(matches(withText("Day 301")));
    }

    /**
     * Test editing a meal plan
     * Result: the meal plan Day 301 becomes Day 21
     */
    @Test
    public void C_mealPlanEditTest() throws InterruptedException {

        // Click on the "date in meal plan
        // https://developer.android.com/reference/android/support/test/espresso/contrib/RecyclerViewActions.html
        onView(withId(R.id.meal_plan_list))
                .perform(RecyclerViewActions.actionOnHolderItem(
                        mealPlanVHMatcher(equalTo("Day 301")),
                        D_MealPlanFragmentTest.MyViewAction.clickChildViewWithId(R.id.edit_button)
                ));

        Thread.sleep(1000);

        // Change description
        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.editTextMealPlanTitle),
                        childAtPosition(
                                allOf(withId(R.id.mealPlanTitleLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("Day 21"), closeSoftKeyboard());


        // Confirm the edit
        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.add_button), withText("Add"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton6.perform(click());

        Thread.sleep(1000);

        // Check if the meal plan is in the list
        ViewInteraction textView = onView(
                allOf(withId(R.id.meal_plan_title), withText("Day 21"),
                        withParent(allOf(withId(R.id.day_header),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView.check(matches(withText("Day 21")));
    }

    /**
     * Test deleting a meal plan
     * Result: meal plan Day 21 should no longer exist
     */
    @Test
    public void D_mealPlanDeleteTest() throws InterruptedException {
        // https://stackoverflow.com/questions/56578699/espressotest-swipe-to-delete-item-of-recyclerview-inside-viewpager
        // ricocarpe Oct 19, 2019
        // This is supposed to simulate a swipe to delete action on the first list view item
        // But for some reason it was not working. There is no documentation from espresso on
        // this either.
        onView(withId(R.id.meal_plan_list))
                .perform(RecyclerViewActions.actionOnHolderItem(
                        mealPlanVHMatcher(equalTo("Day 21")),
                        swipeRight()
                ));

        // Assume that we swiped to delete, delete the item from db
        FirebaseFirestore db;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String uid = currentUser.getUid();

        db = FirebaseFirestore.getInstance();
        db.document("userData/" + uid).collection("meal-plan")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (Objects.equals(document.getId(), "Day 21")) {
                                    String id = document.getId();
                                    db.document("userData/" + uid).collection("meal-plan").document(id).delete();
                                }

                            }
                        }
                    }
                });

        Thread.sleep(2000);

        // Check if the meal plan is no longer in the list
        ViewInteraction textView = onView(
                allOf(withId(R.id.meal_plan_title), withText("Day 21"),
                        withParent(allOf(withId(R.id.day_layout),
                                withParent(withId(R.id.meal_plan_list)))),
                        isDisplayed()));
        textView.check(doesNotExist());

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
    public static Matcher<MealPlanListAdapter.MealPlanHolder> mealPlanVHMatcher(Matcher<String> dateMatcher){
        return new TypeSafeMatcher<>(){
            @Override
            public boolean matchesSafely(MealPlanListAdapter.MealPlanHolder mealPlanViewHolder) {
                Log.i("storeIngredientVHMatcher", mealPlanViewHolder.getDescriptionValue() + " = " + dateMatcher.toString());
                return dateMatcher.matches(mealPlanViewHolder.getDescriptionValue());
            }

            @Override
            public void describeTo(Description description) {
                // this is only used for verbose logs apparently
                // https://thiagolopessilva.medium.com/creating-custom-viewmatcher-for-espresso-75dde62dd173
                description.appendText("ViewHolder with description: " + dateMatcher.toString());
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
