package com.androidimpact.app;

import java.io.Serializable;
import java.util.ArrayList;

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
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Recipe> getBreakfastRecipes() {
        return breakfastRecipes;
    }

    public void setBreakfastRecipes(ArrayList<Recipe> breakfastRecipes) {
        this.breakfastRecipes = breakfastRecipes;
    }

    public ArrayList<Recipe> getLunchRecipes() {
        return lunchRecipes;
    }

    public void setLunchRecipes(ArrayList<Recipe> lunchRecipes) {
        this.lunchRecipes = lunchRecipes;
    }

    public ArrayList<Recipe> getDinnerRecipes() {
        return dinnerRecipes;
    }

    public void setDinnerRecipes(ArrayList<Recipe> dinnerRecipes) {
        this.dinnerRecipes = dinnerRecipes;
    }

    public ArrayList<StoreIngredient> getBreakfastIngredients() {
        return breakfastIngredients;
    }

    public void setBreakfastIngredients(ArrayList<StoreIngredient> breakfastIngredients) {
        this.breakfastIngredients = breakfastIngredients;
    }

    public ArrayList<StoreIngredient> getLunchIngredients() {
        return lunchIngredients;
    }

    public void setLunchIngredients(ArrayList<StoreIngredient> lunchIngredients) {
        this.lunchIngredients = lunchIngredients;
    }

    public ArrayList<StoreIngredient> getDinnerIngredients() {
        return dinnerIngredients;
    }

    public void setDinnerIngredients(ArrayList<StoreIngredient> dinnerIngredients) {
        this.dinnerIngredients = dinnerIngredients;
    }

    public ArrayList<Recipe> getSnackRecipes() {
        return snackRecipes;
    }

    public void setSnackRecipes(ArrayList<Recipe> snackRecipes) {
        this.snackRecipes = snackRecipes;
    }

    public ArrayList<StoreIngredient> getSnackIngredients() {
        return snackIngredients;
    }

    public void setSnackIngredients(ArrayList<StoreIngredient> snackIngredients) {
        this.snackIngredients = snackIngredients;
    }
}
