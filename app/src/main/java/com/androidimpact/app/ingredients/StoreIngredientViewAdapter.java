package com.androidimpact.app.ingredients;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.R;
import com.androidimpact.app.meal_plan.OnSelectInterface;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * This class creates a view adapter for StoreIngredient
 * @version 1.0
 * @author Vedant Vyas, Joshua Ji
 */
public class StoreIngredientViewAdapter extends RecyclerView.Adapter<StoreIngredientViewAdapter.StoreIngredientViewHolder>{
    private final String TAG = "StoreIngredientViewAdapter";

    // creating a variable for our array list and context.
    private ArrayList<StoreIngredient> ingredientArrayList;
    private Context mContext;
    private int selected = -1; // initialize no ingredients selected

    private boolean isSelection;
    private OnSelectInterface onSelectInterface;

    // functions that subscribe for edit callbacks
    private ArrayList<StoreIngredientEditListener> editListeners = new ArrayList<>();


    /**
     * Constructor for the class
     * @param mContext
     * @param ingredientArrayList
     *      (ArrayList<StoreIngredient>) The source data for the view adapter
     */
    public StoreIngredientViewAdapter(Context mContext, ArrayList<StoreIngredient> ingredientArrayList) {
        this.ingredientArrayList = ingredientArrayList;
        this.mContext = mContext;
        this.isSelection = false;
    }

    /**
     * Create constructor class
     * @param mContext
     * @param controller
     *      (IngredientStorageController) The controller object that the data is pulled from
     */
    public StoreIngredientViewAdapter(Context mContext, IngredientStorageController controller) {
        this(mContext, controller.getData());
        //this.ingredientArrayList = controller.getDataAsList();
        //this.mContext = mContext;
        this.isSelection = false;
    }

    /**
     * Create constructor class
     * @param mContext
     * @param ingredientStorage
     *      (IngredientStorageController) The controller object that the data is pulled from
     */
    public StoreIngredientViewAdapter(Context mContext, IngredientStorage ingredientStorage, OnSelectInterface onSelectInterface) {
        this(mContext, ingredientStorage.getData());
        this.isSelection = true;
        this.onSelectInterface = onSelectInterface;
    }

    /**
     * Creates viewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public StoreIngredientViewAdapter.StoreIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_storage_item, parent, false);
        return new StoreIngredientViewAdapter.StoreIngredientViewHolder(view);
    }

    /**
     * Creates list item and turns data from textview and user actions into a ViewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull StoreIngredientViewAdapter.StoreIngredientViewHolder holder, int position) {
        // Set the data to textview from our modal class.
        StoreIngredient currentIngredient = ingredientArrayList.get(position);

        // set values
        holder.description.setText(currentIngredient.getDescription());

        // if `selected` is the position, make the expandable section visible
        if (position == selected) {
            holder.dropdownToggle.setImageResource(R.drawable.expand_less_white);
            Log.i(TAG + ":clickedDropdownToggle", "Set item to visible: " + position);
            holder.expandable.setVisibility(View.VISIBLE);
        } else {
            holder.dropdownToggle.setImageResource(R.drawable.expand_more_white);
            holder.expandable.setVisibility(View.GONE);
        }

        // set view values
        // units and categories are stored in an ingredient by their string value
        String amountUnit = holder.res.getString(R.string.shop_ingredient_amount_display, currentIngredient.getAmount(), currentIngredient.getUnit());
        holder.amount.setText(amountUnit);
        holder.category.setText(currentIngredient.getCategory());

        if (currentIngredient.hasNull()){
            holder.has_null_marker.setVisibility(View.VISIBLE);
        } else {
            holder.has_null_marker.setVisibility(View.INVISIBLE);
        }

        if (currentIngredient.getLocation() == ""){
            holder.location.setVisibility(View.INVISIBLE);
        } else {
            holder.location.setVisibility(View.VISIBLE);
            holder.location.setText(currentIngredient.getLocation());
        }


        Date nullDate = new Date(122, 8, 30);

        String myFormat="MMM dd yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        String formattedDate = dateFormat.format(currentIngredient.getBestBeforeDate().getTime());
        String date = holder.res.getString(R.string.store_ingredient_date_display, formattedDate);

        String nullDateString = dateFormat.format(nullDate.getTime());
        Log.i("testDate", nullDateString);
        if (formattedDate.equals(nullDateString)){
            holder.date.setText("To be added");
        } else {
            holder.date.setText(date);
        }


        // OnClick Listener
        holder.dropdownToggle.setOnClickListener(v -> {
            Log.i(TAG + ":clickedDropdownToggle", "Clicked dropdown of item at position " + position);
            clickedItem(position);
        });

        if(this.isSelection) {
            holder.editIngredientFAB.setVisibility(View.GONE);
            holder.content.setOnClickListener(view -> {
                this.onSelectInterface.selectItem(position);

            });
        } else {
            holder.editIngredientFAB.setOnClickListener(v -> {
                // execute all listeners
                for (StoreIngredientEditListener listener : editListeners) {
                    listener.storeIngredientEditClicked(currentIngredient, position);
                }
            });
        }


    }

    /**
     * This method returns the size of recyclerview
     * @return
     */
    @Override
    @NonNull
    public int getItemCount() {
        return ingredientArrayList.size();
    }

    /**
     * View Holder Class to handle Recycler View.
     * Holds all the view elements necessary for the Adapter
     */
    public class StoreIngredientViewHolder extends RecyclerView.ViewHolder {
        private Resources res;

        // creating a variable for our text view.
        private TextView description;
        private ImageView has_null_marker;

        // creating a variable for category
        private Chip category;
        private Chip location;
        private ImageButton dropdownToggle;
        private FloatingActionButton editIngredientFAB;

        private ConstraintLayout expandable;
        private LinearLayout content;
        private TextView amount;
        private TextView date;

        /**
         * returns the value of the description
         * used in tests lmao
         * @return description.getText().toString()
         */
        public String getDescriptionValue() {
            return description.getText().toString();
        }

        /**
         * initializing our text views
         * @param itemView
         */
        public StoreIngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            res = itemView.getResources();

            //Need to be changed for now
            description = itemView.findViewById(R.id.store_ingredient_description);
            category = itemView.findViewById(R.id.store_ingredient_category);
            location = itemView.findViewById(R.id.store_ingredient_location);
            dropdownToggle = itemView.findViewById(R.id.store_ingredient_dropdown_toggle);
            editIngredientFAB = itemView.findViewById(R.id.edit_store_ingredient);

            has_null_marker = itemView.findViewById(R.id.has_null_marker);
            expandable = itemView.findViewById(R.id.store_ingredient_expandable_section);
            content = itemView.findViewById(R.id.linearLayout);
            date = itemView.findViewById(R.id.store_ingredient_expiry);
            amount = itemView.findViewById(R.id.store_ingredient_amount);
        }
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
     * this interface lets people subscribe to changes in the StoreIngredientViewAdapter
     * this is because we need the parent activity to react to changes because it has the Context and Activity info
     * https://stackoverflow.com/a/36662886
     */
    public interface StoreIngredientEditListener {
        void storeIngredientEditClicked(StoreIngredient food, int position);
    }

    /**
     * Edit button listener
     * @param toAdd
     */
    public void setEditClickListener(StoreIngredientEditListener toAdd) {
        editListeners.add(toAdd);
    }

    /**
     * This is purely for testing.
     */
    public StoreIngredient getItem(int i) {
        return ingredientArrayList.get(i);
    }
}
