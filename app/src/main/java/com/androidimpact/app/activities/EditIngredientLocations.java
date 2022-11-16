package com.androidimpact.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.Location;
import com.androidimpact.app.LocationAdapter;
import com.androidimpact.app.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class EditIngredientLocations extends AppCompatActivity {
    final String TAG = "EditIngredientLocations";

    RecyclerView locationRecyclerView;
    ArrayList<Location> locationArrayList;
    LocationAdapter locationViewAdapter;

    // Firestore
    FirebaseFirestore db;
    CollectionReference ingredientsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ingredient_locations);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        ingredientsCollection = db.collection("locations");

        // initialize adapters and custom
        locationRecyclerView = findViewById(R.id.location_listview);
        locationArrayList = new ArrayList<>();
        locationViewAdapter = new LocationAdapter(this, locationArrayList);

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        locationRecyclerView.setLayoutManager(manager);
        locationRecyclerView.setAdapter(locationViewAdapter);

        // implements drag to delete
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
                Location deletedIngredient = locationArrayList.get(position);
                String location = deletedIngredient.getLocation();
                String id = deletedIngredient.getId();

                Log.d(TAG, "Swiped " + location + " at position " + position);

                // delete item from firebase
                ingredientsCollection.document(id)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, location + " has been deleted successfully!");
                            Snackbar.make(locationRecyclerView, "Deleted " + location, Snackbar.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            Snackbar.make(locationRecyclerView, "Could not delete " + location + "!", Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, location + " could not be deleted!" + e);
                        });
            }
            // finally, we add this to our recycler view.
        }).attachToRecyclerView(locationRecyclerView);

        ingredientsCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
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

                    String location = doc.get("location", String.class);
                    Location l = new Location(location);
                    locationArrayList.add(l);
                } catch (Exception e) {
                    Log.i(TAG + ":snapshotListener", "Error retrieving document " + id + ":" + e);
                    errorCount += 1;
                }
            }

            if (errorCount > 0) {
                Snackbar.make(locationRecyclerView, "Error reading " + errorCount + " documents!", Snackbar.LENGTH_LONG).show();
            }
            Log.i(TAG, "Snapshot listener: Added " + locationArrayList.size() + " elements");
            locationViewAdapter.notifyDataSetChanged();
        });
    }
}