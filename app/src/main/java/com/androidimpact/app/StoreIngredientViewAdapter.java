package com.androidimpact.app;

import android.content.Context;
import android.content.res.Resources;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Locale;

public class StoreIngredientViewAdapter extends RecyclerView.Adapter<StoreIngredientViewAdapter.StoreIngredientViewHolder>{
    private final String TAG = "StoreIngredientViewAdapter";

    // creating a variable for our array list and context.
    private ArrayList<StoreIngredient> ingredientArrayList;
    private Context mContext;
    private int selected = -1; // initialize no ingredients selected

    // functions that subscribe for edit callbacks
    private ArrayList<StoreIngredientEditListener> editListeners = new ArrayList<>();

    // creating a constructor class.
    public StoreIngredientViewAdapter(Context mContext, ArrayList<StoreIngredient> ingredientArrayList) {
        this.ingredientArrayList = ingredientArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public StoreIngredientViewAdapter.StoreIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_storage_item, parent, false);
        return new StoreIngredientViewAdapter.StoreIngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreIngredientViewAdapter.StoreIngredientViewHolder holder, int position) {
        // Set the data to textview from our modal class.
        StoreIngredient currentIngredient = ingredientArrayList.get(position);

        // set values
        holder.description.setText(currentIngredient.getDescription());
        holder.category.setText(currentIngredient.getCategory());
        holder.location.setText(currentIngredient.getLocation());

        // if `selected` is the position, make the expandable section visible
        if (position == selected) {
            holder.dropdownToggle.setImageResource(R.drawable.expand_less_white);
            Log.i(TAG + ":clickedDropdownToggle", "Set item to visible: " + position);
            holder.expandable.setVisibility(View.VISIBLE);
        } else {
            holder.dropdownToggle.setImageResource(R.drawable.expand_more_white);
            holder.expandable.setVisibility(View.GONE);
        }

        // edit content inside the expandable section
        // using strings with placeholders because that's apparently better
        // https://stackoverflow.com/a/40715374
        
        String amount = holder.res.getString(R.string.store_ingredient_amount_display, currentIngredient.getAmount(), currentIngredient.getUnit());
        holder.amount.setText(amount);

        // setting formatted date
        String myFormat="MMM dd yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        String formattedDate = dateFormat.format(currentIngredient.getBestBeforeDate().getTime());
        String date = holder.res.getString(R.string.store_ingredient_date_display, formattedDate);
        holder.date.setText(date);

        // OnClick Listener
        holder.dropdownToggle.setOnClickListener(v -> {
            Log.i(TAG + ":clickedDropdownToggle", "Clicked dropdown of item at position " + position);
            clickedItem(position);
        });

        holder.editIngredientFAB.setOnClickListener(v -> {
            // execute all listeners
            for (StoreIngredientEditListener listener : editListeners) {
                listener.storeIngredientEditClicked(currentIngredient, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method returns
        // the size of recyclerview
        return ingredientArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    // not sure why thi sis necessary
    // From what I understand, all this does is retrieve all the items. The ViewHolder means it "holds"
    // all the view elements necessary fro the Adapter.
    public class StoreIngredientViewHolder extends RecyclerView.ViewHolder {
        private Resources res;

        // creating a variable for our text view.
        private TextView description;

        // creating a variable for category
        private Chip category;
        private Chip location;
        private ImageButton dropdownToggle;
        private FloatingActionButton editIngredientFAB;

        private ConstraintLayout expandable;
        private TextView amount;
        private TextView date;

        public StoreIngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            res = itemView.getResources();

            // initializing our text views.
            //Need to be changed for now
            description = itemView.findViewById(R.id.store_ingredient_description);
            category = itemView.findViewById(R.id.store_ingredient_category);
            location = itemView.findViewById(R.id.store_ingredient_location);
            dropdownToggle = itemView.findViewById(R.id.store_ingredient_dropdown_toggle);
            editIngredientFAB = itemView.findViewById(R.id.edit_store_ingredient);

            expandable = itemView.findViewById(R.id.store_ingredient_expandable_section);
            date = itemView.findViewById(R.id.store_ingredient_expiry);
            amount = itemView.findViewById(R.id.store_ingredient_amount);
        }
    }

    private void clickedItem(int position) {
        if (selected == position) {
            // deselect
            selected = -1;
        } else {
            selected = position;
        }
        notifyItemChanged(position);
    }

    // OBSERVER PATTERN: this interface lets people subscribe to changes in the StoreIngredientViewAdapter
    // this is because we need the parent activity to react to changes because it has the Context and Activity info
    // https://stackoverflow.com/a/36662886
    public interface StoreIngredientEditListener {
        void storeIngredientEditClicked(StoreIngredient food, int position);
    }

    public void setEditClickListener(StoreIngredientEditListener toAdd) {
        editListeners.add(toAdd);
    }

}
