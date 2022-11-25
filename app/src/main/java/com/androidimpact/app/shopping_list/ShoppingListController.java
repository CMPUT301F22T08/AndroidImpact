package com.androidimpact.app.shopping_list;

import android.content.Context;
import android.util.Log;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.ingredients.ShopIngredient;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.ingredients.StoreIngredientViewAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.UUID;

public class ShoppingListController {
    final String TAG = "ShoppingListController";

    final private String collectionName = "shoppingList";

    private Context context;
    private FirebaseFirestore db;
    private CollectionReference shoppingListCollection;
    private ShoppingList shoppingList;

    public ShoppingListController(Context context){
        this.context = context;
        shoppingList = new ShoppingList(new ArrayList<>());
        db = FirebaseFirestore.getInstance();
        shoppingListCollection = db.collection(collectionName);
    }

    private void pushSnackBarToContext(String s) {
        Snackbar.make(((MainActivity)context).findViewById(R.id.nav_fragment), s, Snackbar.LENGTH_LONG)
                .setAction("OK", (v)->{}).show();
    }

    public void addDataUpdateSnapshotListener(StoreIngredientViewAdapter storeIngredientViewAdapter){
        shoppingListCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }

            // Clear the old list
            shoppingList.clear();

            if (queryDocumentSnapshots == null) { return; }

            int errorCount = 0;
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                try {
                    // adding data from firestore
                    ShopIngredient ingredient = doc.toObject(ShopIngredient.class);
                    if (ingredient.getId() == null)
                        ingredient.setID(id);
                    shoppingList.add(ingredient);
                } catch (Exception e) {
                    Log.i(TAG + ":snapshotListener", "Error retrieving document " + id + ":" + e);
                    errorCount += 1;
                }
            }

            if (errorCount>0)
                pushSnackBarToContext("Error reading " + errorCount + " documents!");
            Log.i(TAG, "Snapshot listener: Added " + shoppingList.size() + " ingredients");

            shoppingList.sortByChoice();
            storeIngredientViewAdapter.notifyDataSetChanged();
        });
    }

    public void addEdit(StoreIngredient storeIngredient){
        //TODO
    }

    public void delete(int position) {
        //TODO
    }

    public String[] getSortingChoices() { return ShoppingList.getSortChoices();}

    public void sortData(int sortChoice){
        shoppingList.setSortChoice(sortChoice);
        shoppingList.sortByChoice();
    }

    public ArrayList<ShopIngredient> getData(){
        return shoppingList.getData();
    }


}
