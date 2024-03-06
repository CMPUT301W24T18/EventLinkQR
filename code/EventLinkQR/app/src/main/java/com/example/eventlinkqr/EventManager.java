package com.example.eventlinkqr;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** Class for managing database interaction for events */
public class EventManager extends Manager {
    /** The Firestore collection path for events */
    private static final String COLLECTION_PATH = "Events";

    /**
     * Check the given attendee into an event
     * @param attendeeName The name of the attendee to check in
     * @param eventId The id of the event to check into
     */
    public static Task<Void> checkIn(String attendeeName, String eventId) {
        return getCollection().document(eventId).collection("attendees").document(attendeeName).update("checkedIn", true);
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
    public static Event fromDocument(DocumentSnapshot document) {
        Event e =  new Event(
                document.get("name", String.class),
                document.get("description", String.class),
                document.get("category", String.class),
                document.get("dateAndTime", Timestamp.class),
                document.get("location", String.class),
                document.get("geoTracking", Boolean.class));
        e.setId(document.getId());
        return e;
    }

    /**
     * Get the events collection from firebase
     * @return The collection
     */
    private static CollectionReference getCollection() {
        return getFirebase().collection(COLLECTION_PATH);
    }
}
