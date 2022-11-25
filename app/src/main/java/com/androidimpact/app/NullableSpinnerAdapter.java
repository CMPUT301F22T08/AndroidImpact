package com.androidimpact.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.androidimpact.app.category.Category;

import java.util.ArrayList;

public class NullableSpinnerAdapter<T> extends ArrayAdapter<T> {
    private final String TAG = "CategorySpinnerAdapter";

    public NullableSpinnerAdapter(Context context, ArrayList<T> items) {
        super(context, R.layout.spinner_item, items);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return initialSelection(true);
        }
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return initialSelection(false);
        }
        return getCustomView(position, convertView, parent);
    }


    @Override
    public int getCount() {
        return super.getCount() + 1; // Adjust for initial selection item
    }

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
        position = position - 1; // compensate for hidden item at the beginning
        T item = getItem(position);
        TextView title = row.findViewById(layout_title_id);
        title.setText(item.toString());
        return row;
    }
}
