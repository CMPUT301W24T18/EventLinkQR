package com.example.eventlinkqr;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private Switch toggleLocation; // Used for location permission
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private String uuid; // Unique identifier for the attendee
    private AttendeeArrayAdapter attendeeArrayAdapter; // Adapter for managing attendees

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_profile); // Set the content view

        // Initialize UI components
        etName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.phoneNumberEdit);
        etHomepage = findViewById(R.id.homepageEdit);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnBack = findViewById(R.id.btnBack);
        Button photoButton = findViewById(R.id.btnEditProfile);
        Button switchAccount = findViewById(R.id.switch_account);
        toggleLocation = findViewById(R.id.toggleLocation);
        // Set a listener for the location switch
        toggleLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            onToggleLocationButtonClicked(isChecked);
        });

        attendeeArrayAdapter = AttendeeArrayAdapter.getInstance(); // Get the singleton instance of the adapter

        checkUUIDAndLoadProfile(); // Check UUID and load profile data

        // return to the select page to switch account type
        switchAccount.setOnClickListener(v -> {
            Intent intent = new Intent(AttendeeProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });
        btnSave.setOnClickListener(view -> fetchAndUpdateFCMToken()); // Fetch FCM token and save profile
        btnBack.setOnClickListener(view -> finish());// Finish activity on back button click

        Bitmap deterministicBitmap = ImageManager.generateDeterministicImage(uuid);


        ImageView preview = findViewById(R.id.ivProfileImage);
        preview.setImageBitmap(deterministicBitmap);

        photoButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, UploadImageActivity.class);
            intent.putExtra("origin", "Attendee");
            intent.putExtra("uuid", uuid);
            startActivity(intent);
        });

        // Reset App Data button
        Button btnResetApp = findViewById(R.id.btnResetApp);

        // Reset app data on button click
        btnResetApp.setOnClickListener(view -> resetAppData());
    }

    /**
     * Checks UUID and loads profile data.
     */
    private void checkUUIDAndLoadProfile() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Intent intent = getIntent();
        uuid = intent.getStringExtra("UUID"); // Get UUID from intent

        if (uuid == null) {
            findViewById(R.id.switch_account).setVisibility(View.GONE);
            // New profile: generate a new UUID
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UUID", uuid);
            editor.apply();
        } else {
            findViewById(R.id.switch_account).setVisibility(View.VISIBLE);
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
        Boolean locationEnabled = toggleLocation.isChecked();


        Attendee attendee = new Attendee(uuid, name, phoneNumber, homepage, fcmToken, locationEnabled);

        // Validate name is not null or empty
        if (name == null || name.trim().isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate phone number if provided, and ensure it is exactly 10 digits
        if (!phoneNumber.isEmpty()) {
            if (!phoneNumber.matches("\\d{10}")) {
                Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uuid).set(attendee)
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
        Intent intent = new Intent(AttendeeProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Loads an attendee's profile from Firestore based on the provided UUID.
     * @param uuid The unique identifier for the attendee.
     */
    private void loadProfile(String uuid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Attendee attendee = documentSnapshot.toObject(Attendee.class);
                    if (attendee != null) {
                        etName.setText(attendee.getName());
                        etPhoneNumber.setText(attendee.getPhone_number());
                        etHomepage.setText(attendee.getHomepage());
                        toggleLocation.setChecked(attendee.getLocation_enabled());
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

        // Also clear NotificationPrefs shared preferences
        SharedPreferences notificationPrefs = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        SharedPreferences.Editor notificationEditor = notificationPrefs.edit();
        notificationEditor.clear(); // Clear all preferences related to notification
        notificationEditor.apply();

        // Redirect to LandingPage
        Intent intent = new Intent(this, LandingPage.class);
        startActivity(intent);
        finish();
    }

    /**
     * Handles the location switch toggle and first time location permissions.
     * @param isChecked The state of the switch
     */
    private void onToggleLocationButtonClicked(boolean isChecked) {
        if (isChecked) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // First time enable location tracking, make a request for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                // The switch is on and location permission is granted
                Toast.makeText(this, "Location tracking enabled", Toast.LENGTH_SHORT).show();
            }
        } else {
            // The switch is off
            Toast.makeText(this, "Location tracking disabled", Toast.LENGTH_SHORT).show();
        }
    }
}