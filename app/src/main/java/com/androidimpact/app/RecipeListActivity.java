package com.androidimpact.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Activity class for Recipe List
 * @author Aneeljyot Alagh, Clare Chen
 * @version 1.0
 */
public class RecipeListActivity extends AppCompatActivity {

    // Declare the variables so that you will be able to reference it later.
    RecyclerView recipeListView;
    RecipeList recipeViewAdapter;
    ArrayList<Recipe> recipeDataList;
    String[] sortingOptions;
    Spinner sortSpinner;

    // adding recipes to firebase
    final String TAG = "RecipeListActivity";
    Button addRecipeBtn;
    EditText addRecipeDescriptionText;
    FirebaseFirestore db;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list_activity);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("recipe");

        // Initialize views
        addRecipeBtn = findViewById(R.id.add_recipe_button);
        addRecipeDescriptionText = findViewById(R.id.add_recipe_description);
        sortSpinner = findViewById(R.id.sort_recipe_spinner);

        // initialize adapters and customList, connect to DB
        recipeListView = findViewById(R.id.recipe_listview);
        recipeDataList = new ArrayList<>();
        recipeViewAdapter = new RecipeList(this, recipeDataList);
        sortingOptions = RecipeList.getSortChoices();
        ArrayAdapter<String> sortingOptionsAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                sortingOptions
                );
        sortingOptionsAdapter.setDropDownViewResource(
                android.R.layout.simple_list_item_1
        );
        sortSpinner.setAdapter(sortingOptionsAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             *
             * @param adapterView
             * @param view
             * @param i
             * @param l
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                recipeViewAdapter.setSortChoice(i);
                recipeViewAdapter.sortByChoice();
                recipeViewAdapter.notifyDataSetChanged();
            }

            /**
             *
             * @param adapterView
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                recipeViewAdapter.setSortChoice(0);
                recipeViewAdapter.sortByChoice();
                recipeViewAdapter.notifyDataSetChanged();
            }
        });

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recipeListView.setLayoutManager(manager);
        recipeListView.setAdapter(recipeViewAdapter);

        // drag to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            /**
             *
             * @param recyclerView
             * @param viewHolder
             * @param target
             * @return
             */
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // this method is called
                // when the item is moved.
                return false;
            }

            /**
             *
             * @param viewHolder
             * @param direction
             */
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // below line is to get the position
                // of the item at that position.
                int position = viewHolder.getAdapterPosition();

                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                Recipe deletedRecipe = recipeDataList.get(position);
                String description = deletedRecipe.getTitle();

                Log.d(TAG, "Swiped " + description + " at position " + position);

                // delete item from firebase
                collectionReference.document(description)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // task succeeded
                            // cityAdapter will automatically update. No need to remove it from out list
                            Log.d(TAG, description + " has been deleted successfully!");
                            Snackbar.make(recipeListView, "Deleted " + description, Snackbar.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            Snackbar.make(recipeListView, "Could not delete " + description + "!", Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, description + " could not be deleted!" + e);
                        });
            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(recipeListView);

        // on snapshot listener for the collection
        collectionReference.addSnapshotListener((queryDocumentSnapshots, error) -> {
            // Clear the old list
            recipeDataList.clear();

            if (queryDocumentSnapshots == null) {
                return;
            }
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Log.d(TAG, String.valueOf(doc.getId()));
                Log.d(TAG, String.valueOf(doc.getData()));
                String description = doc.getId();
                recipeDataList.add(new Recipe(
                        new ArrayList<>(Arrays.asList(
                                new Ingredient[]{new Ingredient("")})),
                        description,
                        0,//Integer.valueOf((String) doc.getData().get("prep time")),
                        0,
                        "breakfast",
                        "hello i like food test",
                        "date"
                )); // Adding the cities and provinces from FireStore
            }

            Log.i(TAG, "Snapshot listener: Added " + recipeDataList.size() + " elements");
            for (Recipe i : recipeDataList) {
                Log.i(TAG, "Snapshot listener: Added " + i.getTitle() + " to elements");
            }
            recipeViewAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
        });
    }

    /**
     *
     * @param view
     */
    public void addIngredient(View view)  {
        final CollectionReference collectionReference = db.collection("recipe");

        final String description = addRecipeDescriptionText.getText().toString();
        HashMap<String, String> data = new HashMap<>();

        if (description.length() > 0) {
            data.put("Province Name", description);
            data.put("prep time", "5");

            collectionReference
                    .document(description)
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        // task succeeded
                        Log.d(TAG + "AddRecipe", "Data has been added successfully!");
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG + "AddRecipe", "Data could not be added!" + e);
                    });

            addRecipeDescriptionText.setText("");
        }
    }
}