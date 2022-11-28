package com.androidimpact.app.recipes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.androidimpact.app.ingredients.IngredientStorage;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.recipes.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * This class tests the functions in the class RecipeIngredient.java
 * @version 1.0
 * @author Aneeljyot Alagh
 */
public class RecipeIngredientTest {

    private Date current;

    private RecipeIngredient mockRecipeIngredient(Date date) {
        return new RecipeIngredient(
                "1234",
                "Potatoes",
                4.0F,
                "collection/units",
                "collection/categories",
                date
        );
    }

    /**
     * This function tests the getter method, getDateAdded()
     */
    @Test
    public void testGetDateAdded() {
        RecipeIngredient recipeIngredient = mockRecipeIngredient(current);
        assertEquals(current, recipeIngredient.getDateAdded());
    }


}
