package com.example.eventlinkqr;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.function.Consumer;

public class AttendeeManager extends Manager {
    /**
     * The Firestore collection path for attendees
     */
    private static final String COLLECTION_PATH = "Users";


    private static CollectionReference getCollection() {
        return getFirebase().collection(COLLECTION_PATH);
    }

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
