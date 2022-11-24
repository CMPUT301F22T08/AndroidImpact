package com.androidimpact.app.shopping_list;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.R;
import com.androidimpact.app.ingredients.Ingredient;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;


/**
 *
 * @author vedantvyas
 */

public class ShopIngredientAdapter extends RecyclerView.Adapter<ShopIngredientAdapter.IngredientViewHolder>{

    private final String TAG = "ShopIngredientViewAdapter";

    // creating a variable for our array list and context.
    private ArrayList<Ingredient> ingredientArrayList;
    private Context mContext;

    private int selected = -1; // initialize no ingredients selected

    public ShopIngredientAdapter(Context mContext, ArrayList<Ingredient> ingredientArrayList) {
        this.ingredientArrayList = ingredientArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ShopIngredientAdapter.IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
        return new ShopIngredientAdapter.IngredientViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ShopIngredientAdapter.IngredientViewHolder holder, int position){

        // Set the data to textview from our modal class.
        Ingredient currentIngredient = ingredientArrayList.get(position);

        Log.i("Test", currentIngredient.getDescription());
        Log.i("Test", String.valueOf(currentIngredient.getAmount()));
        Log.i("Test", currentIngredient.getUnit());


        // set values
        holder.description.setText(currentIngredient.getDescription());
        holder.category.setText(currentIngredient.getCategory());


//
//        // if `selected` is the position, make the expandable section visible
//        if (position == selected) {
//            holder.dropdownToggle.setImageResource(R.drawable.expand_less_white);
//            Log.i(TAG + ":clickedDropdownToggle", "Set item to visible: " + position);
//            holder.expandable.setVisibility(View.VISIBLE);
//        } else {
//            holder.dropdownToggle.setImageResource(R.drawable.expand_more_white);
//            holder.expandable.setVisibility(View.GONE);
//        }

        // set unit
        // since we have to fetch from firebase, we'll use a "loading" state
        String unitStr = holder.res.getString(R.string.shop_ingredient_amount_display, currentIngredient.getAmount(), currentIngredient.getUnit());
        Log.i("String", unitStr);
        holder.amount.setText(unitStr);

//        // OnClick Listener
//        holder.dropdownToggle.setOnClickListener(v -> {
//            Log.i(TAG + ":clickedDropdownToggle", "Clicked dropdown of item at position " + position);
//            clickedItem(position);
//        });
    }




    @Override
    public int getItemCount() {

        return ingredientArrayList.size();
    }

    /**
     * This sets the position of the list item selected
     * @param position
     */
    private void clickedItem(int position) {
        if (selected == position) {
            // deselect
            selected = -1;
        } else {
            selected = position;
        }
        notifyItemChanged(position);
    }

    /**
     * View Holder Class to handle Recycler View.
     * Holds all the view elements necessary for the Adapter
     */
    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        private Resources res;

        // creating a variable for our text view.
        private TextView description;

        // creating a variable for category
        private Chip category;


        private ImageButton dropdownToggle;

        private ConstraintLayout expandable;
        private TextView amount;

        private Switch pickupButton;


        /**
         * initializing our text views
         * @param itemView
         */
        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            res = itemView.getResources();

            //Need to be changed for now
            description = itemView.findViewById(R.id.shop_ingredient_description);
            category = itemView.findViewById(R.id.shop_ingredient_category);
            pickupButton = itemView.findViewById(R.id.shop_ingredient_switch);
            amount = itemView.findViewById(R.id.shop_ingredient_amount);
        }
    }



}
