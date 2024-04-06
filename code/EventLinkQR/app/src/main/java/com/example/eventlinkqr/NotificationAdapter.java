package com.example.eventlinkqr;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;

import java.util.List;

/**
 * Custom ArrayAdapter for displaying notifications with varying content and layout
 * depending on the context (organizer or user).
 * This adapter supports conditional inflation of view layouts and setting up content
 * dynamically based on the notification's details and the source of the notification list.
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {

    private List<Notification> notifications;
    private Context context;
    private String source; // Identifier for the adapter's context (organizer or user)
    private NotificationManager notificationManager;



    /**
     * Constructs a new instance of NotificationAdapter.
     *
     * @param context The current context, used to inflate the layout file.
     * @param notifications A list of Notification objects to be displayed.
     * @param source A string identifier indicating the context (organizer or user)
     *               which affects layout inflation.
     * @param notificationManager An instance of NotificationManager used to manage operations
     *                             on notifications, such as marking them as read.
     */
    public NotificationAdapter(Context context, List<Notification> notifications, String source, NotificationManager notificationManager) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
        this.source = source;
        this.notificationManager = notificationManager;
    }

    /**
    * Provides a view for an AdapterView
     **/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Retrieve the notification item based on position in the list
        Notification notification = getItem(position);

        // Inflate a different layout based on the source
        if (convertView == null || !convertView.getTag().equals(source)) {
            if (source.equals("organizer")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item_organizer, parent, false);
                convertView.setTag("organizer"); // Tag the view to identify its type
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
                convertView.setTag("user"); // Tag the view to identify its type

                ImageView statusIcon = convertView.findViewById(R.id.notificationStatusIcon);
                if (!notification.isRead()) {
                    // Show red circle for unread notifications
                    statusIcon.setImageResource(R.drawable.ic_unread_tick);
                } else {
                    // Show green circle for read notifications
                    statusIcon.setImageResource(R.drawable.ic_blue_tick);
                }
            }
        }

        if (convertView.getTag() == "user"){
            TextView eventName = convertView.findViewById(R.id.tvEventName);
            eventName.setText(notification.getEventName());
        }

        // Find the TextViews in the convertView
        TextView heading = convertView.findViewById(R.id.tvNotificationHeading);
        TextView description = convertView.findViewById(R.id.tvNotificationDescription);
        TextView timestampView = convertView.findViewById(R.id.tvNotificationTime);

        // Set the text for each TextView with the appropriate notification data
        heading.setText(notification.getTitle());
        description.setText(notification.getDescription());
        timestampView.setText(notification.getTimeSinceNotification());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a Bundle to pass data to NotificationDetailFragment
                Bundle bundle = new Bundle();
                bundle.putString("title", notification.getTitle());
                bundle.putString("message", notification.getDescription());
                bundle.putString("eventName", notification.getEventName());
                bundle.putString("source", NotificationAdapter.this.source);

                if ("user".equals(NotificationAdapter.this.source)) {
                    bundle.putString("eventId", notification.getEventId());
                    notificationManager.markNotificationAsRead(notification.getTitle(), notification.getDescription());

                    // Logic to navigate to the detail page for users
                    Navigation.findNavController(view).navigate(R.id.action_notificationDisplayPage_to_DetailPage, bundle);
                } else {
                    // Logic to navigate to the detailed notification for organizers
                    Navigation.findNavController(view).navigate(R.id.action_viewNotifications_to_oneDetailedNotification, bundle);
                }
            }
        });
        return convertView;
    }
}

