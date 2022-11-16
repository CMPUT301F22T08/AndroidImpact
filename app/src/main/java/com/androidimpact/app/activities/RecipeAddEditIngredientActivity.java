package com.androidimpact.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidimpact.app.DocumentRetrievalListener;
import com.androidimpact.app.Ingredient;
import com.androidimpact.app.R;
import com.androidimpact.app.StoreIngredient;
import com.androidimpact.app.unit.Unit;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

/**
 * This class is the activity for ingredient adding/viewing/editing to recipe
 * @author Curtis Kan
 * @version 1.0
 */
public class RecipeAddEditIngredientActivity extends AppCompatActivity {

    // Initialize attributes
    final String TAG = "addRecipeIngredient";
    EditText description, amount, unit, category;
    TextView activity_title;
    private Boolean isEditing;
    private int position;

    // https://developer.android.com/reference/android/widget/AutoCompleteTextView
    // Accessed October 30, 2022
    // https://en.wikibooks.org/wiki/Cookbook:Units_of_measurement
    private static final String[] unitAC = new String[] {
            "mL", "L", "dL", "tsp", "tbsp", "oz", "cup", "pint", "quart", "gallon", "mg", "g", "kg",
            "lb"};

    /**
     * This method runs when the activity is created
     * @param savedInstanceState
     *    Bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_addedit_ingredient);

        description = findViewById(R.id.ingredient_description);
        amount = findViewById(R.id.ingredient_amount);
        unit = findViewById(R.id.ingredient_unit);
        category = findViewById(R.id.ingredient_category);
        activity_title = findViewById(R.id.activity_title);

        // Autocomplete for units
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, unitAC);
        AutoCompleteTextView textView = (AutoCompleteTextView) unit;
        textView.setAdapter(unitAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("activity_name");
            isEditing = extras.getBoolean("isEditing", false);
            activity_title.setText(value);
            if (isEditing) {
                Button addButton = findViewById(R.id.add_button);
                addButton.setText("Edit");
                Ingredient ingredient = (Ingredient) extras.getSerializable("ingredient");
                description.setText(ingredient.getDescription());
                amount.setText(Float.toString(ingredient.getAmount()));
                category.setText(ingredient.getCategory());
                position = extras.getInt("position");

                // set unit
                // since we have to fetch from firebase, we'll use a "loading" state
                unit.setText("loading");
                DocumentRetrievalListener<Unit> getUnitListener = new DocumentRetrievalListener<>() {
                    @Override
                    public void onSuccess(Unit data) {
                        String unitStr = getString(R.string.store_ingredient_amount_display, ingredient.getAmount(), data.getUnit());
                        unit.setText(unitStr);
                    }

                    @Override
                    public void onNullDocument() {
                        unit.setText("NoDoc!");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "Cached get failed: ", e);
                        unit.setText("Failed!");
                    }
                };
                ingredient.getUnitAsync(getUnitListener);
            }
        }

    }

    /**
     * This method handles clicks on the confirm button
     * @param view
     *    The view that this method is connected to (add button)
     */
    public void confirm(View view) {
        if (checkInputs()) {
            Ingredient ingredient = new Ingredient(getStr(description), Float.parseFloat(getStr(amount)), getStr(unit), getStr(category));
            Log.i(TAG + ":confirmed", "Added ingredient");
            Intent returnIntent = new Intent();
            returnIntent.putExtra("ingredient", ingredient);
            if (isEditing) {
                returnIntent.putExtra("position", position);
            }
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    /**
     * This method handles clicks on the cancel button
     * @param view
     *    The view that this method is connected to (cancel button)
     */
    public void cancel(View view) {
        Log.i(TAG + ":cancel", "Cancel ingredient add");
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    /**
     * This method checks if a recipe can be added with the values in the fields.
     * @return
     *   returns true if values are fine, false otherwise.
     */
    public boolean checkInputs() {

        // Code adapted from groupmate Aneeljyot Alagh in his Assignment 1
        // Accessed on October 30, 2022
        String[] blankCheckStrings = {"Description", "Amount", "Unit", "Category"}; // mandatory fill out
        ArrayList<String> snackbarMessage = new ArrayList<>();
        boolean invalidInput = false;
        boolean[] blankChecks = {
                getStr(description).isBlank(),
                getStr(amount).isBlank(),
                getStr(unit).isBlank(),
                getStr(category).isBlank()
        };

        // Make sure all inputs are filled
        for (int i = 0; i < blankChecks.length; i++) {
            if (blankChecks[i]) {
                invalidInput = true;
                snackbarMessage.add(blankCheckStrings[i]);
            }
        }

        if (invalidInput){
            // If blanks, only print blank messages
            generateSnackbar(String.join(", ", snackbarMessage) + " must be filled!");
            return false;
        }
        else if (getStr(amount).equals("0")){
            generateSnackbar("Amount must be larger than 0!");
            return false;
        }
        return true;
    }

    /**
     * This method returns the string stored in an edit text, for reduced code length
     * @param e
     *    The EditText to get the string of
     * @return
     *    The string in the EditText
     */
    public String getStr(EditText e) {
        return e.getText().toString();
    }

    /**
     * This method generates a snackbar from a given message
     * @param message
     *    The string to send a snackbar of
     */
    public void generateSnackbar (String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.ingredient_layout), message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        TextView snackbarTextView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
    }
}
