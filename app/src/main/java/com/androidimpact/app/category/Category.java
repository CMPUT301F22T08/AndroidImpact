package com.androidimpact.app.category;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Category {
    @DocumentId
    String category;
    @ServerTimestamp
    Date dateAdded;

    // default empty constructor for Firebase auto deserialization
    public Category() {}

    /**
     * toString allows the ArrayAdapter to have sensible defaults
     */
    public String toString() {
        return getCategory();
    }

    /**
     * Constructs a Unit class from its value
     */
    public Category(String category) {
        this.category = category;
        this.dateAdded = new Date();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
}
