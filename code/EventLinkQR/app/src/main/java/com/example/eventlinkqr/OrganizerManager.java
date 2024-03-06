package com.example.eventlinkqr;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/** Class for handling organizer related interaction with the database */
public class OrganizerManager extends Manager {
    private static final String COLLECTION_PATH = "Organizers";

    /**
     * This is temporary so not much effort put into it.
     * @param email Email
     * @param password Password
     */
    public static Task<String> signIn(String email, String password) {
        return getCollection().whereEqualTo("email", email)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        // Retrieve the query snapshot
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // organizer with the matching email
                            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                            // set the name of the organizer for future queries and go to the home page
                            if(Objects.equals(password, doc.getString("password"))){
                                return doc.getString("name");
                            } else {
                                throw new RuntimeException("password");
                            }
                        } else {
                            // send invalid email message
                            throw new RuntimeException("email");
                        }
                    } else {
                        // Handle errors
                        Exception exception = task.getException();
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                        return null;
                    }
                });
    }

    private static CollectionReference getCollection() {
        return getFirebase().collection(COLLECTION_PATH);
    }
}
