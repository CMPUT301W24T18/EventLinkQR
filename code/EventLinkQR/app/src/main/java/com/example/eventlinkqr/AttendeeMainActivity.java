package com.example.eventlinkqr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ListView;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

/**
 * Main activity class for attendees in the event management application.
 */
public class AttendeeMainActivity extends Activity {

    // UI components: buttons and a list view
    MaterialButton homeButton, scanButton, profileButton, notificationButton;
    ListView eventListView;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the attendee main layout
        setContentView(R.layout.attendee_main_layout);

        // Initialize UI components
        homeButton = findViewById(R.id.attendee_home_button);
        scanButton = findViewById(R.id.attendee_scan_button);
        profileButton = findViewById(R.id.attendee_profile_button);
        notificationButton = findViewById(R.id.attendee_notification_button);
        eventListView = findViewById(R.id.event_list_view);

        homeButton.setOnClickListener(view -> {
            // Create an intent to start AttendeeMainActivity
            Intent intent = new Intent(AttendeeMainActivity.this, AttendeeMainActivity.class);
            startActivity(intent);
        });

        // Set a click listener for the profile button
        profileButton.setOnClickListener(view -> {
            // Create an intent to start AttendeeProfileActivity
            Intent intent = new Intent(AttendeeMainActivity.this, AttendeeProfileActivity.class);

            // Retrieve UUID from SharedPreferences and pass it to the next activity
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String uuid = prefs.getString("UUID", null);
            if (uuid != null) {
                intent.putExtra("UUID", uuid);
            }
            // Start the AttendeeProfileActivity
            startActivity(intent);
        });

        notificationButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with showing notifications
                    Intent intent = new Intent(this, NotificationDisplayActivity.class);
                    startActivity(intent);
                } else {
                    // Permission denied, guide user to settings
                    showCustomPermissionDialog();
                }
            } else {
                // For Android versions below Tiramisu, permission model is different and direct system settings guidance may be needed if notifications are turned off
                Intent intent = new Intent(this, NotificationDisplayActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Displays a custom dialog to request notification permission from the user.
     * This method first checks if the user has already been prompted for notification permission.
     * If not, it presents an AlertDialog asking the user if they wish to enable notifications for the app.
     * A positive response directs the user to the app's system settings to enable notifications,
     * while a negative response simply proceeds to the NotificationDisplayActivity.
     * Regardless of the user's choice, their decision is recorded to avoid repeated prompts.
     */
    private void showCustomPermissionDialog() {
        if (!shouldPromptForNotificationPermission()) {
            // User has already been prompted, proceed directly to NotificationDisplayActivity
            Intent intent = new Intent(this, NotificationDisplayActivity.class);
            startActivity(intent);
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Enable Notifications")
                .setMessage("Notifications help you stay up to date with important events. Would you like to enable notifications for our app?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Record that the user has been prompted
                    SharedPreferences.Editor editor = getSharedPreferences("NotificationPrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("hasBeenPromptedForNotificationPermission", true);
                    editor.apply();

                    // Direct users to the app's system settings page
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Record that the user has been prompted
                    SharedPreferences.Editor editor = getSharedPreferences("NotificationPrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("hasBeenPromptedForNotificationPermission", true);
                    editor.apply();

                    // User chose not to enable notifications, proceed to NotificationDisplayActivity
                    Intent intent = new Intent(this, NotificationDisplayActivity.class);
                    startActivity(intent);
                })
                .create().show();
    }

    /**
     * Checks whether the app should prompt the user for notification permission. This is based on
     * whether the user has previously been prompted.
     *
     * @return True if the user has not been prompted before, false otherwise.
     */
    private boolean shouldPromptForNotificationPermission() {
        SharedPreferences prefs = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        return !prefs.contains("hasBeenPromptedForNotificationPermission");
    }

}
