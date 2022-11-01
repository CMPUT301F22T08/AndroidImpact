package com.androidimpact.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class RecipeIngredientAdapter extends ArrayAdapter<Ingredient> {
    private ArrayList<Ingredient> ingredients;
    private Context context;

    public RecipeIngredientAdapter(Context context, ArrayList<Ingredient> ingredients) {
        super(context, 0, ingredients);
        this.ingredients = ingredients;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.recipe_ingredient_item, parent, false);
        }

        Ingredient ingredient = ingredients.get(position);

        TextView ingredientDescription = view.findViewById(R.id.ingredient_description);
        TextView ingredientAmount = view.findViewById(R.id.ingredient_amount);
        TextView ingredientUnit = view.findViewById(R.id.ingredient_unit);
        TextView ingredientCategory = view.findViewById(R.id.ingredient_category);

        ingredientDescription.setText(ingredient.getDescription());
        ingredientAmount.setText(String.valueOf(ingredient.getAmount()));
        ingredientUnit.setText(ingredient.getUnit());
        ingredientCategory.setText(ingredient.getCategory());

        return view;
    }
}
