package com.androidimpact.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    RecyclerView ingredientListView;
    RecipeList ingredientViewAdapter;
    ArrayList<Recipe> ingredientDataList;

    // adding cities to firebase
    final String TAG = "RecipeListActivity";
    Button addIngredientBtn;
    EditText addIngredientDescriptionText;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list_activity);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("recipe");

        // Initialize views
        addIngredientBtn = findViewById(R.id.add_ingredient_button);
        addIngredientDescriptionText = findViewById(R.id.add_ingredient_description);

        // initialize adapters and customList, connect to DB
        ingredientListView = findViewById(R.id.ingredient_listview);
        ingredientDataList = new ArrayList<>();
        ingredientViewAdapter = new RecipeList(this, ingredientDataList);

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ingredientListView.setLayoutManager(manager);
        ingredientListView.setAdapter(ingredientViewAdapter);

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
                Recipe deletedIngredient = ingredientDataList.get(position);
                String description = deletedIngredient.getTitle();

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
                ingredientDataList.add(new Recipe(new ArrayList<>(Arrays.asList(new Ingredient[]{new Ingredient("")})), description, 0, 0, "breakfast", "hello i like food test")); // Adding the cities and provinces from FireStore
            }
            Log.i(TAG, "Snapshot listener: Added " + ingredientDataList.size() + " elements");
            for (Recipe i : ingredientDataList) {
                Log.i(TAG, "Snapshot listener: Added " + i.getTitle() + " to elements");
            }
            ingredientViewAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
        });
    }

    public void addIngredient(View view)  {
        final CollectionReference collectionReference = db.collection("recipe");

        final String description = addIngredientDescriptionText.getText().toString();
        HashMap<String, String> data = new HashMap<>();

        if (description.length() > 0) {
            data.put("Province Name", description);

            collectionReference
                    .document(description)
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        // task succeeded
                        Log.d(TAG + "AddIngredient", "Data has been added successfully!");
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG + "AddIngredient", "Data could not be added!" + e);
                    });

            addIngredientDescriptionText.setText("");
        }
    }
}