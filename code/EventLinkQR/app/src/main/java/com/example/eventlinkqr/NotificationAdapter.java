package com.example.eventlinkqr;

import android.content.Context;
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

        return convertView;
    }
}
