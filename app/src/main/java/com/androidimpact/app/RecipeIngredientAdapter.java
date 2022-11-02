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

import java.util.ArrayList;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.RecipeIngredientHolder>  {
    private ArrayList<Ingredient> ingredients;
    private Context context;

    public RecipeIngredientAdapter(Context context, ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeIngredientAdapter.RecipeIngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_ingredient_item, parent, false);
        return new RecipeIngredientAdapter.RecipeIngredientHolder(view);
    }

    /**
     * @param holder
     * @param position
     * Set the data to textview from our modal class
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
     * @return
     * this method returns the size of recyclerview
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

        public RecipeIngredientHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            ingredientDescription = itemView.findViewById(R.id.ingredient_description);
            ingredientAmount = itemView.findViewById(R.id.ingredient_amount);
            ingredientUnit = itemView.findViewById(R.id.ingredient_unit);
            ingredientCategory = itemView.findViewById(R.id.ingredient_category);
        }
    }
}
