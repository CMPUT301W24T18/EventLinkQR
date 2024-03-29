package com.example.eventlinkqr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

/**
 * Manages uploading, fetching, and generating images for Firebase Storage and Firestore.
 * This class provides methods to upload images to Firebase Storage, fetch images from Firebase Storage,
 * generate deterministic images based on a given input, and convert Bitmap images to byte arrays for uploading.
 */
public class ImageManager {

    private final FirebaseStorage storage;
    private final FirebaseFirestore db;

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
        canvas.drawRect(0F, 0F, 500F, 500F, paint);

        // Face
        paint.setColor(featureColor);
        canvas.drawCircle(250F, 250F, 200F, paint);

        // Eyes
        paint.setColor(Color.BLACK);
        canvas.drawCircle(160F, 200F, 25F, paint);
        canvas.drawCircle(340F, 200F, 25F, paint);

        // Mouth
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10F);
        canvas.drawArc(160F, 300F, 340F, 400F, 0F, 180F, false, paint);

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
}
