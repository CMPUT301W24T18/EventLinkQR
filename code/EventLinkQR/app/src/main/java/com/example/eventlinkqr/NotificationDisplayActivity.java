//package com.example.eventlinkqr;
//
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//
//import com.google.firebase.firestore.EventListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class NotificationDisplayActivity extends AppCompatActivity {
//
//
//    private ListView listView;
//    private ArrayAdapter<String> adapter;
//    private List<String> notificationList;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_display_notifications);
//
//
//        listView = findViewById(R.id.lvNotifications);
//        notificationList = new ArrayList<>();
//
//
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationList);
//        listView.setAdapter(adapter);
//
//
//        // Listen for changes in the Firestore database
//        FirebaseFirestore.getInstance().collection("notifications_testing")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
//                        if (e != null) {
//                            System.err.println("Listen failed: " + e);
//                            return;
//                        }
//
//
//                        notificationList.clear();
//                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                            String title = doc.getString("heading"); // Assuming the document has a 'title' field
//                            String message = doc.getString("description"); // Assuming the document has a 'message' field
//                            notificationList.add(title + ": " + message);
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//
//        Button btnBackToMain = findViewById(R.id.btnBackToMain);
//        btnBackToMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish(); // This will close the current activity and return to the previous one in the stack.
//            }
//        });
//    }
//}

package com.example.eventlinkqr;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//public class NotificationDisplayActivity extends AppCompatActivity {
//
//    private ListView listView;
//    private ArrayAdapter<String> adapter;
//    private List<String> notificationList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_display_notifications);
//
//        listView = findViewById(R.id.lvNotifications);
//        notificationList = new ArrayList<>();
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationList);
//        listView.setAdapter(adapter);
//
//        // Retrieve and display locally stored notifications
//        notificationList.addAll(retrieveNotifications());
//        adapter.notifyDataSetChanged();
//
//        Button btnBackToMain = findViewById(R.id.btnBackToMain);
//        btnBackToMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish(); // This will close the current activity and return to the previous one in the stack.
//            }
//        });
//    }
//
//    private List<String> retrieveNotifications() {
//
//        SharedPreferences sharedPref = getSharedPreferences("MyAppNotifications", Context.MODE_PRIVATE);
//        Map<String, ?> allEntries = sharedPref.getAll();
//        List<String> notifications = new ArrayList<>();
//        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            String notification = entry.getValue().toString();
//            notifications.add(notification);
//            // Log the retrieved notification
//            Log.d(TAG, "Retrieved notification: " + notification);
//        }
//        return notifications;
//    }
//
//}

public class NotificationDisplayActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> notificationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_notifications);

        listView = findViewById(R.id.lvNotifications);
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationList);

        // Initialize your list of notifications (empty at this point)
        List<Notification> notifications = new ArrayList<>();

        listView.setAdapter(adapter);

        // Get current FCM token and fetch notifications
        getCurrentFcmToken();

//        Button btnBackToMain = findViewById(R.id.btnBackToMain);
//        btnBackToMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish(); // This will close the current activity and return to the previous one in the stack.
//            }
//        });
    }

//    private void fetchNotifications(String token) {
//        FirebaseFirestore.getInstance().collection("userNotifications").document(token)
//                .get().addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists() && documentSnapshot.contains("notifications")) {
//                        List<Map<String, Object>> notifications = (List<Map<String, Object>>) documentSnapshot.get("notifications");
//                        if (notifications != null) {
//                            Collections.reverse(notifications);
//                            for (Map<String, Object> notif : notifications) {
//                                String title = (String) notif.get("title");
//                                String body = (String) notif.get("body");
//                                notificationList.add(title + ": " + body);
//                            }
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }).addOnFailureListener(e -> {
//                    Log.e(TAG, "Error fetching notifications", e);
//                });
//    }

    private void fetchNotifications(String token) {
        FirebaseFirestore.getInstance().collection("userNotifications").document(token)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("notifications")) {
                        List<Map<String, Object>> notificationsMapList = (List<Map<String, Object>>) documentSnapshot.get("notifications");
                        if (notificationsMapList != null) {
                            List<Notification> notifications = new ArrayList<>();
                            Collections.reverse(notificationsMapList);
                            for (Map<String, Object> notifMap : notificationsMapList) {
                                String title = (String) notifMap.get("title");
                                String body = (String) notifMap.get("body");

                                Timestamp ts = (Timestamp) notifMap.get("timestamp"); // Cast to Timestamp
                                Date notificationDate = ts.toDate(); // Convert Timestamp to Date
                                String timeSinceNotification = getTimeSince(notificationDate);

                                notifications.add(new Notification(title, body, timeSinceNotification));

                            }
                            NotificationAdapter adapter = new NotificationAdapter(NotificationDisplayActivity.this, notifications);
                            listView.setAdapter(adapter);
                        }
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Error fetching notifications", e));
    }


    private void getCurrentFcmToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }

            // Get new FCM registration token
            String token = task.getResult();

            // Log and retrieve notifications using this token
            Log.d(TAG, "FCM Token: " + token);
            fetchNotifications(token);
        });


    }

    private String getTimeSince(Date pastDate) {
        long diff = new Date().getTime() - pastDate.getTime(); // Current time - notification time

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "d";
        } else if (hours > 0) {
            return hours + "h";
        } else if (minutes > 0) {
            return minutes + "m";
        } else {
            return seconds + "s";
        }
    }

}

