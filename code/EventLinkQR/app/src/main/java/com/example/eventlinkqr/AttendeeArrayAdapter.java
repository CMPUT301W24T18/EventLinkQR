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
 * custom array adapter for an array of attendees to an event
 */
public class AttendeeArrayAdapter extends ArrayAdapter<Attendee> {


    public AttendeeArrayAdapter(@NonNull Context context, ArrayList<Attendee> attendee) {
        super(context, 0, attendee);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView == null){
            view = LayoutInflater.from (getContext()).inflate(R.layout.attendees_content, parent, false);
        }else{
            view = convertView;
        }

        Attendee attendee = getItem(position);

        TextView attendeeName = view.findViewById(R.id.attendee_name);
        TextView checkinCount = view.findViewById(R.id.attendee_checkin_count);

        assert attendee != null;

        //sets the value of the textvies
        attendeeName.setText(attendee.getName());
        checkinCount.setText("Check-in count: " + attendee.getCheckInCount());

        return view;
    }
}