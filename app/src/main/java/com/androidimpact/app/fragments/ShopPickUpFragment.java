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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidimpact.app.R;
import com.androidimpact.app.activities.MainActivity;
import com.androidimpact.app.shopping_list.ShopIngredient;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 * Add a toggle listener to adapter class, which opens this dialog box. I guess that's good enough for now
 * 
 * @author Vedant Vyas
 */
public class ShopPickUpFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int pos =-1;
    private ShopIngredient ingredient;

    private float amountPickUp;
    private String unit;
    private EditText editAmountPickUp;


    public ShopPickUpFragment() {
        // Required empty public constructor
    }

    public static ShopPickUpFragment newInstance(ShopIngredient ingredient, int pos) {
        Bundle args = new Bundle();
        //putting all the arguments to bundle so that we can retrieve them later
        Log.i("tt1", String.valueOf(pos));
        args.putInt("itemPos", pos);
        args.putFloat("CurrentAmount", ingredient.getAmountPicked());
        args.putSerializable("ingredient", ingredient);

        ShopPickUpFragment fragment = new ShopPickUpFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_shop_pick_up, null);

        editAmountPickUp = view.findViewById(R.id.editAmountPickedUp);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            //initializing dialog box with existing object values
        if (getArguments() != null) {
            pos = getArguments().getInt("itemPos");
            ingredient = (ShopIngredient) getArguments().getSerializable("ingredient");
            editAmountPickUp.setText(String.valueOf(ingredient.getAmount()));
        }
        else
        {
            pos = -1;

        }



            return builder
                    .setView(view)
                    .setTitle("Add quantity of picked up ingredient")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //might be an issue if user is cancelling to cancel the toggle back
                            ingredient.setAmountPicked(0);
                            MainActivity.getmInstanceActivity().cancelUpdateShopIngredient(ingredient);
                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String cost = editAmountPickUp.getText().toString();

                            //To be Removed
                            Log.i("Empty", cost);

                            float costF;
                            try
                            {
                                costF = Float.parseFloat(cost);
                                if (costF > Float.MAX_VALUE)
                                    throw new IllegalArgumentException("Large Number");
                            }
                            catch(Exception e)
                            {
                                costF = ingredient.getAmountPicked();
                                //SnackBar pop which is specific to large numbers
                                String errorMessage = e.getMessage();
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();


                            }
                            Log.i("Amount Picked up", String.valueOf(costF));

                            ingredient.setAmountPicked(costF);

                            MainActivity.getmInstanceActivity().updateShopIngredient(ingredient);
                        }
                    })
                    .create();
    }
}