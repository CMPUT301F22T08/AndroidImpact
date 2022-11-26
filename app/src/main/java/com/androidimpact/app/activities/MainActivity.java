package com.androidimpact.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.androidimpact.app.fragments.ShopPickUpFragment;
import com.androidimpact.app.ingredients.IngredientStorage;
import com.androidimpact.app.ingredients.IngredientStorageController;
import android.view.View;
import android.widget.Toast;
import android.widget.Switch;

import com.androidimpact.app.R;
import com.androidimpact.app.meal_plan.MealPlanController;
import com.androidimpact.app.shopping_list.ShopIngredient;
import com.androidimpact.app.recipes.Recipe;

import com.androidimpact.app.recipes.RecipeController;
import com.androidimpact.app.fragments.IngredientStorageFragment;
import com.androidimpact.app.fragments.MealPlannerFragment;
import com.androidimpact.app.fragments.NavbarFragment;
import com.androidimpact.app.fragments.RecipeListFragment;
import com.androidimpact.app.fragments.ShoppingListFragment;
import com.androidimpact.app.shopping_list.ShoppingListController;
import com.androidimpact.app.recipes.RecipeList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * This class is the activity Main Activity
 * @version 1.0
 * @author Vedant Vyas
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    final String TAG = "MainActivity";
    public static WeakReference<MainActivity> weakActivity;

    // adding cities to firebase
    final IngredientStorageFragment storageFragment = IngredientStorageFragment.newInstance();
    final ShoppingListFragment shoppingListFragment = ShoppingListFragment.newInstance();
    final MealPlannerFragment mealPlannerFragment = MealPlannerFragment.newInstance();
    final RecipeListFragment recipeListFragment = RecipeListFragment.newInstance();


    final IngredientStorageController ingredientStorageController = new IngredientStorageController(this);
    final ShoppingListController shoppingListController = new ShoppingListController(this);
    final RecipeController recipeController = new RecipeController(this);
    private MealPlanController mealPlanController;

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

        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");

        View parentLayout = findViewById(R.id.main_activity_layout);

        Toast.makeText(this, "Welcome " + username + "!", Toast.LENGTH_SHORT).show();

        // retrieve fab BEFORE we run bottomNav.setSelectedItem
        navbarFAB = findViewById(R.id.navbarFAB);

        bottomnav = findViewById(R.id.bottom_navigation_view);
        bottomnav.setBackground(null);

        bottomnav.setOnNavigationItemSelectedListener(this);
        bottomnav.setSelectedItemId(R.id.storage_icon);

        weakActivity = new WeakReference<>(MainActivity.this);

        mealPlanController = new MealPlanController(this, this.recipeController, this.ingredientStorageController);


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
                mealPlannerFragment.refreshMealItems();
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

    public ShoppingListController getShoppingListController(){
        return shoppingListController;
    }

    public RecipeController getRecipeController(){
        return recipeController;
    }

    public MealPlanController getMealPlanController() {
        return this.mealPlanController;
    }

    public static MainActivity getmInstanceActivity() {
        return weakActivity.get();
    }

    public void showShopPickUpFragment(ShopPickUpFragment ff)
    {
        ff.show(getSupportFragmentManager(), "ADD_FOOD");
    }

    public void updateShopIngredient(ShopIngredient ingredient)
    {
        getSupportActionBar().setTitle("Shopping List");
        updateActiveFragment(shoppingListFragment);
        //call a function in shoppingListFragment which does the data updation
//        if (ingredient.getAmountPicked() != 0)
        shoppingListFragment.editShopIngredientFB(ingredient);
//        else
//            cancelUpdateShopIngredient(ingredient);
    }

    public void cancelUpdateShopIngredient(ShopIngredient ingredient)
    {
       // shoppingListFragment.cancelPickUp();

        getSupportActionBar().setTitle("Shopping List");
        updateActiveFragment(shoppingListFragment);
        shoppingListFragment.editShopIngredientFB(ingredient);
//        Switch pickup = findViewById(R.id.shop_ingredient_switch);
//        pickup.setChecked(false);
    }

}
