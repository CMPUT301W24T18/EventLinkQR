package com.example.eventlinkqr;

import android.util.Log;

import com.google.firebase.firestore.Query;

import java.util.function.Consumer;

public class MilestoneManager extends Manager {

    private static final String COLLECTION_PATH = "Milestones";

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

}
