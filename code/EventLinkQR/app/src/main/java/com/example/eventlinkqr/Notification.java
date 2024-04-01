package com.example.eventlinkqr;

/**
 * Represents a notification with a title, description, and the time elapsed since the notification was received.
 */
public class Notification {
    private String title;
    private String description;
    private String timeSinceNotification;
    private boolean isRead;
    private String eventId;
    private String eventName; // Add this field

    /**
     * Constructs a new Notification with the specified title, description, and time elapsed since notification.
     *
     * @param title                The title of the notification.
     * @param description          The description of the notification.
     */
    public Notification(String title, String description) {
        this.title = title;
        this.description = description;
    }

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
     * Constructs a new Notification with the specified title, description, and time elapsed since notification.
     *
     * @param title                The title of the notification.
     * @param description          The description of the notification.
     * @param eventId              The eventId of the event which the notification belongs to.
     * @param timeSinceNotification The time elapsed since the notification was created or received, formatted as a string (e.g., "2h" for two hours).
     * @param isRead                A boolean values which tells whether the notification is yet read or unread.
     */
    public Notification(String title, String description, String eventId, String timeSinceNotification, Boolean isRead) {
        this.title = title;
        this.description = description;
        this.eventId = eventId;
        this.timeSinceNotification = timeSinceNotification;
        this.isRead = isRead;
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
     * Returns whether the notification is read or unread by the user
     *
     * @return The read status of the notification.
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Sets the read status of a notification
     *
     * @param read The read status of a notification.
     */
    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Returns the time elapsed since the notification was received.
     *
     * @return The time elapsed since the notification was received.
     */
    public String getTimeSinceNotification() {
        return timeSinceNotification;
    }

    /**
     * Returns the event name of the notification.
     *
     * @return The Event Name of the notification.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the EventName associated with the notification.
     *
     * @param eventName The Event Name associated with the notification.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
