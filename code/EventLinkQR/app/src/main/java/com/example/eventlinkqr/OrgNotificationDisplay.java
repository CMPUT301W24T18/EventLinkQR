package com.example.eventlinkqr;

import static android.content.ContentValues.TAG;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * takes care of the event page on the organizer activity
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_notification_display, container, false);
        Button notificationButton = view.findViewById(R.id.btnSendNotification);


        Toolbar orgEventToolBar = view.findViewById(R.id.org_event_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(orgEventToolBar);
        orgEventToolBar.setTitle(null);
        // make the back button return to the home page
        orgEventToolBar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.orgEventFragment));


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

        notificationButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_viewNotification_to_sendNotification));


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
                NotificationAdapter adapter = new NotificationAdapter(getContext(), notifications);
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
