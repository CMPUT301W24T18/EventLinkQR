package com.example.eventlinkqr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

/**
 * A Fragment subclass that displays the details of an attendee.
 * It includes functionality for fetching attendee details from Firestore
 * and deleting the attendee's data.
 *
 * Usage: This fragment should be used within an activity where details of a specific
 * attendee need to be displayed and managed.
 */
public class AttendeeDetailsFragment extends Fragment {

    private String attendeeUuid;

    /**
     * Default constructor. Required for the instantiation of the fragment.
     */
    public AttendeeDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the provided UUID.
     *
     * @param attendeeUuid UUID of the attendee whose details are to be displayed.
     * @return A new instance of fragment AttendeeDetailsFragment.
     */
    public static AttendeeDetailsFragment newInstance(String attendeeUuid) {
        AttendeeDetailsFragment fragment = new AttendeeDetailsFragment();
        Bundle args = new Bundle();
        args.putString("attendeeUuid", attendeeUuid);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendee_details, container, false);

        if (getArguments() != null) {
            attendeeUuid = getArguments().getString("attendeeUuid");
            fetchAttendeeDetails(attendeeUuid, view);
        }

        Button deleteButton = view.findViewById(R.id.deleteAttendeeButton);
        deleteButton.setOnClickListener(v -> {
            if (getArguments() != null) {
                String attendeeUuid = getArguments().getString("attendeeUuid");
                deleteAttendee(attendeeUuid, view);
            }
        });

        return view;
    }

    /**
     * Fetches the details of an attendee from Firestore and populates the views in the fragment.
     *
     * @param uuid The UUID of the attendee.
     * @param view The view of the fragment.
     */
    private void fetchAttendeeDetails(String uuid, View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uuid).get().addOnSuccessListener(documentSnapshot -> {
            Attendee attendee = documentSnapshot.toObject(Attendee.class);

            if (attendee != null) {
                // Populate your views with the attendee data here
                TextView nameTextView = view.findViewById(R.id.attendeeNameTextView);
                TextView phoneNumberTextView = view.findViewById(R.id.attendeePhoneNumberTextView);
                TextView HomepageTextview = view.findViewById(R.id.attendeeHomepageTextView);

                nameTextView.setText(attendee.getName());
                phoneNumberTextView.setText(attendee.getPhone_number());
                HomepageTextview.setText(attendee.getHomepage());
            }
        }).addOnFailureListener(e -> {
            // Handle any errors here
        });
    }

    /**
     * Deletes the attendee from Firestore and displays a toast message on success or failure.
     * This method also handles the display of a confirmation dialog before deletion.
     *
     * @param attendeeUuid The UUID of the attendee to be deleted.
     * @param view         The view of the fragment.
     */
    private void deleteAttendee(String attendeeUuid, View view) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to permanently delete this User?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User confirmed deletion
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Users").document(attendeeUuid)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(view.getContext(), "Attendee deleted successfully", Toast.LENGTH_SHORT).show();
                                    if (getActivity() != null) {
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(view.getContext(), "Error deleting attendee", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
