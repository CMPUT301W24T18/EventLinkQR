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

    // Uncomment and import BufferedImage if you wish to add image handling.
    // private BufferedImage image;

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

    // Add methods for image handling here if needed.
    // Ensure you import and handle BufferedImage appropriately.
}