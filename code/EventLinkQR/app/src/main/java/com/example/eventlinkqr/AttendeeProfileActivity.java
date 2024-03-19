package com.example.eventlinkqr;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;
import java.util.UUID;

/**
 * Activity for managing an attendee's profile.
 */
public class AttendeeProfileActivity extends Fragment {
    private static final String TAG = "AttendeeProfile";
    // UI components: input fields, buttons, and switch
    private EditText etName, etPhoneNumber, etHomepage;
    private Switch toggleLocation; // Used for location permission
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private String uuid; // Unique identifier for the attendee
    private AttendeeArrayAdapter attendeeArrayAdapter; // Adapter for managing attendees
    private ImageView preview;
    private Bitmap deterministicBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.attendee_profile, container, false);
        // Initialize UI components
        etName = view.findViewById(R.id.etFullName);
        etPhoneNumber = view.findViewById(R.id.phoneNumberEdit);
        etHomepage = view.findViewById(R.id.homepageEdit);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button photoButton = view.findViewById(R.id.btnEditProfile);
        toggleLocation = view.findViewById(R.id.toggleLocation);
        // Set a listener for the location switch
        toggleLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            onToggleLocationButtonClicked(isChecked);
        });

        attendeeArrayAdapter = AttendeeArrayAdapter.getInstance(); // Get the singleton instance of the adapter

        checkUUIDAndLoadProfile(); // Check UUID and load profile data

        btnSave.setOnClickListener(v -> fetchAndUpdateFCMToken()); // Fetch FCM token and save profile

        deterministicBitmap = ImageManager.generateDeterministicImage(uuid);


        preview = view.findViewById(R.id.ivProfileImage);
        preview.setImageBitmap(deterministicBitmap);

        photoButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), UploadImageActivity.class);
            intent.putExtra("origin", "Attendee");
            intent.putExtra("uuid", uuid);
            startActivity(intent);
        });

        // Reset App Data button
        Button btnResetApp = view.findViewById(R.id.btnResetApp);

        // Reset app data on button click
        btnResetApp.setOnClickListener(v -> resetAppData());
        return view;
    }

    /**
     * Checks UUID and loads profile data.
     */
    private void checkUUIDAndLoadProfile() {
        uuid = ((AttendeeMainActivity) requireActivity()).getAttUUID();
        if (uuid == null) {
            SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            // New profile: generate a new UUID
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UUID", uuid);
            editor.apply();
        } else {
            // Existing profile: load it
            loadProfile(uuid);
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

    /**
     * Saves the profile data entered by the user.
     */
    private void saveProfile(String fcmToken) {
        // Extract data from UI components
        String name = etName.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String homepage = etHomepage.getText().toString();
        Boolean locationEnabled = toggleLocation.isChecked();


        Attendee attendee = new Attendee(uuid, name, phoneNumber, homepage, fcmToken, locationEnabled);

        // Validate name is not null or empty
        if (name == null || name.trim().isEmpty()) {
            Toast.makeText(requireActivity(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate phone number if provided, and ensure it is exactly 10 digits
        if (!phoneNumber.isEmpty()) {
            if (!phoneNumber.matches("\\d{10}")) {
                Toast.makeText(requireActivity(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uuid).set(attendee)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireActivity(), "Profile Saved", Toast.LENGTH_SHORT).show();
                    redirectToMainActivity();
                })
                .addOnFailureListener(e -> Toast.makeText(requireActivity(), "Error saving profile", Toast.LENGTH_SHORT).show());
    }



    /**
     * Redirects to AttendeeMainActivity.
     */
    private void redirectToMainActivity() {
        Navigation.findNavController(Objects.requireNonNull(requireActivity().getCurrentFocus())).navigate(R.id.action_attendeeProfilePage_to_attendeeHomePage);
    }

    /**
     * Loads an attendee's profile from Firestore based on the provided UUID.
     * @param uuid The unique identifier for the attendee.
     */
    private void loadProfile(String uuid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Attendee attendee = documentSnapshot.toObject(Attendee.class);

                    Log.d("uuid added", "here");
                    if (attendee != null) {
                        etName.setText(attendee.getName());
                        etPhoneNumber.setText(attendee.getPhone_number());
                        etHomepage.setText(attendee.getHomepage());
                        toggleLocation.setChecked(attendee.getLocation_enabled());
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading profile", e));
    }

    /**
     * Resets the app data, removing the UUID from SharedPreferences.
     */
    public void resetAppData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("UUID"); // Clear only the UUID
        editor.apply();

        // Also clear NotificationPrefs shared preferences
        SharedPreferences notificationPrefs = requireActivity().getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        SharedPreferences.Editor notificationEditor = notificationPrefs.edit();
        notificationEditor.clear(); // Clear all preferences related to notification
        notificationEditor.apply();

        // Redirect to LandingPage
        Intent intent = new Intent(requireActivity(), LandingPage.class);
        startActivity(intent);
    }

    /**
     * Handles the location switch toggle and first time location permissions.
     * @param isChecked The state of the switch
     */
    private void onToggleLocationButtonClicked(boolean isChecked) {
        if (isChecked) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
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
     * Handles updating the ImageView preview when an image has been uploaded or removed
     */
    public void refreshProfileImage(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("images_testing").document(uuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String base64Image = documentSnapshot.getString("base64Image");
                        if (base64Image != null && !base64Image.isEmpty()) {
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            preview.setImageBitmap(decodedByte);
                        } else {
                            // If no uploaded image is present, display the deterministic image
                            preview.setImageBitmap(deterministicBitmap);
                        }
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error displaying profile Image", Toast.LENGTH_SHORT).show();// Handle any errors
                });
//        if (imageUri != null) {
//            Glide.with(this).load(imageUri).into(preview);
//        } else {
//            // If no uploaded image is present, display the deterministic image
//            preview.setImageBitmap(deterministicBitmap);
//        }
    }

    /**
     * Is called to refresh the profile image everytime the AttendeeProfilActivity is on the foreground
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshProfileImage();
    }
}