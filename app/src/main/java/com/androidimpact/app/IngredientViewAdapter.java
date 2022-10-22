package com.androidimpact.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IngredientViewAdapter extends RecyclerView.Adapter<IngredientViewAdapter.IngredientViewHolder> {

    // creating a variable for our array list and context.
    private ArrayList<Ingredient> ingredientArrayList;
    private Context mContext;

    // creating a constructor class.
    public IngredientViewAdapter(Context mContext, ArrayList<Ingredient> ingredientArrayList) {
        this.ingredientArrayList = ingredientArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_in_list, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        // Set the data to textview from our modal class.
        Ingredient recyclerData = ingredientArrayList.get(position);
        holder.ingredientDescription.setText(recyclerData.getDescription());
    }

    @Override
    public int getItemCount() {
        // this method returns
        // the size of recyclerview
        return ingredientArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view.
        private TextView ingredientDescription;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            ingredientDescription = itemView.findViewById(R.id.ingredient_description);
        }
    }
}

