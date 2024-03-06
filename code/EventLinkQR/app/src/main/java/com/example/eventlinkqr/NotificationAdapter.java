package com.example.eventlinkqr;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * Adapter for displaying notifications in a ListView. Each notification includes a title, description,
 * and the time since the notification was received.
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {
    /**
     * Constructs a new NotificationAdapter.
     *
     * @param context       The current context.
     * @param notifications The list of notifications to display.
     */
    public NotificationAdapter(Context context, List<Notification> notifications) {
        super(context, 0, notifications);
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

        // Set an OnClickListener on the convertView
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to start the NotificationDetailActivity
                Intent intent = new Intent(getContext(), NotificationDetailActivity.class);
                // Pass the notification details to the activity
                intent.putExtra("title", notification.getTitle());
                intent.putExtra("message", notification.getDescription());
                // Start the activity
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
