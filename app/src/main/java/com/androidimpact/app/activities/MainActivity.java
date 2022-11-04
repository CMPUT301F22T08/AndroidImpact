package com.androidimpact.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.androidimpact.app.R;
import com.androidimpact.app.fragments.IngredientStorageFragment;
import com.androidimpact.app.fragments.MealPlannerFragment;
import com.androidimpact.app.fragments.RecipeListFragment;
import com.androidimpact.app.fragments.ShoppingListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    // adding cities to firebase
    final String TAG = "MainActivity";


    BottomNavigationView bottomnav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("AndroidImpact");


        bottomnav = findViewById(R.id.bottom_navigation_view);
        bottomnav.setBackground(null);

        bottomnav.setOnNavigationItemSelectedListener(this);
        bottomnav.setSelectedItemId(R.id.storage_icon);


        //navController = findNavController(R.id.nav_fragment)
    }


    ShoppingListFragment shoppingListFragment = new ShoppingListFragment();
    MealPlannerFragment mealPlannerFragment = new MealPlannerFragment();


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.storage_icon:
                //IngredientStorage storageFragment = new IngredientStorage();
                getSupportActionBar().setTitle("Ingredient Storage");
                IngredientStorageFragment storageFragment = IngredientStorageFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment, storageFragment, "STORAGE").commit();
                return true;

            case R.id.recipe_icon:
                getSupportActionBar().setTitle("Recipe List");
                RecipeListFragment recipeListFragment = new RecipeListFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment, recipeListFragment, null).commit();
                return true;

            case R.id.cart_icon:
                getSupportActionBar().setTitle("Shopping List");
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment, shoppingListFragment, null).commit();
                return true;

            case R.id.meal_icon:
                getSupportActionBar().setTitle("Meal Plan");
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment, mealPlannerFragment, null).commit();
                return true;
        }
        return false;
    }
}
