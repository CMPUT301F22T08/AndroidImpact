package com.androidimpact.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidimpact.app.activities.AddEditStoreIngredientActivity;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.ingredients.Ingredient;
import com.androidimpact.app.R;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.shopping_list.ShopIngredientAdapter;
import com.androidimpact.app.shopping_list.ShoppingList;
import com.androidimpact.app.ingredients.StoreIngredientViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    ShopIngredientAdapter shopIngredientViewAdapter;
    ArrayList<Ingredient> shopIngredientDataList;

    ShoppingList shoppingList;

    // adding cities to firebase
    FirebaseFirestore db;
    CollectionReference shoppingCollection;
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
        shoppingCollection = db.collection(COLLECTION_NAME);
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
        shopIngredientViewAdapter = new ShopIngredientAdapter(getContext(), shopIngredientDataList);

        //shopIngredientViewAdapter = new ShopIngredientViewAdapter(getContext(), ingredientDataList.getIngredientStorageList());


        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        shoppingListView.setLayoutManager(manager);
        shoppingListView.setAdapter(shopIngredientViewAdapter);

//        // listen for edits in storeIngredientViewAdapter`
//        storeIngredientViewAdapter.setEditClickListener((storeIngredient, position) -> {
//            // runs whenever a store ingredient edit btn is clicked
//            Log.i(TAG + ":setEditClickListener", "Editing ingredient at position " + position);
//            Intent intent = new Intent(getContext(), AddEditStoreIngredientActivity.class);
//            intent.putExtra("storeIngredient", storeIngredient);
//            editStoreIngredientLauncher.launch(intent);
//        });

        //finding sort spinner
        sortIngredientSpinner = a.findViewById(R.id.sort_shopping_spinner);
        sortText = a.findViewById(R.id.sort_shopping_info);

        // getting available sorting choices
        sortingChoices = shoppingList.getSortChoices();

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

        /**
         * Needs to be removed when controller class is implemented
         *
         *
         *
         */

        shoppingCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }

            // Clear the old list
            shoppingList.clear();

            if (queryDocumentSnapshots == null) { return; }

            int errorCount = 0;
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                try {
                    // adding data from firestore
                    Ingredient ingredient = doc.toObject(Ingredient.class);
                    if (ingredient.getId() == null)
                        ingredient.setID(id);
                    shoppingList.add(ingredient);
                } catch (Exception e) {
                    Log.i(TAG + ":snapshotListener", "Error retrieving document " + id + ":" + e);
                    errorCount += 1;
                }
            }

//            if (errorCount>0)
//                pushSnackBarToContext("Error reading " + errorCount + " documents!");
            Log.i(TAG, "Snapshot listener: Added " + shoppingList.size() + " ingredients");

            shoppingList.sortByChoice();
            shopIngredientViewAdapter.notifyDataSetChanged();
        });














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