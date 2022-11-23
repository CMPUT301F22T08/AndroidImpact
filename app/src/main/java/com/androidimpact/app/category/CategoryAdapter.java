package com.androidimpact.app.category;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.R;

import java.util.ArrayList;

/**
 * LocationAdapter
 *
 * Provides the adapter for managing the recycler view when editing locations
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private ArrayList<Category> categoryArrayList;
    private Context mContext;

    /**
     * Create constructor class
     * @param mContext
     * @param categoryArrayList
     */
    public CategoryAdapter(Context mContext, ArrayList<Category> categoryArrayList) {
        this.categoryArrayList = categoryArrayList;
        this.mContext = mContext;
    }


    /**
     * Creates viewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new CategoryViewHolder(view);
    }

    /**
     * Creates list item and turns data from textview and user actions into a ViewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Set the data to textview from our modal class.
        Category currentCategory = categoryArrayList.get(position);

        holder.title.setText(currentCategory.getCategory());
    }

    /**
     * View Holder Class to handle Recycler View.
     * Holds all the view elements necessary for the Adapter
     */
    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private Resources res;

        private TextView title;
        /**
         * initializing our text views
         * @param itemView
         */
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            res = itemView.getResources();

            title = itemView.findViewById(R.id.recycler_view_item_title);
        }
    }


    /**
     * This method returns the size of recyclerview
     * @return
     */
    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }
}

