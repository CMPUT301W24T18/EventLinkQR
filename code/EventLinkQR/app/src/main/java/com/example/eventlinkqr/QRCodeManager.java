package com.example.eventlinkqr;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/** Class for managing QR Codes interaction with the database */
public class QRCodeManager extends Manager {
    private static final String COLLECTION_PATH = "QRCode";

    /**
     * Add a QR code to the QRCode collection
     * @param codeText The text encoded in the QR Code
     * @param codeType The type of QRCode
     * @param event The event that the code belongs to
     * @return Task to listen for success / failure
     */
    public static Task<Void> addQRCode(String codeText, int codeType, Event event) {
        Map<String, Object> qrCode = new HashMap<>();
        qrCode.put("codeType", codeType);
        qrCode.put("event", "/Event/" + event.getId());
        return getCollection().document(codeText).set(qrCode);
    }

    /**
     * Get a QRCode object based on the encoded text
     * @param barcodeText The text from a scanned code
     * @return A task with the QRCode object
     */
    public static Task<QRCode> fetchQRCode(String barcodeText) {
        return getCollection().document(barcodeText).get().continueWith(d -> {
            DocumentSnapshot doc = d.getResult();
            String eventId = doc.get("event", DocumentReference.class).getId();
            return new QRCode(barcodeText, doc.get("codeType", Integer.class), eventId);
        });
    }

    private static CollectionReference getCollection() {
        return getFirebase().collection(COLLECTION_PATH);
    }
}
