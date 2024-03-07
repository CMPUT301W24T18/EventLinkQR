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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * The LandingPage activity serves as the entry point to the attendee section.
 * It also handles the creation of notification channels and requesting notification permissions on Android Tiramisu (13) and above.
 */
public class LandingPage extends AppCompatActivity {

    private ActivityResultLauncher<String> requestPermissionLauncher;

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

        if (uuid == null) {
            createProfileButton.setOnClickListener(v -> {
                Intent intent = new Intent(LandingPage.this, AttendeeProfileActivity.class);
                startActivity(intent);
            });
        } else {
            Intent intent = new Intent(LandingPage.this, AttendeeMainActivity.class);
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

