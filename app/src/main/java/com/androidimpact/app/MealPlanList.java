package com.androidimpact.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
                (Comparator<MealPlan>[]) Arrays.asList(
                        Comparator.comparing(MealPlan::getDate, String.CASE_INSENSITIVE_ORDER)
                ).toArray());
    }
}
