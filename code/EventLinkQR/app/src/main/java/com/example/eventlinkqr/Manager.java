package com.example.eventlinkqr;

import com.google.firebase.firestore.FirebaseFirestore;

/** Class for implementing managers. Currently the managers just have static methods. */
public abstract class Manager {
    protected static FirebaseFirestore getFirebase() {
        return DatabaseManager.getInstance().getFirebaseFirestore();
    }
}
