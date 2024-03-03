package com.example.eventlinkqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<Event> {
    public EventArrayAdapter(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertview, @NonNull ViewGroup parent){
        View view;
        if(convertview == null){
            view = LayoutInflater.from (getContext()).inflate(R.layout.org_events_content, parent, false);
        }else{
            view = convertview;
        }

        Event event = getItem(position);

        //Map all the TextViews
        TextView eventName = view.findViewById(R.id.event_title);
        TextView eventDate = view.findViewById(R.id.event_date);

        assert event != null;

        //Set the value of all the TextViews
        eventName.setText(event.getName());
        eventDate.setText(event.getDate());
        return view;
    }
}
