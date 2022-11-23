package com.androidimpact.app;

import android.content.Context;
import android.util.Log;

import com.androidimpact.app.activities.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class IngredientStorageController {
    final String TAG = "IngredientStorageController";

    private Context context;
    private FirebaseFirestore db;
    private CollectionReference ingredientStorageCollection;
    private IngredientStorage ingredientStorage;


    public IngredientStorageController(Context context) {
        this.context = context;
        //Instantiate the classes
        db = FirebaseFirestore.getInstance();
        ingredientStorageCollection = db.collection("ingredientStorage");
        ingredientStorage = new IngredientStorage();
    }

    private void pushSnackBarToContext(String s) {
        Snackbar.make(((MainActivity)context).findViewById(R.id.nav_fragment), s, Snackbar.LENGTH_LONG)
                .setAction("OK", (v)->{}).show();
    }

    public void delete(int position) {
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

    public String[] getSortingChoices() { return ingredientStorage.getSortChoices();}

    public void sortData(int sortChoice){
        ingredientStorage.setSortChoice(sortChoice);
        ingredientStorage.sortByChoice();
    }

    public CollectionReference getIngredientStorageCollection() {
        return ingredientStorageCollection;
    }

    public FirebaseFirestore getDb(){
        return db;
    }

    public IngredientStorage getIngredientStorage() {
        return ingredientStorage;
    }

    public ArrayList<StoreIngredient> getIngredientStorageList(){
        return ingredientStorage.getIngredientStorageList();
    }



    // IMPLEMENTING SERIALIZABLE
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(ingredientStorage);
    }


    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        db = FirebaseFirestore.getInstance();
        ingredientStorageCollection = db.collection("ingredientStorage");
    }












}
