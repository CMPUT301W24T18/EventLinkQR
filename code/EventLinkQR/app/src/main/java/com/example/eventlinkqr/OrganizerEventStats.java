package com.example.eventlinkqr;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** This activity is the page where organizers can track event statistics.
 * In the current state it is very barebones and serves as a home for our map fragment.
 * */
public class OrganizerEventStats extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private Event event;
    private ArrayList<LatLng> locations;
    private TextView textViewTotalAttendees;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_event_stats);

        // Initialize the text view for the total number of attendees
        textViewTotalAttendees = findViewById(R.id.textViewTotalAttendance);

        // Get the eventID from the intent
        //String eventId = getIntent().getStringExtra("eventId");
        String eventId = "event1";
        EventManager.getEventById(eventId, event -> {
            if (event != null) {
                this.event = event;
                Log.d("OrganizerEventStats", event.getName());
            } else {
                Log.d("OrganizerEventStats", "Event is null");
            }
        });



        if (eventId != null) {
            EventManager.getEventById(eventId, event -> {
                if (event != null) {
                    this.event = event; // Now you have your event object populated from Firestore
                    // Proceed with other operations that depend on the event object
                    // ...
                    Log.d("OrganizerEventStats", event.getName());
                } else {
                    // Handle the case where the event is null (not found or error)
                    // ...
                    Log.d("OrganizerEventStats", "Event is null");
                }
            });
        }

        if (event != null && event.getGeoTracking()) {

            locations = event.getCheckInLocations();
            //Set up the map fragment
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapPreviewContainer);
            if (mapFragment == null) {
                //If not found, we'll create it for now and add it to the FrameLayout
                mapFragment = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.mapPreviewContainer, mapFragment)
                        .commit();
            }
            mapFragment.getMapAsync(this);
        }

        // Set the total number of attendees
        if (this.event == null) {
            Log.d("OrganizerEventStats", "Event is null");
        }
        assert this.event != null;
        int totalAttendees = event.getTotalAttendees();
        String displayTotalAttendees = "Total Attendance\n" + totalAttendees;
        textViewTotalAttendees.setText(displayTotalAttendees);
    }

    /**
     * Configures the Google Map camera bounds based on a list of locations.
     * This implementation is adapted from the Google Maps Android API documentation.
     * Source: https://developers.google.com/maps/documentation/android-sdk/start
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        // Since this is just a preview and we'll likely be using emulators a lot let's add controls
        myMap.getUiSettings().setZoomControlsEnabled(true);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // Loop through the list of locations and add a marker for each one
        for (LatLng location : locations) {
            myMap.addMarker(new MarkerOptions().position(location));
            builder.include(location);
        }

        LatLngBounds bounds = builder.build();

        //This padding helps ensure points are visually inside of the map, not just on the boarder
        int padding = 200;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapPreviewContainer);
        if (mapFragment != null && mapFragment.getView() != null) {
            mapFragment.getView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                // Now that the layout has occurred, move the camera
                if (myMap != null) {
                    myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                }
            });
        }
    }

}
