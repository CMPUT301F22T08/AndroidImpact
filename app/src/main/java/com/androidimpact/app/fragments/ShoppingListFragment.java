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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.shopping_list.ShopIngredient;
import com.androidimpact.app.shopping_list.AddEditShoppingItemActivity;
import com.androidimpact.app.shopping_list.ShopIngredientAdapter;
import com.androidimpact.app.shopping_list.ShoppingListController;
import com.androidimpact.app.shopping_list.automate.ReviewRecommendations;
import com.androidimpact.app.shopping_list.automate.ShoppingListAutomator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;

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

    // This class lets us automate the shopping list fragment, pulling it from Firebase
    ShoppingListAutomator shoppingListAutomator;
    // for ShoppingListAutomator to work, we need an Executor instance
    // this class helps us manage background threads. ShoppingListAutomator basically does one long background task!
    private final Executor executor;
    private Button automateBtn;

    private static ShoppingListFragment instance;

    RecyclerView shoppingListView;
    ShopIngredientAdapter shopIngredientViewAdapter;
    ShoppingListController shoppingListController;

    String userPath;
    Spinner sortIngredientSpinner;
    String[] sortingChoices;
    TextView sortText;

    Switch pickupSwitch;
    FloatingActionButton moveFAB;

    // use ActivityResultLaunchers to go to different activities
    // this is defined in onViewCreated, see the comment where we initialize it
    private ActivityResultLauncher<Intent> addShoppingListItemLauncher;
    private ActivityResultLauncher<Intent> editShoppingListItemLauncher;
    private ActivityResultLauncher<Intent> reviewAutomatedRecommendations;

    /**
     * public constructor. This is nonempty because it helps us initialize the Executor
     */
    public ShoppingListFragment(Executor executor) {
        this.executor = executor;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShoppingList.
     */
    public static ShoppingListFragment newInstance(Executor executor) {
        if (instance == null) {
            ShoppingListFragment fragment = new ShoppingListFragment(executor);
            instance = fragment;
        }
        return instance;
    }

    /**
     * Adds an instance of the {@link ShoppingListAutomator ShoppingListAutomator} class.
     * <br /><br />
     * Note: we cannot initialize a ShoppingListAutomator inside the fragment, since it needs
     * access to {@link com.androidimpact.app.recipes.RecipeController RecipeController} and
     * {@link com.androidimpact.app.meal_plan.MealPlanController MealPlanController}
     */
    public void addAutomator(ShoppingListAutomator shoppingListAutomator) {
        this.shoppingListAutomator = shoppingListAutomator;
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


        super.onCreate(savedInstanceState);

        Activity a = getActivity();

        if (a == null) {
            Log.i(TAG + ":onViewCreated", "Fragment is not associated with an activity!");
            return;
        }

        userPath = ((MainActivity) a).getUserDataPath();

        // initialize adapters and customList
        shoppingListView = a.findViewById(R.id.shopping_listview);
        pickupSwitch = a.findViewById(R.id.shop_ingredient_switch);

        shoppingListController = ((MainActivity) a).getShoppingListController();
        moveFAB  = a.findViewById(R.id.move_fab);
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
                shoppingListController.delete(position);
            }
            // finally, we add this to our recycler view.
        }).attachToRecyclerView(shoppingListView);

        // on snapshot listener for the collection
        shoppingListController.addDataUpdateSnapshotListener(shopIngredientViewAdapter);


        moveFAB.setOnClickListener(v -> {
            ArrayList<ShopIngredient> moveIngredientList = new ArrayList<>();


            ArrayList<ShopIngredient> tempList = shoppingListController.getData();


            for (int i = 0; i < tempList.size(); ++i)
            {
                ShopIngredient moveIngredient = tempList.get(i);

                if (moveIngredient.getAmountPicked() != 0)
                {
                    moveIngredientList.add(moveIngredient);
                    Log.i("Adding item to be moved", moveIngredient.getDescription());
                }
            }

            //call a function in main activity that switches the shoppingListFragment to IngredientStorageFragment and
            //add the ingredients from List to the the Ingredient Storage
            ((MainActivity) a).addShopListToShopIngredient(moveIngredientList);

        });


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
                shoppingListController.sortData(i);
                shopIngredientViewAdapter.notifyDataSetChanged();
            }

            /**
             * This function ensures default sorting if no other sorting selected
             * @param adapterView   The adapterView that does not contain any user selection
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                shoppingListController.sortData(0);
                shopIngredientViewAdapter.notifyDataSetChanged();
            }
        });

        shoppingListController.addDataUpdateSnapshotListener(shopIngredientViewAdapter);

        // EVENT LISTENERS
        userPath = ((MainActivity) a).getUserDataPath();
        shopIngredientViewAdapter.setEditClickListener((food, position) -> {
            // when a shop ingredient is clicked, we edit
            // yeah, it's a bit different than ingredient and recipe
            Log.i(TAG + ":addRecipe", "Adding recipe!");
            Intent intent = new Intent(getContext(), AddEditShoppingItemActivity.class);
            intent.putExtra("ingredient", food);
            intent.putExtra("adding", false);
            intent.putExtra("data-path", userPath);
            editShoppingListItemLauncher.launch(intent);
        });

        shopIngredientViewAdapter.setEditToggleListener((food, checked) -> {


            Log.i("Adding Toggle listener", food.getDescription());
            if (checked)
            {
                if (food.getAmountPicked() == 0)
                {
                    ShopPickUpFragment ff1 = ShopPickUpFragment.newInstance(food);
                    MainActivity main = (MainActivity)getActivity();

                    ff1.show(main.getSupportFragmentManager(), "ADD_FOOD");
                }
            }
            else {
                if (food.getAmountPicked() != 0)
                    {
                        food.setAmountPicked(0);
                        shoppingListController.addEdit(food);
                    }
            }



        });

        // somehow, android:onClick doesn't work in fragments, so I have to setOnClickLIstener here
        // https://stackoverflow.com/a/4153842
        automateBtn = a.findViewById(R.id.automateShoppingListBtn);
        automateBtn.setOnClickListener(v -> {
            try {
                // tried to make this a task, but since it's not running on the current thread, I couldn't make it work
                // so we have to pass in listeners instead of adding addSuccessListeners
                shoppingListAutomator.automateShoppingList(
                        shopIngredients -> {
                            Log.i(TAG + ":automateShoppingList", "Automate Shopping List Success! Found " + shopIngredients.size() + " elements");
                            if (shopIngredients.size() > 0) {
                                // go to reviewRecommendations activity
                                Intent intent = new Intent(getContext(), ReviewRecommendations.class);
                                intent.putExtra("ingredients", shopIngredients);
                                reviewAutomatedRecommendations.launch(intent);
                            } else {
                                Toast.makeText(getActivity(), "Ingredient Storage is sufficient for the Meal Plan!", Toast.LENGTH_SHORT).show();
                            }
                        },
                        e -> Log.i(TAG, "Error running shoppingListAutomator!", e));
            } catch (Exception e) {
                Log.i(TAG, "Error running shoppingListAutomator!", e);
            }
        });

        /**
         * DEFINE ACTIVITY LAUNCHERS
         *
         * It is strongly recommended to register our activity result launchers in onCreate
         * https://stackoverflow.com/a/70215498
         */
        reviewAutomatedRecommendations = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (isNull(result.getData())) {
                        return;
                    }

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        int size = shoppingListAutomator.getRecommendations().size();
                        // it's good! add all the ingredients in the automator to shoppingListContrller
                        shoppingListController.addArray(shoppingListAutomator.getRecommendations())
                                .addOnSuccessListener(unused -> {
                                    makeSnackbar("Added " + size + " recommendations!");
                                })
                                .addOnFailureListener(error -> {
                                    makeSnackbar("Error: Failed to add recommendations!");
                                    Log.i(TAG + ":editStoreIngredientLauncher", "Received cancelled");
                                });
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // cancelled request - do nothing.
                        Log.i(TAG + ":editStoreIngredientLauncher", "Received cancelled");
                    }
                });

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
            intent.putExtra("data-path", userPath);
            intent.putExtra("adding", true);
            addShoppingListItemLauncher.launch(intent);
        });
    }

    /**
     *
     *
     */
    public void cancelToggleDialog(ShopIngredient ingredient)
    {
        Log.i("check ", "Editing Shop Ingredient to zero");
        shoppingListController.addEdit(ingredient);

        shopIngredientViewAdapter.notifyDataSetChanged();

    }


}