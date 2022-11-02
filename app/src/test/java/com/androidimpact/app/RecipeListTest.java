package com.androidimpact.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Log;

import org.checkerframework.checker.units.qual.A;
import org.junit.Test;

import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test class to test RecipeList class
 * Functions of RecipeList class to test:
 *  - getItemCount()
 *  - getSortChoice()
 *  - setSortChoice(int index)
 *  = getSortChoices()
 *  - sortByChoice()
 */
public class RecipeListTest {
    private RecipeList recipeList;

    /**
     * Create a mock recipe list
     * @return mock recipe list as RecipeList object
     */
    public RecipeList MockRecipeList() {
        recipeList = new RecipeList(
                null, new ArrayList<>()
        );
        return recipeList;
    }

    /**
     * Create a mock recipe for our RecipeList
     * @return mock recipe as Recipe object
     */
    public Recipe MockRecipe() {
        Recipe recipe = new Recipe(
                new ArrayList<Ingredient>(),
                "French fries",
                20,
                4,
                "Fast food",
                "Be careful not too add too much peanut butter!",
                "05/12/2022 09:20:40"
        );
        return recipe;
    }

    /**
     * Test that the item count is correct
     * First initialize a blank arraylist, and initialize the recipe list over that arraylist
     * Check this size is 0
     * Then add a recipe to the arraylist
     * Check this size is incremented
     */
    @Test
    public void getItemCountTest() {
        ArrayList<Recipe> recipeArrayList = new ArrayList<>();
        recipeList = new RecipeList(null, recipeArrayList);
        assertEquals(recipeList.getItemCount(), 0);

        recipeArrayList.add(MockRecipe());
        assertEquals(recipeList.getItemCount(), 1);
    }

    /**
     * Test that the default sort choice is as below
     * Initialize the recipe list and ensure value of default sort choice is "Date Added"
     */
    @Test
    public void getSortChoiceTest() {
        recipeList = MockRecipeList();
        assertEquals(recipeList.getSortChoice(), "Date Added");
    }

    /**
     * Test that the set sort choice is correct
     * Initialize the recipe list and ensure the value when we set the sort choice to one of our
     * choosing, then it reflects the choice we want
     */
    @Test
    public void setSortChoiceTest() {
        recipeList = MockRecipeList();
        recipeList.setSortChoice(2);
        assertEquals(recipeList.getSortChoice(), "Preparation Time");
    }

    /**
     * Test that the sort choices available to the user are correctly returned
     * In this case, check all the values are the same as intended, and that there aren't any more
     * than intended
     */
    @Test
    public void getSortChoicesTest() {
        recipeList = MockRecipeList();
        assertTrue(Arrays.equals(RecipeList.getSortChoices(), new String[]{
                "Date Added",
                "Title",
                "Preparation Time",
                "Number of Servings",
                "Recipe Category"
        }));
    }

    /**
     * Test that sorting by a user's choice actually sorts the list as intended
     * In this case, set attributes of recipe using a number i
     * In the way implemented in the test, each sorting will result in a unique recipe at the 'top'
     * Check that this unique value is at the 'top' by title
     */
    @Test
    public void sortByChoiceTest() {
        ArrayList<Recipe> recipeArrayList = new ArrayList<>();
        int n = 5;
        for(int i = 0; i < n; i++) {
            recipeArrayList.add(new Recipe(
                    new ArrayList<>(),
                    String.valueOf(i), // n-(n-i) = i
                    n-(4-i),
                    n-(3-i),
                    String.valueOf(n-(2-i)),
                    "",
                    String.valueOf(n-(1-i))
            ));
        }

        recipeList = new RecipeList(null, recipeArrayList);

        for(int i = 0; i < n; i++) {
            recipeList.setSortChoice(i);
            recipeList.sortByChoice();

            assertEquals(recipeArrayList.get(i).getTitle(), String.valueOf(i));
        }

        assertTrue(true);
    }

    @Test
    public void deleteEdition() {

    }
}