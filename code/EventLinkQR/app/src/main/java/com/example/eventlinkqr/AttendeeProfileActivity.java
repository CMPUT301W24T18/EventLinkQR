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

public class AttendeeProfileActivity extends AppCompatActivity {
    private EditText etName, etPhoneNumber, etHomepage;
    private Button btnSave, btnBack;
    private Switch switchLocation; // Assuming you will use this for location permission
    private String uuid;
    private AttendeeArrayAdapter attendeeArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee);

        etName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.phoneNumberEdit);
        etHomepage = findViewById(R.id.homepageEdit);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        switchLocation = findViewById(R.id.switchLocation); // Your switch for location

        attendeeArrayAdapter = AttendeeArrayAdapter.getInstance();

        checkUUIDAndLoadProfile();

        btnSave.setOnClickListener(view -> {
            saveProfile();
        });

        btnBack.setOnClickListener(view -> {
            finish();
        });

        Button btnResetApp = findViewById(R.id.btnResetApp);
        btnResetApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAppData();
            }
        });

    }

    private void checkUUIDAndLoadProfile() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Intent intent = getIntent();
        uuid = intent.getStringExtra("UUID");
        Log.d("UUIDAdapter1", "Attendee List: " + uuid);

        if (uuid == null) {
            // New profile: generate a new UUID
            uuid = UUID.randomUUID().toString();
            Log.d("UUIDAdapter2", "Attendee List: " + uuid);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UUID", uuid);
            editor.apply();
        } else {
            // Existing profile: load it
            Log.d("UUIDAdapter3", "Attendee List: " + uuid);
            loadProfile(uuid);
        }
    }

    private void saveProfile() {
        String name = etName.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String homepage = etHomepage.getText().toString();
        boolean locationPermission = switchLocation.isChecked();

        Attendee attendee = new Attendee();
        attendee.setName(name);
        attendee.setPhone_number(phoneNumber);
        attendee.setHomepage(homepage);
        attendee.setUuid(uuid); // Set UUID for this attendee

        if (!attendeeArrayAdapter.containsUUID(uuid)) {
            attendeeArrayAdapter.addAttendee(attendee);
            // Redirect to AttendeeMainActivity after first-time profile creation
            Intent intent = new Intent(AttendeeProfileActivity.this, AttendeeMainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show();
            finish(); // Close current activity
        } else {
            // Update existing profile
            updateAttendeeProfile(attendee);
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
            // Optionally, show a message or do something else but don't redirect
        }
    }

    private void loadProfile(String uuid) {
        Attendee attendee = attendeeArrayAdapter.getAttendeeByUUID(uuid);
//        Log.d("AttendeeExist", "Attendee List: " + attendee.getName());
        if (attendee != null) {
            etName.setText(attendee.getName());
            etPhoneNumber.setText(attendee.getPhone_number());
            etHomepage.setText(attendee.getHomepage());
        }
    }

    private void updateAttendeeProfile(Attendee updatedAttendee) {
        for (int i = 0; i < attendeeArrayAdapter.getCount(); i++) {
            Attendee attendee = attendeeArrayAdapter.getItem(i);
            if (attendee.getUuid().equals(uuid)) {
                attendee.setName(updatedAttendee.getName());
                attendee.setPhone_number(updatedAttendee.getPhone_number());
                attendee.setHomepage(updatedAttendee.getHomepage());

                Log.d("AttendeeArrayAdapter", "Attendee List: " + attendee.getName());

                // Handle location permission update as well
                break;
            }
        }
    }

    public void resetAppData() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("UUID"); // Clear only the UUID
        editor.apply();

        Intent intent = new Intent(this, LandingPage.class);
        startActivity(intent);
        finish();
    }


}
