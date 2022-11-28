package com.androidimpact.app.shopping_list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.androidimpact.app.ingredients.IngredientStorage;
import com.androidimpact.app.ingredients.StoreIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ShoppingListTest
{

    public ShoppingList mockShoppingList()
    {
        ArrayList<ShopIngredient> list = new ArrayList<>();
        ShoppingList ingredientList = new ShoppingList(list);
        ingredientList.add(mockShopIngredient());
        return ingredientList;
    }

    public ShopIngredient mockShopIngredient()
    {
        return new ShopIngredient("01", "test food", 0, "","");
    }

    public ShopIngredient mockShopIngredient(String description)
    {
        return new ShopIngredient("01", description, 0, "","");
    }




    /**
     * This function tests addition of ingredients to ingredient list
     */
    @Test
    public void testAdd(){
        ShoppingList ingredientList = mockShoppingList();
        assertEquals(1, ingredientList.size());
        ShopIngredient ingredient = mockShopIngredient();
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
    }




    /**
     * This function checks the getter method getIngredient()
     */
    @Test
    public void testGetIngredient() {
        ShoppingList ingredientList = mockShoppingList();
        ShopIngredient ingredient = mockShopIngredient("banana");
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
        ShoppingList ingredientList = mockShoppingList();
        ShopIngredient ingredient = mockShopIngredient("banana");
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

        ShopIngredient ingredient = mockShopIngredient();
        ShoppingList ingredientList = mockShoppingList();
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
        ingredientList.remove(1);
        assertEquals(1, ingredientList.size());

        // test deleting invalid store ingredient
        assertThrows( ArrayIndexOutOfBoundsException.class, () -> {
            ingredientList.remove(4); });


        assertEquals(1, ingredientList.size());
    }

    /**
     * Test that the default sort choice is as below
     * Initialize the ingredientlist and ensure value of default sort choice is "Description"
     */
    @Test
    public void getSortChoiceTest() {
        ShoppingList ingredientList = mockShoppingList();
        assertEquals(ingredientList.getSortChoice(), "Description");
    }


    /**
     * Test that the set sort choice is correct
     * Initialize the recipe list and ensure the value when we set the sort choice to one of our
     * choosing, then it reflects the choice we want
     */
    @Test
    public void setSortChoiceTest() {
        ShoppingList ingredientList = mockShoppingList();
        ingredientList.setSortChoice(1);
        assertEquals(ingredientList.getSortChoice(), "ShopIngredient Category");
    }



    /**
     * Test that the sort choices available to the user are correctly returned
     * In this case, check all the values are the same as intended, and that there aren't any more
     * than intended
     */
    @Test
    public void getSortChoicesTest() {
        ShoppingList ingredientList = mockShoppingList();
        assertTrue(Arrays.equals(ingredientList.getSortChoices(), new String[]{
                "Description",
                "ShopIngredient Category",

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
        ArrayList<ShopIngredient> IngredientTestList = new ArrayList<>();
        int n = 4;
        for(int i = 0; i < n; i++) {
            IngredientTestList.add(new ShopIngredient(
                    "",
                    String.valueOf(i), // n-(n-i) = i
                    0,
                    String.valueOf(n-(2-i)),
                    String.valueOf(n-(0-i))
            ));
        }

        ShoppingList ingredientList = new ShoppingList(IngredientTestList);

        for(int i = 0; i < n; i++) {
            ingredientList.setSortChoice(i%2);
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
        ShopIngredient ingredient = mockShopIngredient();
        ShoppingList ingredientList = mockShoppingList();
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
        ShoppingList ingredientList = mockShoppingList();
        assertEquals(1, ingredientList.size());
        ShopIngredient ingredient = mockShopIngredient();
        ingredientList.add(ingredient);
        assertEquals(2, ingredientList.size());
        ingredientList.clear();
        assertEquals(0, ingredientList.size());

    }



}
