package com.example.eventlinkqr;

import android.content.Context;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
        List<ImageModel> imageList = new ArrayList<>();
        List<String> documentIds = new ArrayList<>();

        db.collection("Images").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ImageModel image = documentSnapshot.toObject(ImageModel.class);
                            imageList.add(image);
                            documentIds.add(documentSnapshot.getId());
                        }
                        callback.onSuccess(imageList, documentIds);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Failed to fetch images"));
    }

    public void deleteImage(String documentId, final AdminEventOperationCallback callback) {
        db.collection("Images").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
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
