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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidimpact.app.ingredients.IngredientStorage;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.meal_plan.MealPlan;
import com.androidimpact.app.meal_plan.MealPlanController;
import com.androidimpact.app.meal_plan.MealPlanListAdapter;
import com.androidimpact.app.R;
import com.androidimpact.app.meal_plan.MealPlanListAddEditAdapter;
import com.androidimpact.app.recipes.RecipeList;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.activities.MealPlanAddEditViewActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
 * @author Aneeljyot Alagh and Vedant Vyas
 */
public class MealPlannerFragment extends Fragment implements NavbarFragment {
    final String TAG = "MealPlannerFragment";

    // Declare the variables so that you will be able to reference it later.
    RecyclerView mealPlanListView;
    MealPlanListAdapter mealPlanAdapter;
    ArrayList<MealPlan> mealPlans;
    MealPlanController mealPlanController;

    String dataPath;

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
     * Fragment Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created.
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

        // initialize controller
        mealPlanController = ((MainActivity) a).getMealPlanController();

        // initialize data path
        dataPath = ((MainActivity) a).getUserDataPath();

        // initialize adapters and customList
        mealPlanListView = a.findViewById(R.id.meal_plan_list);
        mealPlanAdapter = new MealPlanListAdapter(getContext()/*, dataPath*/, mealPlanController.getData());

        // below line is to set layout manager for our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mealPlanListView.setLayoutManager(manager);
        mealPlanListView.setAdapter(mealPlanAdapter);


        // drag to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            /**
             * This method is called when the item is moved
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
             * Creates swipe to delete functionality
             * @param viewHolder
             * @param direction
             */
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // below line is to get the position
                // of the item at that position.
                int position = viewHolder.getAdapterPosition();
                mealPlanController.delete(position, mealPlanListView, mealPlanAdapter);

            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(mealPlanListView);

        mealPlanController.refresh(mealPlanAdapter);
        mealPlanController.addDataUpdateSnapshotListener(mealPlanAdapter);

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

                        Snackbar.make(mealPlanListView, "Added day meal plan!", Snackbar.LENGTH_LONG)
                                .setAction("Ok", view1 -> {})
                                .show();
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
            intent.putExtra("activity_name", "Add Meal Plan");
            intent.putExtra("data-path", ((MainActivity) getActivity()).getUserDataPath());
            refreshMealItems();
            addMealPlanLauncher.launch(intent);
        });
    }

    /**
     * Updates the meal plan when recipes/ingredients change.
     */
    public void refreshMealItems() {
        mealPlanAdapter.notifyDataSetChanged();
    }
}