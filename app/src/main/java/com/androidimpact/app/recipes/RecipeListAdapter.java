package com.androidimpact.app.recipes;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.activities.RecipeAddViewEditActivity;
import com.androidimpact.app.meal_plan.OnSelectInterface;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.squareup.picasso.Picasso;

/**
 * This class defines a recipe list adapter
 * @author Aneeljyot Alagh, Clare Chen
 * @version 1.0
 */
public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {
    final String TAG = "RecipeList";

    private RecipeController recipeController;
    private Context context;
    private boolean isSelection;
    private OnSelectInterface onSelectInterface;



    /**
     * Constructor for RecipeList
     * @param context         the context for the parent view
     * @param recipeController the recipes to consider in the RecipeListAdapter object
     */
    public RecipeListAdapter(Context context, RecipeController recipeController) {
        this.context = context;
        this.recipeController = recipeController;
        this.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                recipeController.sortData();
            }
        });
        isSelection = false;
        this.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                recipeController.sortData();
            }
        });
    }

    /**
     * Constructor for RecipeList
     * @param context         the context for the parent view
     * @param recipeList the recipes to consider in the RecipeListAdapter object
     */
    public RecipeListAdapter(Context context, RecipeList recipeList, OnSelectInterface onSelectInterface) {
        this(context, ((MainActivity) context).getRecipeController());
        this.isSelection = true;
        this.onSelectInterface = onSelectInterface;
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
     *                     <li>URL: https://www.geeksforgeeks.org/how-to-retrieve-image-from-firebase-in-realtime-in-android/</li>
     *                     <li>Author: surajkeshr023</li>
     *                     <li>Editor: marcus007</li>
     *                     <li>Licence: CCBY-SA</li>
     *                     <li>Use: Inspired use of Picasso library</li>
     *                     <li>Accessed: 2022-11-01, 2:45 PM
     *                     </li>
     *                 </ul>
     *             </li>
     *             <li>Firebase Storage Docs: Download files with Cloud Storage on Android
     *                 <ul>
     *                     <li>URL: https://firebase.google.com/docs/storage/android/download-files</li>
     *                     <li>Author: Google Inc.</li>
     *                     <li>Licence: http://www.apache.org/licenses/LICENSE-2.0</li>
     *                     <li>Use: Motivated adding images to ImageView</li>
     *                     <li>Accessed: 2022-11-01, 2:50 PM</li>
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
        Recipe recipe = recipeController.get(position);
        holder.recipeTitle.setText(recipe.getTitle());
        holder.recipeCategory.setText(recipe.getCategory());
        holder.recipePrepTime.setText(String.format(this.context.getResources().getString(R.string.recipe_prep_time_in_list
                ), recipe.getPrep_time()));
        holder.recipeServings.setText(String.format(this.context.getResources().getString(R.string.recipe_servings_in_list
                ), recipe.getServings()));


        OnSuccessListener successListener = o -> Picasso.get().load((Uri) o).into(holder.recipeImage);
        OnFailureListener failureListener = e -> {
            Log.e(TAG, "Image Not Found: "+recipe.getTitle(), e);
            holder.recipeImage.setImageResource(R.drawable.ic_baseline_dining_24);
        };

        try {
            String photoURI = recipeController.get(position).getPhoto();
            recipeController.processPhoto(photoURI, successListener, failureListener);
        } catch (Exception e) {
            Log.e(TAG, "Child Not Found: "+recipe.getTitle(), e);
        }

        if(this.isSelection) {
            holder.editRecipeFAB.setVisibility(View.GONE);
            holder.container.setOnClickListener(view -> {
                this.onSelectInterface.selectItem(position);

            });
        } else {
            holder.editRecipeFAB.setOnClickListener(v -> {
                Intent intent = new Intent(context, RecipeAddViewEditActivity.class);
                intent.putExtra("activity_name", "Edit recipe");
                intent.putExtra("recipe", recipe);
                intent.putExtra("isEditing", true);
                context.startActivity(intent);
                notifyItemChanged(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.recipeController.size();
    }

    public void sortData(int i) {
        recipeController.sortData(i);
    }
    public void sortData() {
        recipeController.sortData();
    }

    /**
     * View Holder Class to handle Recycler View.
     */
    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view.
        private TextView recipeTitle, recipeCategory, recipePrepTime, recipeServings;
        private ImageView recipeImage;
        private FloatingActionButton editRecipeFAB;
        private ConstraintLayout container;
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
            container = itemView.findViewById(R.id.recipe_container);
        }
    }
}

