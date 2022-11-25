package com.androidimpact.app.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.androidimpact.app.DocumentRetrievalListener;
import com.androidimpact.app.NullableSpinnerAdapter;
import com.androidimpact.app.R;
import com.androidimpact.app.location.Location;
import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.recipes.RecipeIngredient;
import com.androidimpact.app.recipes.RecipeIngredientAdapter;
import com.androidimpact.app.Timestamped;
import com.androidimpact.app.category.Category;
import com.androidimpact.app.category.EditCategoriesActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import id.zelory.compressor.Compressor;
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
    EditText title, prep_time, servings, comments;
    Button confirmBtn;

    RecyclerView ingredientList;
    RecipeIngredientAdapter ingredientAdapter;
    ArrayList<RecipeIngredient> recipeIngredients;

    ImageView photo;
    private Boolean isEditing;
    // if we're editing, this is the photoID of the editing photo
    private String photoID;
    // true if we are currently uploading an image to firebase;
    private boolean uploading = false;

    // spinners
    Spinner categorySpinner;
    AtomicReference<Category> selectedCategory = new AtomicReference<>();

    // firebase
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;
    CollectionReference recipesCollection;
    CollectionReference ingredientsCollection;
    CollectionReference categoriesCollection;
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
        recipesCollection = db.collection("recipes");
        categoriesCollection = db.collection("categories");

        // Initialize storage for photos
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Link XML objects
        title = findViewById(R.id.recipe_title);
        prep_time = findViewById(R.id.recipe_prep);
        servings = findViewById(R.id.recipe_servings);
        comments = findViewById(R.id.recipe_comments);
        photo = findViewById(R.id.recipe_image);
        confirmBtn = findViewById(R.id.confirm_button);
        uploading = false;
        categorySpinner = findViewById(R.id.recipe_category_spinner);

        recipeIngredients = new ArrayList<>();
        ingredientList = findViewById(R.id.recipe_ingredients_list);

        ingredientAdapter = new RecipeIngredientAdapter(this, recipeIngredients);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ingredientList.setLayoutManager(manager);
        ingredientList.setAdapter(ingredientAdapter);

        // set up adapters and stuff for custom user-defined category spinner

        ArrayList<Category> categories = new ArrayList<>();
        NullableSpinnerAdapter<Category> categoryAdapter = new NullableSpinnerAdapter<>(this, categories);
        categorySpinner.setAdapter(categoryAdapter);

        // extract extras
        extras = getIntent().getExtras();
        if (extras != null) {

            isEditing = extras.getBoolean("isEditing", false);

            if (isEditing) {
                getSupportActionBar().setTitle("Edit Recipe");
                Recipe currentRecipe = (Recipe) extras.getSerializable("recipe");

                photoID = currentRecipe.getPhoto();
                id = currentRecipe.getId();
                title.setText(currentRecipe.getTitle());
                prep_time.setText(String.valueOf(currentRecipe.getPrep_time()));
                servings.setText(String.valueOf(currentRecipe.getServings()));
                comments.setText(currentRecipe.getComments());

                // load image for recipe
                // very similar code as in RecipeListAdapter...
                String photoURI = currentRecipe.getPhoto();
                if (photoURI != null) {
                    try {
                        // get child in storage
                        StorageReference photoRef = storageReference.child("images/" + photoURI);
                        photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Got the download URL and put image in corresponding ImageView
                            Picasso.get().load(uri).into(photo);
                        }).addOnFailureListener(exception -> {
                            // Log any errors
                            Log.e("Image Not Found", currentRecipe.getTitle(), exception);
                            photo.setImageResource(R.drawable.ic_baseline_dining_24);
                        });
                    } catch (Exception exception) {
                        // Log any errors
                        Log.e("Child Not Found", currentRecipe.getTitle(), exception);
                    }
                } else {
                    photo.setImageResource(R.drawable.ic_baseline_dining_24);
                }

                // add listener to the ingredients collection
                ingredientsCollection = db.collection(currentRecipe.getCollectionPath());

                // sets selectedCategory for us to set initial spinner values
                // although, this is only seen when
                selectedCategory.set(new Category(currentRecipe.getCategory()));
                Log.i(TAG, "Set category: " + selectedCategory.get());
            } else {
                // when non editing, make a new collection
                getSupportActionBar().setTitle("Add Recipe");
                // initialize defaults
                photo.setImageResource(R.drawable.ic_baseline_dining_24);

                // make a new document and photo
                id = UUID.randomUUID().toString();
                ingredientsCollection = recipesCollection.document(id).collection("ingredients");
            }

            // add event listeners to ingredients collection for other changes
            ingredientsCollection.addSnapshotListener(abstractSnapshotListener(
                    RecipeIngredient.class, recipeIngredients, "RecipeIngredient", null, ingredientAdapter, null, null));
            categoriesCollection.addSnapshotListener(abstractSnapshotListener(
                    Category.class, categories, "Category", categoryAdapter, null, categorySpinner, selectedCategory));
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
                ingredientsCollection.document(toDelete.getId()).delete()
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
             intent.putExtra("isEditing",  true);
             intent.putExtra("ingredient", recipeIngredients.get(position));
             editIngredientLauncher.launch(intent);
        });

        // Cancel button on bottom left
        final Button cancelRecipe = findViewById(R.id.cancel_button);
        cancelRecipe.setOnClickListener(v -> finish());

        // category spinner stuff
        categorySpinner.setOnItemSelectedListener(abstractOnItemSelectedListener(selectedCategory));
    }

    public void confirm(View view) {
        try {
            if (!uploading) {
                uploading = true;
                confirmBtn.setEnabled(false);
                confirmBtn.setText(R.string.loading);
                parseRecipe()
                        .onSuccessTask(r -> recipesCollection.document(r.getId()).set(r))
                        .addOnSuccessListener(unused -> {
                            Log.i(TAG, "Successfully added recipe!");
                            setResult(Activity.RESULT_OK);

                            finish();
                        });
            }
        } catch (Exception e) {
            confirmBtn.setEnabled(true);
            uploading = false;
            confirmBtn.setText(R.string.confirm);
            Log.i(TAG, "Failed to confirm", e);
            generateSnackbar(e.getMessage());
        }
    }

    private Task<Recipe> parseRecipe() throws Exception {
         // Make sure inputs are valid
        ArrayList<String> exceptionString = new ArrayList<>();
        if (getStr(title).isBlank()) {
            exceptionString.add("Title");
        }
        if (getStr(prep_time).isBlank()) {
            exceptionString.add("Prep-time");
        }
        if (getStr(servings).isBlank()) {
            exceptionString.add("Servings");
        }
        if (selectedCategory.get() == null) {
            exceptionString.add("Category");
        }
        if (exceptionString.size() > 0) {
            throw new Exception(String.join(", ", exceptionString) + " must be filled!");
        }

        // list of futures; we wait for all of them to complete via whenAll at the end of this method
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
        String collection = selectedCategory.get().getCategory();
        return Tasks.whenAll(futures).continueWith(unused -> new Recipe(id, getStr(title), Integer.parseInt(getStr(prep_time)), Integer.parseInt(getStr(servings)),
                collection, getStr(comments), new Date(), finalNewPhotoID, ingredientsCollection.getPath()));
    }


    /**
     * A generic onItemSelectedlistener for spinners for the user-defined collections (units, locations, categories)
     */
    private <T extends Serializable> AdapterView.OnItemSelectedListener abstractOnItemSelectedListener(
            AtomicReference<T> selectedElem
    ) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedElem.set((T) parentView.getItemAtPosition(position - 1));
                if (selectedElem.get() != null) {
                    Log.i(TAG, "onItemSelected: selected " + selectedElem.get().toString());

                } else {
                    Log.i(TAG, "onItemSelected: selected null");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.i(TAG, "Nothing selected");
            }
        };
    }

    /**
     * A generic snapshot listener for simple user-defined collections (units, locations, categories)
     * Also sorts the data based on the timestamp data
     *
     * This abstracts the snapshot listener
     */
    private <T extends Timestamped, U extends RecyclerView.Adapter<?>> EventListener<QuerySnapshot> abstractSnapshotListener(
            Class<T> valueType,
            ArrayList<T> data,
            String debugName,
            @Nullable ArrayAdapter<T> arrayAdapter,
            @Nullable U recyclerViewAdapter,
            @Nullable Spinner spinner,
            @Nullable AtomicReference<T> selectedElem
    ) {
        return (queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }
            if (queryDocumentSnapshots == null) {
                Log.w(TAG + ":snapshotListener", "Location collection is null!");
                return;
            }
            data.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                T item = doc.toObject(valueType);
                data.add(item);
            }
            Log.i(TAG, "Added " + data.size() + " elements of class " + valueType.toString());
            // sort by date added
            data.sort((l1, l2) -> (int) (l1.getDateAdded().getTime() - l2.getDateAdded().getTime()));
            if (arrayAdapter != null) arrayAdapter.notifyDataSetChanged();
            if (recyclerViewAdapter != null) recyclerViewAdapter.notifyDataSetChanged();

            // a bit of a hack...
            // this is only used when we need to have a selected element, like for spinners and stuff
            if (spinner != null && selectedElem != null) {
                int idx = data.indexOf(selectedElem.get());
                // add 1 to the spinner - this allows us to select the "null" element
                spinner.setSelection(idx + 1);
                String classStr = selectedElem.get() == null ? "null" : selectedElem.get().getClass().toString();
                Log.i(TAG, "SnapshotListener: " + selectedElem.get() + " " + classStr + " - (" + data.indexOf(selectedElem.get()) + ")");
                if (idx == -1) {
                    generateSnackbar("Warning: " + debugName + " '" + selectedElem.get() + "' was not found in the database!");
                    selectedElem.set(null);
                }
            }
        };
    }

    /**
     * A generic DocumentRetrievalListener for initial population of ArrayLists for user-defined collections
     * (units, locations, categories)
     */
    private <T> DocumentRetrievalListener<T> abstractDocumentRetrievalListener(
            AtomicReference<T> selectedItem,
            ArrayList<T> datas,
            Spinner spinner,
            String recipeTitle // for debug purposes
    ) {
        return new DocumentRetrievalListener<T>() {
            @Override
            public void onSuccess(T data) {
                selectedItem.set(data);
                spinner.setSelection(datas.indexOf(data));
                Log.i(TAG, "DocumentRetrieval: " + data.toString() + " " + data.getClass() + " - (" + datas.indexOf(data) + ")" + datas.size());
            }
            @Override
            public void onNullDocument() {
                // happens if the user deletes a document by themselves. We should not allow it!
                Log.i(TAG, "Bruh moment: ingredient " + recipeTitle + " cannot retrieve unit - Document does not exist");
            }
            @Override
            public void onError(Exception e) {
                Log.d(TAG, "Bruh moment: ingredient cannot retrieve unit: failed ", e);
            }
        };
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
                    ingredientsCollection.document(ingredient.getId()).set(ingredient)
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
                    Log.i(TAG + "addIngredientLauncher", "Adding ingredient" + ingredient);
                    Log.i(TAG + "addIngredientLauncher", "PATH:" + ingredientsCollection.getPath());
                    Log.i(TAG + "addIngredientLauncher", "ingredient:" + ingredient.toString());
                    Log.i(TAG + "addIngredientLauncher", "ingredientID:" + ingredient.getId());
                    ingredientsCollection.document(ingredient.getId()).set(ingredient)
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
                    try {
                        // Image compression
                        // https://github.com/zetbaitsu/Compressor/blob/master/README_v2.md
                        // zetbaitsu Mar 22, 2021
                        photoFile = new Compressor(this).compressToFile(photoFile);
                        fileProvider = Uri.fromFile(photoFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photo.setImageBitmap(selectedImageBitmap);

                    // https://stackoverflow.com/questions/28505123/getting-an-image-path-from-a-imageview
                    photo.setTag(fileProvider); //Get file path for adding to storage
                    changedImage = true;
                }
            });


    /**
     * This is run when R.id.recipe_category_editbtn is clicked
     *
     * this function jumps to the EditLocations activity.
     */
    public void editCategories(View view) {
        Log.i(TAG + ":editCategories", "Going to Edit categories");
        Intent intent = new Intent(this, EditCategoriesActivity.class);
        disregardResultLauncher.launch(intent);
    }

    /**
     * A launcher that disregards the result
     *
     * This is used in editUnits and editCategories
     *
     * we don't care about the callback because the new location (or deleted locations)
     * will be updated automatically by firebase, via the addSnapshotListener
     */
    final private ActivityResultLauncher<Intent> disregardResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {}
    );

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