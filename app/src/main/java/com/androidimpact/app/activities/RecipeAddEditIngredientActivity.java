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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidimpact.app.DocumentRetrievalListener;
import com.androidimpact.app.R;
import com.androidimpact.app.RecipeIngredient;
import com.androidimpact.app.unit.EditUnitsActivity;
import com.androidimpact.app.unit.Unit;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * This class is the activity for ingredient adding/viewing/editing to recipe
 * @author Curtis Kan
 * @version 1.0
 */
public class RecipeAddEditIngredientActivity extends AppCompatActivity {

    // Initialize attributes
    final String TAG = "RecipeAddEditIngredientActivity";
    EditText description, amount, category;
    private Boolean isEditing = false;
    private int position;

    // other globals
    String id;

    // Spinners
    // as before, the selectedUnit is the source of truth.
    Spinner unitSpinner;
    Unit selectedUnit;

    // firebase
    FirebaseFirestore db;
    // collection of ingredients inside this recipe
    CollectionReference ingredientsCollection;
    CollectionReference unitCollection;

    /**
     * This method runs when the activity is created
     * @param savedInstanceState
     *    Bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize Firestore
        // initialize ingredientsCollection later - after we know whether or not we are editing an ingredient or adding
        db = FirebaseFirestore.getInstance();
        unitCollection = db.collection("units");

        setContentView(R.layout.activity_recipe_addedit_ingredient);


        description = findViewById(R.id.ingredient_description);
        amount = findViewById(R.id.ingredient_amount);
        category = findViewById(R.id.ingredient_category);
        unitSpinner = findViewById(R.id.recipe_ingredient_unit);

        // init spinners
        ArrayList<Unit> units = new ArrayList<>();
        ArrayAdapter<Unit> unitAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, units);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        // set onclick for spinner
        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedUnit = (Unit) parentView.getItemAtPosition(position);

                Log.i(TAG, "selected unit is " + selectedUnit.getUnit());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.i(TAG, "Nothing selected");
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isEditing = extras.getBoolean("isEditing");
        }
        if (isEditing) {
            getSupportActionBar().setTitle("Edit Ingredient");
            Button addButton = findViewById(R.id.confirm_button);
            addButton.setText(R.string.edit);
            RecipeIngredient ingredient = (RecipeIngredient) extras.getSerializable("ingredient");
            id = ingredient.getId();
            description.setText(ingredient.getDescription());
            amount.setText(Float.toString(ingredient.getAmount()));
            category.setText(ingredient.getCategory());
            position = extras.getInt("position");

            // setting initial spinner values are a bit weird
            // we have to wait for firebase to get the data from the server
            // thus, we set a location listener on the first data retrieval
            DocumentRetrievalListener<Unit> getUnitListener = new DocumentRetrievalListener<>() {
                @Override
                public void onSuccess(Unit data) {
                    selectedUnit = data;
                }
                @Override
                public void onNullDocument() {
                    // happens if the user deletes a document by themselves. We should not allow it!
                    Log.i(TAG, "Bruh moment: ingredient " + ingredient.getDescription()
                            + " cannot retrieve unit - Document does not exist");
                }
                @Override
                public void onError(Exception e) {
                    Log.d(TAG, "Bruh moment: ingredient cannot retrieve unit: failed ", e);
                }
            };
            ingredient.getUnitAsync(getUnitListener);
        } else {
            // we're adding a new element!
            // autogenerate an ID
            id = UUID.randomUUID().toString();
            getSupportActionBar().setTitle("Add Ingredient");
        }

        unitCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }
            if (queryDocumentSnapshots == null) {
                Log.w(TAG + ":snapshotListener", "Location collection is null!");
                return;
            }
            units.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Unit u = doc.toObject(Unit.class);
                units.add(u);
                Log.i(TAG, "Add unit with date " + u.getUnit() + " " + u.getDateAdded());
            }
            Log.i(TAG, "Added " + units.size() + " elements");
            // sort by date added
            units.sort((l1, l2) -> (int) (l1.getDateAdded().getTime() - l2.getDateAdded().getTime()));
            unitAdapter.notifyDataSetChanged();
            // a bit of a hack...
            if (selectedUnit != null) {
                unitSpinner.setPrompt(selectedUnit.getUnit());
            }
        });

    }

    /**
     * This method handles clicks on the confirm button
     * @param view
     *    The view that this method is connected to (add button)
     */
    public void confirm(View view) {
        if (checkInputs()) {
            DocumentReference unitRef = unitCollection.document(selectedUnit.getUnit());
            RecipeIngredient ingredient = new RecipeIngredient(
                    id,
                    getStr(description),
                    Float.parseFloat(getStr(amount)),
                    unitRef.getPath(),
                    getStr(category),
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
                selectedUnit == null,
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
        editLocationLauncher.launch(intent);
    }

    /**
     * A launcher for a previously-prepared call to start the process of executing edit and updation of ingredient
     *
     * we don't care about the callback! the new location (or deleted locations) will be updated automatically
     * by firebase, via the addSnapshotListener
     */
    final private ActivityResultLauncher<Intent> editLocationLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {}
    );
}
