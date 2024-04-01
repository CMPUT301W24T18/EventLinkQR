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


    exports.addAdminRole = functions.https.onCall((data, context) => {
        // check if request is made by an admin
        if (context.auth.token.admin !== true) {
            return { error: 'Only admins can add other admins, sucker' };
        }
    
        // get user and add custom claim (admin)
        return admin.auth().getUserByEmail(data.email)
            .then(user => {
                return admin.auth().setCustomUserClaims(user.uid, {
                    admin: true
                });
            })
            .then(() => {
                return {
                    message: `Success! ${data.email} has been made an admin.`
                };
            })
            .catch(err => {
                return err;
            });
    });

    
// Helper function to generate a random code
function generateRandomCode(length) {
    let result = '';
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    return result;
}

// Cloud Function to generate and store an admin code
exports.generateAdminCode = functions.https.onCall((data, context) => {
    // Optional: Check if the user requesting the code has permission
    if (!context.auth || !context.auth.token.admin) { // Adjust based on your auth setup
        throw new functions.https.HttpsError('permission-denied', 'Only admins can generate codes.');
    }

    const code = generateRandomCode(6); // Generate a 6-character code
    const expiresAt = admin.firestore.Timestamp.fromDate(new Date(Date.now() + 24 * 3600 * 1000)); // 24 hours from now

    // Store the code in Firestore
    return admin.firestore().collection('AdminCodes').doc(code).set({
        code,
        expiresAt,
    }).then(() => {
        return { code, expiresAt };
    }).catch(error => {
        throw new functions.https.HttpsError('unknown', error.message, error);
    });
});