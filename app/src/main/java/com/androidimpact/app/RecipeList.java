package com.androidimpact.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class defines a recipe list
 * @author Aneeljyot Alagh, Clare Chen
 * @version 1.0
 */
public class RecipeList extends SortableItemList<Recipe> {

    // creating a variable for our array list
    //private ArrayList<Recipe> recipeArrayList;
    private static String[] sortChoices;

    /**
     * Constructor for RecipeList
     * @param recipeArrayList   the recipes to consider in the RecipeList object
     */
    public RecipeList(ArrayList<Recipe> recipeArrayList) {
        super(recipeArrayList,
                new String[]{
                        "Date Added",
                        "Title",
                        "Preparation Time",
                        "Number of Servings",
                        "Recipe Category"
                },
                (Comparator<Recipe>[]) Arrays.asList(
                        Comparator.comparingInt(a -> (int) ((Recipe) a).getDate().getTime()),
                        Comparator.comparing(Recipe::getTitle, String.CASE_INSENSITIVE_ORDER),
                        Comparator.comparingInt(Recipe::getPrep_time),
                        Comparator.comparingInt(Recipe::getServings),
                        Comparator.comparing(Recipe::getCategory, String.CASE_INSENSITIVE_ORDER)
                ).toArray());
        //this.recipeArrayList = recipeArrayList;
        sortChoices = new String[]{
                "Date Added",
                "Title",
                "Preparation Time",
                "Number of Servings",
                "Recipe Category"
        };
    }

    /**
     * Return the sorting choices for the recipe list
     * @return list of available sorting choices
     */
    public static String[] getSortChoices() { return sortChoices.clone(); }
}
