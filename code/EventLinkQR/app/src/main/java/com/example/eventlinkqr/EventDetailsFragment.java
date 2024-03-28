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

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A Fragment that displays the details of an event, including options to view and delete the event.
 * This class interacts with Firebase Firestore to retrieve and delete event data.
 */
public class EventDetailsFragment extends Fragment {

    private String eventId;

    /**
     * Default constructor required for instantiating the fragment.
     */
    public EventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of EventDetailsFragment with a specific event ID.
     *
     * @param eventId The ID of the event to display.
     * @return EventDetailsFragment An instance of EventDetailsFragment with event ID bundled.
     */
    public static EventDetailsFragment newInstance(String eventId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inflates the layout for the fragment's view and initiates the process of loading event details.
     *
     * @param inflater LayoutInflater object to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return View Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        Toolbar toolbar = view.findViewById(R.id.create_event_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            // Handle the back button action
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            fetchEventDetails(eventId, view);
        }

        Button deleteButton = view.findViewById(R.id.deleteEventButton);
        deleteButton.setOnClickListener(v -> deleteEvent(eventId, view));

        return view;
    }

    /**
     * Fetches event details from Firestore and populates the UI with these details.
     *
     * @param id The unique ID of the event.
     * @param view The view to populate with event data.
     */
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

    /**
     * Initiates the deletion process for the current event.
     *
     * @param eventId The unique ID of the event to be deleted.
     * @param view The view from which the delete action was initiated.
     */
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

    /**
     * Handles the conversion and formatting of the event date and time from Firebase Timestamp to a human-readable format.
     *
     * @param event The event object containing the Firebase Timestamp.
     * @param DateTextView The TextView where the formatted date and time should be displayed.
     */
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
