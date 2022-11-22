package com.androidimpact.app;

import android.util.Log;

import com.androidimpact.app.category.Category;
import com.androidimpact.app.unit.Unit;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

/**
 * Ingredient class: Holds the information of an ingredient to be used in recipes and the shopping list
 * - description (String) - A short description of the ingredient e.g. peppercorn ranch
 * - amount (float) - The quantity of the ingredient needed for the recipe/shopping list e.g. 300 in 300g
 * - unit (String) - The unit that amount is measuring e.g. g in 300g
 * - category (String) - Any name that helps categorize the ingredient e.g. Sauce for peppercorn ranch
 * @author Kailash Seshadri
 * @version 1.0
 */
public class Ingredient implements Serializable  {
    protected String description;
    protected float amount;
    // instead of a document, this is a path to the a firebase document
    // https://stackoverflow.com/a/57225579
    private String unitDocumentPath;
    private String categoryDocumentPath;

    /**
     * Empty constructor - used by StoreIngredient for Firebase's auto deserialization
     */
    public Ingredient() {}

    /**
     * Constructor for the Ingredient class
     * @param description (String) - A short description of the ingredient e.g. peppercorn ranch
     * @param amount (float) - The quantity of the ingredient needed for the recipe/shopping list e.g. 300 in 300g
     * @param unitDocumentPath (String) - The unit that amount is measuring e.g. g in 300g
     * @param categoryDocumentPath (String) - Path to category document
     * */
    public Ingredient(String description, float amount, String unitDocumentPath, String categoryDocumentPath) {
        this.description = description;
        this.amount = amount;
        this.unitDocumentPath = unitDocumentPath;
        this.categoryDocumentPath = categoryDocumentPath;
    }

    /**
     * Get the ingredient description
     * @return (String) The description of the ingredient
     */
    public String getDescription() {
        return description;
    }

    /**
     * Rename the ingredient
     * @param description (String) - The new description to be used
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the amount of the ingredient
     * @return (float) The amount of the ingredient
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Set a new amount of the ingredient
     * @param amount (float) - The new amount to be used
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Exclude
    public void setAmount(double amount) {
        this.amount = (float) amount;
    }

    @Exclude
    public void setAmount(int amount) {
        this.amount = (float) amount;
    }

    /**
     * Get the ingredient category
     * @return (String) The category of the ingredient
     */
    public String getCategory() {
        return categoryDocumentPath;
    }

    /**
     * Give the ingredient a new category
     * @param category (String) - The new category of the ingredient
     */
    public void setCategory(String category) {
        this.categoryDocumentPath = category;
    }

    /**
     * gets the unit document path
     * not used by the user, but used by Firebase to know they need to serialize the unitDocumentPath
     * @return
     */
    public String getUnitDocumentPath() { return unitDocumentPath; };

    /**
     * A fully-featured function to retrieve the unit from firestore
     *
     * this architecture lets us reduce the callback hell somewhat using listeners, i think...
     */
    @Exclude
    public void getUnitAsync(DocumentRetrievalListener<Unit> listener) {
        FirebaseFirestore.getInstance().document(unitDocumentPath).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Unit u = document.toObject(Unit.class);
                    listener.onSuccess(u);
                } else {
                    listener.onNullDocument();
                }
            } else {
                listener.onError(task.getException());
            }
        });
    }

    /**
     * A fully-featured function to retrieve the unit from firestore
     *
     * this architecture lets us reduce the callback hell somewhat using listeners, i think...
     */
    @Exclude
    public void getCategoryAsync(DocumentRetrievalListener<Category> listener) {
        FirebaseFirestore.getInstance().document(categoryDocumentPath).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Category c = document.toObject(Category.class);
                    listener.onSuccess(c);
                } else {
                    listener.onNullDocument();
                }
            } else {
                listener.onError(task.getException());
            }
        });
    }

}