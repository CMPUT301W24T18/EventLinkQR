package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * initializes the value that will be displayed on the selected tab in the attendees page
 */
public class AttendeesTabFragment extends Fragment {
    private AttendeeArrayAdapter attendeesAdapter;
    private ArrayList<Attendee> dataList;
    private ListView attendeesList;
    private Event event;
    private static final String ARG_TAB_POSITION = "TAB_POSITION";

    /**
     * creates a new instance of the AttendeesTabFragment
     * @param tabPosition the tab that is currently selected
     * @return a new instance of the tha tab,
     */
    public static AttendeesTabFragment newInstance(int tabPosition) {
        AttendeesTabFragment fragment = new AttendeesTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_POSITION, tabPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Create ArrayAdapter for the ListView
        View view = inflater.inflate(R.layout.attendees_tab, container, false);
        attendeesList = view.findViewById(R.id.attendees_listview);
        // Get the tab position from arguments
        assert getArguments() != null;
        int tabPosition = getArguments().getInt(ARG_TAB_POSITION, 0);
        event = ((UserMainActivity) requireActivity()).getCurrentEvent();

        dataList = new ArrayList<>();

        // generate data from the database
        generateDataForTab(tabPosition);

        attendeesAdapter = new AttendeeArrayAdapter(requireContext(), dataList);

        // Set the adapter to the ListView
        attendeesList.setAdapter(attendeesAdapter);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        generateDataForTab(getArguments().getInt(ARG_TAB_POSITION, 0));
    }

    /**
     * generates the data of the tab depending on which one it and adds it to the dataList
     * @param tabPosition the position of the tab
     */
    private void generateDataForTab(int tabPosition) {
        Consumer<List<Attendee>> attendeeNamesCallback = attendees -> {
            dataList.clear();
            dataList.addAll(attendees);
            attendeesAdapter.notifyDataSetChanged();
        };

        // select what data is shown
        switch (tabPosition) {
            case 0:
                EventManager.addEventAttendeeSnapshotCallback(event.getId(), attendeeNamesCallback);
                break;
            case 1:
                EventManager.addEventAttendeeSnapshotCallback(event.getId(), true, attendeeNamesCallback);
                break;
            case 2:
                EventManager.addEventAttendeeSnapshotCallback(event.getId(), false, attendeeNamesCallback);
                break;
        }
    }
}
