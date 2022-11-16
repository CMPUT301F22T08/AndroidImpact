package com.androidimpact.app;

import java.util.UUID;

public class Location {
    String id;
    String location;

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
    }

    /**
     * Constructs a Location with the value and its id
     *
     * also generates a UUID
     * @param value
     */
    public Location(String id, String value) {
        id = id;
        value = value;
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
}
