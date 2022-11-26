package com.androidimpact.app.meal_plan;

import android.util.Log;

import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.recipes.RecipeList;
import com.androidimpact.app.ingredients.StoreIngredient;

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
    private String sortString;

    private ArrayList<Recipe> breakfastRecipes, lunchRecipes, dinnerRecipes, snackRecipes;
    private ArrayList<StoreIngredient> breakfastIngredients, lunchIngredients, dinnerIngredients, snackIngredients;
    private ArrayList<Double> breakfastRecipes_Servings, lunchRecipes_Servings, dinnerRecipes_Servings, snackRecipes_Servings;
    private ArrayList<Double> breakfastIngredients_Servings, lunchIngredients_Servings, dinnerIngredients_Servings, snackIngredients_Servings;

    private HashMap<String, ArrayList<Recipe>> mealRecipeMap;
    private HashMap<String, ArrayList<StoreIngredient>> mealIngredientMap;
    private HashMap<String, ArrayList<Double>> mealRecipeServingsMap;
    private HashMap<String, ArrayList<Double>> mealIngredientServingsMap;

    public MealPlan(String date, String sortString) {
        this.date = date;
        this.sortString = sortString;
        this.breakfastRecipes = new ArrayList<>();
        this.lunchRecipes = new ArrayList<>();
        this.dinnerRecipes = new ArrayList<>();
        this.breakfastIngredients = new ArrayList<>();
        this.lunchIngredients = new ArrayList<>();
        this.dinnerIngredients = new ArrayList<>();
        this.snackRecipes = new ArrayList<>();
        this.snackIngredients = new ArrayList<>();

        this.breakfastRecipes_Servings = new ArrayList<>();
        this.lunchRecipes_Servings = new ArrayList<>();
        this.dinnerRecipes_Servings = new ArrayList<>();
        this.snackRecipes_Servings = new ArrayList<>();
        this.breakfastIngredients_Servings = new ArrayList<>();
        this.lunchIngredients_Servings = new ArrayList<>();
        this.dinnerIngredients_Servings = new ArrayList<>();
        this.snackIngredients_Servings = new ArrayList<>();


        this.putInHashMaps();
    }

    /*public MealPlan(String date,
                    ArrayList<Recipe> breakfastRecipes, ArrayList<Recipe> lunchRecipes, ArrayList<Recipe> dinnerRecipes, ArrayList<Recipe> snackRecipes,
                    ArrayList<StoreIngredient> breakfastIngredients, ArrayList<StoreIngredient> lunchIngredients, ArrayList<StoreIngredient> dinnerIngredients, ArrayList<StoreIngredient> snackIngredients,
                    ArrayList<Float> breakfastRecipes_Servings, ArrayList<Float> lunchRecipes_Servings, ArrayList<Float> dinnerRecipes_Servings, ArrayList<Float> snackRecipes_Servings,
                    ArrayList<Float> breakfastIngredients_Servings, ArrayList<Float> lunchIngredients_Servings, ArrayList<Float> dinnerIngredients_Servings, ArrayList<Float> snackIngredients_Servings

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

        this.breakfastRecipes_Servings = breakfastRecipes_Servings;
        this.lunchRecipes_Servings = lunchRecipes_Servings;
        this.dinnerRecipes_Servings = dinnerRecipes_Servings;
        this.snackRecipes_Servings = snackRecipes_Servings;
        this.breakfastIngredients_Servings = breakfastIngredients_Servings;
        this.lunchIngredients_Servings = lunchIngredients_Servings;
        this.dinnerIngredients_Servings = dinnerIngredients_Servings;
        this.snackIngredients_Servings = snackIngredients_Servings;


        this.putInHashMaps();
    }*/

    private void putInHashMaps() {
        this.mealRecipeMap = new HashMap<>();
        this.mealIngredientMap = new HashMap<>();
        this.mealRecipeServingsMap = new HashMap<>();
        this.mealIngredientServingsMap = new HashMap<>();

        this.mealRecipeMap.put("breakfast", this.breakfastRecipes);
        this.mealRecipeMap.put("lunch", this.lunchRecipes);
        this.mealRecipeMap.put("dinner", this.dinnerRecipes);
        this.mealRecipeMap.put("snacks", this.snackRecipes);
        this.mealIngredientMap.put("breakfast", this.breakfastIngredients);
        this.mealIngredientMap.put("lunch", this.lunchIngredients);
        this.mealIngredientMap.put("dinner", this.dinnerIngredients);
        this.mealIngredientMap.put("snacks", this.snackIngredients);

        this.mealRecipeServingsMap.put("breakfast", this.breakfastRecipes_Servings);
        this.mealRecipeServingsMap.put("lunch", this.lunchRecipes_Servings);
        this.mealRecipeServingsMap.put("dinner", this.dinnerRecipes_Servings);
        this.mealRecipeServingsMap.put("snacks", this.snackRecipes_Servings);
        this.mealIngredientServingsMap.put("breakfast", this.breakfastIngredients_Servings);
        this.mealIngredientServingsMap.put("lunch", this.lunchIngredients_Servings);
        this.mealIngredientServingsMap.put("dinner", this.dinnerIngredients_Servings);
        this.mealIngredientServingsMap.put("snacks", this.snackIngredients_Servings);
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

    public String getSortString() {
        return this.sortString;
    }

    public ArrayList<Recipe> getRecipes(String key) {return this.mealRecipeMap.get(key);}

    public ArrayList<StoreIngredient> getIngredients(String key) {return this.mealIngredientMap.get(key);}

    public ArrayList<Double> getRecipeServings(String key) {return this.mealRecipeServingsMap.get(key);}

    public ArrayList<Double> getIngredientServings(String key) {return this.mealIngredientServingsMap.get(key);}

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

    public void addMealItemRecipe(String key, String recipeKey, double recipeServings, RecipeList recipeList1) {
        ArrayList<Recipe> currentRecipes = this.mealRecipeMap.get(key);
        ArrayList<Double> currentRecipeServings = this.mealRecipeServingsMap.get(key);
        Log.i("data data", recipeList1.getData().toString());
        recipeList1.getData().forEach(a -> {
            if(a.getId().equals(recipeKey)) {
                currentRecipes.add(a);
                currentRecipeServings.add(recipeServings);
                Log.i("added recipe", "bogo - recip");
            }
        });
    }

    public void addMealItemIngredient(String key, String ingredientKey, double ingredientServings, ArrayList<StoreIngredient> ingredients) {
        ArrayList<StoreIngredient> currentIngredients = this.mealIngredientMap.get(key);
        ArrayList<Double> currentIngredientServings = this.mealIngredientServingsMap.get(key);
        ingredients.forEach(a -> {
            if(a.getId().equals(ingredientKey)) {
                currentIngredients.add(a);
                currentIngredientServings.add(ingredientServings);
                Log.i("added ingredient", "bogo - ingr");
            }
        });
    }

    public ArrayList<Recipe> getBreakfastRecipes() {
        return breakfastRecipes;
    }

    public ArrayList<Recipe> getLunchRecipes() {
        return lunchRecipes;
    }

    public ArrayList<Recipe> getDinnerRecipes() {
        return dinnerRecipes;
    }

    public ArrayList<Recipe> getSnackRecipes() {
        return snackRecipes;
    }
}
