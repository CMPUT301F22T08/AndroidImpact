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
    String id;
    String location;
    @ServerTimestamp
    Date dateAdded;

    // default empty constructor for Firebase auto deserialization
    public Location() {}

    /**
     * Constructs a Location class from its value
     *
     * also generates a UUID
     * @param location_
     */
    public Location(String location_) {
        UUID uuid = UUID.randomUUID();
        id = uuid.toString();
        location = location_;
        dateAdded = new Date();
    }

    /**
     * Constructs a Location with the value and its id
     *
     * @param id_
     * @param location_
     * @param date
     */
    public Location(String id_, String location_, Date date) {
        id = id_;
        location = location_;
        dateAdded = date;
    }

    @NonNull
    public String toString() {
        return getLocation();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
        if (!(o instanceof Location)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Location c = (Location) o;

        // Compare the data members and return accordingly
        return Objects.equals(id, c.getId());
    }
}
