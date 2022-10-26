package com.androidimpact.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.androidimpact.app.R;
import com.androidimpact.app.StoreIngredient;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddStoreIngredientActivity extends AppCompatActivity {
    // TAG: useful for logging
    final String TAG = "AddStoreIngredientActivity";

    // declare all view variables
    private EditText descriptionEditText;
    private EditText amountEditText;
    private EditText locationEditText;
    private EditText unitEditText;
    private EditText categoryEditText;
    private DatePicker bestBeforeDatePicker;

    // buttons
    private Button cancelBtn;
    private Button confirmBtn;

    // Misc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_storage_add);

        // init view variables
        descriptionEditText = findViewById(R.id.ingredientStoreAdd_description);
        amountEditText = findViewById(R.id.ingredientStoreAdd_amount);
        locationEditText = findViewById(R.id.ingredientStoreAdd_location);
        unitEditText = findViewById(R.id.ingredientStoreAdd_unit);
        categoryEditText = findViewById(R.id.ingredientStoreAdd_category);
        cancelBtn = findViewById(R.id.ingredientStoreAdd_cancelBtn);
        confirmBtn = findViewById(R.id.ingredientStoreAdd_confirmBtn);
        bestBeforeDatePicker = findViewById(R.id.ingredientStoreAdd_bestBefore);
        bestBeforeDatePicker.setMinDate(System.currentTimeMillis());

    }

    /**
     * This is executed when the "cancel" button is clicked
     */
    public void cancel(View view) {
        Log.i(TAG + ":cancel", "Cancel ingredient add");
        Intent intent = new Intent(this, MainActivity.class);
        setResult(Activity.RESULT_CANCELED, intent);
        startActivity(intent);
    }

    /**
     * This is executed when the "confirm" button is clicked
     */
    public void confirm(View view) {
        try {
            // try to create an ingredient.
            StoreIngredient newStoreIngredient = createIngredient();
            Intent intent = new Intent(this, MainActivity.class);

            // put the ingredient as an extra to our intent before we pass it back to the IngredientStorage
            intent.putExtra("ingredient", newStoreIngredient);
            setResult(Activity.RESULT_OK, intent);

            Log.i(TAG + ":cancel", "Returning to MainActivity");
            finish();
        } catch (Exception e){
            String snackbarStr = e.getMessage();

           // Error - add a snackbar
           View parentLayout = findViewById(android.R.id.content);
           Snackbar.make(parentLayout, snackbarStr, Snackbar.LENGTH_LONG)
                   .setAction("Ok", view1 -> {})
                   .show();
        }
    }

    /**
     * Validates the data input by the user
     * Create a StoreIngredient object if the data is valid.
     * @return A StoreIngredient object created using the data from the editTexts
     * @throws Exception if the data is invalid or if the Ingredient could not be created
     * @see StoreIngredient
     */
    // Should we throw an exception or have a snack-bar that says fields can't be empty?
    private StoreIngredient createIngredient() throws Exception {
        String description = descriptionEditText.getText().toString();
        String amountRaw = amountEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String unit = unitEditText.getText().toString();
        String category = categoryEditText.getText().toString();
        Calendar bestBeforeCalendar = Calendar.getInstance();
        bestBeforeCalendar.set(
                bestBeforeDatePicker.getYear(),
                bestBeforeDatePicker.getMonth(),
                bestBeforeDatePicker.getDayOfMonth()
        );

        if (description.equals("")) {
            throw new Exception("Description cannot be empty.");
        }

        if (amountRaw.equals("")) {
            throw new Exception("Amount cannot be empty.");
        }

        float amount;
        try {
            amount = Float.parseFloat(amountRaw);
        } catch (Exception e) {
            throw new Exception("Amount must be a float!");
        }

        if (amount < 0) {
            throw new Exception("Amount must be positive!");
        }

        if (location.equals("")) {
            throw new Exception("Location cannot be empty.");
        }

        if (unit.equals("")) {
            throw new Exception("Unit cannot be empty.");
        }

        if (category.equals("")) {
            throw new Exception("Category cannot be empty.");
        }

        try {
            return new StoreIngredient(description, amount, unit, category, bestBeforeCalendar, location);
        } catch(Exception e) {
            throw new Exception("Error parsing ingredients");
        }
    }
}