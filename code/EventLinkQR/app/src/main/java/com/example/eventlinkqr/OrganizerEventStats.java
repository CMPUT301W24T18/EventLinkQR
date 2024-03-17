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
        String eventId = getIntent().getStringExtra("eventId");
        Log.d("OrganizerEventStats", eventId);

        EventManager.getEventById(eventId, event -> {
            if (event != null) {
                this.event = event;
                locations = event.getCheckInLocations();
                if (locations != null) {
                    runOnUiThread(() -> setupMap());
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

        if (!locations.isEmpty()) {
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
        } else {
            LatLng defaultEdmonton = new LatLng(53.5461, -113.4938);
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultEdmonton, 10));
        }

    }

    public void updateCheckInCount() {
        EventManager.addEventCountSnapshotCallback(event.getId(), count -> {
            int checkedInCount = count[0];
            int totalCount = count[1];
            String displayTotalAttendees = "Total Attendance\n" + checkedInCount + "/" + totalCount;
            textViewTotalAttendees.setText(displayTotalAttendees);
        });

    }

}
