package com.example.eventlinkqr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import com.google.android.material.button.MaterialButton;

/**
 * Main activity class for attendees in the event management application.
 */
public class AttendeeMainActivity extends Activity {

    // UI components: buttons and a list view
    MaterialButton scanButton, profileButton, notificationButton;
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
        scanButton = findViewById(R.id.attendee_scan_button);
        profileButton = findViewById(R.id.attendee_profile_button);
        notificationButton = findViewById(R.id.attendee_notification_button);
        eventListView = findViewById(R.id.event_list_view);

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
    }
}