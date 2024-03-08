package com.example.eventlinkqr;

import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity class for displaying notifications to the user.
 * It retrieves notification data from a Firebase Firestore database and displays the notifications in a list.
 * The class also supports pull-to-refresh functionality using a SwipeRefreshLayout and allows navigation
 * to other activities through MaterialButtons.
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
                fetchNotifications(); // This will fetch the token and refresh notifications
                swipeRefreshLayout.setRefreshing(false); // This will stop the refresh animation
            }
        });

        // Get current FCM token and fetch notifications
        fetchNotifications();

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
     * Fetches notifications from Firestore based on the current FCM token and updates the UI.
     * It uses the NotificationManager class to retrieve notifications and handles success or error
     * with appropriate actions.
     */
    private void fetchNotifications() {
        NotificationManager manager = new NotificationManager();
        manager.fetchNotifications(new NotificationsFetchListener() {
            @Override
            public void onNotificationsFetched(List<Notification> notifications) {
                NotificationAdapter adapter = new NotificationAdapter(NotificationDisplayActivity.this, notifications);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error fetching notifications", e);
                // Will Handle error later
            }
        });
    }
}

