package com.example.eventlinkqr;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * initializes the value that will be displayed on the selected tab in the attendees page
 */
public class AttendeesTabFragment extends Fragment {
    ArrayAdapter<String> attendeesAdapter;
    ArrayList<String> dataList;
    ListView attendeesList;
    CollectionReference eventRef;
    Event event;
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
//        View view =  super.onCreateView(inflater, container, savedInstanceState);
        // Create ArrayAdapter for the ListView
        View view = inflater.inflate(R.layout.attendees_tab, container, false);
        attendeesList = view.findViewById(R.id.attendees_listview);
        // Get the tab position from arguments
        assert getArguments() != null;
        int tabPosition = getArguments().getInt(ARG_TAB_POSITION, 0);
        event = ((OrgMainActivity) requireActivity()).getCurrentEvent();

        eventRef = ((OrgMainActivity) requireActivity()).db.collection("Events")
                .document(event.getName()).collection("attendees");

        dataList = new ArrayList<>();

        // generate data from the database
        generateDataForTab(tabPosition);

        attendeesAdapter = new ArrayAdapter<>(requireContext(), R.layout.attendees_content, dataList);

        // Set the adapter to the ListView
        attendeesList.setAdapter(attendeesAdapter);

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

        // select what data is shown
        switch (tabPosition) {
            case 0:
                eventRef.addSnapshotListener((querySnapshots, error) -> {

                    // add all attendees to the list
                    if (error != null) {
                        Log.e("Firestore", error.toString());
                        return;
                    }
                    if (querySnapshots != null) {
                        for (QueryDocumentSnapshot doc: querySnapshots) {
                            String fieldValue = doc.getString("name");
                            dataList.add(fieldValue);
                        }
                    }
                    attendeesAdapter.notifyDataSetChanged();
                });
                break;
            case 1:
                eventRef.addSnapshotListener((querySnapshots, error) -> {
                // add all attendees that have checked in
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        if(Boolean.TRUE.equals(doc.getBoolean("checkedIn"))) {
                            String fieldValue = doc.getString("name");
                            dataList.add(fieldValue);
                        }
                    }
                }
                attendeesAdapter.notifyDataSetChanged();
                });
                break;
            case 2:
                eventRef.addSnapshotListener((querySnapshots, error) -> {
                    // add all attendees that haven't checked in
                    if (error != null) {
                        Log.e("Firestore", error.toString());
                        return;
                    }
                    if (querySnapshots != null) {
                        for (QueryDocumentSnapshot doc: querySnapshots) {
                            if(Boolean.FALSE.equals(doc.getBoolean("checkedIn"))) {
                                String fieldValue = doc.getString("name");
                                dataList.add(fieldValue);
                            }
                        }
                    }
                    attendeesAdapter.notifyDataSetChanged();
                });
                break;
        }
    }
}
