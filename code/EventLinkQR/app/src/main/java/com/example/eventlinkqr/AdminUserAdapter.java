package com.example.eventlinkqr;

import android.app.AlertDialog;
import android.content.Context;
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
 * An ArrayAdapter subclass for managing and displaying a list of attendees (users) in an admin context.
 * This adapter binds attendee data to views and provides interactive functionality such as
 * viewing attendee details and deleting attendees.
 */

public class AdminUserAdapter extends ArrayAdapter<User> {

    /**
     * Constructor for the AdminUserAdapter.
     *
     * @param context  The current context. This value cannot be null.
     * @param users The list of attendees to display. This value cannot be null.
     */
    public AdminUserAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
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
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_list_item_user, parent, false);
        }
        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.textViewUserName);
        // Populate the data into the template view using the data object
        tvName.setText(user.getName());


        ImageView deleteButton = convertView.findViewById(R.id.deleteUserButton);

        convertView.setOnClickListener(v -> {
            User selectedAttendee = getItem(position);
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, UserDetailsFragment.newInstance(selectedAttendee.getUuid()))
                    .addToBackStack(null)
                    .commit();
        });

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to permanently delete this User?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        User userToDelete = getItem(position);
                        AdminManager adminManager = new AdminManager(getContext());
                        adminManager.deleteUser(userToDelete.getUuid(), new AdminManager.AdminEventOperationCallback() {
                            @Override
                            public void onSuccess() {
                                remove(userToDelete); // Remove from the adapter's dataset
                                notifyDataSetChanged(); // Refresh the list
                                Toast.makeText(getContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(getContext(), "Failed to delete User: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
