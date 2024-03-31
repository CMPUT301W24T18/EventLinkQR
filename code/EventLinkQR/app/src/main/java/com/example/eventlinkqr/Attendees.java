package com.example.eventlinkqr;

/**
 * This class represents an attendee to an event
 */
public class Attendees {
    private String name;
    private int checkInCount;
    private boolean checkedIn;

    public Attendees(int checkinCount, boolean checkedIn, String name) {
        this.name = name;
        this.checkedIn = checkedIn;
        this.checkInCount = checkinCount;
    }

    /**
     * gets the attendee's name
     * @return the attendee's name
     */
    public String getName() {
        return name;
    }

    public int getCheckInCount() {
        return checkInCount;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    /**
     * stes the name of the attendee
     * @param name the attendee's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * sets the check in count of the attendee
     * @param checkInCount the new checkin count
     */
    public void setCheckInCount(int checkInCount) {
        this.checkInCount = checkInCount;
    }

    /**
     * sets the checked in status
     * @param checkedIn the check in status
     */
    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    /**
     * empty constructor for fireabe converter
     */
    public Attendees() {
    }
}
