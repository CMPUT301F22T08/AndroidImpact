package com.androidimpact.app;

import java.util.ArrayList;

public class IngredientStorage {
    private ArrayList<StoreIngredient> ingredientStorageList;


    /**
     * constructor for IngredientStorage
     */
    public IngredientStorage()
    {
        this.ingredientStorageList = new ArrayList<StoreIngredient>();
    }

    /**
     * this function returns the ingredientStorageList
     * @return ingredientStorageList
     */
    public ArrayList<StoreIngredient> getIngredientStorageList() {
        return ingredientStorageList;
    }

    /**
     * this function returns the element at i index in the list
     * @param i
     * @return StoreIngredient
     */
    public StoreIngredient get(int i) {
        return ingredientStorageList.get(i);
    }

    /**
     * this function sets the ingredients to index i in ingredientStorageList
     * @param i
     * @param ingredient
     */
    public void set(int i, StoreIngredient ingredient){
        this.ingredientStorageList.set(i, ingredient);
    }

    /**
     * this function adds the ingredient to ingredientStorageList
     * @param ingredient
     */
    public void add(StoreIngredient ingredient)
    {
        this.ingredientStorageList.add(ingredient);
    }

    /**
     * This function removes the element from ingredientStorageList at index i
     * @param i
     */
    public void remove(int i)
    {
        if (i < this.size() && i >= 0)
            this.ingredientStorageList.remove(i);
    }

    /**
     * This function removes the ingredient from ingredientStorageList
     * @param ingredient
     */
    public void remove(StoreIngredient ingredient)
    {
        if (!this.ingredientStorageList.contains(ingredient))
            this.ingredientStorageList.remove(ingredient);
    }

    /**
     * this function returns the size of the list
     * @return int
     */
    public int size()
    {
        return this.ingredientStorageList.size();
    }

    /**
     * this function clears the ingredientStorageList
     */
    public void clear()
    {
        this.ingredientStorageList.clear();
    }
}
