package com.androidimpact.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.androidimpact.app.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */


//Add a toggle listener to adapter class, which opens this dialog box. I guess that's good enough for now


public class shopPickUpFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private float amountPickUp;
    private String unit;
    private EditText editAmountPickUp;


    public shopPickUpFragment() {
        // Required empty public constructor
    }


//
//    public interface OnFragmentInteractionListener{
//
//        public void onOkPressed(Food food, int pos, int cost);
//    }
//
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //To be done
//        if (context instanceof OnFragmentInteractionListener)
//        {
//            listener = (OnFragmentInteractionListener) context;
//        }else
//        {
//            throw new RuntimeException(context.toString() + "must implement the interface methods");
//        }
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment shopPickUpFragment.
     */
//    // TODO: Rename and change types and number of parameters
//    public static shopPickUpFragment newInstance(String param1, String param2) {
//        shopPickUpFragment fragment = new shopPickUpFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_shop_pick_up, container, false);
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_shop_pick_up, null);


        editAmountPickUp = view.findViewById(R.id.editAmountPickedUp);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());



//        foodCostText = view.findViewById(R.id.editFoodCost);
//        foodDescriptionText = view.findViewById(R.id.editFoodDescription);
//
//        //https://developer.android.com/develop/ui/views/components/spinner
//        Spinner spinner = (Spinner) view.findViewById(R.id.editFoodLocation);
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
//                R.array.location_menu, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);
//        foodCountText = view.findViewById(R.id.editFoodCount);
//        datepicker = view.findViewById(R.id.simpleDatePicker);
//        // creating a dialog builder
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//
//        return builder
//                .setView(view)
//                .setTitle("Add Food Item")
//                .setNegativeButton("Cancel", null)
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String name = "";
//                        String cost = foodCostText.getText().toString();
//                        String description = foodDescriptionText.getText().toString();
//                        String location = spinner.getSelectedItem().toString();
//                        String count = foodCountText.getText().toString();
//                        String date;
//
//                        int day = datepicker.getDayOfMonth();
//                        int month = datepicker.getMonth();
//
//                        //making sure to store two digits for day and month
//                        if (day < 10)
//                            day1 = "0" + String.valueOf(day);
//                        else
//                            day1 = String.valueOf(day);
//
//                        if (month < 10)
//                            month1 = "0" + String.valueOf(month+1);
//                        else
//                            month1 = String.valueOf(month+1);
//
//                        date = "" + datepicker.getYear()+ "-" + month1 + "-" +day1;
//
//
//                        //making a new food object
//                        Food newFood = new Food(description, Integer.parseInt(count), (int)Math.ceil(Float.parseFloat(cost)),date,location);
//
//                        listener.onOkPressed(newFood, -1, 0);
//                    }
//                })
//
//                .create();
        return builder
                .setView(view)
                .setTitle("Add quantity of purchased things")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Cancel", null)
                .create();



    }
    }