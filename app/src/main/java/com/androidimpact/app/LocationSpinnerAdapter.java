package com.androidimpact.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * LocationSpinnerAdapter
 *
 * provides an adapter for managing the spinner dropdown
 * this is when the user is choosing a location when editing a StoreItem
 * https://www.geeksforgeeks.org/how-to-add-custom-spinner-in-android/
 */
public class LocationSpinnerAdapter extends ArrayAdapter<Location> {

    public LocationSpinnerAdapter(Context context, ArrayList<Location> locations) {
        super(context, 0, locations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable  View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        // It is used to set our custom view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        // text1 is the id for the builtin `simple_list_item_1`
        TextView textViewName = convertView.findViewById(android.R.id.text1);
        Location currentItem = getItem(position);

        // This sets the text for all the dropdown
        if (currentItem != null) {
            textViewName.setText(currentItem.getLocation());
        }
        return convertView;
    }
}
