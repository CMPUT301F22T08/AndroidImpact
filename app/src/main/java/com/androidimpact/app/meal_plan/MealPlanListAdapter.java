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
 * MealPlanListAdapter class
 * This class defines an adapter for MealPlanList
 * @version 1.0
 * @author Aneeljyot Alagh
 */

public class MealPlanListAdapter extends RecyclerView.Adapter<MealPlanListAdapter.MealPlanHolder>  {
    final String TAG = "MealPlanListAdapter";

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
    public MealPlanListAdapter(Context context, ArrayList<MealPlan> mealPlans, RecipeList recipeList, ArrayList<StoreIngredient> ingredients) {
        this.mealPlans = mealPlans;
        this.mealPlanList = new MealPlanList(this.mealPlans/*, recipeList, ingredients*/);
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
        });
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

        String[] keys = {"breakfast", "lunch", "dinner", "snacks"};
        RecyclerView[] recyclerViews = {holder.mealsListBreakfast, holder.mealsListLunch, holder.mealsListDinner, holder.mealsListSnacks};

        for(int i = 0; i < keys.length; i++) {
            MealAdapter adapter = new MealAdapter(recyclerData, keys[i]);
            LinearLayoutManager manager = new LinearLayoutManager(context);
            recyclerViews[i].setLayoutManager(manager);
            recyclerViews[i].setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }



        holder.mealPlanEditButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealPlanAddEditViewActivity.class);
            intent.putExtra("activity_name", "Edit meal plan");
            //intent.putExtra("recipe", currentRecipe);
            intent.putExtra("isEditing", true);
            context.startActivity(intent);
            notifyItemChanged(position);
        });
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
     * This function allows us to sort the meal plan list by date
     */
    public void sortByChoice() {
        this.mealPlanList.sortByChoice();
    }


    /**
     * View Holder Class to handle Recycler View.
     */
    public class MealPlanHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view and button
        private TextView date;
        private FloatingActionButton mealPlanEditButton;
        private RecyclerView mealsListBreakfast, mealsListLunch, mealsListDinner, mealsListSnacks;

        /**
         * Initializing our text views
         * @param itemView
         */
        public MealPlanHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.meal_plan_title);
            mealsListBreakfast = itemView.findViewById(R.id.meals_list_breakfast);
            mealsListLunch = itemView.findViewById(R.id.meals_list_lunch);
            mealsListDinner = itemView.findViewById(R.id.meals_list_dinner);
            mealsListSnacks = itemView.findViewById(R.id.meals_list_snacks);
            mealPlanEditButton = itemView.findViewById(R.id.edit_button);
        }
    }

}
