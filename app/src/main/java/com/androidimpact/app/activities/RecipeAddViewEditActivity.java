package com.androidimpact.app.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.androidimpact.app.Ingredient;
import com.androidimpact.app.R;
import com.androidimpact.app.RecipeIngredientAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

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

    //https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/#:~:text=Create%20a%20new%20project%20on,firebase%20to%20that%20android%20application.&text=Two%20buttons%3A,firebase%20storage%20on%20the%20cloud
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_addviewedit);

        // Initialize database
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("recipes");

        // Initialize storage for photos
        storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReference();

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

            // Check if recipe can be added with given inputs
            if (checkInputs()) {
                    HashMap<String, Object> data = new HashMap<>();
                    HashMap<String, Object> ingredientData = new HashMap<>();
                    for (int i = 0; i < ingredients.size(); i++) {
                        ingredientData.put("ingredient" + i, ingredients.get(i));
                    }

                    //https://www.javatpoint.com/java-get-current-date
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    data.put("date", formatter.format(date));
                    data.put("prep time", getStr(prep_time));
                    data.put("servings", getStr(servings));
                    data.put("category", getStr(category));
                    data.put("comments", getStr(comments));
                    data.put("photo", photo.getTag().toString());
                    data.put("ingredients", ingredientData);

                    collectionReference
                            .document(title.getText().toString())
                            .set(data)
                            .addOnSuccessListener(unused -> Log.d(TAG, "Data addition successful"))
                            .addOnFailureListener(e -> Log.d(TAG, "Data addition failed"));

                    // Add photo to Firebase storage
                    String img_name = UUID.randomUUID().toString();
                    StorageReference imgs = storageReference.child("images/" + img_name);
                    imgs.putFile((Uri) photo.getTag())
                            .addOnSuccessListener(unused -> Log.d(TAG, "Photo addition successful"))
                            .addOnFailureListener(e -> Log.d(TAG, "Photo addition failed"));

                generateSnackbar("Added " + getStr(title) + "!");
                title.setText("");
                prep_time.setText("");
                servings.setText("");
                category.setText("");
                comments.setText("");
                ingredients.clear();
                photo.setImageResource(R.drawable.ic_launcher_foreground);
                ingredientAdapter.notifyDataSetChanged();
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
                    generateSnackbar("Added " + ingredient.getDescription() + "!");
                    ingredientAdapter.notifyDataSetChanged();
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // cancelled request - do nothing.
                    Log.i(TAG + ":addIngredientResult", "Received cancelled");
                    generateSnackbar("Ingredient cancelled!");
                }
            });

    /**
     * Opens activity to add ingredients
     * @param v
     *    Button that activates the function
     */
    public void addIngredient(View v) {
        Log.i(TAG + ":addPhoto", "Adding ingredient!");
        Intent intent = new Intent(this, RecipeAddEditIngredientActivity.class);
        intent.putExtra("activity_name", "Add ingredient");
        addIngredientLauncher.launch(intent);
    }

    // Adding photo
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
                        photo.setImageBitmap(selectedImageBitmap);

                        //https://stackoverflow.com/questions/28505123/getting-an-image-path-from-a-imageview
                        photo.setTag(selectedImageUri); //Get file path for adding to storage
                    }
                }
            });

    /**
     * Opens activity to add photo
     * @param v
     *    ImageView that activates this method
     */
    public void addPhoto(View v) {
        Log.i(TAG + ":addPhoto", "Adding photo!");
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        addPhotoLauncher.launch(intent);
    }

    /**
     * This method checks if a recipe can be added with the values in the fields.
     * @return
     *   returns true if values are fine, false otherwise.
     */
    public boolean checkInputs() {

        // Code adapted from groupmate Aneeljyot Alagh in his Assignment 1
        // Accessed on October 30, 2022
        String[] blankCheckStrings = {"Title", "Prep time", "Servings", "Category", }; // mandatory fill out
        ArrayList<String> snackbarMessage = new ArrayList<>();
        boolean invalidInput = false;
        boolean[] blankChecks = {
                getStr(title).isBlank(),
                getStr(prep_time).isBlank(),
                getStr(servings).isBlank(),
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
        Snackbar snackbar = Snackbar.make(findViewById(R.id.recipe_layout), message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        TextView snackbarTextView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
    }
}