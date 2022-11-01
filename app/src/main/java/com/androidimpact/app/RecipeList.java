package com.androidimpact.app;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.squareup.picasso.Picasso;

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

    private StorageReference storageReference;

    /**
     * Constructor for RecipeList
     * @param context           the context for the parent view
     * @param recipeArrayList   the recipes to consider in the RecipeList object
     */
    public RecipeList(Context context, ArrayList<Recipe> recipeArrayList) {
        this.recipeArrayList = recipeArrayList;
        this.context = context;
        this.sortChoices = new String[]{
                "Date Added",
                "Title",
                "Preparation Time",
                "Number of Servings",
                "Recipe Category"
        };
        this.sortIndex = 0;

        // set compare variables
        defaultComparator = Comparator.comparing(Recipe::getDate, String.CASE_INSENSITIVE_ORDER);
        titleComparator = Comparator.comparing(Recipe::getTitle, String.CASE_INSENSITIVE_ORDER);
        prepTimeComparator = Comparator.comparingInt(Recipe::getPrep_time);
        servingsComparator = Comparator.comparingInt(Recipe::getServings);
        categoryComparator = Comparator.comparing(Recipe::getCategory, String.CASE_INSENSITIVE_ORDER);

        storageReference = FirebaseStorage.getInstance().getReference();
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
        holder.recipeServings.setText(String.format(
                this.context.getResources().getString(R.string.recipe_servings_in_list
                ), recyclerData.getPrep_time()));

        // load image for recipe
        String photoURI = recyclerData.getPhoto();
        if(photoURI != null) {
            try {
                // get child in storage
                StorageReference photoRef = storageReference.child("images/" + photoURI);
                photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Got the download URL and put image in corresponding ImageView
                    Picasso.get().load(uri).into(holder.recipeImage);
                }).addOnFailureListener(exception -> {
                    // Log any errors
                    Log.e("Image Not Found", recyclerData.getTitle(), exception);
                });

            }
            catch (Exception exception) {
                // Log any errors
                Log.e("Child Not Found", recyclerData.getTitle(), exception);
            }
        }

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
        private TextView recipeTitle, recipeCategory, recipePrepTime, recipeServings;
        private ImageView recipeImage;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            recipeTitle = itemView.findViewById(R.id.recipe_name);
            recipeCategory = itemView.findViewById(R.id.recipe_category);
            recipePrepTime = itemView.findViewById(R.id.recipe_prep_time);
            recipeServings = itemView.findViewById(R.id.recipe_servings);
            recipeImage = itemView.findViewById(R.id.recipe_image_view);
        }
    }

    /**
     * Return the current sorting choice for the recipe list
     * @return the index of the sorting choices for the user
     */
    public String getSortChoice() {
        return this.sortChoices[this.sortIndex];
    }

    /**
     * Set the sorting choice for the recipe list
     * @param index the index of the sorting choices for the user
     */
    public void setSortChoice(int index) {
        this.sortIndex = index;
    }

    /**
     * Return the sorting choices for the recipe list
     * @return list of available sorting choices
     */
    public static String[] getSortChoices() {
        return sortChoices.clone(); }

    /**
     * This function allows us to sort the recipe list by the user's choice
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

