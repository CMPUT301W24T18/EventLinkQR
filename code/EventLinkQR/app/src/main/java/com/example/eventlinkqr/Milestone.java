package com.example.eventlinkqr;


import com.google.firebase.Timestamp;
import java.util.Date;
/**
 * Milestone class to keep track of the milestones that the user has reached
 * and to provide a way to add a milestone to the database.
 *
 */
public class Milestone {
    private String eventId;
    private String organizerId;
    private String info;
    private Integer value;
    private Timestamp timestamp;

    /**
     * Default constructor required for Firebase's DataSnapshot.getValue method when fetching Milestone data.
     */
    public Milestone() {
        // Default constructor required for calls to DataSnapshot.getValue(Milestone.class)
    }

    /**
     * Constructs a Milestone with specified details.
     *
     * @param eventId The ID of the event associated with this milestone.
     * @param organizerId The ID of the organizer who created the milestone.
     * @param info Information or description of the milestone.
     * @param value Numerical value or quantifier of the milestone.
     * @param timestamp Timestamp indicating the creation or relevant time of the milestone.
     */
    public Milestone(String eventId, String organizerId, String info, Integer value, Timestamp timestamp) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.info = info;
        this.value = value;
        this.timestamp = timestamp;
    }

    // Getter and setter methods below...
    public String getInfo() {
        return info;
    }

    public Integer getValue() {
        return value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getOrganizerId() {
        return organizerId;
    }

}
