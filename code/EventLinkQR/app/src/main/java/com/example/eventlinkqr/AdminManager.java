package com.example.eventlinkqr;

import android.content.Context;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminManager extends Manager {
    private final FirebaseFirestore db;
    private final Context context;

    /**
     * Constructs an instance of AdminManager with the provided context.
     * @param context The context in which the AdminManager operates.
     */
    public AdminManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Deletes an event with the specified eventId from the Firestore database.
     * @param eventId The ID of the event to delete.
     * @param callback Callback to handle the result of the deletion operation.
     */
    public void deleteEvent(String eventId, final AdminEventOperationCallback callback) {
        db.collection("Events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e.getMessage());
                });
    }

    /**
     * Callback interface for handling the outcome of administrative operations.
     */
    public interface AdminEventOperationCallback {
        /**
         * Called when the operation is successful.
         */
        void onSuccess();

        /**
         * Called when the operation fails.
         * @param errorMessage A string describing the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * Callback interface for handling the results of fetching images from the Firestore database.
     */
    public interface FetchImagesCallback {
        /**
         * Called when images are successfully fetched.
         * @param images A list of ImageModel objects representing the fetched images.
         * @param documentIds A list of document IDs corresponding to the fetched images.
         */
        void onSuccess(List<ImageModel> images, List<String> documentIds);

        /**
         * Called when there is an error fetching images.
         * @param errorMessage A string describing the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * Fetches images from the Firestore database, excluding those with base64 matching the violated images.
     * @param callback Callback to handle the result of the fetch operation, providing a list of ImageModels and their document IDs.
     */
    public void fetchImages(final FetchImagesCallback callback) {
    // First, fetch the base64 strings for the violated images
        fetchViolatedImagesBase64(new ViolatedImagesBase64Callback() {
        @Override
        public void onSuccess(Map<String, String> violatedBase64Images) {
            List<ImageModel> imageList = new ArrayList<>();
            List<String> documentIds = new ArrayList<>();

            db.collection("Images").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                ImageModel image = documentSnapshot.toObject(ImageModel.class);

                                // Check if the current image's base64 is not equal to those of violated images
                                if (!violatedBase64Images.containsValue(image.getBase64Image())) {
                                    imageList.add(image);
                                    documentIds.add(documentSnapshot.getId());
                                }
                            }
                            callback.onSuccess(imageList, documentIds);
                        } else {
                            callback.onFailure("Failed to fetch images: No images found.");
                        }
                    })
                    .addOnFailureListener(e -> callback.onFailure("Failed to fetch images: " + e.getMessage()));
        }

        @Override
        public void onFailure(String errorMessage) {
            callback.onFailure("Failed to fetch violated images base64: " + errorMessage);
        }
    });
}

    /**
     * Fetches the base64 strings of predefined "violated" images to facilitate filtering during the fetchImages operation.
     * @param callback Callback to handle the fetched base64 strings or any errors.
     */
    private void fetchViolatedImagesBase64(ViolatedImagesBase64Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, String> violatedBase64Images = new HashMap<>();

        // Assuming these document IDs are known and constant
        List<String> violatedImageDocIds = Arrays.asList("violated_poster", "profile_violated");

        AtomicInteger count = new AtomicInteger(violatedImageDocIds.size());

        for (String docId : violatedImageDocIds) {
            db.collection("Images").document(docId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    String base64Image = task.getResult().getString("base64Image");
                    if (base64Image != null) {
                        violatedBase64Images.put(docId, base64Image);
                    }
                }
                if (count.decrementAndGet() == 0) {
                    // Once all fetches are complete
                    callback.onSuccess(violatedBase64Images);
                }
            }).addOnFailureListener(e -> callback.onFailure("Failed to fetch base64 for document: " + docId));
        }
    }

    /**
     * Callback interface for handling the fetched base64 strings of "violated" images.
     */
    interface ViolatedImagesBase64Callback {
        /**
         * Called when base64 strings for "violated" images are successfully fetched.
         * @param violatedBase64Images A map with document IDs as keys and their corresponding base64 strings as values.
         */
        void onSuccess(Map<String, String> violatedBase64Images);

        /**
         * Called when there is an error fetching the base64 strings.
         * @param errorMessage A string describing the error that occurred.
         */
        void onFailure(String errorMessage);
    }

    /**
     * Replaces the image for a given document ID with a default image based on its classification.
     * @param documentId The ID of the document for which to replace the image.
     * @param callback Callback to handle the result of the replace operation.
     */
    public void replaceImageWithDefault(String documentId, final AdminEventOperationCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Determine which image to fetch based on the document ID
        String imageDocId = documentId.length() == 20 ? "violated_poster" : "profile_violated";

        // Fetch the appropriate image
        fetchImageFromKnownId(imageDocId, new ImageFetchCallback() {
            @Override
            public void onImageFetched(String base64Image) {
                // Create a map with the base64Image field updated to the fetched image
                Map<String, Object> updatedImageData = new HashMap<>();
                updatedImageData.put("base64Image", base64Image);

                // Update the document with the new image
                db.collection("Images").document(documentId)
                        .update(updatedImageData)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }

            @Override
            public void onError(String errorMessage) {
                // Call the admin event operation callback with the error message
                callback.onFailure(errorMessage);
            }
        });
    }

    /**
     * Fetches an image's base64 string from a document with a known ID.
     * @param documentId The ID of the document from which to fetch the image.
     * @param callback Callback to handle the result of the fetch operation, providing the base64 string of the image.
     */
    public void fetchImageFromKnownId(String documentId, final ImageFetchCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Directly access the document with ID "poster_violated"
        db.collection("Images").document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Fetch the base64Image field
                            String base64Image = document.getString("base64Image");
                            if (base64Image != null) {
                                // Pass the image back through the callback
                                callback.onImageFetched(base64Image);
                            } else {
                                // Handle the case where the image is not found
                                callback.onError("Image field is missing.");
                            }
                        } else {
                            // Handle the case where the document does not exist
                            callback.onError("Document does not exist.");
                        }
                    } else {
                        // Handle the task failure
                        callback.onError("Failed to fetch document: " + task.getException().getMessage());
                    }
                });
    }

    // Callback interface for image fetching
    public interface ImageFetchCallback {
        void onImageFetched(String base64Image);
        void onError(String errorMessage);
    }

    /**
     * Deletes a user with the specified userId from the Firestore database.
     * @param userId The ID of the user to delete.
     * @param callback Callback to handle the result of the deletion operation.
     */
    public void deleteUser(String userId, final AdminEventOperationCallback callback) {
        db.collection("Users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /**
     * Fetches users from the Firestore database and optionally sorts them alphabetically by name.
     * @param callback Callback to handle the result of the fetch operation, providing a list of Users.
     */
    public void fetchUsers(final FetchUsersCallback callback) {
        List<User> usersList = new ArrayList<>();

        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    usersList.add(user);
                }

                // Optionally, sort the usersList here if needed
                Collections.sort(usersList, new Comparator<User>() {
                    @Override
                    public int compare(User user1, User user2) {
                        return user1.getName().compareToIgnoreCase(user2.getName());
                    }
                });

                callback.onSuccess(usersList);
            } else {
                callback.onFailure(task.getException().getMessage());
            }
        });
    }

    /**
     * Callback interface for handling the results of fetching users from the Firestore database.
     */
    public interface FetchUsersCallback {
        /**
         * Called when users are successfully fetched.
         * @param usersList A list of User objects representing the fetched users.
         */
        void onSuccess(List<User> usersList);

        /**
         * Called when there is an error fetching users.
         * @param errorMessage A string describing the error that occurred.
         */
        void onFailure(String errorMessage);
    }



}
