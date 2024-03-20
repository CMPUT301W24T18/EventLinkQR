package com.example.eventlinkqr;

import java.util.ArrayList;

/**
 * Singleton class that manages a list of Attendee objects.
 */
public class AttendeeArrayAdapter {
    // Static instance of the adapter for singleton pattern
    private static AttendeeArrayAdapter instance = null;

    // List to hold Attendee objects
    private ArrayList<Attendee> attendees;

    /**
     * Private constructor for singleton pattern.
     */
    private AttendeeArrayAdapter() {
        attendees = new ArrayList<>();
    }

    /**
     * Returns the number of attendees in the adapter.
     * @return int representing the number of attendees
     */
    public int getCount() {
        return attendees.size();
    }

    /**
     * Returns the Attendee at the specified position in the list.
     * @param position The position of the attendee in the list.
     * @return Attendee object at the specified position.
     */
    public Attendee getItem(int position) {
        return attendees.get(position);
    }

    /**
     * Ensures a single instance of AttendeeArrayAdapter is created (Singleton pattern).
     * @return The single instance of AttendeeArrayAdapter.
     */
    public static synchronized AttendeeArrayAdapter getInstance() {
        if (instance == null) {
            instance = new AttendeeArrayAdapter();
        }
        return instance;
    }

    /**
     * Adds an Attendee object to the adapter list.
     * @param attendee The Attendee object to be added.
     */
    public void addAttendee(Attendee attendee) {
        attendees.add(attendee);
    }

    /**
     * Checks if an attendee with the specified UUID exists in the list.
     * @param uuid The UUID to be checked.
     * @return true if an attendee with the UUID exists, false otherwise.
     */
    public boolean containsUUID(String uuid) {
        for (Attendee attendee : attendees) {
            if (attendee.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves an Attendee by their UUID.
     * @param uuid The UUID of the attendee to retrieve.
     * @return Attendee object with the specified UUID, or null if not found.
     */
    public Attendee getAttendeeByUUID(String uuid) {
        for (Attendee attendee : attendees) {
            if (attendee.getUuid().equals(uuid)) {
                return attendee;
            }
        }
        return null;
    }
}