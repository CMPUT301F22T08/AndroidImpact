package com.androidimpact.app.unit;

import com.androidimpact.app.Timestamped;
import com.androidimpact.app.location.Location;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


/**
 * Unit
 * Defines a user defined unit
 * @version 1.0
 * @author Joshua Ji
 */
public class Unit implements Serializable, Timestamped {
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

    /**
     * getter function for Unit
     * @return unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * setter function for unit
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * getter function for DateAdded
     * @return dateAdded
     */
    public Date getDateAdded() {
        return dateAdded;
    }

    /**
     * setter function for DateAdded
     * @param dateAdded
     */
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
        if (!(o instanceof Unit)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Unit c = (Unit) o;

        // Compare the data members and return accordingly
        return Objects.equals(unit, c.getUnit());
    }
}
