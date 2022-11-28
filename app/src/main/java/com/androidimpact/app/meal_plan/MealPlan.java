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

    public ArrayList<String> getRecipeIds(String key) {
        ArrayList<Recipe> recipesInMealPlan = this.getRecipes(key);
        ArrayList<String> recipeIdsInMealPlan = new ArrayList<>();
        for(int i = 0; i < recipesInMealPlan.size(); i++) {
            recipeIdsInMealPlan.add(recipesInMealPlan.get(i).getId());
        }
        return recipeIdsInMealPlan;
    }

    public ArrayList<String> getRecipeTitles(String key) {
        ArrayList<Recipe> recipesInMealPlan = this.getRecipes(key);
        ArrayList<String> recipeIdsInMealPlan = new ArrayList<>();
        for(int i = 0; i < recipesInMealPlan.size(); i++) {
            recipeIdsInMealPlan.add(recipesInMealPlan.get(i).getTitle());
        }
        return recipeIdsInMealPlan;
    }

    public ArrayList<StoreIngredient> getIngredients(String key) {return this.mealIngredientMap.get(key);}

    public ArrayList<String> getIngredientIds(String key) {
        ArrayList<StoreIngredient> ingredientsInMealPlan = this.getIngredients(key);
        ArrayList<String> ingredientIdsInMealPlan = new ArrayList<>();
        for(int i = 0; i < ingredientsInMealPlan.size(); i++) {
            ingredientIdsInMealPlan.add(ingredientsInMealPlan.get(i).getId());
        }
        return ingredientIdsInMealPlan;
    }

    public ArrayList<String> getIngredientTitles(String key) {
        ArrayList<StoreIngredient> ingredientsInMealPlan = this.getIngredients(key);
        ArrayList<String> ingredientIdsInMealPlan = new ArrayList<>();
        for(int i = 0; i < ingredientsInMealPlan.size(); i++) {
            ingredientIdsInMealPlan.add(ingredientsInMealPlan.get(i).getDescription());
        }
        return ingredientIdsInMealPlan;
    }

    public ArrayList<Double> getRecipeServings(String key) {return this.mealRecipeServingsMap.get(key);}

    public ArrayList<Double> getIngredientServings(String key) {return this.mealIngredientServingsMap.get(key);}

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
