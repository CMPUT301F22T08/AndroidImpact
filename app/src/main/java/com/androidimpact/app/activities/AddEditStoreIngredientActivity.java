package com.androidimpact.app.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.androidimpact.app.location.EditLocationsActivity;
import com.androidimpact.app.location.Location;
import com.androidimpact.app.R;
import com.androidimpact.app.StoreIngredient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Activity class for  Adding/Edit/Store Ingredient Activity
 * @version 1.0
 */
public class AddEditStoreIngredientActivity extends AppCompatActivity {
    // TAG: useful for logging
    final String TAG = "AddEditStoreIngredientActivity";
    final String COLLECTION_NAME = "locations";

    // declare all view variables
    private EditText descriptionEditText;
    private EditText amountEditText;
    private Spinner locationSpinner;
    private Location selectedLocation;
    private EditText unitEditText;
    private EditText categoryEditText;
    private EditText bestBeforeEditText;

    // buttons
    private ImageButton editLocationsBtn;

    // Calendar for bestBeforeDatePicker
    final Calendar bestBeforeCalendar = Calendar.getInstance();

    // firebase
    FirebaseFirestore db;
    CollectionReference locationCollection;

    /**
     * Initalizes button data
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient_storage);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        locationCollection = db.collection(COLLECTION_NAME);

        // Init EditText variables
        descriptionEditText = findViewById(R.id.ingredientStoreAdd_description);
        amountEditText = findViewById(R.id.ingredientStoreAdd_amount);
        locationSpinner = findViewById(R.id.ingredientStoreAdd_location);
        unitEditText = findViewById(R.id.ingredientStoreAdd_unit);
        categoryEditText = findViewById(R.id.ingredientStoreAdd_category);
        bestBeforeEditText = findViewById(R.id.ingredientStoreAdd_bestBefore);
        editLocationsBtn = findViewById(R.id.ingredientStoreAdd_editLocationsBtn);

        // init btns
        Button cancelBtn = findViewById(R.id.ingredientStoreAdd_cancelBtn);
        Button confirmBtn = findViewById(R.id.ingredientStoreAdd_confirmBtn);

        // Check Bundle - determine if we're editing or adding!
        // Init activity title
        Bundle extras = getIntent().getExtras();
        StoreIngredient ingredient;
        if (extras != null) {
            ingredient = (StoreIngredient) extras.getSerializable("storeIngredient");
            getSupportActionBar().setTitle("Edit Ingredient");

            // set initial values
            descriptionEditText.setText(ingredient.getDescription());
            amountEditText.setText(String.valueOf(ingredient.getAmount()));
            unitEditText.setText(ingredient.getUnit());
            categoryEditText.setText(ingredient.getCategory());
            bestBeforeCalendar.setTime(ingredient.getBestBeforeDate());

        } else {
            ingredient = null;
            getSupportActionBar().setTitle("Add Ingredient");
        }

        // Initialize location spinner
        ArrayList<Location> locations = new ArrayList<>();
        ArrayAdapter<Location> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        locationSpinner.setAdapter(locationAdapter);

        // EVENT LISTENERS

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            bestBeforeCalendar.set(Calendar.YEAR, year);
            bestBeforeCalendar.set(Calendar.MONTH,month);
            bestBeforeCalendar.set(Calendar.DAY_OF_MONTH,day);
            updateLabel();
        };


        cancelBtn.setOnClickListener(v -> {
            Log.i(TAG + ":cancel", "Cancel ingredient add");
            Intent intent = new Intent(this, IngredientStorageActivity.class);
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        });


        confirmBtn.setOnClickListener(v -> {
            try {
                // try to create an ingredient.
                StoreIngredient newStoreIngredient = createIngredient(ingredient);
                Intent intent = new Intent(AddEditStoreIngredientActivity.this, IngredientStorageActivity.class);

                // put the ingredient as an extra to our intent before we pass it back to the IngredientStorage
                intent.putExtra("ingredient", newStoreIngredient);
                setResult(Activity.RESULT_OK, intent);

                Log.i(TAG + ":cancel", "Returning to MainActivity");
                finish();
            } catch (Exception e){
                // Error - add a snackBar
                Log.i(TAG, "Error making storeIngredient", e);
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Error making storeIngredient!", Snackbar.LENGTH_LONG)
                        .setAction("Ok", view1 -> {})
                        .show();
            }
        });


        bestBeforeEditText.setOnClickListener(view -> new DatePickerDialog(
                AddEditStoreIngredientActivity.this,
                date,
                bestBeforeCalendar.get(Calendar.YEAR),
                bestBeforeCalendar.get(Calendar.MONTH),
                bestBeforeCalendar.get(Calendar.DAY_OF_MONTH)).show());


        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedLocation = (Location) parentView.getItemAtPosition(position);

                Log.i(TAG, "selected location is " + selectedLocation.getLocation());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.i(TAG, "Nothing selected");
            }
        });


        locationCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.w(TAG + ":snapshotListener", "Listen failed.", error);
                return;
            }
            if (queryDocumentSnapshots == null) {
                Log.w(TAG + ":snapshotListener", "Location collection is null!");
                return;
            }
            locations.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                locations.add(doc.toObject(Location.class));
            }
            // sort by date added
            locations.sort((l1, l2) -> (int) (l1.getDateAdded().getTime() - l2.getDateAdded().getTime()));
            locationAdapter.notifyDataSetChanged();
        });
    }

    /**
     * This will set the date label
     */
    private void updateLabel(){
        String myFormat="dd MMMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        bestBeforeEditText.setText(dateFormat.format(bestBeforeCalendar.getTime()));
    }

    /**
     * This is run when R.id.ingredientStoreAdd_editLocationsBtn is clicked
     *
     * this function jumps to the EditLocations activity.
     */
    public void editLocations(View view) {
        Log.i(TAG + ":editLocations", "Going to Edit Locations");
        Intent intent = new Intent(this, EditLocationsActivity.class);
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

    /**
     * Validates the data input by the user
     * Create a StoreIngredient object if the data is valid.
     * @param editing (StoreIngredient) Possible storeIngredient we are editing
     * @return A StoreIngredient object created using the data from the editTexts
     * @throws Exception if the data is invalid or if the Ingredient could not be created
     * @see StoreIngredient
     */
    // Should we throw an exception or have a snack-bar that says fields can't be empty?
    private StoreIngredient createIngredient(@Nullable StoreIngredient editing) throws Exception {
        String description = descriptionEditText.getText().toString();
        String amountRaw = amountEditText.getText().toString();
        String unit = unitEditText.getText().toString();
        String category = categoryEditText.getText().toString();
        String bestBefore = bestBeforeEditText.getText().toString();

        if (description.equals("")) {
            throw new Exception("Description cannot be empty.");
        }

        if (amountRaw.equals("")) {
            throw new Exception("Amount cannot be empty.");
        }

        float amount;
        try {
            amount = Float.parseFloat(amountRaw);
        } catch (Exception e) {
            throw new Exception("Amount must be a float!");
        }

        if (amount < 0) {
            throw new Exception("Amount must be positive!");
        }

        if (unit.equals("")) {
            throw new Exception("Unit cannot be empty.");
        }

        if (category.equals("")) {
            throw new Exception("Category cannot be empty.");
        }

        if (bestBefore.equals("")) {
            throw new Exception("Best before cannot be empty.");
        }

        if (bestBeforeCalendar.compareTo(Calendar.getInstance()) <= 0) {
            throw new Exception("Best before must be a future date.");
        }

        try {
            String id;
            if (editing != null) {
                id = editing.getId();
            } else {
                UUID uuid = UUID.randomUUID();
                id = uuid.toString();
            }
            Date date = bestBeforeCalendar.getTime();
            DocumentReference locationRef = locationCollection.document(selectedLocation.getId());
            return new StoreIngredient(id, description, amount, unit, category, date, locationRef.getPath());
        } catch(Exception e) {
            Log.i(TAG, "Error parsing ingredients", e);
            throw new Exception("Error parsing ingredients");
        }
    }
}