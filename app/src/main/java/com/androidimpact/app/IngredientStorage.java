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

    //should I remove this function?
    /**
     * this function returns the ingredientStorageList
     * @return ingredientStorageList
     */
    public ArrayList<StoreIngredient> getIngredientStorageList() {
        return ingredientStorageList;
    }

    /**
     *
     * @param i
     * @return
     */
    public StoreIngredient get(int i) {
        return ingredientStorageList.get(i);
    }

    /**
     *
     * @param i
     * @param ingredient
     */
    public void set(int i, StoreIngredient ingredient){
        this.ingredientStorageList.set(i, ingredient);
    }

    /**
     *
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
     *
     * @return
     */
    public int size()
    {
        return this.ingredientStorageList.size();
    }

    public void clear()
    {
        this.ingredientStorageList.clear();
    }
}
