package com.example.eventlinkqr;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
@RunWith(RobolectricTestRunner.class)
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
    private UploadTask mockUploadTask;
    @Mock
    private DocumentReference mockDocumentRef;
    @Mock
    private Context context;
    private ImageManager imageManager;

//    FirebaseStorage mockStorage = mock(FirebaseStorage.class);
//    FirebaseFirestore mockDb = mock(FirebaseFirestore.class);


//    @RunWith(PowerMockRunner.class)
    @PrepareForTest({ FirebaseFirestore.class, FirebaseStorage.class })
    @Before
    public void setUp() {
        PowerMockito.mockStatic(FirebaseFirestore.class);
        PowerMockito.mockStatic(FirebaseStorage.class);

        FirebaseFirestore mockFirestore = Mockito.mock(FirebaseFirestore.class);
        FirebaseStorage mockStorage = Mockito.mock(FirebaseStorage.class);

        Mockito.when(FirebaseFirestore.getInstance()).thenReturn(mockFirestore);
        Mockito.when(FirebaseStorage.getInstance()).thenReturn(mockStorage);

    }

    /**
     * Tests the generation of a deterministic image to ensure the output is non-null and has the expected dimensions.
     */
    @Test
    public void testGenerateDeterministicImage() {
//        ImageManager imageManager = new ImageManager();
//        String testInput = "testUser";
//        Bitmap image = imageManager.generateDeterministicImage(testInput);
//
//        assertNotNull(image, "Generated image should not be null");
//        assertEquals(100, image.getWidth(), "Image width should be 100 pixels");
//        assertEquals(100, image.getHeight(), "Image height should be 100 pixels");
        ImageManager imageManager = new ImageManager();
        String input = "testInput";

        // Generate an image with a specific input
        Bitmap firstImage = imageManager.generateDeterministicImage(input);
        assertNotNull(firstImage, "The generated image should not be null.");

        // Generate another image with the same input
        Bitmap secondImage = imageManager.generateDeterministicImage(input);
        assertNotNull(secondImage, "The generated image should not be null.");

        // Convert Bitmaps to byte arrays for comparison
        byte[] firstImageBytes = ImageManager.bitmapToByteArray(firstImage);
        byte[] secondImageBytes = ImageManager.bitmapToByteArray(secondImage);

        // Check if two generated images from the same input are identical
        assertArrayEquals(firstImageBytes, secondImageBytes, "The images should be identical when generated with the same input.");

        // Optionally, verify that different inputs produce different images
        String differentInput = "differentTestInput";
        Bitmap differentImage = imageManager.generateDeterministicImage(differentInput);
        byte[] differentImageBytes = ImageManager.bitmapToByteArray(differentImage);

        assertFalse(Arrays.equals(firstImageBytes, differentImageBytes), "The images should be different when generated with different inputs.");
    }

    /**
     * Tests the upload image functionality by simulating an upload to Firebase Storage and verifying the interaction.
     * This test ensures that the method correctly uploads a file to the specified path in Firebase Storage.
     */
    @Test
    public void testUploadImage() {
        Uri fileUri = mock(Uri.class);//Uri.parse("android.resource://com.example.eventlinkqr/drawable/ic_launcher_foreground");
        String userId = "testUser";
        String imagePath = "images/testImage.jpg";

        // Setup the mock to simulate a successful upload
        when(mockUploadTask.isSuccessful()).thenReturn(true);
        when(mockUploadTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<UploadTask.TaskSnapshot> listener = invocation.getArgument(0);
            UploadTask.TaskSnapshot snapshot = mock(UploadTask.TaskSnapshot.class);
            listener.onSuccess(snapshot);
            return mockUploadTask;
        });

        imageManager = new ImageManager();

//        imageManager.uploadImage(context, fileUri, userId, imagePath, );

        verify(mockStorageRef).putFile(fileUri);

        // verify that your Firestore database is updated correctly, depending on how onSuccessListener of the upload task is setup.
        verify(mockDocumentRef).update("imagePath", imagePath);
    }
}
