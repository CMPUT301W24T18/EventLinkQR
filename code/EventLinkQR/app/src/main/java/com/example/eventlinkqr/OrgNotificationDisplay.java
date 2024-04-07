package com.example.eventlinkqr;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of notifications related to an event for organizers.
 * Includes functionality to refresh notifications and navigate to the notification creation page.
 */
public class OrgNotificationDisplay extends Fragment {
    /**
     * ListView for displaying notifications.
     */
    private ListView listView;

    /**
     * Adapter for the ListView that displays notifications.
     */
    private ArrayAdapter<String> adapter;

    /**
     * SwipeRefreshLayout for implementing pull-to-refresh functionality.
     */
    private SwipeRefreshLayout swipeRefreshLayout;

    // Handler and Runnable for auto-refresh
    private Handler autoRefreshHandler = new Handler();
    private Runnable autoRefreshRunnable;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_notification_display, container, false);
        Button notificationButton = view.findViewById(R.id.btnSendNotification);

        Event event = ((UserMainActivity) requireActivity()).getCurrentEvent();
        String eventName = event.getName(); // Define eventName from event object
        String eventId = event.getId(); // Define eventId from event object

        Toolbar orgEventToolBar = view.findViewById(R.id.org_event_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(orgEventToolBar);
        orgEventToolBar.setTitle(null);
        // make the back button return to the home page
        orgEventToolBar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.orgEventFragment));


        TextView header = view.findViewById(R.id.tvNotificationsHeader);
        header.setText(eventName);

        listView = view.findViewById(R.id.lvNotifications);
        List<Notification> notifications = new ArrayList<>();
        listView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call fetchNotifications here
                fetchNotifications(eventId); // This will fetch the token and refresh notifications
                swipeRefreshLayout.setRefreshing(false); // This will stop the refresh animation
            }
        });

        autoRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                if(getActivity() != null && isAdded()) { // Check if Fragment is attached
                    fetchNotifications(eventId); // Call your method to fetch notifications
                    autoRefreshHandler.postDelayed(this, 2500); // Schedule the next execution every 5 seconds
                }
            }
        };

        autoRefreshHandler.post(autoRefreshRunnable);

        notificationButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_viewNotification_to_sendNotification));


        fetchNotifications(eventId);

        return view;

    }


    /**
     * Fetches notifications for a specific event based on its ID and updates the display.
     * Utilizes the NotificationManager to retrieve notifications and updates the ListView
     * with the fetched data. If an error occurs during fetching, it is logged for further action.
     *
     * @param eventId The unique identifier of the event to fetch notifications for.
     */
    private void fetchNotifications(String eventId) {
        NotificationManager manager = new NotificationManager(requireContext());

        manager.fetchOrganizerNotifications(eventId, new NotificationsFetchListener() {
            @Override
            public void onNotificationsFetched(List<Notification> notifications) {
                NotificationAdapter adapter = new NotificationAdapter(getContext(), notifications,"organizer", manager);
                listView.setAdapter(adapter);
            }
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error fetching notifications", e);
                // Will Handle error later
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop the auto-refresh when the fragment is not visible
        autoRefreshHandler.removeCallbacks(autoRefreshRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume auto-refresh when the fragment becomes visible again
        autoRefreshHandler.post(autoRefreshRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Ensure the handler is stopped to avoid memory leaks
        autoRefreshHandler.removeCallbacks(autoRefreshRunnable);
    }
}
