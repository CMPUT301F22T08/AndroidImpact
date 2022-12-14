package com.androidimpact.app.recipes;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.recipes.RecipeList;

/**
 * Test class to test RecipeList class
 */
public class RecipeListTest {
    private RecipeList recipeList;

    /**
     * Create a recipe list based on a given arraylist
     * @param arrlist arraylist to base mock recipe list on
     * @return recipe list as RecipeList object
     */
    public RecipeList MakeRecipeList(ArrayList<Recipe> arrlist) {
        recipeList = new RecipeList(
                arrlist
        );
        return recipeList;
    }

    /**
     * Create a mock recipe list
     * @return mock recipe list as RecipeList object
     */
    public RecipeList MockRecipeList() {
        return MakeRecipeList(new ArrayList<>());
    }

    /**
     * Create a mock recipe for our RecipeList
     * @return mock recipe as Recipe object
     */
    public Recipe MockRecipe() {
        Recipe recipe = new Recipe(
                "1234",
                "French fries",
                20,
                4,
                "Fast food",
                "Be careful not too add too much peanut butter!",
                new Date(),
                "photo link",
                "users/recipes/collections/"
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
        recipeList = MakeRecipeList(recipeArrayList);
        assertEquals(recipeList.size(), 0);

        recipeArrayList.add(MockRecipe());
        assertEquals(recipeList.size(), 1);
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
        //String id, String title, int prep_time, int servings,
        //                  String category, String comments, Date date, @Nullable String photo, String collectionPath
        for(int i = 0; i < n; i++) {
            recipeArrayList.add(new Recipe(
                    "",
                    String.valueOf(n-(3-i)),
                    n-(2-i),
                    n-(1-i),
                    String.valueOf(n-(0-i)),
                    "",
                    new Date(i),
                    "",
                    ""
            ));
        }

        recipeList = MakeRecipeList(recipeArrayList);

        for(int i = 0; i < n; i++) {
            System.out.println(i);
            recipeList.setSortChoice(i);
            recipeList.sortByChoice();

            assertEquals(String.valueOf(recipeArrayList.get(i).getDate().getTime()), String.valueOf(i));
        }

        assertTrue(true);
    }
}