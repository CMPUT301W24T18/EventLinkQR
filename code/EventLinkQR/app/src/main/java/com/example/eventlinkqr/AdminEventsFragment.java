package com.example.eventlinkqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.fragment.app.Fragment;

import android.view.ViewGroup;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class AdminEventsFragment extends Fragment {

    ListView eventsListView;
    ArrayList<Event> eventsList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

//    AdminEventAdapter<> adapter;

    AdminEventAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_admin_events, container, false);

        eventsListView = view.findViewById(R.id.eventsListView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

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

        adapter = new AdminEventAdapter(getContext(), eventsList);
        eventsListView.setAdapter(adapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false); // This will stop the refresh animation
            }
        });
        return view;
    }
}

