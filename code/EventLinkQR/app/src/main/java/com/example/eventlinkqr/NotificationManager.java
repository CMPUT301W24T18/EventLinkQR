package com.example.eventlinkqr;

import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationManager {
    private static final String TAG = "NotificationManager";
    private final FirebaseFirestore db;

    public NotificationManager() {
        db = FirebaseFirestore.getInstance();
    }

    public void sendNotificationToDatabase(String eventId, String title, String description) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("eventId", eventId);
        notificationData.put("heading", title);
        notificationData.put("description", description);

        db.collection("notifications_testing").add(notificationData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }


    public void fetchNotifications(NotificationsFetchListener listener) {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and retrieve notifications using this token
                    Log.d(TAG, "FCM Token: " + token);

                    FirebaseFirestore.getInstance().collection("userNotifications").document(token)
                            .get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists() && documentSnapshot.contains("notifications")) {
                                    List<Map<String, Object>> notificationsMapList = (List<Map<String, Object>>) documentSnapshot.get("notifications");
                                    List<Notification> notifications = new ArrayList<>();
                                    Collections.reverse(notificationsMapList);
                                    for (Map<String, Object> notifMap : notificationsMapList) {
                                        String title = (String) notifMap.get("title");
                                        String body = (String) notifMap.get("body");
                                        Timestamp ts = (Timestamp) notifMap.get("timestamp");
                                        Date notificationDate = ts.toDate();
                                        String timeSinceNotification = getTimeSince(notificationDate);
                                        notifications.add(new Notification(title, body, timeSinceNotification));
                                    }
                                    listener.onNotificationsFetched(notifications);
                                } else {
                                    listener.onError(new Exception("No notifications found"));
                                }
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Error fetching notifications", e);
                                listener.onError(e);
                            });
                }
        );
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



