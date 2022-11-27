package com.androidimpact.app.meal_plan;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidimpact.app.R;
import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.ingredients.StoreIngredient;

import java.util.ArrayList;

/**
 * MealAdapter class
 * This class defines an adapter for MealPlan
 * @version 1.0
 * @author Aneeljyot Alagh
 */
public class MealAdapterAddEdit extends RecyclerView.Adapter<MealAdapterAddEdit.AddEditMealViewHolder> {
    final String TAG = "AddEditMealPlanAdapter";
    private MealPlan mealPlan;
    ArrayList<Recipe> recipeArrayList;
    ArrayList<StoreIngredient> ingredientArrayList;
    ArrayList<Double> recipeServingsArrayList, ingredientServingsArrayList;


    String key;

    /**
     * Constructor for adapter for MealPlan
     * @param mealPlan
     */
    MealAdapterAddEdit(MealPlan mealPlan, String key)
    {
        this.mealPlan = mealPlan;
        this.key = key;
        this.recipeArrayList = mealPlan.getRecipes(key);
        this.ingredientArrayList = mealPlan.getIngredients(key);

        this.recipeServingsArrayList = mealPlan.getRecipeServings(key);
        this.ingredientServingsArrayList = mealPlan.getIngredientServings(key);

    }

    /**
     * Inflate Layout
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public MealAdapterAddEdit.AddEditMealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_meal_plan_add_edit_recipe_ingredient, parent, false);
        return new MealAdapterAddEdit.AddEditMealViewHolder(view);
    }

    /**
     * Set the data to textview from our modal class
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MealAdapterAddEdit.AddEditMealViewHolder holder, int position) {
        if(this.getItemCount() > 0) {
            if(position == 0) {
                String keyDisplay = key.substring(0,1).toUpperCase().concat(key.substring(1));
                String header = keyDisplay + " Meals";
                holder.type.setText(header);
            } else {
                holder.type.setText("\t");
            }
        }

        if(this.getItemCount() > 0) {
            if(position < this.recipeArrayList.size()) {
                holder.item.setText(this.recipeArrayList.get(position).getTitle());
                holder.servings.setText(String.valueOf(this.recipeServingsArrayList.get(position)));
            } else {
                holder.item.setText(this.ingredientArrayList.get(position - this.recipeArrayList.size()).getDescription());
                holder.servings.setText(String.valueOf(this.ingredientServingsArrayList.get(position - this.recipeArrayList.size())));
            }

        }

    }

    /**
     * this method returns the size of recyclerview
     * @return
     */
    @Override
    public int getItemCount() {
        return (this.recipeArrayList.size() + this.ingredientArrayList.size());
    }

    /**
     * View Holder Class to handle Recycler View.
     */
    public class AddEditMealViewHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view and button
        private TextView type, item, servings;

        /**
         * Initializing our text views
         * @param itemView
         */
        public AddEditMealViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.add_edit_meal_name);
            item = itemView.findViewById(R.id.add_edit_meal_item);
            servings = itemView.findViewById(R.id.add_edit_meal_serving);
        }
    }
}
