package com.androidimpact.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.androidimpact.app.R;
import com.androidimpact.app.fragments.IngredientStorageFragment;
import com.androidimpact.app.fragments.MealPlannerFragment;
import com.androidimpact.app.fragments.NavbarFragment;
import com.androidimpact.app.fragments.RecipeListFragment;
import com.androidimpact.app.fragments.ShoppingListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * This class is the activity Main Activity
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    // adding cities to firebase
    final String TAG = "MainActivity";

    final IngredientStorageFragment storageFragment = IngredientStorageFragment.newInstance();
    final ShoppingListFragment shoppingListFragment = ShoppingListFragment.newInstance();
    final MealPlannerFragment mealPlannerFragment = MealPlannerFragment.newInstance();
    final RecipeListFragment recipeListFragment = RecipeListFragment.newInstance();
    FloatingActionButton navbarFAB;
    Fragment active = storageFragment;

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

        // retrieve fab BEFORE we run bottomNav.setSelectedItem
        navbarFAB = findViewById(R.id.navbarFAB);

        bottomnav = findViewById(R.id.bottom_navigation_view);
        bottomnav.setBackground(null);

        bottomnav.setOnNavigationItemSelectedListener(this);
        bottomnav.setSelectedItemId(R.id.storage_icon);


        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, recipeListFragment, "2").hide(recipeListFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, shoppingListFragment, "3").hide(shoppingListFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, mealPlannerFragment, "4").hide(mealPlannerFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, storageFragment, "1").commit();

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
}
