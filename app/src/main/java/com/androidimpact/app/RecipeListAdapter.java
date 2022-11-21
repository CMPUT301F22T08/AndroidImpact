package com.androidimpact.app;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.activities.RecipeAddEditIngredientActivity;
import com.androidimpact.app.activities.RecipeAddViewEditActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.squareup.picasso.Picasso;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * This class defines a recipe list adapter
 * @author Aneeljyot Alagh, Clare Chen
 * @version 1.0
 */
public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {

    // creating a variable for our array list and context.
    private ArrayList<Recipe> recipeArrayList;

    //private ArrayList<StoreRecipeEditListener> editListeners = new ArrayList<>();
    RecipeList recipeList;
    private Context context;

    // adding recipes to firebase
    FirebaseFirestore db;
    CollectionReference recipeCollection;
    private StorageReference storageReference;
    final String TAG = "RecipeList";

    // using ActivityResultLaunchers
    // not that addRecipeLauncher is defined in RecipeListFragment
    private ActivityResultLauncher<Intent> editRecipeLauncher;

    /**
     * Constructor for RecipeList
     * @param context         the context for the parent view
     * @param recipeArrayList the recipes to consider in the RecipeListAdapter object
     */
    public RecipeListAdapter(Context context, ArrayList<Recipe> recipeArrayList) {
        this.recipeArrayList = recipeArrayList;
        this.context = context;
        this.recipeList = new RecipeList(recipeArrayList);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        //final CollectionReference collectionReference = db.collection("recipes");
        recipeCollection = db.collection("recipes-new");
        FirebaseStorage fs = FirebaseStorage.getInstance();
        storageReference = fs.getReference();


        this.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                sortByChoice();
            }
        });
    }


    /**
     * Create an Adapter
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
     *
     * @param holder
     * @param position Set the data to textview from our modal class
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapter.RecipeViewHolder holder, int position) {
        Recipe recyclerData = recipeArrayList.get(position);
        holder.recipeTitle.setText(recyclerData.getTitle());
        holder.recipeCategory.setText(recyclerData.getCategory());
        Recipe currentRecipe = recipeArrayList.get(position);
        holder.recipePrepTime.setText(String.format(this.context.getResources().getString(R.string.recipe_prep_time_in_list
                ), recyclerData.getPrep_time()));
        holder.recipeServings.setText(String.format(this.context.getResources().getString(R.string.recipe_servings_in_list
                ), recyclerData.getServings()));

        // load image for recipe
        String photoURI = recyclerData.getPhoto();
        if (photoURI != null) {
            try {
                // get child in storage
                StorageReference photoRef = storageReference.child("images/" + photoURI);
                photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Got the download URL and put image in corresponding ImageView
                    Picasso.get().load(uri).into(holder.recipeImage);
                }).addOnFailureListener(exception -> {
                    // Log any errors
                    Log.e("Image Not Found", recyclerData.getTitle(), exception);
                    holder.recipeImage.setImageResource(R.drawable.ic_baseline_dining_24);
                });

            } catch (Exception exception) {
                // Log any errors
                Log.e("Child Not Found", recyclerData.getTitle(), exception);
            }
        } else {
            holder.recipeImage.setImageResource(R.drawable.ic_baseline_dining_24);
        }

        holder.editRecipeFAB.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeAddViewEditActivity.class);
            intent.putExtra("activity_name", "Edit recipe");
            intent.putExtra("recipe", currentRecipe);
            intent.putExtra("isEditing", true);
            context.startActivity(intent);
            notifyItemChanged(position);
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
        //private FloatingActionButton editRecipeFAB;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            recipeTitle = itemView.findViewById(R.id.recipe_name);
            recipeCategory = itemView.findViewById(R.id.recipe_category);
            recipePrepTime = itemView.findViewById(R.id.recipe_prep_time);
            recipeServings = itemView.findViewById(R.id.recipe_servings);
            recipeImage = itemView.findViewById(R.id.recipe_image_view);
            editRecipeFAB = itemView.findViewById(R.id.floatingActionButton);
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

    public Task<Void> removeItem(int position) {

        // this method is called when we swipe our item to right direction.
        // on below line we are getting the item at a particular position.
        Recipe deletedRecipe = recipeArrayList.get(position);
        String id = deletedRecipe.getId();
        String photo = deletedRecipe.getPhoto();

        List<Task<?>> futures = new ArrayList<>();

        CollectionReference ingredients = db.collection(deletedRecipe.getCollectionPath());
        ingredients.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                futures.add(ingredients.document(doc.getId()).delete());
            }
        });

        // delete photo from Firebase Storage
        if (photo != null) {
            futures.add(storageReference.child("images/" + photo).delete()
                    .addOnSuccessListener(aVoid -> Log.i(TAG, "Successfully deleted image from recipe: " + deletedRecipe.getTitle())));
        }

        futures.add(recipeCollection.document(id)
                        .delete()
                        .addOnSuccessListener(unused -> Log.i(TAG, "Successfully deleted recipeDocument: " + deletedRecipe.getTitle())));

        // return the tasks
        return Tasks.whenAll(futures);
    }


}

