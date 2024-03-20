package com.example.eventlinkqr;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
//@RunWith(RobolectricTestRunner.class)
public class ImageManagerTest {
    @Mock
    private FirebaseStorage mockStorage = mock(FirebaseStorage.class);
    @Mock
    private FirebaseFirestore mockDb = mock(FirebaseFirestore.class);
    @Mock
    private Task<Uri> mockDownloadUrlTask;
    @Mock
    private StorageReference mockStorageRef;
    @Mock
    private StorageReference mockImageRef;
    @Mock
    private UploadTask mockUploadTask;
    @Mock
    private DocumentReference mockDocumentRef;
    @Mock
    private Context mockContext;

    @Test
    public void testCreateImageManager() {
        try(MockedStatic<FirebaseFirestore> mockedFirestore = mockStatic(FirebaseFirestore.class);
            MockedStatic<FirebaseStorage> mockedStorage = mockStatic(FirebaseStorage.class)) {
            mockedStorage.when(FirebaseStorage::getInstance).thenReturn(mockStorage);
            mockedFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockDb);
            when(mockStorage.getReference()).thenReturn(mockStorageRef);
            when(mockStorageRef.child(anyString())).thenReturn(mockImageRef);
            when(mockImageRef.putFile(any(Uri.class))).thenReturn(mockUploadTask);
            when(mockUploadTask.addOnSuccessListener(any())).thenReturn(mockUploadTask);

            ImageManager imageManager = new ImageManager();

//            imageManager.uploadImage(mockContext, new ImageManager.UploadCallback() {
//                @Override
//                public void onSuccess() { }
//                @Override
//                public void onFailure(Exception exception) { }
//            });

            verify(mockImageRef, times(1)).putFile(any(Uri.class));
            verify(mockUploadTask, times(1)).addOnSuccessListener(any());
            verify(mockUploadTask, times(1)).addOnFailureListener(any());
        }
    }
}
