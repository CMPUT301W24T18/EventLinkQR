package com.example.eventlinkqr;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

/**
 * Activity for managing an attendee's profile.
 */
public class AttendeeProfileActivity extends AppCompatActivity {
    // UI components: input fields, buttons, and switch
    private EditText etName, etPhoneNumber, etHomepage;
    private Button btnSave, btnBack;
    private Switch switchLocation; // Used for location permission
    private String uuid; // Unique identifier for the attendee
    private AttendeeArrayAdapter attendeeArrayAdapter; // Adapter for managing attendees

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee); // Set the content view

        // Initialize UI components
        etName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.phoneNumberEdit);
        etHomepage = findViewById(R.id.homepageEdit);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        switchLocation = findViewById(R.id.switchLocation);

        attendeeArrayAdapter = AttendeeArrayAdapter.getInstance(); // Get the singleton instance of the adapter

        checkUUIDAndLoadProfile(); // Check UUID and load profile data

        btnSave.setOnClickListener(view -> saveProfile()); // Save profile on button click
        btnBack.setOnClickListener(view -> finish()); // Finish activity on back button click

        // Reset App Data button
        Button btnResetApp = findViewById(R.id.btnResetApp);
        btnResetApp.setOnClickListener(view -> resetAppData()); // Reset app data on button click
    }

    /**
     * Checks UUID and loads profile data.
     */
    private void checkUUIDAndLoadProfile() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Intent intent = getIntent();
        uuid = intent.getStringExtra("UUID"); // Get UUID from intent

        if (uuid == null) {
            // New profile: generate a new UUID
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UUID", uuid);
            editor.apply();
        } else {
            // Existing profile: load it
            loadProfile(uuid);
        }
    }

    /**
     * Saves the profile data entered by the user.
     */
    private void saveProfile() {
        // Extract data from UI components
        String name = etName.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String homepage = etHomepage.getText().toString();

        // Create a new Attendee object and set its properties
        Attendee attendee = new Attendee();
        attendee.setName(name);
        attendee.setPhone_number(phoneNumber);
        attendee.setHomepage(homepage);
        attendee.setUuid(uuid);

        if (!attendeeArrayAdapter.containsUUID(uuid)) {
            // Add new attendee to the adapter
            attendeeArrayAdapter.addAttendee(attendee);
            // Redirect to AttendeeMainActivity after first-time profile creation
            redirectToMainActivity();
        } else {
            // Update existing attendee profile
            updateAttendeeProfile(attendee);
        }
    }

    /**
     * Redirects to AttendeeMainActivity.
     */
    private void redirectToMainActivity() {
        Intent intent = new Intent(AttendeeProfileActivity.this, AttendeeMainActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Loads the profile data for a given UUID.
     * @param uuid The UUID of the attendee.
     */
    private void loadProfile(String uuid) {
        Attendee attendee = attendeeArrayAdapter.getAttendeeByUUID(uuid);
        if (attendee != null) {
            // Set attendee data to UI components
            etName.setText(attendee.getName());
            etPhoneNumber.setText(attendee.getPhone_number());
            etHomepage.setText(attendee.getHomepage());
        }
    }

    /**
     * Updates the profile of an existing attendee.
     * @param updatedAttendee The attendee with updated information.
     */
    private void updateAttendeeProfile(Attendee updatedAttendee) {
        for (int i = 0; i < attendeeArrayAdapter.getCount(); i++) {
            Attendee attendee = attendeeArrayAdapter.getItem(i);
            if (attendee.getUuid().equals(uuid)) {
                // Update attendee data
                attendee.setName(updatedAttendee.getName());
                attendee.setPhone_number(updatedAttendee.getPhone_number());
                attendee.setHomepage(updatedAttendee.getHomepage());
                break;
            }
        }
        Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
    }

    /**
     * Resets the app data, removing the UUID from SharedPreferences.
     */
    public void resetAppData() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("UUID"); // Clear only the UUID
        editor.apply();

        // Redirect to LandingPage
        Intent intent = new Intent(this, LandingPage.class);
        startActivity(intent);
        finish();
    }
}