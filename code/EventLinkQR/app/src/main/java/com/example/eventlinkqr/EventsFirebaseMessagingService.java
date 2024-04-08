package com.example.eventlinkqr;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Handles incoming messages from Firebase Cloud Messaging (FCM).
 * This service is responsible for receiving messages sent via FCM. It processes the data payload contained within
 * these messages and creates user-visible notifications to inform the user of new messages. Additionally, it handles
 * the creation of new FCM registration tokens which are crucial for receiving messages.
 */
public class EventsFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";

    /**
     * Called when a new message from FCM is received.
     * This method is triggered whenever a new message is received from Firebase Cloud Messaging. It extracts the title
     * and body from the message's data payload and displays a notification to the user.
     *
     * @param remoteMessage The message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            // Extract the title and body from the data payload.
            String title = remoteMessage.getData().get("title");
            String messageBody = remoteMessage.getData().get("body");
            String eventId = remoteMessage.getData().get("eventId");
            String eventName = remoteMessage.getData().get("eventName");
            String click_action = "TARGET_NOTIFICATION";

            if (title != null && messageBody != null) {
                showNotification(title, messageBody, eventId, eventName, click_action);
            }
        }
    }

    /**
     * Creates and shows a simple notification containing the received FCM message.
     *
     * @param title       The title of the message to be displayed in the notification.
     * @param messageBody The body text of the message to be displayed in the notification.
     */
    private void showNotification(String title, String messageBody, String eventId, String eventName, String intentTest) {
        Intent intent = new Intent(intentTest);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("notification_title", title);
        intent.putExtra("notification_message", messageBody);
        intent.putExtra("eventId", eventId);
        intent.putExtra("eventName", eventName);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        // Define the notification channel ID.
        String channelId = "event_notifications";
        // Build the notification.
        NotificationCompat.Builder notificationBuilder;
        notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.baseline_notifications_24) // Set the icon for the notification.
                .setContentTitle(title) // Set the title of the notification.
                .setContentText(messageBody) // Set the body text of the notification.
                .setAutoCancel(true) // Make notification dismissible when tapped.
                .setContentIntent(pendingIntent); // Set the intent to fire when the notification is tapped.

        // Get the system's NotificationManager service.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Issue the notification.
        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * Called if the FCM registration token is updated.
     * <p>
     * This may occur if the security of the previous token had been compromised. This call is initiated by the
     * Firebase library before the new token is sent to the server.
     *
     * @param token The new token for the client app instance.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Here, we can send the token to your server for registration
    }
}

