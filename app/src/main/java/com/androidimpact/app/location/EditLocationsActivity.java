package com.androidimpact.app.location;

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
import android.widget.Button;
import android.widget.EditText;

import com.androidimpact.app.activities.AddEditStoreIngredientActivity;
import com.androidimpact.app.R;
import com.androidimpact.app.category.Category;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * EditLocationsActivity
 * Activity for modifying user-defined locations
 * @version 1.0
 * @author Joshua Ji
 */
public class EditLocationsActivity extends AppCompatActivity {

    final String TAG = "EditIngredientLocations";
    final String COLLECTION_NAME = "locations";

    RecyclerView locationRecyclerView;
    ArrayList<Location> locationArrayList;
    LocationAdapter locationViewAdapter;

    // Views
    Button addLocationBtn;
    EditText newLocationInput;

    // Firestore
    FirebaseFirestore db;
    CollectionReference locationCollection;
    String userPath;

    /**
     * OnCreate class which initializes edit location firebase/recyclerview
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_locations);

        getSupportActionBar().setTitle("Edit Locations");

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            userPath = extras.getString("data-path");
        }

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        locationCollection = db.document(userPath).collection(COLLECTION_NAME);

        // initialize views
        newLocationInput = findViewById(R.id.location_editText);

        // initialize adapters and custom
        locationRecyclerView = findViewById(R.id.location_listview);
        locationArrayList = new ArrayList<>();
        locationViewAdapter = new LocationAdapter(this, locationArrayList);

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        locationRecyclerView.setLayoutManager(manager);
        locationRecyclerView.setAdapter(locationViewAdapter);
        setUpItemTouchHelper();

        locationCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }

            // Clear the old list
            locationArrayList.clear();

            if (queryDocumentSnapshots == null) { return; }

            int errorCount = 0;
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                try {
                    locationArrayList.add(doc.toObject(Location.class));
                } catch (Exception e) {
                    Log.i(TAG + ":snapshotListener", "Error retrieving document " + id + ":" + e);
                    errorCount += 1;
                }
            }

            if (errorCount > 0) {
                Snackbar.make(locationRecyclerView, "Error reading " + errorCount + " documents!", Snackbar.LENGTH_LONG)
                        .setAction("Ok", view1 -> {})
                        .show();
            }
            Log.i(TAG, "Snapshot listener: Added " + locationArrayList.size() + " elements");
            locationArrayList.sort((l1, l2) -> (int) (l1.getDateAdded().getTime() - l2.getDateAdded().getTime()));
            locationViewAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Helper function to set up ItemTouchHelper, which manages callbacks when a user swipes on a RecyclerView
     * It then attaches the itemTouchHelper to the recyclerView (locationRecyclerView)
     *
     * This makes our code cleaner
     */
    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            /**
             * this method is called when the item is moved.
             *
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
             *
             * @param viewHolder
             * @param direction
             */
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // get the position of the selected item
                int position = viewHolder.getAdapterPosition();

                // Get the swiped item at a particular position.
                Location deletedIngredient = locationArrayList.get(position);
                String location = deletedIngredient.getLocation();

                Log.d(TAG, "Swiped " + location + " at position " + position);

                // we first count up how many collections there are
                // if there is only 1 left, we cannot delete
                locationCollection.count().get(AggregateSource.SERVER)
                        .addOnSuccessListener(aggregateQuerySnapshot -> {
                            long count = aggregateQuerySnapshot.getCount();

                            if (count <= 1) {
                                makeSnackbar("You need at least 1 location!");
                                // "swipes" the element back lmao
                                locationViewAdapter.notifyItemChanged(position);
                                return;
                            }
                            // delete item from firebase
                            locationCollection.document(location)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, location + " has been deleted successfully!");
                                        makeSnackbar("Deleted " + location);
                                    })
                                    .addOnFailureListener(e -> {
                                        makeSnackbar("Could not delete " + location + "!");
                                        Log.d(TAG, location + " could not be deleted!" + e);
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "Failed to count locations!", e);
                            makeSnackbar("Failed to count locations! Check the logs... ");
                        });
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(locationRecyclerView);
    }

    /**
     * This is run whenever `R.id.addLocationBtn` is pressed
     */
    public void addLocation(View view) {
        Log.i(TAG + ":locationBtnPressed", "Adding a new location!");
        String locationName = newLocationInput.getText().toString();

        // return fast on empty location
        if (locationName.equals("")) {
            makeSnackbar("Please enter a location!");
            return;
        }

        // check if location already exists
        // if it does, warn the user (and don't add the location)
        locationCollection.document(locationName)
                .get()
                .continueWithTask(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        throw new Exception(locationName + " already exists!");
                    } else {
                        Location l = new Location(locationName);
                        Log.i(TAG, "Adding location " + l.getLocation());
                        newLocationInput.setText("");
                        return locationCollection.document(l.getLocation()).set(l);
                    }
                })
                .addOnSuccessListener(unused -> {
                    makeSnackbar("Added " + locationName);
                })
                .addOnFailureListener(e -> {
                    makeSnackbar("Error: " + e.getMessage());
                });
    }

    /**
     * This is when the "back" button is pressed
     */
    public void goBack(View view) {
        Log.i(TAG + ":goBack", "Finish ingredient location edit");
        Intent intent = new Intent(this, AddEditStoreIngredientActivity.class);
        // make sure you can't go back to this activity
        // https://stackoverflow.com/a/18957237
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * Simplified method to make a simple snackbar
     */
    private void makeSnackbar(String msg) {
        RecyclerView root = findViewById(R.id.location_listview);
        Snackbar.make(root, msg, Snackbar.LENGTH_LONG)
                .setAction("Ok", view1 -> {})
                .show();
    }
}