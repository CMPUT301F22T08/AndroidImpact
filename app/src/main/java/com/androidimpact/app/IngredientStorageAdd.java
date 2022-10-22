package com.androidimpact.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class IngredientStorageAdd extends AppCompatActivity {
    // TAG: useful for logging
    final String TAG = "IngredientStorageAdd";

    // declare all view variables
    private EditText descriptionEdit;
    private EditText amountEdit;
    private EditText locationEdit;
    private EditText unitCostEdit;
    private EditText categoryEdit;

    // buttons
    private Button cancelBtn;
    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_storage_add);

        // init view variables
        descriptionEdit = findViewById(R.id.ingredientStoreAdd_description);
        amountEdit = findViewById(R.id.ingredientStoreAdd_amount);
        locationEdit = findViewById(R.id.ingredientStoreAdd_location);
        unitCostEdit = findViewById(R.id.ingredientStoreAdd_unitCost);
        categoryEdit = findViewById(R.id.ingredientStoreAdd_category);
        cancelBtn = findViewById(R.id.ingredientStoreAdd_cancelBtn);
        confirmBtn = findViewById(R.id.ingredientStoreAdd_confirmBtn);


    }

    /**
     * CANCEL
     *
     * This is executed when the "cancel" button is clicked
     */
    public void cancel(View view) {
        Log.i(TAG + ":cancel", "Cancel ingredient add");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * CONFIRM
     *
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
}