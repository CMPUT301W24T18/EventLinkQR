// const functions = require('firebase-functions');
// const admin = require('firebase-admin');
// admin.initializeApp();

// exports.sendNotificationToEventAttendees = functions.firestore
//     .document('/notifications_testing/{notificationId}')
//     .onCreate(async (snapshot, context) => {
//         // Extract information from the created notification
//         const notificationData = snapshot.data();
//         const eventId = notificationData.eventId;
//         console.log(`Notification created for eventId: ${eventId}`);

//         // Initialize arrays to hold FCM tokens and userIds
//         let tokens = [];
//         let userIds = [];

//         try {
//             // Retrieve the event document from the events_testing collection
//             const eventDoc = await admin.firestore().collection('events_testing').doc(eventId).get();
//             if (!eventDoc.exists) {
//                 console.log('No such event!');
//                 return;
//             }
//             const eventDocData = eventDoc.data();
//             console.log(`Event document retrieved for eventId: ${eventId}`);

//             // Check if attendees array exists
//             if (eventDocData && eventDocData.attendees) {
//                 console.log(`Found ${eventDocData.attendees.length} attendees for eventId: ${eventId}`);
//                 // Retrieve tokens for each attendee and store userIds
//                 const attendeesTokensPromises = eventDocData.attendees.map(async (userId) => {
//                     const userDoc = await admin.firestore().collection('attendees_testing').doc(userId).get();
//                     if (userDoc.exists && userDoc.data().fcmToken) {
//                         console.log(`Token found for userId: ${userId}`);
//                         tokens.push(userDoc.data().fcmToken);
//                         userIds.push(userId); // Store userId for notification storage
//                     } else {
//                         console.log(`No token found for userId: ${userId}`);
//                     }
//                 });

//                 // Wait for all promises to resolve
//                 await Promise.all(attendeesTokensPromises);
//                 console.log(`Total tokens collected: ${tokens.length}`);
//             } else {
//                 console.log('No attendees array found in the event document.');
//             }

//             console.log('This is notification data ' + notificationData);
//             console.log('This is notification heading ' + notificationData.heading);
//             console.log('This is notification description ' + notificationData.description);

//             if (tokens.length > 0) {
//                 const messages = tokens.map(token => ({
//                     token,
//                     notification: {
//                         title: notificationData.heading,
//                         body: notificationData.description,
//                     }
//                 }));

//                 console.log(messages.token);

//                 // Store the notification for each attendee
//                 const storagePromises = userIds.map(userId => {
//                     return admin.firestore().collection('userNotifications').doc(userId)
//                         .set({
//                             notifications: admin.firestore.FieldValue.arrayUnion({
//                                 title: notificationData.heading,
//                                 body: notificationData.description,
//                                 eventId: eventId,
//                                 timestamp: new Date(), // Use JavaScript Date object for the timestamp
//                             })
//                         }, { merge: true });
//                 });

//                 await Promise.all(storagePromises);

//                 // Send a batch of messages
//                 const response = await admin.messaging().sendAll(messages); // Consider using sendAll for better performance
//                 console.log('Successfully sent messages:', response);
//             } else {
//                 console.log('No tokens to send notifications.');
//             }
//         } catch (error) {
//             console.error('Error sending notification:', error);
//         }
//     });

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
                    const userDoc = await admin.firestore().collection('attendees_testing').doc(userId).get();
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
