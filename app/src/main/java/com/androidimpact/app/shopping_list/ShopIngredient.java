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
import com.androidimpact.app.location.Location;

import java.io.Serializable;
import java.util.Objects;

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

    /**
     * Overriding equals so array searching can work
     * @param o another object to compare
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof ShopIngredient)) {
            return false;
        }

        // typecast o to ShopIngredient so that we can compare data members
        ShopIngredient c = (ShopIngredient) o;

        // Compare the data members and return accordingly
        boolean descriptionEquals = Objects.equals(description, c.getDescription());
        boolean amountEquals = Objects.equals(amount, c.getAmount());
        boolean unitEquals = Objects.equals(unit, c.getUnit());
        return descriptionEquals && amountEquals && unitEquals;
    }
}
