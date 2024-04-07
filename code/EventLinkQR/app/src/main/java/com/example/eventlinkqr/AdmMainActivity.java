package com.example.eventlinkqr;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventlinkqr.databinding.AdmMainActivityBinding;

/**
 * Main activity for the Admin section of the EventLink QR application.
 * This activity is responsible for handling the navigation between different fragments
 * (such as AdminEventsFragment, AdminUsersFragment, AdminImagesFragment) and switching to
 * the AttendeeMainActivity.
 *
 * The activity uses a bottom navigation view to facilitate the navigation between different
 * sections of the admin interface.
 */
public class AdmMainActivity extends AppCompatActivity {

    AdmMainActivityBinding binding;

    /**
     * Initializes the activity. This method sets up the UI and fragment navigation.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the most recent data,
     *                           or null if there is no data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = AdmMainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new AdminEventsFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.users) {
                replaceFragment(new AdminUsersFragment());
            } else if (id == R.id.events) {
                replaceFragment(new AdminEventsFragment());
            } else if (id == R.id.profile) {
                new AlertDialog.Builder(this)
                        .setTitle("Exit Admin Mode")
                        .setMessage("Are you sure you would like to exit the Admin mode?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Positive button logic
                            Intent intent = new Intent(this, UserMainActivity.class);
                            startActivity(intent);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Negative button logic (do nothing)
                        })
                        .show();
            } else if (id == R.id.images) {
                replaceFragment(new AdminImagesFragment());
            }
            return true;
        });

    }

    /**
     * Replaces the current fragment in the 'frame_layout' with the specified fragment.
     * This method is used to switch between the different admin sections.
     *
     * @param fragment The new fragment to display.
     */
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();

    }

}
