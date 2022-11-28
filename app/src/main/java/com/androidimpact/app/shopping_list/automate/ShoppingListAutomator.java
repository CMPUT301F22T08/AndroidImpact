package com.androidimpact.app.shopping_list.automate;


import android.util.Log;

import com.androidimpact.app.ingredients.IngredientStorageController;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.meal_plan.MealPlan;
import com.androidimpact.app.meal_plan.MealPlanController;
import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.recipes.RecipeController;
import com.androidimpact.app.recipes.RecipeIngredient;
import com.androidimpact.app.shopping_list.ShopIngredient;
import com.androidimpact.app.shopping_list.ShoppingList;
import com.androidimpact.app.shopping_list.ShoppingListController;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * This class deals with shopping list automation
 * Specifically, it defines tasks that goes through the ingredients in the meal plan recipes
 * and the ingredients in ingredientStorage,
 * and generates which recipeIngredients need to be added
 *
 * @author Joshua Ji
 */
public class ShoppingListAutomator {
    // Logging stuff
    private final String TAG = "ShoppingListAutomate";

    // a reference to the Executor (defined in MainActivity - we have it here via dependency injection)
    // this lets us run automateShoppingList() in a background thread
    private final Executor executor;

    // Firebase
    FirebaseFirestore db;

    // Controllers
    MealPlanController mealPlanController;
    IngredientStorageController ingredientStorageController;
    ShoppingListController shoppingListController;

    // Actual Data
    ArrayList<ShopIngredient> recommendations;


    /**
     * Constructor for ShoppingListAutomate
     * @param ingredientStorageController {@link RecipeController RecipeController}
     * @param mealPlanController {@link MealPlanController MealPlanController}
     * @param shoppingListController {@link ShoppingListController ShoppingListController}
     * @param executor {@link Executor Executor} class for us to execute background tasks
     */
    public ShoppingListAutomator(
            IngredientStorageController ingredientStorageController,
            MealPlanController mealPlanController,
            ShoppingListController shoppingListController,
            Executor executor
    ) {
        this.db = FirebaseFirestore.getInstance();
        this.mealPlanController = mealPlanController;
        this.ingredientStorageController = ingredientStorageController;
        this.shoppingListController = shoppingListController;
        this.executor = executor;
    }

    /**
     * Asynchronously calculates the shopIngredients needed
     *
     * This runs in the background thread via the Executor
     *
     * https://developer.android.com/guide/background/threading
     */
    public void automateShoppingList(OnSuccessListener<ArrayList<ShopIngredient>> successListener, OnFailureListener errorListener) throws Exception {
        executor.execute(() -> {
            try {
                synchronousAutomateShoppingList()
                        .addOnSuccessListener(successListener)
                        .addOnFailureListener(errorListener);
            } catch (Exception e) {
                errorListener.onFailure(e);
            }
        });
    }


    /**
     * Actually calculates all the shopIngredients needed (synchronously), then stores the result inside `recommendations`
     *
     * we name it `synchronousAutomateShoppingList` because we want to make sure that
     * people know it is synchronous and blocking! in fact, if we try to run it on the MainThread,
     * we'll get an error from Android
     *
     * @return a task containing an array of all Shopingredients to add
     */
    public Task<ArrayList<ShopIngredient>> synchronousAutomateShoppingList() throws Exception {
        Log.i(TAG + ":automateShoppingList", "Start automation");

        // this is what we return (result)
        ArrayList<ShopIngredient> res = new ArrayList<>();

        // now, the mealPlan and recipe should be populated
        ArrayList<MealPlan> mealPlans = this.mealPlanController.getData();
        for (MealPlan mealPlan : mealPlans) {
            ArrayList<Recipe> recipes = getRecipes(mealPlan);
            for (Recipe recipe : recipes) {
                ArrayList<ShopIngredient> recipeRecs = Tasks.await(getRecipeRecommendations(recipe));
                // add unique shopIngredients to result
                for (ShopIngredient rec : recipeRecs) {
                    if (!res.contains(rec)) res.add(rec);
                }
            }
        }
        recommendations = res;
        return liftToTask(res);
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
        ArrayList<ShopIngredient> ret = new ArrayList<>();
        CollectionReference recipeIngredientsCollection = db.collection(recipe.getCollectionPath());

        ArrayList<RecipeIngredient> recipeIngredients = Tasks.await(fetchCollection(RecipeIngredient.class, recipeIngredientsCollection));
        ArrayList<StoreIngredient> storeIngredients = this.ingredientStorageController.getData();

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
        return liftToTask(ret);

    }

    /**
     * Abstract function for reading a collection from firestore
     *
     * Note: Tasks and continuations are really weird.
     * I personally enjoyed this blog post when learning about chaining:
     * https://firebase.blog/posts/2016/09/become-a-firebase-taskmaster-part-3_29
     */
    private <T>Task<ArrayList<T>> fetchCollection(Class<T> valueType, CollectionReference cr) throws Exception {
        QuerySnapshot snapshots = Tasks.await(cr.get());

        ArrayList<T> list = new ArrayList<>();
        int errorCount = 0;
        for (QueryDocumentSnapshot doc : snapshots) {
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
        return liftToTask(list);
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

    /**
     * This is a helper function to "lift" a value to a task
     *
     * https://developers.google.com/android/reference/com/google/android/gms/tasks/TaskCompletionSource
     * https://stackoverflow.com/q/69933562
     *
     * I don't know why this is so complicated!
     */
    private <T> Task<T> liftToTask(T val) {
        TaskCompletionSource<T> taskCompletionSource = new TaskCompletionSource<>();
        taskCompletionSource.setResult(val);
        return taskCompletionSource.getTask();
    }

    /**
     * Simple getter function for recommendations;
     * @return the shopingredient list
     */
    public ArrayList<ShopIngredient> getRecommendations() {
        return recommendations;
    }
}
