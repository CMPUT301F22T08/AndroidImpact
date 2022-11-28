package com.androidimpact.app.recipes;

import com.androidimpact.app.SortableItemList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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
     * Constructor for an empty recipeList
     */
    public RecipeList() {
        super(new ArrayList<>(),
                new String[]{
                        "Date Added",
                        "Title",
                        "Preparation Time",
                        "Number of Servings",
                        "Recipe Category"
                },
                new Comparator[]{
                        Comparator.comparingInt(a -> (int) ((Recipe) a).getDate().getTime()),
                        Comparator.comparing(Recipe::getTitle, String.CASE_INSENSITIVE_ORDER),
                        Comparator.comparingInt(Recipe::getPrep_time),
                        Comparator.comparingInt(Recipe::getServings),
                        Comparator.comparing(Recipe::getCategory, String.CASE_INSENSITIVE_ORDER)
                }
        );
        sortChoices = new String[]{
                "Date Added",
                "Title",
                "Preparation Time",
                "Number of Servings",
                "Recipe Category"
        };
    }

    public RecipeList(ArrayList<Recipe> recipeList) {
        super(recipeList,
                new String[]{
                        "Date Added",
                        "Title",
                        "Preparation Time",
                        "Number of Servings",
                        "Recipe Category"
                },
                new Comparator[]{
                        Comparator.comparingInt(a -> (int) ((Recipe) a).getDate().getTime()),
                        Comparator.comparing(Recipe::getTitle, String.CASE_INSENSITIVE_ORDER),
                        Comparator.comparingInt(Recipe::getPrep_time),
                        Comparator.comparingInt(Recipe::getServings),
                        Comparator.comparing(Recipe::getCategory, String.CASE_INSENSITIVE_ORDER)
                });
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
