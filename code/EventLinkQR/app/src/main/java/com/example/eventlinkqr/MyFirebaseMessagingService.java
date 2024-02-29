package com.example.eventlinkqr;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // If you need to handle the generation of a new token, do it here.
        // This method will be called whenever a new token is generated.
        Log.d(TAG, "Refreshed token: " + token);

        // You can send the token to your server or store it locally for later use.
    }
}