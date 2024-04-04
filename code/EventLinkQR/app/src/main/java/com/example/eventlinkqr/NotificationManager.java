package com.example.eventlinkqr;

import static android.content.Context.MODE_PRIVATE;

import android.util.Log;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** Class for managing database interaction for Notifications */
public class NotificationManager {
    private static final String TAG = "NotificationManager";
    private final FirebaseFirestore db;

    private Context context; // Add this line

    /**
     * Initializes a new instance of the NotificationManager class.
     * This constructor specifically initializes a Firestore instance for use in notification management.
     */
    public NotificationManager(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }




    /**
     * Sends a notification to the Firestore database under the "notifications_testing" collection.
     * The notification includes details about the event, such as its ID, title, and description.
     *
     * @param eventId     The unique identifier of the event.
     * @param title       The title of the notification to be sent.
     * @param description The description of the notification to be sent.
     */
    public void sendNotificationToDatabase(String eventId, String title, String description, Boolean isMilestone) {
        DocumentReference eventDocumentRef = db.collection("Notifications").document(eventId);

        // Create a new notification Map to represent the notification details
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("heading", title);
        notificationData.put("description", description);
        notificationData.put("timestamp", new Date());
        notificationData.put("isMilestone", isMilestone);

        // Use a transaction to ensure that the operation is atomic
        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot eventDocument = transaction.get(eventDocumentRef);
                    List<Map<String, Object>> notificationsList;

                    // If the document already exists, retrieve its notifications list and add the new notification
                    if (eventDocument.exists()) {
                        notificationsList = (List<Map<String, Object>>) eventDocument.get("notifications");
                        if (notificationsList == null) { // Initialize the list if it's not present
                            notificationsList = new ArrayList<>();
                        }
                    } else {
                        // Initialize the list for new document
                        notificationsList = new ArrayList<>();
                    }

                    // Add the new notification to the list
                    notificationsList.add(notificationData);

                    // Update the document with the new list of notifications
                    transaction.set(eventDocumentRef, Collections.singletonMap("notifications", notificationsList));
                    return null; // Void function, so return null
                }).addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction success"))
                .addOnFailureListener(e -> Log.w(TAG, "Transaction failure", e));
    }

    /**
     * Fetches notifications for a user based on their UUID and enriches them with event names from the Events collection.
     * Notifications are returned in reverse chronological order. This method handles asynchronous fetching of each
     * notification's event name and aggregates the results. Upon completion, the listener is notified with a list of
     * enriched notifications. If no notifications are found or an error occurs, the listener is notified of the error.
     *
     * @param uuid The UUID of the user to fetch notifications for.
     * @param listener The NotificationsFetchListener to notify with the fetched notifications or an error.
     */
    public void fetchNotifications(String uuid, NotificationsFetchListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("userNotifications").document(uuid)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("notifications")) {
                        List<Map<String, Object>> notificationsMapList = (List<Map<String, Object>>) documentSnapshot.get("notifications");
                        List<Notification> notifications = new ArrayList<>();
                        Collections.reverse(notificationsMapList);

                        // Track pending event name fetches
                        AtomicInteger pendingEventFetches = new AtomicInteger(notificationsMapList.size());

                        for (Map<String, Object> notifMap : notificationsMapList) {
                            String title = (String) notifMap.get("title");
                            String body = (String) notifMap.get("body");
                            String eventId = (String) notifMap.get("eventId"); // Assuming eventId is stored here
                            Timestamp ts = (Timestamp) notifMap.get("timestamp");
                            Boolean isRead = (Boolean) notifMap.get("isRead") ;
                            Date notificationDate = ts.toDate();
                            String timeSinceNotification = getTimeSince(notificationDate);

                            // Fetch event name using eventId
                            db.collection("Events").document(eventId).get().addOnSuccessListener(eventDoc -> {
                                String eventName = eventDoc.getString("name"); // Assuming event name is stored as 'name'
                                Notification notification = new Notification(title, body, eventId, timeSinceNotification, isRead);
                                notification.setEventName(eventName); // Set the event name
                                notifications.add(notification);

                                // Check if all fetches are done
                                if (pendingEventFetches.decrementAndGet() == 0) {
                                    listener.onNotificationsFetched(notifications);
                                }
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Error fetching event name", e);
                                pendingEventFetches.decrementAndGet();
                            });
                        }
                    } else {
                        listener.onError(new Exception("No notifications found"));
                    }
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching notifications", e);
                    listener.onError(e);
                });
    }

    /**
     * Retrieves event-specific notifications from Firestore and processes them into Notification objects.
     * Notifies a listener with either the fetched notifications or an error.
     *
     * Notifications are returned in reverse order, ensuring the most recent is first.
     * If no notifications are found, or if an error occurs during the fetch, the listener is notified of the error.
     *
     * @param eventId The ID of the event to fetch notifications for.
     * @param listener The listener to notify upon completion or error.
     */
    public void fetchOrganizerNotifications(String eventId, NotificationsFetchListener listener) {
        FirebaseFirestore.getInstance().collection("Notifications").document(eventId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Notification> notifications = new ArrayList<>();
                        Map<String, Object> data = task.getResult().getData();

                        if (data != null && data.containsKey("notifications")) {
                            List<Map<String, Object>> notificationsList = (List<Map<String, Object>>) data.get("notifications");
                            Collections.reverse(notificationsList); // Reverse the list to start with the most recent notification
                            for (Map<String, Object> notifMap : notificationsList) {
                                String heading = (String) notifMap.get("heading");
                                String description = (String) notifMap.get("description");
                                Timestamp ts = (Timestamp) notifMap.get("timestamp");
                                Date notificationDate = ts.toDate();
                                String timeSinceNotification = getTimeSince(notificationDate);
                                notifications.add(new Notification(heading, description,  timeSinceNotification));


                            }
                            listener.onNotificationsFetched(notifications);
                        } else {
                            listener.onError(new Exception("No notifications found in the document"));
                        }
                    } else {
                        Log.e(TAG, "Error fetching document: ", task.getException());
                        listener.onError(task.getException());
                    }
                });
    }

    /**
     * Marks a notification as read based on its title and description. It updates the 'isRead' flag
     * for the matched notification in the Firestore database. Assumes only one notification matches
     * the criteria and updates the first one found.
     *
     * @param title The title of the notification.
     * @param description The description of the notification.
     * @param timeSinceNotification Not used in the current implementation, reserved for future use.
     */
    public void markNotificationAsRead(String title, String description, String timeSinceNotification) {
        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String uuid = prefs.getString("UUID", null);

        DocumentReference docRef = db.collection("userNotifications").document(uuid);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> notifications = (List<Map<String, Object>>) documentSnapshot.get("notifications");
                if (notifications != null) {
                    for (Map<String, Object> notification : notifications) {
                        if (title.equals(notification.get("title")) &&
                                description.equals(notification.get("body"))) {
                            notification.put("isRead", true);
                            // Assuming only one notification matches, break after finding it
                            break;
                        }
                    }

                    // Update the entire notifications array back to Firestore
                    docRef.update("notifications", notifications)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Notification marked as read"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error marking notification as read", e));
                }
            }
        }).addOnFailureListener(e -> Log.w(TAG, "Error fetching document", e));
    }


    /**
     * Calculates the time elapsed since a given past date.
     * @param pastDate The date to calculate the time since from.
     * @return A string representing the time elapsed since the given date, in an appropriate format (seconds, minutes, hours, or days).
     */
    private String getTimeSince(Date pastDate) {
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



