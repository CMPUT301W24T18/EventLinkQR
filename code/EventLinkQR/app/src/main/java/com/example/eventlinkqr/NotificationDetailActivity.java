package com.example.eventlinkqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

/**
 * Activity to display the details of a notification.
 */
public class NotificationDetailActivity extends AppCompatActivity {

    // Buttons for navigation
    MaterialButton homeButton, scanButton, profileButton, notificationButton;

    /**
     * Called when the activity is starting.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        // Get data from the intent
        String title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("message");

        // Set the title and message to their respective TextViews
        TextView tvTitle = findViewById(R.id.tvFullNotificationTitle);
        tvTitle.setText(title);
        TextView tvMessage = findViewById(R.id.tvFullNotificationMessage);
        tvMessage.setText(message);

        // Initialize buttons
        homeButton = findViewById(R.id.attendee_home_button);
        scanButton = findViewById(R.id.attendee_scan_button);
        profileButton = findViewById(R.id.attendee_profile_button);
        notificationButton = findViewById(R.id.attendee_notification_button);

        // Home button click listener
        homeButton.setOnClickListener(view -> {
            // Create an intent to start AttendeeMainActivity
            Intent intent = new Intent(NotificationDetailActivity.this, AttendeeMainActivity.class);
            startActivity(intent);
        });

        // Profile button click listener
        profileButton.setOnClickListener(view -> {
            // Create an intent to start AttendeeProfileActivity
            Intent intent = new Intent(NotificationDetailActivity.this, AttendeeProfileActivity.class);

            // Retrieve UUID from SharedPreferences and pass it to the next activity
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String uuid = prefs.getString("UUID", null);
            if (uuid != null) {
                intent.putExtra("UUID", uuid);
            }
            // Start the AttendeeProfileActivity
            startActivity(intent);
        });

        // Notification button click listener
        notificationButton.setOnClickListener(view -> {
            // Create an intent to start NotificationActivity
            Intent intent = new Intent(NotificationDetailActivity.this, NotificationDisplayActivity.class);
            startActivity(intent);
        });
    }



}
