package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


/**
 * takes care of the event page on the organizer activity
 */
public class OrgEventFragment extends Fragment {
    /** All buttons and the toolbar that will be used on this page*/
    private Button detailsButton, attendeesButton;
    private Toolbar orgEventToolBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_event_page, container, false);
        detailsButton = view.findViewById(R.id.details_button);
        attendeesButton = view.findViewById(R.id.attendees_button);

        orgEventToolBar = view.findViewById(R.id.org_event_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(orgEventToolBar);
        orgEventToolBar.setTitle(null);

        // make the back button return to the home page
        orgEventToolBar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.org_home_page));

        // make the attendees button go to the attendees page
        attendeesButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_orgEventFragment_to_attendeesPage));

        // temporary message since it is not yet completely implemented
        detailsButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "This function is not ready yet", Toast.LENGTH_SHORT).show());

        // Inflate the layout for this fragment
        return view;
    }
}
