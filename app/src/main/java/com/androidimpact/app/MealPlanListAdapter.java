package com.androidimpact.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * MealPlanListAdapter class
 * This class defines an adapter for MealPlan
 * @version 1.0
 * @author Aneeljyot Alagh
 */

public class MealPlanListAdapter extends RecyclerView.Adapter<MealPlanListAdapter.MealPlanHolder>  {
    final String TAG = "MealPlanListAdapter";

    private ArrayList<MealPlan> mealPlans;
    private Context context;

    // adding recipes to firebase
    FirebaseFirestore db;
    CollectionReference mealPlanCollection;

    /**
     * Constructor for adapter for MealPlan
     * @param context
     * @param mealPlans
     */
    public MealPlanListAdapter(Context context, ArrayList<MealPlan> mealPlans) {
        this.mealPlans = mealPlans;
        this.context = context;

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        mealPlanCollection = db.collection("meal-plan");
    }

    /**
     * Inflate Layout
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public MealPlanListAdapter.MealPlanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_plan_day, parent, false);
        return new MealPlanListAdapter.MealPlanHolder(view);
    }

    /**
     * Set the data to textview from our modal class
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MealPlanListAdapter.MealPlanHolder holder, int position) {
        MealPlan recyclerData = mealPlans.get(position);
        holder.date.setText(recyclerData.getDate());
    }

    /**
     * this method returns the size of recyclerview
     * @return
     */
    @Override
    public int getItemCount() {
        return mealPlans.size();
    }


    /**
     * View Holder Class to handle Recycler View.
     */
    public class MealPlanHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view and button
        private TextView date;
        private FloatingActionButton mealPlanEditButton;

        /**
         * Initializing our text views
         * @param itemView
         */
        public MealPlanHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.day_title);
            mealPlanEditButton = itemView.findViewById(R.id.edit_button);
        }
    }

}
