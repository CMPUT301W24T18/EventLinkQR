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

public class AdminUserAdapter extends ArrayAdapter<Attendee> {

    public AdminUserAdapter(Context context, ArrayList<Attendee> Attendee) {
        super(context, 0, Attendee);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Attendee Attendee = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_list_item_user, parent, false);
        }
        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.textViewUserName);
        // Populate the data into the template view using the data object
        tvName.setText(Attendee.getName());


        ImageView deleteButton = convertView.findViewById(R.id.deleteUserButton);

        convertView.setOnClickListener(v -> {
            Attendee selectedAttendee = getItem(position);
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, AttendeeDetailsFragment.newInstance(selectedAttendee.getUuid()))
                    .addToBackStack(null)
                    .commit();
        });



        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Event")
                        .setMessage("Are you sure you want to permanently delete this User?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete
                                Attendee eventToDelete = getItem(position);
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Users").document(Attendee.getUuid())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            // Remove from the adapter's dataset and refresh
                                            remove(eventToDelete);
                                            notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure
                                            Toast.makeText(getContext(), "Failed to delete User.", Toast.LENGTH_SHORT).show();
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
