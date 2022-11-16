package com.androidimpact.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class IngredientStorageFragmentTest {

    /**
     * This function creates a ingredientList with a single ingredient and returns
     * @return type(ingredientStorage)
     */
    private IngredientStorage mockIngredientList()
    {
        IngredientStorage ingredientList = new IngredientStorage();
        ingredientList.add(mockIngredient());
        return ingredientList;
    }

    /**
     * This function creates a store ingredient object and returns it
     * @return type(StoreIngredient)
     */
    private StoreIngredient mockIngredient()
    {
        return new StoreIngredient("01", "test food", 0, "", new Date(), "", "trial");
    }

    /**
     * This function tests addition of ingredients to ingredient list
     */
    @Test
    public void testAdd(){
        IngredientStorage ingredientList = mockIngredientList();
        assertEquals(1, ingredientList.size());
        StoreIngredient ingredient = mockIngredient();
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
    }


    // Need some clarification for this error from team so will fix it later
    @Test
    public void testAddException() {
        IngredientStorage ingredientList = mockIngredientList();
        StoreIngredient ingredient = mockIngredient();
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
        ingredientList.add(ingredient);
        assertEquals(3, ingredientList.size());
    }
    @Test
    public void testGetIngredient() {
        IngredientStorage ingredientList = mockIngredientList();
        StoreIngredient ingredient = mockIngredient();
        ingredientList.add(ingredient);
        //to add a compareTo method in store ingredient
        assertEquals(true, ingredient == ingredientList.get(1));

        assertEquals(false, ingredient == ingredientList.get(0));
    }

    /**
     *  this function allows us to test setIngredient() method
     */
    @Test
    public void testSetIngredient() {
        IngredientStorage ingredientList = mockIngredientList();
        StoreIngredient ingredient = mockIngredient();
        ingredientList.set(0, ingredient);
        //to add a compareTo method in store ingredient
        assertEquals(true, ingredient == ingredientList.get(0));

    }

    /**
     * This function tests if we can remove the ingredient from ingredient storage
     */
    @Test
    public void testRemove()
    {

        StoreIngredient ingredient = mockIngredient();
        IngredientStorage ingredientList = mockIngredientList();
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
        ingredientList.remove(ingredient);
        assertEquals(1, ingredientList.size());

        // test deleting invalid store ingredient
        assertThrows( IllegalArgumentException.class, () -> {
            ingredientList.remove(new StoreIngredient("0101", "egg1", 10, "", "plt", new Date(), "counter")); });

        assertEquals(1, ingredientList.size());
    }
    /**
     * Test that the default sort choice is as below
     * Initialize the ingredientlist and ensure value of default sort choice is "Description"
     */
    @Test
    public void getSortChoiceTest() {
        IngredientStorage ingredientList = mockIngredientList();
        assertEquals(ingredientList.getSortChoice(), "Description");
    }

    /**
     * Test that the set sort choice is correct
     * Initialize the recipe list and ensure the value when we set the sort choice to one of our
     * choosing, then it reflects the choice we want
     */
    @Test
    public void setSortChoiceTest() {
        IngredientStorage ingredientList = mockIngredientList();
        ingredientList.setSortChoice(1);
        assertEquals(ingredientList.getSortChoice(), "Best Before Date");
    }

    /**
     * Test that the sort choices available to the user are correctly returned
     * In this case, check all the values are the same as intended, and that there aren't any more
     * than intended
     */
    @Test
    public void getSortChoicesTest() {
        IngredientStorage ingredientList = mockIngredientList();
        assertTrue(Arrays.equals(ingredientList.getSortChoices(), new String[]{
                "Description",
                "Best Before Date",
                "Location",
                "Ingredient Category"
        }));
    }

    /**
     * Test that sorting by a user's choice actually sorts the list as intended
     * In this case, set attributes of recipe using a number i
     * In the way implemented in the test, each sorting will result in a unique recipe at the 'top'
     * Check that this unique value is at the 'top' by title
     */
    @Test
    public void sortByChoiceTest() {
        ArrayList<StoreIngredient> IngredientTestList = new ArrayList<>();
        int n = 5;
        float dum = 0;
        //String id, String description, float amount, String unit, String category, Date bestBeforeDate, String location
        for(int i = 0; i < n; i++) {
            IngredientTestList.add(new StoreIngredient(
                    "",
                    String.valueOf(i), // n-(n-i) = i
                    dum,
                    "",
                    String.valueOf(n-(4-i)),
                    new Date(i),
                    String.valueOf(n-(3-i))
            ));
        }


        IngredientStorage ingredientList = new IngredientStorage();
        //To be changed
        for (int i = 0; i < n; ++i){
            ingredientList.add(IngredientTestList.get(i));
        }

        for(int i = 0; i < n; i++) {
            ingredientList.setSortChoice(i);
            ingredientList.sortByChoice();

            assertEquals(ingredientList.get(i).getDescription(), String.valueOf(i));
        }

        assertTrue(true);
    }


    /**
     * This function tests if clear method for ingredient List works properly
     */
    @Test
    public void testClear()
    {
        IngredientStorage ingredientList = mockIngredientList();
        StoreIngredient ingredient = mockIngredient();
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
        ingredientList.clear();
        assertEquals(0, ingredientList.size());

    }

    /**
     * This function tests if size method for ingredient List works properly
     */
    @Test
    public void testSize()
    {
        IngredientStorage ingredientList = mockIngredientList();
        assertEquals(1, ingredientList.size());
        StoreIngredient ingredient = mockIngredient();
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
        ingredientList.clear();
        assertEquals(0, ingredientList.size());

    }

    /**
     * This function checks if we can call remove(int) and remove that index
     */
    @Test
    public void testRemoveInt()
    {
        IngredientStorage ingredientList = mockIngredientList();
        StoreIngredient ingredient = mockIngredient();
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
        ingredientList.remove(1);
        assertEquals(1, ingredientList.size());

        // test deleting invalid store ingredient
        assertThrows( ArrayIndexOutOfBoundsException.class, () -> {
            ingredientList.remove(5); });

        assertEquals(1, ingredientList.size());
    }

}
