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

@ExtendWith(MockitoExtension.class)
public class QRCodeScannerTest {
    private QRCodeScanner qrCodeScanner;
    @Mock
    private Context mockContext;
    @Mock
    private GmsBarcodeScanner mockScanner;
    @Mock
    private Consumer<QRCode> mockSuccessConsumer;
    @Mock
    private Consumer<Exception> mockFailuerConsumer;
    @Mock
    private Task<Barcode> mockTask;

    @BeforeEach
    public void setup() {
        try (MockedStatic<GmsBarcodeScanning> mockedGmsBarcodeScanning = mockStatic(GmsBarcodeScanning.class)) {
            mockedGmsBarcodeScanning.when(() -> GmsBarcodeScanning.getClient(eq(mockContext), any()))
                    .thenReturn(mockScanner);
            qrCodeScanner = new QRCodeScanner(mockContext);
        }
    }

    @Test
    public void testCodeFromScan() {
        when(mockScanner.startScan()).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
        qrCodeScanner.codeFromScan(mockSuccessConsumer, mockFailuerConsumer);

        verify(mockTask, times(1)).addOnSuccessListener(any());
        verify(mockTask, times(1)).addOnFailureListener(any());
    }

    @Test
    public void testHashBarcodeText() {
        String testText = "ABC-1234-/+"; // Simulate barcode text
        String hashedText = QRCodeScanner.hashBarcodeText(testText);
        assertEquals(hashedText, "7fc2d719a8274ade368094cac41187122963a9a09ac299818132940730434fb1");
    }
}
