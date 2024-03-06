package com.example.eventlinkqr;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {
    public NotificationAdapter(Context context, List<Notification> notifications) {
        super(context, 0, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification notification = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
        }

        TextView titleView = convertView.findViewById(R.id.tvNotificationTitle);
        TextView descriptionView = convertView.findViewById(R.id.tvNotificationDescription);
        TextView timestampView = convertView.findViewById(R.id.tvNotificationTime);

        titleView.setText(notification.getTitle());
        descriptionView.setText(notification.getDescription());
        timestampView.setText(notification.getTimestamp());

        // Inside your NotificationAdapter's getView() method, add an OnClickListener to the convertView
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
