package com.example.eventlinkqr;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.navigation.Navigation;

import java.util.ArrayList;

public class OrgHomePage extends Fragment {

    private ListView eventList;
    private EventArrayAdapter eventsAdapter;
    private ArrayList<Event> dataList;
    private Button createEventButton;

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

        dataList = new ArrayList<>();
        eventsAdapter = new EventArrayAdapter(view.getContext(), dataList);

        eventList.setAdapter(eventsAdapter);


        // switch to the event page
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((OrgMainActivity) requireActivity()).setCurrentEvent(dataList.get(position));
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

        //add the data from the database into the events listview
        EventManager.addEventSnapshotCallback(((OrgMainActivity) requireActivity()).getOrganizerName(), events -> {
            dataList.clear();
            dataList.addAll(events);
            eventsAdapter.notifyDataSetChanged();
        });

        // Inflate the layout for this fragment
        return view;
    }
}