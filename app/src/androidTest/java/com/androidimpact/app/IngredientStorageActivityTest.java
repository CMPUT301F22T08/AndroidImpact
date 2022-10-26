package com.androidimpact.app;

import android.content.res.Resources;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.androidimpact.app.activities.AddStoreIngredientActivity;
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

    // TODO: Change to IngredientStorageActivity
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

    /**
     * Checks that clicking the "add ingredient" fab directs us to the addStoreIngredientActivity
     * and that clicking "cancel" directs us to back to the original activity
     */
    @Test
    public void addIngredientAndCancel() {
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);
        MainActivity a = (MainActivity) solo.getCurrentActivity();

        FloatingActionButton fab = a.findViewById(R.id.addStoreIngredientFAB);
        solo.clickOnView(fab);

        solo.assertCurrentActivity("Wrong activity after clicking FAB", AddStoreIngredientActivity.class);
        solo.clickOnButton("Cancel");
        solo.assertCurrentActivity("Wrong activity after clicking cancel", MainActivity.class);
    }


    /**
     * Checks that adding a valid ingredient redirects us back to the activity
     */
    @Test
    public void addIngredientSuccess() {
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);
        MainActivity a1 = (MainActivity) solo.getCurrentActivity();

        FloatingActionButton fab = a1.findViewById(R.id.addStoreIngredientFAB);
        solo.clickOnView(fab);

        solo.assertCurrentActivity("Wrong activity after clicking FAB", AddStoreIngredientActivity.class);
        AddStoreIngredientActivity a2 = (AddStoreIngredientActivity) solo.getCurrentActivity();

        // fill in description
        EditText description = a2.findViewById(R.id.ingredientStoreAdd_description);
        EditText amount = a2.findViewById(R.id.ingredientStoreAdd_amount);
        EditText location = a2.findViewById(R.id.ingredientStoreAdd_location);
        EditText unit = a2.findViewById(R.id.ingredientStoreAdd_unit);
        EditText category = a2.findViewById(R.id.ingredientStoreAdd_category);
        DatePicker bestBefore = a2.findViewById(R.id.ingredientStoreAdd_bestBefore);

        // BULKING
        solo.enterText(description, "eggs");
        solo.enterText(amount, "155");
        solo.enterText(location, "Cabinet");
        solo.enterText(unit, "kg");
        solo.enterText(category, "bulk");
        solo.setDatePicker(bestBefore, 2025, 10, 10);

        solo.clickOnButton("Confirm");
        solo.assertCurrentActivity("Wrong activity after clicking cancel", MainActivity.class);
    }
}
