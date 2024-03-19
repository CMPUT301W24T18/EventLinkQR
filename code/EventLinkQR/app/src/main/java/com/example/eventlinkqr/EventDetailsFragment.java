package com.example.eventlinkqr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsFragment extends Fragment {

    private String eventId;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    public static EventDetailsFragment newInstance(String eventId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            fetchEventDetails(eventId, view);
        }

        Button deleteButton = view.findViewById(R.id.deleteEventButton);
        deleteButton.setOnClickListener(v -> deleteEvent(eventId, view));

        return view;
    }

    private void fetchEventDetails(String id, View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events").document(id).get().addOnSuccessListener(documentSnapshot -> {
            Event event = documentSnapshot.toObject(Event.class);

            if (event != null) {
                // Populate your views with the event data here
                TextView nameTextView = view.findViewById(R.id.eventNameTextView);
                TextView categoryTextView = view.findViewById(R.id.eventCategoryTextView);
                TextView DescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
                TextView DateTextView = view.findViewById(R.id.eventDateTimeTextView);
                TextView LocationTextView = view.findViewById(R.id.eventLocationTextView);

                handleDateTime(event,DateTextView);

                // Add more views as needed
                nameTextView.setText(event.getName());
                categoryTextView.setText(event.getCategory());
                DescriptionTextView.setText(event.getDescription());
                LocationTextView.setText(event.getLocation());
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load event.", Toast.LENGTH_SHORT).show());
    }

    private void deleteEvent(String eventId, View view) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to permanently delete this event?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Events").document(eventId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(view.getContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                if (getActivity() != null) {
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(view.getContext(), "Error deleting event", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void handleDateTime(Event event,TextView DateTextView ){

        com.google.firebase.Timestamp firebaseTimestamp = event.getDate();

        if (firebaseTimestamp != null) {
            // Convert Firebase Timestamp to java.util.Date
            Date date = firebaseTimestamp.toDate();

            // Format Date to String
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(date);

            // Now set the formatted date string to the TextView
            DateTextView.setText(formattedDate);
        }else {
            // Handle the case where the timestamp is null
            DateTextView.setText("No date available");
        }
    }
}
