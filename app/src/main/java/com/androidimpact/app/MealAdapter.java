package com.androidimpact.app;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * MealAdapter class
 * This class defines an adapter for MealPlan
 * @version 1.0
 * @author Aneeljyot Alagh
 */
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    final String TAG = "MealPlanAdapter";
    private MealPlan mealPlan;
    private ArrayList<Recipe> breakfastRecipes, lunchRecipes, dinnerRecipes, snackRecipes;
    private ArrayList<StoreIngredient> breakfastIngredients, lunchIngredients, dinnerIngredients, snackIngredients;

    /**
     * Constructor for adapter for MealPlan
     * @param mealPlan
     */
    MealAdapter(MealPlan mealPlan)
    {
        this.mealPlan = mealPlan;
        this.breakfastRecipes = mealPlan.getRecipes("breakfast");
        this.lunchRecipes = mealPlan.getRecipes("lunch");
        this.dinnerRecipes = mealPlan.getRecipes("dinner");
        this.breakfastIngredients = mealPlan.getIngredients("breakfast");
        this.lunchIngredients = mealPlan.getIngredients("lunch");
        this.dinnerIngredients = mealPlan.getIngredients("dinner");
        this.snackRecipes = mealPlan.getRecipes("snacks");
        this.snackIngredients = mealPlan.getIngredients("snacks");
    }

    /**
     * Inflate Layout
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public MealAdapter.MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_plan_meal, parent, false);
        return new MealAdapter.MealViewHolder(view);
    }

    /**
     * Set the data to textview from our modal class
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MealAdapter.MealViewHolder holder, int position) {
        ArrayList<Recipe> sumArrayList = new ArrayList<>();
        sumArrayList.addAll(this.breakfastRecipes);
        sumArrayList.addAll(this.lunchRecipes);
        //sumArrayList.addAll(this.dinnerRecipes);
        int i = position;
        //for(int i = 0; i < this.breakfastRecipes.size(); i++) {
            if(i==0) {
                holder.type.setText("Breakfast");
            }
            holder.item.setText(sumArrayList.get(i).getTitle());
        //}
        //this.breakfastRecipes.forEach(recipe -> {
        //    holder.item.setText(recipe.getTitle());
        //});
        //MealPlan recyclerData = mealPlan.get(position);
        //holder.date.setText(recyclerData.getDate());
    }

    /**
     * this method returns the size of recyclerview
     * @return
     */
    @Override
    public int getItemCount() {
        return this.mealPlan.size();
    }

    /**
     * View Holder Class to handle Recycler View.
     */
    public class MealViewHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view and button
        private TextView type, item;

        /**
         * Initializing our text views
         * @param itemView
         */
        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.meal_name);
            item = itemView.findViewById(R.id.meal_item);
        }
    }
}
