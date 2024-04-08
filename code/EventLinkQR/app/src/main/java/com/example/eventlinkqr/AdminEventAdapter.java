package com.example.eventlinkqr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Adapter for managing and displaying a list of events in an admin context.
 * This adapter is used to bind event data to views and provide interactive functionality
 * like viewing event details and deleting events.
 *
 **/
public class AdminEventAdapter extends ArrayAdapter<Event> {

    /**
     * Constructor for the AdminEventAdapter.
     *
     * @param context The current context. This value cannot be null.
     * @param events  The list of events to display. This value cannot be null.
     */
    public AdminEventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the data set of the data item.
     * @param convertView The old view to reuse, if possible. This value may be null.
     * @param parent      The parent view that this view will eventually be attached to.
     *                    This value cannot be null.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event event = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_list_item_event, parent, false);
        }
        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.textViewEventName);
        // Populate the data into the template view using the data object
        tvName.setText(event.getName());

        convertView.setOnClickListener(v -> {
            Event selectedEvent = getItem(position);
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, EventDetailsFragment.newInstance(selectedEvent.getId()))
                    .addToBackStack(null)
                    .commit();
        });

        ImageView deleteButton = convertView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Event")
                        .setMessage("Are you sure you want to permanently delete this event?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete
                                Event eventToDelete = getItem(position);
                                AdminManager adminManager = new AdminManager(getContext());
                                adminManager.deleteEvent(eventToDelete.getId(), new AdminManager.AdminEventOperationCallback() {
                                    @Override
                                    public void onSuccess() {
                                        // Remove from the adapter's dataset and refresh
                                        remove(eventToDelete);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Toast.makeText(getContext(), "Failed to delete event: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });


        // Return the completed view to render on screen
        return convertView;
    }
}
