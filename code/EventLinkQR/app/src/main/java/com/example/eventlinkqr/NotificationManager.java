package com.example.eventlinkqr;


import static com.example.eventlinkqr.Notification.parseNotificationsList;


import android.util.Log;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


/** Class for managing database interaction for Notifications */
public class NotificationManager {
    private static final String TAG = "NotificationManager";
    private static final String COLLECTION_PATH_SEND = "notifications_testing";


    private static final String COLLECTION_PATH_FETCH = "userNotifications";


    /**
     * Utility method to access the notifications collection from Firestore.
     *
     * @return The Firestore collection reference for notifications
     */
    private static CollectionReference getCollection(String COLLECTION_PATH) {
        return FirebaseFirestore.getInstance().collection(COLLECTION_PATH);
    }


    /**
     * Sends a notification to the Firestore database under the "notifications_testing" collection.
     * The notification includes details about the event, such as its ID, title, and description.
     *
     * @param eventId     The unique identifier of the event.
     * @param title       The title of the notification to be sent.
     * @param description The description of the notification to be sent.
     */
    public static void sendNotificationToDatabase(String eventId, String title, String description) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("eventId", eventId);
        notificationData.put("heading", title);
        notificationData.put("description", description);


        getCollection(COLLECTION_PATH_SEND).add(notificationData);
//                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
//                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }


    /**
     * Fetches the FCM token and performs an action with it.
     *
     * @param onTokenFetched Callback with the token as a parameter for further processing.
     */
    private static void fetchFcmTokenAndPerformAction(Consumer<String> onTokenFetched) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }


            // Get new FCM registration token
            String token = task.getResult();
            Log.d(TAG, "FCM Token: " + token);


            onTokenFetched.accept(token);
        });
    }


    public static void fetchNotifications(NotificationsFetchListener listener) {
        fetchFcmTokenAndPerformAction(token -> {
            getCollection(COLLECTION_PATH_FETCH).document(token)
                    .get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && documentSnapshot.contains("notifications")) {
                            List<Map<String, Object>> notificationsMapList = (List<Map<String, Object>>) documentSnapshot.get("notifications");
                            List<Notification> notifications = parseNotificationsList(notificationsMapList);
                            listener.onNotificationsFetched(notifications);
                        } else {
                            listener.onError(new Exception("No notifications found"));
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching notifications", e);
                        listener.onError(e);
                    });
        });
    }


}






