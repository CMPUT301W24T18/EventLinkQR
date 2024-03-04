package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Class for handling the organizer UI and storing all needed data for the Organizer
 */
public class OrgMainActivity extends AppCompatActivity {
    MaterialButton homeButton, profileButton, scanButton;
    FragmentContainerView navController;
    FirebaseFirestore db;
    DatabaseManager databaseManager;

    private Event currentEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_main);
        homeButton = findViewById(R.id.org_home_button);
        navController = findViewById(R.id.org_nav_controller);

        databaseManager = DatabaseManager.getInstance();

        db = databaseManager.getDatabase();

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(navController).navigate(R.id.org_home_page);
            }
        });

    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }
}
