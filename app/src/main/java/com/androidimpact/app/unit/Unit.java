package com.androidimpact.app.unit;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Unit implements Serializable {
    @DocumentId
    String unit;
    @ServerTimestamp
    Date dateAdded;

    // default empty constructor for Firebase auto deserialization
    public Unit() {}

    /**
     * toString allows the ArrayAdapter to have sensible defaults
     */
    public String toString() {
        return getUnit();
    }

    /**
     * Constructs a Unit class from its value
     */
    public Unit(String unit) {
        this.unit = unit;
        this.dateAdded = new Date();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
}
