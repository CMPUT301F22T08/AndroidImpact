package com.androidimpact.app;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import org.junit.Test;

import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;

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
     * Test that the default sort choice is as below
     */
    @Test
    public void getSortChoiceTest() {
        recipeList = MockRecipeList();
        assertEquals(recipeList.getSortChoice(), "Date Added");
    }
}