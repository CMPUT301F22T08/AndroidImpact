package com.androidimpact.app.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.Spinner;

import com.androidimpact.app.R;
import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.recipes.RecipeController;
import com.androidimpact.app.recipes.RecipeList;
import com.androidimpact.app.recipes.RecipeListAdapter;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.activities.RecipeAddViewEditActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeListFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @version 1.0
 * @author Vedant Vyas
 */
public class RecipeListFragment extends Fragment implements NavbarFragment{
    final String TAG = "RecipeListFragment";

    // Declare the variables so that you will be able to reference it later.
    RecyclerView recipeListView;
    RecipeListAdapter recipeViewAdapter;

    RecipeController recipeController;

    // These must go
    FirebaseFirestore db;
    CollectionReference recipeCollection;


    String[] sortingOptions;
    Spinner sortSpinner;


    // using ActivityResultLaunchers
    // note that editRecipeLauncher is defined in RecipeListAdapter
    private ActivityResultLauncher<Intent> addRecipeLauncher;

    /**
     * Required empty public constructor
     */
    public RecipeListFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecipeList.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeListFragment newInstance() {
        RecipeListFragment fragment = new RecipeListFragment();
        return fragment;
    }

    /**
     *
     Fragment Called to do initial creation of a fragment. This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        recipeCollection = db.collection("recipes");
    }

    /**
     *
     * Fragment Called to have the fragment instantiate its user interface view.
     * This will be called between onCreate(Bundle) and onViewCreated(View, Bundle).
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_list, container, false);
    }


    /**
     *
     * Fragment Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     * @param view
     * @param savedInstanceState
     */
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

        recipeController = ((MainActivity)a).getRecipeController();

        // Initialize views
        sortSpinner = a.findViewById(R.id.sort_recipe_spinner);

        // initialize adapters and customList, connect to DB
        recipeListView = a.findViewById(R.id.recipe_listview);

        recipeViewAdapter = new RecipeListAdapter(getContext(), recipeController);
        sortingOptions = RecipeList.getSortChoices();
        ArrayAdapter<String> sortingOptionsAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item,
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
                recipeController.sortData(i);
                recipeViewAdapter.notifyDataSetChanged();
            }

            /**
             * This function ensures default sorting if no other sorting selected
             * @param adapterView   The adapterView that does not contain any user selection
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                recipeController.sortData(0);
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
             * This method is called when the item is moved
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
             * Creates swipe to delete functionality
             * @param viewHolder
             * @param direction
             */
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // below line is to get the position
                // of the item at that position.

                int position = viewHolder.getAdapterPosition();
                Log.d(TAG, "Swiped " + recipeController.get(position).getTitle() + " at position " + position);
                recipeController.delete(position);
            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(recipeListView);

        // on snapshot listener for the collection
        recipeController.addDataUpdateSnapshotListener(recipeViewAdapter);


        /**
         * DEFINE ACTIVITY LAUNCHERS
         *
         * It is strongly recommended to register our activity result launchers in onCreate
         * https://stackoverflow.com/a/70215498
         */
        addRecipeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.i(TAG + ":addRecipeResult", "Got bundle");

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        final KonfettiView confetti = a.findViewById(R.id.confetti_view_recipe);
                        Snackbar.make(recipeListView, "Added the recipe!", Snackbar.LENGTH_SHORT).show();

                        int[] test = {0,1};
                        confetti.getLocationInWindow(test);
                        Log.i(TAG, "location:" + Arrays.toString(test));

                        confetti.build()
                                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                                .setDirection(0.0, 359.0)
                                .setSpeed(1f, 5f)
                                .setFadeOutEnabled(true)
                                .setTimeToLive(500L)
                                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                                .addSizes(new Size(8, 4f))
                                .setPosition(-50f, confetti.getWidth() + 50f, -50f, -50f)
                                .streamFor(300, 2000L);

                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // cancelled request - do nothing.
                        Log.i(TAG + ":addRecipeResult", "Received cancelled");
                    }
                });
    }


    /**
     * Sets the FAB in the navigation bar to act as a "add Recipelist" button
     *
     * Derived from NavbarFragment
     * @param navigationFAB
     */
    public void setFabListener(FloatingActionButton navigationFAB) {
        navigationFAB.setOnClickListener(v -> {
            Log.i(TAG + ":addRecipe", "Adding recipe!");
            Intent intent = new Intent(getContext(), RecipeAddViewEditActivity.class);
            intent.putExtra("activity_name", "Add recipe");
            addRecipeLauncher.launch(intent);
        });
    }
}