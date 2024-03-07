package com.example.eventlinkqr;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Singleton class to manage the Firebase Realtime Database instance.
 * This class ensures that only one instance of the FirebaseDatabase is initialized
 * and used throughout the application, providing a centralized point of access
 * to the Firebase Realtime Database.
 */
public class DatabaseManager {

    /** Singleton instance of DatabaseManager */
    private static volatile DatabaseManager instance;

    /** Instance of FirebaseDatabase */
    private final FirebaseDatabase firebaseDatabase;

    /** Instance of Firestore database */
    private final FirebaseFirestore firebaseFirestore;


    /**
     * Private constructor to prevent instantiation from outside the class.
     * Initializes the FirebaseDatabase instance.
     */
    private DatabaseManager() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    /**
     * Gets the singleton instance of the DatabaseManager.
     * If the instance does not exist, it initializes the instance with synchronization
     * to ensure thread safety.
     *
     * @return The singleton instance of DatabaseManager.
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    /**
     * Provides access to the FirebaseDatabase instance.
     *
     * @return The FirebaseDatabase instance for database operations.
     */
    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    /**
     * Provides access to the FirebaseFirestore instance.
     * @return The FirebaseFirestore instance for database operations.
     */
    public FirebaseFirestore getFirebaseFirestore() {
        return firebaseFirestore;
    }
}
