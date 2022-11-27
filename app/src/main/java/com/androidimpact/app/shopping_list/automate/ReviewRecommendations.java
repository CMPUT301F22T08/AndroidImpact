package com.androidimpact.app.shopping_list.automate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidimpact.app.R;
import com.androidimpact.app.category.Category;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.location.Location;
import com.androidimpact.app.shopping_list.ShopIngredient;
import com.androidimpact.app.shopping_list.ShopIngredientAdapter;
import com.androidimpact.app.unit.Unit;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ReviewRecommendations extends AppCompatActivity {
    private final String TAG = "ReviewRecommendations";

    ArrayList<ShopIngredient> recommendations;
    ShopIngredientAdapter shopIngredientViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_recommendations);

        RecyclerView recommendationsList = findViewById(R.id.review_recommendations_list);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recommendations = (ArrayList<ShopIngredient>) extras.getSerializable("ingredients");
            getSupportActionBar().setTitle("Review Recommendations");

            // adapter
            shopIngredientViewAdapter = new ShopIngredientAdapter(this, recommendations);
            // below line is to set layout manager for our recycler view.
            LinearLayoutManager manager = new LinearLayoutManager(this);
            recommendationsList.setLayoutManager(manager);
            recommendationsList.setAdapter(shopIngredientViewAdapter);
        } else {
            Log.i(TAG, "Error: bundle extras is null!");
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        }
    }

    /**
     * Cancel - This is run when the "Cancel" button is pressed
     * @param view
     *     The view that triggers the method
     */
    public void cancel(View view) {
        Log.i(TAG + ":cancel", "Cancel ingredient add");
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    /**
     * Confirm - This is run when the "Confirm" button is pressed
     * @param view
     *     The view that triggers the method
     */
    public void confirm(View view) {
        try {
            // try to create an ingredient.
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            Log.i(TAG + ":confirm", "Returning to MainActivity");
            finish();
        } catch (Exception e){
            // Error - add a snackBar
            Log.i(TAG, "Error making storeIngredient", e);
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
}