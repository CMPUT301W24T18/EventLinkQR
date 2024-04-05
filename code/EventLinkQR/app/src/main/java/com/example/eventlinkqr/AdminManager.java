package com.example.eventlinkqr;

import android.content.Context;

import com.google.firebase.firestore.CollectionReference;
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
import java.util.function.Consumer;

public class AdminManager extends Manager {
    private final FirebaseFirestore db;
    private final Context context;

    
    public AdminManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

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

    public interface AdminEventOperationCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface FetchImagesCallback {
        void onSuccess(List<ImageModel> images, List<String> documentIds);
        void onFailure(String errorMessage);
    }

public void fetchImages(final FetchImagesCallback callback) {
    // First, fetch the base64 strings for the violated images
    fetchViolatedImagesBase64(new ViolatedImagesBase64Callback() {
        @Override
        public void onSuccess(Map<String, String> violatedBase64Images) {
            List<ImageModel> imageList = new ArrayList<>();
            List<String> documentIds = new ArrayList<>();

            db.collection("images_testing").get()
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

    // Method to fetch the base64 strings for violated images
    private void fetchViolatedImagesBase64(ViolatedImagesBase64Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, String> violatedBase64Images = new HashMap<>();

        // Assuming these document IDs are known and constant
        List<String> violatedImageDocIds = Arrays.asList("violated_poster", "profile_violated");

        AtomicInteger count = new AtomicInteger(violatedImageDocIds.size());

        for (String docId : violatedImageDocIds) {
            db.collection("images_testing").document(docId).get().addOnCompleteListener(task -> {
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

    interface ViolatedImagesBase64Callback {
        void onSuccess(Map<String, String> violatedBase64Images);
        void onFailure(String errorMessage);
    }


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
                db.collection("images_testing").document(documentId)
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

    // Method to fetch an image from a document with a known ID
    public void fetchImageFromKnownId(String documentId, final ImageFetchCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Directly access the document with ID "poster_violated"
        db.collection("images_testing").document(documentId)
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


    public void deleteUser(String userId, final AdminEventOperationCallback callback) {
        db.collection("Users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void fetchUsers(final FetchUsersCallback callback) {
        List<Attendee> usersList = new ArrayList<>();

        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Attendee attendee = document.toObject(Attendee.class);
                    usersList.add(attendee);
                }

                // Optionally, sort the usersList here if needed
                Collections.sort(usersList, new Comparator<Attendee>() {
                    @Override
                    public int compare(Attendee user1, Attendee user2) {
                        return user1.getName().compareToIgnoreCase(user2.getName());
                    }
                });

                callback.onSuccess(usersList);
            } else {
                callback.onFailure(task.getException().getMessage());
            }
        });
    }

    public interface FetchUsersCallback {
        void onSuccess(List<Attendee> usersList);
        void onFailure(String errorMessage);
    }


}
