package com.example.eventlinkqr;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * LandingPage is an AppCompatActivity that serves as the entry point of the app.
 * It manages the initial setup and navigation based on whether the user has a UUID.
 * The class also handles Firebase Messaging token retrieval and creates a notification channel
 * for devices running Android Oreo (API 26) or higher.
 */


public class LandingPage extends AppCompatActivity {

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, using findViewById(int)
     * to programmatically interact with widgets in the UI, setting up listeners, and
     * initializing other activity-wide resources.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String uuid = prefs.getString("UUID", null);

        Button createProfileButton = findViewById(R.id.createProfile);

        if (uuid == null) {
            createProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // User needs to create a profile
                    Intent intent = new Intent(LandingPage.this, AttendeeProfileActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            // UUID exists, go directly to AttendeeMainActivity
            Intent intent = new Intent(LandingPage.this, AttendeeMainActivity.class);
            startActivity(intent);
            finish(); // Close the landing page activity
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    // Log and toast
                    Log.d(TAG, "FCM Token: " + token);
                });

        // Check for Android Oreo (API 26) or newer to create a notification channel as it is mandatory for notifications on these versions.
        // For older versions, no channel is required.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("event_notifications", name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d("NotificationChannel", "Channel created");
        }

    }
}
