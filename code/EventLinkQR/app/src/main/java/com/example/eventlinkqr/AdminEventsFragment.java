package com.example.eventlinkqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.fragment.app.Fragment;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

/**
 * A Fragment that represents the admin events view in the application.
 * This fragment is responsible for displaying a list of events that can be managed by an admin.
 * It uses {@link AdminEventAdapter} to bind event data to the UI.
 **/

 public class AdminEventsFragment extends Fragment {

    ListView eventsListView;
    ArrayList<Event> eventsList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

//    AdminEventAdapter<> adapter;

    AdminEventAdapter adapter;

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null.
     * This method is called between onCreate(Bundle) and onActivityCreated(Bundle).
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
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

