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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.shopping_list.ShopIngredient;
import com.androidimpact.app.shopping_list.AddEditShoppingItemActivity;
import com.androidimpact.app.shopping_list.ShopIngredientAdapter;
import com.androidimpact.app.shopping_list.ShoppingListController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.UUID;
import java.util.Arrays;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

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
    ShoppingListController shoppingListController;

    // adding cities to firebase
    FirebaseFirestore db;
    CollectionReference shoppingCollection;
    Spinner sortIngredientSpinner;
    String[] sortingChoices;
    TextView sortText;

    Switch pickupSwitch;

    // use ActivityResultLaunchers to go to different activities
    // this is defined in onViewCreated, see the comment where we initialize it
    private ActivityResultLauncher<Intent> addShoppingListItemLauncher;
    private ActivityResultLauncher<Intent> editShoppingListItemLauncher;

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


        pickupSwitch = a.findViewById(R.id.shop_ingredient_switch);

//        pickupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked)
//                {
//                    //throw a dialog fragment that asks for amount pickedUp and updates the ingredient accordingly
//                }
//                else
//                {
//                    //This means that user accidentaly picked it up so change amount picked up to 0
//                }
//            }
//        });

        shoppingListController = ((MainActivity) a).getShoppingListController();
        shopIngredientViewAdapter = new ShopIngredientAdapter(getContext(), shoppingListController);

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        shoppingListView.setLayoutManager(manager);
        shoppingListView.setAdapter(shopIngredientViewAdapter);

        //finding sort spinner
        sortIngredientSpinner = a.findViewById(R.id.sort_shopping_spinner);
        sortText = a.findViewById(R.id.sort_shopping_info);

        // getting available sorting choices
        sortingChoices = shoppingListController.getSortingChoices();

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
        shoppingListController.addDataUpdateSnapshotListener(shopIngredientViewAdapter);

        // EVENT LISTENERS
        shopIngredientViewAdapter.setEditClickListener((food, position) -> {
            // when a shop ingredient is clicked, we edit
            // yeah, it's a bit different than ingredient and recipe
            Log.i(TAG + ":addRecipe", "Adding recipe!");
            Intent intent = new Intent(getContext(), AddEditShoppingItemActivity.class);
            intent.putExtra("ingredient", food);
            editShoppingListItemLauncher.launch(intent);
        });


        /**
         * DEFINE ACTIVITY LAUNCHERS
         *
         * It is strongly recommended to register our activity result launchers in onCreate
         * https://stackoverflow.com/a/70215498
         */
        editShoppingListItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (isNull(result.getData())) {
                        return;
                    }
                    Bundle bundle = result.getData().getExtras();

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Ok - we have an updated ingredient!
                        ShopIngredient ingredient = (ShopIngredient) bundle.getSerializable("ingredient");
                        shoppingListController.addEdit(ingredient)
                                        .addOnSuccessListener(unused -> {
                                            makeSnackbar("Edited " + ingredient.getDescription());
                                        })
                                .addOnFailureListener(error -> {
                                    makeSnackbar("Edited " + ingredient.getDescription());
                                    Log.i(TAG + ":editStoreIngredientLauncher", "Received cancelled");
                                });
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // cancelled request - do nothing.
                        Log.i(TAG + ":editStoreIngredientLauncher", "Received cancelled");
                    }
                });

        addShoppingListItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.i(TAG + ":addRecipeResult", "Got bundle");
                    Bundle bundle = result.getData().getExtras();

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        ShopIngredient ingredient = (ShopIngredient) bundle.getSerializable("ingredient");

                        shoppingListController.addEdit(ingredient)
                                .addOnSuccessListener(unused -> {
                                    makeSnackbar("Added " + ingredient.getDescription());
                                    makeConfetti(a);
                                })
                                .addOnFailureListener(error -> {
                                    makeSnackbar( "Edited " + ingredient.getDescription());
                                    Log.i(TAG + ":editStoreIngredientLauncher", "Received cancelled");
                                });
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // cancelled request - do nothing.
                        Log.i(TAG + ":addRecipeResult", "Received cancelled");
                    }
                });
    }

    /**
     * Helper function to make a snackbar
     */
    private void makeSnackbar(String msg ) {
        Snackbar.make(shoppingListView, msg, Snackbar.LENGTH_SHORT)
                .setAction("Ok", view1 -> {})
                .show();
    }

    /**
     * Helper function to make confetti
     */
    private void makeConfetti(Activity a) {
        final KonfettiView confetti = a.findViewById(R.id.confetti_view_shopping_list);

        int[] test = {0,1};
        confetti.getLocationInWindow(test);
        Log.i(TAG + ":makeConfetti", "Confetti location:" + Arrays.toString(test));
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
    }

    /**
     * Sets the FAB in the navigation bar to act as a "add shopping list" button
     *
     * Derived from NavbarFragment
     * @param navigationFAB
     */
    public void setFabListener(FloatingActionButton navigationFAB) {
        navigationFAB.setOnClickListener(v -> {
            Log.i(TAG + ":addRecipe", "Adding recipe!");
            Intent intent = new Intent(getContext(), AddEditShoppingItemActivity.class);
            addShoppingListItemLauncher.launch(intent);
        });
    }


    /**
     *
     *
     */
    public void editShopIngredientFB(ShopIngredient ingredient)
    {
        Log.i("check ", "DONEDONEDONE");
        String id = ingredient.getId();
        if (id == null){
            UUID uuid = UUID.randomUUID();
            id = uuid.toString();
            ingredient.setID(id);
        }
        shoppingCollection.document(id).set(ingredient);
        shopIngredientViewAdapter.notifyDataSetChanged();
    }

//    public void cancelPickUp(int pos)
//    {
//        pickupSwitch.setChecked(false);
//        shopIngredientViewAdapter.notifyDataSetChanged();
//
//    }

}