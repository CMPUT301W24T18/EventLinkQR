package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class serves to display the list of attendees that signed up for the current event, but have not yet checked in
 */
public class NotCheckedFragment extends Fragment {
    ArrayAdapter<String> attendeesAdapter;
    ArrayList<String> dataList;
    ListView attendeesList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.all_attendees, container, false);
        attendeesList = (ListView) view.findViewById(R.id.all_attendees_list_view);
        String [] attendees = {"unchecked attendees", "unchecked attendees", "unchecked attendees", "unchecked attendees", "unchecked attendees", "unchecked attendees",
                "unchecked attendees", "unchecked attendees", "unchecked attendees", "unchecked attendees"};

        dataList = new ArrayList<>();
        dataList.addAll(Arrays.asList(attendees));
        attendeesAdapter = new ArrayAdapter<>( view.getContext(), R.layout.attendees_list_content, dataList);

        attendeesList.setAdapter(attendeesAdapter);

        return view;
    }
}
