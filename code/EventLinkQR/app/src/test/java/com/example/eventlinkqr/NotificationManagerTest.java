//package com.example.eventlinkqr;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.ArgumentMatchers.eq;
//
//@ExtendWith(MockitoExtension.class)
//public class NotificationManagerTest {
//
//    private MockedStatic<FirebaseFirestore> mockedFirebaseFirestore;
//    private FirebaseFirestore firebaseFirestore;
//    private CollectionReference collectionReference;
//
//    @Mock
//    private Task<Void> mockAddTask;
//
//    @Captor
//    private ArgumentCaptor<OnSuccessListener<? super Void>> onSuccessListenerCaptor;
//
//    @Captor
//    private ArgumentCaptor<OnFailureListener> onFailureListenerCaptor;
//
//    @BeforeEach
//    void setUp() {
//        // Mock FirebaseFirestore static methods
//        mockedFirebaseFirestore = Mockito.mockStatic(FirebaseFirestore.class);
//        firebaseFirestore = Mockito.mock(FirebaseFirestore.class);
//        collectionReference = Mockito.mock(CollectionReference.class);
//
//        mockedFirebaseFirestore.when(FirebaseFirestore::getInstance).thenReturn(firebaseFirestore);
//        Mockito.when(firebaseFirestore.collection(eq("notifications_testing"))).thenReturn(collectionReference);
////        Mockito.when(collectionReference.add(any()).getResult(mockAddTask);
//    }
//
//    @Test
//    void testSendNotificationToDatabase() {
//        // Given
//        String eventId = "event123";
//        String title = "Test Notification";
//        String description = "This is a test description.";
//
//        // When
//        NotificationManager.sendNotificationToDatabase(eventId, title, description);
//
//        // Then
//        Mockito.verify(collectionReference).add(any());
//
////        verify(mockAddTask, times(1)).addOnSuccessListener(onSuccessListenerCaptor.capture());
////        onSuccessListenerCaptor.getValue().onSuccess(null);
////
////        // Capture and trigger onFailureListener
////        verify(mockAddTask, times(1)).addOnFailureListener(onFailureListenerCaptor.capture());
////        onFailureListenerCaptor.getValue().onFailure(new Exception("Test failure"));
//        // At this point, you can verify effects of onFailure (e.g., logging)
//    }
//}
//
//
//////        ArgumentCaptor<OnSuccessListener<Void>> successListenerCaptor = ArgumentCaptor.forClass(OnSuccessListener.class);
//////        Mockito.verify(mockTask).addOnSuccessListener(successListenerCaptor.capture());
//////
//////        successListenerCaptor.getValue().onSuccess(null);
//////
//////        ArgumentCaptor<OnFailureListener> failureListenerCaptor = ArgumentCaptor.forClass(OnFailureListener.class);
//////        Mockito.verify(mockTask).addOnFailureListener(failureListenerCaptor.capture());
//////
//////        Exception mockException = new Exception("Mock failure");
//////        failureListenerCaptor.getValue().onFailure(mockException);

package com.example.eventlinkqr;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationManagerTest {

    private MockedStatic<FirebaseFirestore> mockedFirebaseFirestore;

    @Mock
    private Task<Void> mockAddTask;

    @Mock
    private FirebaseFirestore firebaseFirestore;
    @Mock
    private CollectionReference collectionReference;

    @BeforeEach
    void setUp() {
        mockedFirebaseFirestore = Mockito.mockStatic(FirebaseFirestore.class);
        mockedFirebaseFirestore.when(FirebaseFirestore::getInstance).thenReturn(firebaseFirestore);
        Mockito.when(firebaseFirestore.collection(eq("notifications_testing"))).thenReturn(collectionReference);
    }

    @Test
    void testSendNotificationToDatabase() {
        // Given
        String eventId = "event1234";
        String title = "Test Notification";
        String description = "This is a test description.";

        // When
        NotificationManager.sendNotificationToDatabase(eventId, title, description);

        // Then
        verify(collectionReference, times(1)).add(any());
    }

}
