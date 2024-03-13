package com.example.eventlinkqr;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;
import java.util.UUID;

/**
 * Fragment for creating a profile the first time a user opens the app
 */
public class CreateProfile extends Fragment {
    private static final String TAG = "AttendeeProfile";
    // UI components: input fields, buttons, and switch
    private EditText etName, etPhoneNumber, etHomepage;
    private Switch toggleLocation; // Used for location permission
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private String uuid; // Unique identifier for the attendee\

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.create_profile, container, false);
        // Initialize UI components
        etName = view.findViewById(R.id.new_full_name);
        etPhoneNumber = view.findViewById(R.id.new_phone);
        etHomepage = view.findViewById(R.id.new_home_page);

        Button btnSave = view.findViewById(R.id.new_save_button);
        Button btnBack = view.findViewById(R.id.new_back_button);
        Button photoButton = view.findViewById(R.id.new_edit_photo);
        toggleLocation = view.findViewById(R.id.new_loc_permission);
        // Set a listener for the location switch
        toggleLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            onToggleLocationButtonClicked(isChecked);
        });

        // close the app
        btnBack.setOnClickListener(v ->{
            requireActivity().finish();
        });

        photoButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), UploadImageActivity.class);
            intent.putExtra("origin", "Attendee");
            intent.putExtra("uuid", uuid);
            startActivity(intent);
        });

        btnSave.setOnClickListener(v ->
                fetchAndUpdateFCMToken());
        return view;

    }

                /**
     * Handles the location switch toggle and first time location permissions.
     * @param isChecked The state of the switch
     */
    private void onToggleLocationButtonClicked(boolean isChecked) {
        if (isChecked) {
            if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // First time enable location tracking, make a request for permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                // The switch is on and location permission is granted
                Toast.makeText(requireActivity(), "Location tracking enabled", Toast.LENGTH_SHORT).show();
            }
        } else {
            // The switch is off
            Toast.makeText(requireActivity(), "Location tracking disabled", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Redirects to AttendeeMainActivity.
     */
    private void redirectToMainActivity() {
        Intent intent = new Intent(requireActivity(), AttendeeMainActivity.class);
        startActivity(intent);
    }

    /**
     * saves the new profile into the database
     * @param fcmToken the new fcm token
     */
    private void saveProfile(String fcmToken){
        // Extract data from UI components
        String name = etName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String homepage = etHomepage.getText().toString().trim();
        Boolean locationEnabled = toggleLocation.isChecked();


        // Validate name is not null or empty
        if (name.equals("")) {
            Toast.makeText(requireActivity(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }else if (phoneNumber.equals("") || phoneNumber.length()<10) {
            // Validate phone number if provided, and ensure it is exactly 10 digits
            Toast.makeText(requireActivity(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
        }else {
            uuid = UUID.randomUUID().toString();
            SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UUID", uuid);
            editor.apply();
            Attendee attendee = new Attendee(uuid, name, phoneNumber, homepage, fcmToken, locationEnabled);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(uuid).set(attendee)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireActivity(), "Profile Saved", Toast.LENGTH_SHORT).show();
                        redirectToMainActivity();
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireActivity(), "Error saving profile", Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Fetches the current FCM token. Upon successful retrieval, calls {@code saveProfile} to save the
     * attendee's profile data along with the token.
     */
    private void fetchAndUpdateFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();

                    // Proceed to save profile with the FCM token
                    saveProfile(token);
                });
    }

}


