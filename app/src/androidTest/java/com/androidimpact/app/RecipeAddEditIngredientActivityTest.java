package com.androidimpact.app;

import android.app.Activity;
import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.androidimpact.app.activities.RecipeAddEditIngredientActivity;
import com.robotium.solo.Solo;

@RunWith(AndroidJUnit4.class)
public class RecipeAddEditIngredientActivityTest {

    private Solo solo;
    @Rule
    public ActivityTestRule<RecipeAddEditIngredientActivity> rule =
            new ActivityTestRule<>(RecipeAddEditIngredientActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }


}
