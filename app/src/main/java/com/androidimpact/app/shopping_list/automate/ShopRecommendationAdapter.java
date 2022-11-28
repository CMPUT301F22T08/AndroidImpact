package com.androidimpact.app.shopping_list.automate;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.fragments.ShopPickUpFragment;
import com.androidimpact.app.shopping_list.ShopIngredient;
import com.androidimpact.app.shopping_list.ShopIngredientAdapter;
import com.androidimpact.app.shopping_list.ShoppingListController;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * This class creates a view adapter for {@link ShopIngredient ShopIngredient} when viewed as a recommendation
 *
 * Similar to {@link ShopIngredientAdapter ShopIngredientAdapter}, but we don't view the pickup toggle
 * @version 1.0
 * @author JOshua Ji
 */

public class ShopRecommendationAdapter extends RecyclerView.Adapter<ShopRecommendationAdapter.ShopRecommendationViewHolder>{

    private final String TAG = "ShopIngredientViewAdapter";

    // creating a variable for our array list and context.
    private ArrayList<ShopIngredient> ingredientArrayList;

    /**
     * Constructor for ShopIngredientAdapter
     * @param data Array list for shopIngredient
     */
    public ShopRecommendationAdapter(ArrayList<ShopIngredient> data) {
        this.ingredientArrayList = data;
    }

    @NonNull
    @Override
    public ShopRecommendationAdapter.ShopRecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_review_item, parent, false);
        return new ShopRecommendationAdapter.ShopRecommendationViewHolder(view);
    }


    /**
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ShopRecommendationViewHolder holder, int position){

        // Set the data to textview from our modal class.
        ShopIngredient currentIngredient = ingredientArrayList.get(position);

        Log.i("Test", currentIngredient.getDescription());
        Log.i("Test", String.valueOf(currentIngredient.getAmount()));
        Log.i("Test", currentIngredient.getUnit());

        // set values
        holder.description.setText(currentIngredient.getDescription());
        holder.category.setText(currentIngredient.getCategory());

        float amt = currentIngredient.getAmountPicked();
        holder.amountPicked.setText(String.valueOf(amt));

        // set unit
        // since we have to fetch from firebase, we'll use a "loading" state
        String unitStr = holder.res.getString(R.string.shop_ingredient_amount_display, currentIngredient.getAmount(), currentIngredient.getUnit());
        Log.i("String", unitStr);
        holder.amount.setText(unitStr);
    }


    /**
     * @return Size of ingredient List
     */
    @Override
    public int getItemCount() {

        return ingredientArrayList.size();
    }

    /**
     * View Holder Class to handle Recycler View.
     * Holds all the view elements necessary for the Adapter
     */
    public class ShopRecommendationViewHolder extends RecyclerView.ViewHolder {
        private final Resources res;

        // creating a variable for our text view.
        private final TextView description;

        // creating a variable for category
        // creating variables for other items in review_ingredient_item (XML)
        private final Chip category;
        private final TextView amount;
        private final TextView amountPicked;

        /**
         * initializing our text views
         * @param itemView
         */
        public ShopRecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            res = itemView.getResources();

            //finding the items in the XML and assigning them to variables
            description = itemView.findViewById(R.id.review_ingredient_description);
            category = itemView.findViewById(R.id.review_ingredient_category);
            amount = itemView.findViewById(R.id.review_ingredient_amount);
            amountPicked = itemView.findViewById(R.id.review_ingredient_amountPick);
        }
    }
}
