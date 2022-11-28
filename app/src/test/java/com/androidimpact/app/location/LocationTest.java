package com.androidimpact.app.location;

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
 * This class tests the functions in Location.java
 * @version 1.0
 * @author Aneeljyot Alagh
 */
public class LocationTest {

    private String location1 = "pantry";
    private String location2 = "fridge";
    private Date date2 = new Date(0);

    /**
     * This function generates a mock location using the first constructor.
     * @return (Location) a mock location object
     */
    public Location mockLocation1() {
        return new Location(location1);
    }

    /**
     * This function generates a mock location using the second constructor.
     * @return (Location) a mock location object
     */
    public Location mockLocation2() {
        return new Location(location2, date2);
    }

    /**
     * This function tests the methods toString(), getLocation(), setLocation()
     */
    @Test
    public void testLocationString() {
        Location location = mockLocation1();

        // test toString()
        assertEquals(location1, location.toString());

        // test getLocation()
        assertEquals(location1, location.getLocation());

        // test setLocation()
        assertEquals(location1, location.getLocation());
        location.setLocation("Greece");
        assertEquals("Greece", location.getLocation());
    }

    /**
     * This function tests the methods getDateAdded(), setDateAdded()
     */
    @Test
    public void testDate() {
        Location location = mockLocation2();

        // test getDateAdded()
        assertEquals(date2, location.getDateAdded());

        // test setDateAdded()
        Date locationDate = new Date();
        location.setDateAdded(locationDate);
        assertEquals(locationDate, location.getDateAdded());
    }

    /**
     * This function tests the method equals()
     */
    @Test
    public void testEquals() {
        Location loc1 = mockLocation1();
        Location loc2 = mockLocation2();

        assertFalse(loc1.equals(loc2));
        assertTrue(loc1.equals(loc1));
        assertFalse(loc1.equals(5));

        loc2.setLocation(location1);
        assertTrue(loc1.equals(loc2));
    }

}
