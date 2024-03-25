package com.example.eventlinkqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/**
 * An activity that handles the uploading of images by users.
 * This activity presents a user interface where users can select an image from their device
 * and upload it to the app and it also gets uploaded to the Firebase Storage.
 * The activity handles the user interactions, image selection, and communicates
 * with an {@link ImageManager} instance to perform the upload operation.
 */
public class UploadImageActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private Button upload_button, cancel_button, chooseImage_button, delete_button;
    private TextView prompt;
    private Uri imageUri;


    // ActivityResultLauncher for handling gallery selection result
    private final ActivityResultLauncher<String> getImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                imageUri = uri;
                imagePreview.setImageURI(uri);
            });

    @Override
    public void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image_upload);

        imagePreview = findViewById(R.id.image_preview);
        chooseImage_button = findViewById(R.id.button_choose_image);
        upload_button = findViewById(R.id.button_confirm_upload);
        cancel_button = findViewById(R.id.button_cancel_upload);
        delete_button = findViewById(R.id.button_delete_image);
        prompt = findViewById(R.id.prompt);

        Intent intent = getIntent();
        String origin = intent.getStringExtra("origin");
        String uuid = intent.getStringExtra("uuid");
        if(origin != null && uuid != null && origin.equals("Attendee")) {
            // Call the method to generate a deterministic image
            Bitmap deterministicImage = ImageManager.generateDeterministicImage(uuid);
            imagePreview.setImageBitmap(deterministicImage);
        }


//        For Testing Purposes
//        Bitmap deterministicBitmap = ImageManager.generateDeterministicImage("Basia"); //(Attendee.getUuid);
//        imagePreview.setImageBitmap(deterministicBitmap);

        chooseImage_button.setOnClickListener(view -> getImage.launch("image/*"));

        upload_button.setOnClickListener(view -> {

            if (imageUri != null) {
                ImageManager imageManager = new ImageManager();

                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String userId = prefs.getString("UUID", null);
                String imagePath = "images/" + userId + ".jpg";

                imageManager.uploadImage(UploadImageActivity.this, imageUri, userId, imagePath, new ImageManager.UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        Toast.makeText(UploadImageActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        // Update the image preview and close the activity
                        ImageView imagePreview = findViewById(R.id.image_preview);
                        imagePreview.setImageURI(imageUri);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(UploadImageActivity.this, "Failed to upload image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(UploadImageActivity.this, "No image selected. Please select an image and try again.", Toast.LENGTH_LONG).show();
            }
        });

        cancel_button.setOnClickListener(view -> this.finish());

        delete_button.setOnClickListener(view -> {
            Bitmap deterministicImage = ImageManager.generateDeterministicImage(uuid);
            ConfirmDeleteDialogFragment confirmDeleteDialogFragment = new ConfirmDeleteDialogFragment(imagePreview, deterministicImage);
            confirmDeleteDialogFragment.show(getSupportFragmentManager(), "confirmDelete");
        });
    }
}