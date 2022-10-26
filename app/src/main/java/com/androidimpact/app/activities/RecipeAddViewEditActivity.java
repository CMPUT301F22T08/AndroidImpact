package com.androidimpact.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.androidimpact.app.Ingredient;
import com.androidimpact.app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * This class is the activity for recipe adding/viewing/editing
 * @author Curtis Kan
 * @version 1.0
 */
public class RecipeAddViewEditActivity extends AppCompatActivity {

    // Initialize attributes
    final String TAG = "Sample";
    EditText title, prep_time, servings, category, comments;
    RecyclerView ingredientAdapter;
    ArrayList<Ingredient> ingredients;
    ImageView photo;
    TextView activity_title;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_addviewedit);

        // Initialize database
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("recipes");

        // Link XML objects
        title = findViewById(R.id.recipe_title);
        prep_time = findViewById(R.id.recipe_prep);
        servings = findViewById(R.id.recipe_servings);
        category = findViewById(R.id.recipe_category);
        comments = findViewById(R.id.recipe_comments);
        photo = findViewById(R.id.recipe_image);
        activity_title = findViewById(R.id.activity_title);

        // Floating add button for ingredients
        final FloatingActionButton addIngredientButton = findViewById(R.id.add_ingredient);
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(RecipeAddViewEditActivity.this, RecipeAddEditIngredientActivity.class));
            }
        });

        // Add button on bottom right
        final Button addRecipe = findViewById(R.id.add_button);
        addRecipe.setOnClickListener(v -> {

            // Don't allow blank title, prep time, servings, or category
            if (title.getText().toString().isBlank() || prep_time.getText().toString().isBlank() || servings.getText().toString().isBlank() || category.getText().toString().isBlank()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Blank input!", Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                HashMap<String, String> data = new HashMap<>();

                //https://www.javatpoint.com/java-get-current-date
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                data.put("date", formatter.format(date));
                data.put("prep time", prep_time.getText().toString());
                data.put("servings", servings.getText().toString());
                data.put("category", category.getText().toString());
                data.put("comments", comments.getText().toString());
                data.put("photo", comments.getText().toString());

                collectionReference
                        .document(title.getText().toString())
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "Data addition successful");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Data addition failed");
                            }
                        });

                finish();
            }
        });

        // Cancel button on bottom left
        final Button cancelRecipe = findViewById(R.id.cancel_button);
        cancelRecipe.setOnClickListener(v -> finish());
    }

}