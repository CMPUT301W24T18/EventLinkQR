package com.example.eventlinkqr;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This class represents an attendee in the event management system.
 * It holds information such as the attendee's name, unique identifier (UUID),
 * phone number, and homepage.
 */
public class Attendee {

    // Fields representing attendee's details.
    private String name;         // The name of the attendee.
    private String uuid;         // Unique identifier for the attendee.
    private String phone_number; // Phone number of the attendee.
    private String homepage;     // Homepage URL of the attendee.
    private String fcmToken; // FCM Token for the user
    private boolean location_enabled; // Whether the user has enabled location tracking

/**
 * Constructs an Attendee object with the specified details.
 *
 * @param uuid The unique identifier for the attendee.
 * @param name The name of the attendee.
 * @param phoneNumber The phone number of the attendee.
 * @param homepage The homepage URL of the attendee.
 * @param fcmToken The Firebase Cloud Messaging token associated with the attendee's device.
 * @param location_enabled Whether the user has enabled location tracking.
 */
    public Attendee(String uuid, String name, String phoneNumber, String homepage, String fcmToken, Boolean location_enabled) {
        this.fcmToken = fcmToken;
        this.uuid = uuid;
        this.name = name;
        this.homepage = homepage;
        this.phone_number = phoneNumber;
        this.location_enabled = location_enabled;
    }

    // Uncomment and import BufferedImage if you wish to add image handling.
    // private BufferedImage image;


    // No-argument constructor required for Firebase deserialization
    public Attendee() {
    }

    /**
     * Gets the attendee's name.
     * @return The name of the attendee.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the attendee's name.
     * @param name The name to set for the attendee.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the attendee's phone number.
     * @return The phone number of the attendee.
     */
    public String getPhone_number() {
        return phone_number;
    }

    /**
     * Sets the attendee's phone number.
     * @param phone_number The phone number to set for the attendee.
     */
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    /**
     * Gets the attendee's homepage URL.
     * @return The homepage URL of the attendee.
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * Sets the attendee's homepage URL.
     * @param homepage The homepage URL to set for the attendee.
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    /**
     * Gets the UUID of the attendee.
     * @return The UUID of the attendee.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID for the attendee.
     * @param uuid The UUID to set for the attendee.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the FCM Token of the attendee.
     * @return The FCM token of the attendee.
     */
    public String getFcmToken() { return fcmToken; }

    /**
     * Sets the FCM Token for the attendee.
     * @param fcmToken token The UUID to set for the attendee.
     */
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    /**
     * Gets the location tracking status of the attendee.
     * @return The location tracking status of the attendee.
     */
    public boolean getLocation_enabled() {
        return location_enabled;
    }

    /**
     * Sets the location tracking status for the attendee.
     * @param location_enabled The location tracking status to set for the attendee.
     */
    public void setLocation_enabled(boolean location_enabled) {
        this.location_enabled = location_enabled;
    }


    // Add methods for image handling here if needed.
    // Ensure you import and handle BufferedImage appropriately.
}
