package com.example.eventlinkqr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Manages uploading, fetching, and generating images for Firebase Storage and Firestore.
 * This class provides methods to upload images to Firebase Storage, fetch images from Firebase Storage,
 * generate deterministic images based on a given input, and convert Bitmap images to byte arrays for uploading.
 */
public class ImageManager {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Uploads an image to Firebase Storage and updates the Firestore database with the image path.
     *
     * @param fileUri The URI of the file to upload.
     * @param userId The user ID to associate the uploaded image with.
     * @param imagePath The path within Firebase Storage where the image will be stored.
     */
    public void uploadImage(Uri fileUri, String userId, String imagePath) {
        // Create a storage reference
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(imagePath);

        // Upload file to Firebase Storage
        imageRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        // Save reference to Firestore
                        db.collection("users").document(userId)
                                .update("imagePath", imagePath);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle unsuccessful uploads
                    }
                });
    }

    /**
     * Fetches an image from Firebase Storage by its path.
     *
     * @param imagePath The path of the image in Firebase Storage to fetch.
     * @param onSuccessListener Listener for successful image fetch, returns the URI of the fetched image.
     * @param onFailureListener Listener for handling failures in fetching the image.
     */
    public void fetchImage(String imagePath, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(imagePath);

        imageRef.getDownloadUrl().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    /**
     * Generates a deterministic Bitmap image based on the provided input string.
     * The generated image is a simple colored square, where the color is determined by the hash code of the input.
     *
     * @param input The input string used to generate the image.
     * @return A Bitmap image generated deterministically from the input string.
     */
    public Bitmap generateDeterministicImage(String input) {
        // Create a simple bitmap based on input (e.g., hash code of user ID)
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        int colorCode = input.hashCode();
        paint.setColor(Color.rgb(colorCode & 255, (colorCode >> 8) & 255, (colorCode >> 16) & 255));
        canvas.drawRect(0F, 0F, 100F, 100F, paint);
        return bitmap;
    }

    /**
     * Converts a Bitmap image to a byte array.
     * This method is useful for preparing Bitmap images for upload to Firebase Storage.
     *
     * @param bitmap The Bitmap image to convert.
     * @return A byte array representing the Bitmap image.
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
