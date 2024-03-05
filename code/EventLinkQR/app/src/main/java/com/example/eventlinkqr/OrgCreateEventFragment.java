package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * this class takes care of taking in the input for a new event and adding it to the data
 */
public class OrgCreateEventFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_create_event_page, container, false);

        // Inflate the layout for this fragment
        return view;
    }
}