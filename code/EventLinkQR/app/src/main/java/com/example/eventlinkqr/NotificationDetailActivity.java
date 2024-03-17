package com.example.eventlinkqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;

/**
 * Activity to display the details of a notification.
 */
public class NotificationDetailActivity extends Fragment {

    /**
     * Called when the activity is starting.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notification_detail, container, false);

        Bundle arguments = getArguments();
        String title = arguments.getString("title", "Default Title");
        String message = arguments.getString("message", "Default Message");
        String event = arguments.getString("", "Default Message");

        Toolbar orgEventToolBar = view.findViewById(R.id.org_event_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(orgEventToolBar);
        orgEventToolBar.setTitle(null);
        // make the back button return to the home page

        orgEventToolBar.setNavigationOnClickListener(v -> {
            if ("organizer".equals(arguments.getString("source"))) {
                Navigation.findNavController(view).navigate(R.id.action_detailedNotification_to_organizerNotification);
            } else {
                Navigation.findNavController(view).navigate(R.id.action_detailedNotification_to_userNotification);
            }
        });


        // Set the title and message to their respective TextViews
        TextView tvTitle = view.findViewById(R.id.tvFullNotificationTitle);
        tvTitle.setText(title);
        TextView tvMessage = view.findViewById(R.id.tvFullNotificationMessage);
        tvMessage.setText(message);

        return view;

    }



}
