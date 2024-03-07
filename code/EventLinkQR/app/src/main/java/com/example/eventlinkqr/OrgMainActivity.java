package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;

/**
 * Class for handling the organizer UI and storing all needed data for the Organizer
 */
public class OrgMainActivity extends AppCompatActivity {
    private MaterialButton homeButton, profileButton, scanButton;
    private FragmentContainerView navController;
    private Event currentEvent;
    private String organizerName;

    private QRCodeScanner scanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_main);
        homeButton = findViewById(R.id.org_home_button);
        navController = findViewById(R.id.org_nav_controller);
        scanner = new QRCodeScanner(this);
        homeButton.setOnClickListener(v -> {
            Navigation.findNavController(navController).navigate(R.id.org_home_page);
        });

    }

    /**
     * gets the current event on display
     * @return the current event
     */
    public Event getCurrentEvent() {
        return currentEvent;
    }

    /**
     * sets the current event
     * @param currentEvent the new current event
     */
    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    /**
     * sets the name of the organizer
     * @param organizerName the name of the organizer
     */
    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    /**
     * gets the name of the organizer
     * @return the name of the organizer
     */
    public String getOrganizerName() {
        return organizerName;
    }

    /**
     * makes the buttons at the bottom of the activity visible
     * Used when the organizer successfully logs into the app
     */
    public void setButtonsVisible(){
        findViewById(R.id.org_main_buttons).setVisibility(View.VISIBLE);
    }

    /**
     * Get the qrCode scanner
     * @return The QRCode scanner
     */
    public QRCodeScanner getScanner() {
        return scanner;
    }
}
