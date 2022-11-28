package com.androidimpact.app.ingredients;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * This class tests the functions in IngredientTest.java
 * @version 1.0
 * @author Aneeljyot Alagh
 */
public class IngredientTest {

    private Ingredient mockIngredient() {
        return new Ingredient(
                "1234",
                "Celery",
                4F,
                "Stalks",
                "Greens"
        );
    }

    /**
     * This function tests all getters and setters, other than ID getters and setters
     */
    @Test
    public void testGettersSettersNotID() {
        Ingredient ingredient = mockIngredient();

        // test getDescription()
        assertEquals("Celery", ingredient.getDescription());

        // test setDescription()
        ingredient.setDescription("Asparagus");
        assertEquals("Asparagus", ingredient.getDescription());

        // test getAmount()
        assertEquals(4F, ingredient.getAmount(), 0.00001);

        // test setAmount()
        ingredient.setAmount(8F);
        assertEquals(8F, ingredient.getAmount(), 0.00001);

        // test getCategory()
        assertEquals("Greens", ingredient.getCategory());

        // test setCategory()
        ingredient.setCategory("Vegetables");
        assertEquals("Vegetables", ingredient.getCategory());

        // test getUnit()
        assertEquals("Stalks", ingredient.getUnit());
    }

    /**
     * This function tests the getter, setter methods on ID
     */
    @Test
    public void testID() {

    }

}
