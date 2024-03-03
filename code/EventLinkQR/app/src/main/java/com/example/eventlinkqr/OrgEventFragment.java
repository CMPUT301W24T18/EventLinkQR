package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class OrgEventFragment extends Fragment {
    Button backButton, detailsButton, attendeesButton;
    Toolbar orgEventToolBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_event_page, container, false);
        detailsButton = view.findViewById(R.id.details_button);
        attendeesButton = view.findViewById(R.id.attendees_button);

        orgEventToolBar = (Toolbar) view.findViewById(R.id.org_event_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(orgEventToolBar);

        orgEventToolBar.setTitle(null);


        orgEventToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.org_home_page);
            }
        });

        attendeesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_orgEventFragment_to_attendeesPage);
            }
        });

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "This function is not ready yet", Toast.LENGTH_SHORT).show();
                ;
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}

