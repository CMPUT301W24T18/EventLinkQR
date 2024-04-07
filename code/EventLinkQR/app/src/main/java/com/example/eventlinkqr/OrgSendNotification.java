package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

/**
 * A Fragment that provides UI for organizers to create and send notifications for an event.
 * It presents input fields for the notification's title and message, along with a button to send the notification.
 * The notification details are sent to the Firestore database under the corresponding event ID.
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
        View view = inflater.inflate(R.layout.create_notification_page, container, false);

        Toolbar orgEventToolBar = view.findViewById(R.id.org_event_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(orgEventToolBar);
        orgEventToolBar.setTitle(null);
        // make the back button return to the home page
        orgEventToolBar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.viewNotification));

        notificationManager = new NotificationManager(requireContext());

        Event event = ((UserMainActivity) requireActivity()).getCurrentEvent();

        String eventName = event.getName(); // Define eventName from event object

        TextView header = view.findViewById(R.id.tvNotificationsHeader);
        header.setText(eventName);

        String eventId = event.getId(); // Define eventId from event object
        final EditText titleInput = view.findViewById(R.id.etNotificationTitle);
        final EditText messageInput = view.findViewById(R.id.etNotificationMessage);
        Button sendButton = view.findViewById(R.id.btnCreateNotification);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString();
                String message = messageInput.getText().toString();
                // Check if either the title or message is empty
                if (title.isEmpty() || message.isEmpty()) {
                    // Display a toast message and return without sending the notification
                    Toast.makeText(requireContext(), "Both title and message are required.", Toast.LENGTH_LONG).show();
                } else {
                    // Both title and message contain text, proceed with sending the notification
                    notificationManager.sendNotificationToDatabase(eventId, title, message, false);
                    // Navigate back or perform any other required actions after sending
                    Navigation.findNavController(view).popBackStack();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}


