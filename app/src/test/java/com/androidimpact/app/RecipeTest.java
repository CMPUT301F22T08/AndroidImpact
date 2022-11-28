package com.androidimpact.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

import com.androidimpact.app.ingredients.Ingredient;
import com.androidimpact.app.recipes.Recipe;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecipeTest {

    private String id = "MockRecipeId"
    private String title = "Mac and Cheese";
    private int prep_time = 2;
    private int servings = 3;
    private String category = "Lunch";
    private String comments = "-Add milk";
    private Date date = new Date();
    private String photo = "MockPhotoAddress";
    private String collectionPath = "MockCollectionPath";

    private Recipe mockRecipe() {
        return new Recipe(id, title, prep_time, servings, category, comments, date, photo, collectionPath);
    }

    @Test
    public void testAddIngredient() {
        Recipe recipe = mockRecipe();
//        recipe.addIngredient(mockIngredient());
//        assertEquals(1, recipe.getIngredients().size());
    }

    /*@Test
    public void testEditIngredient() {
        Recipe recipe = mockRecipe();
        recipe.addIngredient(mockIngredient());
        Ingredient mockIngredient = new Ingredient("1", "Cheese", 5, "g", "dairy");
        recipe.addIngredient(mockIngredient);
        mockIngredient.setDescription("White cheese");
        recipe.editIngredient(1, mockIngredient);
        assertEquals(mockIngredient.getDescription(), recipe.getIngredients().get(1).getDescription());
        assertEquals("Macaroni", recipe.getIngredients().get(0).getDescription());
    }

    @Test
    public void removeIngredient() {
        Recipe recipe = mockRecipe();
        Ingredient ingredient = mockIngredient();
        recipe.addIngredient(ingredient);
        assertEquals(1, recipe.getIngredients().size());
        recipe.removeIngredient(ingredient);
        assertEquals(0, recipe.getIngredients().size());

    }

    @Test
    public void removeIngredientException() {
        Recipe recipe = mockRecipe();
        recipe.addIngredient(mockIngredient());
        assertEquals(1, recipe.getIngredients().size());
        assertThrows(IllegalArgumentException.class, () -> {recipe.removeIngredient(mockIngredient());});
    }*/

}