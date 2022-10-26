package com.androidimpact.app;

import static org.junit.Assert.assertEquals;

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

}
