package com.androidimpact.app;

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;

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
        defaultComparator = Comparator.comparing(Recipe::getDate, String.CASE_INSENSITIVE_ORDER);
        titleComparator = Comparator.comparing(Recipe::getTitle, String.CASE_INSENSITIVE_ORDER);
        prepTimeComparator = Comparator.comparingInt(Recipe::getPrep_time);
        servingsComparator = Comparator.comparingInt(Recipe::getServings);
        categoryComparator = Comparator.comparing(Recipe::getCategory, String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * This method returns the size of the recipe list
     * @return
     * integer length of recipe list
     */
    public int getItemCount() {
        return recipeArrayList.size();
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
