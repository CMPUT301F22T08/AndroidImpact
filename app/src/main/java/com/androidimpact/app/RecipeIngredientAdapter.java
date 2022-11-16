package com.androidimpact.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.activities.RecipeAddEditIngredientActivity;
import com.androidimpact.app.activities.RecipeAddViewEditActivity;
import com.androidimpact.app.unit.Unit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * RecipeIngredientAdapter class
 * This class connects Recipe with Ingredient View
 * @version 1.0
 * @author Curtis Kan
 */

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.RecipeIngredientHolder>  {
    final String TAG = "RecipeIngredientAdapter";

    private ArrayList<RecipeIngredient> ingredients;
    private Context context;
    private ArrayList<RecipeIngredientAdapter.StoreRecipeIngredientEdit> editListeners = new ArrayList<>();


    /**
     * Constructor for adapter to connect Ingredient view with Recipe
     * @param context
     * @param ingredients
     */
    public RecipeIngredientAdapter(Context context, ArrayList<RecipeIngredient> ingredients) {
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
        holder.ingredientCategory.setText(recyclerData.getCategory());
        holder.ingredientEditButton.setOnClickListener(v -> {
            // execute all listeners
            for (RecipeIngredientAdapter.StoreRecipeIngredientEdit listener : editListeners) {
                listener.storeRecipeIngredientEdit(recyclerData, position);
            }
        });

        // now, retrieve "unit" from firebase
        // since this takes a while, we first set a loading state
        holder.ingredientUnit.setText("loading...");
        DocumentRetrievalListener<Unit> getUnitListener = new DocumentRetrievalListener<>() {
            @Override
            public void onSuccess(Unit data) {
                String unitStr = context.getString(R.string.store_ingredient_amount_display, recyclerData.getAmount(), data.getUnit());
                holder.ingredientUnit.setText(unitStr);
            }

            @Override
            public void onNullDocument() {
                holder.ingredientUnit.setText("NoDoc!");
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "Cached get failed: ", e);
                holder.ingredientUnit.setText("Failed!");
            }
        };
        recyclerData.getUnitAsync(getUnitListener);

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

        // creating a variable for our text view and button
        private TextView ingredientDescription, ingredientAmount, ingredientUnit, ingredientCategory;
        private FloatingActionButton ingredientEditButton;

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
            ingredientEditButton = itemView.findViewById(R.id.edit_button);
        }
    }

    public interface StoreRecipeIngredientEdit {
        void storeRecipeIngredientEdit(Ingredient ingredient, int position);
    }

    /**
     * Edit button listener
     * @param toAdd
     */
    public void setEditClickListener(StoreRecipeIngredientEdit toAdd) {
        editListeners.add(toAdd);
    }

}
