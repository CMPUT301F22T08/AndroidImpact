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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidimpact.app.ingredients.IngredientStorage;
import com.androidimpact.app.meal_plan.MealPlan;
import com.androidimpact.app.meal_plan.MealPlanListAdapter;
import com.androidimpact.app.R;
import com.androidimpact.app.recipes.RecipeList;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.activities.MealPlanAddEditViewActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MealPlannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @version 1.0
 * @author Vedant Vyas
 */
public class MealPlannerFragment extends Fragment implements NavbarFragment {
    final String TAG = "MealPlannerFragment";

    // Declare the variables so that you will be able to reference it later.
    RecyclerView mealPlanListView;
    MealPlanListAdapter mealPlanAdapter;
    ArrayList<MealPlan> mealPlans;
    RecipeList recipeList;
    IngredientStorage ingredientStorage;

    // adding recipes to firebase
    FirebaseFirestore db;
    CollectionReference mealPlanCollection;

    // using ActivityResultLaunchers
    private ActivityResultLauncher<Intent> addMealPlanLauncher;


    /**
     * Required empty public constructor
     */
    public MealPlannerFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MealPlanner.
     */
    // TODO: Rename and change types and number of parameters
    public static MealPlannerFragment newInstance() {
        MealPlannerFragment fragment = new MealPlannerFragment();
        return fragment;
    }

    /**
     * Runs on the creation of the fragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        mealPlanCollection = db.collection("meal-plan");
    }

    /**
     * Inflate the layout for this fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_planner, container, false);
    }

    /**
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

        // initialize adapters and customList, connect to DB
        mealPlanListView = a.findViewById(R.id.meal_plan_list);
        recipeList = ((MainActivity) a).getRecipeList();
        ingredientStorage = ((MainActivity) a).getIngredientStorage();
        mealPlans = new ArrayList<>();
        mealPlanAdapter = new MealPlanListAdapter(getContext(), mealPlans, recipeList, ingredientStorage);

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mealPlanListView.setLayoutManager(manager);
        mealPlanListView.setAdapter(mealPlanAdapter);

        // drag to delete
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//            /**
//             * This method is called when the item is moved
//             * @param recyclerView
//             * @param viewHolder
//             * @param target
//             * @return
//             */
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            /**
//             * Creates swipe to delete functionality
//             * @param viewHolder
//             * @param direction
//             */
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                // below line is to get the position
//                // of the item at that position.
//                int position = viewHolder.getAdapterPosition();
//                Recipe deletedRecipe = recipeDataList.get(position);
//                String description = deletedRecipe.getTitle();
//
//                OnSuccessListener sl = o -> {
//                    Log.d(TAG, description + " has been deleted successfully!");
//                    Snackbar.make(recipeListView, "Deleted " + description, Snackbar.LENGTH_LONG).show();
//                };
//                OnFailureListener fl = e -> {
//                    Log.d(TAG, description + " could not be deleted!" + e);
//                    Snackbar.make(recipeListView, "Could not delete " + description + "!", Snackbar.LENGTH_LONG).show();
//                };
//                recipeViewAdapter.removeItem(position, sl, fl);
//
//            }
//            // at last we are adding this
//            // to our recycler view.
//        }).attachToRecyclerView(recipeListView);

        // on snapshot listener for the collection
        mealPlanCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            // Clear the old list
            mealPlans.clear();

            if (queryDocumentSnapshots == null) {
                return;
            }
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Map<String, Object> data = doc.getData();
                MealPlan mealPlanToAdd = new MealPlan(doc.getId(), (String) data.get("sortString"));

                String[] keys = {"breakfast", "lunch", "dinner", "snacks"};
                for(String key: keys) {
                    ArrayList<String> recipeIdList = (ArrayList<String>) data.get(key + "Recipes");
                    if(recipeIdList != null) {
                        recipeIdList.forEach(recipeKey -> {
                            mealPlanToAdd.addMealItemRecipe(key, recipeKey, this.recipeList);
                            Log.i("naruto", recipeKey);
                        });
                    }
                }
                mealPlans.add(mealPlanToAdd); // Adding the recipe attributes from FireStore
            }

            Log.i(TAG, "Snapshot listener: Added " + mealPlans.size() + " elements");
            for (MealPlan i : mealPlans) {
                Log.i(TAG, "Snapshot listener: Added " + i.getDate() + " to elements");
            }
            mealPlanAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
        });

        /**
         * DEFINE ACTIVITY LAUNCHERS
         *
         * It is strongly recommended to register our activity result launchers in onCreate
         * https://stackoverflow.com/a/70215498
         */
        addMealPlanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.i(TAG + ":addMealPlanResult", "Got bundle");

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        final KonfettiView confetti = a.findViewById(R.id.meal_planner_confetti_view);

                        int[] test = {0,1};
                        confetti.getLocationInWindow(test);
                        Log.i(TAG, "location:" + Arrays.toString(test));

                        Snackbar.make(mealPlanListView, "Added day meal plan!", Snackbar.LENGTH_LONG).show();
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
                        Log.i(TAG + ":addMealPlanResult", "Received cancelled");
                    }
                });
    }

    /**
     * Sets the FAB in the navigation bar to act as a "add meal planner" button
     *
     * Derived from NavbarFragment
     * @param navigationFAB
     */
    public void setFabListener(FloatingActionButton navigationFAB) {
        navigationFAB.setOnClickListener(v -> {
            Log.i(TAG + ":addMealPlan", "Adding meal plan!");
            Intent intent = new Intent(getContext(), MealPlanAddEditViewActivity.class);
            intent.putExtra("activity_name", "Add meal plan");
            addMealPlanLauncher.launch(intent);
        });
    }
}