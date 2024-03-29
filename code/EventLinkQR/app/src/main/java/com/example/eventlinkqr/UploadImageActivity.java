package com.example.eventlinkqr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

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
    private Uri imageUri;
    String userUuid;
    Bitmap deterministicImage;
    ImageManager imageManager;


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
        imagePreview = findViewById(R.id.image_preview);

        Intent intent = getIntent();
        String origin = intent.getStringExtra("origin");
        userUuid = intent.getStringExtra("uuid"); // to get the uuid of the user

        if(origin != null && userUuid != null && origin.equals("Attendee")) {
            refreshImageView();
            // Call the method to generate a deterministic image
            deterministicImage = ImageManager.generateDeterministicImage(userUuid);
            imagePreview.setImageBitmap(deterministicImage);
        }else{
            // find a way to display the image that's in the database
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Images").document(userUuid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists() && documentSnapshot.contains("base64Image")) {
                            String base64Image = documentSnapshot.getString("base64Image");
                            ImageManager.displayBase64Image(base64Image, imagePreview); // Static method called directly with class name
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(UploadImageActivity.this, "Failed to fetch image: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        }

        chooseImage_button.setOnClickListener(view -> getImage.launch("image/*"));

        upload_button.setOnClickListener(view -> {
            if (imageUri != null) {
                imageManager = new ImageManager();

                // https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
                Bitmap image;
                try {
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                imageManager.uploadImage(UploadImageActivity.this,  userUuid, image, new ImageManager.UploadCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(UploadImageActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        // Update the image preview and close the activity
                        ImageView imagePreview = findViewById(R.id.image_preview);
                        imagePreview.setImageURI(imageUri);

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("imageUri", imageUri.toString());
                        setResult(Activity.RESULT_OK, returnIntent);
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
            Bitmap deterministicImage = ImageManager.generateDeterministicImage(userUuid);
            ConfirmDeleteDialogFragment confirmDeleteDialogFragment = new ConfirmDeleteDialogFragment(imagePreview, deterministicImage, userUuid);
            confirmDeleteDialogFragment.show(getSupportFragmentManager(), "confirmDelete");
        });
    }

    /**
     * To refresh the image preview in the uploadImageActivity so that the user can see what image is being uploaded or deleted
     */
    public void refreshImageView() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Images").document(userUuid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("base64Image")) {
                        String base64Image = documentSnapshot.getString("base64Image");
                        if (base64Image != null && !base64Image.isEmpty()) {
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imagePreview.setImageBitmap(decodedByte);
                        } else {
                            // Handle the case where there is no image
                            imagePreview.setImageBitmap(deterministicImage);
                        }
                    } else {
                        // Document doesn't exist or doesn't have the image; handle accordingly
                        imagePreview.setImageBitmap(deterministicImage);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any failure in fetching the document
                    Toast.makeText(UploadImageActivity.this, "Failed to refresh image view: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    imagePreview.setImageBitmap(null); // or set a default image upon failure
                });
    }
}