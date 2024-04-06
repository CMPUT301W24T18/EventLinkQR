package com.example.eventlinkqr;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/** Class for managing QR Codes interaction with the database */
public class QRCodeManager extends Manager {
    private static final String COLLECTION_PATH = "QRCode";

    /**
     * Add a QR code to the QRCode collection
     * @param codeText The text encoded in the QR Code
     * @param codeType The type of QRCode
     * @param eventId The event that the code belongs to
     * @return Task to listen for success / failure
     */
    public static Task<DocumentReference> addQRCode(String codeText, int codeType, String eventId) {
        Map<String, Object> qrCode = new HashMap<>();
        qrCode.put("codeType", codeType);
        qrCode.put("event", getFirebase().document("/Events/" + eventId));
        qrCode.put("codeText", codeText);
        return getCollection().add(qrCode);
    }

    /**
     * Get a QRCode object based on the encoded text
     * @param barcodeText The text from a scanned code
     * @return A task with the QRCode object
     */
    public static Task<QRCode> fetchQRCode(String barcodeText) {
        return getCollection().where(Filter.equalTo("codeText", barcodeText)).get().continueWith(d -> {
            DocumentSnapshot doc = d.getResult().getDocuments().get(0);
            String eventId = doc.get("event", DocumentReference.class).getId();
            return new QRCode(barcodeText, doc.get("codeType", Integer.class), eventId);
        });
    }

    /**
     * Get a QRCode object based on the associated event
     * @param event The related event
     * @param codeType The type of code to fetch
     * @return A task with the QRCode object
     */
    public static Task<QRCode> fetchQRCode(Event event, int codeType) {
        return getCollection().where(Filter.and(Filter.equalTo("event", getFirebase().document("/Events/" + event.getId())), Filter.equalTo("codeType", codeType))).get().continueWith(q -> {
            QuerySnapshot querySnapshot = q.getResult();
            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
            return new QRCode(doc.getString("codeText"), doc.get("codeType", Integer.class), event.getId());
        });
    }

    private static CollectionReference getCollection() {
        return getFirebase().collection(COLLECTION_PATH);
    }
}
