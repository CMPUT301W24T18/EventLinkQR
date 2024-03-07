//package com.example.eventlinkqr;
//
//import static android.content.ContentValues.TAG;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//
//public class LandingPage extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_landing_page);
//
//        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//        String uuid = prefs.getString("UUID", null);
//
//        Button createProfileButton = findViewById(R.id.createProfile);
//
//        if (uuid == null) {
//            createProfileButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // User needs to create a profile
//                    Intent intent = new Intent(LandingPage.this, AttendeeProfileActivity.class);
//                    startActivity(intent);
//                }
//            });
//        } else {
//            // UUID exists, go directly to AttendeeMainActivity
//            Intent intent = new Intent(LandingPage.this, AttendeeMainActivity.class);
//            startActivity(intent);
//            finish(); // Close the landing page activity
//        }
//
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                        return;
//                    }
//                    // Get new FCM registration token
//                    String token = task.getResult();
//                    // Log and toast
//                    Log.d(TAG, "FCM Token: " + token);
//                });
//
//        // Check for Android Oreo (API 26) or newer to create a notification channel as it is mandatory for notifications on these versions.
//        // For older versions, no channel is required.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("event_notifications", name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//            Log.d("NotificationChannel", "Channel created");
//        }
//
//    }
//}

package com.example.eventlinkqr;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;

public class LandingPage extends AppCompatActivity {

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        initializeNotificationChannel();
        handleCreateProfileButton();

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "Notification permission granted");
                        // Permission is granted. Continue the action or workflow in your app.
                    } else {
                        Log.d(TAG, "Notification permission denied");
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their decision.
                    }
                });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission();
        }
    }

    private void handleCreateProfileButton() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String uuid = prefs.getString("UUID", null);

        Button createProfileButton = findViewById(R.id.createProfile);

        if (uuid == null) {
            createProfileButton.setOnClickListener(v -> {
                Intent intent = new Intent(LandingPage.this, AttendeeProfileActivity.class);
                startActivity(intent);
            });
        } else {
            Intent intent = new Intent(LandingPage.this, AttendeeMainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initializeNotificationChannel() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("event_notifications", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            } else {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}

