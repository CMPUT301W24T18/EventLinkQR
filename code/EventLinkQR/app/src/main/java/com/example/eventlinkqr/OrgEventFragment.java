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
    Button detailsButton, attendeesButton;
    Toolbar orgEventToolBar;

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

        orgEventToolBar = (Toolbar) view.findViewById(R.id.org_event_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(orgEventToolBar);

        orgEventToolBar.setTitle(null);


        orgEventToolBar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.org_home_page));

        attendeesButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_orgEventFragment_to_attendeesPage));

        detailsButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "This function is not ready yet", Toast.LENGTH_SHORT).show());
        // Inflate the layout for this fragment
        return view;
    }
}

