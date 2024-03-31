package com.example.eventlinkqr;


import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class MilestoneManager extends Manager {

    private static final String COLLECTION_PATH = "Milestones";
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

    public static void checkForCheckInMilestone(String eventId, String organizerId) {
        Log.d("MilestoneManager", "Checking for checkIn milestone for eventId: " + eventId + " and organizerId: " + organizerId);
        getFirebase().collection("Events")
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

                                NotificationManager notificationManager = new NotificationManager();
                                notificationManager.sendNotificationToDatabase(eventId, "Check-in Milestone", event.getName() + " has reached a check-in milestone of " + checkedInAttendeesCount + " attendees!");
                            }
                        }
                    }
                });


    }

    public static void checkForSignUpMilestone(String eventId, String organizerId) {
        getFirebase().collection("Events")
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

                                NotificationManager notificationManager = new NotificationManager();
                                notificationManager.sendNotificationToDatabase(eventId, "Check-in Milestone", event.getName() + " has reached a sign-up milestone of " + signedUpAttendeesCount + " attendees!");
                            }
                        }
                    }
                });
    }

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
