package com.example.eventlinkqr;

import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment used for managing users (attendees) in an admin context within an Android application.
 * This fragment allows admins to view a list of users and provides functionality for refreshing
 * the user list.
 *
 * The class interacts with Firestore to fetch users and uses a custom adapter
 * to bind user data to a ListView.
 */

public class AdminUsersFragment extends Fragment {

    private ListView userListView;
    private ArrayList<Attendee> usersList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdminUserAdapter adapter;

    /**
     * Default constructor. Required for instantiation of the fragment.
     */
    public AdminUsersFragment() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);

        userListView = view.findViewById(R.id.userListView); // Ensure the ID matches in your layout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        setupFirebaseAndAdapter(view);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh your list here
            swipeRefreshLayout.setRefreshing(false); // This stops the refresh animation
        });

        return view;
    }

    /**
     * Sets up Firestore database interaction and initializes the ListView adapter.
     * This method fetches user data from Firestore and populates the ListView via the adapter.
     *
     * @param view The current view of the fragment.
     */
    private void setupFirebaseAndAdapter(View view) {
        // Initialize Firestore and Adapter here
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Attendee attendee = document.toObject(Attendee.class);
                    usersList.add(attendee);
                }
                adapter.notifyDataSetChanged();
            } else {
                // Handle the error
            }
        });

        adapter = new AdminUserAdapter(getContext(), usersList);
        userListView.setAdapter(adapter);
    }
}
