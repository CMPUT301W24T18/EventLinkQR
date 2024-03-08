const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationToEventAttendees = functions.firestore
    .document('/notifications_testing/{notificationId}')
    .onCreate(async (snapshot, context) => {
        const notificationData = snapshot.data();
        const eventId = notificationData.eventId;

        // Initialize an array to hold FCM tokens
        let tokens = [];

        try {
            const eventDoc = await admin.firestore().collection('events_testing').doc(eventId).get();
            if (!eventDoc.exists) {
                console.log('No such event!');
                return;
            }
            const eventDocData = eventDoc.data();

            if (eventDocData && eventDocData.attendees) {
                const attendeesTokensPromises = eventDocData.attendees.map(async (userId) => {
                    const userDoc = await admin.firestore().collection('Attendees').doc(userId).get();
                    if (userDoc.exists && userDoc.data().fcmToken) {
                        const fcmToken = userDoc.data().fcmToken;
                        console.log(`Token found for userId: ${userId}`);
                        tokens.push(fcmToken);

                        // Store the notification under the FCM token document
                        await admin.firestore().collection('userNotifications').doc(fcmToken)
                            .set({
                                notifications: admin.firestore.FieldValue.arrayUnion({
                                    title: notificationData.heading,
                                    body: notificationData.description,
                                    eventId: eventId,
                                    timestamp: new Date(), // Use JavaScript Date object for the timestamp
                                })
                            }, { merge: true });
                    } else {
                        console.log(`No token found for userId: ${userId}`);
                    }
                });

                await Promise.all(attendeesTokensPromises);
                console.log(`Notifications stored for ${tokens.length} tokens.`);
            } else {
                console.log('No attendees array found in the event document.');
            }

            // Prepare notifications for FCM
            const messages = tokens.map(token => ({
                token,
                notification: {
                    title: notificationData.heading,
                    body: notificationData.description,
                }
            }));

            // Send a batch of messages
            const response = await admin.messaging().sendAll(messages);
            console.log('Successfully sent messages:', response);
        } catch (error) {
            console.error('Error sending notification:', error);
        }
    });
