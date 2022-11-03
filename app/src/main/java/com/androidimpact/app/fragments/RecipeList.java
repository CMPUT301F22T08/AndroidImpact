package com.androidimpact.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.androidimpact.app.R;
import com.androidimpact.app.Recipe;
import com.androidimpact.app.StoreIngredient;
import com.androidimpact.app.StoreIngredientViewAdapter;
import com.androidimpact.app.activities.AddEditStoreIngredientActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeList extends Fragment {
    final String TAG = "RecipeListFragment";

    // Declare the variables so that you will be able to reference it later.
    RecyclerView recipeListView;
    com.androidimpact.app.RecipeList recipeViewAdapter;
    ArrayList<Recipe> recipeDataList;
    String[] sortingOptions;
    Spinner sortSpinner;

    // adding recipes to firebase
    EditText addRecipeDescriptionText;
    FirebaseFirestore db;

    public RecipeList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecipeList.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeList newInstance() {
        RecipeList fragment = new RecipeList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // TODO: change this to fragment_recipe_list
        return inflater.inflate(R.layout.activity_recipe_list_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);
        Log.i(TAG + ":onViewCreated", "onViewCreated called!");

        Activity a = getActivity();
        if (a == null) {
            Log.i(TAG + ":onViewCreated", "Fragment is not associated with an activity!");
            return;
        }

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("recipes");

        // Initialize views
        sortSpinner = a.findViewById(R.id.sort_recipe_spinner);

        // initialize adapters and customList, connect to DB
        recipeListView = a.findViewById(R.id.recipe_listview);
        recipeDataList = new ArrayList<>();
        recipeViewAdapter = new com.androidimpact.app.RecipeList(getContext(), recipeDataList);
        sortingOptions = com.androidimpact.app.RecipeList.getSortChoices();
        ArrayAdapter<String> sortingOptionsAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                sortingOptions
        );
        sortingOptionsAdapter.setDropDownViewResource(
                android.R.layout.simple_list_item_1
        );
        sortSpinner.setAdapter(sortingOptionsAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * This function implements sorting based on a selection
             * @param adapterView   The adapterView where the user picked sorting choice
             * @param view          The view for the spinner choice picked
             * @param i             The index the user has picked
             * @param l             The row id of the user choice
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                recipeViewAdapter.setSortChoice(i);
                recipeViewAdapter.sortByChoice();
                recipeViewAdapter.notifyDataSetChanged();
            }

            /**
             * This function ensures default sorting if no other sorting selected
             * @param adapterView   The adapterView that does not contain any user selection
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                recipeViewAdapter.setSortChoice(0);
                recipeViewAdapter.sortByChoice();
                recipeViewAdapter.notifyDataSetChanged();
            }
        });

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
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

                Recipe recipeToAdd = new Recipe(
                        new ArrayList<>(),
                        description,
                        Integer.parseInt((String) doc.getData().get("prep time")),
                        Integer.parseInt((String) doc.getData().get("servings")),
                        (String) doc.getData().get("category"),
                        (String) doc.getData().get("comments"),
                        (String) doc.getData().get("date")
                );
                recipeToAdd.setPhoto((String) doc.getData().get("photo"));

                recipeDataList.add(recipeToAdd); // Adding the recipe attributes from FireStore

            }

            Log.i(TAG, "Snapshot listener: Added " + recipeDataList.size() + " elements");
            for (Recipe i : recipeDataList) {
                Log.i(TAG, "Snapshot listener: Added " + i.getTitle() + " to elements");
            }
            recipeViewAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
        });
    }
}