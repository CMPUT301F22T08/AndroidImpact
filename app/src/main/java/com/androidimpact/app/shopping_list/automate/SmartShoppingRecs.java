package com.androidimpact.app.shopping_list.automate;

import android.util.Log;

import com.androidimpact.app.shopping_list.ShopIngredient;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Shopping list but it merges duplicates
 */
public class SmartShoppingRecs {
    private static final String TAG = "SmartShoppingRecs";
    private final ArrayList<ShopIngredient> data;

    public SmartShoppingRecs() {
        data = new ArrayList<>();
    }

    /**
     * Add ingredient, while merging amount if equal
     * @param newIngr
     */
    public void addMerge(ShopIngredient newIngr) {
        if (newIngr.getAmount() == 0) {
            Log.i(TAG, "Skipping merge for " + newIngr.getDescription() + ":" + newIngr.getAmount() + " " + newIngr.getUnit() + " - zero amount!");
            return;
        }
        boolean added = false;
        for (ShopIngredient currIngr : data) {
            boolean nameEqual = Objects.equals(currIngr.getDescription(), newIngr.getDescription());
            boolean unitEqual = Objects.equals(currIngr.getUnit(), newIngr.getUnit());
            boolean categoryEqual = Objects.equals(currIngr.getCategory(), newIngr.getCategory());

            if (nameEqual && unitEqual && categoryEqual) {
                Log.i(TAG, "Merge " + currIngr.getDescription() + ":" + currIngr.getAmount() + " " + newIngr.getUnit() + " with " + newIngr.getDescription() + ":" + newIngr.getAmount() + " " + newIngr.getUnit());
                added = true;
                // get difference
                currIngr.setAmount(currIngr.getAmount() + newIngr.getAmount());
            }
        }
        if (!added) {
            Log.i(TAG, "Add " + newIngr.getDescription() + ":" + newIngr.getAmount() + " " + newIngr.getUnit());
            // add
            data.add(newIngr);
        }
    }

    public ArrayList<ShopIngredient> getData() {
        return data;
    }
}
