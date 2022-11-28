package com.androidimpact.app.ingredients;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.androidimpact.app.ingredients.IngredientStorage;
import com.androidimpact.app.ingredients.StoreIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * This class tests the functions in StoreIngredient.java
 * @version 1.0
 * @author Aneeljyot Alagh
 */
public class StoreIngredientTest {

    private Date date = new Date(17280000); // 2 days after epoch time

    /**
     * This function returns a mock store ingredient object, following the first constructor
     * @return (StoreIngredient) a mock store ingredient object
     */
    public StoreIngredient mockStoreIngredient() {
        return new StoreIngredient();
    }

    /**
     * This function returns another type of mock store ingredient object, following the second constructor
     * @return (StoreIngredient) a mock store ingredient object
     */
    public StoreIngredient mockStoreIngredient2() {
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
     * This function tests all other methods
     */
    @Test
    public void testMethods() {
        StoreIngredient ingredient = mockStoreIngredient();
        StoreIngredient ingredient2 = mockStoreIngredient2();
        StoreIngredient ingredient5 = mockStoreIngredient2();

        // test getBestBeforeDate()
        assertEquals(date, ingredient2.getBestBeforeDate());

        // test getBestBeforeCalendar()
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, date.getYear());
        cal.set(Calendar.MONTH, date.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, date.getDate());
        assertTrue(ingredient5.compareCalendar(ingredient2));

        // test getLocation()
        assertEquals("freezer", ingredient2.getLocation());

        // test hasNull()
        StoreIngredient ingredient3 = new StoreIngredient(
                "",
                "",
                4F,
                "",
                new Date(122, 8, 30),
                "not blank",
                ""
        );
        StoreIngredient ingredient4 = new StoreIngredient(
                "",
                "",
                4F,
                "",
                date,
                "",
                ""
        );
        assertTrue(ingredient3.hasNull());
        assertTrue(ingredient4.hasNull());
        assertFalse(ingredient2.hasNull());
    }

    /**
     * This function tests compareCalandar()
     */
    @Test
    public void testCompareCalendar() {
        StoreIngredient ingredient = new StoreIngredient(
                "",
                "Pears",
                9F,
                "fruit",
                new Date(),
                "fridge",
                "items"
        );
        StoreIngredient ingredient2 = mockStoreIngredient2();
        StoreIngredient ingredient3 = new StoreIngredient(
                "5678",
                "Apples",
                7F,
                "fruit",
                new Date(20),
                "fridge",
                "items"
        );

        assertFalse(ingredient.compareCalendar(ingredient2));
        assertTrue(ingredient.compareCalendar(ingredient));
        assertTrue(ingredient2.compareCalendar(ingredient3));
    }
}
