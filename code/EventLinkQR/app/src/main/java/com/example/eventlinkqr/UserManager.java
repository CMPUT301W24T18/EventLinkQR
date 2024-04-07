package com.example.eventlinkqr;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.function.Consumer;

/**
 * Manages operations related to attendees in the database
 */
public class UserManager extends Manager {
    /**
     * The Firestore collection path for attendees
     */
    private static final String COLLECTION_PATH = "Users";

    /**
     * Provides a reference to the Firestore collection containing attendee data.
     *
     * @return A reference to the Firestore 'Users' collection.
     */
    private static CollectionReference getCollection() {
        return getFirebase().collection(COLLECTION_PATH);
    }

    /**
     * Creates a User object from a Firestore document snapshot.
     *
     * @param document The document snapshot from which to create the User object.
     * @return A User object populated with data from the document snapshot.
     */
    private static User fromDocument(DocumentSnapshot document) {
        User a = new User(
                document.getString("uuid"),
                document.getString("name"),
                document.getString("phone_number"),
                document.getString("homepage"),
                document.getString("fcmToken"),
                document.getBoolean("location_enabled"),
                document.getBoolean("isAdmin")
        );
        return a;
    }

    /**
     *  Given a UUID, retrieve the attendee object from the database
     */
    public static void getUser(String uuid, Consumer<User> callback) {
        getCollection().document(uuid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                callback.accept(fromDocument(documentSnapshot));
            } else {
                callback.accept(null);
            }
        });
    }

    /**
     * Deletes a user with the specified userId from the Firestore database.
     * @param userId The ID of the user to delete.
     */
    public static void deleteUser(String userId) {
        getCollection().document(userId)
            .delete()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Log.d("Firestore", "user " + userId + " deleted");
                }else{
                    Log.d("Firestore", "delete failed with ", task.getException());
                }
            });
    }
}
