package com.androidimpact.app.activities;

import static java.util.Objects.isNull;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.androidimpact.app.Ingredient;
import com.androidimpact.app.IngredientStorage;
import com.androidimpact.app.StoreIngredient;
import com.androidimpact.app.StoreIngredientViewAdapter;
import com.androidimpact.app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class IngredientStorageActivity extends AppCompatActivity {
    final String TAG = "IngredientStorageActivity";

    // Declare the variables so that you will be able to reference it later.
    RecyclerView ingredientListView;
    StoreIngredientViewAdapter storeingredientViewAdapter;
    IngredientStorage ingredientDataList;

    // adding cities to firebase
    FirebaseFirestore db;
    CollectionReference ingredientsCollection;
    FloatingActionButton addIngredientFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_storage);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        ingredientsCollection = db.collection("ingredientStorage");

        // initialize adapters and customList
        ingredientListView = findViewById(R.id.ingredient_listview);
        addIngredientFAB = findViewById(R.id.addStoreIngredientFAB);

        ingredientDataList = new IngredientStorage();
        storeingredientViewAdapter = new StoreIngredientViewAdapter(this, ingredientDataList.getIngredientStorageList());

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ingredientListView.setLayoutManager(manager);
        ingredientListView.setAdapter(storeingredientViewAdapter);

        // Launch AddStoreIngredientActivity when FAB is clicked
        addIngredientFAB.setOnClickListener(v -> {
            Log.i(TAG + ":addStoreIngredient", "Adding ingredient!");
            Intent intent = new Intent(IngredientStorageActivity.this, AddStoreIngredientActivity.class);
            addStoreIngredientLauncher.launch(intent);
        });

        // drag to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // this method is called when the item is moved.
                return false;
            }

            @Override
            // this method is called when we swipe our item to right direction.
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // get the position of the selected item
                int position = viewHolder.getAdapterPosition();

                // Get the swiped item at a particular position.
                StoreIngredient deletedIngredient = ingredientDataList.get(position);
                String description = deletedIngredient.getDescription();
                String id = deletedIngredient.getId();

                Log.d(TAG, "Swiped " + description + " at position " + position);

                // delete item from firebase
                ingredientsCollection.document(id)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, description + " has been deleted successfully!");
                        Snackbar.make(ingredientListView, "Deleted " + description, Snackbar.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Snackbar.make(ingredientListView, "Could not delete " + description + "!", Snackbar.LENGTH_LONG).show();
                        Log.d(TAG, description + " could not be deleted!" + e);
                    });
            }
            // finally, we add this to our recycler view.
        }).attachToRecyclerView(ingredientListView);

        // on snapshot listener for the collection
        ingredientsCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }

            // Clear the old list
            ingredientDataList.clear();

            if (queryDocumentSnapshots == null) { return; }

            int errorCount = 0;
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                try {
                    String description = doc.get("description", String.class);
                    float amount = doc.get("amount", float.class);
                    Date bestBefore = doc.get("bestBeforeDate", Date.class);
                    String category = doc.get("category", String.class);
                    String location = doc.get("location", String.class);
                    String unit = doc.get("unit", String.class);

                    StoreIngredient store = new StoreIngredient(id, description, amount, unit, category, bestBefore, location);

                    ingredientDataList.add(store); // Adding the cities and provinces from FireStore
                } catch (Exception e) {
                    Log.i(TAG + ":snapshotListener", "Error retrieving document " + id + ":" + e);
                    errorCount += 1;
                }
            }

            if (errorCount > 0) {
                Snackbar.make(ingredientListView, "Error reading " + errorCount + " documents!", Snackbar.LENGTH_LONG).show();
            }
            Log.i(TAG, "Snapshot listener: Added " + ingredientDataList.size() + " elements");
            storeingredientViewAdapter.notifyDataSetChanged();
        });
    }

    /**
     * AddIngredientLauncher uses the ActivityResultAPIs to handle data returned from
     * AddStoreIngredientActivity
     */
    final private ActivityResultLauncher<Intent> addStoreIngredientLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (isNull(result.getData())) {
                return;
            }
            Bundle bundle = result.getData().getExtras();

            Log.i(TAG + ":addIngredientResult", "Got bundle");

            if (result.getResultCode() == Activity.RESULT_OK) {
                // Ok - we have an ingredient!
                StoreIngredient ingredient = (StoreIngredient) bundle.getSerializable("ingredient");
                Log.i(TAG + ":addIngredientResult", ingredient.getDescription());
                ingredientsCollection.document(ingredient.getId()).set(ingredient);
            } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                // cancelled request - do nothing.
                Log.i(TAG + ":addIngredientResult", "Received cancelled");
            }
        });
}