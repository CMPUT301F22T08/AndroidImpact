package com.androidimpact.app.shopping_list;

/**
 * StoreIngredient object that is stored in IngredientStorage
 * Extended from Ingredient
 * Extra attributes:
 *  - (float) amountPicked - this attribute specifies the amount user picked up for shopping list
 *  Need to add some unit testing
 * @author Vedant Vyas
 * @version 1.0
 * @see Ingredient
 */

import com.androidimpact.app.ingredients.Ingredient;

import java.io.Serializable;

public class ShopIngredient extends Ingredient implements Serializable {

    private float amountPicked = 0;


    /**
     * Empty constructor for firebase
     */
    public ShopIngredient() {}



    /**
     * Constructor for the ShopIngredient class
     * @param id (String) - Document id in Firebase
     * @param description (String) - A short description of the ingredient e.g. peppercorn ranch
     * @param amount (float) - The quantity of the ingredient needed for the recipe/shopping list e.g. 300 in 300g
     * @param category (String) - Category of the ingredient
     * @param unit (String) - string value of the unit
     * @see Ingredient
     */
    public ShopIngredient(String id, String description, float amount, String unit, String category) {
        super(id, description, amount, unit, category);
        this.amountPicked=0;
    }


    /**
     * Second Constructor for the ShopIngredient class
     * @param id (String) - Document id in Firebase
     * @param description (String) - A short description of the ingredient e.g. peppercorn ranch
     * @param amount (float) - The quantity of the ingredient needed for the recipe/shopping list e.g. 300 in 300g
     * @param category (String) - Category of the ingredient
     * @param unit (String) - string value of the unit
     * @param amountPicked - quantity of ingredient picked up by user while shopping
     * @see Ingredient
     */
    public ShopIngredient(String id, String description, float amount, String unit, String category, float amountPicked) {
        super(id, description, amount, unit, category);
        this.amountPicked=amountPicked;
    }

    /**
     * Get the amount picked up of the shop ingredient
     * @return amountPicked
     */
    public float getAmountPicked() {
        return amountPicked;
    }

    /**
     * Set the amount picked up of the shop ingredient
     * @param amountPicked
     */
    public void setAmountPicked(float amountPicked) {
        this.amountPicked = amountPicked;
    }

}
