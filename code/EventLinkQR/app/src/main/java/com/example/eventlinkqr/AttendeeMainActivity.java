package com.example.eventlinkqr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

/**
 * Main activity class for attendees in the event management application.
 */
public class AttendeeMainActivity extends Activity {

    // UI components: buttons and a list view
    private MaterialButton scanButton, profileButton, notificationButton;
    private ListView eventListView;

    /** QRCode scanner for scanning codes */
    private QRCodeScanner scanner;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scanner = new QRCodeScanner(this);

        // Set the content view to the attendee main layout
        setContentView(R.layout.attendee_main_layout);

        // Initialize UI components
        scanButton = findViewById(R.id.attendee_scan_button);
        profileButton = findViewById(R.id.attendee_profile_button);
        notificationButton = findViewById(R.id.attendee_notification_button);
        eventListView = findViewById(R.id.event_list_view);

        setupProfileButton();

        setupScanButton();
    }

    /** Initialize onClick listener for the profile button*/
    private void setupProfileButton() {
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

    /** Initialize the onClick listener for the scan button */
    private void setupScanButton() {
        scanButton.setOnClickListener(v -> {
            scanner.codeFromScan(codeText -> {
                QRCodeManager.fetchQRCode(codeText).addOnSuccessListener(code -> {
                    if (code.getCodeType() == QRCode.CHECK_IN_TYPE) {
                        EventManager.checkIn("David", code.getEventId()).addOnSuccessListener(x -> {
                            Toast.makeText(this,"Checked In", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(x -> {
                            Toast.makeText(this, "Failed to check in", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }, e -> {
                Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
            });
        });
    }
}