package com.androidimpact.app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.activities.RecipeAddViewEditActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.squareup.picasso.Picasso;

/**
 * This class defines a recipe list adapter
 * @author Aneeljyot Alagh, Clare Chen
 * @version 1.0
 */
public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {

    // creating a variable for our array list and context.
    private ArrayList<Recipe> recipeArrayList;

    private ArrayList<StoreRecipeEditListener> editListeners = new ArrayList<>();
    RecipeList recipeList;
    private Context context;

    private StorageReference storageReference;

    /**
     * Constructor for RecipeList
     * @param context           the context for the parent view
     * @param recipeArrayList   the recipes to consider in the RecipeListAdapter object
     */
    public RecipeListAdapter(Context context, ArrayList<Recipe> recipeArrayList) {
        this.recipeArrayList = recipeArrayList;
        this.context = context;
        this.recipeList = new RecipeList(recipeArrayList);

        FirebaseStorage fs = FirebaseStorage.getInstance();
        storageReference = fs.getReference();
    }


    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecipeListAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_in_list, parent, false);
        return new RecipeListAdapter.RecipeViewHolder(view);
    }

    /**
     *
     *
     * References:
     * <ul>
     *     <li>For ImageView elements, we used:
     *         <ul>
     *             <li>Geeks for Geeks: How to Retrieve Image from Firebase in Realtime in Android?
     *                 <ul>
     *                     <li>
     *                         URL: https://www.geeksforgeeks.org/how-to-retrieve-image-from-firebase-in-realtime-in-android/
     *                     </li>
     *                     <li>
     *                         Author: surajkeshr023
     *                     </li>
     *                     <li>
     *                         Editor: marcus007
     *                     </li>
     *                     <li>
     *                         Licence: CCBY-SA
     *                     </li>
     *                     <li>
     *                         Use: Inspired use of Picasso library
     *                     </li>
     *                     <li>
     *                         Accessed: 2022-11-01, 2:45 PM
     *                     </li>
     *                 </ul>
     *             </li>
     *             <li>Firebase Storage Docs: Download files with Cloud Storage on Android
     *                 <ul>
     *                     <li>
     *                         URL: https://firebase.google.com/docs/storage/android/download-files
     *                     </li>
     *                     <li>
     *                         Author: Google Inc.
     *                     </li>
     *                     <li>
     *                         Licence: http://www.apache.org/licenses/LICENSE-2.0
     *                     </li>
     *                     <li>
     *                         Use: Motivated adding images to ImageView
     *                     </li>
     *                     <li>
     *                         Accessed: 2022-11-01, 2:50 PM
     *                     </li>
     *                 </ul>
     *             </li>
     *         </ul>
     *     </li>
     * </ul>
     * @param holder
     * @param position
     * Set the data to textview from our modal class
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapter.RecipeViewHolder holder, int position) {
        Recipe recyclerData = recipeArrayList.get(position);
        holder.recipeTitle.setText(recyclerData.getTitle());
        holder.recipeCategory.setText(recyclerData.getCategory());
        Recipe currentRecipe = recipeArrayList.get(position);
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

        holder.editRecipeFAB.setOnClickListener(v -> {
            // execute all listeners
            for (StoreRecipeEditListener listener : editListeners) {
                listener.storeRecipeEditClicked(currentRecipe, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.recipeArrayList.size();
    }

    /**
     * View Holder Class to handle Recycler View.
     */
    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view.
        private TextView recipeTitle, recipeCategory, recipePrepTime, recipeServings;
        private ImageView recipeImage;
        private FloatingActionButton editRecipeFAB;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            recipeTitle = itemView.findViewById(R.id.recipe_name);
            recipeCategory = itemView.findViewById(R.id.recipe_category);
            recipePrepTime = itemView.findViewById(R.id.recipe_prep_time);
            recipeServings = itemView.findViewById(R.id.recipe_servings);
            recipeImage = itemView.findViewById(R.id.recipe_image_view);
            editRecipeFAB = itemView.findViewById(R.id.edit_button);
        }
    }

    /**
     * Set the sorting choice for the recipe list
     * @param index the index of the sorting choices for the user
     */
    public void setSortChoice(int index) {
        recipeList.setSortChoice(index);
    }

    /**
     * This function allows us to sort the recipe list by the user's choice
     */
    public void sortByChoice() {
        recipeList.sortByChoice();
    }

    // OBSERVER PATTERN: this interface lets people subscribe to changes in the StoreIngredientViewAdapter
    // this is because we need the parent activity to react to changes because it has the Context and Activity info
    // https://stackoverflow.com/a/36662886
    public interface StoreRecipeEditListener {
        void storeRecipeEditClicked(Recipe food, int position);
    }

    public void setEditClickListener(StoreRecipeEditListener toAdd) {
        editListeners.add(toAdd);
    }
}

