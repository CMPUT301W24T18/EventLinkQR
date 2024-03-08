package com.example.eventlinkqr;


import com.google.firebase.Timestamp;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


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
     * @param title                 The title of the notification.
     * @param description           The description of the notification.
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


    static List<Notification> parseNotificationsList(List<Map<String, Object>> notificationsMapList) {
        List<Notification> notifications = new ArrayList<>();
        for (Map<String, Object> notifMap : notificationsMapList) {
            String title = (String) notifMap.get("title");
            String body = (String) notifMap.get("body");
            Timestamp ts = (Timestamp) notifMap.get("timestamp");
            Date notificationDate = ts.toDate();
            String timeSinceNotification = getTimeSince(notificationDate);
            notifications.add(new Notification(title, body, timeSinceNotification));
        }
        return notifications;
    }




    /**
     * Calculates the time elapsed since a given past date.
     *
     * @param pastDate The date to calculate the time since from.
     * @return A string representing the time elapsed since the given date, in an appropriate format (seconds, minutes, hours, or days).
     */
    private static String getTimeSince(Date pastDate) {
        long diff = new Date().getTime() - pastDate.getTime(); // Current time - notification time


        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;


        if (days > 0) {
            return days + "d";
        } else if (hours > 0) {
            return hours + "h";
        } else if (minutes > 0) {
            return minutes + "m";
        } else {
            return seconds + "s";
        }


    }


}
