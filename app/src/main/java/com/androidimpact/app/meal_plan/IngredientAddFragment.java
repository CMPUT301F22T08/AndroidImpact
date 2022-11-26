package com.androidimpact.app.meal_plan;

import static java.util.Objects.isNull;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.AddEditStoreIngredientActivity;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.activities.MealPlanAddEditViewActivity;
import com.androidimpact.app.fragments.IngredientStorageFragment;
import com.androidimpact.app.ingredients.IngredientStorage;
import com.androidimpact.app.ingredients.IngredientStorageController;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.ingredients.StoreIngredientViewAdapter;
import com.androidimpact.app.recipes.Recipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class IngredientAddFragment extends DialogFragment {
    final String TAG = "IngredientAddFragment";
    private static IngredientAddFragment instance;

    RecyclerView ingredientListView;
    StoreIngredientViewAdapter storeIngredientViewAdapter;

    Spinner sortIngredientSpinner;
    String[] sortingChoices;
    TextView sortText;

    OnSelectInterface onSelectInterface;
    String selectedIngredientId;
    IngredientStorage ingredientStorage;
    ArrayList<StoreIngredient> ingredientDataList;

    // adding recipes to firebase
    FirebaseFirestore db;
    CollectionReference ingredientCollection;

    private String mealType;

    /**
     * Required empty public constructor
     */
    public IngredientAddFragment(String meal) {
        super(R.layout.fragment_ingredient_storage);
        this.mealType = meal;
    }

    /**
     *
     Fragment Called to do initial creation of a fragment. This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onSelectInterface = i -> {
            selectedIngredientId = ingredientDataList.get(i).getId();
            //MealPlanAddEditViewActivity mealPlanAddEditViewActivity = (MealPlanAddEditViewActivity) getActivity();
            //mealPlanAddEditViewActivity.addIngredient(this.mealType, selectedIngredientId);
            ServingsAddFragment servingsAddFragment = new ServingsAddFragment(this.mealType, selectedIngredientId, false);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            servingsAddFragment.show(transaction, "Add Servings");

            dismiss();
        };

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        ingredientCollection = db.collection("ingredientStorage");
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
        return inflater.inflate(R.layout.fragment_ingredient_storage, container, false);
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

        // initialize adapters and customList
        ingredientStorage = new IngredientStorage();
        ingredientDataList = ingredientStorage.getData();
        ingredientListView = getDialog().findViewById(R.id.ingredient_listview);
        storeIngredientViewAdapter = new StoreIngredientViewAdapter(getContext(), ingredientStorage, onSelectInterface);

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        ingredientListView.setLayoutManager(manager);
        ingredientListView.setAdapter(storeIngredientViewAdapter);

        //finding sort spinner
        sortIngredientSpinner = getDialog().findViewById(R.id.sort_ingredient_spinner);
        sortText = getDialog().findViewById(R.id.sort_ingredient_info);

        // getting available sorting choices
        sortingChoices = ingredientStorage.getSortChoices();

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
                ingredientStorage.setSortChoice(i);
                ingredientStorage.sortByChoice();
                storeIngredientViewAdapter.notifyDataSetChanged();
            }

            /**
             * This function ensures default sorting if no other sorting selected
             * @param adapterView   The adapterView that does not contain any user selection
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ingredientStorage.setSortChoice(0);
                ingredientStorage.sortByChoice();
                storeIngredientViewAdapter.notifyDataSetChanged();
            }
        });

        // on snapshot listener for the collection
        ingredientCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }

            // Clear the old list
            ingredientStorage.clear();

            if (queryDocumentSnapshots == null) { return; }

            int errorCount = 0;
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                try {
                    // adding data from firestore
                    StoreIngredient ingredient = doc.toObject(StoreIngredient.class);
                    if (ingredient.getId() == null)
                        ingredient.setID(id);
                    ingredientStorage.add(ingredient);
                } catch (Exception e) {
                    Log.i(TAG + ":snapshotListener", "Error retrieving document " + id + ":" + e);
                    errorCount += 1;
                }
            }

            //if (errorCount>0)
            //    Snackbar.make("Error reading " + errorCount + " documents!");
            //Snackbar.make(getContext().findViewById(R.id.nav_fragment), s, Snackbar.LENGTH_LONG)
            //        .setAction("OK", (v)->{}).show();
            Log.i(TAG, "Snapshot listener: Added " + ingredientStorage.size() + " ingredients");

            ingredientStorage.sortByChoice();
            storeIngredientViewAdapter.notifyDataSetChanged();
        });

    }

}
