package com.androidimpact.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.androidimpact.app.R;

/**
 * Activity for adding, viewing, and editing a meal plan
 * @version 1.0
 * @author Aneeljyot Alagh and Clare Chen
 */
public class MealPlanAddEditViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan_add_edit_view);
    }

    /**
     * onClick method for cancel
     * @param view
     *    view that calls this method
     */
    public void cancel(View view) {
        finish();
    }

    /**
     * onClick method for confirm
     * @param view
     *    view that calls this method
     */
    public void confirm(View view) {
        finish();
    }
}