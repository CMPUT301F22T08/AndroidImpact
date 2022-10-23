package com.androidimpact.app;

/**
 * Ingredient class: Holds the information of an ingredient to be used in recipes and the shopping list
 * - description (String) - A short description of the ingredient e.g. peppercorn ranch
 * - amount (float) - The quantity of the ingredient needed for the recipe/shopping list e.g. 300 in 300g
 * - unit (String) - The unit that amount is measuring e.g. g in 300g
 * - category (String) - Any name that helps categorize the ingredient e.g. Sauce for peppercorn ranch
 * @author Kailash Seshadri
 * @version 1.0
 */
public class Ingredient {
    protected String description;
    protected float amount;
    protected String unit;
    protected String category;

    /**
     * Constructor for the Ingredient class
     * @author Kailash Seshadri
     * @version 1.0
     * @param description (String) - A short description of the ingredient e.g. peppercorn ranch
     * @param amount (float) - The quantity of the ingredient needed for the recipe/shopping list e.g. 300 in 300g
     * @param unit (String) - The unit that amount is measuring e.g. g in 300g
     * @param category (String) - Any name that helps categorize the ingredient e.g. Sauce for peppercorn ranch
     * */
    public Ingredient(String description, float amount, String unit, String category) {
        this.description = description;
        this.amount = amount;
        this.unit = unit;
        this.category = category;
    }

    /**
     * Getter for ingredient description
     * @author Kailash Seshadri
     * @version 1.0
     * @return this.description (String)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Rename the ingredient
     * @author Kailash Seshadri
     * @version 1.0
     * @param description (String) - The new description to be used
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the amount of the ingredient
     * @author Kailash Seshadri
     * @version 1.0
     * @return this.amount (float)
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Set a new amount of the ingredient
     * @author Kailash Seshadri
     * @version 1.0
     * @param amount (float) - The new amount to be used
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * Get the units that amount is measuring
     * @author Kailash Seshadri
     * @version 1.0
     * @return this.unit (String)
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Change the unit of measurement
     * @author Kailash Seshadri
     * @version 1.0
     * @param unit (String) - The new unit of measurement
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Get the ingredient category
     * @author Kailash Seshadri
     * @version 1.0
     * @return this.category (String)
     */
    public String getCategory() {
        return category;
    }

    /**
     * Give the ingredient a new category
     * @author Kailash Seshadri
     * @version 1.0
     * @param category (String) - The new category of the ingredient
     */
    public void setCategory(String category) {
        this.category = category;
    }
}