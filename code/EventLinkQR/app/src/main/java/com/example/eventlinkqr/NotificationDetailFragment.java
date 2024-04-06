package com.example.eventlinkqr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

/**
 * A Fragment subclass for displaying detailed information about a notification.
 * This includes the notification title, message, and an event name and it's poster.
 * Users can navigate from this detail view back to their respective
 * notification lists.
 */
public class NotificationDetailFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notification_detail, container, false);

        // Extract arguments passed to the fragment
        Bundle arguments = getArguments();
        String title = arguments.getString("title", "Default Title");
        String message = arguments.getString("message", "Default Message");
        String eventId;

        // Set up the toolbar
        Toolbar orgEventToolBar = view.findViewById(R.id.org_event_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(orgEventToolBar);
        orgEventToolBar.setTitle(null);

        // Initialize image and text views
        ImageView eventPic = view.findViewById(R.id.imageView);
        TextView header = view.findViewById(R.id.tvNotificationsHeader);

        // Check the source of the notification and adjust UI accordingly
        if ("organizer".equals(arguments.getString("source"))) {
            Event event = ((AttendeeMainActivity) requireActivity()).getCurrentEvent();
            String eventName = event.getName(); // Define eventName from event object
            header.setText(eventName);
            eventId = event.getId();
        } else{
            String eventName = arguments.getString("eventName", "Event");
            header.setText(eventName);
            eventId = arguments.getString("eventId", "Default Message");
        }

        // Fetch and set the event poster image
        ImageManager.getPoster(eventId, posterBitmap -> {
            if(posterBitmap != null) {
                float scale;
                if (posterBitmap.getWidth() >= posterBitmap.getHeight()) {
                    scale = (float) eventPic.getWidth() / posterBitmap.getWidth();
                } else {
                    scale = (float) eventPic.getHeight() / posterBitmap.getHeight();
                }
                Bitmap scaleImage = Bitmap
                        .createScaledBitmap(posterBitmap, (int) (posterBitmap.getWidth() *scale), (int) (posterBitmap.getHeight() *scale), true);
                eventPic.setImageBitmap(scaleImage);
            }
        });

        // Navigation click listener
        orgEventToolBar.setNavigationOnClickListener(v -> {
            if ("organizer".equals(arguments.getString("source"))) {
                Navigation.findNavController(view).navigate(R.id.action_detailedNotification_to_organizerNotification);
            } else {
                Navigation.findNavController(view).navigate(R.id.action_detailedNotification_to_userNotification);
            }
        });

        // Set the title and message to their respective TextViews
        TextView tvTitle = view.findViewById(R.id.tvFullNotificationTitle);
        tvTitle.setText(title);
        TextView tvMessage = view.findViewById(R.id.tvFullNotificationMessage);
        tvMessage.setText(message);
        return view;

    }
}
