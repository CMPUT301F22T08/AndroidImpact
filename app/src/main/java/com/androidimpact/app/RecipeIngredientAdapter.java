package com.androidimpact.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.androidimpact.app.activities.RecipeAddViewEditActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

/**
 * RecipeIngredientAdapter class
 * This class connects Recipe with Ingredient View
 * @version 1.0
 * @author Curtis Kan
 */

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.RecipeIngredientHolder>  {
    private ArrayList<Ingredient> ingredients;
    private Context context;


    /**
     * Constructor for adapter to connect Ingredient view with Recipe
     * @param context
     * @param ingredients
     */
    public RecipeIngredientAdapter(Context context, ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
        this.context = context;
    }

    /**
     * Inflate Layout
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecipeIngredientAdapter.RecipeIngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_ingredient_item, parent, false);
        return new RecipeIngredientAdapter.RecipeIngredientHolder(view);
    }

    /**
     * Set the data to textview from our modal class
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeIngredientAdapter.RecipeIngredientHolder holder, int position) {
        Ingredient recyclerData = ingredients.get(position);
        holder.ingredientDescription.setText(recyclerData.getDescription());
        holder.ingredientAmount.setText(String.valueOf(recyclerData.getAmount()));
        holder.ingredientUnit.setText(recyclerData.getUnit());
        holder.ingredientCategory.setText(recyclerData.getCategory());



    }

    /**
     * this method returns the size of recyclerview
     * @return
     */
    @Override
    public int getItemCount() {
        return ingredients.size();
    }


    /**
     * View Holder Class to handle Recycler View.
     */
    public class RecipeIngredientHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view.
        private TextView ingredientDescription, ingredientAmount, ingredientUnit, ingredientCategory;

        /**
         * Initializing our text views
         * @param itemView
         */
        public RecipeIngredientHolder(@NonNull View itemView) {
            super(itemView);
            ingredientDescription = itemView.findViewById(R.id.ingredient_description);
            ingredientAmount = itemView.findViewById(R.id.ingredient_amount);
            ingredientUnit = itemView.findViewById(R.id.ingredient_unit);
            ingredientCategory = itemView.findViewById(R.id.ingredient_category);
        }
    }

}
