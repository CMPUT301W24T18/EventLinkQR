package com.example.eventlinkqr;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminManagerTest {
    @Mock
    private Context mockContext;

    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private CollectionReference mockCollectionReference;

    @Mock
    private DocumentReference mockDocumentReference;

    @Mock
    private DocumentSnapshot mockDocumentSnapshot;

    @Mock
    private Exception mockException;

    @Mock
    private Task<Void> mockVoidTask;

    @Mock
    private Task<DocumentSnapshot> mockDocSnapTask;

    @Captor
    ArgumentCaptor<OnSuccessListener<Void>> voidSuccessCaptor;

    @Captor
    ArgumentCaptor<OnFailureListener> voidFailureCaptor;

    @Captor
    ArgumentCaptor<OnCompleteListener<DocumentSnapshot>> snapshotCompleteCaptor;

    private AdminManager manager;

    @BeforeEach
    public void setup() {
        try (MockedStatic<FirebaseFirestore> mockedFirestore = mockStatic(FirebaseFirestore.class)) {
            mockedFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockDb);
            manager = new AdminManager(mockContext);
        }
    }

    @Test
    public void testDeleteEvent() {
        AdminManager.AdminEventOperationCallback mockCallback = mock(AdminManager.AdminEventOperationCallback.class);

        when(mockDb.collection("Events")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document("event_id_123")).thenReturn(mockDocumentReference);
        when(mockDocumentReference.delete()).thenReturn(mockVoidTask);

        when(mockVoidTask.addOnSuccessListener(any())).thenReturn(mockVoidTask);

        manager.deleteEvent("event_id_123", mockCallback);

        verify(mockDocumentReference, times(1)).delete();

        verify(mockVoidTask, times(1)).addOnSuccessListener(voidSuccessCaptor.capture());
        verify(mockVoidTask, times(1)).addOnFailureListener(any());
        OnSuccessListener<Void> successListener = voidSuccessCaptor.getValue();
        successListener.onSuccess(null);
        verify(mockCallback, times(1)).onSuccess();
        verify(mockCallback, times(0)).onFailure("fail");
    }

    @Test
    public void testDeleteEvent_failed() {
        AdminManager.AdminEventOperationCallback mockCallback = mock(AdminManager.AdminEventOperationCallback.class);

        when(mockDb.collection("Events")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document("event_id_123")).thenReturn(mockDocumentReference);
        when(mockDocumentReference.delete()).thenReturn(mockVoidTask);

        when(mockVoidTask.addOnSuccessListener(any())).thenReturn(mockVoidTask);

        manager.deleteEvent("event_id_123", mockCallback);

        verify(mockDocumentReference, times(1)).delete();

        verify(mockVoidTask, times(1)).addOnSuccessListener(any());
        verify(mockVoidTask, times(1)).addOnFailureListener(voidFailureCaptor.capture());
        OnFailureListener successListener = voidFailureCaptor.getValue();

        when(mockException.getMessage()).thenReturn("fail");

        successListener.onFailure(mockException);

        verify(mockCallback, times(0)).onSuccess();
        verify(mockCallback, times(1)).onFailure("fail");
    }

    @Test
    public void testDeleteUser() {
        AdminManager.AdminEventOperationCallback mockCallback = mock(AdminManager.AdminEventOperationCallback.class);

        when(mockDb.collection("Users")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document("user_id_123")).thenReturn(mockDocumentReference);
        when(mockDocumentReference.delete()).thenReturn(mockVoidTask);

        when(mockVoidTask.addOnSuccessListener(any())).thenReturn(mockVoidTask);

        manager.deleteUser("user_id_123", mockCallback);

        verify(mockDocumentReference, times(1)).delete();

        verify(mockVoidTask, times(1)).addOnSuccessListener(voidSuccessCaptor.capture());
        verify(mockVoidTask, times(1)).addOnFailureListener(any());
        OnSuccessListener<Void> successListener = voidSuccessCaptor.getValue();
        successListener.onSuccess(null);
        verify(mockCallback, times(1)).onSuccess();
        verify(mockCallback, times(0)).onFailure("fail");
    }

    @Test
    public void testDeleteUser_failed() {
        AdminManager.AdminEventOperationCallback mockCallback = mock(AdminManager.AdminEventOperationCallback.class);

        when(mockDb.collection("Users")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document("user_id_123")).thenReturn(mockDocumentReference);
        when(mockDocumentReference.delete()).thenReturn(mockVoidTask);

        when(mockVoidTask.addOnSuccessListener(any())).thenReturn(mockVoidTask);

        manager.deleteUser("user_id_123", mockCallback);

        verify(mockDocumentReference, times(1)).delete();

        verify(mockVoidTask, times(1)).addOnSuccessListener(any());
        verify(mockVoidTask, times(1)).addOnFailureListener(voidFailureCaptor.capture());
        OnFailureListener successListener = voidFailureCaptor.getValue();

        when(mockException.getMessage()).thenReturn("fail");

        successListener.onFailure(mockException);

        verify(mockCallback, times(0)).onSuccess();
        verify(mockCallback, times(1)).onFailure("fail");
    }

    @Test
    public void testFetchImageFromKnownId_exists_notNull() {
        when(mockDb.collection("Images")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document("imageId")).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockDocSnapTask);

        when(mockDocSnapTask.isSuccessful()).thenReturn(true);
        when(mockDocSnapTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockDocumentSnapshot.getString("base64Image")).thenReturn("ABC");

        AdminManager.ImageFetchCallback mockCallback = mock(AdminManager.ImageFetchCallback.class);

        try(MockedStatic<FirebaseFirestore> mockedFirestore = mockStatic(FirebaseFirestore.class)) {
            mockedFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockDb);

            manager.fetchImageFromKnownId("imageId", mockCallback);
        }

        verify(mockDocSnapTask, times(1)).addOnCompleteListener(snapshotCompleteCaptor.capture());

        snapshotCompleteCaptor.getValue().onComplete(mockDocSnapTask);
        verify(mockCallback, times(1)).onImageFetched("ABC");
    }

    @Test
    public void testFetchImageFromKnownId_exists_null() {
        when(mockDb.collection("Images")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document("imageId")).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockDocSnapTask);

        when(mockDocSnapTask.isSuccessful()).thenReturn(true);
        when(mockDocSnapTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockDocumentSnapshot.getString("base64Image")).thenReturn(null);

        AdminManager.ImageFetchCallback mockCallback = mock(AdminManager.ImageFetchCallback.class);

        try(MockedStatic<FirebaseFirestore> mockedFirestore = mockStatic(FirebaseFirestore.class)) {
            mockedFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockDb);

            manager.fetchImageFromKnownId("imageId", mockCallback);
        }

        verify(mockDocSnapTask, times(1)).addOnCompleteListener(snapshotCompleteCaptor.capture());

        snapshotCompleteCaptor.getValue().onComplete(mockDocSnapTask);
        verify(mockCallback, times(1)).onError("Image field is missing.");
    }

    @Test
    public void testFetchImageFromKnownId_notExists() {
        when(mockDb.collection("Images")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document("imageId")).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockDocSnapTask);

        when(mockDocSnapTask.isSuccessful()).thenReturn(true);
        when(mockDocSnapTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(false);

        AdminManager.ImageFetchCallback mockCallback = mock(AdminManager.ImageFetchCallback.class);

        try(MockedStatic<FirebaseFirestore> mockedFirestore = mockStatic(FirebaseFirestore.class)) {
            mockedFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockDb);

            manager.fetchImageFromKnownId("imageId", mockCallback);
        }

        verify(mockDocSnapTask, times(1)).addOnCompleteListener(snapshotCompleteCaptor.capture());

        snapshotCompleteCaptor.getValue().onComplete(mockDocSnapTask);
        verify(mockCallback, times(1)).onError("Document does not exist.");
    }

    @Test
    public void testFetchImageFromKnownId_failed() {
        when(mockDb.collection("Images")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document("imageId")).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockDocSnapTask);

        when(mockDocSnapTask.isSuccessful()).thenReturn(false);
        when(mockDocSnapTask.getException()).thenReturn(mockException);
        when(mockException.getMessage()).thenReturn("Oops!");

        AdminManager.ImageFetchCallback mockCallback = mock(AdminManager.ImageFetchCallback.class);

        try(MockedStatic<FirebaseFirestore> mockedFirestore = mockStatic(FirebaseFirestore.class)) {
            mockedFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockDb);

            manager.fetchImageFromKnownId("imageId", mockCallback);
        }

        verify(mockDocSnapTask, times(1)).addOnCompleteListener(snapshotCompleteCaptor.capture());

        snapshotCompleteCaptor.getValue().onComplete(mockDocSnapTask);
        verify(mockCallback, times(1)).onError("Failed to fetch document: Oops!");
    }
}
