package com.example.eventlinkqr;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * An activity that allows organizers to create and send notifications.
 * This page is intended for the event organizers to be able to send out notifications related
 * to their events. The actual implementation of sending notifications is not yet completed.
 */
public class NotificationCreationActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    private static final String TAG = "NotificationCreation"; // Added for logging


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        String eventId = getIntent().getStringExtra("eventId");

// Temporarily commented out the notification sending functionality.
// Initially created this page and added basic structure as a placeholder.
// Currently testing notifications directly from the database. I will need to collaborate with Adrien for 
// implementation of this feature, which I plan to address in the next PR. 
        
        final EditText titleInput = findViewById(R.id.etNotificationTitle);
        final EditText messageInput = findViewById(R.id.etNotificationMessage);
        Button sendButton = findViewById(R.id.btnCreateNotification);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString();
                String message = messageInput.getText().toString();


                // Prepare the notification data in a Map
                Map<String, Object> notification = new HashMap<>();
                notification.put("eventId", eventId);
                notification.put("heading", title);
                notification.put("description", message);


                // Save to Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("notifications_testing").add(notification)
                        .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                        .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));



                finish(); // Close activity
            }
        });
    }


}
