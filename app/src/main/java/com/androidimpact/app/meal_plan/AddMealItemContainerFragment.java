package com.androidimpact.app.meal_plan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.androidimpact.app.R;

public class AddMealItemContainerFragment extends DialogFragment {
    String meal;
    boolean isRecipe;
    Button cancelBtn;


    public AddMealItemContainerFragment(String meal, boolean isRecipe) {
        this.meal = meal;
        this.isRecipe = isRecipe;
    }

    /**
     *
     Fragment Called to have the fragment instantiate its user interface view.
     This is optional, and non-graphical fragments can return null. This will be called between onCreate(Bundle) and onViewCreated(View, Bundle).
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_meal_item_container_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        RecipeAddFragment recipeAddFragment = new RecipeAddFragment(this.meal);
        transaction.replace(R.id.frameLayoutContainer, recipeAddFragment);
        //transaction.show(recipeAddFragment);
        //transaction.add(R.id.frameLayoutContainer, recipeAddFragment, "tag tag");
        transaction.commit();

        //cancelBtn = view.findViewById(R.id.meal_planner_add_item_cancel);
        //cancelBtn.setOnClickListener(view1 -> dismiss());
    }
}
