package com.androidimpact.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MealPlanAddEditViewActivity;

public class ServingsAddFragment extends DialogFragment {

    final String TAG = "ServingsAddFragment";

    // Declare the variables so that you will be able to reference it later.
    private String mealType, hash, title;
    private boolean isRecipe;
    Button cancelBtn, confirmBtn;

    public ServingsAddFragment(String meal, String hash, String title, boolean isRecipe) {
        super(R.layout.fragment_servings_add_mealplan);
        this.mealType = meal;
        this.hash = hash;
        this.title = title;
        this.isRecipe = isRecipe;
    }

    /**
     *
     Fragment Called to do initial creation of a fragment. This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *
     * Fragment Called to have the fragment instantiate its user interface view.
     * This will be called between onCreate(Bundle) and onViewCreated(View, Bundle).
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_servings_add_mealplan, container, false);
    }


    /**
     *
     * Fragment Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);
        Log.i(TAG + ":onViewCreated", "onViewCreated called!");

        cancelBtn = view.findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(view1 -> dismiss());

        confirmBtn = view.findViewById(R.id.confirm_button);
        confirmBtn.setOnClickListener(view1 -> {
            MealPlanAddEditViewActivity mealPlanAddEditViewActivity = (MealPlanAddEditViewActivity) getActivity();
            EditText servingsInput = view.findViewById(R.id.editTextServings);
            String input = servingsInput.getText().toString();
            try {
                double floatingPoint = Double.parseDouble(input);
                if(!(floatingPoint>0)) {
                    throw new Exception("invalid entry");
                }
                if(this.isRecipe) {
                    mealPlanAddEditViewActivity.addRecipe(this.mealType, this.hash, this.title, floatingPoint);
                } else {
                    mealPlanAddEditViewActivity.addIngredient(this.mealType, this.hash, this.title, floatingPoint);
                }
                dismiss();
            }
            catch (Exception e) {
                TextView errorText = view.findViewById(R.id.errorMsg);
                errorText.setVisibility(View.VISIBLE);
                Log.e("bad", "badbad", e);
            }

        });
    }
}
