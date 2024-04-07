const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationToEventAttendees = functions.firestore
    .document('/Notifications/{eventId}')
    .onWrite(async (change, context) => {

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
            try {
                const eventDoc = await admin.firestore().collection('Events').doc(eventId).get();
                if (!eventDoc.exists) {
                    console.log('Event document not found:', eventId);
                    return null;
                }

                const organizerId = eventDoc.data().organizer;
                let recipients = [organizerId];
                
                const attendeesSnapshot = await admin.firestore().collection('Events').doc(eventId).collection('attendees').get();
                attendeesSnapshot.forEach(doc => {
                    recipients.push(doc.id);
                });

                for (const userId of recipients) {

                    const userDoc = await admin.firestore().collection('Users').doc(userId).get();
                    if (!userDoc.exists || !userDoc.data().fcmToken) {
                        console.log(`FCM token not found for user: ${userId}`);
                        continue;
                    }

                    const fcmToken = userDoc.data().fcmToken;

                    const eventDoc = await admin.firestore().collection('Events').doc(eventId).get();
                    if (!eventDoc.exists || !eventDoc.data().name) {
                        console.log(`Event name not found for event: ${eventId}`);
                        continue;
                    }

                    const eventName = eventDoc.data().name

                    const message = {
                        token: fcmToken,
                        data: {
                            // Custom data that you want to send and handle in onMessageReceived
                            title: lastNotification.heading,
                            body: lastNotification.description,
                            eventId: eventId,
                            eventName: eventName,
                            
                            // You can add more key-value pairs as needed
                        }
                    };

                    await admin.messaging().send(message);
                    console.log(`Notification sent to user: ${userId}`);

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
                }
            } catch (error) {
                console.error('Error sending notification:', error);
                return null;
            }
        } 
        else {

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
    
                const eventDoc = await admin.firestore().collection('Events').doc(eventId).get();
                if (!eventDoc.exists || !eventDoc.data().name) {
                    console.log(`Event name not found for event: ${eventId}`);
                }

                const eventName = eventDoc.data().name
    

                if (uniqueTokens.length > 0) {
                    const multicastMessage = {
                        tokens: uniqueTokens,
                        data: {
                            // Custom data
                            title: lastNotification.heading,
                            body: lastNotification.description,
                            eventId: eventId,
                            eventName: eventName,
                        }
                    };

                try {
                    // Correcting the method call to reflect your original query
                    const response = await admin.messaging().sendEachForMulticast(multicastMessage);
                    console.log('Successfully sent messages:', response.successCount);
        
                    // Handling individual message responses
                    response.responses.forEach((resp, idx) => {
                        if (resp.success) {
                            console.log(`Message sent to token ${uniqueTokens[idx]}`);
                        } else {
                            console.error(`Failed to send message to token ${uniqueTokens[idx]}`, resp.error);
                        }
                    });
                } catch (error) {
                    console.error('Error sending multicast messages:', error);
                }
            } }
            catch (error) {
                console.error('Error sending updated notifications:', error.error);
            }
        }
    });