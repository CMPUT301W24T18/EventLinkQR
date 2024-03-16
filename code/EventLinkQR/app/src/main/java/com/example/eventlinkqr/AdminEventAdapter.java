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

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminEventAdapter extends ArrayAdapter<Event> {

    public AdminEventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

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
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Events").document(eventToDelete.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            // Remove from the adapter's dataset and refresh
                                            remove(eventToDelete);
                                            notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure
                                            Toast.makeText(getContext(), "Failed to delete event.", Toast.LENGTH_SHORT).show();
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
