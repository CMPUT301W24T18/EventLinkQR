package com.example.eventlinkqr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.storage.FirebaseStorage;

/**
 * Manages uploading, fetching, and generating images for Firebase Storage and Firestore.
 * This class provides methods to upload images to Firebase Storage, fetch images from Firebase Storage,
 * generate deterministic images based on a given input, and convert Bitmap images to byte arrays for uploading.
 */
public class ImageManager {

    private final FirebaseStorage storage;
    private final FirebaseFirestore db;
    private ImageView preview;
    static String uuid;

    /**
     * ImageManager constructor that instantiates the Firebase Storage and Firestore instances
     */
    public ImageManager(){
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * A callback interface that responds to completing an image upload operation.
     */
    public interface UploadCallback {
        /**
         * Called when the image has been successfully uploaded to Firebase Storage.
         */
        void onSuccess();
        /**
         * Called when the image upload operation fails.
         *
         * @param exception captures the error encountered during the upload operation.
         */
        void onFailure(Exception exception);
    }

    /**
     * Uploads an image to Firebase Storage that is linked to the uuid that uploaded it and updates the Firestore database with the image path as Base64.
     *
     * @param context The context that the function is being called in.
     * @param uuid The user ID to associate the uploaded image with.
     * @param image The path within Firebase Storage where the image will be stored.
     * @param callback The callback interface that handles the outcome of the upload operation.
     */
    public void uploadImage(Context context, String uuid, Bitmap image, UploadCallback callback) {
        String base64Encoded = Base64.encodeToString(ImageManager.bitmapToByteArray(image), Base64.DEFAULT);
        Map<String, String> imageMap = new HashMap<>();
        imageMap.put("base64Image", base64Encoded);

        db.collection("images_testing").document(uuid).set(imageMap) // changed add to set
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }

    /**
     * Deletes the image from the firebase storage using the uuid that it is linked to
     *
     * @param context The context that the function is being called in.
     * @param uuid The user ID to associate the uploaded image with.
     */
    static void deleteImageFromFirebase(ConfirmDeleteDialogFragment context, String uuid) {
        // reference to image file in Firebase Storage
        DocumentReference imageRef = Manager.getFirebase().collection("images_testing")
                .document(uuid);

        imageRef.delete().addOnSuccessListener(aVoid -> {
            // Image deleted successfully
            Toast.makeText(context.getContext(), "Image deleted successfully.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            // Image deleted unsuccessfully
            Toast.makeText(context.getContext(), "Deletion failed.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Handles updating the ImageView preview when an image has been uploaded or removed
     */
    public static void refreshProfileImage(Context context, ImageView preview){
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
                            preview.setImageBitmap(ImageManager.generateDeterministicImage(uuid));
                        }
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Error displaying profile Image", Toast.LENGTH_SHORT).show();// Handle any errors
                });
    }

    /**
     * Generates a deterministic Bitmap image (a smiley face) based on the provided input string.
     * The generated image is a simple colored square, where the color is determined by the hash code of the input.
     *
     * @param input The input string used to generate the image.
     * @return A Bitmap image generated deterministically from the input string.
     */
    public static Bitmap generateDeterministicImage(String input) {

        Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Determine colors based on the userid input hash code
        int colorCode = input.hashCode();
        int backgroundColor = Color.rgb(colorCode & 255, (colorCode >> 8) & 255, (colorCode >> 16) & 255);
        int featureColor = Color.rgb(~(colorCode & 255), ~(colorCode >> 8) & 255, ~(colorCode >> 16) & 255);

        // Background
        paint.setColor(backgroundColor);
        canvas.drawRect(0F, 0F, 100F, 100F, paint);

        // Face
        paint.setColor(featureColor);
        canvas.drawCircle(50F, 50F, 30F, paint);

        // Eyes
        paint.setColor(Color.BLACK);
        canvas.drawCircle(35F, 40F, 5F, paint);
        canvas.drawCircle(65F, 40F, 5F, paint);

        // Mouth
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2F);
        canvas.drawArc(35F, 50F, 65F, 70F, 0F, 180F, false, paint);

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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        return baos.toByteArray();
    }

    /**
     * Decodes the base64Image back t a bitmap and displays it to the specified imageView
     *
     * @param base64Image is retrieved from the database as the image that the user uploaded as either profile or poster
     * @param imageView to display the decoded base64image
     */
    public static void displayBase64Image(String base64Image, ImageView imageView) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(decodedByte);
    }
}
