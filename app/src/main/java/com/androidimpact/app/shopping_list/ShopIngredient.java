package com.androidimpact.app.shopping_list;

import com.androidimpact.app.ingredients.Ingredient;

import java.io.Serializable;

public class ShopIngredient extends Ingredient implements Serializable {

    private float amountPicked = 0;


    /**
     * Empty constructor for firebase
     */
    public ShopIngredient() {}

    //will create two instructors one where amount picked is not specified
    public ShopIngredient(String id, String description, float amount, String unit, String category) {
        super(id, description, amount, unit, category);
        this.amountPicked=0;
    }

    public ShopIngredient(String id, String description, float amount, String unit, String category, float amountPicked) {
        super(id, description, amount, unit, category);
        this.amountPicked=amountPicked;
    }

    public float getAmountPicked() {
        return amountPicked;
    }

    public void setAmountPicked(float amountPicked) {
        this.amountPicked = amountPicked;
    }

}
