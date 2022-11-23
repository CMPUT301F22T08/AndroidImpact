package com.androidimpact.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.androidimpact.app.IngredientStorageController;
import android.util.Log;
import android.view.View;

import com.androidimpact.app.IngredientStorage;
import com.androidimpact.app.MealPlanList;
import com.androidimpact.app.R;
import com.androidimpact.app.Recipe;
import com.androidimpact.app.RecipeList;
import com.androidimpact.app.fragments.IngredientStorageFragment;
import com.androidimpact.app.fragments.MealPlannerFragment;
import com.androidimpact.app.fragments.NavbarFragment;
import com.androidimpact.app.fragments.RecipeListFragment;
import com.androidimpact.app.fragments.ShoppingListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This class is the activity Main Activity
 * @version 1.0
 * @author Vedant Vyas
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    // adding cities to firebase
    final String TAG = "MainActivity";
    final IngredientStorageFragment storageFragment = IngredientStorageFragment.newInstance();
    final ShoppingListFragment shoppingListFragment = ShoppingListFragment.newInstance();
    MealPlannerFragment mealPlannerFragment;// mealPlannerFragment = MealPlannerFragment.newInstance();
    final RecipeListFragment recipeListFragment = RecipeListFragment.newInstance();


    final IngredientStorageController ingredientStorageController = new IngredientStorageController(this);

    FloatingActionButton navbarFAB;
    Fragment active = storageFragment;

    RecipeList recipeList;
    IngredientStorage ingredientStorage;
    MealPlanList mealPlanList;

    BottomNavigationView bottomnav;

    /**
     * Initalize the fragments
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("AndroidImpact");

        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");

        View parentLayout = findViewById(R.id.main_activity_layout);
        Snackbar.make(parentLayout, "Welcome " + username + "!", Snackbar.LENGTH_SHORT)
                .setAction("Ok", view1 -> {})
                .show();

        ArrayList<Recipe> recipes = new ArrayList<>();
        recipeList = new RecipeList(recipes);
        Log.i("recipelistboom", recipeList.getData().toString());
        ingredientStorage = new IngredientStorage();

        // retrieve fab BEFORE we run bottomNav.setSelectedItem
        navbarFAB = findViewById(R.id.navbarFAB);

        bottomnav = findViewById(R.id.bottom_navigation_view);
        bottomnav.setBackground(null);

        bottomnav.setOnNavigationItemSelectedListener(this);
        bottomnav.setSelectedItemId(R.id.storage_icon);

        this.mealPlannerFragment = MealPlannerFragment.newInstance();

        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, recipeListFragment, "2").hide(recipeListFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, shoppingListFragment, "3").hide(shoppingListFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, mealPlannerFragment, "4").hide(mealPlannerFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, storageFragment, "1").commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
    /**
     * Sets switching in nav bar
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.storage_icon:
                getSupportActionBar().setTitle("Ingredient Storage");
                updateActiveFragment(storageFragment);
                return true;

            case R.id.recipe_icon:
                getSupportActionBar().setTitle("Recipe List");
                updateActiveFragment(recipeListFragment);
                return true;

            case R.id.cart_icon:
                getSupportActionBar().setTitle("Shopping List");
                updateActiveFragment(shoppingListFragment);
                return true;

            case R.id.meal_icon:
                getSupportActionBar().setTitle("Meal Plan");
                updateActiveFragment(mealPlannerFragment);
                return true;
        }
        return false;
    }

    /**
     * Updates the active fragment to a new fragment
     * @param newFragment
     * @param <T>
     */
    private <T extends Fragment & NavbarFragment> void updateActiveFragment(T newFragment) {
        getSupportFragmentManager().beginTransaction().hide(active).show(newFragment).commit();
        active = newFragment;
        newFragment.setFabListener(navbarFAB);
    }


    public IngredientStorageController getIngredientStorageController(){
        return ingredientStorageController;
    }

    public RecipeList getRecipeList() {
        return this.recipeList;
    }

    public IngredientStorage getIngredientStorage() {
        return this.ingredientStorage;
    }
}
