package com.example.eventlinkqr;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class QRCodeManagerTest {
    @Mock
    public CollectionReference mockCollectionReference;

    @Mock
    public DocumentReference mockDocumentReference;

    @Mock
    public DatabaseManager mockDb;

    @Mock
    public FirebaseFirestore mockFirestore;

    @BeforeEach
    void setUp() {
        when(mockDb.getFirebaseFirestore()).thenReturn(mockFirestore);
        when(mockFirestore.collection("QRCode")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
    }

    @Test
    void addQRCodeTest() {
        // Note, this test was adapted from Chat GPTs suggestion, still citing it here.
        // OpenAI, 2024, ChatGPT, Test add QR code
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getInstance).thenReturn(mockDb);
            when(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null));

            // Arrange
            String codeText = "sampleCode";
            int codeType = 1;
            Event event = new Event("eventId", "event description", "category", Timestamp.now(), "location", true);

            // Act
            Task<Void> result = QRCodeManager.addQRCode(codeText, codeType, event);

            // Assert
            Assertions.assertTrue(result.isSuccessful());
            ArgumentCaptor<Map<String, Object>> argumentCaptor = ArgumentCaptor.forClass(Map.class);
            verify(mockDocumentReference).set(argumentCaptor.capture());
            Map<String, Object> capturedArgument = argumentCaptor.getValue();
            Assertions.assertEquals(codeType, capturedArgument.get("codeType"));
            Assertions.assertEquals("/Event/" + event.getId(), capturedArgument.get("event"));
        }
    }

    @Test
    public void addQrCodeFailed() {
        // Note, this test was adapted from Chat GPTs suggestion, still citing it here.
        // OpenAI, 2024, ChatGPT, Test add QR code
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getInstance).thenReturn(mockDb);
            when(mockDocumentReference.set(any())).thenReturn(Tasks.forException(new Exception()));

            // Arrange
            String codeText = "sampleCode";
            int codeType = 1;
            Event event = new Event("eventId", "event description", "category", Timestamp.now(), "location", true);

            // Act
            Task<Void> result = QRCodeManager.addQRCode(codeText, codeType, event);

            // Assert
            Assertions.assertFalse(result.isSuccessful());
        }
    }
}
