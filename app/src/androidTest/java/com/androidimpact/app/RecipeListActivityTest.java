package com.androidimpact.app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.res.Resources;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.androidimpact.app.activities.RecipeListActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RecipeListActivityTest {
    private Solo solo;
    private Resources resources;

    @Rule
    public ActivityTestRule<RecipeListActivity> rule =
            new ActivityTestRule<>(RecipeListActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        resources = InstrumentationRegistry.getInstrumentation().getContext().getResources();
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * spinner button works
     */
    @Test
    public void btnChecks() {
        solo.assertCurrentActivity("Wrong Activity!", RecipeListActivity.class);
        Spinner spinner = (Spinner) solo.getView(R.id.sort_recipe_spinner);
        assertEquals("button should have drop down", "Date Added", spinner.getSelectedItem().toString());

    }

    @Test
    public void deleteChecks() {
        // TODO: fix

        //ok this is wrong
//        RecyclerView recipeListView = (RecyclerView) solo.getView(R.id.recipe_listview);
//        int initialSize = recipeListView.getAdapter().getItemCount();
//
//        onView(ViewMatchers.withText((String) ((TextView) recipeListView.getChildAt(0).findViewById(R.id.recipe_name)).getText())).perform(ViewActions.swipeRight());
//
//        solo.sleep(2000);
//        //aye yo this is wack
//        assertEquals(recipeListView.getAdapter().getItemCount(), initialSize - 1);
    }

}