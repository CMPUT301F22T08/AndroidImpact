package com.androidimpact.app;

import java.util.Date;

/**
 * StoreIngredient object that is stored in IngredientStorage
 * Extended from Ingredient
 * Extra attributes:
 *  - bestBeforeDate (Date) - The best before date for the stored ingredient
 *  - location (String) - Where the ingredient is stored e.g. Pantry or feezer
 */
public class StoreIngredient extends Ingredient{
    private Date bestBeforeDate;
    private String location;

    /**
     * Constructor for the StoreIngredient class
     * @param description (String) - A short description of the ingredient e.g. peppercorn ranch
     * @param amount (float) - The quantity of the ingredient needed for the recipe/shopping list e.g. 300 in 300g
     * @param unit (String) - The unit that amount is measuring e.g. g in 300g
     * @param category (String) - Any name that helps categorize the ingredient e.g. Sauce for peppercorn ranch
     * @param bestBeforeDate (Date) - The best before date for the stored ingredient
     * @param location (String) - Where the ingredient is being stored
     */
    public StoreIngredient(String description, float amount, String unit, String category, Date bestBeforeDate, String location){
        super(description, amount, unit, category);
        this.bestBeforeDate = bestBeforeDate;
        this.location = location;
    }

    /**
     * Get the bestBeforeDate of the stored ingredient
     * @return bestBeforeDate (Date) - The best before date for the stored ingredient
     */
    public Date getBestBeforeDate() {
        return bestBeforeDate;
    }

    /**
     * Change the best-before date of the stored ingredient
     * @param bestBeforeDate (Date) - The new best-before date of the stored ingredient
     */
    public void setBestBeforeDate(Date bestBeforeDate) {
        this.bestBeforeDate = bestBeforeDate;
    }

    /**
     * Get the location where the ingredient is currently stored
     * @return location (String) - Where the ingredient is being stored
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
