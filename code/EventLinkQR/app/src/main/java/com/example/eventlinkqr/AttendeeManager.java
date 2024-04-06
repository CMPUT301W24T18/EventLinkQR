package com.example.eventlinkqr;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.function.Consumer;

/**
 * Manages operations related to attendees in the database.
 */
public class AttendeeManager extends Manager {
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
     * Creates an Attendee object from a Firestore document snapshot.
     *
     * @param document The document snapshot from which to create the Attendee object.
     * @return An Attendee object populated with data from the document snapshot.
     */
    private static Attendee fromDocument(DocumentSnapshot document) {
        Attendee a = new Attendee(
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
    public static void getAttendee(String uuid, Consumer<Attendee> callback) {
        getCollection().document(uuid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                callback.accept(fromDocument(documentSnapshot));
            } else {
                callback.accept(null);
            }
        });
    }
}
