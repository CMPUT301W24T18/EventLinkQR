package com.example.eventlinkqr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
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
    public static void checkIn(Context context, String uuid, String attendeeName, String eventId) {
        Map<String, Object> attendee = new HashMap<>();
        EventManager.getCheckinCount(eventId, uuid, checkInCount -> {
            //increment the checkin count for the attendee
            int newCheckInCount = checkInCount + 1;
            attendee.put("name", attendeeName);
            attendee.put("checkedIn", true);
            attendee.put("checkInCount", newCheckInCount);
            getCollection().document(eventId).collection("attendees").document(uuid).set(attendee)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(context, "Checked In", Toast.LENGTH_SHORT).show();
                        getCollection().document(eventId).update("checkedInAttendeesCount", FieldValue.increment(1));
                        EventManager.getOrganizerId(eventId, organizerId -> {
                            MilestoneManager.checkForCheckInMilestone(context, eventId, organizerId);
                        });
                    }else{
                        Toast.makeText(context, "Failed to check in", Toast.LENGTH_SHORT).show();
                    }
                });
        });
    }

    /**
     * Check the given attendee into an event with a location
     *
     * @param attendeeName The name of the attendee to check in
     * @param eventId      The id of the event to check into
     * @param location     The location of the check-in
     */
    public static void checkIn(Context context, String uuid, String attendeeName, String eventId, LatLng location) {
        Map<String, Object> attendee = new HashMap<>();
        EventManager.getCheckinCount(eventId, uuid, checkInCount -> {
            //increment the checkin count for the attendee
            int newCheckInCount = checkInCount + 1;
            attendee.put("name", attendeeName);
            attendee.put("checkedIn", true);
            attendee.put("checkInCount", newCheckInCount);
            attendee.put("location", new GeoPoint(location.latitude, location.longitude));
            getCollection().document(eventId).collection("attendees").document(uuid).set(attendee)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(context, "Checked In", Toast.LENGTH_SHORT).show();
                        getCollection().document(eventId).update("checkedInAttendeesCount", FieldValue.increment(1));
                        EventManager.getOrganizerId(eventId, organizerId -> {
                            MilestoneManager.checkForCheckInMilestone(context, eventId, organizerId);
                        });
                    }else{
                        Toast.makeText(context, "Failed to check in", Toast.LENGTH_SHORT).show();
                    }
                });
        });
    }

    /**
     * Check the given attendee into an event with a location
     *
     * @param uuid         The uuid of the attendee to sign up
     * @param attendeeName The name of the attendee to check in
     * @param eventId      The id of the event to check into
     * @param checkingIn   whether we need to check in immediately after signing up or not
     * @param location     the location in case the method is getting called from checkin with location on
     */
    public static void signUp(Context context, String uuid, String attendeeName, String eventId, boolean checkingIn, LatLng location) {
        EventManager.getMaxAttendees(eventId, maxAttendees -> {
            EventManager.getNumAttendees(eventId, attendeeCount -> {
                // check if the max number of attendees was reached
                if(attendeeCount >= maxAttendees){
                    Toast.makeText(context, "This event is Full", Toast.LENGTH_SHORT).show();
                }else{
                    // add the user to the list
                    Map<String, Object> attendee = new HashMap<>();
                    attendee.put("name", attendeeName);
                    attendee.put("checkedIn", false);
                    attendee.put("checkInCount", 0);
                    getCollection().document(eventId).collection("attendees").document(uuid).set(attendee)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){

                                Toast.makeText(context, "Signed Up", Toast.LENGTH_SHORT).show();

                                EventManager.getOrganizerId(eventId, organizerId -> {
                                    getCollection().document(eventId).update("signedUpCount", FieldValue.increment(1));
                                    MilestoneManager.checkForSignUpMilestone(context, eventId, organizerId);
                                });

                                // checkin if the method was called form the checkIn method
                                if(checkingIn && location != null){
                                    EventManager.checkIn(context, uuid, attendeeName, eventId, location);
                                }else if(checkingIn){
                                    EventManager.checkIn(context, uuid, attendeeName, eventId);
                                }
                            }else{
                                Toast.makeText(context, "Failed to sign up", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            });
        });
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
        getCollection().addSnapshotListener((querySnapshots, error) -> {
            if(querySnapshots != null){
                // Check the list opf attendees for each event and see if the userId is one of them
                for(DocumentSnapshot doc : querySnapshots.getDocuments()){
                    getCollection().document(doc.getId()).collection("attendees").document(userID)
                            .addSnapshotListener((value, error1) -> {
                        if(value != null && value.exists()){
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
     * @param userID the user's uuid
     * @param eventCallback The callback to be invoked when the events change
     */
    public static void addAllEventSnapshotCallback(String userID, Consumer<List<Event>> eventCallback) {
        List<Event> events = new ArrayList<>();
        // gets all the events the user is not the organiser of
        getCollection().whereNotEqualTo("organizer", userID).addSnapshotListener((querySnapshots, error) -> {
            if(querySnapshots != null){
                // Check the list opf attendees for each event and see if the userId is one of them
                for(DocumentSnapshot doc : querySnapshots.getDocuments()){
                    getCollection().document(doc.getId()).collection("attendees").document(userID)
                            .addSnapshotListener((value, error1) -> {
                                if(value != null && !value.exists()){
                                    events.add(EventManager.fromDocument(doc, null));
                                    eventCallback.accept(events);
                                }
                            });
                }
            }
        });
    }

    /**
     * Add a callback to changes in the event attendees.
     *
     * @param eventName        Event to get attendees for
     * @param attendeeCallback The callback to be invoked when the event attendees change
     */
    public static void addEventAttendeeSnapshotCallback(String eventName, Consumer<List<Attendees>> attendeeCallback) {
        getCollection().document(eventName).collection("attendees").addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (querySnapshots != null) {
                List<Attendees> attendees = new ArrayList<>();
                for(DocumentSnapshot doc : querySnapshots.getDocuments()){
                    Attendees attendee = doc.toObject(Attendees.class);
                    if(attendee != null) {
                        attendees.add(attendee);
                    }
                }
                attendeeCallback.accept(attendees);

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
    public static void addEventAttendeeSnapshotCallback(String eventName, boolean checkedIn, Consumer<List<Attendees>> attendeeCallback) {
        getCollection().document(eventName).collection("attendees").whereEqualTo("checkedIn", checkedIn).addSnapshotListener((querySnapshots, error) -> {
            // add all attendees that have checked in
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (querySnapshots != null) {
                List<Attendees> attendees = new ArrayList<>();
                for(DocumentSnapshot doc : querySnapshots.getDocuments()){
                    Attendees attendee = doc.toObject(Attendees.class);
                    if(attendee != null) {
                        attendees.add(attendee);
                    }
                }
                attendeeCallback.accept(attendees);

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
//                document.get("checkedInCount", Integer.class),
//                document.get("signedUpCount", Integer.class));
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
     * Gets the organizer's ID for an event.
     *
     * @param eventId The ID of the event.
     * @param eventCallback The function to call with the organizer's ID.
     */
    public static void getOrganizerId(String eventId, Consumer<String> eventCallback) {
        getCollection().document(eventId).get().addOnCompleteListener((Task<DocumentSnapshot> task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                    eventCallback.accept(document.getString("organizer"));
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
    public static void createEvent(Context context, Event newEvent, String organizer, String customQR, String customPromotionalQR, int maxAttendees, Bitmap poster) {
        HashMap<String, Object> newEventData = new HashMap<>();
        newEventData.put("name", newEvent.getName());
        newEventData.put("description", newEvent.getDescription());
        newEventData.put("category", newEvent.getCategory());
        newEventData.put("location", newEvent.getLocation());
        newEventData.put("dateAndTime", newEvent.getDate());
        newEventData.put("maxAttendees", maxAttendees);
        newEventData.put("geoTracking", newEvent.getGeoTracking());
        newEventData.put("organizer", organizer);
        newEventData.put("signedUpCount", 0);
        newEventData.put("checkedInAttendeesCount", 0);

        newEventData.put("signedUpCount", newEvent.getSignedUpCount());
        newEventData.put("checkedInAttendeesCount", newEvent.getCheckedInAttendeesCount());

        getCollection().add(newEventData)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();
                    String codeText = customQR;
                    String promotionalText = customPromotionalQR;

                    // upload the poster if there is one
                    if(poster != null){
                        ImageManager.uploadPoster(context, eventId, poster);
                    }
                    if (codeText == null) {
                        codeText = "eventlinkqr:" + eventId;
                    }
                    if (promotionalText == null) {
                        promotionalText = "eventlinkqr:promotion:" + eventId;
                    }
                    // Automatically generate a QR Code for now. In the future support uploading custom.
                    QRCodeManager.addQRCode(codeText, QRCode.CHECK_IN_TYPE, eventId);
                    QRCodeManager.addQRCode(promotionalText, QRCode.PROMOTIONAL_TYPE, eventId);
                    Log.e("Firestore", "Event " + newEvent.getName() + " by " + organizer + " successfully added");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Event failed to be added");
                });
    }

    /**
     * deletes an event from the database
     * @param eventId the event to be deleted
     */
    public static void deleteEvent(String eventId){
        getCollection().document(eventId).delete()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d("Firestore", "event " + eventId + " deleted");
                    }else{
                        Log.d("Firestore", "delete failed with ", task.getException());
                    }
                });
    }

    /**
     * Adds a new event to the database
     *
     * @param editedEvent the event containing the new values
     */
    public static void editEvent(Event editedEvent) {
        HashMap<String, Object> editedEventData = new HashMap<>();
        editedEventData.put("name", editedEvent.getName());
        editedEventData.put("description", editedEvent.getDescription());
        editedEventData.put("category", editedEvent.getCategory());
        editedEventData.put("location", editedEvent.getLocation());
        editedEventData.put("dateAndTime", editedEvent.getDate());
        editedEventData.put("geoTracking", editedEvent.getGeoTracking());

        getCollection().document(editedEvent.getId()).update(editedEventData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "event succesfully edited");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Event failed to be edited");
                });
    }


    /**
     * Checks if the user is signed up to the event
     *
     * @param eventId the event id
     * @param uuid the user's uuid
     * @param signedUp consumer to receive the boolean result
     */
    public static void isSignedUp(String uuid, String eventId, Consumer<Boolean> signedUp){
        getCollection().document(eventId).collection("attendees").document(uuid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    signedUp.accept(true);
                }else{
                    signedUp.accept(false);
                }
            }else{
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
    }

    /**
     * gets the attendee limit for the event, set by the organizer
     *
     * @param eventId the event's id
     * @param signedUp the consumer that will receive the value
     */
    public static void getMaxAttendees(String eventId, Consumer<Integer> signedUp){
        getCollection().document(eventId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                signedUp.accept(Objects.requireNonNull(document.getLong("maxAttendees")).intValue());
            }else{
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
    }

    /**
     * finds the amount of times the user has checked into the event
     * @param eventId the event's id
     * @param uuid the user's uuid
     * @param count the consumer to get the count
     */
    public static void getCheckinCount(String eventId, String uuid, Consumer<Integer> count){
        getCollection().document(eventId).collection("attendees").document(uuid).get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                   DocumentSnapshot document = task.getResult();
                   count.accept(Objects.requireNonNull(document.getLong("checkInCount")).intValue());
                }
            });
    }

    /**
     * finds the number of attendees currently signed up to the event
     * @param eventId the event's id
     * @param count the consumer for the count of attendees
     */
    public static void getNumAttendees(String eventId, Consumer<Integer> count){
        getCollection().document(eventId).collection("attendees").get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    count.accept(task.getResult().size());
                }
            });
    }
}