package com.androidimpact.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.androidimpact.app.category.Category;

import java.util.ArrayList;

public class NullableSpinnerAdapter<T> extends ArrayAdapter<T> {
    private final String TAG = "NullableSpinnerAdapter";

    private ArrayList<T> mObjects;


    /**
     * Constructor for NullableSpinnerAdapter
     * @param context
     * @param items
     */
    public NullableSpinnerAdapter(Context context, ArrayList<T> items) {
        super(context, R.layout.spinner_item, items);
        mObjects = items;
    }

    /**
     * getter method for drop down view
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return initialSelection(true);
        }
        return getCustomView(position, convertView, parent);
    }

    /**
     * getter method for view
     * @param position (integer)
     * @param convertView (view)
     * @param parent (view group)
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return initialSelection(false);
        }
        return getCustomView(position, convertView, parent);
    }


    /**
     * getter method for count
     * @return (integer)
     */
    @Override
    public int getCount() {
        return super.getCount() + 1; // Adjust for initial selection item
    }

    /**
     * sets the initial selection for the dropdown menu
     * @param dropdown
     * @return View
     */
    private View initialSelection(boolean dropdown) {
        // Just an example using a simple TextView. Create whatever default view
        // to suit your needs, inflating a separate layout if it's cleaner.
        TextView view = new TextView(getContext());
        view.setText("");

        if (dropdown) { // Hidden when the dropdown is opened
            view.setHeight(0);
        }

        return view;
    }

    /**
     *  getter method for item
     * @param position (integer)
     * @return
     */
    @Override
    public T getItem(int position) {
        if (position == 0) {
            return null;
        }
        return mObjects.get(position - 1);
    }

    /**
     * getter method for custom view
     * @param position (integer)
     * @param convertView (view)
     * @param parent (view group)
     * @return View
     */
    private View getCustomView(int position, View convertView, ViewGroup parent) {
        int layout = R.layout.spinner_item;
        int layout_title_id = R.id.spinner_item_text;

        // Distinguish "real" spinner items (that can be reused) from initial selection item
        View row = convertView != null && !(convertView instanceof TextView)
                ? convertView :
                LayoutInflater.from(getContext()).inflate(layout, parent, false);

        // return empty item
        if (position == 0) {
            return row;
        }
        // return item with text
        T item = getItem(position);
        Log.i(TAG, "Getting custom view for item " + position + ":" + item.toString());
        TextView title = row.findViewById(layout_title_id);
        title.setText(item.toString());
        return row;
    }
}
