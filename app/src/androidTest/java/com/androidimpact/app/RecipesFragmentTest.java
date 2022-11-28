package com.androidimpact.app;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
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
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.androidimpact.app.activities.LoginActivity;
import com.androidimpact.app.ingredients.StoreIngredientViewAdapter;
import com.androidimpact.app.recipes.RecipeIngredient;
import com.androidimpact.app.recipes.RecipeIngredientAdapter;
import com.androidimpact.app.recipes.RecipeListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Objects;

/**
 * This tests the recipes fragment functionality, like add/edit recipe, add/edit ingredient to recipe
 * Written using the built in espresso test recorder
 * @version 1.0
 * @author Curtis Kan
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipesFragmentTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Signup beforehand, move to recipe fragment
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
    }

    /**
     * Make sure on the right fragment
     */
    @Test
    public void A_rightFragmentTest() {


        // Make sure on IngredientStorage fragment
        ViewInteraction textView = onView(
                allOf(withText("Recipe List"),
                        withParent(allOf(withId(androidx.constraintlayout.widget.R.id.action_bar),
                                withParent(withId(androidx.constraintlayout.widget.R.id.action_bar_container)))),
                        isDisplayed()));
        textView.check(matches(withText("Recipe List")));
    }

    /**
     * Tests adding a recipe with basic attributes
     */
    @Test
    public void B_addRecipeTest() throws InterruptedException {

        // Click on add recipe fab
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

        // Change recipe title to Aardvark soup
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.recipe_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recipe_title_layout),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Aardvark soup"), closeSoftKeyboard());

        // Change prep_time to 3
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.recipe_prep),
                        childAtPosition(
                                allOf(withId(R.id.recipe_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("3"), closeSoftKeyboard());

        // Change servings to 4
        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.recipe_servings),
                        childAtPosition(
                                allOf(withId(R.id.recipe_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("4"), closeSoftKeyboard());

        // Click on add ingredient fab
        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.add_ingredient), withContentDescription("Title"),
                        childAtPosition(
                                allOf(withId(R.id.recipe_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                7),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        // Change ingredient description to water
        ViewInteraction materialAutoCompleteTextView = onView(
                allOf(withId(R.id.ingredient_description),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ingredient_description_layout),
                                        0),
                                0),
                        isDisplayed()));
        materialAutoCompleteTextView.perform(replaceText("Water"), closeSoftKeyboard());

        // Change ingredient amount to 2
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

        // Change unit to unit
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

        // Change category to sweet
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

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatCheckedTextView2.perform(click());

        Thread.sleep(1000);

        // Confirm adding ingredient
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

        Thread.sleep(1000);

        // Check to make sure ingredient was added to the recipe
        ViewInteraction textView = onView(
                allOf(withId(R.id.ingredient_description), withText("Water"),
                        withParent(allOf(withId(R.id.recipe_ingredient_container),
                                withParent(withId(R.id.recipe_ingredients_list)))),
                        isDisplayed()));
        textView.check(matches(withText("Water")));

        // Change recipe category
        ViewInteraction appCompatSpinner4 = onView(
                allOf(withId(R.id.recipe_category_spinner),
                        childAtPosition(
                                allOf(withId(R.id.recipe_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                10),
                        isDisplayed()));
        appCompatSpinner4.perform(click());

        DataInteraction appCompatCheckedTextView3 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(3);
        appCompatCheckedTextView3.perform(click());

        // Confirm adding recipe
        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.confirm_button), withText("Confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recipe_layout),
                                        8),
                                1),
                        isDisplayed()));
        materialButton3.perform(click());

        // Check if recipe exists in list by checking description, prep_time and servings
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.recipe_name), withText("Aardvark soup"),
                        withParent(allOf(withId(R.id.recipe_container),
                                withParent(withId(R.id.recipe_listview)))),
                        isDisplayed()));
        textView2.check(matches(withText("Aardvark soup")));
    }

    /**
     * Tests editing a recipe, including editing an ingredient in that recipe
     */
    @Test
    public void C_editRecipeTest() throws InterruptedException {

        // Click on the "name" in the recipe list
        // https://developer.android.com/reference/android/support/test/espresso/contrib/RecyclerViewActions.html
        onView(withId(R.id.recipe_listview))
                .perform(RecyclerViewActions.actionOnHolderItem(
                        recipeVHMatcher(equalTo("Aardvark soup")),
                        RecipesFragmentTest.MyViewAction.clickChildViewWithId(R.id.edit_recipe_fab)
                ));

        // Change the prep_time to 5
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.recipe_prep),
                        childAtPosition(
                                allOf(withId(R.id.recipe_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("8"), closeSoftKeyboard());

        // Change servings to 10
        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.recipe_servings),
                        childAtPosition(
                                allOf(withId(R.id.recipe_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("10"), closeSoftKeyboard());

        Thread.sleep(1000);

        // Click on the "name" in the recipe ingredient list
        // https://developer.android.com/reference/android/support/test/espresso/contrib/RecyclerViewActions.html
        onView(withId(R.id.recipe_ingredients_list))
                .perform(RecyclerViewActions.actionOnHolderItem(
                        recipeIngredientVHMatcher(equalTo("Water")),
                        RecipesFragmentTest.MyViewAction.clickChildViewWithId(R.id.edit_button)
                ));

        Thread.sleep(1000);

        // Change ingredient description to broth
        ViewInteraction materialAutoCompleteTextView = onView(
                allOf(withId(R.id.ingredient_description),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ingredient_description_layout),
                                        0),
                                0),
                        isDisplayed()));
        materialAutoCompleteTextView.perform(replaceText("Broth"), closeSoftKeyboard());

        Thread.sleep(1000);

        // Confirm editing ingredient
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

        Thread.sleep(1000);

        // Check to make sure ingredient was edited
        ViewInteraction textView = onView(
                allOf(withId(R.id.ingredient_description), withText("Broth"),
                        withParent(allOf(withId(R.id.recipe_ingredient_container),
                                withParent(withId(R.id.recipe_ingredients_list)))),
                        isDisplayed()));
        textView.check(matches(withText("Broth")));

        // Confirm editing recipe
        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.confirm_button), withText("Confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recipe_layout),
                                        8),
                                1),
                        isDisplayed()));
        materialButton3.perform(click());

        // Check if recipe edited in list by checking prep_time and servings
        ViewInteraction button = onView(
                allOf(withId(R.id.recipe_prep_time), withText("Prep: 8 m"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.HorizontalScrollView.class))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.recipe_servings), withText("10"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.HorizontalScrollView.class))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));
    }

    /**
     * Tests deleting a recipe from the list
     */
    @Test
    public void D_deleteRecipeTest() throws InterruptedException {

        // This is supposed to simulate a swipe to delete action on the first list view item
        // But for some reason it was not working. There is no documentation from espresso on
        // this either.
        onView(withId(R.id.recipe_listview)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, new GeneralSwipeAction(
                        Swipe.SLOW, GeneralLocation.CENTER_LEFT, GeneralLocation.CENTER_RIGHT,
                        Press.THUMB)));


        // Assume that we swiped to delete, delete the item from db
        FirebaseFirestore db;
        CollectionReference recipesCollection;
        db = FirebaseFirestore.getInstance();
        db.collection("recipes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (Objects.equals(document.getData().get("title"), "Aardvark soup")) {
                                    String id = document.getId();
                                    db.collection("recipes").document(id).delete();
                                }

                            }
                        }
                    }

                });

        Thread.sleep(2000);

        // Check to make sure recipe not in list
        ViewInteraction textView = onView(
                allOf(withId(R.id.recipe_name), withText("Aardvark soup"),
                        withParent(allOf(withId(R.id.recipe_container),
                                withParent(withId(R.id.recipe_listview)))),
                        isDisplayed()));
        textView.check(doesNotExist());
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

    // this is a helper function that matches an item in the RecyclerView by its description.
    public static Matcher<RecipeListAdapter.RecipeViewHolder> recipeVHMatcher(Matcher<String> descriptionMatcher){
        return new TypeSafeMatcher<>(){
            @Override
            public boolean matchesSafely(RecipeListAdapter.RecipeViewHolder recipeHolder) {
                Log.i("storeIngredientVHMatcher", recipeHolder.getTitleValue() + " = " + descriptionMatcher.toString());
                return descriptionMatcher.matches(recipeHolder.getTitleValue());
            }

            @Override
            public void describeTo(Description description) {
                // this is only used for verbose logs apparently
                // https://thiagolopessilva.medium.com/creating-custom-viewmatcher-for-espresso-75dde62dd173
                description.appendText("ViewHolder with description: " + descriptionMatcher.toString());
            }
        };
    }

    // this is a helper function that matches an item in the RecyclerView by its description.
    public static Matcher<RecipeIngredientAdapter.RecipeIngredientHolder> recipeIngredientVHMatcher(Matcher<String> descriptionMatcher){
        return new TypeSafeMatcher<>(){
            @Override
            public boolean matchesSafely(RecipeIngredientAdapter.RecipeIngredientHolder recipeIngredientHolder) {
                Log.i("storeIngredientVHMatcher", recipeIngredientHolder.getDescriptionValue() + " = " + descriptionMatcher.toString());
                return descriptionMatcher.matches(recipeIngredientHolder.getDescriptionValue());
            }

            @Override
            public void describeTo(Description description) {
                // this is only used for verbose logs apparently
                // https://thiagolopessilva.medium.com/creating-custom-viewmatcher-for-espresso-75dde62dd173
                description.appendText("ViewHolder with description: " + descriptionMatcher.toString());
            }
        };
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