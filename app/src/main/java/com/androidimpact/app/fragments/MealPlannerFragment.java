package com.androidimpact.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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
import android.widget.Spinner;

import com.androidimpact.app.MealPlan;
import com.androidimpact.app.MealPlanAdapter;
import com.androidimpact.app.R;
import com.androidimpact.app.Recipe;
import com.androidimpact.app.RecipeListAdapter;
import com.androidimpact.app.activities.RecipeAddViewEditActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MealPlannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MealPlannerFragment extends Fragment implements NavbarFragment {
    final String TAG = "MealPlannerFragment";

    // Declare the variables so that you will be able to reference it later.
    RecyclerView mealPlanListView;
    MealPlanAdapter mealPlanAdapter;
    ArrayList<MealPlan> mealPlans;

    // adding recipes to firebase
    FirebaseFirestore db;
    CollectionReference mealPlanCollection;


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
        mealPlans = new ArrayList<>();
        mealPlanAdapter = new com.androidimpact.app.MealPlanAdapter(getContext(), mealPlans);

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
                MealPlan mealPlanToAdd = new MealPlan(doc.getId());
                //MealPlan mealPlanToAdd = doc.toObject(MealPlan.class);
                mealPlans.add(mealPlanToAdd); // Adding the recipe attributes from FireStore
            }

            Log.i(TAG, "Snapshot listener: Added " + mealPlans.size() + " elements");
            for (MealPlan i : mealPlans) {
                Log.i(TAG, "Snapshot listener: Added " + i.getDate() + " to elements");
            }
            mealPlanAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
        });
    }

    /**
     * Sets the FAB in the navigation bar to act as a "add meal planner" button
     *
     * Derived from NavbarFragment
     * @param navigationFAB
     */
    public void setFabListener(FloatingActionButton navigationFAB) {
        return;
    }
}