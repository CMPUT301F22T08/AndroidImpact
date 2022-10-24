package com.androidimpact.app;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    // variables
    private final int ADD_STORE_INGREDIENT_ACTIVITY = 0;

    // Declare the variables so that you will be able to reference it later.
    RecyclerView ingredientListView;
    StoreIngredientViewAdapter storeingredientViewAdapter;
    //ArrayList<Ingredient> ingredientDataList;
    IngredientStorage ingredientDataList;


    // adding cities to firebase
    final String TAG = "MainActivity";
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("test");

        // initialize adapters and customList, connect to DB
        ingredientListView = findViewById(R.id.ingredient_listview);
       // ingredientDataList = new ArrayList<>();
        ingredientDataList = new IngredientStorage();

        storeingredientViewAdapter = new StoreIngredientViewAdapter(this, ingredientDataList.getIngredientStorageList());

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ingredientListView.setLayoutManager(manager);
        ingredientListView.setAdapter(storeingredientViewAdapter);

        // drag to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // this method is called
                // when the item is moved.
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // below line is to get the position
                // of the item at that position.
                int position = viewHolder.getAdapterPosition();

                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                Ingredient deletedIngredient = ingredientDataList.get(position);
                String description = deletedIngredient.getDescription();

                Log.d(TAG, "Swiped " + description + " at position " + position);

                // delete item from firebase
                collectionReference.document(description)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // task succeeded
                            // cityAdapter will automatically update. No need to remove it from out list
                            Log.d(TAG, description + " has been deleted successfully!");
                            Snackbar.make(ingredientListView, "Deleted " + description, Snackbar.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            Snackbar.make(ingredientListView, "Could not delete " + description + "!", Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, description + " could not be deleted!" + e);
                        });
            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(ingredientListView);

        // on snapshot listener for the collection
        collectionReference.addSnapshotListener((queryDocumentSnapshots, error) -> {
            // Clear the old list
            ingredientDataList.clear();

            if (queryDocumentSnapshots == null) {
                return;
            }
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Log.d(TAG, String.valueOf(doc.getData().get("Province Name")));
                String description = doc.getId();
                Calendar rightNow = Calendar.getInstance();
                ingredientDataList.add(new StoreIngredient(description, 0, "", "", rightNow, "trial")); // Adding the cities and provinces from FireStore
            }
            Log.i(TAG, "Snapshot listener: Added " + ingredientDataList.size() + " elements");
            storeingredientViewAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
        });
    }

    /**
     * AddIngredientLauncher uses the ActivityResultAPIs to handle data returned from
     * AddStoreIngredientActivity
     */
    final private ActivityResultLauncher<Intent> addStoreIngredientLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Bundle bundle = result.getData().getExtras();
                Log.i(TAG + ":addIngredientResult", "Got bundle");

                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Ok - we have an ingredient!
                    Ingredient ingredient = (Ingredient) bundle.getSerializable("ingredient");
                    Log.i(TAG + ":addIngredientResult", ingredient.getDescription());
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // cancelled request - do nothing.
                    Log.i(TAG + ":addIngredientResult", "Received cancelled");
                }
            });

    /**
     * ADD INGREDIENT
     *
     * This is executed when the Add ingredient FAB is clicked. It redirects to a new activity.
     * This new activity is basically just a form that creates a new ingredient in the storage
     * Since this activity returns data, we need to use `startActivityForResult`
     *
     * @param view
     */
    public void addIngredient(View view)  {
        Log.i(TAG + ":addIngredient", "Adding ingredient!");
        Intent intent = new Intent(this, AddStoreIngredientActivity.class);
        addStoreIngredientLauncher.launch(intent);
    }


}