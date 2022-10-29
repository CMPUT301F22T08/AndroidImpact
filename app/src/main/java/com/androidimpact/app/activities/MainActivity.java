package com.androidimpact.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.androidimpact.app.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    // TAG: useful for logging
    final String TAG = "MainActivity";

    //FireStore
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        Button ingredientStorageButton = findViewById(R.id.ButtonFromMain_ingredientStorage);
        ingredientStorageButton.setOnClickListener(v -> {
            Log.i(TAG + ":StoreIngredient", "Opening Storage!");
            Intent intent = new Intent(MainActivity.this, IngredientStorageActivity.class);
            startActivity(intent);
        });
    }
}
