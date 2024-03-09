package com.example.eventlinkqr;

import android.os.Bundle;

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
import java.util.List;
import java.util.Random;

/** This activity is the page where organizers can track event statistics.
 * In the current state it is very barebones and serves as a home for our map fragment.
 * */
public class OrganizerEventStats extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;

    //This is a hardcoded location set around edmonton. Later we'll pull this from firebase
    private List<LatLng> locations = generatePoints(new LatLng(53.5461, -113.4938), 30);

    /**
     * OpenAI, 2024, ChatGPT, LatLng random point generator within a given radius
     * @param center
     * @param numberOfPoints
     * @return List<LatLng> randomPoints
     */
    @NonNull
    private List<LatLng> generatePoints(LatLng center, int numberOfPoints) {
        List<LatLng> randomPoints = new ArrayList<>();
        Random random = new Random();

        double radius = 0.02;

        for (int i = 0; i < numberOfPoints; i++) {
            double randomLatOffset = radius * random.nextDouble();
            double randomLngOffset = radius * random.nextDouble();

            randomLatOffset *= random.nextBoolean() ? 1 : -1;
            randomLngOffset *= random.nextBoolean() ? 1 : -1;

            randomPoints.add(new LatLng(center.latitude + randomLatOffset, center.longitude + randomLngOffset));
        }

        return randomPoints;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_event_stats);

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
