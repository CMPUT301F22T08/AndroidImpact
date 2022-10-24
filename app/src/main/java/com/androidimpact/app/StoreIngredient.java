package com.androidimpact.app;

import java.io.Serializable;
import java.util.Calendar;

/**
 * StoreIngredient object that is stored in IngredientStorage
 * Extended from Ingredient
 * Extra attributes:
 *  - bestBeforeDate (Calendar) - The best before date for the stored ingredient
 *  - location (String) - Where the ingredient is stored e.g. Pantry or Freezer
 * @author Kailash Seshadri
 * @version 1.0
 * @see Ingredient
 */
public class StoreIngredient extends Ingredient implements Serializable {
    private Calendar bestBeforeDate;
    private String location;

    /**
     * Constructor for the StoreIngredient class
     * @param description (String) - A short description of the ingredient e.g. peppercorn ranch
     * @param amount (float) - The quantity of the ingredient needed for the recipe/shopping list e.g. 300 in 300g
     * @param unit (String) - The unit that amount is measuring e.g. g in 300g
     * @param category (String) - Any name that helps categorize the ingredient e.g. Sauce for peppercorn ranch
     * @param bestBeforeDate (Calendar) - The best before date for the stored ingredient
     * @param location (String) - Where the ingredient is being stored
     * @see Ingredient
     */
    public StoreIngredient(String description, float amount, String unit, String category, Calendar bestBeforeDate, String location){
        super(description, amount, unit, category);
        this.bestBeforeDate = bestBeforeDate;
        this.location = location;
    }

    /**
     * Get the bestBeforeDate of the stored ingredient
     * @return (Calendar) The best before date for the stored ingredient
     */
    public Calendar getBestBeforeDate() {
        return bestBeforeDate;
    }

    /**
     * Change the best-before date of the stored ingredient
     * @param bestBeforeDate (Calendar) - The new best-before date of the stored ingredient
     */
    public void setBestBeforeDate(Calendar bestBeforeDate) {
        this.bestBeforeDate = bestBeforeDate;
    }

    /**
     * Get the location where the ingredient is currently stored
     * @return (String) Where the ingredient is being stored
     */
    public String getLocation() {
        return location;
    }

    /**
     * Change the location where the ingredient is stored
     * @param location (String) - The new location of the ingredient being stored
     */
    public void setLocation(String location) {
        this.location = location;
    }
}
