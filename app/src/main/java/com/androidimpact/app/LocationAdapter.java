package com.androidimpact.app;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * LocationAdapter
 *
 * Provides the adapter for managing the recycler view when editing locations
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private ArrayList<Location> locationArrayList;
    private Context mContext;

    // functions that subscribe for edit callbacks
    private ArrayList<LocationEditListener> editListeners = new ArrayList<>();

    /**
     * Create constructor class
     * @param mContext
     * @param ingredientArrayList
     */
    public LocationAdapter(Context mContext, ArrayList<Location> ingredientArrayList) {
        this.locationArrayList = ingredientArrayList;
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
    public LocationAdapter.LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        return new LocationAdapter.LocationViewHolder(view);
    }

    /**
     * Creates list item and turns data from textview and user actions into a ViewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.LocationViewHolder holder, int position) {
        // Set the data to textview from our modal class.
        Location currentLocation = locationArrayList.get(position);

        holder.title.setText(currentLocation.getLocation());
    }

    /**
     * View Holder Class to handle Recycler View.
     * Holds all the view elements necessary for the Adapter
     */
    public class LocationViewHolder extends RecyclerView.ViewHolder {
        private Resources res;

        private TextView title;
        /**
         * initializing our text views
         * @param itemView
         */
        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            res = itemView.getResources();

            title = itemView.findViewById(R.id.location_item_title);
        }
    }


    /**
     * This method returns the size of recyclerview
     * @return
     */
    @Override
    public int getItemCount() {
        return locationArrayList.size();
    }

    /**
     * this interface lets people subscribe to changes in the LocationAdapter
     * this is because we need the parent activity to react to changes because it has the Context and Activity info
     * https://stackoverflow.com/a/36662886
     */
    public interface LocationEditListener {
        void locationEdited(Location food, int position);
    }

    /**
     * Edit button listener
     * @param toAdd
     */
    public void addEditListener(LocationEditListener toAdd) {
        editListeners.add(toAdd);
    }
}
