package com.example.eventlinkqr;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A Fragment subclass for displaying a list of notifications to the user.
 * It fetches notification data from a Firebase Firestore database and updates the UI accordingly.
 * This class also implements pull-to-refresh functionality and auto-refreshes the notifications list periodically.
 */
public class NotificationDisplayFragment extends Fragment {
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
     * Handler and Runnable for implementing auto-refresh functionality.
     */
    private Handler autoRefreshHandler = new Handler();
    private Runnable autoRefreshRunnable;


    /**
     * Inflates the fragment layout, initializes the UI components, and sets up auto-refresh for notifications.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications_page, container, false);


        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String uuid = prefs.getString("UUID", null);
        System.out.println("UUID is : " + uuid);

        listView = view.findViewById(R.id.lvNotifications);
        List<Notification> notifications = new ArrayList<>();

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call fetchNotifications here
                fetchNotifications(uuid); // This will fetch the token and refresh notifications
                swipeRefreshLayout.setRefreshing(false); // This will stop the refresh animation
            }
        });

        autoRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                if(getActivity() != null && isAdded()) { // Check if Fragment is attached
                    fetchNotifications(uuid); // Call your method to fetch notifications
                    autoRefreshHandler.postDelayed(this, 2500); // Schedule the next execution every 5 seconds
                }
            }
        };

        // Start auto-refresh
        autoRefreshHandler.post(autoRefreshRunnable);


        // Get current FCM token and fetch notifications
        fetchNotifications(uuid);
        return view;
    }

    /**
     * Fetches notifications from Firestore based on the user's UUID and updates the UI with the fetched data.
     *
     * @param uuid The UUID of the user whose notifications are to be fetched.
     */
    private void fetchNotifications(String uuid) {
        NotificationManager manager = new NotificationManager(requireContext());
        manager.fetchNotifications(uuid, new NotificationsFetchListener() {
            @Override
            public void onNotificationsFetched(List<Notification> notifications) {
                NotificationAdapter adapter = new NotificationAdapter(requireActivity(), notifications, "user", manager);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error fetching notifications", e);
            }
        });
    }

    /**
     * Pauses the auto-refresh functionality when the fragment is no longer in the foreground.
     */
    @Override
    public void onPause() {
        super.onPause();
        // Stop the auto-refresh when the fragment is not visible
        autoRefreshHandler.removeCallbacks(autoRefreshRunnable);
    }

    /**
     * Resumes the auto-refresh functionality when the fragment comes back to the foreground.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Resume auto-refresh when the fragment becomes visible again
        autoRefreshHandler.post(autoRefreshRunnable);
    }

    /**
     * Stops the auto-refresh handler to avoid memory leaks when the fragment is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Ensure the handler is stopped to avoid memory leaks
        autoRefreshHandler.removeCallbacks(autoRefreshRunnable);
    }
}