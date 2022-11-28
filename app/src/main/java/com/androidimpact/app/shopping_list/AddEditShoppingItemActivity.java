package com.androidimpact.app.shopping_list;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.androidimpact.app.NullableSpinnerAdapter;
import com.androidimpact.app.R;
import com.androidimpact.app.Timestamped;
import com.androidimpact.app.category.Category;
import com.androidimpact.app.category.EditCategoriesActivity;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.location.EditLocationsActivity;
import com.androidimpact.app.location.Location;
import com.androidimpact.app.unit.EditUnitsActivity;
import com.androidimpact.app.unit.Unit;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class AddEditShoppingItemActivity extends AppCompatActivity {

    // TAG: useful for logging
    final String TAG = "AddShoppingListItemActivity";

    // declare all view variables
    private EditText descriptionEditText;
    private EditText amountEditText;

    // spinners
    // NOTE: for these, the source of truth is the selectedLocation and selectedUnit
    // due to abstractOnItemSelectedListener editing these references,
    // i "have to" wrap these under AtomicReferences
    private Spinner unitSpinner;
    private AtomicReference<Unit> selectedUnit = new AtomicReference<>();
    private Spinner categorySpinner;
    private AtomicReference<Category> selectedCategory = new AtomicReference<>();

    // firebase
    FirebaseFirestore db;
    CollectionReference shoppingListCollection;
    CollectionReference unitCollection;
    CollectionReference categoryCollection;
    String userPath;

    // Other "global" variables
    // used to track which ingredient we're editing, null if we're creating a new ingredient
    ShopIngredient currentIngredient;

    /**
     * Initalizes button data
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_shopping_list_item);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            userPath = extras.getString("data-path");
        }

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        unitCollection = db.document(userPath).collection("units");
        categoryCollection = db.document(userPath).collection("categories");
        shoppingListCollection = db.document(userPath).collection("shoppingList");

        // Init EditText views
        descriptionEditText = findViewById(R.id.shopping_item_addEdit_description);
        amountEditText = findViewById(R.id.shopping_item_addEdit_amount);

        // Init spinner views
        unitSpinner = findViewById(R.id.shopping_item_addEdit_unit);
        categorySpinner = findViewById(R.id.shopping_item_addEdit_category);

        // init spinners

        ArrayList<Unit> units = new ArrayList<>();
        NullableSpinnerAdapter<Unit> unitAdapter = new NullableSpinnerAdapter<>(this, units);
        unitSpinner.setAdapter(unitAdapter);

        ArrayList<Category> categories = new ArrayList<>();
        NullableSpinnerAdapter<Category> categoryAdapter = new NullableSpinnerAdapter<>(this, categories);
        categorySpinner.setAdapter(categoryAdapter);

        // Check Bundle - determine if we're editing or adding!
        // Init activity title
        //Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean isAdding = extras.getBoolean("adding");

            if (!isAdding) {
                currentIngredient = (ShopIngredient) extras.getSerializable("ingredient");
                getSupportActionBar().setTitle("Edit Shopping List Item");

                // set initial values
                descriptionEditText.setText(currentIngredient.getDescription());
                amountEditText.setText(String.valueOf(currentIngredient.getAmount()));

                // set initial unit, category and locations
                // note that we store the unit as a string, not a document path
                selectedUnit.set(new Unit(currentIngredient.getUnit()));
                Log.i(TAG, "Set unit: " + selectedUnit.get());
                selectedCategory.set(new Category(currentIngredient.getCategory()));
                Log.i(TAG, "Set category: " + selectedCategory.get());
            } else {
                currentIngredient = null;
                getSupportActionBar().setTitle("Add Shopping List Item");
            }
        }

        // EVENT LISTENERS
        unitSpinner.setOnItemSelectedListener(abstractOnItemSelectedListener(selectedUnit));
        categorySpinner.setOnItemSelectedListener(abstractOnItemSelectedListener(selectedCategory));

        unitCollection.addSnapshotListener(abstractSnapshotListener(Unit.class, unitAdapter, units, unitSpinner, selectedUnit, "Unit"));
        categoryCollection.addSnapshotListener(abstractSnapshotListener(Category.class, categoryAdapter, categories, categorySpinner, selectedCategory, "Category"));
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
                Log.w(TAG + ":snapshotListener", "Location collection is null!");
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
            // a bit of a hack to get the spinner to show the correct element
            if (selectedElem.get() != null) {
                int idx = data.indexOf(selectedElem.get());
                // add 1 to the spinner - this allows us to select the "null" element
                spinner.setSelection(idx + 1);
                Log.i(TAG, "SnapshotListener: " + selectedElem.get() + " " + selectedElem.get().getClass() + " - (" + data.indexOf(selectedElem.get()) + ")");
                if (idx == -1) {
                    generateSnackbar("Warning: " + debugName + " '" + selectedElem.get() + "' was not found in the database!");
                    selectedElem.set(null);
                }
            }
        };
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
     * This is run when R.id.shopping_item_addEdit_editLocationsBtn is clicked
     *
     * this function jumps to the EditLocations activity.
     */
    public void editLocations(View view) {
        Log.i(TAG + ":editLocations", "Going to Edit Locations");
        Intent intent = new Intent(this, EditLocationsActivity.class);
        discardResultLauncher.launch(intent);
    }

    /**
     * This is run when R.id.shopping_item_addEdit_editUnitsBtn is clicked
     *
     * this function jumps to the EditLocations activity.
     */
    public void editUnits(View view) {
        Log.i(TAG + ":editUnits", "Going to Edit units");
        Intent intent = new Intent(this, EditUnitsActivity.class);
        discardResultLauncher.launch(intent);
    }

    /**
     * This is run when R.id.shopping_item_addEdit_editUnitsBtn is clicked
     *
     * this function jumps to the EditLocations activity.
     */
    public void editCategories(View view) {
        Log.i(TAG + ":editUnits", "Going to Edit units");
        Intent intent = new Intent(this, EditCategoriesActivity.class);
        discardResultLauncher.launch(intent);
    }

    /**
     * Cancel - This is run when the "Cancel" button is pressed
     */
    public void cancel(View view) {
        Log.i(TAG + ":cancel", "Cancel addEditShoppingListItem");
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    /**
     * Confirm - This is run when the "Confirm" button is pressed
     */
    public void confirm(View view) {
        try {
            // try to create an ingredient.
            ShopIngredient ingredient = createIngredient(currentIngredient);
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);

            intent.putExtra("ingredient", ingredient);
            Log.i(TAG + ":confirm", "Returning to parent activity");
            finish();
        } catch (Exception e){
            // Error - add a snackBar
            Log.i(TAG, "Error making shopIngredient", e);
            generateSnackbar(e.getMessage());
        }
    }

    /**
     * Helper function to make a snackbar
     */
    private void generateSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setAction("Ok", view1 -> {})
                .show();
    }

    /**
     * A launcher for any activity where we can discard the result
     *
     * This is useful for things like editLocations, editUnits and editCategories
     *
     * Note we don't care about the callback! the new data (e.g. location) will be updated automatically
     * by firebase, via the addSnapshotListener
     */
    final private ActivityResultLauncher<Intent> discardResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {}
    );

    /**
     * Validates the data input by the user
     * Create a StoreIngredient object if the data is valid.
     * @param editing (ShopIngredient) Possible shopIngredient we are editing
     * @return A StoreIngredient object created using the data from the editTexts
     * @throws Exception if the data is invalid or if the Ingredient could not be created
     * @see StoreIngredient
     */
    // Should we throw an exception or have a snack-bar that says fields can't be empty?
    private ShopIngredient createIngredient(@Nullable ShopIngredient editing) throws Exception {
        String description = descriptionEditText.getText().toString();
        String amountRaw = amountEditText.getText().toString();

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

        if (selectedUnit.get() == null) {
            throw new Exception("Unit must be non null");
        }
        if (selectedCategory.get() == null) {
            throw new Exception("Category must be non null");
        }

        try {
            String id;
            if (editing != null) {
                id = editing.getId();
            } else {
                // autogenerate ID
                id = UUID.randomUUID().toString();
            }

            // get values
            String unit = selectedUnit.get().getUnit();
            String category = selectedCategory.get().getCategory();
            return new ShopIngredient(id, description, amount, unit, category);
        } catch(Exception e) {
            Log.i(TAG, "Error parsing ingredients", e);
            throw new Exception("Error parsing ingredients");
        }
    }
}