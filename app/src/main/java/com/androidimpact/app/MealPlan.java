package com.androidimpact.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * MealPlan class
 * <br>
 * This class defines a meal plan for a given day.
 * @version 1.0
 * @author Aneeljyot Alagh
 */
public class MealPlan implements Serializable {
    private String date;
    private ArrayList<Recipe> breakfastRecipes, lunchRecipes, dinnerRecipes, snackRecipes;
    private ArrayList<StoreIngredient> breakfastIngredients, lunchIngredients, dinnerIngredients, snackIngredients;
    private HashMap<String, ArrayList<Recipe>> mealRecipeMap;
    private HashMap<String, ArrayList<StoreIngredient>> mealIngredientMap;

    public MealPlan(String date) {
        this.date = date;
        this.breakfastRecipes = new ArrayList<>();
        this.lunchRecipes = new ArrayList<>();
        this.dinnerRecipes = new ArrayList<>();
        this.breakfastIngredients = new ArrayList<>();
        this.lunchIngredients = new ArrayList<>();
        this.dinnerIngredients = new ArrayList<>();
        this.snackRecipes = new ArrayList<>();
        this.snackIngredients = new ArrayList<>();

        this.putInHashMaps();
    }

    public MealPlan(String date,
                    ArrayList<Recipe> breakfastRecipes, ArrayList<Recipe> lunchRecipes, ArrayList<Recipe> dinnerRecipes, ArrayList<Recipe> snackRecipes,
                    ArrayList<StoreIngredient> breakfastIngredients, ArrayList<StoreIngredient> lunchIngredients, ArrayList<StoreIngredient> dinnerIngredients, ArrayList<StoreIngredient> snackIngredients
    ) {
        this.date = date;
        this.breakfastRecipes = breakfastRecipes;
        this.lunchRecipes = lunchRecipes;
        this.dinnerRecipes = dinnerRecipes;
        this.breakfastIngredients = breakfastIngredients;
        this.lunchIngredients = lunchIngredients;
        this.dinnerIngredients = dinnerIngredients;
        this.snackRecipes = snackRecipes;
        this.snackIngredients = snackIngredients;

        this.putInHashMaps();
    }

    private void putInHashMaps() {
        this.mealRecipeMap = new HashMap<>();
        this.mealIngredientMap = new HashMap<>();

        this.mealRecipeMap.put("breakfast", this.breakfastRecipes);
        this.mealRecipeMap.put("lunch", this.lunchRecipes);
        this.mealRecipeMap.put("dinner", this.dinnerRecipes);
        this.mealRecipeMap.put("snacks", this.snackRecipes);
        this.mealIngredientMap.put("breakfast", this.breakfastIngredients);
        this.mealIngredientMap.put("lunch", this.lunchIngredients);
        this.mealIngredientMap.put("dinner", this.dinnerIngredients);
        this.mealIngredientMap.put("snacks", this.snackIngredients);
    }

    public int size() {
        int size = mealRecipeMap.values().stream().mapToInt(ArrayList::size).sum();
        size += mealIngredientMap.values().stream().mapToInt(ArrayList::size).sum();
        return size;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Recipe> getRecipes(String key) {return this.mealRecipeMap.get(key);}

    public ArrayList<StoreIngredient> getIngredients(String key) {return this.mealIngredientMap.get(key);}

    public void setBreakfastRecipes(ArrayList<Recipe> breakfastRecipes) {
        this.breakfastRecipes = breakfastRecipes;
    }

    public void setLunchRecipes(ArrayList<Recipe> lunchRecipes) {
        this.lunchRecipes = lunchRecipes;
    }

    public void setDinnerRecipes(ArrayList<Recipe> dinnerRecipes) {
        this.dinnerRecipes = dinnerRecipes;
    }

    public void setBreakfastIngredients(ArrayList<StoreIngredient> breakfastIngredients) {
        this.breakfastIngredients = breakfastIngredients;
    }

    public void setLunchIngredients(ArrayList<StoreIngredient> lunchIngredients) {
        this.lunchIngredients = lunchIngredients;
    }

    public void setDinnerIngredients(ArrayList<StoreIngredient> dinnerIngredients) {
        this.dinnerIngredients = dinnerIngredients;
    }

    public void setSnackRecipes(ArrayList<Recipe> snackRecipes) {
        this.snackRecipes = snackRecipes;
    }

    public void setSnackIngredients(ArrayList<StoreIngredient> snackIngredients) {
        this.snackIngredients = snackIngredients;
    }
}
