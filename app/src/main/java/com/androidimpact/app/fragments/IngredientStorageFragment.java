package com.androidimpact.app.fragments;

import static java.util.Objects.isNull;

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
import android.widget.TextView;

import com.androidimpact.app.IngredientStorageController;
import com.androidimpact.app.R;
import com.androidimpact.app.StoreIngredient;
import com.androidimpact.app.StoreIngredientViewAdapter;
import com.androidimpact.app.activities.AddEditStoreIngredientActivity;
import com.androidimpact.app.activities.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IngredientStorageFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @version 1.0
 * @author Vedant Vyas
 */
public class IngredientStorageFragment extends Fragment implements NavbarFragment {
    final String TAG = "IngredientStorageFragment";

    private static IngredientStorageFragment instance;

    RecyclerView ingredientListView;
    StoreIngredientViewAdapter storeIngredientViewAdapter;
    IngredientStorageController ingredientStorageController;

    Spinner sortIngredientSpinner;
    String[] sortingChoices;
    TextView sortText;


    /**
     * AddIngredientLauncher uses the ActivityResultAPIs to handle data returned from the activities
     */
    private ActivityResultLauncher<Intent> editStoreIngredientLauncher;
    private ActivityResultLauncher<Intent> addStoreIngredientLauncher;
    /**
     * Required empty public constructor
     */
    public IngredientStorageFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment IngredientStorage.
     */
    // TODO: Rename and change types and number of parameters
    public static IngredientStorageFragment newInstance() {

        if (instance == null)
        {
            IngredientStorageFragment fragment = new IngredientStorageFragment();
            instance = fragment;

            return fragment;
        }

        return instance;
    }


    /**
     *
     * Fragment called to do initial creation of a fragment
     * This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     *
     Fragment Called to have the fragment instantiate its user interface view.
     This is optional, and non-graphical fragments can return null. This will be called between onCreate(Bundle) and onViewCreated(View, Bundle).
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // TODO: change this to fragment_ingredient_storage
        return inflater.inflate(R.layout.activity_ingredient_storage, container, false);
    }


    /**
     *
     Fragment Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG+":onViewCreated", "onViewCreated called!");
        super.onViewCreated(view, savedInstanceState);

        super.onCreate(savedInstanceState);

        Activity a = getActivity();
        if (a == null) {
            Log.i(TAG + ":onViewCreated", "Fragment is not associated with an activity!");
            return;
        }

        // initialize adapters and customList
        ingredientStorageController = ((MainActivity) a).getIngredientStorageController();
        ingredientListView = a.findViewById(R.id.ingredient_listview);
        storeIngredientViewAdapter = new StoreIngredientViewAdapter(getContext(), ingredientStorageController);

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        ingredientListView.setLayoutManager(manager);
        ingredientListView.setAdapter(storeIngredientViewAdapter);

        // listen for edits in storeIngredientViewAdapter`
        storeIngredientViewAdapter.setEditClickListener((storeIngredient, position) -> {
            // runs whenever a store ingredient edit btn is clicked
            Log.i(TAG + ":setEditClickListener", "Editing ingredient at position " + position);
            Intent intent = new Intent(getContext(), AddEditStoreIngredientActivity.class);
            intent.putExtra("storeIngredient", storeIngredient);
            editStoreIngredientLauncher.launch(intent);
        });

        //finding sort spinner
        sortIngredientSpinner = a.findViewById(R.id.sort_ingredient_spinner);
        sortText = a.findViewById(R.id.sort_ingredient_info);

        // getting available sorting choices
        sortingChoices = ingredientStorageController.getSortingChoices();

        // Creating a sorting adapter
        ArrayAdapter<String> sortingOptionsAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item,
                sortingChoices
        );

        //changing drop down layout
        sortingOptionsAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        sortIngredientSpinner.setAdapter(sortingOptionsAdapter);


        //setting up the on item selected listener which lets user sort on the basis of selection
        sortIngredientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Create method to get item for sort
             * @param adapterView
             * @param view
             * @param i
             * @param l
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ingredientStorageController.sortData(i);
                storeIngredientViewAdapter.notifyDataSetChanged();
            }

            /**
             * This function ensures default sorting if no other sorting selected
             * @param adapterView   The adapterView that does not contain any user selection
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ingredientStorageController.sortData(0);
                storeIngredientViewAdapter.notifyDataSetChanged();
            }
        });

        // drag to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            /**
             * this method is called when the item is moved
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
             * Deletes the swiped object.
             * Called when we swipe to the right
             * @param viewHolder
             * @param direction
             */
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ingredientStorageController.delete(position);
            }
            // finally, we add this to our recycler view.
        }).attachToRecyclerView(ingredientListView);

        // on snapshot listener for the collection
        ingredientStorageController.addDataUpdateSnapshotListener(storeIngredientViewAdapter);

        /**
         * DEFINE ACTIVITY LAUNCHERS
         *
         * It is strongly recommended to register our activity result launchers in onCreate
         * https://stackoverflow.com/a/70215498
         */
        editStoreIngredientLauncher = registerForActivityResult(
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
                    ingredientStorageController.addEdit(ingredient);
                    Snackbar.make(ingredientListView, "Edited " + ingredient.getDescription(), Snackbar.LENGTH_SHORT).show();

                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // cancelled request - do nothing.
                    Log.i(TAG + ":editStoreIngredientLauncher", "Received cancelled");
                }
            });

        addStoreIngredientLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (isNull(result.getData())) {
                    return;
                }
                Bundle bundle = result.getData().getExtras();

                Log.i(TAG + ":addIngredientResult", "Got bundle");

                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Ok - we have an ingredient!
                    StoreIngredient ingredient = (StoreIngredient) bundle.getSerializable("ingredient");

                    // LOL
                    final KonfettiView confetti = a.findViewById(R.id.confetti_view_ingredient_storage);
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

                    Log.i(TAG + ":addIngredientResult", ingredient.getDescription());
                    ingredientStorageController.addEdit(ingredient);
                    Snackbar.make(ingredientListView, "Added " + ingredient.getDescription(), Snackbar.LENGTH_SHORT).show();

                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // cancelled request - do nothing.
                    Log.i(TAG + ":addIngredientResult", "Received cancelled");
                }
            });
    }

    /**
     * Sets the FAB in the navigation bar to act as a "add StoreIngredient" button
     *
     * Derived from NavbarFragment
     * @param navigationFAB
     */
    public void setFabListener(FloatingActionButton navigationFAB) {
        navigationFAB.setOnClickListener(v -> {
            Log.i(TAG + ":addStoreIngredient", "Adding ingredient!");
            Intent intent = new Intent(getContext(), AddEditStoreIngredientActivity.class);
            addStoreIngredientLauncher.launch(intent);
        });
    }
}