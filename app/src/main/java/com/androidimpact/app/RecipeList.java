package com.androidimpact.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Context;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * This class defines a recipe list
 * @author Aneeljyot Alagh, Clare Chen
 * @version 1.0
 */
public class RecipeList extends RecyclerView.Adapter<RecipeList.RecipeViewHolder> {

    private ArrayList<Recipe> recipeArrayList;
    private Context context;

    /**
     * Constructor for RecipeList
     */
    public RecipeList(Context context, ArrayList<Recipe> recipeArrayList) {
        this.recipeArrayList = recipeArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeList.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_in_list, parent, false);
        return new RecipeList.RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeList.RecipeViewHolder holder, int position) {
        // Set the data to textview from our modal class.
        Recipe recyclerData = recipeArrayList.get(position);
        holder.recipeTitle.setText(recyclerData.getTitle());
    }

    @Override
    public int getItemCount() {
        // this method returns
        // the size of recyclerview
        return recipeArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view.
        private TextView recipeTitle;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            recipeTitle = itemView.findViewById(R.id.recipe_name);
        }
    }
}

