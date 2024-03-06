package com.example.eventlinkqr;

import android.graphics.Bitmap;

/** Representation of a QR Code that is used in the app. */
public class QRCode {
    public static final int CHECK_IN_TYPE = 1;
    public static final int PROMOTIONAL_TYPE = 2;


    /** The text encoded in the QR Code */
    private String codeText;

    private int codeType;

    private String eventId;

    /**
     * Create a QR Code object from data that can be retrieved from the firestore.
     * @param codeText Text encoded in the QR Code
     * @param codeType Type of the QR Code (Check in, Promotional)
     * @param eventId Id of the event that this code is associated with.
     */
    public QRCode(String codeText, int codeType, String eventId) {
        this.codeText = codeText;
        this.codeType = codeType;
        this.eventId = eventId;
    }

    /**
     * Get the data encoded in this QR Code
     * @return QR code data as a string.
     */
    public String getCodeText() {
        return codeText;
    }

    /**
     * Set the text encoded in the QR Code
     * @param codeText Encoded text
     */
    public void setCodeText(String codeText) {
        this.codeText = codeText;
    }

    /**
     * Get the type of the code
     * @return Either CHECK_IN_TYPE or PROMOTIONAL_TYPE
     */
    public int getCodeType() {
        return codeType;
    }

    /**
     * Set the type of the code
     * @param codeType Either CHECK_IN_TYPE or PROMOTIONAL_TYPE
     */
    public void setCodeType(int codeType) {
        this.codeType = codeType;
    }

    /**
     * Get the id of the event that this code belongs to
     * @return The event id
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Set the id for the event that this belongs to
     * @param event The event id
     */
    public void setEventId(String event) {
        this.eventId = event;
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
