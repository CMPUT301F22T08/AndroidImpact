package com.androidimpact.app.shopping_list;

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
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.ingredients.StoreIngredientViewAdapter;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.fragments.ShopPickUpFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


/**
 * This class creates a view adapter for Shop Ingredient
 * @version 1.0
 * @author vedantvyas
 */

public class ShopIngredientAdapter extends RecyclerView.Adapter<ShopIngredientAdapter.IngredientViewHolder>{

    private final String TAG = "ShopIngredientViewAdapter";

    // creating a variable for our array list and context.
    private ArrayList<ShopIngredient> ingredientArrayList;


    private Context mContext;

    private int selected = -1; // initialize no ingredients selected

    // functions that subscribe for edit callbacks
    private ArrayList<ShopIngredientClickListener> clickListeners = new ArrayList<>();

    // functions that subscribe for edit callbacks
    private ArrayList<ShopIngredientToggleListener> toggleListeners = new ArrayList<>();

    ShopIngredientToggleListener toggleListener;

    /**
     * Constructor for ShopIngredientAdapter
     * @param mContext
     * @param shoppingListController  Controller Class for Shopping List
     */
    public ShopIngredientAdapter(Context mContext, ShoppingListController shoppingListController) {
        this.ingredientArrayList = shoppingListController.getData();
        this.mContext = mContext;
    }

    public ShopIngredientAdapter(Context mContext, ArrayList<ShopIngredient> data) {
        this.ingredientArrayList = data;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ShopIngredientAdapter.IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
        return new ShopIngredientAdapter.IngredientViewHolder(view);
    }


    /**
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ShopIngredientAdapter.IngredientViewHolder holder, int position){

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

        // if amountPicked is zero, then change switch to false
        if (amt == 0)
        {
            holder.pickupButton.setChecked(false);
            Log.i("pickFalse", "False");
        }
        else    // else change switch to true
        {
            holder.pickupButton.setChecked(true);
            Log.i("pickTrue", "True");
        }




        //Adds listener for switch button for every item
        holder.pickupButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


               toggleListener.shopIngredientToggled(currentIngredient, isChecked);
               Log.i("pickup toggle", currentIngredient.getDescription());


            }
        });

        // set unit
        // since we have to fetch from firebase, we'll use a "loading" state
        String unitStr = holder.res.getString(R.string.shop_ingredient_amount_display, currentIngredient.getAmount(), currentIngredient.getUnit());
        Log.i("String", unitStr);
        holder.amount.setText(unitStr);


        //Adding Listeners for list items
        holder.root.setOnClickListener(v -> {
            for (ShopIngredientClickListener l : clickListeners) {
                l.shopIngredientClicked(currentIngredient, position);
            }
        });
    }


    /**
     * @return Size of ingredient List
     */
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
        // creating variables for other items in Shop_ingredient_item (XML)
        private Chip category;
        private ImageButton dropdownToggle;
        private ConstraintLayout root;
        private TextView amount;
        private TextView amountPicked;

        private Switch pickupButton;

        private FloatingActionButton moveFAB;


        /**
         * initializing our text views
         * @param itemView
         */
        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            res = itemView.getResources();

            //finding the items in the XML and assigning them to variables
            description = itemView.findViewById(R.id.shop_ingredient_description);
            category = itemView.findViewById(R.id.shop_ingredient_category);
            pickupButton = itemView.findViewById(R.id.shop_ingredient_switch);
            amount = itemView.findViewById(R.id.shop_ingredient_amount);
            amountPicked = itemView.findViewById(R.id.shop_ingredient_amountPick);
            root = itemView.findViewById(R.id.shop_ingredient_item_root);
            moveFAB = itemView.findViewById(R.id.move_fab);
        }
    }

    /**
     * this interface lets people subscribe to clicks in every shopIngredient
     * this is because we need the parent activity to react to changes because it has the Context and Activity info
     * https://stackoverflow.com/a/36662886
     */
    public interface ShopIngredientClickListener {
        void shopIngredientClicked(ShopIngredient food, int position);
    }

    /**
     *
     */
    public interface ShopIngredientToggleListener {
        void shopIngredientToggled(ShopIngredient food, boolean is_checked);
    }

    /**
     *
     */
    public void setEditToggleListener(ShopIngredientToggleListener toAdd)
    {
        toggleListeners.add(toAdd);
        toggleListener = toAdd;
    }

    /**
     * Edit button listener
     * @param toAdd
     */
    public void setEditClickListener(ShopIngredientClickListener toAdd) {
        clickListeners.add(toAdd);
    }
}
