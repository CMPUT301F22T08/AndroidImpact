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

    // adding cities to firebase
    final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("PAIN");

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
}
