package com.example.eventlinkqr;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * LandingPage is an AppCompatActivity that serves as the entry point of the app.
 * It manages the initial setup and navigation based on whether the user has a UUID.
 * The class also handles Firebase Messaging token retrieval and creates a notification channel
 * for devices running Android Oreo (API 26) or higher.
 */


public class LandingPage extends AppCompatActivity {
    private ActivityResultLauncher<String> requestPermissionLauncher;

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

        initializeNotificationChannel();
        handleCreateProfileButton();

        // Initialize the permission request launcher
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "Notification permission granted");
                    } else {
                        Log.d(TAG, "Notification permission denied");
                    }
                });

        // Request notification permission for Android Tiramisu and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission();
        }
    }

    /**
     * Handles the logic for the Create Profile button,
     * directing users to the profile creation page if no UUID is found, or to the main activity if it exists.
     */
    private void handleCreateProfileButton() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String uuid = prefs.getString("UUID", null);

        Button createProfileButton = findViewById(R.id.createProfile);

        Intent intent = new Intent(LandingPage.this, AttendeeMainActivity.class);
        if (uuid == null) {
            createProfileButton.setOnClickListener(v -> {
                ((RelativeLayout) findViewById(R.id.landing_rel_layout)).setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.landing_container, new CreateProfile())
                        .commit();
            });
        } else {
            startActivity(intent);
            finish();
        }
    }

    /**
     * Initializes the notification channel for the app,
     * which is required for app notifications on Android Oreo and above.
     */
    private void initializeNotificationChannel() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
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
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Requests notification permission for Android Tiramisu (API level 33) and above. For these versions,
     * the app must ask for POST_NOTIFICATIONS permission to display notifications. This method checks for
     * permission and requests it if not already granted. For versions below Tiramisu, this permission is not
     * required, and the method does not perform any action, ensuring backward compatibility.
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}