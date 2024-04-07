package com.example.eventlinkqr;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import java.util.function.Consumer;

/**
 * Manages uploading, fetching, and generating images for Firebase Storage and Firestore.
 * This class provides methods to upload images to Firebase Storage, fetch images from Firebase Storage,
 * generate deterministic images based on a given input, and convert Bitmap images to byte arrays for uploading.
 */
public class ImageManager extends Manager {
    private final FirebaseFirestore db;

    /**
     * The Firestore collection path for images
     */
    private static final String COLLECTION_PATH = "Images";

    /**
     * ImageManager constructor that instantiates the Firebase Storage and Firestore instances
     */
    public ImageManager(){
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

        db.collection("Images").document(uuid).set(imageMap) // changed add to set
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
    static void deleteImageFromFirebase(ConfirmDeleteDialogFragment context, String uuid, UploadCallback callback) {
        // reference to image file in Firebase Storage
        DocumentReference imageRef = Manager.getFirebase().collection("Images")
                .document(uuid);

        // Image deleted unsuccessfully
        imageRef.delete().addOnSuccessListener(aVoid -> {
            // Image deleted successfully
            callback.onSuccess();
        }).addOnFailureListener(callback::onFailure);
    }

    /**
     * Uploads a poster for an event into the database
     *
     * @param context The context that the function is being called in.
     * @param eventId The event ID to associate the uploaded poster with.
     * @param image The bitmap of the event poster
     */
    public static void uploadPoster(Context context, String eventId, Bitmap image) {

        String base64Encoded = Base64.encodeToString(ImageManager.bitmapToByteArray(image), Base64.DEFAULT);

        Map<String, String> imageMap = new HashMap<>();

        imageMap.put("base64Image", base64Encoded);

        getCollection().document(eventId).set(imageMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Poster uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Poster upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Removes the poster of an event from the database
     *
     * @param eventId The event ID to associate the deleted poster with.
     */
    public static void deletePoster(String eventId) {
        ImageManager.isPoster(eventId, posterExists -> {
            if(posterExists){
                getCollection().document(eventId).delete()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Log.d("Firestore", "poster of event " + eventId + " deleted");
                        }else{
                            Log.d("Firestore", "delete failed with ", task.getException());
                        }
                    });
            }
        });
    }

    /**
     * Gets the bitmap of the event poster
     *
     * @param eventId the event id
     * @param poster consumer to receive the bitmap of the poster
     */
    public static void getPoster(String eventId, Consumer<Bitmap> poster){
        //check if the event has a poster
        ImageManager.isPoster(eventId, posterExists -> {
            if(posterExists){
                //fetch the event's poster
                getCollection().document(eventId).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        String base64Image = document.getString("base64Image");
                        if (base64Image != null && !base64Image.isEmpty()) {
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            poster.accept(decodedByte);
                        }
                    }else{
                        Log.d("Firestore", "get failed with ", task.getException());
                    }
                });
            }else{
                // return a deterministic poster
                poster.accept(null);
            }
        });
    }

    /**
     * Checks if the event has a poster associated to it
     *
     * @param eventId the event id
     * @param hasPoster consumer to receive the boolean result
     */
    public static void isPoster(String eventId, Consumer<Boolean> hasPoster){
        getCollection().document(eventId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    hasPoster.accept(true);
                }else{
                    hasPoster.accept(false);
                }
            }else{
                Log.d("Firestore", "get failed with ", task.getException());
            }
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

    /**
     * OpenAI, 2024, ChatGPT, Rotate bitmap based on file metadata
     *
     * Get the orientation of the photo from the metadata
     * @param context App context
     * @param photoUri Uri of the target photo
     * @return Int representing photo orientation in degrees
     */
    public static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            cursor.close();
            return -1;
        }

        cursor.moveToFirst();
        int orientation = cursor.getInt(0);
        cursor.close();
        return orientation;
    }

    /**
     * OpenAI, 2024, ChatGPT, Rotate bitmap based on file metadata
     *
     * Rotate a bitmap image.
     *
     * @param bitmap Bitmap to be rotated
     * @param orientation Number of degrees to rotate
     * @return Rotated bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Get the images collection from firebase
     *
     * @return The collection
     */
    private static CollectionReference getCollection() {
        return getFirebase().collection(COLLECTION_PATH);
    }
}
