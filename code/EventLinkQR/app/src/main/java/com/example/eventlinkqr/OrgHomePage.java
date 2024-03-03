package com.example.eventlinkqr;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Arrays;

public class OrgHomePage extends Fragment {

    ListView eventList;
    EventArrayAdapter eventsAdapter;
    ArrayList<Event> dataList;
    Button createEventButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.org_home_page, container, false);
        eventList = view.findViewById(R.id.event_list_view);
        createEventButton= view.findViewById(R.id.create_event_button);
        String [] events = {"event1", "event2", "event3", "event4", "event5"};

        dataList = new ArrayList<>();
        //dataList.addAll(Arrays.asList(events));
        eventsAdapter = new EventArrayAdapter(view.getContext(), dataList);

        eventList.setAdapter(eventsAdapter);


        // switch to the event page
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Navigation.findNavController(view).navigate(R.id.action_home_to_orgEventFragment);
            }
        });

        // switch to the create event page
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_home_page_to_createEventFragment);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}