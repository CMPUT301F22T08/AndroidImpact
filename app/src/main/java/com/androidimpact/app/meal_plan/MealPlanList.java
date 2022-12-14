package com.androidimpact.app.meal_plan;

import com.androidimpact.app.recipes.RecipeList;
import com.androidimpact.app.SortableItemList;
import com.androidimpact.app.ingredients.IngredientStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MealPlanList extends SortableItemList<MealPlan> {

    /**
     * Constructor for SortableItemList class
     *
     * @param objectArrayList (ArrayList of type MealPlan) an arraylist of meal plan data, with
     *                        any number of days' worth of meal plans
     */
    public MealPlanList(ArrayList<MealPlan> objectArrayList) {
        super(objectArrayList,
                new String[]{
                        "Date"
                },
                new Comparator[]{
                        Comparator.comparing(MealPlan::getDate, String.CASE_INSENSITIVE_ORDER)
                });
    }
}
