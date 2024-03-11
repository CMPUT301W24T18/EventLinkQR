package com.example.eventlinkqr;

import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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
public class NotificationDisplayActivity extends Fragment {
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
     * Initializes the activity, its views, and fetches the initial set of notifications.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_display_notifications, container, false);


        listView = view.findViewById(R.id.lvNotifications);
        List<Notification> notifications = new ArrayList<>();
        listView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
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
        return view;
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
                NotificationAdapter adapter = new NotificationAdapter(requireActivity(), notifications);
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

