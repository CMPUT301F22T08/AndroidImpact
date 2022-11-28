package com.androidimpact.app.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidimpact.app.NullableSpinnerAdapter;
import com.androidimpact.app.R;
import com.androidimpact.app.ingredients.IngredientStorageController;
import com.androidimpact.app.recipes.RecipeIngredient;
import com.androidimpact.app.Timestamped;
import com.androidimpact.app.category.Category;
import com.androidimpact.app.category.EditCategoriesActivity;
import com.androidimpact.app.unit.EditUnitsActivity;
import com.androidimpact.app.unit.Unit;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is the activity for ingredient adding/viewing/editing to recipe
 * @author Curtis Kan
 * @version 1.0
 */
public class RecipeAddEditIngredientActivity extends AppCompatActivity {

    // Initialize attributes
    final String TAG = "RecipeAddEditIngredientActivity";
    AutoCompleteTextView description;
    EditText amount;
    private Boolean isEditing = false;
    private int position;

    // other globals
    String id;
    IngredientStorageController ingredientStorageController;
    ArrayList<String> autoCompleteSource;
    ArrayAdapter<String> autoCompleteAdapter;

    // Spinners
    // as before, the selectedUnit is the source of truth.
    // Note: we are using atomic references because of some quirks with our abstraction (Josh)
    // See: addEditStoreIngredientActivity
    Spinner unitSpinner;
    AtomicReference<Unit> selectedUnit = new AtomicReference<>();
    Spinner categorySpinner;
    AtomicReference<Category> selectedCategory = new AtomicReference<>();

    // firebase
    FirebaseFirestore db;
    // other collections we use when editing an ingredient
    CollectionReference unitCollection;
    CollectionReference categoriesCollection;
    String userPath;

    /**
     * This method runs when the activity is created
     * @param savedInstanceState
     *    Bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            this.userPath = extras.getString("data-path");
        }

        // initialize Firestore
        // initialize ingredientsCollection later - after we know whether or not we are editing an ingredient or adding
        db = FirebaseFirestore.getInstance();
        unitCollection = db.document(this.userPath).collection("units");
        categoriesCollection = db.document(this.userPath).collection("categories");

        setContentView(R.layout.activity_recipe_addedit_ingredient);

        description = findViewById(R.id.ingredient_description);
        amount = findViewById(R.id.ingredient_amount);
        unitSpinner = findViewById(R.id.recipe_ingredient_unit);
        categorySpinner = findViewById(R.id.recipe_ingredient_category);

        // AutoComplete from IngredientStorage
        ingredientStorageController = new IngredientStorageController(this, this.userPath);
        autoCompleteSource = new ArrayList();
        ingredientStorageController.addSnapshotListenerAutocomplete(autoCompleteSource);
        autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, autoCompleteSource);
        description.setAdapter(autoCompleteAdapter);

        // init spinners
        ArrayList<Unit> units = new ArrayList<>();
        NullableSpinnerAdapter<Unit> unitAdapter = new NullableSpinnerAdapter<>(this, units);
        unitSpinner.setAdapter(unitAdapter);

        ArrayList<Category> categories = new ArrayList<>();
        NullableSpinnerAdapter<Category> categoryAdapter = new NullableSpinnerAdapter<>(this, categories);
        categorySpinner.setAdapter(categoryAdapter);

        //Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isEditing = extras.getBoolean("isEditing", false);
            if (isEditing) {
                getSupportActionBar().setTitle("Edit Ingredient");
                RecipeIngredient ingredient = (RecipeIngredient) extras.getSerializable("ingredient");
                id = ingredient.getId();
                description.setText(ingredient.getDescription());
                amount.setText(Float.toString(ingredient.getAmount()));
                position = extras.getInt("position");

                // set initial ingredient unit
                // note that we store the unit as a string, not a document path
                selectedUnit.set(new Unit(ingredient.getUnit()));
                Log.i(TAG, "Set unit: " + selectedUnit.get());
                selectedCategory.set(new Category(ingredient.getCategory()));
                Log.i(TAG, "Set category: " + selectedCategory.get());
            } else {
                // Here, we're also adding a new element
                // autogenerate an ID
                id = UUID.randomUUID().toString();
                getSupportActionBar().setTitle("Add Ingredient");
            }
        } else {
            // Here, we're also adding a new element
            // autogenerate an ID
            id = UUID.randomUUID().toString();
            getSupportActionBar().setTitle("Add Ingredient");
        }

        // EVENT LISTENERS

        // listen for item selected events in spinners
        unitSpinner.setOnItemSelectedListener(abstractOnItemSelectedListener(selectedUnit));
        categorySpinner.setOnItemSelectedListener(abstractOnItemSelectedListener(selectedCategory));

        // Listen for events in collections
        unitCollection.addSnapshotListener(abstractSnapshotListener(
                Unit.class, unitAdapter, units, unitSpinner, selectedUnit, "Units"));
        categoriesCollection.addSnapshotListener(abstractSnapshotListener(
                Category.class, categoryAdapter, categories, categorySpinner, selectedCategory, "Categories"));
    }

    /**
     * This method handles clicks on the confirm button
     * @param view
     *    The view that this method is connected to (add button)
     */
    public void confirm(View view) {
        if (checkInputs()) {
            String unit = selectedUnit.get().toString();
            String category = selectedCategory.get().toString();
            Log.i(TAG, "Adding ingredient with Id" + id);
            RecipeIngredient ingredient = new RecipeIngredient(
                    id,
                    getStr(description),
                    Float.parseFloat(getStr(amount)),
                    unit,
                    category,
                    new Date()
            );
            Intent returnIntent = new Intent();
            returnIntent.putExtra("ingredient", ingredient);
            if (isEditing) {
                returnIntent.putExtra("position", position);
            }
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    /**
     * This method handles clicks on the cancel button
     * @param view
     *    The view that this method is connected to (cancel button)
     */
    public void cancel(View view) {
        Log.i(TAG + ":cancel", "Cancel ingredient add");
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    /**
     * A generic onItemSelectedlistener for spinners for the user-defined collections (units, locations, categories)
     */
    private <T extends Serializable>AdapterView.OnItemSelectedListener abstractOnItemSelectedListener(
            AtomicReference<T> selectedElem
    ) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedElem.set((T) parentView.getItemAtPosition(position));
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
     * @param valueType the class type we deserialize to
     * @param adapter the array adapter of the class type
     * @param data the raw data to use
     * @param spinner the spinner to set the data to
     * @param selectedElem the selected item (wrapped in an AtomicReference)
     * @param debugName DEBUG: the name of the spinner
     * @param <T> a timestamped class
     * @return the event listener to use in an addSnapshotListener
     */
    private <T extends Timestamped> EventListener<QuerySnapshot> abstractSnapshotListener(
            Class<T> valueType,
            ArrayAdapter<T> adapter,
            ArrayList<T> data,
            Spinner spinner,
            AtomicReference<T> selectedElem,
            String debugName
    ) {
        return (queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }
            if (queryDocumentSnapshots == null) {
                Log.w(TAG + ":snapshotListener", "snapshot listener: collection is null!");
                return;
            }
            data.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                T item = doc.toObject(valueType);
                data.add(item);
            }
            Log.i(TAG, "Added " + data.size() + " elements of type: " + valueType.toString());
            // sort by date added
            data.sort((l1, l2) -> (int) (l1.getDateAdded().getTime() - l2.getDateAdded().getTime()));
            adapter.notifyDataSetChanged();
            // a bit of a hack...
            if (selectedElem.get() != null) {
                int idx = data.indexOf(selectedElem.get());
                // add 1 to the spinner - this allows us to select the "null" element
                spinner.setSelection(idx + 1);
                Log.i(TAG, "SnapshotListener: " + selectedElem.get() + " " + selectedElem.get().getClass() + " - (" + data.indexOf(selectedElem.get()) + ")");
                if (idx == -1) {
                    generateSnackbar("Warning: '" + selectedElem.get() + "' was not found in '" + debugName + "'!");
                    selectedElem.set(null);
                }
            }
        };
    }

    /**
     * This method checks if a recipe can be added with the values in the fields.
     * @return
     *   returns true if values are fine, false otherwise.
     */
    public boolean checkInputs() {

        // Code adapted from groupmate Aneeljyot Alagh in his Assignment 1
        // Accessed on October 30, 2022
        String[] blankCheckStrings = {"Description", "Amount", "Unit", "Category"}; // mandatory fill out
        ArrayList<String> snackbarMessage = new ArrayList<>();
        boolean invalidInput = false;
        boolean[] blankChecks = {
                getStr(description).isBlank(),
                getStr(amount).isBlank(),
                selectedUnit.get() == null,
                selectedCategory.get() == null
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
        else if (getStr(amount).equals("0")){
            generateSnackbar("Amount must be larger than 0!");
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
    public void generateSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.ingredient_layout), message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        TextView snackbarTextView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.setAction("Ok", view1 -> {}).show();
    }

    /**
     * This is run when R.id.ingredientStoreAdd_editUnitsBtn is clicked
     *
     * this function jumps to the EditLocations activity.
     */
    public void editUnits(View view) {
        Log.i(TAG + ":editUnits", "Going to Edit units");
        Intent intent = new Intent(this, EditUnitsActivity.class);
        intent.putExtra("data-path", userPath);
        disregardResultLauncher.launch(intent);
    }

    /**
     * This is run when R.id.ingredientStoreAdd_editUnitsBtn is clicked
     *
     * this function jumps to the EditLocations activity.
     */
    public void editCategories(View view) {
        Log.i(TAG + ":editCategories", "Going to Edit categories");
        Intent intent = new Intent(this, EditCategoriesActivity.class);
        intent.putExtra("data-path", userPath);
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
}
