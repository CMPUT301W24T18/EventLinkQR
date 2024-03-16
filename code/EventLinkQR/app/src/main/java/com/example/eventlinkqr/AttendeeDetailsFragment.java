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
public class AttendeeDetailsFragment extends Fragment {

    private String attendeeUuid;

    public AttendeeDetailsFragment() {
        // Required empty public constructor
    }

    public static AttendeeDetailsFragment newInstance(String attendeeUuid) {
        AttendeeDetailsFragment fragment = new AttendeeDetailsFragment();
        Bundle args = new Bundle();
        args.putString("attendeeUuid", attendeeUuid);
        fragment.setArguments(args);
        return fragment;
    }

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
