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
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class AdminUsersFragment extends Fragment {

    private ListView userListView;
    private ArrayList<Attendee> usersList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdminUserAdapter adapter;

    public AdminUsersFragment() {
        // Required empty public constructor
    }

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
