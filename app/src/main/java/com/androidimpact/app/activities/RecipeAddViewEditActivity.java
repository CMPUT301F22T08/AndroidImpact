package com.androidimpact.app.activities;

import static java.util.Objects.isNull;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.androidimpact.app.Ingredient;
import com.androidimpact.app.R;
import com.androidimpact.app.Recipe;
import com.androidimpact.app.RecipeIngredientAdapter;
import com.androidimpact.app.StoreIngredient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * This class is the activity for recipe adding/editing
 * @author Curtis Kan
 * @version 1.0
 */
public class RecipeAddViewEditActivity extends AppCompatActivity {

    // Initialize attributes
    final String TAG = "RecipeAddViewEdit";
    EditText title, prep_time, servings, category, comments;

    RecyclerView ingredientList;
    RecipeIngredientAdapter ingredientAdapter;
    ArrayList<Ingredient> ingredients;

    ImageView photo;
    TextView activity_title;
    FirebaseFirestore db;
    private String docName;
    private Boolean isEditing;
    private String date;
    FirebaseStorage storage;
    File photoFile;
    Uri fileProvider;
    Bundle extras;
    boolean changedImage = false;

    /**
     * This method runs when the activity is created
     * @param savedInstanceState
     *    Bundle object
     */
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
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ingredientList.setLayoutManager(manager);
        ingredientList.setAdapter(ingredientAdapter);

         // extract extras
         extras = getIntent().getExtras();
         if (extras != null) {

             String value = extras.getString("activity_name");
             activity_title.setText(value);
             isEditing = extras.getBoolean("isEditing", false);

             docName = extras.getString("title", "");
             title.setText(docName);
             prep_time.setText(extras.getString("prep time", ""));
             servings.setText(extras.getString("servings", ""));
             category.setText(extras.getString("category", ""));
             comments.setText(extras.getString("comments", ""));
             date = extras.getString("date","");
             if (isEditing) {
                 collectionReference.document(docName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                     @Override
                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            if (task.isSuccessful()) {
                                HashMap<String, Object> map = (HashMap<String, Object>) document.getData().get("ingredients");
                                for (String key : map.keySet()) {
                                    HashMap<String, Object> ingredientMap = (HashMap<String, Object>) map.get(key);
                                    ingredients.add(new Ingredient(
                                            (String) ingredientMap.get("description"),
                                            ((Double) ingredientMap.get("amount")).floatValue(),
                                            (String) ingredientMap.get("unit"),
                                            (String) ingredientMap.get("category")
                                    ));

                                }
                                ingredientAdapter.notifyDataSetChanged();
                            }

                     }
                 });

                 ingredientAdapter.notifyDataSetChanged();
             }

             // load image for recipe
             String photoURI = extras.getString("photo", null);
             if (photoURI != null) {
                 try {
                     // get child in storage
                     StorageReference photoRef = storageReference.child("images/" + photoURI);
                     photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                         // Got the download URL and put image in corresponding ImageView
                         Picasso.get().load(uri).into(photo);
                     }).addOnFailureListener(exception -> {
                         // Log any errors
                         Log.e("Image Not Found", docName, exception);
                         photo.setImageResource(R.drawable.ic_baseline_dining_24);
                     });

                 } catch (Exception exception) {
                     // Log any errors
                     Log.e("Child Not Found", docName, exception);
                 }
             } else {
                 photo.setImageResource(R.drawable.ic_baseline_dining_24);
             }
             photo.setTag(extras.getString("photo", null));

         }


        // drag to delete - code duplicated from recycler view in ingredient storage
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            /**
             * This method is called when item is moved
             * @param recyclerView
             * @param viewHolder
             * @param target
             * @return
             */
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            /**
             * This creates swipe to delete functionality
             * @param viewHolder
             * @param direction
             */
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // below line is to get the position
                // of the item at that position.
                int position = viewHolder.getAdapterPosition();
                generateSnackbar("Deleted " + ingredients.get(position).getDescription() + "!");
                ingredients.remove(position);
                ingredientAdapter.notifyDataSetChanged();
            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(ingredientList);

        // Edit
        ingredientAdapter.setEditClickListener((storeIngredient, position) -> {
             // runs whenever a store ingredient edit btn is clicked
             Log.i(TAG + ":setEditClickListener", "Editing ingredient at position " + position);
             Intent intent = new Intent(this, RecipeAddEditIngredientActivity.class);
             intent.putExtra("activity_name", "Edit ingredient");
             intent.putExtra("isEditing",  true);
             intent.putExtra("position", position);
             intent.putExtra("ingredient", ingredients.get(position));
             editIngredientLauncher.launch(intent);
         });

        // Add button on bottom right
        final Button addRecipe = findViewById(R.id.add_button);
        if (isEditing) {
            addRecipe.setText(getResources().getString(R.string.edit));
        }
        addRecipe.setOnClickListener(v -> {

            // Check if recipe can be added with given inputs
            if (checkInputs()) {
                    HashMap<String, Object> data = new HashMap<>();
                    HashMap<String, Object> ingredientData = new HashMap<>();
                    for (int i = 0; i < ingredients.size(); i++) {
                        ingredientData.put("ingredient" + i, ingredients.get(i));
                    }

                // Code for getting the date
                // https://www.javatpoint.com/java-get-current-date
                // Copyright 2011-2021 www.javatpoint.com. All rights reserved. Developed by JavaTpoint.
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date newDate = new Date();
                data.put("date", isEditing?date:formatter.format(newDate));
                data.put("prep time", getStr(prep_time));
                data.put("servings", getStr(servings));
                data.put("category", getStr(category));
                data.put("comments", getStr(comments));

                if (photo.getTag() == null) {
                    data.put("photo", null);  // shows up as null in database
                }
                // Add photo to firebase storage
                // https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/#:~:text=Create%20a%20new%20project%20on,firebase%20to%20that%20android%20application.&text=Two%20buttons%3A,firebase%20storage%20on%20the%20cloud
                // Rishabh007 - December 7, 2021
                else {
                    // Delete old thing from collection
                    if (isEditing) {
                        collectionReference
                                .document(docName)
                                .delete();
                    }
                    if (changedImage) {
                        // delete photo from Firebase Storage
                        storageReference.child("images/" + extras.getString("photo", null)).delete();
                        String img_name = UUID.randomUUID().toString();
                        data.put("photo", img_name);
                        StorageReference imgs = storageReference.child("images/" + img_name);
                        imgs.putFile((Uri) photo.getTag())
                                .addOnSuccessListener(unused -> Log.d(TAG, "Photo addition successful"))
                                .addOnFailureListener(e -> Log.d(TAG, "Photo addition failed"));
                        photo.setTag(img_name);
                    } else {
                        data.put("photo", extras.getString("photo"));
                    }
                }
                data.put("ingredients", ingredientData);
                Log.i("works", "well");
                Log.i("docName",docName);

                collectionReference
                        .document(title.getText().toString())
                        .set(data)
                        .addOnSuccessListener(unused -> Log.d(TAG, "Data addition successful"))
                        .addOnFailureListener(e -> Log.d(TAG, "Data addition failed"));
                generateSnackbar("Added " + getStr(title) + "!");

                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        // Cancel button on bottom left
        final Button cancelRecipe = findViewById(R.id.cancel_button);
        cancelRecipe.setOnClickListener(v -> finish());
    }

    // Editing ingredients
    final private ActivityResultLauncher<Intent> editIngredientLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    Bundle bundle = result.getData().getExtras();
                    Ingredient ingredient = (Ingredient) bundle.getSerializable("ingredient");
                    Log.i(TAG + ":addIngredientResult", ingredient.getDescription());
                    ingredients.set(bundle.getInt("position"), ingredient);
                    generateSnackbar("Edited " + ingredient.getDescription() + "!");
                    ingredientAdapter.notifyDataSetChanged();
                }
            });

    // Adding ingredients, follows format of ingredient storage activity launchers
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

                    // Confetti for successful adds!
                    final KonfettiView confetti = findViewById(R.id.confetti_view);
                    confetti.build()
                            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(500L)
                            .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                            .addSizes(new Size(8, 4f))
                            .setPosition(-50f, confetti.getWidth() + 50f, -50f, -50f)
                            .streamFor(300, 2000L);
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

                // Code to add photo from gallery
                // https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
                // adityamshidalyali - May 17, 2022
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Bitmap selectedImageBitmap;
                    selectedImageBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                    photo.setImageBitmap(selectedImageBitmap);

                        //https://stackoverflow.com/questions/28505123/getting-an-image-path-from-a-imageview
                    photo.setTag(fileProvider); //Get file path for adding to storage
                    changedImage = true;
                    }
            });

    /**
     * Opens activity to add photo
     * @param v
     *    ImageView that activates this method
     */
    public void addPhoto(View v) {
        Log.i(TAG + ":addPhoto", "Adding photo!");
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri("photo.png");
        fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        addPhotoLauncher.launch(intent);

    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("photo", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
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