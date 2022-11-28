package com.androidimpact.app.meal_plan;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * MealAdapter class
 * This class defines an adapter for MealPlan
 * @version 1.0
 * @author Clare Chen
 */
public class MealAdapterAddEdit extends RecyclerView.Adapter<MealAdapterAddEdit.AddEditMealViewHolder> {
    final String TAG = "AddEditMealPlanAdapter";
    ArrayList<String> recipeArrayList, ingredientArrayList;
    ArrayList<Double> recipeServingsArrayList, ingredientServingsArrayList;
    Context context;


    /**
     *  Constructor for MealAdapter Add Edit
     * @param context
     * @param recipeArrayList (ArrayList<String>)
     * @param ingredientArrayList (ArrayList<String>)
     * @param recipeServingsArrayList  (ArrayList<Double>)
     * @param ingredientServingsArrayList   (ArrayList<Double>)
     */
    public MealAdapterAddEdit(Context context, ArrayList<String> recipeArrayList, ArrayList<String> ingredientArrayList, ArrayList<Double> recipeServingsArrayList, ArrayList<Double> ingredientServingsArrayList/*, String key*/)
    {
        //this.key = key;
        this.context = context;
        this.recipeArrayList = recipeArrayList;
        this.ingredientArrayList = ingredientArrayList;

        this.recipeServingsArrayList = recipeServingsArrayList;
        this.ingredientServingsArrayList = ingredientServingsArrayList;

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
            if(position < this.recipeArrayList.size()) {
                holder.item.setText(this.recipeArrayList.get(position));
                holder.servings.setText(String.valueOf(this.recipeServingsArrayList.get(position)));
            } else {
                holder.item.setText(this.ingredientArrayList.get(position - this.recipeArrayList.size()));
                holder.servings.setText(String.valueOf(this.ingredientServingsArrayList.get(position - this.recipeArrayList.size())));
            }
    }

    /**
     * this method returns the size of recyclerview
     * @return
     */
    @Override
    public int getItemCount() {
        int size = 0;
        try
        {
            size = this.recipeArrayList.size() + this.ingredientArrayList.size();
        }
        catch (Exception e)
        {
            size = Integer.MAX_VALUE;
            pushSnackBarToContext("Item Count is too big, so size is capped");

        }
        return size;

    }

    /**
     * View Holder Class to handle Recycler View.
     */
    public class AddEditMealViewHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view and button
        private TextView item, servings;

        /**
         * Initializing our text views
         * @param itemView
         */
        public AddEditMealViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.add_edit_meal_item);
            servings = itemView.findViewById(R.id.add_edit_meal_serving);
        }
    }

    /**
     * Creates a SnackBar that pup-up on the screen
     * @param s (String) - The text to be shown in the SnackBar
     */
    private void pushSnackBarToContext(String s) {
        Snackbar.make(((MainActivity)context).findViewById(R.id.nav_fragment), s, Snackbar.LENGTH_LONG)
                .setAction("OK", (v)->{}).show();
    }

}
