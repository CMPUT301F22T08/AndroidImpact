package com.androidimpact.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MealPlanAddEditViewActivity;
import com.androidimpact.app.meal_plan.OnSelectInterface;
import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.recipes.RecipeController;
import com.androidimpact.app.recipes.RecipeList;
import com.androidimpact.app.recipes.RecipeListAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class RecipeAddFragment extends DialogFragment {
    private String mealType;

    final String TAG = "RecipeAddFragment";

    // Declare the variables so that you will be able to reference it later.
    RecyclerView recipeListView;
    RecipeListAdapter recipeViewAdapter;
    RecipeController recipeController;
    ArrayList<Recipe> recipeDataList;
    String[] sortingOptions;
    Spinner sortSpinner;
    OnSelectInterface onSelectInterface;
    String selectedRecipeId, selectedRecipeTitle;

    // adding recipes to firebase
    FirebaseFirestore db;
    CollectionReference recipeCollection;
    String dataPath;

    public RecipeAddFragment(String meal, String dataPath) {
        super(R.layout.fragment_recipe_list);
        this.mealType = meal;
        this.dataPath = dataPath;
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
            selectedRecipeId = recipeDataList.get(i).getId();
            selectedRecipeTitle = recipeDataList.get(i).getTitle();
            ServingsAddFragment servingsAddFragment = new ServingsAddFragment(this.mealType, selectedRecipeId, selectedRecipeTitle, true);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            servingsAddFragment.show(transaction, "Add Servings");

            dismiss();
        };

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        recipeCollection = db.document(this.dataPath).collection("recipes");
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

        // Initialize views
        sortSpinner = getView().findViewById(R.id.sort_recipe_spinner);

        // initialize adapters and customList, connect to DB
        recipeListView = getView().findViewById(R.id.recipe_listview);

        this.recipeController = ((MealPlanAddEditViewActivity) getActivity()).getRecipeController();
        this.recipeDataList = this.recipeController.getData();
        recipeViewAdapter = new RecipeListAdapter(getContext(), this.recipeController, onSelectInterface, dataPath);
        this.recipeController.addDataUpdateSnapshotListener(recipeViewAdapter);
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
             *
             * @param adapterView The adapterView where the user picked sorting choice
             * @param view        The view for the spinner choice picked
             * @param i           The index the user has picked
             * @param l           The row id of the user choice
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                recipeViewAdapter.sortData(i);
                recipeViewAdapter.notifyDataSetChanged();
            }

            /**
             * This function ensures default sorting if no other sorting selected
             *
             * @param adapterView The adapterView that does not contain any user selection
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                recipeViewAdapter.sortData(0);
                recipeViewAdapter.notifyDataSetChanged();
            }
        });

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recipeListView.setLayoutManager(manager);
        recipeListView.setAdapter(recipeViewAdapter);

        // on snapshot listener for the collection
        recipeCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            // Clear the old list
            recipeDataList.clear();

            if (queryDocumentSnapshots == null) {
                return;
            }
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Recipe recipeToAdd = doc.toObject(Recipe.class);
                recipeDataList.add(recipeToAdd); // Adding the recipe attributes from FireStore
            }

            Log.i(TAG, "Snapshot listener: Added " + recipeDataList.size() + " elements");
            recipeViewAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
        });

    }
}
