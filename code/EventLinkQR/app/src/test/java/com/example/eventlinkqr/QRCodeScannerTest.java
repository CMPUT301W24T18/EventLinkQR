package com.example.eventlinkqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

/**
 * Test class for QRCodeScanner.
 * Performs unit testing on QRCodeScanner's functionality, particularly the scanning and handling of QR codes.
 */
@ExtendWith(MockitoExtension.class)
public class QRCodeScannerTest {
    private QRCodeScanner qrCodeScanner;
    @Mock
    private Context mockContext;
    @Mock
    private GmsBarcodeScanner mockScanner;
    @Mock
    private Consumer<String> mockSuccessConsumer;
    @Mock
    private Consumer<Exception> mockFailuerConsumer;
    @Mock
    private Runnable mockCancledRunnable;
    @Mock
    private Task<Barcode> mockTask;

    /**
     * Sets up the QRCodeScanner instance with mocked dependencies for testing.
     */
    @BeforeEach
    public void setup() {
        try (MockedStatic<GmsBarcodeScanning> mockedGmsBarcodeScanning = mockStatic(GmsBarcodeScanning.class)) {
            mockedGmsBarcodeScanning.when(() -> GmsBarcodeScanning.getClient(eq(mockContext), any()))
                    .thenReturn(mockScanner);
            qrCodeScanner = new QRCodeScanner(mockContext);
        }
    }

    /**
     * Tests the codeFromScan method of QRCodeScanner.
     * Validates if the scanning process correctly interacts with success, failure, and canceled listeners.
     */
    @Test
    public void testCodeFromScan() {
        when(mockScanner.startScan()).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask);
        qrCodeScanner.codeFromScan(mockSuccessConsumer, mockFailuerConsumer, mockCancledRunnable);

        verify(mockTask, times(1)).addOnSuccessListener(any());
        verify(mockTask, times(1)).addOnFailureListener(any());
        verify(mockTask, times(1)).addOnCanceledListener(any());
    }

    /**
     * Tests the hashBarcodeText method of QRCodeScanner.
     * Checks if the method correctly hashes a given barcode text.
     */
    @Test
    public void testHashBarcodeText() {
        String testText = "ABC-1234-/+"; // Simulate barcode text
        String hashedText = QRCodeScanner.hashBarcodeText(testText);
        assertEquals(hashedText, "7fc2d719a8274ade368094cac41187122963a9a09ac299818132940730434fb1");
    }
}
