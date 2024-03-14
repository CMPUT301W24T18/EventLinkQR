package com.example.eventlinkqr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;


public class AdminMainActivity extends Activity {

    private MaterialButton eventButton, userButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the attendee main layout
        setContentView(R.layout.admin_main);

        Button eventButton = findViewById(R.id.button);

        Button userButton = findViewById(R.id.admin_users_button);

        eventButton.setOnClickListener(view -> {
            // Create an intent to start AttendeeMainActivity
            Intent intent = new Intent(AdminMainActivity.this, AdminEvents.class);
            startActivity(intent);
        });

        userButton.setOnClickListener(view -> {
            // Create an intent to start AttendeeMainActivity
            Intent intent = new Intent(AdminMainActivity.this, AdminUsers.class);
            startActivity(intent);
        });




    }

}
