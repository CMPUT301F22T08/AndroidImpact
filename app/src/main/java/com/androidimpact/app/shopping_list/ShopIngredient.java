package com.androidimpact.app.shopping_list;

import com.androidimpact.app.ingredients.Ingredient;
import com.androidimpact.app.location.Location;

import java.io.Serializable;
import java.util.Objects;

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

    /**
     * Overriding equals so array searching can work
     * @param o another object to compare
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof ShopIngredient)) {
            return false;
        }

        // typecast o to ShopIngredient so that we can compare data members
        ShopIngredient c = (ShopIngredient) o;

        // Compare the data members and return accordingly
        boolean descriptionEquals = Objects.equals(description, c.getDescription());
        boolean amountEquals = Objects.equals(amount, c.getAmount());
        boolean unitEquals = Objects.equals(unit, c.getUnit());
        return descriptionEquals && amountEquals && unitEquals;
    }
}
