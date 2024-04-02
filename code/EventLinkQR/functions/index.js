const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationToEventAttendees = functions.firestore
    .document('/Notifications/{eventId}')
    .onWrite(async (change, context) => { // Changed from onUpdate to onWrite
        // The 'change' parameter contains two properties: .before and .after
        // You can check if the document was created by checking if 'change.before' exists
        const documentWasCreated = !change.before.exists;
        const notificationData = change.after.exists ? change.after.data() : null; // Check if the document still exists

        if (!notificationData) {
            console.log('Document was deleted');
            return null;
        }

        const eventId = context.params.eventId;
        let tokens = [];

        // Assuming the update includes adding a notification to the notifications array, get the last notification
        const lastNotification = notificationData.notifications[notificationData.notifications.length - 1];

        console.log('Updated notification for event ID:', eventId);
        console.log('The heading of the updated notification is:', lastNotification.heading);
        console.log('The description of the updated notification is:', lastNotification.description);
        console.log('Is the notification a Milestone???? ', lastNotification.isMilestone);

        if (lastNotification.isMilestone) {
            // Notify the event organizer instead of attendees
            try {
                const eventDoc = await admin.firestore().collection('Events').doc(eventId).get();
                if (!eventDoc.exists) {
                    console.log('Event document not found:', eventId);
                    return null;
                }

                const organizerId = eventDoc.data().organizer;
                const userDoc = await admin.firestore().collection('Users').doc(organizerId).get();

                if (!userDoc.exists || !userDoc.data().fcmToken) {
                    console.log(`FCM token not found for organizer: ${organizerId}`);
                    return null;
                }

                const fcmToken = userDoc.data().fcmToken;
                // Assuming the notification to send is the last one added to the array
                // const lastNotification = notificationData.notifications[notificationData.notifications.length - 1];

                const message = {
                    token: fcmToken,
                    notification: {
                        title: lastNotification.heading,
                        body: lastNotification.description,
                    }
                };

                await admin.messaging().send(message);
                console.log(`Notification sent to the event organizer: ${organizerId}`);
            } catch (error) {
                console.error('Error sending notification to organizer:', error);
                return null;
            }
        } else {
            // Existing logic to notify event attendees...
            // This is your current logic to handle notifications for attendees

            try {
                // Retrieve attendees UUIDs from the attendees subcollection of the Events collection
                const attendeesSnapshot = await admin.firestore()
                    .collection('Events').doc(eventId)
                    .collection('attendees').get();

                if (attendeesSnapshot.empty) {
                    console.log('No attendees for this event:', eventId);
                    return null; // Ensure to return null to terminate the function correctly
                }

                const attendeesTokensPromises = attendeesSnapshot.docs.map(async (attendeeDoc) => {
                    const userId = attendeeDoc.id; // UUID of the attendee
                    const userDoc = await admin.firestore().collection('Users').doc(userId).get();

                    if (userDoc.exists && userDoc.data().fcmToken) {
                        const fcmToken = userDoc.data().fcmToken;
                        console.log(`Token found for user ID: ${userId}`);
                        tokens.push(fcmToken);

                        // Update notifications in userNotifications collection for each user
                        await admin.firestore().collection('userNotifications').doc(userId)
                            .set({
                                notifications: admin.firestore.FieldValue.arrayUnion({
                                    title: lastNotification.heading,
                                    body: lastNotification.description,
                                    eventId: eventId,
                                    timestamp: new Date(),
                                    isRead: false,
                                })
                            }, { merge: true });
                    } else {
                        console.log(`No FCM token found for user ID: ${userId}`);
                    }
                });

                await Promise.all(attendeesTokensPromises);
                console.log(`Notifications updated for ${tokens.length} attendees. [Not unique]`);

                // New lines added to remove duplicate tokens:
                const uniqueTokens = [...new Set(tokens)];
                console.log(`Notifications updated for ${uniqueTokens.length} unique devices.`);


                // Send updated notifications via FCM
                const messages = uniqueTokens.map(token => ({
                    token,
                    notification: {
                        title: lastNotification.heading,
                        body: lastNotification.description,
                    }
                }));

                const response = await admin.messaging().sendAll(messages);
                console.log('Successfully sent messages:', response.successCount);
            } catch (error) {
                console.error('Error sending updated notifications:', error);
            }
        }


    });