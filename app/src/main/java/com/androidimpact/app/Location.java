package com.androidimpact.app;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Location implements Serializable {
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
     * @param value
     */
    public Location(String value) {
        UUID uuid = UUID.randomUUID();
        id = uuid.toString();
        value = value;
        dateAdded = new Date();
    }

    /**
     * Constructs a Location with the value and its id
     *
     * also generates a UUID
     * @param value
     */
    public Location(String id, String value, Date date) {
        id = id;
        value = value;
        dateAdded = date;
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
}
