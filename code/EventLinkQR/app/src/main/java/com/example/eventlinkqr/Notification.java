package com.example.eventlinkqr;

/**
 * Represents a notification with a title, description, and the time elapsed since the notification was received.
 */
public class Notification {
    private String title;
    private String description;
    private String timeSinceNotification;

    /**
     * Constructs a new Notification with the specified title, description, and time elapsed since notification.
     *
     * @param title                The title of the notification.
     * @param description          The description of the notification.
     * @param timeSinceNotification The time elapsed since the notification was created or received, formatted as a string (e.g., "2h" for two hours).
     */
    public Notification(String title, String description, String timeSinceNotification) {
        this.title = title;
        this.description = description;
        this.timeSinceNotification = timeSinceNotification;
    }

    /**
     * Sets the title of the notification.
     *
     * @param title The new title of the notification.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the title of the notification.
     *
     * @return The title of the notification.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the description of the notification.
     *
     * @param description The new description of the notification.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the description of the notification.
     *
     * @return The description of the notification.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the time elapsed since the notification was received.
     *
     * @param timeSinceNotification The new time elapsed since the notification was received.
     */
    public void setTimeSinceNotification(String timeSinceNotification) {
        this.timeSinceNotification = timeSinceNotification;
    }

    /**
     * Returns the time elapsed since the notification was received.
     *
     * @return The time elapsed since the notification was received.
     */
    public String getTimeSinceNotification() {
        return timeSinceNotification;
    }
}
