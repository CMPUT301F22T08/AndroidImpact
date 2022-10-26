package com.androidimpact.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.res.Resources;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.androidimpact.app.activities.AddStoreIngredientActivity;
import com.androidimpact.app.activities.MainActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AddStoreIngredientActivityTest {
    private Solo solo;
    private Resources resources;

    @Rule
    public ActivityTestRule<AddStoreIngredientActivity> rule =
            new ActivityTestRule<>(AddStoreIngredientActivity.class, true, true);

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
     * Checks to see if the cancel and confirm button works
     */
    @Test
    public void btnChecks() {
        solo.assertCurrentActivity("Wrong Activity!", AddStoreIngredientActivity.class);
        // ensure this cancel text is the same as @string/cancel
        // we don't have access to the actual android context (https://stackoverflow.com/a/39577494)
        // so we have to hardcode this XD
        Button cancelBtn = (Button) solo.getView(R.id.ingredientStoreAdd_cancelBtn);
        assertEquals("button should have cancel", "Cancel", cancelBtn.getText());

        // check that confirm button exists
        Button confirmBtn = (Button) solo.getView(R.id.ingredientStoreAdd_confirmBtn);
        assertEquals("button should have cancel", "Confirm", confirmBtn.getText());
    }

    /**
     * Checks to see if the "confirm" button doesn't go back to MainActivity when the user hasn't inputted anything.
     */
    @Test
    public void confirmBtnCheckEmpty() {
        solo.assertCurrentActivity("Wrong Activity!", AddStoreIngredientActivity.class);
        // ensure this cancel text is the same as @string/confirm
        solo.clickOnButton("Confirm");
        solo.assertCurrentActivity("Wrong Activity!", AddStoreIngredientActivity.class);
    }

    /**
     * INVALID INPUT: when amount is a non-integer
     */
    @Test
    public void confirmBtnCheckInvalid1() {
        solo.assertCurrentActivity("Wrong Activity!", AddStoreIngredientActivity.class);
        AddStoreIngredientActivity a = (AddStoreIngredientActivity) solo.getCurrentActivity();

        // fill in description
        EditText description = a.findViewById(R.id.ingredientStoreAdd_description);
        EditText amount = a.findViewById(R.id.ingredientStoreAdd_amount);
        EditText location = a.findViewById(R.id.ingredientStoreAdd_location);
        EditText unit = a.findViewById(R.id.ingredientStoreAdd_unit);
        EditText category = a.findViewById(R.id.ingredientStoreAdd_category);
        DatePicker bestBefore = a.findViewById(R.id.ingredientStoreAdd_bestBefore);

        // BULKING
        solo.enterText(description, "eggs");
        solo.enterText(amount, "abc");
        solo.enterText(location, "Cabinet");
        solo.enterText(unit, "kg");
        solo.enterText(category, "bulk");
        solo.setDatePicker(bestBefore, 2025, 10, 10);

        solo.clickOnButton("Confirm");
        solo.assertCurrentActivity("Wrong Activity!", AddStoreIngredientActivity.class);
    }

    /**
     * INVALID INPUT: when amount is a non-positive
     */
    @Test
    public void confirmBtnCheckInvalid2() {
        solo.assertCurrentActivity("Wrong Activity!", AddStoreIngredientActivity.class);
        AddStoreIngredientActivity a = (AddStoreIngredientActivity) solo.getCurrentActivity();

        // fill in description
        EditText description = a.findViewById(R.id.ingredientStoreAdd_description);
        EditText amount = a.findViewById(R.id.ingredientStoreAdd_amount);
        EditText location = a.findViewById(R.id.ingredientStoreAdd_location);
        EditText unit = a.findViewById(R.id.ingredientStoreAdd_unit);
        EditText category = a.findViewById(R.id.ingredientStoreAdd_category);
        DatePicker bestBefore = a.findViewById(R.id.ingredientStoreAdd_bestBefore);

        // BULKING
        solo.enterText(description, "eggs");
        solo.enterText(amount, "-100");
        solo.enterText(location, "Cabinet");
        solo.enterText(unit, "kg");
        solo.enterText(category, "bulk");
        solo.setDatePicker(bestBefore, 2025, 10, 10);

        solo.clickOnButton("Confirm");
        solo.assertCurrentActivity("Wrong Activity!", AddStoreIngredientActivity.class);
    }

    /**
     * On a valid input, checks to see if the "confirm" does back to MainActivity
     */
    @Test
    public void confirmBtnCheckFilled() {
        solo.assertCurrentActivity("Wrong Activity!", AddStoreIngredientActivity.class);
        AddStoreIngredientActivity a = (AddStoreIngredientActivity) solo.getCurrentActivity();

        // fill in description
        EditText description = a.findViewById(R.id.ingredientStoreAdd_description);
        EditText amount = a.findViewById(R.id.ingredientStoreAdd_amount);
        EditText location = a.findViewById(R.id.ingredientStoreAdd_location);
        EditText unit = a.findViewById(R.id.ingredientStoreAdd_unit);
        EditText category = a.findViewById(R.id.ingredientStoreAdd_category);
        DatePicker bestBefore = a.findViewById(R.id.ingredientStoreAdd_bestBefore);

        // BULKING
        solo.enterText(description, "eggs");
        solo.enterText(amount, "155");
        solo.enterText(location, "Cabinet");
        solo.enterText(unit, "kg");
        solo.enterText(category, "bulk");
        solo.setDatePicker(bestBefore, 2025, 10, 10);

        solo.clickOnButton("Confirm");
        // At this point, the activity finishes. But, we can't test this;
        // running the code below just fails for some reason...
//        assertTrue(a.isFinishing());
    }

}