package com.androidimpact.app.ingredients;

import com.androidimpact.app.ingredients.Ingredient;
import com.androidimpact.app.shopping_list.ShopIngredient;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * StoreIngredient object that is stored in IngredientStorage
 * Extended from Ingredient
 * Extra attributes:
 *  - bestBeforeDate (Calendar) - The best before date for the stored ingredient
 *  - location (String) - Where the ingredient is stored e.g. Pantry or Freezer
 *  - id (String) - id of the ingredient inside Firebase
 *  Need to add some unit testing
 * @author Kailash Seshadri
 * @version 1.0
 * @see Ingredient
 */
public class StoreIngredient extends Ingredient implements Serializable {
    @ServerTimestamp
    private Date bestBeforeDate;
    private String location;

    /**
     * Empty constructor or Firebase to use when deserializing
     */
    public StoreIngredient() {}

    /**
     * Constructor for the StoreIngredient class
     * @param id (String) - Document id in Firebase
     * @param description (String) - A short description of the ingredient e.g. peppercorn ranch
     * @param amount (float) - The quantity of the ingredient needed for the recipe/shopping list e.g. 300 in 300g
     * @param category (String) - Category of the ingredient
     * @param bestBeforeDate (Date) - The best before date for the stored ingredient
     * @param location (String) - document path of where the ingredient is being stored
     * @param unit (String) - string value of the unit
     * @see Ingredient
     */
    public StoreIngredient(String id, String description, float amount, String category,
                           Date bestBeforeDate, String location, String unit){
        super(id, description, amount, unit, category);
        this.bestBeforeDate = bestBeforeDate;
        this.location = location;
    }


    public StoreIngredient(ShopIngredient ingredient)
    {
        super(ingredient.getId(), ingredient.getDescription(), ingredient.getAmount(), ingredient.getUnit(), ingredient.getCategory());
        //To be changed to null
        this.bestBeforeDate = null;
        this.location = null;
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
    @Exclude
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
     * gets the location document path
     * not used by the user, but used by Firebase to know they need to serialize the locationDocumentPath
     * @return
     */
    public String getLocation() {
        return location;
    }
}
