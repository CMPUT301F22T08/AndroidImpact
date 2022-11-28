package com.androidimpact.app.meal_plan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.androidimpact.app.R;
import com.androidimpact.app.ingredients.IngredientStorage;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.recipes.RecipeList;

import org.junit.Test;

import java.util.Date;

/**
 * Tests for the MealPlan class
 *
 * @version 1.0
 * @author Aneeljyot Alagh
 */
public class MealPlanTest {

    /**
     * Creates a mock meal plan object
     * @return (MealPlan) Mock meal plan object to test functions
     */
    private MealPlan mockMealPlan() {
        return new MealPlan(
                "June 1",
                "1"
        );
    }

    private String id = "MockRecipeId";
    private String title = "Mac and Cheese";
    private int prep_time = 2;
    private int servings = 3;
    private String category = "Lunch";
    private String comments = "-Add milk";
    private Date date = new Date();
    private String photo = "MockPhotoAddress";
    private String collectionPath = "MockCollectionPath";

    /**
     * Returns a mock recipe object
     * @return (Recipe) a mock recipe to test the class functions
     */
    private Recipe mockRecipe() {
        return new Recipe(id, title, prep_time, servings, category, comments, date, photo, collectionPath);
    }

    /**
     * This function returns another type of mock store ingredient object, following the second constructor
     * @return (StoreIngredient) a mock store ingredient object
     */
    public StoreIngredient mockStoreIngredient() {
        return new StoreIngredient(
                "1234",
                "French fries",
                4F,
                "frozen",
                date,
                "freezer",
                "bags"
        );
    }

    /**
     * This function tests the getters and setters for date
     */
    @Test
    public void testDate() {
        MealPlan mealPlan = mockMealPlan();

        // test getDate()
        assertEquals("June 1", mealPlan.getDate());

        // test setDate()
        mealPlan.setDate("May 1");
        assertEquals("May 1", mealPlan.getDate());
    }

    /**
     * This function tests the method getSortString()
     */
    @Test
    public void testGetSortString() {
        MealPlan mealPlan = mockMealPlan();

        assertEquals("1", mealPlan.getSortString());
    }

    /**
     * This function tests adding meal plan items.
     * We make mock recipe, ingredient, recipelist, ingredientstorage.
     * We then add recipe/ingredient to respective list.
     * We then add recipe/ingredient to the meal plan, and test the size increases.
     * We then check the items appear with relevant attributes.
     */
    @Test
    public void testAddingMealPlanItems() {
        // make mock objects
        MealPlan mealPlan = mockMealPlan();
        RecipeList recipeList = new RecipeList();
        IngredientStorage ingredientStorage = new IngredientStorage();
        Recipe mockRecipe = mockRecipe();
        StoreIngredient mockIngredient = mockStoreIngredient();

        // add recipe/ingredient to storages
        recipeList.add(mockRecipe);
        ingredientStorage.add(mockIngredient);

        // add recipe to meal plan
        mealPlan.addMealItemRecipe("breakfast", mockRecipe.getId(), 4F, recipeList);
        assertEquals(mealPlan.size(), 1);

        // add ingredient to meal plan
        mealPlan.addMealItemIngredient("lunch", mockIngredient.getId(), 8F, ingredientStorage.getData());
        assertEquals(mealPlan.size(), 2);

        // test items in meal plan
        assertEquals(mealPlan.getRecipeTitles("breakfast").get(0), "Mac and Cheese");
        assertEquals(mealPlan.getIngredientTitles("lunch").get(0), "French fries");
    }
}
