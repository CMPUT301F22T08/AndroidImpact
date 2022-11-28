package com.androidimpact.app.meal_plan;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.ingredients.IngredientStorageController;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.recipes.RecipeController;
import com.androidimpact.app.recipes.RecipeList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * Controller Class for MealPlanList
 * Acts as a wrapper around MealPlanList and implements functionality to back up the storage on FireStore
 * @version 1.0
 * @author Aneeljyot Alagh
 */
public class MealPlanController {
    final String TAG = "MealPlanController";
    final String firestorePath = "meal-plan";

    private Context context;
    private CollectionReference mealPlanCollection, recipeCollection, ingredientCollection;
    private MealPlanList mealPlanList;
    RecipeList recipeList;
    ArrayList<StoreIngredient> ingredientStorageData;

    /**
     * Constructor: Creates an empty MealPlanList class and populates it with data from FireStore
     * @param context (Context) The current context, used to push success/failure SnackBars to the screen.
     * @param recipeController (RecipeController) The current controller handling recipes
     * @param ingredientStorageController (IngredientStorageController) The current controller handling recipes
     */
    public MealPlanController(Context context, String dataPath, RecipeController recipeController, IngredientStorageController ingredientStorageController) {
        this.context = context;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mealPlanCollection = db.document(dataPath).collection(firestorePath);
        recipeCollection = db.document(dataPath).collection("recipes");
        ingredientCollection = db.document(dataPath).collection("ingredientStorage");
        mealPlanList = new MealPlanList(new ArrayList<>());
        this.recipeList = new RecipeList(recipeController.getData());
        this.ingredientStorageData = ingredientStorageController.getData();
    }

    /**
     * Creates a SnackBar that pup-up on the screen
     * @param s (String) - The text to be shown in the SnackBar
     */
    private void pushSnackBarToContext(String s) {
        Snackbar.make(((MainActivity)context).findViewById(R.id.nav_fragment), s, Snackbar.LENGTH_LONG)
                .setAction("OK", (v)->{}).show();
    }

    /**
     * Add a snapshot listener to the FireStore collection that repopulates the meal plan list when changes are detected.
     * Notifies the current ViewAdapter that the data may have changed.
     * @param mealPlanListAdapter (MealPlanListAdapter) The adapter to be updated when the latest data is pulled from firestore
     */
    public void refresh(MealPlanListAdapter mealPlanListAdapter){
        mealPlanCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            // Clear the old list
            mealPlanList.clear();

            if (queryDocumentSnapshots == null) {
                return;
            }
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Map<String, Object> data = doc.getData();
                MealPlan mealPlanToAdd = new MealPlan(doc.getId(), (String) data.get("sortString"));

                String[] keys = {"breakfast", "lunch", "dinner", "snacks"};
                for(String key: keys) {
                    ArrayList<String> recipeIdList = (ArrayList<String>) data.get(key + "Recipes");
                    ArrayList<Double> recipeServingsList = (ArrayList<Double>) data.get(key + "RecipesServings");
                    if(recipeIdList != null) {
                        for(int i = 0; i < recipeIdList.size(); i++) {
                            mealPlanToAdd.addMealItemRecipe(
                                    key,
                                    recipeIdList.get(i),
                                    recipeServingsList.get(i),
                                    this.recipeList
                            );
                        }
                    }

                    ArrayList<String> ingredientIdList = (ArrayList<String>) data.get(key + "Ingredients");
                    ArrayList<Double> ingredientServingsList = (ArrayList<Double>) data.get(key + "IngredientsServings");
                    if(ingredientIdList != null) {
                        for(int i = 0; i < ingredientIdList.size(); i++) {
                            mealPlanToAdd.addMealItemIngredient(
                                    key,
                                    ingredientIdList.get(i),
                                    ingredientServingsList.get(i),
                                    this.ingredientStorageData
                            );
                        }
                    }
                }
                mealPlanList.add(mealPlanToAdd); // Adding the recipe attributes from FireStore
            }

            Log.i(TAG, "Snapshot listener: Added " + mealPlanList.size() + " elements");
            for (MealPlan i : mealPlanList.getData()) {
                Log.i(TAG, "Snapshot listener: Added " + i.getDate() + " to elements");
            }
            mealPlanListAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
        });
    }

    /**
     * Adds snapshot listeners to the given list adapter, whenever the Firestore collections of meal plan, recipes, and/or ingredients have changes.
     * @param mealPlanListAdapter (MealPlanListAdapter) The adapter to be updated when the latest data is pulled from firestore
     */
    public void addDataUpdateSnapshotListener(MealPlanListAdapter mealPlanListAdapter) {
        refresh(mealPlanListAdapter);
        recipeCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            refresh(mealPlanListAdapter);
        });

        ingredientCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            refresh(mealPlanListAdapter);
        });
    }

    /**
     * Deletes the object in list at the parameter position
     * @param position (int) The position at which to delete the item
     * @param view (View) The view to add Snackbars to
     * @param mealPlanListAdapter (MealPlanListAdapter) The list adapter that needs to be updated when object is deleted
     */
    public void delete(int position, View view, MealPlanListAdapter mealPlanListAdapter) {
        MealPlan deletedMealPlan = this.mealPlanList.get(position);
        String description = deletedMealPlan.getDate();

        OnSuccessListener sl = o -> {
            Log.d(TAG, description + " has been deleted successfully!");
            Snackbar.make(view, "Deleted meal plan for " + description, Snackbar.LENGTH_LONG)
                    .setAction("Ok", view1 -> {})
                    .show();
        };
        OnFailureListener fl = e -> {
            Log.d(TAG, description + " could not be deleted!" + e);
            Snackbar.make(view, "Could not delete meal plan for " + description + "!", Snackbar.LENGTH_LONG)
                    .setAction("Ok", view1 -> {})
                    .show();
        };

        mealPlanCollection.document(description)
                .delete()
                .addOnSuccessListener(sl)
                .addOnFailureListener(fl);

        mealPlanListAdapter.notifyDataSetChanged();
    }

    /**
     * Returns the meal plan at the given position, if it exists.
     * @param position (int) the position of the item to return
     * @return meal plan at the given position
     * @throws IndexOutOfBoundsException if given position is outside the bounds of the meal plan list
     */
    public MealPlan get(int position) throws IndexOutOfBoundsException {
        if (position < mealPlanList.size() && position>=0){
            return mealPlanList.get(position);
        }
        throw new IndexOutOfBoundsException("Please enter an invalid index");
    }

    /**
     * Sorts the data in the meal plan list
     */
    public void sortData(){
        mealPlanList.sortByChoice();
    }

    /**
     * Returns the data in the meal plan list
     * @return meal plans data (ArrayList&lt;MealPlan&gt;)
     */
    public ArrayList<MealPlan> getData(){
        return mealPlanList.getData();
    }

    /**
     * Returns the number of items in the meal plan list
     * @return the size of meal plan list
     */
    public int size(){
        return mealPlanList.size();
    }
}
