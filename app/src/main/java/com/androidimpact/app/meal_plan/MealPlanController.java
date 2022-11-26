package com.androidimpact.app.meal_plan;

import android.content.Context;
import android.util.Log;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.ingredients.IngredientStorageController;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.recipes.RecipeController;
import com.androidimpact.app.recipes.RecipeList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MealPlanController {
    final String TAG = "MealPlanController";
    final String firestorePath = "meal-plan";

    private Context context;
    private FirebaseFirestore db;
    private CollectionReference mealPlanCollection;
    private MealPlanList mealPlanList;
    RecipeList recipeList;
    ArrayList<StoreIngredient> ingredientStorageData;

    public MealPlanController(Context context, RecipeController recipeController, IngredientStorageController ingredientStorageController) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        mealPlanCollection = db.collection(firestorePath);
        mealPlanList = new MealPlanList(new ArrayList<>());
        this.recipeList = new RecipeList(recipeController.getData());
        this.ingredientStorageData = ingredientStorageController.getData();
    }

    private void pushSnackBarToContext(String s) {
        Snackbar.make(((MainActivity)context).findViewById(R.id.nav_fragment), s, Snackbar.LENGTH_LONG)
                .setAction("OK", (v)->{}).show();
    }

    public void addDataUpdateSnapshotListener(MealPlanListAdapter mealPlanListAdapter){
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

    public void addEdit(Recipe recipe){
        /*// Adds if id is null else edits
        String id = recipe.getId();
        if (id == null){
            UUID uuid = UUID.randomUUID();
            id = uuid.toString();
            recipe.setID(id);
        }
        recipeCollection.document(id).set(recipe);*/
    }


    public void delete(int position) {

       /* // Get the swiped item at a particular position.
        Recipe deletedRecipe = recipeList.get(position);
        String title = deletedRecipe.getTitle();
        String id = deletedRecipe.getId();
        String photo = deletedRecipe.getPhoto();

        List<Task<?>> futures = new ArrayList<>();

        // Delete all the ingredients associated with the Recipe
        CollectionReference ingredients = db.collection(deletedRecipe.getCollectionPath());
        ingredients.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                futures.add(ingredients.document(doc.getId()).delete());
            }
        });

        // Delete the photo associated with the Recipe
        if (photo != null) {
            futures.add(photoStorage.child("images/" + photo).delete()
                    .addOnSuccessListener(aVoid -> Log.i(TAG, "Successfully deleted image from recipe: " + deletedRecipe.getTitle())));
        }

        // Delete the Recipe
        futures.add(recipeCollection.document(id)
                .delete()
                .addOnSuccessListener(unused -> Log.i(TAG, "Successfully deleted recipeDocument: " + deletedRecipe.getTitle())));

        Tasks.whenAll(futures)
                .addOnSuccessListener(o -> {
                    Log.d(TAG, title + " has been deleted successfully!");
                    pushSnackBarToContext("Deleted " + title);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, title + " could not be deleted!" + e);
                    pushSnackBarToContext("Could not delete " + title);
                });*/
    }


    public MealPlan get(int position) throws IndexOutOfBoundsException {
        if (position < mealPlanList.size() && position>=0){
            return mealPlanList.get(position);
        }
        throw new IndexOutOfBoundsException("Please enter an invalid index");
    }

    public void sortData(){
        mealPlanList.sortByChoice();
    }

    public ArrayList<MealPlan> getData(){
        return mealPlanList.getData();
    }

    public int size(){
        return mealPlanList.size();
    }
}
