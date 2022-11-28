package com.androidimpact.app.ingredients;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

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


    /**
     * This constructor creates a store ingredient object from ShopIngredient
     * @param ingredient - instance of ShopIngredient class
     */
    public StoreIngredient(ShopIngredient ingredient)
    {
        super(ingredient.getId(), ingredient.getDescription(), ingredient.getAmountPicked(), ingredient.getUnit(), ingredient.getCategory());
        //To be changed to null
        this.bestBeforeDate = new Date(0);
        this.location = "";
    }


    /**
     * Get the best-before date of the stored ingredient as a Calendar object
     * @return (Calendar) The best before date for the stored ingredient
     */
    @Exclude
    public Calendar getBestBeforeCalendar(){
        Calendar cal = Calendar.getInstance();
        cal.set(YEAR, bestBeforeDate.getYear());
        cal.set(MONTH, bestBeforeDate.getMonth());
        cal.set(DAY_OF_MONTH, bestBeforeDate.getDate());
        return cal;
    }

    public boolean compareCalendar(StoreIngredient ingredient)
    {
        Calendar cal = this.getBestBeforeCalendar();
        Calendar cal2 = ingredient.getBestBeforeCalendar();
        return cal.get(DAY_OF_MONTH) == cal2.get(DAY_OF_MONTH) && cal.get(YEAR) == cal2.get(YEAR) && cal.get(MONTH) == cal2.get(MONTH);
    }


    /**
     * Get the best-before date of the stored ingredient
     * @return (Date) The best before date for the stored ingredient
     */
    public Date getBestBeforeDate() {
        return bestBeforeDate;
    }

    /**
     * gets the location document path
     * not used by the user, but used by Firebase to know they need to serialize the locationDocumentPath
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns true if either the best before date is null or the location is null
     * @return boolean
     */
    public boolean hasNull(){
        return (bestBeforeDate.getTime()==0 || location=="");
    }
}
