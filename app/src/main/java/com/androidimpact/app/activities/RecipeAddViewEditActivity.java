package com.androidimpact.app.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.androidimpact.app.Ingredient;
import com.androidimpact.app.R;
import com.androidimpact.app.RecipeIngredientAdapter;
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
    final String TAG = "RecipeAddViewEdit";
    EditText title, prep_time, servings, category, comments;

    ListView ingredientList;
    ArrayAdapter<Ingredient> ingredientAdapter;
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

        ingredients = new ArrayList<>();
        ingredientList = findViewById(R.id.recipe_ingredients_list);

        ingredientAdapter = new RecipeIngredientAdapter(this, ingredients);
        ingredientList.setAdapter(ingredientAdapter);

        // Add button on bottom right
        final Button addRecipe = findViewById(R.id.add_button);
        addRecipe.setOnClickListener(v -> {

            // Don't allow blank title, prep time, servings, or category
            if (title.getText().toString().isBlank() || prep_time.getText().toString().isBlank() || servings.getText().toString().isBlank() || category.getText().toString().isBlank()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Blank input!", Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                HashMap<String, Object> data = new HashMap<>();
                HashMap<String, Object> ingredientData = new HashMap<>();
                for (Ingredient ingredient : ingredients) {
                    ingredientData.put(ingredient.getDescription(), ingredient);
                }

                //https://www.javatpoint.com/java-get-current-date
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                data.put("date", formatter.format(date));
                data.put("prep time", prep_time.getText().toString());
                data.put("servings", servings.getText().toString());
                data.put("category", category.getText().toString());
                data.put("comments", comments.getText().toString());
                data.put("photo", comments.getText().toString());
                data.put("ingredients", ingredientData);

                collectionReference
                        .document(title.getText().toString())
                        .set(data)
                        .addOnSuccessListener(unused -> Log.d(TAG, "Data addition successful"))
                        .addOnFailureListener(e -> Log.d(TAG, "Data addition failed"));

            }
        });

        // Cancel button on bottom left
        final Button cancelRecipe = findViewById(R.id.cancel_button);
        cancelRecipe.setOnClickListener(v -> finish());
    }

    // Adding ingredients
    final private ActivityResultLauncher<Intent> addIngredientLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    Bundle bundle = result.getData().getExtras();
                    Ingredient ingredient = (Ingredient) bundle.getSerializable("ingredient");
                    Log.i(TAG + ":addIngredientResult", ingredient.getDescription());
                    ingredients.add(ingredient);
                    ingredientAdapter.notifyDataSetChanged();
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // cancelled request - do nothing.
                    Log.i(TAG + ":addIngredientResult", "Received cancelled");
                }
            });

    // Adding ingredients
    public void addIngredient(View v) {
        Log.i(TAG + ":addPhoto", "Adding ingredient!");
        Intent intent = new Intent(this, RecipeAddEditIngredientActivity.class);
        intent.putExtra("activity_name", "Add");
        addIngredientLauncher.launch(intent);
    }

    // Adding photos
    final private ActivityResultLauncher<Intent> addPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                // https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        photo.setImageBitmap(
                                selectedImageBitmap);
                    }
                }
            });

    // Adding photos
    public void addPhoto(View v) {
        Log.i(TAG + ":addPhoto", "Adding photo!");
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        addPhotoLauncher.launch(intent);
    }

}