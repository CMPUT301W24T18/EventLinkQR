package com.example.eventlinkqr;

/**
 * This class represents an user in the event management system.
 * It holds information such as the user's name, unique identifier (UUID),
 * phone number, and homepage.
 */
public class User {

    // Fields representing user's details.
    private String name;         // The name of the user.
    private String uuid;         // Unique identifier for the user.
    private String phone_number; // Phone number of the user.
    private String homepage;     // Homepage URL of the user.
    private String fcmToken; // FCM Token for the user
    private boolean location_enabled; // Whether the user has enabled location tracking
    private boolean isAdmin; // Indicates if the user can go to the Admin mode

/**
 * Constructs an Attendee object with the specified details.
 *
 * @param uuid The unique identifier for the user.
 * @param name The name of the user.
 * @param phoneNumber The phone number of the user.
 * @param homepage The homepage URL of the user.
 * @param fcmToken The Firebase Cloud Messaging token associated with the user's device.
 * @param location_enabled Whether the user has enabled location tracking.
 */
    public User(String uuid, String name, String phoneNumber, String homepage, String fcmToken, Boolean location_enabled, Boolean isAdmin) {
        this.fcmToken = fcmToken;
        this.uuid = uuid;
        this.name = name;
        this.homepage = homepage;
        this.phone_number = phoneNumber;
        this.location_enabled = location_enabled;
        this.isAdmin = isAdmin != null && isAdmin;
    }

    // Uncomment and import BufferedImage if you wish to add image handling.
    // private BufferedImage image;


    // No-argument constructor required for Firebase deserialization
    public User() {
    }

    /**
     * Gets the user's name.
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     * @param name The name to set for the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's phone number.
     * @return The phone number of the user.
     */
    public String getPhone_number() {
        return phone_number;
    }

    /**
     * Sets the user's phone number.
     * @param phone_number The phone number to set for the user.
     */
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    /**
     * Gets the user's homepage URL.
     * @return The homepage URL of the user.
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * Sets the user's homepage URL.
     * @param homepage The homepage URL to set for the user.
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    /**
     * Gets the UUID of the user.
     * @return The UUID of the user.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID for the user.
     * @param uuid The UUID to set for the user.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the FCM Token of the user.
     * @return The FCM token of the user.
     */
    public String getFcmToken() { return fcmToken; }

    /**
     * Sets the FCM Token for the user.
     * @param fcmToken token The UUID to set for the user.
     */
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    /**
     * Gets the location tracking status of the user.
     * @return The location tracking status of the user.
     */
    public boolean getLocation_enabled() {
        return location_enabled;
    }

    /**
     * Sets the location tracking status for the user.
     * @param location_enabled The location tracking status to set for the user.
     */
    public void setLocation_enabled(boolean location_enabled) {
        this.location_enabled = location_enabled;
    }

    /**
     * Gets the admin mode status of the user.
     * @return The admin mode status of the user.
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Sets the admin mode status of the user.
     * @param admin The admin mode status of the user.
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

}
