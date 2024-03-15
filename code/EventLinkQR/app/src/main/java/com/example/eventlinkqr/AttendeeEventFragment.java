package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

/**
 * this class takes care of viewing an event's details from the attendee point of view and allows them to sign up to the event
 */
public class AttendeeEventFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.att_event_page, container, false);


        Button signUpButton = view.findViewById(R.id.sign_up_button);
        TextView eventTitle = view.findViewById(R.id.att_event_name);
        TextView eventDate = view.findViewById(R.id.att_event_datetime);
        TextView eventDescription = view.findViewById(R.id.att_event_description);
        TextView eventLocation = view.findViewById(R.id.att_event_location);
        Toolbar eventToolBar = view.findViewById(R.id.att_event_toolbar);
        ImageView eventPic = view.findViewById(R.id.att_event_picture);

        // get the event
        Event event = ((AttendeeMainActivity) requireActivity()).getCurrentEvent();

        // Set the values to be displayed
        eventTitle.setText(event.getName());
        eventLocation.setText(event.getLocation());
        eventDescription.setText(event.getDescription());
        eventDate.setText(event.getDate().toDate().toString());

        // generate a picture for the image based on the event ID
        eventPic.setImageBitmap(ImageManager.generateDeterministicImage(event.getId()));

        // make the back button return to the home page
        eventToolBar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.attendeeHomePage));

        // sign the user up for the event
        signUpButton.setOnClickListener(v -> {
            String uuid = ((AttendeeMainActivity) requireActivity()).getAttUUID();
            String profileName = ((AttendeeMainActivity) requireActivity()).getProfileName();

            EventManager.signUp(uuid, profileName, event.getId()).addOnSuccessListener(x -> {
                Toast.makeText(requireActivity(), "Signed Up", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(x -> {
                Toast.makeText(requireActivity(), "Failed to sign up", Toast.LENGTH_SHORT).show();
            });
        });

        return view;
    }
}
