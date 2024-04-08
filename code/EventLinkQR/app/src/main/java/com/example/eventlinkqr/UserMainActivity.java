package com.example.eventlinkqr;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Main activity class for users in the event management application.
 */
public class UserMainActivity extends AppCompatActivity {

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
    private NotificationManager notificationManager;
    private int clickCount = 10;
    private Handler clickHandler = new Handler();
    private Runnable clickResetRunnable;

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

        // Initialize scanner to work from this activity
        scanner = new QRCodeScanner(this);

        // Set the content view to the user main layout
        setContentView(R.layout.main_layout);

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
            Navigation.findNavController(navController).navigate(R.id.attendeeProfilePage);
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

        notificationManager = new NotificationManager(this); // Add this line to initialize notificationManager


        handleNotificationIntent(getIntent());

        setupProfileButton();

        setupScanButton();
    }

    /**
     * Initialize onClick listener for the notifications button
     */
    private void setupProfileButton() {

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnProfilePage()) {
                    Navigation.findNavController(navController).navigate(R.id.attendeeProfilePage);
                } else {

                    // Cancel any existing callbacks
                    clickHandler.removeCallbacks(clickResetRunnable);

                    clickCount--;

                    // Set up a delayed action to reset click count
                    clickResetRunnable = new Runnable() {
                        @Override
                        public void run() {
                            clickCount = 10;
                        }
                    };

                    // Reset the click count if the button isn't pressed again within 1.5 seconds
                    clickHandler.postDelayed(clickResetRunnable, 1500);

                    if (clickCount <= 1 && clickCount > 0) {
                        Toast.makeText(UserMainActivity.this, "About to Enter Admin Mode", Toast.LENGTH_SHORT).show();
                    } else if (clickCount == 0) {

                        UserManager.getUser(attUUID, user -> {

                            System.out.println(user.isAdmin());
                            System.out.println(user.getUuid());
                            System.out.println(user.getFcmToken());

                            if(user != null && user.isAdmin()) {
                                startActivity(new Intent(UserMainActivity.this, AdmMainActivity.class));
                            } else {
                                startActivity(new Intent(UserMainActivity.this, EnterPinActivity.class));
                            }
                        });

                        clickCount = 10; // Reset the click count

                    }

                }
            }
        });

        // Handles the click event on the notification button. For devices running Android 13 (API level 33) or higher,
        // checks if notification permission is granted. If permission is granted, navigates to the NotificationDisplayFragment.
        // If not, shows a custom dialog to guide users to enable notifications. For devices below Android 13, directly
        // navigates to the NotificationDisplayFragment as permission checks are not required.
        notificationButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with showing notifications
                    Navigation.findNavController(navController).navigate(R.id.notificationDisplayPage);
                } else {
                    // Permission denied, guide user to settings
                    showCustomPermissionDialog();
                }
            } else {
                // For Android versions below Tiramisu, permission model is different and direct system settings guidance may be needed if notifications are turned off
                Navigation.findNavController(navController).navigate(R.id.notificationDisplayPage);
            }
        });

    }

    /**
     * Processes new intent for the activity. Used for handling notification clicks when the app is already running.
     *
     * @param intent New intent containing notification data.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Update the current intent
        handleNotificationIntent(intent); // Handle the notification data
    }

    /**
     * Handles the intent from a notification click. Extracts notification details and navigates to the notification detail page.
     *
     * @param intent Intent from notification with data to process.
     */
    private void handleNotificationIntent(Intent intent) {
        // Check if this intent is a notification click
        if (intent.hasExtra("notification_title")) {
            String title = intent.getStringExtra("notification_title");
            String message = intent.getStringExtra("notification_message");
            String eventId = intent.getStringExtra("eventId");
            String eventName = intent.getStringExtra("eventName");
            Log.d("NotificationIntent", "Title: " + title + ", Message: " + message + ", EventID: " + eventId + ", EventName: " + eventName);

            // Navigate to NotificationDetailFragment with the notification data
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("message", message);
            bundle.putString("eventId", eventId);
            bundle.putString("source", "user");
            bundle.putString("eventName", eventName);

            notificationManager.markLastNotificationAsRead();

            Navigation.findNavController(this, R.id.att_nav_controller).navigate(R.id.notificationDetailedPage, bundle);
        }
    }

    /**
     * Determines if the current view is the profile page.
     *
     * @return true if the current view is the profile page, false otherwise.
     */
    private boolean isOnProfilePage() {
        // Obtain the current destination ID from the NavController
        int currentDestinationId = Navigation.findNavController(navController).getCurrentDestination().getId();

        // Check if the current destination ID matches the profile page ID
        return currentDestinationId == R.id.attendeeProfilePage;
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

                        UserManager.getUser(uuid, user -> {
                            if(user.getLocation_enabled()) {
                                getLastLocation(location -> {
                                    EventManager.isSignedUp(uuid, code.getEventId(), isSignedUp -> {
                                        // Check if the user isn't signed up, check if there is space for the user to sign up first
                                        if(isSignedUp){
                                            EventManager.checkIn(this, uuid, profileName, code.getEventId(), location);
                                        }else{
                                            EventManager.signUp(this, uuid, profileName, code.getEventId(), true, location);
                                        }
                                    });
                                });
                            } else {
                                EventManager.isSignedUp(uuid, code.getEventId(), isSignedUp -> {
                                    // Check if the user isn't signed up, check if there is space for the user to sign up first
                                    if(isSignedUp){
                                        EventManager.checkIn(this, uuid, profileName, code.getEventId());
                                    }else{
                                        EventManager.signUp(this, uuid, profileName, code.getEventId(), true, null);
                                    }
                                });
                            }
                        });
                    } else if (code.getCodeType() == QRCode.PROMOTIONAL_TYPE) {
                        // This is a promotional code, redirect to the attendee event details page
                        EventManager.getEventById(code.getEventId(), event -> {
                            setCurrentEvent(event);
                            Navigation.findNavController(navController).navigate(R.id.attendeeEventFragment);
                        });
                    }
                });
            }, e -> {
                Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                Log.d("QRCode", "Failed to scan code: " + e.getMessage());
            }, () -> {});
        });
    }

    /**
     * Displays a custom dialog to request notification permission from the user.
     * This method first checks if the user has already been prompted for notification permission.
     * If not, it presents an AlertDialog asking the user if they wish to enable notifications for the app.
     * A positive response directs the user to the app's system settings to enable notifications,
     * while a negative response simply proceeds to the NotificationDisplayFragment.
     * Regardless of the user's choice, their decision is recorded to avoid repeated prompts.
     */
    private void showCustomPermissionDialog() {
        if (!shouldPromptForNotificationPermission()) {
            // User has already been prompted, proceed directly to NotificationDisplayFragment
            Intent intent = new Intent(this, NotificationDisplayFragment.class);
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

                    // User chose not to enable notifications, proceed to NotificationDisplayFragment
                    Intent intent = new Intent(this, NotificationDisplayFragment.class);
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
                    Log.d("Location", "Failed to get location with error: " + task.getException());
                }
            });
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * gets the profile name of the user
     * @return the profile name of the user
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * gets the uuid of the user
     * @return the uuid of the user
     */
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



     /** Get the nav controller for this activity
     * @return The nav controller
     */
    public FragmentContainerView getNavController() {
        return navController;
    }

}
