package com.androidimpact.app;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Locale;

public class StoreIngredientViewAdapter extends RecyclerView.Adapter<StoreIngredientViewAdapter.StoreIngredientViewHolder>{
    private final String TAG = "StoreIngredientViewAdapter";

    // creating a variable for our array list and context.
    private ArrayList<StoreIngredient> ingredientArrayList;
    private Context mContext;
    private int selected;

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
        StoreIngredient recyclerData = ingredientArrayList.get(position);
        holder.description.setText(recyclerData.getDescription());
        holder.category.setText(recyclerData.getCategory());
        String myFormat="dd MMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        holder.date.setText(dateFormat.format(recyclerData.getBestBeforeDate().getTime()));
        holder.dropdownToggle.setOnClickListener(v -> {
            Log.i(TAG + ":clickedDropdownToggle", "Clicked dropdown of item at position " + position);
            clickedItem(position);
        });

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
        holder.amount.setText("Amount: " + recyclerData.getAmount() + recyclerData.getUnit());
        holder.location.setText("Location: " + recyclerData.getLocation());
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
    // all the view elements neessary fro the Adapter.
    public class StoreIngredientViewHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view.
        private TextView description;

        // creating a variable for category
        private TextView category;
        private TextView date;
        private ImageButton dropdownToggle;

        private ConstraintLayout expandable;
        private TextView amount;
        private TextView location;

        public StoreIngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            //Need to be changed for now
            date = itemView.findViewById(R.id.store_ingredient_expiry);
            description = itemView.findViewById(R.id.store_ingredient_description);
            category = itemView.findViewById(R.id.store_ingredient_category);
            dropdownToggle = itemView.findViewById(R.id.store_ingredient_dropdown_toggle);

            expandable = itemView.findViewById(R.id.store_ingredient_expandable_section);
            amount = itemView.findViewById(R.id.store_ingredient_amount);
            location = itemView.findViewById(R.id.store_ingredient_location);
        }
    }

    private void clickedItem(int position) {
        if (selected == position) {
            // deselect
            selected = -1;
        } else {
            selected = position;
        }
        notifyDataSetChanged();
    }

}
