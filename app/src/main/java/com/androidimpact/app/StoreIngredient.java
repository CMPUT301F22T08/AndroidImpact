package com.androidimpact.app;

import java.util.Date;

public class StoreIngredient extends Ingredient{
    private Date bestBeforeDate;
    private String location;

    public StoreIngredient(String description, float amount, String unit, String category, Date bestBeforeDate, String location){
        super(description, amount, unit, category);
        this.bestBeforeDate = bestBeforeDate;
        this.location = location;
    }

    public Date getBestBeforeDate() {
        return bestBeforeDate;
    }

    public void setBestBeforeDate(Date bestBeforeDate) {
        this.bestBeforeDate = bestBeforeDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}