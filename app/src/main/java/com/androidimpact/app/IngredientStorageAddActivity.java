package com.androidimpact.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class IngredientStorageAddActivity extends AppCompatActivity {
    // TAG: useful for logging
    final String TAG = "IngredientStorageAddActivity";

    // declare all view variables
    private EditText descriptionEdit;
    private EditText amountEdit;
    private EditText locationEdit;
    private EditText unitEdit;
    private EditText categoryEdit;
    private EditText bestBeforeEdit;

    // helpers to manage date popup
    private Date bestBefore;
    final Calendar cal = Calendar.getInstance();

    // buttons
    private Button cancelBtn;
    private Button confirmBtn;

    // Misc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_storage_add);

        // init view variables
        descriptionEdit = findViewById(R.id.ingredientStoreAdd_description);
        amountEdit = findViewById(R.id.ingredientStoreAdd_amount);
        locationEdit = findViewById(R.id.ingredientStoreAdd_location);
        unitEdit = findViewById(R.id.ingredientStoreAdd_unit);
        categoryEdit = findViewById(R.id.ingredientStoreAdd_category);
        cancelBtn = findViewById(R.id.ingredientStoreAdd_cancelBtn);
        confirmBtn = findViewById(R.id.ingredientStoreAdd_confirmBtn);
        bestBeforeEdit = findViewById(R.id.ingredientStoreAdd_bestBefore);
    }

    /**
     * Runs when "best before" is clicked
     * This creates the date picker popup
     *
     * https://stackoverflow.com/a/14933515
     */
    public void pickDate(View view) {
        DatePickerDialog.OnDateSetListener listener = (v, year, month, day) -> {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH,month);
            cal.set(Calendar.DAY_OF_MONTH,day);

            bestBefore = cal.getTime();

            // now, update best before date
            String myFormat="dd/MM/yy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
            bestBeforeEdit.setText(dateFormat.format(cal.getTime()));
        };

        new DatePickerDialog(this, listener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    /**
     * This is executed when the "cancel" button is clicked
     */
    public void cancel(View view) {
        Log.i(TAG + ":cancel", "Cancel ingredient add");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * This is executed when the "confirm" button is clicked
     */
    public void confirm(View view) {
        Log.i(TAG + ":cancel", "Confirm ingredient add");

        // right now, just make a snackbar saying it's not finished
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "Not implemented!", Snackbar.LENGTH_LONG)
                .setAction("Ok", view1 -> {})
                .show();
    }

    /**
     * Attempts to create an ingredient with the values in the inputs.
     * Throws an exception if it fails.
     * @return
     */
    private Ingredient createIngredient() throws Exception {
        String description = descriptionEdit.toString();
        String amountRaw = amountEdit.toString();
        String location = locationEdit.toString();
        String unit = unitEdit.toString();
        String category = categoryEdit.toString();

        if (description.equals("")) {
            throw new Exception("description");
        }

        if (amountRaw.equals("")) {
            throw new Exception("amount");
        }

        if (location.equals("")) {
            throw new Exception("amount");
        }
        if (unit.equals("")) {
            throw new Exception("amount");
        }
        if (category.equals("")) {
            throw new Exception("amount");
        }

        try {
            int amount = Integer.parseInt(amountRaw);
            return new Ingredient(description, amount, unit, category);
        } catch(Exception e) {
            throw new Exception("Error parsing ingredients");
        }
    }
}