package com.androidimpact.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Context;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class defines a recipe list
 * @author Aneeljyot Alagh, Clare Chen
 * @version 1.0
 */
public class RecipeList extends RecyclerView.Adapter<RecipeList.RecipeViewHolder> {

    // creating a variable for our array list and context.
    private ArrayList<Recipe> recipeArrayList;
    private Context context;
    private static String[] sortChoices;
    private int sortIndex;

    public static Comparator<Recipe> defaultComparator, titleComparator, prepTimeComparator, servingsComparator, categoryComparator;

    /**
     * Constructor for RecipeList
     */
    public RecipeList(Context context, ArrayList<Recipe> recipeArrayList) {
        this.recipeArrayList = recipeArrayList;
        this.context = context;
        this.sortChoices = new String[]{
                "Default",
                "Title",
                "Preparation Time",
                "Number of Servings",
                "Recipe Category"
        };
        this.sortIndex = 0;

        // set compare variables
        defaultComparator = Comparator.comparing(Recipe::getDate);
        titleComparator = Comparator.comparing(Recipe::getTitle);
        prepTimeComparator = Comparator.comparingInt(Recipe::getPrep_time);
        servingsComparator = Comparator.comparingInt(Recipe::getServings);
        categoryComparator = Comparator.comparing(Recipe::getCategory);
    }


    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecipeList.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_in_list, parent, false);
        return new RecipeList.RecipeViewHolder(view);
    }

    /**
     * @param holder
     * @param position
     * Set the data to textview from our modal class
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeList.RecipeViewHolder holder, int position) {
        Recipe recyclerData = recipeArrayList.get(position);
        holder.recipeTitle.setText(recyclerData.getTitle());
        holder.recipeCategory.setText(recyclerData.getCategory());
        holder.recipePrepTime.setText(String.format(
                this.context.getResources().getString(R.string.recipe_prep_time_in_list
                ), recyclerData.getPrep_time())
        );
        //holder.recipeComments.setText()

    }

    /**
     * @return
     * this method returns the size of recyclerview
     */
    @Override
    public int getItemCount() {
        return recipeArrayList.size();
    }


    /**
     * View Holder Class to handle Recycler View.
     */
    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view.
        private TextView recipeTitle, recipeCategory, recipePrepTime, recipeComments;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            recipeTitle = itemView.findViewById(R.id.recipe_name);
            recipeCategory = itemView.findViewById(R.id.recipe_category);
            recipePrepTime = itemView.findViewById(R.id.recipe_prep_time);
            recipeComments = itemView.findViewById(R.id.recipe_comment_size);
        }
    }

    /**
     *
     * @return
     */
    public String getSortChoice() {
        return this.sortChoices[this.sortIndex];
    }

    /**
     *
     * @param index
     */
    public void setSortChoice(int index) {
        this.sortIndex = index;
    }

    /**
     *
     * @return
     */
    public static String[] getSortChoices() {
        return sortChoices.clone(); }

    /**
     *
     */
    public void sortByChoice() {
        switch(this.sortIndex) {
            case 0:
                Collections.sort(recipeArrayList, defaultComparator); break;
            case 1:
                Collections.sort(recipeArrayList, titleComparator); break;
            case 2:
                Collections.sort(recipeArrayList, prepTimeComparator); break;
            case 3:
                Collections.sort(recipeArrayList, servingsComparator); break;
            case 4:
                Collections.sort(recipeArrayList, categoryComparator); break;
        }
    }
}

