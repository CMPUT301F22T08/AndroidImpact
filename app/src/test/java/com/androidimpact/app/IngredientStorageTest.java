package com.androidimpact.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import java.util.Date;

public class IngredientStorageTest {

    private IngredientStorage mockIngredientList()
    {
        IngredientStorage ingredientList = new IngredientStorage();
        ingredientList.add(mockIngredient());
        return ingredientList;
    }
    private StoreIngredient mockIngredient()
    {
        return new StoreIngredient("test food", 0, "", "",new Date(), "trial");
    }

    @Test
    public void testAdd(){
        IngredientStorage ingredientList = mockIngredientList();
        assertEquals(1, ingredientList.size());
        StoreIngredient ingredient = new StoreIngredient("egg", 10, "", "poultry",new Date(), "fridge");
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
    }


    // Need some clarification for this error from team so will fix it later
    @Test
    public void testAddException() {
        IngredientStorage ingredientList = mockIngredientList();
        StoreIngredient ingredient = new StoreIngredient("egg", 10, "", "poultry",new Date(), "fridge");
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
        ingredientList.add(ingredient);
        assertEquals(3, ingredientList.size());
    }
    @Test
    public void testGetIngredient() {
        IngredientStorage ingredientList = mockIngredientList();
        StoreIngredient ingredient = new StoreIngredient("egg", 10, "", "poultry",new Date(), "fridge");
        ingredientList.add(ingredient);
        //to add a compareTo method in store ingredient
        assertEquals(true, ingredient == ingredientList.get(1));

        assertEquals(false, ingredient == ingredientList.get(0));
    }

    @Test
    public void testSetIngredient() {
        IngredientStorage ingredientList = mockIngredientList();
        StoreIngredient ingredient = new StoreIngredient("egg", 10, "", "poultry",new Date(), "fridge");
        ingredientList.set(0, ingredient);
        //to add a compareTo method in store ingredient
        assertEquals(true, ingredient == ingredientList.get(0));

    }

    @Test
    public void testRemove()
    {
        IngredientStorage ingredientList = mockIngredientList();
        StoreIngredient ingredient = new StoreIngredient("egg", 10, "", "poultry",new Date(), "fridge");
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
        ingredientList.remove(ingredient);
        assertEquals(1, ingredientList.size());

        // test deleting invalid store ingredient
        assertThrows( IllegalArgumentException.class, () -> {
            ingredientList.remove(new StoreIngredient("egg1", 10, "", "plt", new Date(), "counter")); });

        assertEquals(1, ingredientList.size());
    }


    @Test
    public void testClear()
    {
        IngredientStorage ingredientList = mockIngredientList();
        StoreIngredient ingredient = new StoreIngredient("egg", 10, "", "poultry",new Date(), "fridge");
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
        ingredientList.clear();
        assertEquals(0, ingredientList.size());

    }
    @Test
    public void testSize()
    {
        IngredientStorage ingredientList = mockIngredientList();
        assertEquals(1, ingredientList.size());
        StoreIngredient ingredient = new StoreIngredient("egg", 10, "", "poultry",new Date(), "fridge");
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
        ingredientList.clear();
        assertEquals(0, ingredientList.size());

    }

    @Test
    public void testRemoveInt()
    {
        IngredientStorage ingredientList = mockIngredientList();
        StoreIngredient ingredient = new StoreIngredient("egg", 10, "", "poultry",new Date(), "fridge");
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
        ingredientList.remove(1);
        assertEquals(1, ingredientList.size());

        // test deleting invalid city
        assertThrows( ArrayIndexOutOfBoundsException.class, () -> {
            ingredientList.remove(5); });

        assertEquals(1, ingredientList.size());
    }

}
