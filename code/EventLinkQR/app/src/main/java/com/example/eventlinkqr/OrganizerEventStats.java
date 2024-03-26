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

import java.util.ArrayList;

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
        String eventId = getIntent().getStringExtra("eventId");

        EventManager.getEventById(eventId, event -> {
            if (event != null) {
                this.event = event;
                locations = event.getCheckInLocations();
                if (locations != null) {
                    setupMap();
                } else {
                    Log.d("OrganizerEventStats", "Locations is null");
                }
                this.updateCheckInCount();
            } else {
                Log.d("OrganizerEventStats", "Event is null");
            }
        });
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapPreviewContainer);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mapPreviewContainer, mapFragment)
                    .commit();
        }
        mapFragment.getMapAsync(this);
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

        // Add a snapshot listener to the event's check-in locations
       EventManager.addEventLocationSnapshotCallback(event.getId(), newLocations -> {
           this.populateMap(myMap, newLocations);
        });
        this.populateMap(myMap, locations);
    }

    /**
     * Updates the count of the attendees that have checked in to the event
     */
    public void updateCheckInCount() {
        EventManager.addEventCountSnapshotCallback(event.getId(), count -> {
            int checkedInCount = count[0];
            int totalCount = count[1];
            String displayTotalAttendees = "Total Attendance\n" + checkedInCount + "/" + totalCount;
            textViewTotalAttendees.setText(displayTotalAttendees);
        });

    }

    /**
     * Populates the map with markers for each location in the list.
     * @param myMap The Google Map object to be populated.
     * @param newLocations The list of locations to be added to the map.
     */
    public void populateMap(GoogleMap myMap, ArrayList<LatLng> newLocations) {
        myMap.clear();
        if (!newLocations.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            // Loop through the list of locations and add a marker for each one
            for (LatLng location : newLocations) {
                myMap.addMarker(new MarkerOptions().position(location));
                builder.include(location);
            }

            LatLngBounds bounds = builder.build();

            //This padding helps ensure points are visually inside of the map, not just on the boarder
            int padding = 200;
            myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        } else {
            LatLng defaultEdmonton = new LatLng(53.5461, -113.4938);
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultEdmonton, 10));
        }
    }
}
