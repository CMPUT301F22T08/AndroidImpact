package com.androidimpact.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.androidimpact.app.R;

/**
 * Activity to add/edit meal plan recipes
 * @version 1.0
 * @author Aneeljyot Alagh, Clare Chen
 */
public class MealPlanAddEditRecipeIngredientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan_add_edit_recipe_ingredient);
    }
}