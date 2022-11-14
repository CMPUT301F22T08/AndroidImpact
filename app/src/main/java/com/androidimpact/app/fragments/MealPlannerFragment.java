package com.androidimpact.app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidimpact.app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MealPlannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MealPlannerFragment extends Fragment {
    final String TAG = "MealPlannerFragment";


    /**
     * Required empty public constructor
     */
    public MealPlannerFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MealPlanner.
     */
    // TODO: Rename and change types and number of parameters
    public static MealPlannerFragment newInstance() {
        MealPlannerFragment fragment = new MealPlannerFragment();
        return fragment;
    }

    /**
     * Runs on the creation of the fragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflate the layout for this fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_planner, container, false);
    }

    /**
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG + ":onViewCreated", "onViewCreated called!");
    }
}