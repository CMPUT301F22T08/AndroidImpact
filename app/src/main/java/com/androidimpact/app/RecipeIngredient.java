package com.androidimpact.app;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

/**
 * RecipeIngredient object that is stored as a part of a recipe
 * @author Joshua Ji
 * @version 1.0
 * @see Ingredient
 */
public class RecipeIngredient extends Ingredient implements Serializable {
    @ServerTimestamp
    private Date dateAdded;

    /**
     * Empty constructor or Firebase to use when automatically deserializing
     */
    public RecipeIngredient() {}

    /**
     * This constructor is what we use to actually make a RecipeIngredient
     * @return
     */
    public RecipeIngredient(String id, String description, float amount, String unitPath, String category, Date dateAdded) {
        super(id, description, amount, unitPath, category);
        this.dateAdded = dateAdded;
    }

    public Date getDateAdded() {
        return dateAdded;
    }
}
