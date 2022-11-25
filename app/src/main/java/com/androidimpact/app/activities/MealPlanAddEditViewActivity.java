package com.androidimpact.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidimpact.app.R;
import com.androidimpact.app.meal_plan.RecipeAddFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity for adding, viewing, and editing a meal plan
 * @version 1.0
 * @author Aneeljyot Alagh and Clare Chen
 */
public class MealPlanAddEditViewActivity extends AppCompatActivity {

    private HashMap<String, ArrayList<String>> recipeIdMap;
    private FloatingActionButton breakfastRecipeAdd, lunchRecipeAdd, dinnerRecipeAdd, snackRecipeAdd;

    // adding recipes to firebase
    FirebaseFirestore db;
    CollectionReference mealPlanCollection;
    CollectionReference recipeCollection;
    CollectionReference ingredientCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan_add_edit_view);

        this.recipeIdMap = new HashMap<>();
        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        mealPlanCollection = db.collection("meal-plan");
        recipeCollection = db.collection("recipes");
        ingredientCollection = db.collection("ingredientStorage");

        breakfastRecipeAdd = findViewById(R.id.add_breakfast);
        lunchRecipeAdd = findViewById(R.id.add_lunch);
        dinnerRecipeAdd = findViewById(R.id.add_dinner);
        snackRecipeAdd = findViewById(R.id.add_snack);

        breakfastRecipeAdd.setOnClickListener(view -> {
            new RecipeAddFragment("breakfastRecipes").show(
                    getSupportFragmentManager(), "add breakfast recipe to meal plan"
            );
        });
        lunchRecipeAdd.setOnClickListener(view -> {
            new RecipeAddFragment("lunchRecipes").show(
                    getSupportFragmentManager(), "add lunch recipe to meal plan"
            );
        });
        dinnerRecipeAdd.setOnClickListener(view -> {
            new RecipeAddFragment("dinnerRecipes").show(
                    getSupportFragmentManager(), "add dinner recipe to meal plan"
            );
        });
        snackRecipeAdd.setOnClickListener(view -> {
            new RecipeAddFragment("snackRecipes").show(
                    getSupportFragmentManager(), "add snack recipe to meal plan"
            );
        });


    }

    public void addRecipe(String mealType, String recipeId) {
        Log.i("data got", mealType + recipeId);
        ArrayList<String> entry = this.recipeIdMap.getOrDefault(mealType, new ArrayList<>());
        entry.add(recipeId);
        this.recipeIdMap.put(mealType, entry);
    }

    /**
     * onClick method for cancel
     * @param view
     *    view that calls this method
     */
    public void cancel(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    /**
     * onClick method for confirm
     * @param view
     *    view that calls this method
     */
    public void confirm(View view) {
        Map<String, Object> data = new HashMap<>();
        this.recipeIdMap.keySet().forEach(key -> {
            data.put(key, this.recipeIdMap.get(key));
        });
        mealPlanCollection
                .document("Friday")
                .set(this.recipeIdMap);
        setResult(Activity.RESULT_OK);
        finish();
    }
}