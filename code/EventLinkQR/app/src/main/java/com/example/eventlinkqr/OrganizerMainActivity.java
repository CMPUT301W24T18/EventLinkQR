package com.example.eventlinkqr;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.button.MaterialButton;

/**
 * Class for handling the organizer UI and storing all needed data for the Organizer
 */
public class OrganizerMainActivity extends Activity {
    /** initialize all button on the activity*/
    MaterialButton homeButton, scanButton, profileButton;
    Button createEventButton;
    ListView eventListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_main);

        homeButton = findViewById(R.id.org_home_button);
        scanButton = findViewById(R.id.org_scan_button);
        profileButton = findViewById(R.id.org_profile_button);
        createEventButton = findViewById(R.id.create_event_button);

        eventListView = findViewById(R.id.event_list_view);

    }
}
