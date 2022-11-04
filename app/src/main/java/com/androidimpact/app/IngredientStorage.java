package com.androidimpact.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


//Need to implement compare to in ingredient storage
/**
 * IngredientStorage class
 * ingredientStorageList (ArrayList<StoreIngredient>)
 * @version 1.0
 * @author vedantvyas
 */

public class IngredientStorage {
    private ArrayList<StoreIngredient> ingredientStorageList;

    private static String[] sortChoices;
    private int sortIndex;

    public static Comparator<StoreIngredient> descriptionComparator, bbdComparator, locationComparator, categoryComparator;


    /**
     * constructor for IngredientStorage
     */
    public IngredientStorage()
    {
        this.ingredientStorageList = new ArrayList<StoreIngredient>();

        this.sortChoices = new String[]{
                "Description",
                "Best Before Date",
                "Location",
                "Ingredient Category"
        };
        this.sortIndex = 0;
        // set compare variables
        descriptionComparator = Comparator.comparing(StoreIngredient::getDescription, String.CASE_INSENSITIVE_ORDER);
        bbdComparator = Comparator.comparing(StoreIngredient::getBestBeforeCalendar);
        locationComparator = Comparator.comparing(StoreIngredient::getLocation, String.CASE_INSENSITIVE_ORDER);
        categoryComparator = Comparator.comparing(StoreIngredient::getCategory, String.CASE_INSENSITIVE_ORDER);

    }

    /**
     * this function returns the ingredientStorageList
     * @return ingredientStorageList (ArrayList<StoreIngredient>)
     */
    public ArrayList<StoreIngredient> getIngredientStorageList() {
        return ingredientStorageList;
    }

    /**
     * this function returns the element at i index in the list
     * @param i (int)
     * @return (StoreIngredient)
     */
    public StoreIngredient get(int i) {
        return ingredientStorageList.get(i);
    }

    /**
     * this function sets the ingredients to index i in ingredientStorageList
     * @param i (int)
     * @param ingredient (StoreIngredient)
     */
    public void set(int i, StoreIngredient ingredient){
        this.ingredientStorageList.set(i, ingredient);
    }

    /**
     * this function adds the ingredient to ingredientStorageList
     * @param ingredient (StoreIngredient)
     */
    public void add(StoreIngredient ingredient)
    {

        this.ingredientStorageList.add(ingredient);
    }

    /**
     * This function removes the element from ingredientStorageList at index i
     * @param i (int)
     */
    public void remove(int i)
    {
        if (i < this.size() && i >= 0)
            this.ingredientStorageList.remove(i);
        else
            throw new ArrayIndexOutOfBoundsException("please choose a i between 0 and list size");
    }

    /**
     * This function removes the ingredient from ingredientStorageList
     * @param ingredient (StoreIngredient)
     */
    public void remove(StoreIngredient ingredient)
    {
        if (this.ingredientStorageList.contains(ingredient))
            this.ingredientStorageList.remove(ingredient);
        else
            throw new IllegalArgumentException("Trying to remove ingredient which isn't in list");
    }

    /**
     * this function returns the size of the list
     * @return (int)
     */
    public int size()
    {
        return this.ingredientStorageList.size();
    }


    /**
     * Return the current sorting choice for the recipe list
     * @return the index of the sorting choices for the user
     */
    public String getSortChoice() {
        return this.sortChoices[this.sortIndex];
    }

    /**
     * Set the sorting choice for the recipe list
     * @param index the index of the sorting choices for the user
     */
    public void setSortChoice(int index) {
        this.sortIndex = index;
    }



    /**
     * Return the sorting choices for the recipe list
     * @return list of available sorting choices
     */
    public static String[] getSortChoices() {
        return sortChoices.clone(); }

    /**
     * This function allows us to sort the recipe list by the user's choice
     */
    public void sortByChoice() {
        switch(this.sortIndex) {
            case 0:
                Collections.sort(ingredientStorageList, descriptionComparator); break;
            case 1:
                Collections.sort(ingredientStorageList, bbdComparator); break;
            case 2:
                Collections.sort(ingredientStorageList, locationComparator); break;
            case 3:
                Collections.sort(ingredientStorageList, categoryComparator); break;
        }
    }

    /**

    /**
     * this function clears the ingredientStorageList
     */
    public void clear()
    {
        this.ingredientStorageList.clear();
    }
}
