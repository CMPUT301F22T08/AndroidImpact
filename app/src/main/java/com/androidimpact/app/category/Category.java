package com.androidimpact.app.category;

import com.androidimpact.app.Timestamped;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Category implements Serializable, Timestamped {
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
     * Constructs a Category class from its value
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
        if (!(o instanceof Category)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Category c = (Category) o;

        // Compare the data members and return accordingly
        return Objects.equals(category, c.getCategory());
    }
}
