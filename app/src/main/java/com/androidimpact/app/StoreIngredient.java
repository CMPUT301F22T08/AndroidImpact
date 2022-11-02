package com.androidimpact.app;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * StoreIngredient object that is stored in IngredientStorage
 * Extended from Ingredient
 * Extra attributes:
 *  - bestBeforeDate (Calendar) - The best before date for the stored ingredient
 *  - location (String) - Where the ingredient is stored e.g. Pantry or Freezer
 *  - id (String) - id of the ingredient inside Firebase
 * @author Kailash Seshadri
 * @version 1.0
 * @see Ingredient
 */
public class StoreIngredient extends Ingredient implements Serializable {
    @ServerTimestamp
    private Date bestBeforeDate;
    private String location;
    private String id;

    /**
     * Constructor for the StoreIngredient class
     * @param id (String) - Document id in Firebase
     * @param description (String) - A short description of the ingredient e.g. peppercorn ranch
     * @param amount (float) - The quantity of the ingredient needed for the recipe/shopping list e.g. 300 in 300g
     * @param unit (String) - The unit that amount is measuring e.g. g in 300g
     * @param category (String) - Any name that helps categorize the ingredient e.g. Sauce for peppercorn ranch
     * @param bestBeforeDate (Date) - The best before date for the stored ingredient
     * @param location (String) - Where the ingredient is being stored
     * @see Ingredient
     */
    public StoreIngredient(String id, String description, float amount, String unit, String category, Date bestBeforeDate, String location){
        super(description, amount, unit, category);
        this.id = id;
        this.bestBeforeDate = bestBeforeDate;
        this.location = location;
    }

    /**
     * Gets the id of the element
     * @return (String) the id of the document
     */
    public String getId() {
        return id;
    }

    /**
     * Get the best-before date of the stored ingredient
     * @return (Date) The best before date for the stored ingredient
     */
    public Date getBestBeforeDate() {
        return bestBeforeDate;
    }


    /**
     * Get the best-before date of the stored ingredient as a Calendar object
     * @return (Calendar) The best before date for the stored ingredient
     */
    public Calendar getBestBeforeCalendar(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, bestBeforeDate.getYear());
        cal.set(Calendar.MONTH, bestBeforeDate.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, bestBeforeDate.getDate());
        return cal;
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
     * @return (String) Where the ingredient is being stored
     */
    public String getLocation() {
        return location;
    }
}
