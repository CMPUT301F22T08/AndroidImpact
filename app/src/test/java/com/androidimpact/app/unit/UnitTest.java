package com.androidimpact.app.unit;

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
 * This class tests the functions in Unit.java
 * @version 1.0
 * @author Aneeljyot Alagh
 */
public class UnitTest {

    private String unitName = "bags";

    /**
     * This function generates a mock unit using the constructor.
     * @return (Unit) a mock unit object
     */
    public Unit mockUnit() {
        return new Unit(unitName);
    }

    /**
     * This function tests the methods toString(), getUnit(), setUnit()
     */
    @Test
    public void testUnitString() {
        Unit unit = mockUnit();

        // test toString()
        assertEquals(unitName, unit.toString());

        // test getCategory()
        assertEquals(unitName, unit.getUnit());

        // test setCategory()
        assertEquals(unitName, unit.getUnit());
        unit.setUnit("bags");
        assertEquals("bags", unit.getUnit());
    }

    /**
     * This function tests the methods getDateAdded(), setDateAdded()
     */
    @Test
    public void testDate() {
        Unit unit = mockUnit();

        // test getDateAdded(), setDateAdded()
        Date temp = unit.getDateAdded();
        unit.setDateAdded(new Date(0));
        assertNotEquals(temp, unit.getDateAdded());
    }

    /**
     * This function tests the method equals()
     */
    @Test
    public void testEquals() {
        Unit unit1 = mockUnit();
        Unit unit2 = new Unit("stalks");
        unit2.setDateAdded(new Date(0));

        assertFalse(unit1.equals(unit2));
        assertTrue(unit1.equals(unit1));
        assertFalse(unit1.equals(5));

        unit2.setUnit(unitName);
        assertTrue(unit1.equals(unit2));
    }

}
