package com.example.eventlinkqr;

import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;
import android.content.SharedPreferences;
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

    /**
     * Initializes a new instance of the NotificationManager class.
     * This constructor specifically initializes a Firestore instance for use in notification management.
     */
    public NotificationManager() {
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
    public void sendNotificationToDatabase(String eventId, String title, String description) {
        DocumentReference eventDocumentRef = db.collection("Notifications").document(eventId);

        // Create a new notification Map to represent the notification details
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("heading", title);
        notificationData.put("description", description);
        notificationData.put("timestamp", new Date());


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
     * Fetches notifications for the current user based on their Firebase Messaging token.
     * Notifications are retrieved from the "userNotifications" document in Firestore.
     * The method asynchronously returns a list of notifications to the provided listener.
     *
     * @param listener The listener that handles the fetched notifications or an error if one occurs.
     */
//    public void fetchNotifications(String uuid, NotificationsFetchListener listener) {
//
//        FirebaseFirestore.getInstance().collection("userNotifications").document(uuid)
//                .get().addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists() && documentSnapshot.contains("notifications")) {
//                        List<Map<String, Object>> notificationsMapList = (List<Map<String, Object>>) documentSnapshot.get("notifications");
//                        List<Notification> notifications = new ArrayList<>();
//                        Collections.reverse(notificationsMapList);
//                        for (Map<String, Object> notifMap : notificationsMapList) {
//                            String title = (String) notifMap.get("title");
//                            String body = (String) notifMap.get("body");
//                            Timestamp ts = (Timestamp) notifMap.get("timestamp");
//                            Date notificationDate = ts.toDate();
//                            String timeSinceNotification = getTimeSince(notificationDate);
//                            notifications.add(new Notification(title, body, timeSinceNotification));
//                        }
//                        listener.onNotificationsFetched(notifications);
//                    } else {
//                        listener.onError(new Exception("No notifications found"));
//                    }
//                }).addOnFailureListener(e -> {
//                    Log.e(TAG, "Error fetching notifications", e);
//                    listener.onError(e);
//                });
//
//    }
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
                            Date notificationDate = ts.toDate();
                            String timeSinceNotification = getTimeSince(notificationDate);

                            // Fetch event name using eventId
                            db.collection("Events").document(eventId).get().addOnSuccessListener(eventDoc -> {
                                String eventName = eventDoc.getString("name"); // Assuming event name is stored as 'name'
                                Notification notification = new Notification(title, body, eventId, timeSinceNotification);
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



