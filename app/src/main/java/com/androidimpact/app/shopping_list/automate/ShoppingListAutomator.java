package com.androidimpact.app.shopping_list.automate;


import android.util.Log;

import com.androidimpact.app.ingredients.Ingredient;
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
        SmartShoppingRecs res = new SmartShoppingRecs();
        String[] mealTypes = {"breakfast", "lunch", "dinner", "snacks"};

        // now, the mealPlan and recipe should be populated
        ArrayList<MealPlan> mealPlans = this.mealPlanController.getData();
        for (MealPlan mealPlan : mealPlans) {
            for (String mealType: mealTypes)
            {
                ArrayList<Double> recipeServings = mealPlan.getRecipeServings(mealType);
                ArrayList<Recipe> recipes = mealPlan.getRecipes(mealType);

                for(int i = 0; i < recipes.size(); ++i)
                {
                    Recipe recipe = recipes.get(i);
                    Double recipeServing = recipeServings.get(i);
                    ArrayList<ShopIngredient> recipeRecs = Tasks.await(getRecipeRecommendations(recipe, recipeServing));
                    // add unique shopIngredients to result
                    for (ShopIngredient rec : recipeRecs) {
                        res.addMerge(rec);
                    }
                }
            }

        }
        recommendations = res.getData();
        return liftToTask(res.getData());
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
    private Task<ArrayList<ShopIngredient>> getRecipeRecommendations(Recipe recipe, Double servings) throws Exception {
        ArrayList<ShopIngredient> recommendations = new ArrayList<>();
        CollectionReference recipeIngredientsCollection = db.collection(recipe.getCollectionPath());

        ArrayList<RecipeIngredient> recipeIngredients = Tasks.await(fetchCollection(RecipeIngredient.class, recipeIngredientsCollection));
        ArrayList<StoreIngredient> storeIngredients = this.ingredientStorageController.getData();
//        ArrayList<Double> recipeSercings = mealPlanController

//        double servings = ;


        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            // check if this recipe ingredient exists inside the ingredientStorage by looping through
            // all ingredients in ingredientStorage
            boolean available = false;
            Log.i("recipe from mp", recipeIngredient.getDescription());

            for (StoreIngredient storeIngredient : storeIngredients) {
                // we have a hit! Check if the amounts are compatible
                // if the units are different, we treat the ingredients as incompatible
                Log.i("finding ingredient", storeIngredient.getDescription() + " "+ String.valueOf(servings));



                boolean nameEqual = Objects.equals(recipeIngredient.getDescription(), storeIngredient.getDescription());
                boolean unitEqual = Objects.equals(recipeIngredient.getUnit(), storeIngredient.getUnit());
                if (nameEqual && unitEqual)
                {

                    ShopIngredient difference = getDifference(recipeIngredient, storeIngredient, servings);
                    if (difference.getAmount() > 0) {

                        available = true;
                        recommendations.add(difference);
                        break;

                    }
                    else if (difference.getAmount() <= 0)
                    {
                        available = true;
                        break;
                    }

                }
            }
            if (!available) {
                // recipe is not found in ingredient storage, add it
                // String id, String description, float amount, String unit, String category


                float amountNeeded = 0;
                try{
                    amountNeeded = recipeIngredient.getAmount() * servings.floatValue();
                }
                catch(Exception e)
                {
                    Log.i("amount needed too big", recipeIngredient.getDescription());
                    amountNeeded = Float.MAX_VALUE;
                }

                Log.i("ingredient added", recipeIngredient.getDescription());

                recommendations.add(new ShopIngredient(
                        UUID.randomUUID().toString(),
                        recipeIngredient.getDescription(),
                        amountNeeded,
                        recipeIngredient.getUnit(),
                        recipeIngredient.getCategory()
                ));
            }
        }

        // now, we have our recommendations.
        // we need to filter one last time with the current recipe recommendations, to avoid redundancies
        // this is kinda the same loop as before but i don't care LOL
        ArrayList<ShopIngredient> returnRecommendations = new ArrayList<>();
        for (ShopIngredient currentRec : recommendations) {
            boolean added = false;
            for (ShopIngredient currentShopItem : this.shoppingListController.getData()) {
                // check if this shopItem already exists inside our shop item
                // this requires an exhaustive check on all attributes
                boolean sameDescription = Objects.equals(currentRec.getDescription(), currentShopItem.getDescription());
                boolean sameUnit = Objects.equals(currentRec.getUnit(), currentShopItem.getUnit());
                boolean sameAmount = currentRec.getAmount() == currentShopItem.getAmount();
                boolean category = Objects.equals(currentRec.getCategory(), currentShopItem.getCategory());
                if (sameDescription && sameUnit && sameAmount && category) {
                    added = true;
                    break;
                }
            }
            if (!added) {
                returnRecommendations.add(new ShopIngredient(
                        UUID.randomUUID().toString(),
                        currentRec.getDescription(),
                        currentRec.getAmount(),
                        currentRec.getUnit(),
                        currentRec.getCategory()
                ));
            }
        }

        // return a result wrapped in a task
        return liftToTask(returnRecommendations);
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
     * Get the difference between an ingredient we want (to have in a shopping list), and a ingredient we have
     * This "difference" is basically just calculating the amount difference, IF THE UNITS ARE THE SAME
     * it is returned as a new ShopIngredient one can immediately treat as a recommendation
     *
     * If the units are not the same, treat the ingredients as if they are incompatible
     *
     * NOTE: we overwrite the category with the RecipeIngredient category
     */
    private ShopIngredient getDifference(Ingredient watchawatchawant, Ingredient watchahave, double servings) {
        // unequal name - incompatible!
        boolean nameEqual = Objects.equals(watchawatchawant.getDescription(), watchahave.getDescription());
        boolean unitEqual = Objects.equals(watchawatchawant.getUnit(), watchahave.getUnit());

        float amountNeeded = 0;
        try{
            amountNeeded = watchawatchawant.getAmount() * (float)servings;
        }
        catch(Exception e)
        {
            Log.i("amount needed too big", watchawatchawant.getDescription());
            amountNeeded = Float.MAX_VALUE;
        }
        if (!nameEqual || !unitEqual) {
            return new ShopIngredient(
                    UUID.randomUUID().toString(),
                    watchawatchawant.getDescription(),
                    amountNeeded,
                    watchawatchawant.getUnit(),
                    watchawatchawant.getCategory()
            );
        }

        // now that units and names are equal, let's calculate the amount
        float newAmount = amountNeeded - watchahave.getAmount();
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
