package com.example.eventlinkqr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button orgButton = findViewById(R.id.button3);
        Button attendeeButton = findViewById(R.id.button4);
        Button adminButton = findViewById(R.id.button5);

        orgButton.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, OrgMainActivity.class));
        });

        attendeeButton.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, AttendeeMainActivity.class));
        });

        adminButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AdminMainActivity.class));
        });


    }
}