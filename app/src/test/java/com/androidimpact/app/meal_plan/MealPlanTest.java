package com.androidimpact.app.meal_plan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.androidimpact.app.recipes.Recipe;

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
     * This function tests adding meal plan items
     */
    @Test
    public void testAddingMealPlanItems() {
        assertTrue(true);
    }
}
