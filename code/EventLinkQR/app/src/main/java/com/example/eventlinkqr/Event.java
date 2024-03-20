package com.example.eventlinkqr;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import com.google.firebase.Timestamp;


/**
 * This class contains all necessary data for an event instance
 */
public class Event {
    /** All the attributes of an event*/
    private String name, description, category, location;
    private Timestamp date;
    private String id;
    private Boolean geoTracking;
    private int checkedInAttendeesCount;
    private ArrayList<Attendee> checkedInAttendees;
    private ArrayList<LatLng> checkInLocations;
    /**
     * Event creator with all attributes
     * @param name the event's name
     * @param description the event's description
     * @param category the event's category
     * @param date the event's date
     * @param location the event's location
     * @param geoTracking whether the event has geolocation tracking or not
     */
    public Event(String name, String description, String category, Timestamp date, String location, Boolean geoTracking) {
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

    // No-argument constructor required for Firebase deserialization
    public Event() {
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
    public Timestamp getDate() {
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

    /**
     * check in an attendee to the event and add their location if geotracking is enabled
     * @param attendee
     * @param checkInLocation
     * @return
     */
    public Boolean checkIn(Attendee attendee, LatLng checkInLocation){
        //Check if the attendee is already checked in
        if(checkedInAttendees.contains(attendee)){
            return false;
        } else {
            checkedInAttendees.add(attendee);
            if (geoTracking) {
                checkInLocations.add(checkInLocation);
            }
            return true;
        }
    }

    /**
     * check in an attendee to the event without a location
     * @param attendee
     * @return
     */
    public Boolean checkIn(Attendee attendee){
        //Check if the attendee is already checked in
        if(checkedInAttendees.contains(attendee)){
            return false;
        } else {
            checkedInAttendees.add(attendee);
            return true;
        }
    }

    /**
     * get the list of checked in attendees
     * @return checkedInAttendees
     */
    public ArrayList<Attendee> getCheckedInAttendees() {
        return checkedInAttendees;
    }

    public int getCheckedInAttendeesCount() {
        return checkedInAttendeesCount;
    }

    public void setCheckedInAttendeesCount(Integer count) {
        if(count == null){
            this.checkedInAttendeesCount = 0;
        } else {
            this.checkedInAttendeesCount = count;
        }

    }

    /**
     * get the list of check in locations
     * @return checkInLocations
     */
    public ArrayList<LatLng> getCheckInLocations() {
        return this.checkInLocations;
    }

    public void setCheckInLocations(ArrayList<LatLng> checkInLocations) {
        this.checkInLocations = checkInLocations;
    }

    public int getTotalAttendees() {
        if (checkedInAttendees == null || checkedInAttendees.isEmpty()) {
            return 0;
        } else {
            return checkedInAttendees.size();
        }
    }
}
