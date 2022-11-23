package com.androidimpact.app.unit;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidimpact.app.R;
import com.androidimpact.app.location.Location;

import java.util.ArrayList;

/**
 * UnitAdapter
 * Provides the adapter for managing the recycler view when editing units
 * @version 1.0
 * @author Joshua Ji
 */
public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.UnitViewHolder> {
    private ArrayList<Unit> unitArrayList;

    /**
     * Create constructor class
     * @param unitArrayList
     */
    public UnitAdapter(ArrayList<Unit> unitArrayList) {
        this.unitArrayList = unitArrayList;
    }


    /**
     * Creates viewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public UnitAdapter.UnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new UnitAdapter.UnitViewHolder(view);
    }

    /**
     * Creates list item and turns data from textview and user actions into a ViewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull UnitAdapter.UnitViewHolder holder, int position) {
        // Set the data to textview from our modal class.
        Unit currentUnit = unitArrayList.get(position);

        holder.title.setText(currentUnit.getUnit());
    }

    /**
     * View Holder Class to handle Recycler View.
     * Holds all the view elements necessary for the Adapter
     */
    public class UnitViewHolder extends RecyclerView.ViewHolder {
        private Resources res;

        private TextView title;
        /**
         * initializing our text views
         * @param itemView
         */
        public UnitViewHolder(@NonNull View itemView) {
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
        return unitArrayList.size();
    }
}
