package com.example.eventlinkqr;

import android.content.Context;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
     * @param cancelCallback Callback to be invoked when the scan is cancelled
     */
    public void codeFromScan(Consumer<String> successCallback, Consumer<Exception> failureCallback, Runnable cancelCallback) {
        scanner.startScan()
                .addOnSuccessListener(barcode -> {
                    String barcodeText = barcode.getRawValue();

                    // If the scanned code is not in QR Code format, hash it
                    if (barcodeText != null && barcode.getFormat() != Barcode.FORMAT_QR_CODE) {
                        barcodeText = hashBarcodeText(barcodeText);
                    }

                    successCallback.accept(barcodeText);
                })
                .addOnFailureListener(failureCallback::accept)
                .addOnCanceledListener(cancelCallback::run);
    }

    /**
     * Hash provided text using SHA-256 algorithm
     * @param barcodeText Text to be hashed
     * @return Hashed text in hex format
     */
    public static String hashBarcodeText(String barcodeText) {
        // See example in official docs: https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        // Get the hash
        byte[] barcodeTextBytes = md.digest(barcodeText.getBytes(StandardCharsets.UTF_8));

        return byteArrayToHex(barcodeTextBytes);
    }

    /**
     * OpenAI, 2024, ChatGPT, Byte array to hex string
     * Converts a byte array to a hexadecimal string.
     * @param byteArray The byte array to be converted.
     * @return A string representing the hexadecimal representation of the byte array.
     */
    private static String byteArrayToHex(byte[] byteArray) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
