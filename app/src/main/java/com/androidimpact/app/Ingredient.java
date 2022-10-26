package com.androidimpact.app;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

/**
 * Ingredient class: Holds the information of an ingredient to be used in recipes and the shopping list
 * - description (String) - A short description of the ingredient e.g. peppercorn ranch
 * - amount (float) - The quantity of the ingredient needed for the recipe/shopping list e.g. 300 in 300g
 * - unit (String) - The unit that amount is measuring e.g. g in 300g
 * - category (String) - Any name that helps categorize the ingredient e.g. Sauce for peppercorn ranch
 * @author Kailash Seshadri
 * @version 1.0
 */
public class Ingredient implements Serializable  {
    protected String description;
    protected float amount;
    protected String unit;
    protected String category;

    /**
     * Constructor for the Ingredient class
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
     * Get the ingredient description
     * @return (String) The description of the ingredient
     */
    public String getDescription() {
        return description;
    }

    /**
     * Rename the ingredient
     * @param description (String) - The new description to be used
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the amount of the ingredient
     * @return (float) The amount of the ingredient
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Set a new amount of the ingredient
     * @param amount (float) - The new amount to be used
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Exclude
    public void setAmount(double amount) {
        this.amount = (float) amount;
    }

    @Exclude
    public void setAmount(int amount) {
        this.amount = (float) amount;
    }

    /**
     * Get the units that amount is measuring
     * @return (String) The units that amount is measuring
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Change the unit of measurement
     * @param unit (String) - The new unit of measurement
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Get the ingredient category
     * @return (String) The category of the ingredient
     */
    public String getCategory() {
        return category;
    }

    /**
     * Give the ingredient a new category
     * @param category (String) - The new category of the ingredient
     */
    public void setCategory(String category) {
        this.category = category;
    }
}