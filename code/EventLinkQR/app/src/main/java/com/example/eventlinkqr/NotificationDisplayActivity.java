package com.example.eventlinkqr;

import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Activity for displaying a list of notifications to the user.
 * This activity fetches notification data from a Firebase Firestore database and displays it using a custom adapter.
 * It also includes functionality to refresh the notifications list with a SwipeRefreshLayout
 * and navigate to other activities using MaterialButtons.
 */
public class NotificationDisplayActivity extends AppCompatActivity {
    /**
     * ListView for displaying notifications.
     */
    private ListView listView;

    /**
     * Adapter for the ListView that displays notifications.
     */
    private ArrayAdapter<String> adapter;

    /**
     * List that holds the notifications to be displayed.
     */
    private List<String> notificationList = new ArrayList<>();

    /**
     * SwipeRefreshLayout for implementing pull-to-refresh functionality.
     */
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Buttons for navigation to other activities.
     */
    MaterialButton homeButton, scanButton, profileButton, notificationButton;

    /**
     * Initializes the activity, its views, and fetches the initial set of notifications.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_notifications);

        listView = findViewById(R.id.lvNotifications);
        List<Notification> notifications = new ArrayList<>();
        listView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call fetchNotifications here
                getCurrentFcmToken(); // This will fetch the token and refresh notifications

                swipeRefreshLayout.setRefreshing(false); // This will stop the refresh animation
            }
        });

        // Get current FCM token and fetch notifications
        getCurrentFcmToken();

        // Initialize buttons
        homeButton = findViewById(R.id.attendee_home_button);
        scanButton = findViewById(R.id.attendee_scan_button);
        profileButton = findViewById(R.id.attendee_profile_button);
        notificationButton = findViewById(R.id.attendee_notification_button);

        // Home Button Click Listener
        homeButton.setOnClickListener(view -> {
            // Create an intent to start NotificationActivity
            Intent intent = new Intent(NotificationDisplayActivity.this, AttendeeMainActivity.class);
            startActivity(intent);
        });

        // Profile Button Click Listener
        profileButton.setOnClickListener(view -> {
            // Intent to start AttendeeProfileActivity
            Intent intent = new Intent(NotificationDisplayActivity.this, AttendeeProfileActivity.class);

            // Retrieve UUID from SharedPreferences and pass it to the next activity
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String uuid = prefs.getString("UUID", null);
            if (uuid != null) {
                intent.putExtra("UUID", uuid);
            }
            // Start the AttendeeProfileActivity
            startActivity(intent);
        });

        // Notification Button Click Listener
        notificationButton.setOnClickListener(view -> {
            // Create an intent to start NotificationActivity
            Intent intent = new Intent(NotificationDisplayActivity.this, NotificationDisplayActivity.class);
            startActivity(intent);
        });


    }




    /**
     * Fetches notifications from the Firebase Firestore database based on the current FCM token.
     * @param token The Firebase Cloud Messaging token used to identify the device/user.
     */
    private void fetchNotifications(String token) {
        FirebaseFirestore.getInstance().collection("userNotifications").document(token)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("notifications")) {
                        List<Map<String, Object>> notificationsMapList = (List<Map<String, Object>>) documentSnapshot.get("notifications");
                        if (notificationsMapList != null) {
                            List<Notification> notifications = new ArrayList<>();
                            Collections.reverse(notificationsMapList);
                            for (Map<String, Object> notifMap : notificationsMapList) {
                                String title = (String) notifMap.get("title");
                                String body = (String) notifMap.get("body");

                                Timestamp ts = (Timestamp) notifMap.get("timestamp"); // Cast to Timestamp
                                Date notificationDate = ts.toDate(); // Convert Timestamp to Date
                                String timeSinceNotification = getTimeSince(notificationDate);

                                notifications.add(new Notification(title, body, timeSinceNotification));

                            }
                            NotificationAdapter adapter = new NotificationAdapter(NotificationDisplayActivity.this, notifications);
                            listView.setAdapter(adapter);
                        }
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Error fetching notifications", e));
    }

    /**
     * Gets the current Firebase Cloud Messaging token and uses it to fetch notifications.
     */
    private void getCurrentFcmToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }

            // Get new FCM registration token
            String token = task.getResult();

            // Log and retrieve notifications using this token
            Log.d(TAG, "FCM Token: " + token);
            fetchNotifications(token);
        });
    }

    /**
     * Calculates the time elapsed since a given past date.
     * @param pastDate The date to calculate the time since from.
     * @return A string representing the time elapsed since the given date, in an appropriate format (seconds, minutes, hours, or days).
     */
    private String getTimeSince(Date pastDate) {
        long diff = new Date().getTime() - pastDate.getTime(); // Current time - notification time

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "d";
        } else if (hours > 0) {
            return hours + "h";
        } else if (minutes > 0) {
            return minutes + "m";
        } else {
            return seconds + "s";
        }
    }

}

