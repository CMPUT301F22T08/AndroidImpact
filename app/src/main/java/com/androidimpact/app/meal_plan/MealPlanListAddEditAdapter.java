package com.androidimpact.app.meal_plan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MealPlanAddEditViewActivity;
import com.androidimpact.app.activities.RecipeAddViewEditActivity;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.recipes.RecipeList;
import com.androidimpact.app.ingredients.IngredientStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * MealPlanListAddEditAdapter class
 * This class defines an adapter for MealPlanList
 * @version 1.0
 * @author Clare Chen
 */

public class MealPlanListAddEditAdapter /*extends RecyclerView.Adapter<MealPlanListAddEditAdapter.MealPlanHolder>*/  {
    final String TAG = "MealPlanListAddEditAdapter";

    private ArrayList<MealPlan> mealPlans;
    private Context context;
    private MealPlanList mealPlanList;

    // adding recipes to firebase
    FirebaseFirestore db;
    CollectionReference mealPlanCollection;

    /**
     * Constructor for adapter for MealPlanList
     * @param context
     * @param mealPlans
     */
    public MealPlanListAddEditAdapter(Context context, ArrayList<MealPlan> mealPlans) {
        /*this.mealPlans = mealPlans;
        this.mealPlanList = new MealPlanList(this.mealPlans*//*, recipeList, ingredients*//*);
        this.context = context;

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        mealPlanCollection = db.collection("meal-plan");

        this.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                sortByChoice();
            }
        });*/
    }

    /**
     * Inflate Layout
     * @param parent
     * @param viewType
     * @return
     */
    /*@NonNull
    @Override
    public MealPlanListAddEditAdapter.MealPlanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_meal_plan_add_edit_view, parent, false);
        return new MealPlanListAddEditAdapter.MealPlanHolder(view);
    }*/

    /**
     * Set the data to textview from our modal class
     * @param holder
     * @param position
     */
    /*@Override
    public void onBindViewHolder(@NonNull MealPlanListAddEditAdapter.MealPlanHolder holder, int position) {
        MealPlan recyclerData = mealPlans.get(position);

        String[] keys = {"breakfast", "lunch", "dinner", "snacks"};
        RecyclerView[] recyclerViews = {holder.mealsListBreakfast, holder.mealsListLunch, holder.mealsListDinner, holder.mealsListSnacks};

        for(int i = 0; i < keys.length; i++) {
            MealAdapterAddEdit adapter = new MealAdapterAddEdit(recyclerData, keys[i]);
            LinearLayoutManager manager = new LinearLayoutManager(context);
            recyclerViews[i].setLayoutManager(manager);
            recyclerViews[i].setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }



    }*/

    /**
     * this method returns the size of recyclerview
     * @return
     */
    /*@Override
    public int getItemCount() {
        return mealPlans.size();
    }*/

    /**
     * This function allows us to sort the meal plan list by date
     */
    /*public void sortByChoice() {
        this.mealPlanList.sortByChoice();
    }*/


    /**
     * View Holder Class to handle Recycler View.
     */
    /*public class MealPlanHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view and button
        private RecyclerView mealsListBreakfast, mealsListLunch, mealsListDinner, mealsListSnacks;

        *//**
         * Initializing our text views
         * @param itemView
         *//*
        public MealPlanHolder(@NonNull View itemView) {
            super(itemView);
            mealsListBreakfast = itemView.findViewById(R.id.breakfast_meals);
            mealsListLunch = itemView.findViewById(R.id.lunch_meals);
            mealsListDinner = itemView.findViewById(R.id.dinner_meals);
            mealsListSnacks = itemView.findViewById(R.id.snack_meals);
        }
    }*/

}
