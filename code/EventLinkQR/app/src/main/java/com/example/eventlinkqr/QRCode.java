package com.example.eventlinkqr;

import android.graphics.Bitmap;

/** Representation of a QR Code that is used in the app. */
public class QRCode {
    /** The text encoded in the QR Code */
    private final String codeText;

    /**
     * Create a new QRCode from text
     * @param codeText The text to be encoded.
     */
    public QRCode(String codeText) {
        this.codeText = codeText;
    }

    /**
     * Get the data encoded in this QR Code
     * @return QR code data as a string.
     */
    public String getCodeText() {
        return codeText;
    }

    /**
     * Get the Bitmap image representation of the QR Code
     * @return A Bitmap of the QR Code
     * @throws QRCodeGeneratorException when encoding fails.
     */
    public Bitmap toBitmap(int width, int height) throws QRCodeGeneratorException {
        return QRCodeGenerator.imageFromQRCode(this, width, height);
    }
}
