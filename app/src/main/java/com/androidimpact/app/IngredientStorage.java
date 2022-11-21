package com.androidimpact.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


//Need to implement compare to in ingredient storage
/**
 * IngredientStorage class
 * ingredientStorageList (ArrayList<StoreIngredient>)
 * @version 1.0
 * @author vedantvyas
 */

public class IngredientStorage extends SortableItemList<StoreIngredient> {
    //private ArrayList<StoreIngredient> ingredientStorageList;
    private static String[] sortChoices;

    /**
     * constructor for IngredientStorage
     */
    public IngredientStorage()
    {
        super(new ArrayList<>(),
                new String[]{
                        "Description",
                        "Best Before Date",
                        "Ingredient Category"
                },
                (Comparator<StoreIngredient>[]) Arrays.asList(
                        Comparator.comparing(StoreIngredient::getDescription, String.CASE_INSENSITIVE_ORDER),
                        Comparator.comparing(StoreIngredient::getBestBeforeCalendar),
                        Comparator.comparing(StoreIngredient::getCategory, String.CASE_INSENSITIVE_ORDER)
                ).toArray()
        );
        //this.ingredientStorageList = new ArrayList<>();

        sortChoices = new String[]{
                "Description",
                "Best Before Date",
                "Ingredient Category"
        };
    }

    /**
     * this function returns the ingredientStorageList
     * @return ingredientStorageList (ArrayList<StoreIngredient>)
     */
    public ArrayList<StoreIngredient> getIngredientStorageList() {
        return super.getData();
    }

    /**
     * Return the sorting choices for the recipe list
     * @return list of available sorting choices
     */
    public static String[] getSortChoices() {
        return sortChoices.clone(); }

    /**
     * this function clears the ingredientStorageList
     */
    public void clear()
    {
        this.getData().clear();
    }
}
