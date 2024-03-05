package com.example.eventlinkqr;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;


public class NotificationDisplayActivity extends AppCompatActivity {


    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> notificationList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_notifications);


        listView = findViewById(R.id.lvNotifications);
        notificationList = new ArrayList<>();


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationList);
        listView.setAdapter(adapter);


        // Listen for changes in the Firestore database
        FirebaseFirestore.getInstance().collection("notifications_testing")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            System.err.println("Listen failed: " + e);
                            return;
                        }


                        notificationList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String title = doc.getString("heading"); // Assuming the document has a 'title' field
                            String message = doc.getString("description"); // Assuming the document has a 'message' field
                            notificationList.add(title + ": " + message);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });


        Button btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will close the current activity and return to the previous one in the stack.
            }
        });
    }
}
