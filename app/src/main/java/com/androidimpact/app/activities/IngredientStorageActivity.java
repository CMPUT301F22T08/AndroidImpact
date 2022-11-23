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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidimpact.app.IngredientStorage;
import com.androidimpact.app.StoreIngredient;
import com.androidimpact.app.StoreIngredientViewAdapter;
import com.androidimpact.app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Activity class for Ingredient Storage Activity
 * @version 1.0
 * @author Joshua Ji
 */
public class IngredientStorageActivity extends AppCompatActivity {
    final String TAG = "IngredientStorageActivity";
    final String COLLECTION_NAME = "ingredientStorage-old";

    // Declare the variables so that you will be able to reference it later.
    RecyclerView ingredientListView;
    StoreIngredientViewAdapter storeingredientViewAdapter;
    IngredientStorage ingredientDataList;

    // adding cities to firebase
    FirebaseFirestore db;
    CollectionReference ingredientsCollection;
    FloatingActionButton addIngredientFAB;
    Spinner sortSpinner2;
    String[] sortingChoices;
    TextView sortText;

    /**
     * Runs fragment on creation
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_storage);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        ingredientsCollection = db.collection(COLLECTION_NAME);

        // initialize adapters and customList
        ingredientListView = findViewById(R.id.ingredient_listview);

        ingredientDataList = new IngredientStorage();
        storeingredientViewAdapter = new StoreIngredientViewAdapter(this, ingredientDataList.getIngredientStorageList());

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ingredientListView.setLayoutManager(manager);
        ingredientListView.setAdapter(storeingredientViewAdapter);

        // Launch AddStoreIngredientActivity when FAB is clicked
        addIngredientFAB.setOnClickListener(v -> {
            Log.i(TAG + ":addStoreIngredient", "Adding ingredient!");
            Intent intent = new Intent(IngredientStorageActivity.this, AddEditStoreIngredientActivity.class);
            addStoreIngredientLauncher.launch(intent);
        });

        sortSpinner2 = findViewById(R.id.sort_ingredient_spinner);

        sortText = findViewById(R.id.sort_ingredient_info);


        sortingChoices = ingredientDataList.getSortChoices();
        ArrayAdapter<String> sortingOptionsAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                sortingChoices
        );
        sortingOptionsAdapter.setDropDownViewResource(
                android.R.layout.simple_list_item_1
        );
        sortSpinner2.setAdapter(sortingOptionsAdapter);

        sortSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            /**
             * Allow sorting to update
             * @param adapterView
             * @param view
             * @param i
             * @param l
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ingredientDataList.setSortChoice(i);
                ingredientDataList.sortByChoice();
              //  sortText.setText("Sort by: "+ ingredientDataList.getSortChoice());


                storeingredientViewAdapter.notifyDataSetChanged();
            }

            /**
             * This function ensures default sorting if no other sorting selected
             * @param adapterView   The adapterView that does not contain any user selection
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ingredientDataList.setSortChoice(0);
                ingredientDataList.sortByChoice();
              //  sortText.setText("Sort by: "+ ingredientDataList.getSortChoice());

                storeingredientViewAdapter.notifyDataSetChanged();
            }
        });

        // Adding onCLick listener for spinner so that list can be sorted


        storeingredientViewAdapter.setEditClickListener((storeIngredient, position) -> {
            // runs whenever a store ingredient edit btn is clicked
            Log.i(TAG + ":setEditClickListener", "Editing ingredient at position " + position);
            Intent intent = new Intent(IngredientStorageActivity.this, AddEditStoreIngredientActivity.class);
            intent.putExtra("storeIngredient", storeIngredient);
            editStoreIngredientLauncher.launch(intent);
        });

        // drag to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            /**
             * this method is called when the item is moved.
             * @param recyclerView
             * @param viewHolder
             * @param target
             * @return
             */
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            /**
             * this method is called when we swipe our item to right direction
             * @param viewHolder
             * @param direction
             */
            @Override
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
                    ingredientDataList.add(doc.toObject(StoreIngredient.class));
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

    final private ActivityResultLauncher<Intent> editStoreIngredientLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (isNull(result.getData())) {
                    return;
                }
                Bundle bundle = result.getData().getExtras();

                Log.i(TAG + ":editStoreIngredientLauncher", "Got bundle");

                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Ok - we have an updated ingredient!
                    // edit firebase directly
                    StoreIngredient ingredient = (StoreIngredient) bundle.getSerializable("ingredient");
                    ingredientsCollection.document(ingredient.getId()).set(ingredient);
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // cancelled request - do nothing.
                    Log.i(TAG + ":editStoreIngredientLauncher", "Received cancelled");
                }
            });

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

            Log.i(TAG + ":addStoreIngredientLauncher", "Got bundle");

            if (result.getResultCode() == Activity.RESULT_OK) {
                // Ok - we have an ingredient!
                StoreIngredient ingredient = (StoreIngredient) bundle.getSerializable("ingredient");
                Log.i(TAG + ":addStoreIngredientLauncher", ingredient.getDescription());
                ingredientsCollection.document(ingredient.getId()).set(ingredient);
            } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                // cancelled request - do nothing.
                Log.i(TAG + ":addStoreIngredientLauncher", "Received cancelled");
            }
        });
}