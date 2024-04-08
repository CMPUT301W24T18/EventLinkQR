package com.example.eventlinkqr;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for ImageManager.
 * Performs unit testing for methods in ImageManager, focusing on interactions with Firebase Firestore and image processing.
 */
@ExtendWith(MockitoExtension.class)
public class ImageManagerTest {
    @Mock
    private FirebaseFirestore mockDb = mock(FirebaseFirestore.class);
    @Mock
    private Task<Void> mockUploadTask;
    @Mock
    private CollectionReference mockCollectionRef;
    @Mock
    private DocumentReference mockDocumentRef;
    @Mock
    private Context mockContext;

    /**
     * Tests the creation and functionality of ImageManager.
     * Validates the process of uploading an image to Firestore and handling the task callbacks.
     */
    @Test
    public void testCreateImageManager() {
        try(MockedStatic<FirebaseFirestore> mockedFirestore = mockStatic(FirebaseFirestore.class);
            MockedStatic<Base64> mockedBase64 = mockStatic(Base64.class)) {
            mockedFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockDb);
            when(mockDb.collection(anyString())).thenReturn(mockCollectionRef);
            when(mockCollectionRef.document("uuid")).thenReturn(mockDocumentRef);
            when(mockDocumentRef.set(any())).thenReturn(mockUploadTask);

            Bitmap mockBitmap = mock(Bitmap.class);

            mockedBase64.when(() -> Base64.encodeToString(ImageManager.bitmapToByteArray(mockBitmap), Base64.DEFAULT)).thenReturn("abcd");

            when(mockUploadTask.addOnSuccessListener(any())).thenReturn(mockUploadTask);

            ImageManager imageManager = new ImageManager();
            imageManager.uploadImage(mockContext, "uuid", mockBitmap, new ImageManager.UploadCallback() {
                @Override
                public void onSuccess() { }
                @Override
                public void onFailure(Exception exception) { }
            });

            verify(mockUploadTask, times(1)).addOnSuccessListener(any());
            verify(mockUploadTask, times(1)).addOnFailureListener(any());
        }
    }
}
