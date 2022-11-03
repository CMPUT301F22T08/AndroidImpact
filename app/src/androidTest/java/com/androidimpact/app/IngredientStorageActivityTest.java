package com.androidimpact.app;

import android.content.res.Resources;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.androidimpact.app.activities.AddEditStoreIngredientActivity;
import com.androidimpact.app.activities.IngredientStorageActivity;
import com.androidimpact.app.activities.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class IngredientStorageActivityTest {
    private Solo solo;
    private Resources resources;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        resources = InstrumentationRegistry.getInstrumentation().getContext().getResources();
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @Test
    public void testIngredientStorageLaunch(){
        solo.assertCurrentActivity("Should be in MainActivity!", MainActivity.class);
        MainActivity activity = (MainActivity) solo.getCurrentActivity();
        Button goToStorage = activity.findViewById(R.id.ButtonFromMain_ingredientStorage);
        solo.clickOnView(goToStorage);
        solo.assertCurrentActivity("Wrong Activity after clicking IngredientStorage Button: Should be in IngredientStorageActivity!", IngredientStorageActivity.class);
    }

    /**
     * Checks that clicking the "add ingredient" fab directs us to the addStoreIngredientActivity
     * and that clicking "cancel" directs us to back to the original activity
     */
    @Test
    public void addIngredientAndCancel() {
        MainActivity activity = (MainActivity) solo.getCurrentActivity();
        Button goToStorage = activity.findViewById(R.id.ButtonFromMain_ingredientStorage);
        solo.clickOnView(goToStorage);

        solo.assertCurrentActivity("Should be in IngredientStorageActivity!", IngredientStorageActivity.class);
        // Why must solo be so slow
        solo.waitForActivity(IngredientStorageActivity.class,10000);
        IngredientStorageActivity a = (IngredientStorageActivity) solo.getCurrentActivity();

        FloatingActionButton fab = a.findViewById(R.id.addStoreIngredientFAB);
        solo.clickOnView(fab);

        solo.assertCurrentActivity("Wrong activity after clicking FAB: Should be in AddStoreIngredientActivity", AddEditStoreIngredientActivity.class);
        solo.clickOnButton("Cancel");
        solo.assertCurrentActivity("Wrong activity after clicking cancel: Should be in IngredientStorageActivity", IngredientStorageActivity.class);
    }


    /**
     * Checks that adding a valid ingredient redirects us back to the activity
     */
    @Test
    public void addIngredientSuccess() {
        MainActivity activity = (MainActivity) solo.getCurrentActivity();
        Button goToStorage = activity.findViewById(R.id.ButtonFromMain_ingredientStorage);
        solo.clickOnView(goToStorage);

        solo.waitForActivity(IngredientStorageActivity.class,10000);
        solo.assertCurrentActivity("Should be in IngredientStorageActivity!", IngredientStorageActivity.class);
        // Why must solo be so slow
        IngredientStorageActivity a1 = (IngredientStorageActivity) solo.getCurrentActivity();
        FloatingActionButton fab = a1.findViewById(R.id.addStoreIngredientFAB);
        solo.clickOnView(fab);

        solo.waitForActivity(AddEditStoreIngredientActivity.class,10000);
        solo.assertCurrentActivity("Wrong activity after clicking FAB: Should be in AddStoreIngredientActivity", AddEditStoreIngredientActivity.class);
        // Why must solo be so slow
        AddEditStoreIngredientActivity a2 = (AddEditStoreIngredientActivity) solo.getCurrentActivity();

        // fill in description
        EditText description = a2.findViewById(R.id.ingredientStoreAdd_description);
        EditText amount = a2.findViewById(R.id.ingredientStoreAdd_amount);
        EditText location = a2.findViewById(R.id.ingredientStoreAdd_location);
        EditText unit = a2.findViewById(R.id.ingredientStoreAdd_unit);
        EditText category = a2.findViewById(R.id.ingredientStoreAdd_category);
        EditText bestBefore = a2.findViewById(R.id.ingredientStoreAdd_bestBefore);

        // BULKING
        solo.enterText(description, "eggs");
        solo.enterText(amount, "155");
        solo.enterText(location, "Cabinet");
        solo.enterText(unit, "kg");
        solo.enterText(category, "bulk");

        solo.clickOnView(bestBefore);
        solo.setDatePicker(0, 2025, 10, 10);
        solo.clickOnText("OK");

        solo.clickOnButton("Confirm");
        solo.waitForActivity(IngredientStorageActivity.class,10000);
        solo.assertCurrentActivity("Wrong activity after clicking cancel: Should be in IngredientStorageActivity", IngredientStorageActivity.class);
    }
}
