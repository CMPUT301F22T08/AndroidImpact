package com.androidimpact.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.androidimpact.app.fragments.ShopPickUpFragment;
import com.androidimpact.app.ingredients.IngredientStorageController;
import android.view.View;
import android.widget.Toast;

import com.androidimpact.app.R;
import com.androidimpact.app.ingredients.StoreIngredient;
import com.androidimpact.app.meal_plan.MealPlanController;
import com.androidimpact.app.shopping_list.ShopIngredient;

import com.androidimpact.app.recipes.RecipeController;
import com.androidimpact.app.fragments.IngredientStorageFragment;
import com.androidimpact.app.fragments.MealPlannerFragment;
import com.androidimpact.app.fragments.NavbarFragment;
import com.androidimpact.app.fragments.RecipeListFragment;
import com.androidimpact.app.fragments.ShoppingListFragment;
import com.androidimpact.app.shopping_list.ShoppingListController;
import com.androidimpact.app.shopping_list.automate.ShoppingListAutomator;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the activity Main Activity
 * @version 1.0
 * @author Joshua Ji, Vedant Vyas, Aneeljyot Alagh, Curtis Kan
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    final String TAG = "MainActivity";

    // define ExecutorService (thread pool) so things can run in the background
    // e.g. ShoppingListAutomator
    final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static WeakReference<MainActivity> weakActivity;
    private final IngredientStorageFragment storageFragment = IngredientStorageFragment.newInstance();
    private final ShoppingListFragment shoppingListFragment = ShoppingListFragment.newInstance(executorService);
    private final MealPlannerFragment mealPlannerFragment = MealPlannerFragment.newInstance();
    private final RecipeListFragment recipeListFragment = RecipeListFragment.newInstance(executorService);

    // adding cities to firebase
    IngredientStorageController ingredientStorageController;
    ShoppingListController shoppingListController;
    RecipeController recipeController;
    private MealPlanController mealPlanController;

    // Shopping LIst Automator is stored in the MainActivity as it needs access to some controllers
    ShoppingListAutomator shoppingListAutomator;

    FloatingActionButton navbarFAB;
    Fragment active = storageFragment;
    String userId, userPath;

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
        userId = extras.getString("uid");
        userPath = extras.getString("user-path-firebase");

        View parentLayout = findViewById(R.id.main_activity_layout);

        Toast.makeText(this, "Welcome " + (username == null ? "" : username) + "!", Toast.LENGTH_SHORT).show();

        // retrieve fab BEFORE we run bottomNav.setSelectedItem
        navbarFAB = findViewById(R.id.navbarFAB);

        bottomnav = findViewById(R.id.bottom_navigation_view);
        bottomnav.setBackground(null);
        bottomnav.setOnNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, recipeListFragment, "2").hide(recipeListFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, shoppingListFragment, "3").hide(shoppingListFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, mealPlannerFragment, "4").hide(mealPlannerFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, storageFragment, "1").commit();

        bottomnav.setSelectedItemId(R.id.storage_icon);

        weakActivity = new WeakReference<>(MainActivity.this);

        ingredientStorageController = new IngredientStorageController(this, this.userPath);
        shoppingListController = new ShoppingListController(this, this.userPath);
        recipeController = new RecipeController(this, this.userPath);
        mealPlanController = new MealPlanController(this, this.userPath, this.recipeController, this.ingredientStorageController);

        // initialize ShoppingListAutomator, as it needs access to some controllers
        shoppingListAutomator = new ShoppingListAutomator(
                this.ingredientStorageController,
                this.mealPlanController,
                this.shoppingListController,
                executorService
        );
        shoppingListFragment.addAutomator(shoppingListAutomator);

    }

    /**
     * this method inflates the logout menu
     * @param menu
     * @return (boolean)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    /**
     * This function defines the control flow for logout button selection
     * @param item
     * @return (boolean)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        finish();
        return true;
    }
    /**
     * Sets switching in nav bar
     * @param item
     * @return (boolean) true if item is a valid item of the menu
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

    /**
     * getter method for ingredientStorageController
     * @return ingredientStorageController
     */
    public IngredientStorageController getIngredientStorageController(){
        return ingredientStorageController;
    }

    /**
     * getter method for ShoppingListController
     * @return ShoppingListController
     */
    public ShoppingListController getShoppingListController(){
        return shoppingListController;
    }

    /**
     * getter method for recipeController
     * @return recipeController
     */
    public RecipeController getRecipeController(){
        return recipeController;
    }

    /**
     * getter method for mealPLanController
     * @return mealPlanController
     */
    public MealPlanController getMealPlanController() {
        return this.mealPlanController;
    }

    /**
     * this method returns a weakinstance of mainactivity
     * @return weakActivity<MainActivity>
     */
    public static MainActivity getmInstanceActivity() {
        return weakActivity.get();
    }

    /**
     * this function shows dialog pickup fragment
     * @param ff (ShopPickUpFragment)
     */
    public void showShopPickUpFragment(ShopPickUpFragment ff)
    {
        ff.show(getSupportFragmentManager(), "ADD_FOOD");
    }

    /**
     * This function updates shopIngredient
     * @param ingredient (ShopIngredient)
     */
    public void updateShopIngredient(ShopIngredient ingredient)
    {
        if (ingredient.getAmountPicked() == 0)
            shoppingListFragment.cancelToggleDialog(ingredient);  //if picked up item is zero, then toggle is cancelled
        else
            shoppingListController.addEdit(ingredient);            //otherwise add item to firebase through shopping list controller
    }

    /**
     * this function treats the case when cancel is pressed in dialog fragment
     * @param ingredient
     */
    public void cancelUpdateShopIngredient(ShopIngredient ingredient)
    {
         Log.i("check check", String.valueOf(ingredient.getAmountPicked()));
        shoppingListFragment.cancelToggleDialog(ingredient);
    }

    /**
     * This method takes in a arraylist of shop ingredient and add those ingredients to ingredient storage
     * @param data ArrayList<ShopIngredient>
     */
    public void addShopListToShopIngredient(ArrayList<ShopIngredient> data)
    {

        for (ShopIngredient item: data)
        {
            StoreIngredient ingredient = new StoreIngredient(item);
            //Add items to ingredient storage
            ingredientStorageController.addEdit(ingredient);

            //Delete all the items from Shopping List that were moved
            shoppingListController.delete(item);
        }

        //Maybe move the fragment to storage immediately
        if (data.size() != 0)
        {

            String errorMessage = "Some items moved to Ingredient Storage";
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
        else
        {
            String errorMessage = "No Item Picked Up";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }


    }

    /**
     *  getter method for id
     * @return user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     *  getter method for user path
     * @return userPath
     */
    public String getUserDataPath() {
        return userPath;
    }

}
