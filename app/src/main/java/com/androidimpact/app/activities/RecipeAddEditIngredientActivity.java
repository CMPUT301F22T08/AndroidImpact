package com.androidimpact.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidimpact.app.Ingredient;
import com.androidimpact.app.R;


/**
 * This class is the activity for ingredient adding/viewing/editing to recipe
 * @author Curtis Kan
 * @version 1.0
 */
public class RecipeAddEditIngredientActivity extends AppCompatActivity {

    // Initialize attributes
    final String TAG = "addRecipeIngredient";
    EditText description;
    EditText amount;
    EditText unit;
    EditText category;
    TextView activity_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_addedit_ingredient);

        description = findViewById(R.id.ingredient_description);
        amount = findViewById(R.id.ingredient_amount);
        unit = findViewById(R.id.ingredient_unit);
        category = findViewById(R.id.ingredient_category);
        activity_title = findViewById(R.id.activity_title);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("activity_name");
            activity_title.setText(value);
        }
    }

    public void confirm(View view) {
        if (description.getText().toString().isBlank() || amount.getText().toString().isBlank() || unit.getText().toString().isBlank() || category.getText().toString().isBlank()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Blank input!", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Ingredient ingredient = new Ingredient(description.getText().toString(), Float.parseFloat(amount.getText().toString()), unit.getText().toString(), category.getText().toString());
            Log.i(TAG + ":confirmed", "Added ingredient");
            Intent returnIntent = new Intent();
            returnIntent.putExtra("ingredient", ingredient);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }


    public void cancel(View view) {
        Log.i(TAG + ":cancel", "Cancel ingredient add");
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

}