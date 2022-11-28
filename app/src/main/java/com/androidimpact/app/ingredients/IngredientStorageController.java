package com.androidimpact.app.ingredients;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Controller Class for IngredientStorage
 * Acts as a wrapper around IngredientStorage and implements functionality to back up the storage on FireStore
 */
public class IngredientStorageController {
    final String TAG = "IngredientStorageController";
    final String firestorePath = "ingredientStorage";

    private Context context;
    private FirebaseFirestore db;
    private CollectionReference ingredientStorageCollection;
    private IngredientStorage ingredientStorage;
    private boolean snapshotAdded;

    /**
     * Constructor: Creates an empty IngredientStorage class and populates it with data from FireStore
     * @param context
     *      (Context) The current context, used to push success/failure SnackBars to the screen.
     * @param userPath
     */
    public IngredientStorageController(Context context, String userPath) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        ingredientStorageCollection = db.document(userPath).collection(firestorePath);
        ingredientStorage = new IngredientStorage();
        snapshotAdded = false;
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
     * Add a snapshot listener to the FireStore collection that repopulates the ingredient storage when changes are detected.
     * Notifies the current ViewAdapter that the data may have changed.
     * @param storeIngredientViewAdapter (StoreIngredientViewAdapter) - The adapter to be updated when the latest data is pulled from firestore
     * @throws RuntimeException if the snapshot listener has already been added
     */
    public void addDataUpdateSnapshotListener(StoreIngredientViewAdapter storeIngredientViewAdapter) throws RuntimeException {
        if (snapshotAdded){
            throw new RuntimeException("Can only call addSnapshotListener once");
        }
        snapshotAdded = true;
        ingredientStorageCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }

            // Clear the old list
            ingredientStorage.clear();

            if (queryDocumentSnapshots == null) { return; }

            int errorCount = 0;
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                try {
                    // adding data from firestore
                    StoreIngredient ingredient = doc.toObject(StoreIngredient.class);
                    if (ingredient.getId() == null)
                        ingredient.setID(id);
                    ingredientStorage.add(ingredient);
                } catch (Exception e) {
                    Log.i(TAG + ":snapshotListener", "Error retrieving document " + id + ":" + e);
                    errorCount += 1;
                }
            }

            if (errorCount>0)
                pushSnackBarToContext("Error reading " + errorCount + " documents!");
            Log.i(TAG, "Snapshot listener: Added " + ingredientStorage.size() + " ingredients");

            ingredientStorage.sortByChoice();
            storeIngredientViewAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Add a snapshot listener to the FireStore collection that repopulates the ingredient storage when changes are detected.
     *
     */
    public void addSnapshotListenerAutocomplete(ArrayList<String> autoCompleteSource){
        if (snapshotAdded){
            throw new RuntimeException("Can only call addSnapshotListener once");
        }
        snapshotAdded = true;
        ingredientStorageCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }

            // Clear the old list
            ingredientStorage.clear();

            if (queryDocumentSnapshots == null) { return; }

            int errorCount = 0;
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                try {
                    // adding data from firestore
                    StoreIngredient ingredient = doc.toObject(StoreIngredient.class);
                    if (ingredient.getId() == null)
                        ingredient.setID(id);
                    ingredientStorage.add(ingredient);
                } catch (Exception e) {
                    Log.i(TAG + ":snapshotListener", "Error retrieving document " + id + ":" + e);
                    errorCount += 1;
                }
            }

            if (errorCount>0)
                pushSnackBarToContext("Error reading " + errorCount + " documents!");
            Log.i(TAG, "Snapshot listener: Added " + ingredientStorage.size() + " ingredients");

            ingredientStorage.sortByChoice();

            for (int i = 0; i<ingredientStorage.size(); i++){
                autoCompleteSource.add(ingredientStorage.get(i).getDescription());
            }
        });
    }


    /**
     * Adds/updates a storeIngredient object to the FireStore database.
     * Updates if the id of the parameter StoreIngredient object is not null.
     * Else generates a random ID and adds the item to the database
     * @param storeIngredient
     *      (StoreIngredient) - The ingredient to be added/edited in storage
     */
    public void addEdit(StoreIngredient storeIngredient){
        // Adds if id is null else edits
        String id = storeIngredient.getId();
        String description = storeIngredient.getDescription();
        if (id == null){
            UUID uuid = UUID.randomUUID();
            id = uuid.toString();
            storeIngredient.setID(id);
        }
        final String finalId = id;


        //finding ingredient with similar attribute (except amount)
        // if there is a similar ingredient in firebase, we merge ingredients instead of allowing duplicates

        ingredientStorageCollection.whereEqualTo("description", description).whereEqualTo("location", storeIngredient.getLocation()).whereEqualTo("unit", storeIngredient.getUnit()).whereEqualTo("category", storeIngredient.getCategory())
                .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if(task.isSuccessful()) {
                                    boolean found = false;

                                    int size = task.getResult().getDocuments().size();
                                    Log.i("size", String.valueOf(size));

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        StoreIngredient ingredientFB = document.toObject(StoreIngredient.class);
                                        if (ingredientFB.compareCalendar(storeIngredient) && ingredientFB.getLocation() != "" && finalId != ingredientFB.getId() ) {
                                            boolean check = storeIngredient.getId() == ingredientFB.getId();
                                            Log.d("found", "Found a matching Ingredient in FB " + ingredientFB.getDescription() + "comparison ID: " + String.valueOf(check));
                                            float newAmount;
                                            try {
                                                newAmount = ingredientFB.getAmount() + storeIngredient.getAmount();
                                            } catch (Exception e) {
                                                newAmount = Float.MAX_VALUE;
                                            }
                                            String message = "Found a similar ingredient, will merge both ingredients";
                                            pushSnackBarToContext(message);

                                            Log.i("amount", String.valueOf(newAmount));
                                            storeIngredient.setAmount(newAmount);
                                            ingredientStorageCollection.document(finalId).set(storeIngredient);

                                            ingredientStorageCollection.document(ingredientFB.getId())
                                                        .delete()
                                                        .addOnSuccessListener(aVoid -> {
                                                            Log.i(TAG, ingredientFB.getDescription() + " has been deleted successfully!");
                                                            //pushSnackBarToContext("Updated " + description);
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            pushSnackBarToContext("Could not delete " + ingredientFB.getDescription() + "!");
                                                            Log.i(TAG, ingredientFB.getDescription() + " could not be deleted: " + e);
                                                        });

                                            found = true;
                                        }
                                    }
                                    if (!found)
                                    {
                                        Log.d("not found", "Couldn't find the object in firebase");
                                        ingredientStorageCollection.document(finalId).set(storeIngredient);
                                    }
                                }
                                else
                                {
                                    Log.d("not found", "Couldn't find the object in firebase");
                                    ingredientStorageCollection.document(finalId).set(storeIngredient);

                                }
                            }
                        });

    }

    /**
     * Deletes the object in storage at the parameter position
     * @param position (int) - The position at which to delete the item
     * @throws ArrayIndexOutOfBoundsException if the index is not within the size of the storage
     */
    public void delete(int position) throws ArrayIndexOutOfBoundsException{
        // Get the swiped item at a particular position.
        StoreIngredient deletedIngredient = ingredientStorage.get(position);
        String description = deletedIngredient.getDescription();
        String id = deletedIngredient.getId();

        Log.d(TAG, "Swiped " + description + " at position " + position);

        // delete item from firebase
        ingredientStorageCollection.document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, description + " has been deleted successfully!");
                    pushSnackBarToContext("Deleted " + description);
                })
                .addOnFailureListener(e -> {
                    pushSnackBarToContext("Could not delete " + description + "!");
                    Log.d(TAG, description + " could not be deleted: " + e);
                });
    }

    /**
     * Get an list of names (String) that the storage can be sorted on
     * @return String[] - The sorting choices
     */
    public String[] getSortingChoices() { return IngredientStorage.getSortChoices();}

    /**
     * Sort the data according to parameter choice.
     * @param sortChoice - The position in the SortingChoices list that you want to sort by
     * @see com.androidimpact.app.SortableItemList
     */
    public void sortData(int sortChoice){
        ingredientStorage.setSortChoice(sortChoice);
        ingredientStorage.sortByChoice();
    }

    /**
     * Returns an array of StoreIngredient objects that are in the IngredientStorage associated with the controller
     * @return ArrayList<StoreIngredient>
     */
    public ArrayList<StoreIngredient> getData(){
        return ingredientStorage.getData();
    }

    /**
     * Return the underlying IngredientStorage object
     * @return IngredientStorage
     */
    public IngredientStorage getIngredientStorage() {
        return ingredientStorage;
    }

}
