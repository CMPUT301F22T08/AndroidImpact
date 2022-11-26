package com.androidimpact.app.shopping_list.automate;


import android.os.Handler;
import android.util.Log;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;

import com.androidimpact.app.ingredients.Ingredient;
import com.androidimpact.app.ingredients.IngredientStorage;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.meal_plan.MealPlan;
import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.recipes.RecipeIngredient;
import com.androidimpact.app.shopping_list.ShopIngredient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.local.QueryResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This class deals with shopping list automation
 * Specifically, it defines tasks that goes through the ingredients in the meal plan recipes
 * and the ingredients in ingredientStorage,
 * and generates which recipeIngredients need to be added
 *
 */
public class ShoppingListAutomate {
    // Logging stuff
    private final String TAG = "ShoppingListAutomate";
    // Firestore
    FirebaseFirestore db;
    CollectionReference mealPlanCollection;
    CollectionReference recipesCollection;
    CollectionReference ingredientStorageCollection;

    // Actual Data
    ArrayList<MealPlan> mealPlan;
    ArrayList<Recipe> recipe;
    ArrayList<StoreIngredient> storeIngredients;

    /**
     * Constructor for ShoppingListAutomate
     *
     * using dependency injection (?) lo
     * @param db Firebase Firestore database instance
     * @param mealPlanCollection CollectionReference to the mealPlan
     * @param recipesCollection CollectionReference to the recipes
     */
    public ShoppingListAutomate(
            FirebaseFirestore db,
            CollectionReference mealPlanCollection,
            CollectionReference recipesCollection,
            CollectionReference ingredientStorageCollection
    ) {
        this.db = db;
        this.mealPlanCollection = mealPlanCollection;
        this.recipesCollection = recipesCollection;
        this.ingredientStorageCollection = ingredientStorageCollection;
    }

    /**
     * Actually calculates all the shopIngredients needed
     *
     * Note: this is an async function that returns a task. This might take a while!
     * @return
     */
    public Task<ArrayList<ShopIngredient>> automateShoppingList() throws Exception {
        try {
            Tasks.await(fetchTopLevelData());
            ArrayList<ShopIngredient> recommendations = new ArrayList<>();

            // now, the mealPlan and recipe should be populated
            ArrayList<Task<ArrayList<ShopIngredient>>> futures = new ArrayList<>();
            for (MealPlan mp : mealPlan) {
                ArrayList<Recipe> recipes = getRecipes(mp);
                for (Recipe recipe : recipes) {
                    futures.add(getRecipeRecommendations(recipe));
                }
            }
            // TODO: wait for futures to finish, then add them to recommendations
            // return a result wrapped in a task
            // https://developers.google.com/android/reference/com/google/android/gms/tasks/TaskCompletionSource
            // https://stackoverflow.com/q/69933562
            TaskCompletionSource<ArrayList<ShopIngredient>> taskCompletionSource = new TaskCompletionSource<>();
            taskCompletionSource.setResult(recommendations);
            return taskCompletionSource.getTask();
        } catch (Exception e) {
            Log.i(TAG, "Error automating shopping list!", e);
            throw e;
        }
    }

    /**
     * Retrieves all the top-level data we need (recipes, mealPLan) and store them inside the class.
     * Top level meaning we don't fetch the ingredients inside recipes.
     */
    private Task<Void> fetchTopLevelData() throws Exception {
        try {
            mealPlan = Tasks.await(fetchCollection(MealPlan.class, mealPlanCollection));
            recipe = Tasks.await(fetchCollection(Recipe.class, recipesCollection));
            storeIngredients = Tasks.await(fetchCollection(StoreIngredient.class, ingredientStorageCollection));
            return null;
        } catch (Exception e) {
            Log.i(TAG, "Error fetching top level data!", e);
            throw e;
        }
    }

    /**
     * Gets all the recipes from a mealPlan
     *
     * Looks through breakfast, lunch, dinner and snack recipes
     */
    private ArrayList<Recipe> getRecipes(MealPlan mp) {
        ArrayList<Recipe> ret = new ArrayList<>();
        ret.addAll(mp.getBreakfastRecipes());
        ret.addAll(mp.getLunchRecipes());
        ret.addAll(mp.getDinnerRecipes());
        ret.addAll(mp.getSnackRecipes());
        return ret;
    }

    /**
     * Given a recipe, get the recommendations by checking its ingredients with our ingredient storage
     */
    private Task<ArrayList<ShopIngredient>> getRecipeRecommendations(Recipe recipe) throws Exception {
        try {
            ArrayList<ShopIngredient> ret = new ArrayList<>();
            CollectionReference recipeIngredientsCollection = db.collection(recipe.getCollectionPath());
            ArrayList<RecipeIngredient> recipeIngredients = Tasks.await(fetchCollection(RecipeIngredient.class, recipeIngredientsCollection));

            for (RecipeIngredient recipeIngredient : recipeIngredients) {
                // check if this recipe ingredient exists inside the ingredientStorage by looping through
                // all ingredients in ingredientStorage
                boolean available = false;
                for (StoreIngredient storeIngredient : storeIngredients) {
                    // we have a hit! Check if the amounts are compatible
                    // if the units are different, we treat the ingredients as incompatible
                    ShopIngredient difference = getDifference(recipeIngredient, storeIngredient);
                    if (difference.getAmount() > 0) {
                        available = true;
                        ret.add(difference);
                        break;
                    }
                }
                if (!available) {
                    // recipe is not found in ingredient storage, add it
                    // String id, String description, float amount, String unit, String category
                    ret.add(new ShopIngredient(
                            UUID.randomUUID().toString(),
                            recipeIngredient.getDescription(),
                            recipeIngredient.getAmount(),
                            recipeIngredient.getUnit(),
                            recipeIngredient.getCategory()
                    ));
                }
            }
            // return a result wrapped in a task
            // https://developers.google.com/android/reference/com/google/android/gms/tasks/TaskCompletionSource
            // https://stackoverflow.com/q/69933562
            TaskCompletionSource<ArrayList<ShopIngredient>> taskCompletionSource = new TaskCompletionSource<>();
            taskCompletionSource.setResult(ret);
            return taskCompletionSource.getTask();
        } catch (Exception e) {
            Log.i(TAG, "Error fetching recipe recs for recipe " + recipe.getTitle(), e);
            throw e;
        }

    }

    /**
     * Abstract function for reading a collection from firestore
     *
     * Note: Tasks and continuations are really weird.
     * I personally enjoyed this blog post when learning about chaining
     */
    private <T>Task<ArrayList<T>> fetchCollection(Class<T> valueType, CollectionReference cr) {
        return cr.get()
                .continueWith((Continuation<QuerySnapshot, ArrayList<T>>) task -> {
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    ArrayList<T> list = new ArrayList<>();
                    int errorCount = 0;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            T data = doc.toObject(valueType);
                            list.add(data);
                        } catch (Exception e) {
                            errorCount += 1;
                        }
                    }
                    if (errorCount > 0) {
                        Log.i(TAG, "Fatal: " + errorCount + " errors when reading items of class " + valueType.getCanonicalName() + "!");
                    }
                    return list;
                });
    }

    /**
     * Get the difference between the RecipeIngredient we want, and a StoreIngredient we have
     * This "difference" is basically just calculating the amount difference, IF THE UNITS ARE THE SAME
     * it is returned as a new ShopIngredient one can immediately treat as a recommendation
     *
     * If the units are not the same, treat the ingredients as if they are incompatible
     *
     * NOTE: we overwrite the category with the RecipeIngredient category
     */
    private ShopIngredient getDifference(RecipeIngredient watchawatchawant, StoreIngredient watchahave) {
        // unequal name - incompatible!
        boolean nameEqual = Objects.equals(watchawatchawant.getDescription(), watchahave.getDescription());
        boolean unitEqual = Objects.equals(watchawatchawant.getUnit(), watchahave.getUnit());

        if (!nameEqual || !unitEqual) {
            return new ShopIngredient(
                    watchawatchawant.getId(),
                    watchawatchawant.getDescription(),
                    watchawatchawant.getAmount(),
                    watchawatchawant.getUnit(),
                    watchawatchawant.getCategory()
            );
        }

        // now that units and names are equal, let's calculate the amount
        float newAmount = watchawatchawant.getAmount() - watchahave.getAmount();
        return new ShopIngredient(
                UUID.randomUUID().toString(),
                watchawatchawant.getDescription(),
                newAmount,
                watchawatchawant.getUnit(),
                watchawatchawant.getCategory()
        );
    }
}
