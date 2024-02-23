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
 * Class for handling the organizer UI and storing all needed data for the Organizer
 */
public class AttendeeMainActivity extends Activity {
    /** initialize all button on the activity*/
    MaterialButton scanButton, profileButton, notificationButton;
    ListView eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_main_layout);

//        homeButton = findViewById(R.id.attendee_home_button);
        scanButton = findViewById(R.id.attendee_scan_button);
        profileButton = findViewById(R.id.attendee_profile_button);
        notificationButton = findViewById(R.id.attendee_notification_button);

        eventListView = findViewById(R.id.event_list_view);

        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(AttendeeMainActivity.this, AttendeeProfileActivity.class);

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String uuid = prefs.getString("UUID", null);
            Log.d("AttendeeMainActivity", "UUID: " + uuid);
            if (uuid != null) {
                intent.putExtra("UUID", uuid);
            }
            startActivity(intent);
        });

    }
}