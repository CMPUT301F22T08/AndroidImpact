package com.androidimpact.app.meal_plan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.activities.MealPlanAddEditViewActivity;
import com.androidimpact.app.activities.RecipeAddViewEditActivity;
import com.androidimpact.app.fragments.RecipeListFragment;
import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.recipes.RecipeList;
import com.androidimpact.app.recipes.RecipeListAdapter;
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

public class RecipeAddFragment extends DialogFragment {
    private String mealType;

    final String TAG = "RecipeListFragment";

    // Declare the variables so that you will be able to reference it later.
    RecyclerView recipeListView;
    RecipeListAdapter recipeViewAdapter;
    RecipeList recipeList;
    ArrayList<Recipe> recipeDataList;
    String[] sortingOptions;
    Spinner sortSpinner;
    OnSelectInterface onSelectInterface;
    String selectedRecipeId;

    // adding recipes to firebase
    FirebaseFirestore db;
    CollectionReference recipeCollection;

    public RecipeAddFragment(String meal) {
        super(R.layout.fragment_recipe_list);
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
            selectedRecipeId = recipeDataList.get(i).getId();
            MealPlanAddEditViewActivity mealPlanAddEditViewActivity = (MealPlanAddEditViewActivity) getActivity();
            mealPlanAddEditViewActivity.addRecipe(this.mealType, selectedRecipeId);
            dismiss();
        };

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


        // Initialize views
        sortSpinner = getDialog().findViewById(R.id.sort_recipe_spinner);

        // initialize adapters and customList, connect to DB
        recipeListView = getDialog().findViewById(R.id.recipe_listview);

        recipeDataList = new ArrayList<>();
        recipeList = new RecipeList(recipeDataList);
        //recipeList = ((MainActivity) a).getRecipeList();
        //recipeDataList = recipeList.getData();
        recipeViewAdapter = new RecipeListAdapter(getContext(), recipeList, onSelectInterface);
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
                recipeViewAdapter.setSortChoice(i);
                recipeViewAdapter.sortByChoice();
                recipeViewAdapter.notifyDataSetChanged();
            }

            /**
             * This function ensures default sorting if no other sorting selected
             *
             * @param adapterView The adapterView that does not contain any user selection
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
