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
import com.androidimpact.app.RecipeIngredient;
import com.androidimpact.app.RecipeIngredientAdapter;
import com.androidimpact.app.StoreIngredient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.util.List;
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
    // if we're editing, this is the photoID of the editing photo
    private String photoID;

    // firebase
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;
    CollectionReference recipes;
    CollectionReference ingredients;
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
        recipes = db.collection("recipes");

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
         extras = getIntent().getExtras();
         if (extras != null) {

             String value = extras.getString("activity_name");
             activity_title.setText(value);
             isEditing = extras.getBoolean("isEditing", false);

             if (isEditing) {
                 Recipe recipe = (Recipe) extras.getSerializable("recipe");

                 photoID = recipe.getPhoto();
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
                parseRecipe()
                        .onSuccessTask(r -> recipes.document(r.getId()).set(r)).addOnSuccessListener(unused -> {
                            Log.i(TAG, "Successfully added recipe!");
                            setResult(Activity.RESULT_OK);
                            finish();
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

    private Task<Recipe> parseRecipe() throws Exception {
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

        // list of futures
        List<Task<?>> futures = new ArrayList<>();

        String newPhotoID = photoID;
        if (changedImage) {
            // delete old image
            if (photoID != null) {
                futures.add(storageReference.child("images/" + photoID)
                        .delete()
                        .addOnSuccessListener(unused -> Log.i(TAG, "Deleted old image " + photoID)));
            }

            // add new image
            String img_name = UUID.randomUUID().toString();
            newPhotoID = img_name;
            StorageReference imgs = storageReference.child("images/" + img_name);
            futures.add(imgs.putFile((Uri) photo.getTag()).addOnSuccessListener(unused -> Log.i(TAG, "Successfully put file " + img_name)));
        }

        // somehow we need this because of the lambda expression below
        String finalNewPhotoID = newPhotoID;
        return Tasks.whenAll(futures).continueWith(unused -> new Recipe(id, getStr(title), Integer.parseInt(getStr(prep_time)), Integer.parseInt(getStr(servings)),
                getStr(category), getStr(comments), new Date(), finalNewPhotoID, ingredients.getPath()));
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
                    Bitmap selectedImageBitmap;
                    selectedImageBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                    photo.setImageBitmap(selectedImageBitmap);

                    // https://stackoverflow.com/questions/28505123/getting-an-image-path-from-a-imageview
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
    public void generateSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.recipe_layout), message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        TextView snackbarTextView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.setAction("Ok", view1 -> {}).show();
    }
}