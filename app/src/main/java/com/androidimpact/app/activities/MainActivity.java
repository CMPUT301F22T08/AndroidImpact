package com.androidimpact.app.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.androidimpact.app.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    // Declare the variables so that you will be able to reference it later.
    RecyclerView ingredientListView;
    StoreIngredientViewAdapter storeingredientViewAdapter;
    //ArrayList<Ingredient> ingredientDataList;
    IngredientStorage ingredientDataList;

    Button EditButton;

    // adding cities to firebase
    final String TAG = "MainActivity";

    //FireStore
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if(Build.VERSION.SDK_INT >= 21) {
//            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.purple_700));
//        }
        getSupportActionBar().setTitle("PAIN");
        // initialize Firestore
        db = FirebaseFirestore.getInstance();

        Button ingredientStorageButton = findViewById(R.id.ButtonFromMain_ingredientStorage);
        ingredientStorageButton.setOnClickListener(v -> {
            Log.i(TAG + ":StoreIngredient", "Opening Storage!");
            Intent intent = new Intent(MainActivity.this, IngredientStorageActivity.class);
            startActivity(intent);
        });

        Button recipeButton = findViewById(R.id.ButtonFromMain_recipe);
        recipeButton.setOnClickListener(v -> {
            Log.i(TAG + ":StoreIngredient", "Opening Storage!");
            Intent intent = new Intent(MainActivity.this, RecipeAddViewEditActivity.class);
            startActivity(intent);
        });
    }

    /**
     * AddIngredientLauncher uses the ActivityResultAPIs to handle data returned from
     * AddStoreIngredientActivity
     */
    final private ActivityResultLauncher<Intent> addStoreIngredientLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Bundle bundle = result.getData().getExtras();
                Log.i(TAG + ":addIngredientResult", "Got bundle");

                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Ok - we have an ingredient!
                    Ingredient ingredient = (Ingredient) bundle.getSerializable("ingredient");
                    Log.i(TAG + ":addIngredientResult", ingredient.getDescription());
                    ingredientsCollection.add(ingredient);
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // cancelled request - do nothing.
                    Log.i(TAG + ":addIngredientResult", "Received cancelled");
                }
            });

    /**
     * ADD STORE INGREDIENT
     *
     * This is executed when the Add ingredient FAB is clicked. It redirects to a new activity.
     * This new activity is basically just a form that creates a new ingredient in the storage
     * Since this activity returns data, we need to use the `ActivityResultLauncher` APIs
     *
     * @param view
     */
    public void addStoreIngredient(View view)  {
        Log.i(TAG + ":addStoreIngredient", "Adding ingredient!");
        Intent intent = new Intent(this, AddStoreIngredientActivity.class);
        addStoreIngredientLauncher.launch(intent);
    }



    /**
     * ADD STORE INGREDIENT
     *
     * This is executed when the Add ingredient FAB is clicked. It redirects to a new activity.
     * This new activity is basically just a form that creates a new ingredient in the storage
     * Since this activity returns data, we need to use the `ActivityResultLauncher` APIs
     *
     * @param view
     */
    public void editStoreIngredient(View view)  {
        Log.i(TAG + ":addStoreIngredient", "Adding ingredient!");
        Intent intent = new Intent(this, AddStoreIngredientActivity.class);
        addStoreIngredientLauncher.launch(intent);
    }
}
