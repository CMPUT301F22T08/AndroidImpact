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
 * Add a toggle listener to adapter class, which opens this dialog box.
 * 
 * @author Vedant Vyas
 */
public class ShopPickUpFragment extends DialogFragment {

    //Class attributes
    private ShopIngredient ingredient;

    private float amountPickUp;
    private String unit;
    private EditText editAmountPickUp;

    /**
     * Required empty public constructor
     */
    public ShopPickUpFragment() {
    }

    /**
     * Creates a new ShopPickUpFragment object and passes in the arguments using bundle
     * @param ingredient ShopIngredient in the list whose switch was clicked
     * @return ShopPickUpFragment
     */
    public static ShopPickUpFragment newInstance(ShopIngredient ingredient) {
        Bundle args = new Bundle();
        //putting all the arguments to bundle so that we can retrieve them later
      //  args.putInt("itemPos", pos);
        args.putFloat("CurrentAmount", ingredient.getAmountPicked());
        args.putSerializable("ingredient", ingredient);

        ShopPickUpFragment fragment = new ShopPickUpFragment();
        fragment.setArguments(args);
        return fragment;

    }

    /**
     * OnCreate is called after onAttach
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    /**
     * @param savedInstanceState
     * @return DialogBuilder object with appropriate title and EditText that lets user decidee the amount they picked up
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_shop_pick_up, null);

        editAmountPickUp = view.findViewById(R.id.editAmountPickedUp);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //initializing dialog box with existing object values

        //We are indeed using arguments passed in to dialog fragment
        if (getArguments() != null) {
           // pos = getArguments().getInt("itemPos");
            ingredient = (ShopIngredient) getArguments().getSerializable("ingredient");
            editAmountPickUp.setText(String.valueOf(ingredient.getAmount()));
        }
        else{
            //This case is not possible as we never call the PickUpFragment class with out arguments, but here just for the sake of good practices
            return builder
                    .setView(view)
                    .setTitle("Wrong Usage of PickUp Dialog Box")
                    .create();
        }

            return builder
                    .setView(view)
                    .setTitle("Add quantity of picked up ingredient")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        /**
                         * This creates an onClick class
                         * @param dialog
                         * @param which
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //might be an issue if user is cancelling to cancel the toggle back
                            ingredient.setAmountPicked(0);
                            MainActivity main = (MainActivity)getActivity();
                            main.cancelUpdateShopIngredient(ingredient);

                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        /**
                         * This is an onClick class implements error handling
                         * @param dialog
                         * @param which
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String cost = editAmountPickUp.getText().toString();
                            Log.i("CostOfItem", cost);

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
                            MainActivity main = (MainActivity)getActivity();
                            main.updateShopIngredient(ingredient);

                            //MainActivity.getmInstanceActivity().updateShopIngredient(ingredient);
                        }
                    })
                    .create();
    }
}