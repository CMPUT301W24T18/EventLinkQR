package com.example.eventlinkqr;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EventManagerTest {
    @Mock
    public CollectionReference mockCollectionReference;

    @Mock
    public DocumentReference mockDocumentReference;

    @Mock
    public Task<DocumentSnapshot> mockDocumentSnapshotTask;

    @Mock
    public DatabaseManager mockDb;

    @Mock
    public FirebaseFirestore mockFirestore;

    @Mock
    public Context mockContext;

    /**
     * Sets up common mock behavior used in the tests.
     */
    @BeforeEach
    void setUp() {
        when(mockDb.getFirebaseFirestore()).thenReturn(mockFirestore);
        when(mockFirestore.collection("Events")).thenReturn(mockCollectionReference);
    }

    /**
     * Tests the check-in process to validate successful attendee check-in.
     */
    @Test
    public void testCheckIn() {
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getInstance).thenReturn(mockDb);
            when(mockCollectionReference.document("eventId")).thenReturn(mockDocumentReference);

            when(mockDocumentReference.collection("attendees")).thenReturn(mockCollectionReference);
            when(mockCollectionReference.document("875779a8-9646-40ce-b245-882ebdb707ee")).thenReturn(mockDocumentReference);

            when(mockDocumentReference.get()).thenReturn(mockDocumentSnapshotTask);

            EventManager.checkIn(mockContext, "875779a8-9646-40ce-b245-882ebdb707ee", "David", "eventId");

        }
    }

    /**
     * Tests the check-in process to handle failures or exceptions.
     */
    @Test
    public void testCheckInFailed() {
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getInstance).thenReturn(mockDb);
            when(mockCollectionReference.document("eventId")).thenReturn(mockDocumentReference);

            when(mockDocumentReference.collection("attendees")).thenReturn(mockCollectionReference);
            when(mockCollectionReference.document("875779a8-9646-40ce-b245-882ebdb707ee")).thenReturn(mockDocumentReference);

            when(mockDocumentReference.get()).thenReturn(mockDocumentSnapshotTask);

            EventManager.checkIn(mockContext, "875779a8-9646-40ce-b245-882ebdb707ee", "David", "eventId");

        }
    }

    /**
     * Tests the addition of a real-time snapshot listener for event data.
     */
    @Test
    public void testAddEventSnapshotCallback() {
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getInstance).thenReturn(mockDb);
            Query mockQuery = mock(Query.class);
            when(mockCollectionReference.whereEqualTo(eq("organizer"), eq("Me"))).thenReturn(mockQuery);
            EventManager.addEventSnapshotCallback("Me", e -> {});
            verify(mockQuery, times(1)).addSnapshotListener(any());
        }
    }

    /**
     * Tests the addition of a real-time snapshot listener for event attendees.
     */
    @Test
    public void testAddEventAttendeeSnapshotCallback() {
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getInstance).thenReturn(mockDb);
            when(mockCollectionReference.document(eq("eventName"))).thenReturn(mockDocumentReference);
            when(mockDocumentReference.collection(eq("attendees"))).thenReturn(mockCollectionReference);
            EventManager.addEventAttendeeSnapshotCallback("eventName", e -> {});
            verify(mockCollectionReference, times(1)).addSnapshotListener(any());
        }
    }

    /**
     * Tests the addition of a real-time snapshot listener for event attendees with a filter applied.
     */
    @Test
    public void testAddEventAttendeeSnapshotCallbackWithFilter() {
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getInstance).thenReturn(mockDb);
            when(mockCollectionReference.document(eq("eventName"))).thenReturn(mockDocumentReference);
            when(mockDocumentReference.collection(eq("attendees"))).thenReturn(mockCollectionReference);
            Query mockQuery = mock(Query.class);
            when(mockCollectionReference.whereEqualTo(eq("checkedIn"), any())).thenReturn(mockQuery);
            EventManager.addEventAttendeeSnapshotCallback("eventName", true, e -> {});
            verify(mockQuery, times(1)).addSnapshotListener(any());
        }
    }
}
