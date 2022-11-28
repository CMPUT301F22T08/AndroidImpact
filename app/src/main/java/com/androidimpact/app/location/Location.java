package com.androidimpact.app.location;

import androidx.annotation.NonNull;

import com.androidimpact.app.Timestamped;
import com.androidimpact.app.category.Category;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Location
 * Defines a user defined location
 * @version 1.0
 * @author Joshua Ji
 */
public class Location implements Serializable, Timestamped {
    @DocumentId
    private String location;
    @ServerTimestamp
    private Date dateAdded;

    // default empty constructor for Firebase auto deserialization
    public Location() {}

    /**
     * Constructs a Location class from its value, with the current date
     *
     * also generates a UUID
     * @param location
     */
    public Location(String location) {
        this.location = location;
        this.dateAdded = new Date();
    }

    /**
     * Constructs a Location with the value and its id
     *
     * @param location The string value of the location
     * @param date current date time
     */
    public Location(String location, Date date) {
        this.location = location;
        this.dateAdded = date;
    }

    /**
     * turns location to String
     * @return
     */
    @NonNull
    public String toString() {
        return getLocation();
    }

    /**
     * getter for Location
     * @return
     */
    public String getLocation() {
        return location;
    }

    /**
     * setter for Location
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * getter for DateAdded
     * @return
     */
    public Date getDateAdded() {
        return dateAdded;
    }

    /**
     * setter for DateAdded
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
        if (!(o instanceof Location)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Location c = (Location) o;

        // Compare the data members and return accordingly
        return Objects.equals(location, c.getLocation());
    }
}
