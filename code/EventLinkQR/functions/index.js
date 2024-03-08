
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationToEventAttendees = functions.firestore
    .document('/notifications_testing/{notificationId}')
    .onCreate(async (snapshot, context) => {
        const notificationData = snapshot.data();
        const eventId = notificationData.eventId;
        let tokens = [];

        try {
            const attendeesSnapshot = await admin.firestore()
                .collection('Events').doc(eventId)
                .collection('attendees').get();

            if (attendeesSnapshot.empty) {
                console.log('No attendees for this event!');
                return;
            }

            if (eventDocData && eventDocData.attendees) {
                const attendeesTokensPromises = eventDocData.attendees.map(async (userId) => {
                    const userDoc = await admin.firestore().collection('Attendees').doc(userId).get();

                    if (userDoc.exists && userDoc.data().fcmToken) {
                        const fcmToken = userDoc.data().fcmToken;
                        console.log(`Token found for userId: ${userId}`);
                        tokens.push(fcmToken);

                        await admin.firestore().collection('userNotifications').doc(fcmToken)
                            .set({
                                notifications: admin.firestore.FieldValue.arrayUnion({
                                    title: notificationData.heading,
                                    body: notificationData.description,
                                    eventId: eventId,
                                    timestamp: new Date(),
                                })
                            }, { merge: true });
                    } else {
                        console.log(`No token found for userId: ${userId}`);
                    }
                });
            }

            await Promise.all(attendeesTokensPromises);
            console.log(`Notifications stored for ${tokens.length} tokens.`);

            const messages = tokens.map(token => ({
                token,
                notification: {
                    title: notificationData.heading,
                    body: notificationData.description,
                }
            }));

            const response = await admin.messaging().sendAll(messages);
            console.log('Successfully sent messages:', response);
        } catch (error) {
            console.error('Error sending notification:', error);
        }
    });