package com.example.eventlinkqr;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
    }

    @Test
    void addQRCodeTest() {
        // Note, this test was adapted from Chat GPTs suggestion, still citing it here.
        // OpenAI, 2024, ChatGPT, Test add QR code
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getInstance).thenReturn(mockDb);
            when(mockCollectionReference.add(any())).thenReturn(Tasks.forResult(mockDocumentReference));

            // Arrange
            String codeText = "sampleCode";
            int codeType = 1;
            String eventId = "eventId";

            DocumentReference mockEventReference = mock(DocumentReference.class);
            when(mockFirestore.document("/Events/eventId")).thenReturn(mockEventReference);

            // Act
            Task<DocumentReference> result = QRCodeManager.addQRCode(codeText, codeType, eventId);

            // Assert
            Assertions.assertTrue(result.isSuccessful());
            ArgumentCaptor<Map<String, Object>> argumentCaptor = ArgumentCaptor.forClass(Map.class);
            verify(mockCollectionReference).add(argumentCaptor.capture());
            Map<String, Object> capturedArgument = argumentCaptor.getValue();
            Assertions.assertEquals(codeType, capturedArgument.get("codeType"));
            Assertions.assertEquals(mockEventReference, capturedArgument.get("event"));
        }
    }

    @Test
    public void addQrCodeFailed() {
        // Note, this test was adapted from Chat GPTs suggestion, still citing it here.
        // OpenAI, 2024, ChatGPT, Test add QR code
        try (MockedStatic<DatabaseManager> mockedDbManager = mockStatic(DatabaseManager.class)) {
            mockedDbManager.when(DatabaseManager::getInstance).thenReturn(mockDb);
            when(mockCollectionReference.add(any())).thenReturn(Tasks.forException(new Exception()));
            // Arrange
            String codeText = "sampleCode";
            int codeType = 1;

            String eventId = "eventId";

            // Act
            Task<DocumentReference> result = QRCodeManager.addQRCode(codeText, codeType, eventId);

            // Assert
            Assertions.assertFalse(result.isSuccessful());
        }
    }
}
