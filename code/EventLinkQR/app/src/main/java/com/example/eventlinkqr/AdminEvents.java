package com.example.eventlinkqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class AdminEvents extends AppCompatActivity {

    ListView eventsListView;
    ArrayList<Event> eventsList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

//    AdminEventAdapter<> adapter;

    AdminEventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_events);

        eventsListView = findViewById(R.id.eventsListView);

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    event.setId(document.getId());
                    eventsList.add(event);
                }
                adapter.notifyDataSetChanged();
            } else {
                // Handle the error
            }
        });

        adapter = new AdminEventAdapter(this, eventsList);
        eventsListView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false); // This will stop the refresh animation
            }
        });

//        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Event selectedEvent = eventsList.get(position);
//                // Extract the Firestore document ID from the selected event
//                String eventId = selectedEvent.getId();
//
//                // Create an Intent to start OrgMainActivity and pass the eventId
//                Intent intent = new Intent(AdminEvents.this, OrgMainActivity.class);
//                intent.putExtra("eventId", eventId); // Pass the eventId
//                startActivity(intent);
//            }
//        });
    }
}

