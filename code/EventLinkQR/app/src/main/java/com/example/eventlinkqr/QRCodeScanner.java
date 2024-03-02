package com.example.eventlinkqr;

import android.content.Context;

import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.util.function.Consumer;

/** Encapsulate Google MLKit QR code scanning and create QR Code objects. There should only be one
 * scanner in the app. */
public class QRCodeScanner {
    /** The single scanner object for the whole app. */
    private final GmsBarcodeScanner scanner;

    /**
     * Initialize the QR code scanner to be able to create new QR Code objects from scan.
     * @param context The context that the scanner runs in.
     */
    public QRCodeScanner(Context context) {
        GmsBarcodeScannerOptions.Builder builder = new GmsBarcodeScannerOptions.Builder();
        builder.enableAutoZoom();
        this.scanner = GmsBarcodeScanning.getClient(context, builder.build());
    }

    /**
     * Activate the QRCode scanner and create a QR Code object from the result.
     * @param successCallback Callback to be invoked when a QR code is successfully scanned.
     * @param failureCallback Callback to be invoked when an error occurs during scan.
     */
    public void codeFromScan(Consumer<QRCode> successCallback, Consumer<Exception> failureCallback) {
        scanner.startScan()
                .addOnSuccessListener(barcode -> {
                    String barcodeText = barcode.getRawValue();
                    QRCode code = new QRCode(barcodeText);
                    successCallback.accept(code);
                })
                .addOnFailureListener(failureCallback::accept);
    }
}
