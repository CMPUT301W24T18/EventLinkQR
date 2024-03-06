package com.example.eventlinkqr;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
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

    /**
     * Fetches the current FCM token. Upon successful retrieval, calls {@code saveProfile} to save the
     * attendee's profile data along with the token.
     */
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
     * Loads an attendee's profile from Firestore based on the provided UUID.
     * @param uuid The unique identifier for the attendee.
     */
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