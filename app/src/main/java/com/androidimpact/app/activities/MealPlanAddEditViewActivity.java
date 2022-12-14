package com.androidimpact.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidimpact.app.R;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.meal_plan.MealAdapterAddEdit;
import com.androidimpact.app.meal_plan.MealPlan;
import com.androidimpact.app.fragments.IngredientAddFragment;
import com.androidimpact.app.fragments.RecipeAddFragment;
import com.androidimpact.app.meal_plan.MealPlanListAdapter;
import com.androidimpact.app.recipes.Recipe;
import com.androidimpact.app.recipes.RecipeController;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity for adding, viewing, and editing a meal plan
 * @version 1.0
 * @author Aneeljyot Alagh and Clare Chen
 */
public class MealPlanAddEditViewActivity extends AppCompatActivity {

    private HashMap<String, ArrayList<String>> recipeIdMap, ingredientIdMap, recipeTitleMap, ingredientDescriptionMap;
    private HashMap<String, ArrayList<Double>> recipeServingsMap, ingredientServingsMap;
    private Boolean isEditing;
    private RecipeController recipeController;
    Bundle extras;

    private Button breakfastRecipeAdd,breakfastIngredientAdd, lunchRecipeAdd, lunchIngredientAdd,
            dinnerRecipeAdd, dinnerIngredientAdd, snackRecipeAdd, snackIngredientAdd;
    EditText editText;
    String initialDocName, dataPath;
    RecyclerView breakfastListView, lunchListView, dinnerListView, snacksListView;
    MealAdapterAddEdit breakfastAdapter, lunchAdapter, dinnerAdapter, snacksAdapter;
    HashMap<String, MealAdapterAddEdit> adapterAddEditHashMap;

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
        this.recipeTitleMap = new HashMap<>();
        this.ingredientDescriptionMap = new HashMap<>();
        this.recipeServingsMap = new HashMap<>();
        this.ingredientServingsMap = new HashMap<>();
        this.adapterAddEditHashMap = new HashMap<>();

        breakfastRecipeAdd = findViewById(R.id.add_breakfast_recipe);
        lunchRecipeAdd = findViewById(R.id.add_lunch_recipe);
        dinnerRecipeAdd = findViewById(R.id.add_dinner_recipe);
        snackRecipeAdd = findViewById(R.id.add_snack_recipe);
        breakfastIngredientAdd = findViewById(R.id.add_breakfast_ingredient);
        lunchIngredientAdd = findViewById(R.id.add_lunch_ingredient);
        dinnerIngredientAdd = findViewById(R.id.add_dinner_ingredient);
        snackIngredientAdd = findViewById(R.id.add_snack_ingredient);

        // find recyclerviews
        breakfastListView = findViewById(R.id.breakfast_meals);
        lunchListView = findViewById(R.id.lunch_meals);
        dinnerListView = findViewById(R.id.dinner_meals);
        snacksListView = findViewById(R.id.snack_meals);

        editText = findViewById(R.id.editTextMealPlanTitle);

        //!!! Add link to xml here once it exists !!!///

        // extract extras
        extras = getIntent().getExtras();
        if (extras != null) {

            isEditing = extras.getBoolean("isEditing", false);
            MealPlan currentMealPlan = (MealPlan) extras.getSerializable("meal plan");
            getSupportActionBar().setTitle(extras.getString("activity_name"));
            this.dataPath = extras.getString("data-path");

            if (isEditing) {
                editText.setText(currentMealPlan.getDate());
                initialDocName = currentMealPlan.getDate();

                // load data from currentMealPlan into activity set
                String[] mealTypes = {"breakfast", "lunch", "dinner", "snacks"};
                for(String type: mealTypes) {
                    this.recipeIdMap.put(type + "Recipes", currentMealPlan.getRecipeIds(type));
                    this.ingredientIdMap.put(type + "Ingredients", currentMealPlan.getIngredientIds(type));
                    this.recipeTitleMap.put(type + "Recipes", currentMealPlan.getRecipeTitles(type));
                    this.ingredientDescriptionMap.put(type + "Ingredients", currentMealPlan.getIngredientTitles(type));

                    this.recipeServingsMap.put(type + "RecipesServings", currentMealPlan.getRecipeServings(type));
                    this.ingredientServingsMap.put(type + "IngredientsServings", currentMealPlan.getIngredientServings(type));
                }
            }
        }

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        mealPlanCollection = db.document(dataPath).collection("meal-plan");
        recipeCollection = db.document(dataPath).collection("recipes");
        ingredientCollection = db.document(dataPath).collection("ingredientStorage");

        this.recipeController = new RecipeController(this, this.dataPath);

        breakfastRecipeAdd.setOnClickListener(view -> {
            new RecipeAddFragment("breakfastRecipes", this.dataPath).show(
                    getSupportFragmentManager(), "add breakfast recipe to meal plan"
            );
        });
        lunchRecipeAdd.setOnClickListener(view -> {
            new RecipeAddFragment("lunchRecipes", this.dataPath).show(
                    getSupportFragmentManager(), "add lunch recipe to meal plan"
            );
        });
        dinnerRecipeAdd.setOnClickListener(view -> {
            new RecipeAddFragment("dinnerRecipes", this.dataPath).show(
                    getSupportFragmentManager(), "add dinner recipe to meal plan"
            );
        });
        snackRecipeAdd.setOnClickListener(view -> {
            new RecipeAddFragment("snacksRecipes", this.dataPath).show(
                    getSupportFragmentManager(), "add snack recipe to meal plan"
            );
        });

        breakfastIngredientAdd.setOnClickListener(view -> {
            new IngredientAddFragment("breakfastIngredients", this.dataPath).show(
                    getSupportFragmentManager(), "add breakfast ingredient to meal plan"
            );
        });
        lunchIngredientAdd.setOnClickListener(view -> {
            new IngredientAddFragment("lunchIngredients", this.dataPath).show(
                    getSupportFragmentManager(), "add lunch ingredient to meal plan"
            );
        });
        dinnerIngredientAdd.setOnClickListener(view -> {
            new IngredientAddFragment("dinnerIngredients", this.dataPath).show(
                    getSupportFragmentManager(), "add dinner ingredient to meal plan"
            );
        });
        snackIngredientAdd.setOnClickListener(view -> {
            new IngredientAddFragment("snacksIngredients", this.dataPath).show(
                    getSupportFragmentManager(), "add snack ingredient to meal plan"
            );
        });

        String[] keys = {"breakfast", "lunch", "dinner", "snacks"};
        RecyclerView[] recyclerViews = new RecyclerView[]{breakfastListView, lunchListView, dinnerListView, snacksListView};

        for(int i = 0; i < keys.length; i++) {
            ArrayList<String> recipeIds = getOrDefaultString(keys[i] + "Recipes", recipeIdMap);
            ArrayList<String> ingredientIds = getOrDefaultString(keys[i] + "Ingredients", ingredientIdMap);
            ArrayList<String> recipeTitles = getOrDefaultString(keys[i] + "Recipes", recipeTitleMap);
            ArrayList<String> ingredientTitles = getOrDefaultString(keys[i] + "Ingredients", ingredientDescriptionMap);
            ArrayList<Double> recipeServings = getOrDefaultDouble(keys[i] + "RecipesServings", recipeServingsMap);
            ArrayList<Double> ingredientServings = getOrDefaultDouble(keys[i] + "IngredientsServings", ingredientServingsMap);

            MealAdapterAddEdit mealAdapter = new MealAdapterAddEdit(
                    this,
                    recipeTitles,
                    ingredientTitles,
                    recipeServings,
                    ingredientServings
            );

            // below line is to set layout manager for our recycler view.
            LinearLayoutManager manager = new LinearLayoutManager(MealPlanAddEditViewActivity.this);
            recyclerViews[i].setLayoutManager(manager);
            recyclerViews[i].setAdapter(mealAdapter);


            // drag to delete
            ArrayList<String> finalRecipeIds = recipeIds;
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                /**
                 * This method is called when the item is moved
                 *
                 * @param recyclerView
                 * @param viewHolder
                 * @param target
                 * @return
                 */
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                /**
                 * Creates swipe to delete functionality
                 *
                 * @param viewHolder
                 * @param direction
                 */
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    // below line is to get the position
                    // of the item at that position.
                    int position = viewHolder.getAdapterPosition();
                    if (position < finalRecipeIds.size()) {
                        finalRecipeIds.remove(position);
                        recipeTitles.remove(position);
                        recipeServings.remove(position);
                    } else {
                        ingredientIds.remove(position - finalRecipeIds.size());
                        ingredientTitles.remove(position - finalRecipeIds.size());
                        ingredientServings.remove(position - finalRecipeIds.size());
                    }

                    mealAdapter.notifyDataSetChanged();

                }
                // at last we are adding this
                // to our recycler view.
            }).attachToRecyclerView(recyclerViews[i]);
            mealAdapter.notifyDataSetChanged();

            this.adapterAddEditHashMap.put(keys[i], mealAdapter);
        }

    }

    /**
     *
     * @param mealType
     * @param recipeId
     * @param recipeTitle
     * @param f
     */
    public void addRecipe(String mealType, String recipeId, String recipeTitle, double f) {
        Log.i("data got", mealType + recipeId);
        ArrayList<String> entry = this.recipeIdMap.getOrDefault(mealType, new ArrayList<>());
        if(!entry.contains(recipeId)) {
            entry.add(recipeId);
            this.recipeIdMap.put(mealType, entry);

            ArrayList<String> entry2 = this.recipeTitleMap.getOrDefault(mealType, new ArrayList<>());
            entry2.add(recipeTitle);
            this.recipeTitleMap.put(mealType, entry2);

            ArrayList<Double> servings = this.recipeServingsMap.getOrDefault(mealType + "Servings", new ArrayList<>());
            servings.add(f);
            this.recipeServingsMap.put(mealType + "Servings", servings);

            this.adapterAddEditHashMap.values().forEach(
                    adapter -> adapter.notifyDataSetChanged()
            );
        } else {
            Toast.makeText(this, "Invalid request: Recipe already exists in meal plan", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * @param mealType
     * @param ingredientId
     * @param ingredientTitle
     * @param f
     */
    public void addIngredient(String mealType, String ingredientId, String ingredientTitle, double f) {
        Log.i("data got", mealType + ingredientId);
        ArrayList<String> entry = this.ingredientIdMap.getOrDefault(mealType, new ArrayList<>());
        if(!entry.contains(ingredientId)) {
            entry.add(ingredientId);
            this.ingredientIdMap.put(mealType, entry);

            ArrayList<String> entry2 = this.ingredientDescriptionMap.getOrDefault(mealType, new ArrayList<>());
            entry2.add(ingredientTitle);
            this.ingredientDescriptionMap.put(mealType, entry2);

            ArrayList<Double> servings = this.ingredientServingsMap.getOrDefault(mealType + "Servings", new ArrayList<>());
            servings.add(f);
            this.ingredientServingsMap.put(mealType + "Servings", servings);


            this.adapterAddEditHashMap.values().forEach(
                    adapter -> adapter.notifyDataSetChanged()
            );
        } else {
            Toast.makeText(this, "Invalid request: Ingredient already exists in meal plan", Toast.LENGTH_SHORT).show();
        }
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

        String temp = editText.getText().toString();
        String docName = (temp.equals("")) ? "Day x" : temp;

        if(isEditing) {
            mealPlanCollection
                    .document(initialDocName)
                    .delete();
        }

        mealPlanCollection
                .document(docName)
                .set(data);
        setResult(Activity.RESULT_OK);
        finish();
    }

    /**
     *
     * @return
     */
    public RecipeController getRecipeController(){
        return recipeController;
    }

    /**
     *
     * @param key
     * @param map
     * @return
     */
    private ArrayList<String> getOrDefaultString(String key, HashMap<String, ArrayList<String>> map) {

        ArrayList<String> entry = map.get(key);
        if(entry == null) {
            map.put(key, new ArrayList<>());
        }
        return map.get(key);
    }

    /**
     *
     * @param key
     * @param map
     * @return
     */
    private ArrayList<Double> getOrDefaultDouble(String key, HashMap<String, ArrayList<Double>> map) {
        ArrayList<Double> entry = map.get(key);
        if(entry == null) {
            map.put(key, new ArrayList<>());
        }
        return map.get(key);
    }
}