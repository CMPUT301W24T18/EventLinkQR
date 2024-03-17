package com.example.eventlinkqr;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.navigation.Navigation;

/**
 * Adapter for displaying notifications in a ListView. Each notification includes a title, description,
 * and the time since the notification was received.
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {

    private List<Notification> notifications;
    private Context context;
    private String source; // Add this line

    /**
     * Constructs a new NotificationAdapter.
     *
     * @param context          The current context.
     * @param notifications    The list of notifications to display.
     */
    public NotificationAdapter(Context context, List<Notification> notifications, String source) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
        this.source = source;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Retrieve the notification item based on position in the list
        Notification notification = getItem(position);

        // Check if an existing view is being reused, otherwise inflate a new view from XML
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
        }

        // Find the TextViews in the convertView
        TextView titleView = convertView.findViewById(R.id.tvNotificationTitle);
        TextView descriptionView = convertView.findViewById(R.id.tvNotificationDescription);
        TextView timestampView = convertView.findViewById(R.id.tvNotificationTime);

        // Set the text for each TextView with the appropriate notification data
        titleView.setText(notification.getTitle());
        descriptionView.setText(notification.getDescription());
        timestampView.setText(notification.getTimeSinceNotification());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a Bundle to pass data to NotificationDetailFragment
                Bundle bundle = new Bundle();
                bundle.putString("title", notification.getTitle());
                bundle.putString("message", notification.getDescription());
                bundle.putString("source", NotificationAdapter.this.source);

                // Determine the navigation action based on the source
                int actionId = NotificationAdapter.this.source.equals("organizer") ?
                        R.id.action_viewNotifications_to_oneDetailedNotification :
                        R.id.action_notificationDisplayPage_to_DetailPage;

                Navigation.findNavController(view).navigate(actionId, bundle);
            }
        });


        return convertView;
    }
}

