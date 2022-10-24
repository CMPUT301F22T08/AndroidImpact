package com.androidimpact.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidimpact.app.R;

public class RecipeAddEditIngredient extends AppCompatActivity {

    Button cancel;
    Button add;
    EditText description;
    EditText amount;
    EditText unit;
    EditText category;
    TextView activity_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_addedit_ingredient);

        cancel = findViewById(R.id.cancel_button);
        add = findViewById(R.id.add_button);
        description = findViewById(R.id.ingredient_description);
        amount = findViewById(R.id.ingredient_amount);
        unit = findViewById(R.id.ingredient_unit);
        category = findViewById(R.id.ingredient_category);
        activity_title = findViewById(R.id.activity_title);

    }

}