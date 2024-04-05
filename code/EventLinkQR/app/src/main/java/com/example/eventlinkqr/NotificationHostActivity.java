package com.example.eventlinkqr;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class NotificationHostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_host); // Make sure you have this layout file.

        // Check if the activity is being recreated after a configuration change
        // to avoid overlapping the fragment on top of an existing one.
        if (savedInstanceState == null) {
            // Get data from the intent
            String title = getIntent().getStringExtra("title");
            String message = getIntent().getStringExtra("message");

            // Pass data to the fragment
            NotificationDetailFragment fragment = new NotificationDetailFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("message", message);
            fragment.setArguments(args);

            // Begin the transaction
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment) // Ensure you have a container in your layout for the fragment.
                    .commit();
        }
    }
}
