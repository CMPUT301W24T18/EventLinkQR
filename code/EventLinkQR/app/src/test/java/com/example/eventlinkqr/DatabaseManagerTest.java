package com.example.eventlinkqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for DatabaseManager.
 * This class tests the functionality and behavior of the DatabaseManager class, particularly
 * its initialization and interaction with Firebase services.
 */
@ExtendWith(MockitoExtension.class)
public class DatabaseManagerTest {

    @Mock
    public FirebaseDatabase mockFirebaseDatabase;

    @Mock FirebaseFirestore mockFirebaseFirestore;

    /**
     * Tests the initialization of the DatabaseManager.
     * Validates if the DatabaseManager properly initializes and retrieves instances
     * of FirebaseDatabase and FirebaseFirestore. It uses mock objects to ensure
     * independence from the actual Firebase services.
     */
    @Test
    public void testDatabaseInitialization() {
        try (MockedStatic<FirebaseDatabase> mockedDatabase = mockStatic(FirebaseDatabase.class);
                MockedStatic<FirebaseFirestore> mockedFirestore = mockStatic(FirebaseFirestore.class)) {
            mockedDatabase.when(FirebaseDatabase::getInstance).thenReturn(mockFirebaseDatabase);
            mockedFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockFirebaseFirestore);

            DatabaseManager manager = DatabaseManager.getInstance();
            FirebaseDatabase database = manager.getFirebaseDatabase();
            FirebaseFirestore firestore = manager.getFirebaseFirestore();

            assertEquals(database, mockFirebaseDatabase);
            assertEquals(firestore, mockFirebaseFirestore);
            mockedDatabase.verify(FirebaseDatabase::getInstance);
            mockedFirestore.verify(FirebaseFirestore::getInstance);
        }
    }
}
