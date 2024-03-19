package com.example.eventlinkqr;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Class for managing database interaction for events */
public class EventManager extends Manager {
    /**
     * The Firestore collection path for events
     */
    private static final String COLLECTION_PATH = "Events";

    /**
     * Check the given attendee into an event with no location
     *
     * @param attendeeName The name of the attendee to check in
     * @param eventId      The id of the event to check into
     */
    public static Task<Void> checkIn(String uuid, String attendeeName, String eventId) {
        Map<String, Object> attendee = new HashMap<>();
        attendee.put("name", attendeeName);
        attendee.put("checkedIn", true);
        return getCollection().document(eventId).collection("attendees").document(uuid).set(attendee);
    }

    /**
     * Check the given attendee into an event with a location
     *
     * @param attendeeName The name of the attendee to check in
     * @param eventId      The id of the event to check into
     * @param location     The location of the check-in
     */
    public static Task<Void> checkIn(String uuid, String attendeeName, String eventId, LatLng location) {
        Map<String, Object> attendee = new HashMap<>();
        attendee.put("name", attendeeName);
        attendee.put("checkedIn", true);
        attendee.put("location", new GeoPoint(location.latitude, location.longitude));
        return getCollection().document(eventId).collection("attendees").document(uuid).set(attendee);
    }

    /**
     * Check the given attendee into an event with a location
     *
     * @param uuid         The uuid of the attendee to check in
     * @param attendeeName The name of the attendee to check in
     * @param eventId      The id of the event to check into
     */
    public static Task<Void> signUp(String uuid, String attendeeName, String eventId) {
        Map<String, Object> attendee = new HashMap<>();
        attendee.put("name", attendeeName);
        attendee.put("checkedIn", false);
        return getCollection().document(eventId).collection("attendees").document(uuid).set(attendee);
    }

    /**
     * Add a callback to changes in the Events
     *
     * @param organizer     The organizer to filter events on
     * @param eventCallback The callback to be invoked when the events change
     */
    public static void addEventSnapshotCallback(String organizer, Consumer<List<Event>> eventCallback) {
        getCollection().whereEqualTo("organizer", organizer).addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }

            if (querySnapshots != null) {
                eventCallback.accept(querySnapshots.getDocuments().stream().map(d -> EventManager.fromDocument(d, null)).collect(Collectors.toList()));
            }
        });
    }


    /**
     * Add a callback to changes in the Events the user is signed up to
     * @param userID the user's id
     * @param eventCallback The callback to be invoked when the events change
     */
    public static void addSignedUpEventsSnapshotcallback(String userID, Consumer<List<Event>> eventCallback) {
        List<Event> events = new ArrayList<>();
        getCollection().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Check the list opf attendees for each event and see if the userId is one of them
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                    getCollection().document(doc.getId()).collection("attendees").document(userID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if(documentSnapshot.exists()){
                                    events.add(EventManager.fromDocument(doc, null));
                                    eventCallback.accept(events);
                                }
                            });
                }
            }
        });
    }


    /**
     * Add a callback to changes in the Events
     *
     * @param eventCallback The callback to be invoked when the events change
     */
    public static void addAllEventSnapshotCallback(Consumer<List<Event>> eventCallback) {
        getCollection().addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }

            if (querySnapshots != null) {
                eventCallback.accept(querySnapshots.getDocuments().stream().map(d -> EventManager.fromDocument(d, null)).collect(Collectors.toList()));
            }
        });
    }
    /**
     * Add a callback to changes in the event attendees.
     *
     * @param eventName        Event to get attendees for
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
     *
     * @param eventName        Event to get attendees for
     * @param checkedIn        Filter on checked-in / not-checked-in attendees
     * @param attendeeCallback The callback to be invoked when the event attendees change
     */
    public static void addEventAttendeeSnapshotCallback(String eventName, boolean checkedIn, Consumer<List<String>> attendeeCallback) {
        getCollection().document(eventName).collection("attendees").whereEqualTo("checkedIn", checkedIn).addSnapshotListener((querySnapshots, error) -> {
            // add all attendees that have checked in
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
     * Add a callback to changes in the event attendees. Will return the number of checked in attendees and the total number of attendees (those who have signed up)
     *
     * @param eventName
     * @param attendeeCountCallback
     */
    public static void addEventCountSnapshotCallback(String eventName, Consumer<int[]> attendeeCountCallback) {
        getCollection().document(eventName).collection("attendees").addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }

            if (querySnapshots != null) {
                int checkedInCount = (int) querySnapshots.getDocuments().stream().filter(d -> d.getBoolean("checkedIn")).count();
                int totalAttendees = querySnapshots.size();
                attendeeCountCallback.accept(new int[]{checkedInCount, totalAttendees});
            } else {
                Log.d("addEventCountSnapshotCallback", "No attendees found");
                attendeeCountCallback.accept(new int[]{0, 0});
            }
        });
    }

    /**
     * Add a callback to changes in the event attendees. Will return the new location list of attendees
     * @param eventName The event id
     * @param locationCallback The callback to be invoked when the event attendees change
     */
    public static void addEventLocationSnapshotCallback(String eventName, Consumer<ArrayList<LatLng>> locationCallback) {
        getCollection().document(eventName).collection("attendees").addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }

            if (querySnapshots != null) {
                List<GeoPoint> geoPoints = querySnapshots.getDocuments().stream().map(d -> d.getGeoPoint("location")).filter(Objects::nonNull).collect(Collectors.toList());
                ArrayList<LatLng> locations = new ArrayList<>();
                for (GeoPoint gp : geoPoints) {
                    locations.add(new LatLng(gp.getLatitude(), gp.getLongitude()));
                }
                Log.d("addEventLocationSnapshotCallback", "Locations: " + locations);
                locationCallback.accept(locations);
            } else {
                Log.d("addEventLocationSnapshotCallback", "No attendees found");
                locationCallback.accept(new ArrayList<>());
            }
        });
    }

    /**
     * Create an event from the document
     *
     * @param document The document to generate the event from
     * @return The event
     */
    private static Event fromDocument(DocumentSnapshot document, QuerySnapshot attendees) {
        Event e = new Event(
                document.get("name", String.class),
                document.get("description", String.class),
                document.get("category", String.class),
                document.get("dateAndTime", Timestamp.class),
                document.get("location", String.class),
                document.get("geoTracking", Boolean.class));
        if (attendees != null) {
            e.setCheckedInAttendeesCount(attendees.size());
        }
        e.setId(document.getId());

        /**
         * Make sure that the document has geoTracking enabled and that the checkInLocations field is not null
         * and that the checkInLocations field is not empty and that the type of the checkInLocations field is
         * a list of GeoPoints
         */
        ArrayList<com.google.android.gms.maps.model.LatLng> locations = new ArrayList<>();
        if (Boolean.TRUE.equals(document.getBoolean("geoTracking"))) {
            if (attendees != null) {
                List<GeoPoint> geoPoints = attendees.getDocuments().stream().map(d -> d.getGeoPoint("location")).filter(Objects::nonNull).collect(Collectors.toList());
                Log.d("EventManager", "GeoPoints: " + geoPoints);
                if (geoPoints != null && !geoPoints.isEmpty()) {
                    // Convert each GeoPoint to a LatLng and add to the locations list
                    for (GeoPoint gp : geoPoints) {
                        locations.add(new LatLng(gp.getLatitude(), gp.getLongitude()));
                    }
                }
                e.setCheckInLocations(locations);
            }

        }

        return e;
    }

    /**
     * Get the events collection from firebase
     *
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
        getCollection().document(eventId).get().addOnCompleteListener((Task<DocumentSnapshot> task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                    getCollection().document(eventId).collection("attendees").get().addOnSuccessListener(q -> {
                        eventCallback.accept(fromDocument(document, q));
                    });
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
    }

    /**
     * Adds a new event to the database
     *
     * @param newEvent  the new event to be added to the database
     * @param organizer the organizer of the event
     * @param customQR  (optional) encoded text for the qr code
     */
    public static void createEvent(Event newEvent, String organizer, String customQR) {
        HashMap<String, Object> newEventData = new HashMap<>();
        newEventData.put("name", newEvent.getName());
        newEventData.put("description", newEvent.getDescription());
        newEventData.put("category", newEvent.getCategory());
        newEventData.put("location", newEvent.getLocation());
        newEventData.put("dateAndTime", newEvent.getDate());

        newEventData.put("geoTracking", newEvent.getGeoTracking());
        newEventData.put("organizer", organizer);

        getCollection().add(newEventData)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();
                    String codeText = customQR;
                    if (codeText == null) {
                        codeText = "eventlinkqr:" + eventId;
                    }
                    // Automatically generate a QR Code for now. In the future support uploading custom.
                    QRCodeManager.addQRCode(codeText, QRCode.CHECK_IN_TYPE, eventId);
                    Log.e("Firestore", "Event " + newEvent.getName() + " by " + organizer + " successfully added");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Event failed to be added");
                });
    }
}