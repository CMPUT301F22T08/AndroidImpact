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

    /**
     *  Constructor for meal plan
     * @param date (Date)
     * @param sortString (String)
     */
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

    /**
     * initialises hashmap
     */
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

    /**
     *  getter method for size
     * @return size (int)
     */
    public int size() {
        int size = mealRecipeMap.values().stream().mapToInt(ArrayList::size).sum();
        size += mealIngredientMap.values().stream().mapToInt(ArrayList::size).sum();
        return size;
    }

    /**
     *  getter method for date
     * @return (Date)
     */
    public String getDate() {
        return date;
    }

    /**
     * setter method for date
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *  getter method for sort string
     * @return (String)
     */
    public String getSortString() {
        return this.sortString;
    }

    /**
     * getter method for ArrayList<Recipe>
     * @param key (String)
     * @return (ArrayList<Recipe>)
     */
    public ArrayList<Recipe> getRecipes(String key) {return this.mealRecipeMap.get(key);}

    /**
     * Getter method for getRecipeIds
     * @param key (String)
     * @return (ArrayList<String>)
     */
    public ArrayList<String> getRecipeIds(String key) {
        ArrayList<Recipe> recipesInMealPlan = this.getRecipes(key);
        ArrayList<String> recipeIdsInMealPlan = new ArrayList<>();
        for(int i = 0; i < recipesInMealPlan.size(); i++) {
            recipeIdsInMealPlan.add(recipesInMealPlan.get(i).getId());
        }
        return recipeIdsInMealPlan;
    }

    /**
     *
     * @param key (String)
     * @return (ArrayList<String>)
     */
    public ArrayList<String> getRecipeTitles(String key) {
        ArrayList<Recipe> recipesInMealPlan = this.getRecipes(key);
        ArrayList<String> recipeIdsInMealPlan = new ArrayList<>();
        for(int i = 0; i < recipesInMealPlan.size(); i++) {
            recipeIdsInMealPlan.add(recipesInMealPlan.get(i).getTitle());
        }
        return recipeIdsInMealPlan;
    }

    /**
     * getter method for ingredients
     * @param key (String)
     * @return (ArrayList<StoreIngredient>)
     */
    public ArrayList<StoreIngredient> getIngredients(String key) {return this.mealIngredientMap.get(key);}

    /**
     * getter method for ingredient ids
     * @param key (String)
     * @return ArrayList<String>
     */
    public ArrayList<String> getIngredientIds(String key) {
        ArrayList<StoreIngredient> ingredientsInMealPlan = this.getIngredients(key);
        ArrayList<String> ingredientIdsInMealPlan = new ArrayList<>();
        for(int i = 0; i < ingredientsInMealPlan.size(); i++) {
            ingredientIdsInMealPlan.add(ingredientsInMealPlan.get(i).getId());
        }
        return ingredientIdsInMealPlan;
    }

    /**
     * getter method for ingredient titles
     * @param key
     * @return (ArrayList<String>)
     */
    public ArrayList<String> getIngredientTitles(String key) {
        ArrayList<StoreIngredient> ingredientsInMealPlan = this.getIngredients(key);
        ArrayList<String> ingredientIdsInMealPlan = new ArrayList<>();
        for(int i = 0; i < ingredientsInMealPlan.size(); i++) {
            ingredientIdsInMealPlan.add(ingredientsInMealPlan.get(i).getDescription());
        }
        return ingredientIdsInMealPlan;
    }

    /**
     *  getter method for recipe servings
     * @param key (String)
     * @return ArrayList<Double>
     */
    public ArrayList<Double> getRecipeServings(String key) {return this.mealRecipeServingsMap.get(key);}

    /**
     *  getter for ingredient servings
     * @param key (String)
     * @return (ArrayList<Double>)
     */
    public ArrayList<Double> getIngredientServings(String key) {return this.mealIngredientServingsMap.get(key);}

    /**
     * this method adds recipe to meal plan
     * @param key (String)
     * @param recipeKey (String)
     * @param recipeServings (Double)
     * @param recipeList1  (RecipeList)
     */
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

    /**
     * this method adds ingredient to meal plan
     * @param key (String)
     * @param ingredientKey (String)
     * @param ingredientServings (double)
     * @param ingredients (ArrayList<StoreIngredient>)
     */
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

    /**
     * getter for breakfast recipes
     * @return (ArrayList<Recipe>)
     */
    public ArrayList<Recipe> getBreakfastRecipes() {
        return breakfastRecipes;
    }

    /**
     * getter for lunch recipes
     * @return (ArrayList<Recipe>)
     */
    public ArrayList<Recipe> getLunchRecipes() {
        return lunchRecipes;
    }

    /**
     * getter for dinner recipes
     * @return (ArrayList<Recipe>)
     */
    public ArrayList<Recipe> getDinnerRecipes() {
        return dinnerRecipes;
    }

    /**
     * getter for snack recipes
     * @return (ArrayList<Recipe>)
     */
    public ArrayList<Recipe> getSnackRecipes() {
        return snackRecipes;
    }
}
