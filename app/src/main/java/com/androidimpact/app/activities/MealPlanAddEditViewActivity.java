package com.androidimpact.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidimpact.app.R;
import com.androidimpact.app.category.Category;
import com.androidimpact.app.meal_plan.AddMealItemContainerFragment;
import com.androidimpact.app.meal_plan.MealPlan;
import com.androidimpact.app.meal_plan.IngredientAddFragment;
import com.androidimpact.app.meal_plan.RecipeAddFragment;
import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.recipes.RecipeController;
import com.androidimpact.app.recipes.RecipeIngredient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Activity for adding, viewing, and editing a meal plan
 * @version 1.0
 * @author Aneeljyot Alagh and Clare Chen
 */
public class MealPlanAddEditViewActivity extends AppCompatActivity {

    private HashMap<String, ArrayList<String>> recipeIdMap, ingredientIdMap;
    private HashMap<String, ArrayList<Float>> recipeServingsMap, ingredientServingsMap;
    private Boolean isEditing;
    private RecipeController recipeController;
    Bundle extras;

    private Button breakfastRecipeAdd,breakfastIngredientAdd, lunchRecipeAdd, lunchIngredientAdd,
            dinnerRecipeAdd, dinnerIngredientAdd, snackRecipeAdd, snackIngredientAdd;

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
        this.ingredientIdMap = new HashMap<>();
        this.recipeServingsMap = new HashMap<>();
        this.ingredientServingsMap = new HashMap<>();

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        mealPlanCollection = db.collection("meal-plan");
        recipeCollection = db.collection("recipes");
        ingredientCollection = db.collection("ingredientStorage");

        this.recipeController = new RecipeController(this);

        breakfastRecipeAdd = findViewById(R.id.add_breakfast_recipe);
        lunchRecipeAdd = findViewById(R.id.add_lunch_recipe);
        dinnerRecipeAdd = findViewById(R.id.add_dinner_recipe);
        snackRecipeAdd = findViewById(R.id.add_snack_recipe);
        breakfastIngredientAdd = findViewById(R.id.add_breakfast_ingredient);
        lunchIngredientAdd = findViewById(R.id.add_lunch_ingredient);
        dinnerIngredientAdd = findViewById(R.id.add_dinner_ingredient);
        snackIngredientAdd = findViewById(R.id.add_snack_ingredient);

        //!!! Add link to xml here once it exists !!!///

        // extract extras
        extras = getIntent().getExtras();
        if (extras != null) {

            isEditing = extras.getBoolean("isEditing", false);
            MealPlan currentMealPlan = (MealPlan) extras.getSerializable("meal plan");

            if (isEditing) {
                getSupportActionBar().setTitle("Edit MealPlan");


            } else {
                // when non editing, make a new collection
                getSupportActionBar().setTitle("Add Meal Plan");
                // initialize defaults
            }


        }

        /*breakfastRecipeAdd.setOnClickListener(view -> {
            new AddMealItemContainerFragment("breakfastRecipes", true).show(
                    getSupportFragmentManager(), "add breakfast recipe to meal plan"
            );
        });*/

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
            new RecipeAddFragment("snacksRecipes").show(
                    getSupportFragmentManager(), "add snack recipe to meal plan"
            );
        });

        breakfastIngredientAdd.setOnClickListener(view -> {
            new IngredientAddFragment("breakfastIngredients").show(
                    getSupportFragmentManager(), "add breakfast ingredient to meal plan"
            );
        });
        lunchIngredientAdd.setOnClickListener(view -> {
            new IngredientAddFragment("lunchIngredients").show(
                    getSupportFragmentManager(), "add lunch ingredient to meal plan"
            );
        });
        dinnerIngredientAdd.setOnClickListener(view -> {
            new IngredientAddFragment("dinnerIngredients").show(
                    getSupportFragmentManager(), "add dinner ingredient to meal plan"
            );
        });
        snackIngredientAdd.setOnClickListener(view -> {
            new IngredientAddFragment("snacksIngredients").show(
                    getSupportFragmentManager(), "add snack ingredient to meal plan"
            );
        });


    }

    public void addRecipe(String mealType, String recipeId) {
        Log.i("data got", mealType + recipeId);
        ArrayList<String> entry = this.recipeIdMap.getOrDefault(mealType, new ArrayList<>());
        entry.add(recipeId);
        this.recipeIdMap.put(mealType, entry);

        ArrayList<Float> servings = this.recipeServingsMap.getOrDefault(mealType + "Servings", new ArrayList<>());
        servings.add(1F);
        this.recipeServingsMap.put(mealType + "Servings", servings);
    }

    public void addIngredient(String mealType, String ingredientId) {
        Log.i("data got", mealType + ingredientId);
        ArrayList<String> entry = this.ingredientIdMap.getOrDefault(mealType, new ArrayList<>());
        entry.add(ingredientId);
        this.ingredientIdMap.put(mealType, entry);

        ArrayList<Float> servings = this.ingredientServingsMap.getOrDefault(mealType + "Servings", new ArrayList<>());
        servings.add(1F);
        this.ingredientServingsMap.put(mealType + "Servings", servings);
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
        this.ingredientIdMap.keySet().forEach(key -> {
            data.put(key, this.ingredientIdMap.get(key));
        });
        this.recipeServingsMap.keySet().forEach(key -> {
            data.put(key, this.recipeServingsMap.get(key));
        });
        this.ingredientServingsMap.keySet().forEach(key -> {
            data.put(key, this.ingredientServingsMap.get(key));
        });
        TextView editText = findViewById(R.id.editTextMealPlanTitle);
        String temp = editText.getText().toString();
        String docName = (temp.equals("")) ? "Day x" : temp;
        mealPlanCollection
                .document(docName)
                .set(data);
        setResult(Activity.RESULT_OK);
        finish();
    }

    public RecipeController getRecipeController(){
        return recipeController;
    }
}