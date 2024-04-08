package com.example.eventlinkqr;

import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * manages the milestones communications between the database and the users
 */
public class MilestoneManager extends Manager {

    private static final String COLLECTION_PATH = "Milestones";
    private static final String EVENT_COLLECTION_PATH = "Events";
    private static final ArrayList<Integer> milestones = new ArrayList<>(Arrays.asList(1, 5, 10, 20, 50, 100, 200, 500, 1000));

    /**
     * Listen for changes in the milestones collection and call the callback function. A change means
     * some event has just reached a milestone. We filter by the organizerId and pull the most recent
     * milestone.
     *
     * @param organizerId
     * @param callback
     */
    public static void addMilestoneSnapshotCallback(String organizerId, Consumer<Milestone> callback) {
        Log.d("MilestoneManager", "Listening for milestone changes for organizerId: " + organizerId);
        getFirebase().collection(COLLECTION_PATH)
                .whereEqualTo("organizerId", organizerId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1) // We only care about the most recent milestone
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        System.err.println("Listen failed: " + e);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        Log.d("MilestoneManager", "Milestone snapshot received" + snapshots.getDocuments().get(0).getId());
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        Milestone latestMilestone = snapshots.getDocuments().get(0).toObject(Milestone.class);
                        callback.accept(latestMilestone);
                    } else {
                        System.out.println("No recent milestone found for organizerId: " + organizerId);
                    }
                });
    }

    /**
    * Checks if the number of attendees checked in for a given event has reached a predefined milestone.
    * If a milestone is reached, a new {@link Milestone} object is created and added to the database,
    * and a notification is sent out to highlight the achievement.
    * 
    * @param eventId The unique identifier for the event being checked.
    * @param organizerId The unique identifier for the organizer of the event.
    */
    public static void checkForCheckInMilestone(Context context, String eventId, String organizerId) {
        Log.d("MilestoneManager", "Checking for checkIn milestone for eventId: " + eventId + " and organizerId: " + organizerId);
        getFirebase().collection(EVENT_COLLECTION_PATH)
                .document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Event event = task.getResult().toObject(Event.class);
                        if (event != null) {
                            int checkedInAttendeesCount = event.getCheckedInAttendeesCount();
                            if (milestones.contains(checkedInAttendeesCount)) {
                                // Create a milestone object and add it to the database for record keeping purposes
                                Milestone milestone = new Milestone(eventId, organizerId, "checkIn", checkedInAttendeesCount, Timestamp.now());
                                addMilestone(milestone);

                                NotificationManager notificationManager = new NotificationManager(context);
                                notificationManager.sendNotificationToDatabase(eventId, "Check-in Milestone", event.getName() + " has reached a check-in milestone of " + checkedInAttendeesCount + " attendee" + (checkedInAttendeesCount > 1 ? "s!" : "!"), true);
                            }
                        }
                    }
                });
    }


    /**
    * Checks if the number of attendees signed up for a given event has reached a predefined milestone.
    * Upon reaching a milestone, it creates and stores a {@link Milestone} object in the database for
    * record-keeping and sends a notification to highlight the achievement.
    *
    * @param eventId The unique identifier for the event.
    * @param organizerId The unique identifier for the organizer of the event.
    */
    public static void checkForSignUpMilestone(Context context, String eventId, String organizerId) {
        getFirebase().collection(EVENT_COLLECTION_PATH)
                .document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Event event = task.getResult().toObject(Event.class);
                        if (event != null) {
                            int signedUpAttendeesCount = event.getSignedUpCount();
                            if (milestones.contains(signedUpAttendeesCount)) {
                                Milestone milestone = new Milestone(eventId, organizerId, "signUp", signedUpAttendeesCount, Timestamp.now());
                                Log.d("MilestoneManager", "OrganizerId: " + organizerId + " reached a milestone ");
                                addMilestone(milestone);

                                NotificationManager notificationManager = new NotificationManager(context);
                                notificationManager.sendNotificationToDatabase(eventId, "Sign-up Milestone", event.getName() + " has reached a sign-up milestone of " + signedUpAttendeesCount + " attendee" + (signedUpAttendeesCount > 1 ? "s!" : "!"), true);
                            }
                        }
                    }
                });
    }
    
    /**
    * Adds a milestone to the database.
    *
    * @param milestone The milestone to add.
    */
    public static void addMilestone(Milestone milestone) {
        getFirebase().collection(COLLECTION_PATH)
                .add(milestone)
                .addOnSuccessListener(documentReference -> {
                    Log.d("MilestoneManager", "Milestone added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("MilestoneManager", "Error adding milestone", e);
                });
    }
}
