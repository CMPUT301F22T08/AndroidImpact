package com.androidimpact.app;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Ingredient implements Serializable {

    protected String description;
    protected float amount;
    protected String unit;
    protected String category;

    public Ingredient(String description){
        this.description = description;
        this.amount = 0;
        this.unit = "kg";
        this.category = "test";
    }


    public Ingredient(String description, float amount, String unit, String category) {
        this.description = description;
        this.amount = amount;
        this.unit = unit;
        this.category = category;
    }

    public Ingredient(String description, double amount, String unit, String category) {
        this.description = description;
        this.amount = (float) amount;
        this.unit = unit;
        this.category = category;
    }

    public Ingredient(String description, int amount, String unit, String category) {
        this.description = description;
        this.amount = (float) amount;
        this.unit = unit;
        this.category = category;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getAmount() {
        return amount;
    }

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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
