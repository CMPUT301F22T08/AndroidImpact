package com.androidimpact.app.shopping_list;


import android.content.Context;
import android.util.Log;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.UUID;


/**
 * This class serves as the controller class for shopping list
 * @version 1.0
 * @author Joshua Ji, Kailash Seshadri
 */
public class ShoppingListController {
    final String TAG = "ShoppingListController";

    final private String collectionName = "shoppingList";

    private Context context;
    private FirebaseFirestore db;
    private CollectionReference shoppingListCollection;
    private ShoppingList shoppingList;

    /**
     * Constructor for the Controller class.
     * @param context - The current context of the controller. Used to push Snackbars to the screen
     * @param userPath - The path to the current user's data in FireStore
     */
    public ShoppingListController(Context context, String userPath){
        this.context = context;
        shoppingList = new ShoppingList(new ArrayList<>());
        db = FirebaseFirestore.getInstance();
        shoppingListCollection = db.document(userPath).collection(collectionName);
    }

    /**
     * Used to push a snackBar to the screen to alert users.
     * @param s - The message to be pushed in the snackbar
     */
    private void pushSnackBarToContext(String s) {
        Snackbar.make(((MainActivity)context).findViewById(R.id.nav_fragment), s, Snackbar.LENGTH_LONG)
                .setAction("OK", (v)->{}).show();
    }

    /**
     * Add a SnapshotListener to the FireStore collection that the data si sourced from.
     * The SnapshotListener clears and updates the data when updated and tells the parameter ViewAdapter that the dataset has updated
     * @param shopIngredientViewAdapter - The ViewAdapter to be notified when the data changes.
     */
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

    /**
     * Add a new ShopIngredient or edit an existing ShopIngredient object in the ShoppingList.
     * Adds if the parameter ShopIngredient object with a random ID if it has a null id.
     * Changes the ShopIngredient object at the ID of the parameter ShopIngredient object if ID is not null.
     * @param shopIngredient The parameter ShopIngredient object ot be added/edited
     * @return The Task<void> of adding/editing the document in FireStore
     */
    public Task<Void> addEdit(ShopIngredient shopIngredient){
        Log.i(TAG + ":addEdit","AddEdit called on " + shopIngredient.getDescription());
        String id = shopIngredient.getId();
        if (id == null){
            UUID uuid = UUID.randomUUID();
            id = uuid.toString();
            shopIngredient.setID(id);
        }
        return shoppingListCollection.document(shopIngredient.getId()).set(shopIngredient);
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

    /**
     * Deletes the ShopIngredient object at the parameter position.
     * @param position The position in the list of the Object to deleted
     * @throws ArrayIndexOutOfBoundsException if the parameter position is not in the list
     */
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

    /**
     * Deletes the ShopIngredient object provided, if it exists in the list
     * @param deletedIngredient The ShopIngredient object to be deleted
     */
    public void delete(ShopIngredient deletedIngredient){
        // Get the swiped item at a particular position.
       // ShopIngredient deletedIngredient = shoppingList.get(position);
        String description = deletedIngredient.getDescription();
        String id = deletedIngredient.getId();

        Log.d(TAG, "Swiped " + description);

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

    /**
     * Returns the choices by which the list can be sorted
     * @return  A Strings[] of the choices
     */
    public String[] getSortingChoices() { return ShoppingList.getSortChoices();}

    /**
     * Sorts the data by the sortingChoice specified by sortChoices[sortChoice]
     * @param sortChoice the index of the sort choice in sortChoices
     */
    public void sortData(int sortChoice){
        shoppingList.setSortChoice(sortChoice);
        shoppingList.sortByChoice();
    }

    /**
     * Returns the data in the List as an array
     * @return ArrayList<ShopIngredient>
     */
    public ArrayList<ShopIngredient> getData(){
        return shoppingList.getData();
    }


}
