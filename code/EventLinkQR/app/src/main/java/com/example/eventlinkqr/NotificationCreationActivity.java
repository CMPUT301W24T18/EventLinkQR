package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;


/**
 * An activity that allows organizers to create and send notifications.
 * This page is intended for the event organizers to be able to send out notifications related
 * to their events. The actual implementation of sending notifications is not yet completed.
 */
public class NotificationCreationActivity extends AppCompatActivity {
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        notificationManager = new NotificationManager();

        String eventId = getIntent().getStringExtra("eventId");

        final EditText titleInput = findViewById(R.id.etNotificationTitle);
        final EditText messageInput = findViewById(R.id.etNotificationMessage);
        Button sendButton = findViewById(R.id.btnCreateNotification);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString();
                String message = messageInput.getText().toString();
                notificationManager.sendNotificationToDatabase(eventId, title, message);
                finish(); // Close activity
            }
        });
    }


}
