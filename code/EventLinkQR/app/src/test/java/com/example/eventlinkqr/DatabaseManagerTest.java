package com.example.eventlinkqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class DatabaseManagerTest {

    @Mock
    public FirebaseDatabase mockFirebaseDatabase;

    @Mock FirebaseFirestore mockFirebaseFirestore;

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
