package com.androidimpact.app.fragments;

import android.app.Activity;
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

import com.androidimpact.app.ingredients.Ingredient;
import com.androidimpact.app.R;
import com.androidimpact.app.shopping_list.ShoppingList;
import com.androidimpact.app.ingredients.StoreIngredientViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @version 1.0
 * @author Vedant Vyas
 */
public class ShoppingListFragment extends Fragment implements NavbarFragment {
    final String TAG = "ShoppingListFragment";

    //Assume collection name is shopping list
    final String COLLECTION_NAME = "shoppingList";


    // Declare the variables so that you will be able to reference it later.
    RecyclerView shoppingListView;
    StoreIngredientViewAdapter shopIngredientViewAdapter;
    ArrayList<Ingredient> shopIngredientDataList;

    ShoppingList shoppingList;

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
        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        ingredientsCollection = db.collection(COLLECTION_NAME);
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


        super.onCreate(savedInstanceState);

        Activity a = getActivity();

        if (a == null) {
            Log.i(TAG + ":onViewCreated", "Fragment is not associated with an activity!");
            return;
        }


        // initialize adapters and customList
        shoppingListView = a.findViewById(R.id.shopping_listview);

        shopIngredientDataList = new ArrayList<Ingredient>();

        shoppingList = new ShoppingList(shopIngredientDataList);

        //shopIngredientViewAdapter = new ShopIngredientViewAdapter(getContext(), ingredientDataList.getIngredientStorageList());















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