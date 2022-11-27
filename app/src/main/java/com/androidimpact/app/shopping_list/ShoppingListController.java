package com.androidimpact.app.shopping_list;

import android.content.Context;
import android.util.Log;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.ingredients.StoreIngredientViewAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

    public void addDataUpdateSnapshotListener(ShopIngredientAdapter shopIngredientViewAdapter){
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
            Log.i(TAG, "Snapshot listener: Added " + shoppingList.size() + " shoppingList items");

            shoppingList.sortByChoice();
            shopIngredientViewAdapter.notifyDataSetChanged();
        });
    }

    public Task<Void> addEdit(ShopIngredient storeIngredient){
        Log.i(TAG + ":addEdit","AddEdit called on " + storeIngredient.getDescription());
        return shoppingListCollection.document(storeIngredient.getId()).set(storeIngredient);
    }

    /**
     * Adds a list of ingredients, and returns a task when they will all be added
     * @param ingredients the list of ingredients to add
     * @return a task that succeeds when all ingredients are added
     */
    public Task<Void> addArray(ArrayList<ShopIngredient> ingredients) {
        Log.i(TAG + ":addArray","AddArray called on " + ingredients.size() + " elements");
        ArrayList<Task<Void>> futures = new ArrayList<>();
        for (ShopIngredient i : ingredients) {
            futures.add(shoppingListCollection.document(i.getId()).set(i));
        }
        return Tasks.whenAll(futures);
    }

    public void delete(int position) throws ArrayIndexOutOfBoundsException{
        // Get the swiped item at a particular position.
        ShopIngredient deletedIngredient = shoppingList.get(position);
        String description = deletedIngredient.getDescription();
        String id = deletedIngredient.getId();

        Log.d(TAG, "Swiped " + description + " at position " + position);

        // delete item from firebase
        shoppingListCollection.document(id)
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

    public String[] getSortingChoices() { return ShoppingList.getSortChoices();}

    public void sortData(int sortChoice){
        shoppingList.setSortChoice(sortChoice);
        shoppingList.sortByChoice();
    }

    public ArrayList<ShopIngredient> getData(){
        return shoppingList.getData();
    }


}
