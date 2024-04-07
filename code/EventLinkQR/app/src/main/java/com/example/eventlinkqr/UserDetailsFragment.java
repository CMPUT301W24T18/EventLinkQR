package com.example.eventlinkqr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A Fragment subclass that displays the details of an attendee.
 * It includes functionality for fetching attendee details from Firestore
 * and deleting the attendee's data.
 *
 * Usage: This fragment should be used within an activity where details of a specific
 * attendee need to be displayed and managed.
 */
public class UserDetailsFragment extends Fragment {

    private String userUuid;

    /**
     * Default constructor. Required for the instantiation of the fragment.
     */
    public UserDetailsFragment() {
        // Required empty public constructor
    }


    /**
     * Factory method to create a new instance of this fragment using the provided UUID.
     *
     * @param userUuid UUID of the attendee whose details are to be displayed.
     * @return A new instance of fragment AttendeeDetailsFragment.
     */
    public static UserDetailsFragment newInstance(String userUuid) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        Bundle args = new Bundle();
        args.putString("userUuid", userUuid);
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

        Toolbar toolbar = view.findViewById(R.id.create_event_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            // Handle the back button action
            if (getActivity() != null) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout,new AdminUsersFragment());
                fragmentTransaction.commit();
            }
        });

        if (getArguments() != null) {
            userUuid = getArguments().getString("userUuid");
            fetchAttendeeDetails(userUuid, view);
        }

        Button deleteButton = view.findViewById(R.id.deleteAttendeeButton);
        deleteButton.setOnClickListener(v -> {
            if (getArguments() != null) {
                String userUuid = getArguments().getString("userUuid");
                deleteAttendee(userUuid, view);
            }
        });

        return view;
    }

    /**
     * Fetches the details of an user from Firestore and populates the views in the fragment.
     *
     * @param uuid The UUID of the user.
     * @param view The view of the fragment.
     */
    private void fetchAttendeeDetails(String uuid, View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uuid).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);

            if (user != null) {
                // Populate your views with the user data here
                TextView nameTextView = view.findViewById(R.id.attendeeNameTextView);
                TextView phoneNumberTextView = view.findViewById(R.id.attendeePhoneNumberTextView);
                TextView HomepageTextview = view.findViewById(R.id.attendeeHomepageTextView);

                nameTextView.setText(user.getName());
                phoneNumberTextView.setText(user.getPhone_number());
                HomepageTextview.setText(user.getHomepage());
            }
        }).addOnFailureListener(e -> {
            // Handle any errors here
        });
    }

    /**
     * Deletes the user from Firestore and displays a toast message on success or failure.
     * This method also handles the display of a confirmation dialog before deletion.
     *
     * @param userUuid The UUID of the user to be deleted.
     * @param view         The view of the fragment.
     */
    private void deleteAttendee(String userUuid, View view) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to permanently delete this User?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User confirmed deletion
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Users").document(userUuid)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(view.getContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                                    if (getActivity() != null) {
                                        FragmentManager fragmentManager = getParentFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.frame_layout,new AdminUsersFragment());
                                        fragmentTransaction.commit();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(view.getContext(), "Error deleting user", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
