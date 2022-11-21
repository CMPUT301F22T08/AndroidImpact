package com.androidimpact.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class defines a recipe list
 * @author Aneeljyot Alagh, Clare Chen
 * @version 1.0
 */
public class RecipeList {

    // creating a variable for our array list and context.
    private ArrayList<Recipe> recipeArrayList;
    private static String[] sortChoices;
    private int sortIndex;
    public static Comparator<Recipe> defaultComparator, titleComparator, prepTimeComparator, servingsComparator, categoryComparator;

    /**
     * Constructor for RecipeList
     * @param recipeArrayList   the recipes to consider in the RecipeList object
     */
    public RecipeList(ArrayList<Recipe> recipeArrayList) {
        this.recipeArrayList = recipeArrayList;
        this.sortChoices = new String[]{
                "Date Added",
                "Title",
                "Preparation Time",
                "Number of Servings",
                "Recipe Category"
        };
        this.sortIndex = 0;

        // set compare variables
        defaultComparator = Comparator.comparingInt(a -> (int) a.getDate().getTime());
        titleComparator = Comparator.comparing(Recipe::getTitle, String.CASE_INSENSITIVE_ORDER);
        prepTimeComparator = Comparator.comparingInt(Recipe::getPrep_time);
        servingsComparator = Comparator.comparingInt(Recipe::getServings);
        categoryComparator = Comparator.comparing(Recipe::getCategory, String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * this function returns the element at i index in the list
     * @param i (int)
     * @return (Recipe)
     */
    public Recipe get(int i) {
        return this.recipeArrayList.get(i);
    }

    /**
     * this function sets the recipes to index i in RecipeList
     * @param i (int)
     * @param recipe (Recipe)
     */
    public void set(int i, Recipe recipe){
        this.recipeArrayList.set(i, recipe);
    }

    /**
     * this function adds the recipe to RecipeList
     * @param recipe (Recipe)
     */
    public void add(Recipe recipe)
    {
        this.recipeArrayList.add(recipe);
    }

    /**
     * This function removes the element from recipeList at index i
     * @param i (int)
     */
    public void remove(int i)
    {
        if (i < this.size() && i >= 0)
            this.recipeArrayList.remove(i);
        else
            throw new ArrayIndexOutOfBoundsException("please choose a i between 0 and list size");
    }

    /**
     * This function removes the recipe from the recipeList
     * @param recipe (Recipe)
     */
    public void remove(Recipe recipe)
    {
        if (this.recipeArrayList.contains(recipe))
            this.recipeArrayList.remove(recipe);
        else
            throw new IllegalArgumentException("Trying to remove recipe which isn't in list");
    }

    /**
     * this function returns the size of the list
     * @return (int)
     */
    public int size()
    {
        return this.recipeArrayList.size();
    }

    /**
     * Return the current sorting choice for the recipe list
     * @return the sorting choice at the index chosen by the user
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
                Collections.sort(recipeArrayList, defaultComparator); break;
            case 1:
                Collections.sort(recipeArrayList, titleComparator); break;
            case 2:
                Collections.sort(recipeArrayList, prepTimeComparator); break;
            case 3:
                Collections.sort(recipeArrayList, servingsComparator); break;
            case 4:
                Collections.sort(recipeArrayList, categoryComparator); break;
        }
    }
}
