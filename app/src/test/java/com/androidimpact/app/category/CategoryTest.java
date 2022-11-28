package com.androidimpact.app.category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
 * This class tests the functions in Category.java
 * @version 1.0
 * @author Aneeljyot Alagh
 */
public class CategoryTest {

    private String categoryName = "fruit";

    /**
     * This function generates a mock category using the constructor.
     * @return (Category) a mock category object
     */
    public Category mockCategory() {
        return new Category(categoryName);
    }

    /**
     * This function tests the methods toString(), getCategory(), setCategory()
     */
    @Test
    public void testCategoryString() {
        Category category = mockCategory();

        // test toString()
        assertEquals(categoryName, category.toString());

        // test getCategory()
        assertEquals(categoryName, category.getCategory());

        // test setCategory()
        assertEquals(categoryName, category.getCategory());
        category.setCategory("frozen");
        assertEquals("frozen", category.getCategory());
    }

    /**
     * This function tests the methods getDateAdded(), setDateAdded()
     */
    @Test
    public void testDate() {
        Category category = mockCategory();

        // test getDateAdded(), setDateAdded()
        Date temp = category.getDateAdded();
        category.setDateAdded(new Date(0));
        assertNotEquals(temp, category.getDateAdded());
    }

    /**
     * This function tests the method equals()
     */
    @Test
    public void testEquals() {
        Category cat1 = mockCategory();
        Category cat2 = new Category("frozen");
        cat2.setDateAdded(new Date(0));

        assertFalse(cat1.equals(cat2));
        assertTrue(cat1.equals(cat1));
        assertFalse(cat1.equals(5));

        cat2.setCategory(categoryName);
        assertTrue(cat1.equals(cat2));
    }

}
