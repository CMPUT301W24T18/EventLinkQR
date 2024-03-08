package com.example.eventlinkqr;
import java.util.List;

/**
 * Interface for handling the outcome of fetching notifications.
 * Implementations of this interface should define how to process
 * a list of notifications once fetched and how to handle errors.
 */
public interface NotificationsFetchListener {

    /**
     * Called when notifications are successfully fetched.
     * @param notifications A list of {@link Notification} objects representing the fetched notifications.
     */
    void onNotificationsFetched(List<Notification> notifications);

    /**
     * Called when an error occurs during the notification fetch process.
     * @param e The exception that was encountered.
     */
    void onError(Exception e);
}