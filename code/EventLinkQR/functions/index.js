const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationToEventAttendees = functions.firestore
    .document('/notifications_testing/{notificationId}')
    .onCreate(async (snapshot, context) => {
        // Extract information from the created notification
        const notificationData = snapshot.data();
        const eventId = notificationData.eventId;
        console.log(`Notification created for eventId: ${eventId}`);

        // Initialize an array to hold the FCM tokens
        let tokens = [];

        try {
            // Retrieve the event document from events_testing collection
            const eventDoc = await admin.firestore().collection('events_testing').doc(eventId).get();
            if (!eventDoc.exists) {
                console.log('No such event!');
                return;
            }
            const eventDocData = eventDoc.data();
            console.log(`Event document retrieved for eventId: ${eventId}`);

            // Check if attendees array exists
            if (eventDocData && eventDocData.attendees) {
                console.log(`Found ${eventDocData.attendees.length} attendees for eventId: ${eventId}`);
                // Retrieve tokens for each attendee
                const attendeesTokensPromises = eventDocData.attendees.map(async (userId) => {
                    const userDoc = await admin.firestore().collection('user_testing').doc(userId).get();
                    if (userDoc.exists && userDoc.data().token) {
                        console.log(`Token found for userId: ${userId}`);
                        tokens.push(userDoc.data().token);
                    } else {
                        console.log(`No token found for userId: ${userId}`);
                    }
                });
                // Wait for all promises to resolve
                await Promise.all(attendeesTokensPromises);
                console.log(`Total tokens collected: ${tokens.length}`);
            } else {
                console.log('No attendees array found in the event document.');
            }

            // Prepare and send notifications to all tokens

            
            console.log('This is notification data ' + notificationData);
            console.log('This is notification heading ' + notificationData.heading);
            console.log('This is notification description ' + notificationData.description);

            if (tokens.length > 0) {


                const messages = tokens.map(token => ({
                    token,
                    notification: {
                        title: notificationData.heading, // Assuming the notification data contains a title
                        body: notificationData.description, // Assuming the notification data contains a message
                    }

                }));


            console.log(messages.token)

                // Send a batch of messages
                const response = await admin.messaging().sendEach(messages);
                console.log('Successfully sent messages:', response);
            } else {
                console.log('No tokens to send notifications.');
            }
        } catch (error) {
            console.error('Error sending notification:', error);
        }
    });
