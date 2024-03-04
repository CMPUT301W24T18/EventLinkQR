package com.example.eventlinkqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LandingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String uuid = prefs.getString("UUID", null);

        Button createProfileButton = findViewById(R.id.createProfile);

        if (uuid == null) {
            createProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // User needs to create a profile
                    Intent intent = new Intent(LandingPage.this, AttendeeProfileActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            // UUID exists, go directly to AttendeeMainActivity
            Intent intent = new Intent(LandingPage.this, AttendeeMainActivity.class);
            startActivity(intent);
            finish(); // Close the landing page activity
        }
    }
}
