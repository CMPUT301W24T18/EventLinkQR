package com.example.eventlinkqr;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AttendeeArrayAdapter {
    private static AttendeeArrayAdapter instance = null;
    private ArrayList<Attendee> attendees;

    private AttendeeArrayAdapter() {
        attendees = new ArrayList<>();
    }

    public int getCount() {
        return attendees.size();
    }

    public Attendee getItem(int position) {
        return attendees.get(position);
    }

    public static synchronized AttendeeArrayAdapter getInstance() {
        if (instance == null) {
            instance = new AttendeeArrayAdapter();
        }
        return instance;
    }

    public void addAttendee(Attendee attendee) {
        attendees.add(attendee);
        Log.d("AttendeeArrayAdapter", "Added attendee: " + attendee.getName());
        // Log the entire list
        for (Attendee a : attendees) {
            Log.d("AttendeeArrayAdapter", "Attendee List: " + a.getName());
        }
    }


    public boolean containsUUID(String uuid) {
        for (int i = 0; i < getCount(); i++) {
            Attendee attendee = getItem(i);
            if (attendee.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public Attendee getAttendeeByUUID(String uuid) {
        for (int i = 0; i < getCount(); i++) {
            Log.d("AttendeeFound0", "No attendee found with UUID: " + getItem(i).getName());
            Attendee attendee = getItem(i);
            Log.d("AttendeeFound1", "No attendee found with UUID: " + attendee.getName());
            if (attendee.getUuid().equals(uuid)) {
                Log.d("AttendeeFound2", "No attendee found with UUID: " + attendee.getName());
                return attendee;
            }
        }
        return null;
    }


}
