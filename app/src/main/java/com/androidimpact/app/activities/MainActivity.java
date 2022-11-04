package com.androidimpact.app.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.androidimpact.app.R;
import com.androidimpact.app.fragments.IngredientStorage;
import com.androidimpact.app.fragments.MealPlanner;
import com.androidimpact.app.fragments.RecipeList;
import com.androidimpact.app.fragments.ShoppingList;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

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


    ShoppingList shoppingListFragment = new ShoppingList();
    MealPlanner mealPlannerFragment = new MealPlanner();


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.storage_icon:
                //IngredientStorage storageFragment = new IngredientStorage();
                getSupportActionBar().setTitle("Ingredient Storage");
                IngredientStorage storageFragment = IngredientStorage.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment, storageFragment, "STORAGE").commit();
                return true;

            case R.id.recipe_icon:
                getSupportActionBar().setTitle("Recipe List");
                RecipeList recipeListFragment = new RecipeList();
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
