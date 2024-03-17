package com.example.eventlinkqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


/**
 * takes care of the event page on the organizer activity
 */
public class OrgSendNotification extends Fragment {

    private NotificationManager notificationManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_notification, container, false);

        Toolbar orgEventToolBar = view.findViewById(R.id.org_event_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(orgEventToolBar);
        orgEventToolBar.setTitle(null);
        // make the back button return to the home page
        orgEventToolBar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.viewNotification));


        notificationManager = new NotificationManager();

        Event event = ((OrgMainActivity) requireActivity()).getCurrentEvent();

        String eventId = event.getId(); // Define eventId from event object
        final EditText titleInput = view.findViewById(R.id.etNotificationTitle);
        final EditText messageInput = view.findViewById(R.id.etNotificationMessage);
        Button sendButton = view.findViewById(R.id.btnCreateNotification);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString();
                String message = messageInput.getText().toString();
                notificationManager.sendNotificationToDatabase(eventId, title, message);
                Navigation.findNavController(view).popBackStack();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}


