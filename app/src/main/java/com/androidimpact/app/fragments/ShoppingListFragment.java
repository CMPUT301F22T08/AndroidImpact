package com.androidimpact.app.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidimpact.app.IngredientStorage;
import com.androidimpact.app.R;
import com.androidimpact.app.StoreIngredientViewAdapter;
import com.androidimpact.app.activities.RecipeAddViewEditActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment implements NavbarFragment {
    final String TAG = "ShoppingListFragment";


    // Declare the variables so that you will be able to reference it later.
    RecyclerView shoppingListView;
    StoreIngredientViewAdapter shopIngredientViewAdapter;
    IngredientStorage shopIngredientDataList;

    // adding cities to firebase
    FirebaseFirestore db;
    CollectionReference ingredientsCollection;
    Spinner sortIngredientSpinner;
    String[] sortingChoices;
    TextView sortText;

    /**
     * Required empty public constructor
     */
    public ShoppingListFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShoppingList.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
        return fragment;
    }

    /**
     * Runs on creation of the fragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
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
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    /**
     * Fragment Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG + ":onViewCreated", "onViewCreated called!");
    }

    /**
     * Sets the FAB in the navigation bar to act as a "add shopping list" button
     *
     * Derived from NavbarFragment
     * @param navigationFAB
     */
    public void setFabListener(FloatingActionButton navigationFAB) {
        return;
    }
}