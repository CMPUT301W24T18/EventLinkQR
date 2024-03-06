package com.example.eventlinkqr;

/**
 * This class contains all necessary data for an event instance
 */
public class Event {
    /** All the attributes of an event*/
    private QRCode qr;
    private String name, description, category, date, location;
    private String id;
    private Boolean geoTracking;

    /**
     * Event creator with all attributes
     * @param qr the event's QR code
     * @param name the event's name
     * @param description the event's description
     * @param category the event's category
     * @param date the event's date
     * @param location the event's location
     * @param geoTracking whether the event has geolocation tracking or not
     */
    public Event(QRCode qr, String name, String description, String category, String date, String location, Boolean geoTracking) {
        this.qr = qr;
        this.name = name;
        this.description = description;
        this.category = category;
        this.date = date;
        this.location = location;
        this.geoTracking = geoTracking;
    }

    /**
     * event constructor used for testing, only adding its name and description
     * @param name name of the event
     * @param description description of the event
     */
    public Event(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * gets the event's QR code
     * @return the event's QR code
     */
    public QRCode getQr() {
        return qr;
    }

    /**
     * gets the event's name
     * @return the event's name
     */
    public String getName() {
        return name;
    }

    /**
     * gets the event's description
     * @return the event's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * gets the event's category
     * @return the event's category
     */
    public String getCategory() {
        return category;
    }

    /**
     * gets the event's date
     * @return the event's date
     */
    public String getDate() {
        return date;
    }

    /**
     * gets the event's location
     * @return the event's location
     */
    public String getLocation() {
        return location;
    }

    /**
     * gets the event's geotracking status
     * @return the event's geotracking status
     */
    public Boolean getGeoTracking() {
        return geoTracking;
    }

    /**
     * get the event's id
     * @return event's id
     */
    public String getId() {
        return id;
    }

    /**
     * set the event's id
     * @param id event id
     */
    public void setId(String id) {
        this.id = id;
    }
}
