package com.androidimpact.app.unit;

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

import com.androidimpact.app.R;
import com.androidimpact.app.activities.AddEditStoreIngredientActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EditUnitsActivity extends AppCompatActivity {
    final String TAG = "EditUnitsActivity";
    final String COLLECTION_NAME = "units";

    RecyclerView unitRecyclerView;
    ArrayList<Unit> unitArrayList;
    UnitAdapter unitViewAdapter;

    // Views
    Button addUnitBtn;
    EditText newUnitInput;

    // Firestore
    FirebaseFirestore db;
    CollectionReference unitCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_units);

        getSupportActionBar().setTitle("Edit Units");

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        unitCollection = db.collection(COLLECTION_NAME);

        // initialize views
        newUnitInput = findViewById(R.id.edit_units_editText);

        // initialize adapters and custom
        unitRecyclerView = findViewById(R.id.edit_units_listview);
        unitArrayList = new ArrayList<>();
        unitViewAdapter = new UnitAdapter(unitArrayList);

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        unitRecyclerView.setLayoutManager(manager);
        unitRecyclerView.setAdapter(unitViewAdapter);
        setUpItemTouchHelper();

        unitCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }

            // Clear the old list
            unitArrayList.clear();

            if (queryDocumentSnapshots == null) { return; }

            int errorCount = 0;
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                try {
                    unitArrayList.add(doc.toObject(Unit.class));
                } catch (Exception e) {
                    Log.i(TAG + ":snapshotListener", "Error retrieving document " + id + ":" + e);
                    errorCount += 1;
                }
            }

            if (errorCount > 0) {
                Snackbar.make(unitRecyclerView, "Error reading " + errorCount + " documents!", Snackbar.LENGTH_LONG).show();
            }
            Log.i(TAG, "Snapshot listener: Added " + unitArrayList.size() + " elements");
            unitArrayList.sort((l1, l2) -> (int) (l1.getDateAdded().getTime() - l2.getDateAdded().getTime()));
            unitViewAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Helper function to set up ItemTouchHelper, which manages callbacks when a user swipes on a RecyclerView
     * It then attaches the itemTouchHelper to the recyclerView (unitRecyclerView)
     *
     * This makes our code cleaner
     */
    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            /**
             * this method is called when the item is moved
             * We do not care about this
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
                Unit deletedIngredient = unitArrayList.get(position);
                String unit = deletedIngredient.getUnit();

                Log.d(TAG, "Swiped " + unit + " at position " + position);

                // we first count up how many collections there are
                // if there is only 1 left, we cannot delete
                unitCollection.count().get(AggregateSource.SERVER)
                        .addOnSuccessListener(aggregateQuerySnapshot -> {
                            long count = aggregateQuerySnapshot.getCount();

                            if (count <= 1) {
                                makeSnackbar("You need at least 1 unit!");
                                // "swipes" the element back lmao
                                unitViewAdapter.notifyItemChanged(position);
                                return;
                            }
                            // delete item from firebase
                            unitCollection.document(unit)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, unit + " has been deleted successfully!");
                                        makeSnackbar("Deleted " + unit);
                                    })
                                    .addOnFailureListener(e -> {
                                        makeSnackbar("Could not delete " + unit + "!");
                                        Log.d(TAG, unit + " could not be deleted!" + e);
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "Failed to count units!", e);
                            makeSnackbar("Failed to count units! Check the logs... ");
                        });
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(unitRecyclerView);

    }

    /**
     * This is run whenever `R.id.addUnitBtn` is pressed
     */
    public void unitBtnPressed(View view) {
        Log.i(TAG + ":unitBtnPressed", "Adding a new unit!");
        String unitName = newUnitInput.getText().toString();

        // return fast on empty unit
        if (unitName.equals("")) {
            makeSnackbar("Please enter a unit!");
            return;
        }

        Unit l = new Unit(unitName);
        Log.i(TAG, "Adding unit " + l.getUnit());
        newUnitInput.setText("");
        unitCollection.document(l.getUnit()).set(l)
                .addOnSuccessListener(unused -> {
                    makeSnackbar("Added " + unitName);
                })
                .addOnFailureListener(e -> {
                    makeSnackbar("Failed to add " + unitName);
                });
    }

    /**
     * This is when the "back" button is pressed
     */
    public void goBack(View view) {
        Log.i(TAG + ":goBack", "Finish ingredient unit edit");
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
        RecyclerView root = findViewById(R.id.edit_units_listview);
        Snackbar.make(root, msg, Snackbar.LENGTH_LONG)
                .setAction("Ok", view1 -> {})
                .show();
    }
}