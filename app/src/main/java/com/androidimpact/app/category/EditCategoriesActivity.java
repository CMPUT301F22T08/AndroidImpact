package com.androidimpact.app.category;

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
import android.widget.EditText;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.AddEditStoreIngredientActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EditCategoriesActivity extends AppCompatActivity {

    final String TAG = "EditIngredientCategories";
    final String COLLECTION_NAME = "categories";

    RecyclerView categoryRecyclerView;
    ArrayList<Category> categoryArrayList;
    CategoryAdapter categoryViewAdapter;

    // Views
    EditText newCategoryInput;

    // Firestore
    FirebaseFirestore db;
    CollectionReference categoryCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_categories);

        getSupportActionBar().setTitle("Edit Categories");

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        categoryCollection = db.collection(COLLECTION_NAME);

        // initialize views
        newCategoryInput = findViewById(R.id.category_editText);

        // initialize adapters and custom
        categoryRecyclerView = findViewById(R.id.category_listview);
        categoryArrayList = new ArrayList<>();
        categoryViewAdapter = new CategoryAdapter(this, categoryArrayList);

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        categoryRecyclerView.setLayoutManager(manager);
        categoryRecyclerView.setAdapter(categoryViewAdapter);
        setUpItemTouchHelper();

        categoryCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen .", error);
                return;
            }

            // Clear the old list
            categoryArrayList.clear();

            if (queryDocumentSnapshots == null) { return; }

            int errorCount = 0;
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                try {
                    categoryArrayList.add(doc.toObject(Category.class));
                } catch (Exception e) {
                    Log.i(TAG + ":snapshotListener", "Error retrieving document " + id + ":" + e);
                    errorCount += 1;
                }
            }

            if (errorCount > 0) {
                Snackbar.make(categoryRecyclerView, "Error reading " + errorCount + " documents!", Snackbar.LENGTH_LONG)
                    .setAction("Ok", view1 -> {}).show();
            }
            Log.i(TAG, "Snapshot listener: Added " + categoryArrayList.size() + " elements");
            categoryArrayList.sort((l1, l2) -> (int) (l1.getDateAdded().getTime() - l2.getDateAdded().getTime()));
            categoryViewAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Helper function to set up ItemTouchHelper, which manages callbacks when a user swipes on a RecyclerView
     * It then attaches the itemTouchHelper to the recyclerView (categoryRecyclerView)
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
                Category deletedIngredient = categoryArrayList.get(position);
                String category = deletedIngredient.getCategory();
                String id = deletedIngredient.getCategory();

                Log.d(TAG, "Swiped " + category + " at position " + position);

                // we first count up how many collections there are
                // if there is only 1 left, we cannot delete
                categoryCollection.count().get(AggregateSource.SERVER)
                        .addOnSuccessListener(aggregateQuerySnapshot -> {
                            long count = aggregateQuerySnapshot.getCount();

                            if (count <= 1) {
                                makeSnackbar("You need at least 1 category!");
                                // "swipes" the element back lmao
                                categoryViewAdapter.notifyItemChanged(position);
                                return;
                            }
                            // delete item from firebase
                            categoryCollection.document(id)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, category + " has been deleted successfully!");
                                        makeSnackbar("Deleted " + category);
                                    })
                                    .addOnFailureListener(e -> {
                                        makeSnackbar("Could not delete " + category + "!");
                                        Log.d(TAG, category + " could not be deleted!" + e);
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "Failed to count categories!", e);
                            makeSnackbar("Failed to count categories! Check the logs... ");
                        });
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(categoryRecyclerView);
    }

    /**
     * This is run whenever `R.id.addCategoryBtn` is pressed, when the user wants to add a new category
     */
    public void addCategory(View view) {
        Log.i(TAG + ":categoryBtnPressed", "Adding a new category!");
        String categoryName = newCategoryInput.getText().toString();

        // return fast on empty category
        if (categoryName.equals("")) {
            makeSnackbar("Please enter a category!");
            return;
        }

        // check if category already exists
        // if it does, warn the user (and don't add the category)
        categoryCollection.document(categoryName)
                .get()
                .continueWithTask(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        throw new Exception(categoryName + " already exists!");
                    } else {
                        Category l = new Category(categoryName);
                        Log.i(TAG, "Adding category " + l.getCategory());
                        newCategoryInput.setText("");
                        return categoryCollection.document(l.getCategory()).set(l);
                    }
                })
                .addOnSuccessListener(unused -> {
                    makeSnackbar("Added " + categoryName);
                })
                .addOnFailureListener(e -> {
                    makeSnackbar("Error: " + e.getMessage());
                });
    }

    /**
     * This is when the "back" button is pressed
     */
    public void goBack(View view) {
        Log.i(TAG + ":goBack", "Finish ingredient category edit");
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
        RecyclerView root = findViewById(R.id.category_listview);
        Snackbar.make(root, msg, Snackbar.LENGTH_LONG)
                .setAction("Ok", view1 -> {})
                .show();
    }
}