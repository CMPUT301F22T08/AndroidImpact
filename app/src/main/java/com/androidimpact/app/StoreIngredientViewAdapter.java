package com.androidimpact.app;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;

public class StoreIngredientViewAdapter extends RecyclerView.Adapter<StoreIngredientViewAdapter.StoreIngredientViewHolder>{

    // creating a variable for our array list and context.
    private ArrayList<StoreIngredient> ingredientArrayList;
    private Context mContext;

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
        holder.storeIngredientDescription.setText(recyclerData.getDescription());
        holder.storeIngredientCategory.setText(recyclerData.getCategory());
        String myFormat="dd MMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        holder.storeIngredientDate.setText(dateFormat.format(recyclerData.getBestBeforeDate().getTime()));
        holder.storeIngredientEditFAB.setOnClickListener(v -> {
            Log.i("EDIT", "Edit StoreIngredient button was clicked");
            // Launch AddStoreIngredient using the clicked ingredient here
        });
    }

    @Override
    public int getItemCount() {
        // this method returns
        // the size of recyclerview
        return ingredientArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class StoreIngredientViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our views
        private TextView storeIngredientDescription;
        private TextView storeIngredientCategory;
        private TextView storeIngredientDate;
        private FloatingActionButton storeIngredientEditFAB;

        public StoreIngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            //Need to be changed for now
            storeIngredientDate = itemView.findViewById(R.id.store_ingredient_expiry);
            storeIngredientDescription = itemView.findViewById(R.id.store_ingredient_description);
            storeIngredientCategory = itemView.findViewById(R.id.store_ingredient_category);
            storeIngredientEditFAB = itemView.findViewById(R.id.store_ingredient_edit_FAB);
        }
    }
}
