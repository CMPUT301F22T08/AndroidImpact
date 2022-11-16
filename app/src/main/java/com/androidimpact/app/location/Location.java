package com.androidimpact.app.location;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Location implements Serializable {
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
}
