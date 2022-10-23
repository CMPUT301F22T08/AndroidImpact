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

public class AddStoreIngredientActivity extends AppCompatActivity {
    // TAG: useful for logging
    final String TAG = "AddStoreIngredientActivity";

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
            String myFormat="dd-MM-yyyy";
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

       String snackbarStr = "Not implemented!";
       try {
           StoreIngredient newStoreIngredient = createIngredient();
       } catch (Exception e){
           snackbarStr = e.getMessage();
       }

        // right now, just make a snack-bar saying the error or not implemented
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, snackbarStr, Snackbar.LENGTH_LONG)
                .setAction("Ok", view1 -> {})
                .show();
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
        String description = descriptionEdit.getText().toString();
        String amountRaw = amountEdit.getText().toString();
        String location = locationEdit.getText().toString();
        String unit = unitEdit.getText().toString();
        String category = categoryEdit.getText().toString();
        String bestBeforeDate = bestBeforeEdit.getText().toString();

        if (description.equals("")) {
            throw new Exception("Description cannot be empty.");
        }

        if (amountRaw.equals("")) {
            throw new Exception("Amount cannot be empty.");
        }

        float amount;
        try {
            amount = Float.parseFloat(amountRaw);
            assert amount>0;
        } catch (Exception e) {
            throw new Exception("Amount must be positive.");
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

        if (bestBeforeDate.equals("")) {
            throw new Exception("Best before date cannot be empty.");
        }
        try {
            String[] ddMMyy = bestBeforeDate.split("-", 3);
            assert (ddMMyy.length == 3);
        } catch (Exception e) {
            throw new Exception("Best before date must be dd-MM-yyyy.");
        }

        // Check if best before date is today, or a future date.
        Calendar today = Calendar.getInstance();
        Calendar bestBefore = Calendar.getInstance();
        String[] ddMMyy = bestBeforeDate.split("-", 3);
        bestBefore.set(Calendar.DAY_OF_MONTH, Integer.parseInt(ddMMyy[0]));
        bestBefore.set(Calendar.MONTH, Integer.parseInt(ddMMyy[1]));
        bestBefore.set(Calendar.YEAR, Integer.parseInt(ddMMyy[2]));
        if (bestBefore.compareTo(today) <= 0) {
            throw new Exception("Best before date must be a future date.");
        }

        try {
            return new StoreIngredient(description, amount, unit, category, bestBefore, location);
        } catch(Exception e) {
            throw new Exception("Error parsing ingredients");
        }
    }
}