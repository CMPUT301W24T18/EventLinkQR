const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationToEventAttendees = functions.firestore
    .document('/Notifications/{eventId}')
    .onUpdate(async (change, context) => {
        const notificationData = change.after.data(); // Get the state of the document after the change
        const eventId = context.params.eventId;
        let tokens = [];

        // Assuming the update includes adding a notification to the notifications array, get the last notification
        const lastNotification = notificationData.notifications[notificationData.notifications.length - 1];

        console.log('Updated notification for event ID:', eventId);
        console.log('The heading of the updated notification is:', lastNotification.heading);
        console.log('The description of the updated notification is:', lastNotification.description);

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
                            })
                        }, { merge: true });
                } else {
                    console.log(`No FCM token found for user ID: ${userId}`);
                }
            });

            await Promise.all(attendeesTokensPromises);
            console.log(`Notifications updated for ${tokens.length} attendees.`);

            // Send updated notifications via FCM
            const messages = tokens.map(token => ({
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
    });
