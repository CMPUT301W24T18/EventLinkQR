package com.example.eventlinkqr;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** Class for managing database interaction for events */
public class EventManager extends Manager {
    /** The Firestore collection path for events */
    private static final String COLLECTION_PATH = "testCollection";

    /**
     * Check the given attendee into an event
     * @param attendeeName The name of the attendee to check in
     * @param eventId The id of the event to check into
     */
    public static void checkIn(String attendeeName, String eventId) {
        getCollection().document(eventId).collection("attendees").document(attendeeName).update("checkedIn", true);
    }

    /**
     * Add a callback to changes in the Events
     * @param organizer The organizer to filter events on
     * @param eventCallback The callback to be invoked when the events change
     */
    public static void addEventSnapshotCallback(String organizer, Consumer<List<Event>> eventCallback) {
        getCollection().whereEqualTo("organizer", organizer).addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }

            if (querySnapshots != null) {
                eventCallback.accept(querySnapshots.getDocuments().stream().map(EventManager::fromDocument).collect(Collectors.toList()));
            }
        });
    }

    /**
     * Add a callback to changes in the event attendees.
     * @param eventName Event to get attendees for
     * @param attendeeCallback The callback to be invoked when the event attendees change
     */
    public static void addEventAttendeeSnapshotCallback(String eventName, Consumer<List<String>> attendeeCallback) {
        getCollection().document(eventName).collection("attendees").addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }

            if (querySnapshots != null) {
                attendeeCallback.accept(querySnapshots.getDocuments().stream().map(d -> d.getString("name")).collect(Collectors.toList()));
            }
        });
    }

    /**
     * Add a callback to changes in the event attendees.
     * @param eventName Event to get attendees for
     * @param checkedIn Filter on checked-in / not-checked-in attendees
     * @param attendeeCallback The callback to be invoked when the event attendees change
     */
    public static void addEventAttendeeSnapshotCallback(String eventName, boolean checkedIn, Consumer<List<String>> attendeeCallback) {
        getCollection().document(eventName).collection("Attendees").where(Filter.arrayContains("checkedIn", checkedIn)).addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }

            if (querySnapshots != null) {
                attendeeCallback.accept(querySnapshots.getDocuments().stream().map(d -> d.getString("name")).collect(Collectors.toList()));
            }
        });
    }

    /**
     * Create an event from the document
     * @param document The document to generate the event from
     * @return The event
     */
    private static Event fromDocument(DocumentSnapshot document) {
        Event e =  new Event(
                document.get("name", String.class),
                document.get("description", String.class),
                document.get("category", String.class),
                document.get("dateAndTime", Timestamp.class).toString(),
                document.get("location", String.class),
                document.get("geoTracking", Boolean.class));
        e.setId(document.getId());

        /**
         * Make sure that the document has geoTracking enabled and that the checkInLocations field is not null
         * and that the checkInLocations field is not empty and that the type of the checkInLocations field is
         * a list of GeoPoints
         */
        ArrayList<com.google.android.gms.maps.model.LatLng> locations = null;
        if (Boolean.TRUE.equals(document.getBoolean("geoTracking"))) {
            List<GeoPoint> geoPoints = (List<GeoPoint>) document.get("checkInLocations"); //This cast is fine because we know the type of the field
            if (geoPoints != null && !geoPoints.isEmpty()) {
                // Convert each GeoPoint to a LatLng and add to the locations list
                for (GeoPoint gp : geoPoints) {
                    locations.add(new LatLng(gp.getLatitude(), gp.getLongitude()));
                }
            }
            e.setCheckInLocations(locations);
        }

        return e;
    }

    /**
     * Get the events collection from firebase
     * @return The collection
     */
    private static CollectionReference getCollection() {
        return getFirebase().collection(COLLECTION_PATH);
    }

    /**
     * Retrieve an event from its ID and use the provided callback to return the event object
     * @param eventId The id of the event to retrieve
     * @param eventCallback The callback to be invoked with the Event object once it is retrieved
     */
    public static void getEventById(String eventId, Consumer<Event> eventCallback) {
        getFirebase().collection("events_testing").document(eventId).get().addOnCompleteListener((Task<DocumentSnapshot> task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                    eventCallback.accept(fromDocument(document));
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
    }
}
