package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class EventsTabFragment extends Fragment {
    private EventArrayAdapter eventAdapter;
    private ArrayList<Event> dataList;
    private ListView eventsList;
    private Button createEventButton;
    private static final String ARG_TAB_POSITION = "TAB_POSITION";

    /**
     * creates a new instance of the EventsTabFragment
     * @param tabPosition the tab that is currently selected
     * @return a new instance of the the tab,
     */
    public static EventsTabFragment newInstance(int tabPosition) {
        EventsTabFragment fragment = new EventsTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_POSITION, tabPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Create ArrayAdapter for the ListView
        View view = inflater.inflate(R.layout.events_tab, container, false);
        eventsList = view.findViewById(R.id.event_list_view);
        createEventButton = view.findViewById(R.id.create_event_button);

        // Get the tab position from arguments
        assert getArguments() != null;
        int tabPosition = getArguments().getInt(ARG_TAB_POSITION, 0);

        dataList = new ArrayList<>();

        // generate data from the database
        generateDataForTab(tabPosition);
        eventAdapter = new EventArrayAdapter(view.getContext(), dataList);

        // Set the adapter to the ListView
        eventsList.setAdapter(eventAdapter);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_attendeeHomePage_to_orgCreateEventFragment);
            }
        });

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    /**
     * generates the data of the tab depending on which one it and adds it to the dataList
     * @param tabPosition the position of the tab
     */
    private void generateDataForTab(int tabPosition) {
        Consumer<List<Event>> eventsCallback = events -> {
            dataList.clear();
            dataList.addAll(events);
            eventAdapter.notifyDataSetChanged();
        };

        // select what data is shown
        switch (tabPosition) {
            case 0:
                createEventButton.setVisibility(View.GONE);
                EventManager.addAllEventSnapshotCallback(eventsCallback);
                break;
            case 1:
                createEventButton.setVisibility(View.VISIBLE);
                eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ((AttendeeMainActivity) requireActivity()).setCurrentEvent(dataList.get(position));
                        Navigation.findNavController(view).navigate(R.id.action_attendeeHomePage_to_orgEventFragment2);
                    }
                });
                EventManager.addEventSnapshotCallback(((AttendeeMainActivity) requireActivity()).getAttUUID(), eventsCallback);
                break;
            case 2:
                createEventButton.setVisibility(View.GONE);
                break;
        }
    }
}