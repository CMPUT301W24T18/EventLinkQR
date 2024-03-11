package com.example.eventlinkqr;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

/**
 * Main activity class for attendees in the event management application.
 */
public class AttendeeMainActivity extends AppCompatActivity {

    // UI components: buttons and a list view
    private MaterialButton homeButton, scanButton, profileButton, notificationButton;
    private Event currentEvent;

    private FragmentContainerView navController;
    private FusedLocationProviderClient fusedLocationClient;
    /**
     * QRCode scanner for scanning codes
     */
    private QRCodeScanner scanner;
    private String attUUID;
    private String profileName;

    public interface LocationCallback {
        void onLocationReceived(LatLng location);
    }

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
        homeButton = findViewById(R.id.attendee_home_button);
        scanButton = findViewById(R.id.attendee_scan_button);
        profileButton = findViewById(R.id.attendee_profile_button);
        notificationButton = findViewById(R.id.attendee_notification_button);

        navController = findViewById(R.id.att_nav_controller);

        homeButton.setOnClickListener(v -> {
            Navigation.findNavController(navController).navigate(R.id.attendeeHomePage);
        });

        // Set a click listener for the profile button
        profileButton.setOnClickListener(v -> {
            Navigation.findNavController(navController).navigate(R.id.attendeeProfileActivity);
        });

        // Retrieve UUID from SharedPreferences and pass it to the next activity
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        attUUID = prefs.getString("UUID", null);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (attUUID != null) {
            FirebaseFirestore.getInstance().collection("Users").document(attUUID).get().addOnSuccessListener(d -> {
                profileName = d.getString("name");
            });
        }

        setupProfileButton();

        setupScanButton();
    }

    /**
     * Initialize onClick listener for the profile button
     */
    private void setupProfileButton() {

        // Handles the click event on the notification button. For devices running Android 13 (API level 33) or higher,
        // checks if notification permission is granted. If permission is granted, navigates to the NotificationDisplayActivity.
        // If not, shows a custom dialog to guide users to enable notifications. For devices below Android 13, directly
        // navigates to the NotificationDisplayActivity as permission checks are not required.
        notificationButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with showing notifications
                    Intent intent = new Intent(this, NotificationDisplayActivity.class);
                    startActivity(intent);
                } else {
                    // Permission denied, guide user to settings
                    showCustomPermissionDialog();
                }
            } else {
                // For Android versions below Tiramisu, permission model is different and direct system settings guidance may be needed if notifications are turned off
                Intent intent = new Intent(this, NotificationDisplayActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Initialize the onClick listener for the scan button
     */
    private void setupScanButton() {
        scanButton.setOnClickListener(v -> {
            scanner.codeFromScan(codeText -> {
                QRCodeManager.fetchQRCode(codeText).addOnSuccessListener(code -> {
                    if (code.getCodeType() == QRCode.CHECK_IN_TYPE) {
                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        String uuid = prefs.getString("UUID", null);

                        AttendeeManager.getAttendee(uuid, attendee -> {
                            if(attendee.getLocation_enabled()) {
                                getLastLocation(location -> {
                                    EventManager.checkIn(uuid, profileName, code.getEventId(), location).addOnSuccessListener(x -> {
                                        Toast.makeText(this, "Checked In", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(x -> {
                                        Toast.makeText(this, "Failed to check in", Toast.LENGTH_SHORT).show();
                                    });
                                });
                            } else {
                                EventManager.checkIn(uuid, profileName, code.getEventId()).addOnSuccessListener(x -> {
                                    Toast.makeText(this, "Checked In", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(x -> {
                                    Toast.makeText(this, "Failed to check in", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });

                        EventManager.checkIn(uuid, profileName, code.getEventId()).addOnSuccessListener(x -> {
                            Toast.makeText(this, "Checked In", Toast.LENGTH_SHORT).show();
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

    /**
     * Displays a custom dialog to request notification permission from the user.
     * This method first checks if the user has already been prompted for notification permission.
     * If not, it presents an AlertDialog asking the user if they wish to enable notifications for the app.
     * A positive response directs the user to the app's system settings to enable notifications,
     * while a negative response simply proceeds to the NotificationDisplayActivity.
     * Regardless of the user's choice, their decision is recorded to avoid repeated prompts.
     */
    private void showCustomPermissionDialog() {
        if (!shouldPromptForNotificationPermission()) {
            // User has already been prompted, proceed directly to NotificationDisplayActivity
            Intent intent = new Intent(this, NotificationDisplayActivity.class);
            startActivity(intent);
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Enable Notifications")
                .setMessage("Notifications help you stay up to date with important events. Would you like to enable notifications for our app?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Record that the user has been prompted
                    SharedPreferences.Editor editor = getSharedPreferences("NotificationPrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("hasBeenPromptedForNotificationPermission", true);
                    editor.apply();

                    // Direct users to the app's system settings page
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Record that the user has been prompted
                    SharedPreferences.Editor editor = getSharedPreferences("NotificationPrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("hasBeenPromptedForNotificationPermission", true);
                    editor.apply();

                    // User chose not to enable notifications, proceed to NotificationDisplayActivity
                    Intent intent = new Intent(this, NotificationDisplayActivity.class);
                    startActivity(intent);
                })
                .create().show();
    }

    /**
     * Checks whether the app should prompt the user for notification permission. This is based on
     * whether the user has previously been prompted.
     *
     * @return True if the user has not been prompted before, false otherwise.
     */
    private boolean shouldPromptForNotificationPermission() {
        SharedPreferences prefs = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        return !prefs.contains("hasBeenPromptedForNotificationPermission");
    }

    private void getLastLocation(LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    LatLng location = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                    callback.onLocationReceived(location);
                } else {
                    Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public String getAttUUID() {
        return attUUID;
    }
    /**
     * gets the current event on display
     * @return the current event
     */
    public Event getCurrentEvent() {
        return currentEvent;
    }

    /**
     * sets the current event
     * @param currentEvent the new current event
     */
    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    /**
     * Get the qrCode scanner
     * @return The QRCode scanner
     */
    public QRCodeScanner getScanner() {
        return scanner;
    }
}
