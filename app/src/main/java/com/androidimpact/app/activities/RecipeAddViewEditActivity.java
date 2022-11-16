package com.androidimpact.app.activities;

import static java.util.Objects.isNull;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.androidimpact.app.Ingredient;
import com.androidimpact.app.R;
import com.androidimpact.app.Recipe;
import com.androidimpact.app.RecipeIngredient;
import com.androidimpact.app.RecipeIngredientAdapter;
import com.androidimpact.app.StoreIngredient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
    String id;
    EditText title, prep_time, servings, category, comments;

    RecyclerView ingredientList;
    RecipeIngredientAdapter ingredientAdapter;
    ArrayList<RecipeIngredient> recipeIngredients;

    ImageView photo;
    TextView activity_title;
    private String docName;
    private Boolean isEditing;
    private String date;

    // firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore db;
    CollectionReference ingredients;
    CollectionReference recipes; // the parent collection

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
        recipes = db.collection("recipes-new");

        // Initialize storage for photos
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Link XML objects
        title = findViewById(R.id.recipe_title);
        prep_time = findViewById(R.id.recipe_prep);
        servings = findViewById(R.id.recipe_servings);
        category = findViewById(R.id.recipe_category);
        comments = findViewById(R.id.recipe_comments);
        photo = findViewById(R.id.recipe_image);
        activity_title = findViewById(R.id.activity_title);
        final Button addRecipe = findViewById(R.id.add_button);

        recipeIngredients = new ArrayList<>();
        ingredientList = findViewById(R.id.recipe_ingredients_list);

        ingredientAdapter = new RecipeIngredientAdapter(this, recipeIngredients);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ingredientList.setLayoutManager(manager);
        ingredientList.setAdapter(ingredientAdapter);

         // extract extras
         Bundle extras = getIntent().getExtras();
         if (extras != null) {

             String value = extras.getString("activity_name");
             activity_title.setText(value);
             isEditing = extras.getBoolean("isEditing", false);

             if (isEditing) {
                 Recipe recipe = (Recipe) extras.getSerializable("recipe");
                 id = recipe.getId();
                 title.setText(recipe.getTitle());
                 prep_time.setText(String.valueOf(recipe.getPrep_time()));
                 servings.setText(String.valueOf(recipe.getServings()));
                 category.setText(recipe.getCategory());
                 comments.setText(recipe.getComments());

                 // set button
                 addRecipe.setText(getResources().getString(R.string.edit));

                 // load image for recipe
                 // very similar code as in RecipeListAdapter...
                 String photoURI = recipe.getPhoto();
                 if (photoURI != null) {
                     try {
                         // get child in storage
                         StorageReference photoRef = storageReference.child("images/" + photoURI);
                         photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                             // Got the download URL and put image in corresponding ImageView
                             Picasso.get().load(uri).into(photo);
                         }).addOnFailureListener(exception -> {
                             // Log any errors
                             Log.e("Image Not Found", recipe.getTitle(), exception);
                             photo.setImageResource(R.drawable.ic_baseline_dining_24);
                         });
                     } catch (Exception exception) {
                         // Log any errors
                         Log.e("Child Not Found", recipe.getTitle(), exception);
                     }
                 } else {
                     photo.setImageResource(R.drawable.ic_baseline_dining_24);
                 }

                 // add listener to the ingredients collection
                 ingredients = db.collection(recipe.getCollectionPath());
             } else {
                 // when non editing, make a new collection

                 // initialize defaults
                 photo.setImageResource(R.drawable.ic_baseline_dining_24);

                 // make a new document and photo
                 id = UUID.randomUUID().toString();
                 ingredients = recipes.document(id).collection("ingredients");
             }

             // add event listeners to ingredients collection (we are guaranteed to have one)
             ingredients.addSnapshotListener((queryDocumentSnapshots, error) -> {
                 if (error != null) {
                     Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                     return;
                 }
                 if (queryDocumentSnapshots == null) {
                     Log.w(TAG + ":snapshotListener", "Location collection is null!");
                     return;
                 }
                 recipeIngredients.clear();
                 for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                     RecipeIngredient i = doc.toObject(RecipeIngredient.class);
                     recipeIngredients.add(i);
                     Log.i(TAG, "Add recipeIngredient " + i.getId() + " " + i.getDescription());
                 }
                 Log.i(TAG, "Added " + recipeIngredients.size() + " elements");
                 // sort by date added
                 recipeIngredients.sort((l1, l2) -> (int) (l1.getDateAdded().getTime() - l2.getDateAdded().getTime()));
                 ingredientAdapter.notifyDataSetChanged();
             });
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
                int position = viewHolder.getAdapterPosition();;
                RecipeIngredient toDelete = recipeIngredients.get(position);
                ingredients.document(toDelete.getId()).delete()
                        .addOnSuccessListener(unused -> generateSnackbar("Deleted " + toDelete.getDescription() + "!"))
                        .addOnFailureListener(e -> {
                            Log.i(TAG, "Failed to delete " + toDelete.getId(), e);
                            generateSnackbar("Failed to delete " + toDelete.getDescription() + "!");
                        });

                ingredientAdapter.notifyItemRemoved(position);
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
             intent.putExtra("ingredient", recipeIngredients.get(position));
             editIngredientLauncher.launch(intent);
         });

        // Add button on bottom right
        addRecipe.setOnClickListener(v -> {
            try {
                Recipe r = parseRecipe();

                recipes
                    .document(r.getId())
                    .set(r)
                    .addOnSuccessListener(unused -> {
                        setResult(Activity.RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.i(TAG, "Failed to add recipe " + r.getId(), e);
                        generateSnackbar("Failed to add " + getStr(title) + "!");
                    });
            } catch (Exception e) {
                Log.i(TAG, "Failed to add recipe!", e);
                generateSnackbar("Failed to add " + getStr(title) + "!");
            }
        });

        // Cancel button on bottom left
        final Button cancelRecipe = findViewById(R.id.cancel_button);
        cancelRecipe.setOnClickListener(v -> finish());
    }

    private Recipe parseRecipe() throws Exception {
        if (getStr(title).isBlank()) {
            throw new Exception("Title must be nonempty!");
        }
        if (getStr(prep_time).isBlank()) {
            throw new Exception("Prep time must be nonempty!");
        }
        if (getStr(servings).isBlank()) {
            throw new Exception("Servings must be nonempty!");
        }
        if (getStr(category).isBlank()) {
            throw new Exception("Category must be nonempty!");
        }

        String photoID;
        if (photo.getTag() == null) {
            photoID = null;  // shows up as null in database
        } else {
            if (isEditing) {
                photoID = photo.getTag().toString();
            } else {
                // Add photo to firebase storage
                // https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/#:~:text=Create%20a%20new%20project%20on,firebase%20to%20that%20android%20application.&text=Two%20buttons%3A,firebase%20storage%20on%20the%20cloud
                // Rishabh007 - December 7, 2021
                photoID = UUID.randomUUID().toString();
                StorageReference imgs = storageReference.child("images/" + photoID);
                imgs.putFile((Uri) photo.getTag())
                        .addOnSuccessListener(unused -> Log.d(TAG, "Photo addition successful"))
                        .addOnFailureListener(e -> Log.d(TAG, "Photo addition failed"));
            }
        }

        Log.i(TAG, "ingredients path:" + ingredients.getPath());

        return new Recipe(
                id, getStr(title), Integer.parseInt(getStr(prep_time)), Integer.parseInt(getStr(servings)),
                getStr(category), getStr(comments), new Date(), photoID, ingredients.getPath()
        );

    }

    // Editing ingredients
    final private ActivityResultLauncher<Intent> editIngredientLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    Bundle bundle = result.getData().getExtras();
                    RecipeIngredient ingredient = (RecipeIngredient) bundle.getSerializable("ingredient");
                    Log.i(TAG + ":addIngredientResult", ingredient.getDescription());
                    ingredients.document(ingredient.getId()).set(ingredient)
                            .addOnSuccessListener(unused -> generateSnackbar("Edited " + ingredient.getDescription() + "!"))
                            .addOnFailureListener(e -> generateSnackbar("Failed to edit " + ingredient.getDescription() + "!"));
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
                    RecipeIngredient ingredient = (RecipeIngredient) bundle.getSerializable("ingredient");
                    ingredients.document(ingredient.getId()).set(ingredient)
                            .addOnSuccessListener(documentReference -> generateSnackbar("Added " + ingredient.getDescription() + "!"))
                            .addOnFailureListener(e -> generateSnackbar("Failed to add " + ingredient.getDescription() + "!"));

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
        snackbar.setAction("Ok", view1 -> {}).show();
    }
}