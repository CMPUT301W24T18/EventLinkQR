package com.example.eventlinkqr;
import static android.content.ContentValues.TAG;

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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Activity for managing an attendee's profile.
 */
public class AttendeeProfileActivity extends AppCompatActivity {
    private static final String TAG = "AttendeeProfile";
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

//        fetchAndUpdateFCMToken();

        btnSave.setOnClickListener(view -> fetchAndUpdateFCMToken()); // Fetch FCM token and save profile
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

    private void fetchAndUpdateFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Proceed to save profile with the FCM token
                    saveProfile(token);
                });
    }

    /**
     * Saves the profile data entered by the user.
     */
    private void saveProfile(String fcmToken) {
        // Extract data from UI components
        String name = etName.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String homepage = etHomepage.getText().toString();

        Attendee attendee = new Attendee(uuid, name, phoneNumber, homepage, fcmToken);

//        // Create a new Attendee object and set its properties
//        Attendee attendee = new Attendee();
//        attendee.setName(name);
//        attendee.setPhone_number(phoneNumber);
//        attendee.setHomepage(homepage);
//        attendee.setUuid(uuid);

//        if (!attendeeArrayAdapter.containsUUID(uuid)) {
//            // Add new attendee to the adapter
//            attendeeArrayAdapter.addAttendee(attendee);
//            // Redirect to AttendeeMainActivity after first-time profile creation
//            redirectToMainActivity();
//        } else {
//            // Update existing attendee profile
//            updateAttendeeProfile(attendee);
//        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("attendees_testing").document(uuid).set(attendee)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show();
                    redirectToMainActivity();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show());


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
//    private void loadProfile(String uuid) {
//        Attendee attendee = attendeeArrayAdapter.getAttendeeByUUID(uuid);
//        if (attendee != null) {
//            // Set attendee data to UI components
//            etName.setText(attendee.getName());
//            etPhoneNumber.setText(attendee.getPhone_number());
//            etHomepage.setText(attendee.getHomepage());
//        }
//    }


    private void loadProfile(String uuid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("attendees_testing").document(uuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Attendee attendee = documentSnapshot.toObject(Attendee.class);
                    if (attendee != null) {
                        etName.setText(attendee.getName());
                        etPhoneNumber.setText(attendee.getPhone_number());
                        etHomepage.setText(attendee.getHomepage());
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading profile", e));
    }

    /**
     * Updates the profile of an existing attendee.
     * @param updatedAttendee The attendee with updated information.
     */
//    private void updateAttendeeProfile(Attendee updatedAttendee) {
//        for (int i = 0; i < attendeeArrayAdapter.getCount(); i++) {
//            Attendee attendee = attendeeArrayAdapter.getItem(i);
//            if (attendee.getUuid().equals(uuid)) {
//                // Update attendee data
//                attendee.setName(updatedAttendee.getName());
//                attendee.setPhone_number(updatedAttendee.getPhone_number());
//                attendee.setHomepage(updatedAttendee.getHomepage());
//                break;
//            }
//        }
//        Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
//    }

    private void updateAttendeeProfile(DocumentReference docRef, String fcmToken) {
        String name = etName.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String homepage = etHomepage.getText().toString();

        Map<String, Object> update = new HashMap<>();
        update.put("name", name);
        update.put("phone_number", phoneNumber);
        update.put("homepage", homepage);
        update.put("fcmToken", fcmToken);

        docRef.update(update)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Profile updated successfully");
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    redirectToMainActivity();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error updating profile", e));
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