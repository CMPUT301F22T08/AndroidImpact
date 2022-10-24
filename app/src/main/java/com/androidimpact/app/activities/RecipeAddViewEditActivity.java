package com.androidimpact.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidimpact.app.R;

/**
 * This class is the activity for recipe adding/viewing/editing
 * @author Curtis Kan
 * @version 1.0
 */
public class RecipeAddViewEditActivity extends AppCompatActivity {

    Button cancel;
    Button add;
    EditText title;
    EditText prep_time;
    EditText servings;
    EditText category;
    EditText comments;
    RecyclerView pog;
    ImageView photo;
    TextView activity_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_addviewedit);

        cancel = findViewById(R.id.cancel_button);
        add = findViewById(R.id.add_button);
        title = findViewById(R.id.recipe_title);
        prep_time = findViewById(R.id.recipe_prep);
        servings = findViewById(R.id.recipe_servings);
        category = findViewById(R.id.recipe_category);
        comments = findViewById(R.id.recipe_comments);
        photo = findViewById(R.id.recipe_image);
        activity_title = findViewById(R.id.activity_title);

    }
}