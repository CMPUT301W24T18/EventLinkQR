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

/**
 * custom adapter to display the desired info about the event on screen
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;
    public EventArrayAdapter(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertview, @NonNull ViewGroup parent){

        View view = convertview;

        if(convertview == null){
            view = LayoutInflater.from(context).inflate(R.layout.org_events_content, parent, false);
        }

        Event event = events.get(position);

        //Map all the TextViews
        TextView eventName = view.findViewById(R.id.event_title);
        TextView eventDate = view.findViewById(R.id.event_description);

        assert event != null;

        //Set the value of all the TextViews
        // this is a basic implementation that will be updated later on
        eventName.setText(event.getName());
        eventDate.setText(event.getDescription());
        return view;
    }
}
