# Project Part 2 User Stories

DISCLAIMER: This document is for the initial creation of requirements and will **NOT** be updated throughout the rest of the project. You have been warned.

## Organizer:

- US 01.01.01 As an organizer, I want to create a new event and generate a unique QR code for attendee check-ins.
	- Requirements:
		- The system should allow the user to create an event.
		- The system should generate a unique QR code upon creating an event.
		- The generated QR code should be associated with the created event.
	- Tasks:
		- Implement event creation
		- Implement QR code generation
	- Acceptance Criteria:
		- Upon opening the app, a user can create an event in 3 clicks.
		- The system generates and displays a QR code after the user creates an event.
		- The system generates unique QR codes for X events.
	- Questions?

- US 01.01.02 As an organizer, I want the option to reuse an existing QR code for attendee check-ins.
	- Requirements:
		- The system should allow the user to specify a QR code to be used for a new event by scanning one.
		- The system should not generate a new QR code if the user provides one.
	- Tasks:
		- Implement user QR code upload.
	- Acceptance Criteria:
		- When creating an event, a user can optionally click one button to scan a QR code.
		- The system does not generate a QR code for events 
	- Questions?
		- What does "reuse an existing QR code" mean specifically?
			- Dr. Hindle answered this one here: https://eclass.srv.ualberta.ca/mod/forum/discuss.php?d=2446275
		- Does an existing QR code include one that was used for a previous event?

- US 01.02.01 As an organizer, I want to view the list of attendees who have checked in to my event.
	- Requirements:
		- The system should track when a user checks into an event.
		- The system should provide organizers with an interface with a list of attendees checked in to their events.
	- Tasks:
		- Implement check-in tracking.
		- Implement a list view of attendees.
	- Acceptance Criteria:
		- Upon opening the app, an organizer can view a list of attendees at their event.

- US 01.03.01 As an organizer, I want to send notifications to all attendees through the app.
	- Requirements:
		- The system should allow organizers to send notifications.
		- The system should allow attendees to view a list of notifications they have received.
		- The system should display the name of the event that a message is for.
		- The system should notify an attendee when they receive a notification.
	- Tasks:
		- Implement a notification system (back-end)
		- Implement notification send interface
		- Implement received notification interface.
	- Acceptance Criteria:
		- An organizer can compose a message to all attendees of a given event.
		- The name of the event that the message is for is visible when composing a message.
		- An organizer can view previously sent messages.
	- Questions?
		- Presumably this is on a per event basis. One organizer can have many events?

- US 01.04.01 As an organizer, I want to upload an event poster to provide visual information to attendees.
	- Requirements:
		- The system should support image uploads.
		- The system should support storing a poster image for each event.
	- Tasks:
		- Implement image uploads.
		- Implement association between image and event.
		- Implement UI for viewing an events poster.
	- Acceptance criteria:
		- An organizer can optionally provide a poster image when creating an event.
		- An organizer can add or remove a poster for an existing event.
	- Questions:
		- Is displaying the image out of the scope of the story?

- US 01.05.01 As an organizer, I want to track real-time attendance and receive alerts for important milestones.
	- Requirements:
		- The system should support real-time (no manual refresh) updates for attendance.
		- The system should support specified milestones pertaining to event stats.
		- The system should support notifications triggered by the system (rather than a user).
	- Tasks:
		- Implement real-time model updates for attendance.
		- Implement specified milestones.
		- Implement event triggered notifications.
	- Acceptance Criteria:
		- The system updates attendance on the organizers interface without any user interaction.
		- The system updates attendance within 5 seconds.
		- The system allows users to specify milestones.
		- The system notifies the organizer when a milestone is achieved.
	- Questions:
		- What are milestones? What are they based on? Are they hardcoded, user specified? is there a default?
		- Does attendance include a list of attendees? or just a count?

- US 01.06.01 As an organizer, I want to share a generator QR code image to other apps so I can email or update other documents with the QR code.
	- Requirements:
		- The system supports the android "share" functionality https://developer.android.com/training/sharing/send.
	- Tasks:
		- Implement Android share functionality when viewing a QR code.
	- Acceptance Criteria:
		- When an organizer views a QR code, their is a option to share it.
		- Sharing a QR code via email produces a scannable image for the recipient.
	- Questions:
		- What does update other documents mean?

- US 01.07.01 As an organizer, I want to create a new event and generate a unique promotion QR code that links to the event description and event poster in the app.
	- Requirements:
		- The system differentiates between check-in QR codes and promotional QR codes.
		- The system notifies a user when a QR code is invalid.
		- Promotional QR codes are unique.
		- The system produces QR codes that link to the event details in the app.
	- Tasks:
		- Implement linking to event details.
		- Implement promotional QR code generation.
	- Acceptance Criteria:
		- Scanning a promotional QR code navigates the user to the details of an event.
		- If a user attempts to check in with a promotional QR code, they are notified that it is invalid.
	- Questions:
		- Where would a user find check in and promotional QR codes respectively?

- US 01.08.01 As an organizer, I want to see on a map where users are checking in from.   
	- Requirements:
		- The system should support collecting geolocation data.
		- The system should allow attendees to opt out of sharing geolocation data.
		- The system should provide a map UI for viewing user check in locations.
	- Tasks:
		- Implement location data collection.
		- Implement map UI. 
	- Acceptance Criteria:
		- There is a map UI visible by organizers.
		- Check locations are visible on the map.
		- An organizer cannot see the check-in location from users who have opted out.
	- Questions:

- US 01.09.01 As an organizer, I want to see how many times an attendee has checked into an event.  
	- Requirements:
		- The system should support the same user checking in to an event multiple times.
		- The system should provide the organizer a means of viewing how many times an attendee has checked in.
	- Tasks:
		- Implement displaying number of times a user has checked in.
	- Acceptance Criteria:
		- When an organizer views an attendee, they should see how many times they have checked in.
	
## Attendee:

- US 02.01.01 As an attendee, I want to quickly check into an event by scanning the provided QR code.
	- Requirements:
		- The system allows the user to check in to an event by scanning a QR code.
	- Tasks:
		- Integrate camera support (android)
		- Implement check in from QR code scan
	- Questions:
		- Can I scan straight from the camera app?

- US 02.02.01 As an attendee, I want to upload a profile picture for a more personalized experience.
	- Requirements:
		- The system supports image uploads.
		- The system has a profile associated with the user.
		- The system has a picture associated with a user profile.
	- Tasks:
		- Implement image uploads (duplicate)
		- Implement user profiles.
	- Acceptance criteria:
		- As an attendee, a profile is created when I check in if I don't have one already.
		- As an attendee, I can upload a new profile picture.

- US 02.02.02 As an attendee, I want to remove profile pictures if need be.
	- Requirements:
		- The system supports removing profile images.
		- Tasks:
			- Implement removing profile pictures.
		- Acceptance criteria:
			- Attendees can remove profile pictures.
			- When a profile picture is removed, the user's profile picture is reverted to the deterministically generated one.
	- Questions:
		- If I remove my profile image, is it reverted to the deterministic generated one?

- US 02.02.03 As an attendee, I want to update information such as name, homepage, and contact information on my profile.  
	- Requirements:
		- The system only requires a username.
		- The system supports updating profile info.
		- The system displays up to date profile information.
	- Tasks:
		- Implement API for updating user profiles.
	- Acceptance Criteria:
		- When an account is created, just a username is required.
		- Attendees are able to view and updated their profile.
		- When a user's profile is updated, their profile view shows accurate and up to date information.
- Questions:
	- Is just username okay?

- US 02.03.01 As an attendee, I want to receive push notifications with important updates from the event organizers.
	- Requirements:
		- The system supports push notifications https://developer.android.com/develop/ui/views/notifications
		- The system creates push notifications when an organizer sends an update.
		- The system should have an interface for the user to view all of their notifications.
		- The system should have a way of denoting updates as read or unread for the user.
	- Tasks:
		- Implement push notifications.
		- Implement message UI (can be similar to the organizer)
	- Acceptance Criteria:
		- Attendees receive push notifications from organizers for events their are attending.
		- When viewing a notification, the attendee can see which event it pertains to.
		- When attendees read a message from an organizer, it is no longer marked as new / unread.

- US 02.04.01 As an attendee, I want to view event details and announcements within the app.
	- Requirements:
		- The system supports viewing event details for.
		- The system supports viewing announcements for an event.
	- Tasks:
		- Implement announcements for an event.
		- Implement UI for viewing event details.
	- Questions:
		- What event details can I see as an attendee? only what I have checked in to?

- US 02.05.01 As an attendee, I want my profile picture to be deterministically generated from my profile name if I haven't uploaded an profile image yet. 
	- Requirements:
		- The system has an algorithm for deterministically generating profile images.
		- The system is incapable of generating hateful images / is restricted to a predictable class of outcomes.
		- The system automatically assigns the generated picture to a user if none is required.
	- Tasks:
		- Implement deterministic avatar creation algorithm.
		- Automatically assign generated avatar when none is provided.
	- Acceptance Criteria:
		- Generating images from the same name produces the same image (deterministic).
		- When creating a new profile, a generated picture is used if I don't provide one.

- US 02.06.01 As an attendee, I do not want to login to the app. No username, no password.  
	- Requirements:
		- The app saves the current profile of the user on the device.
		- The system does not require authentication to use the app as an attendee.
	- Tasks:
		- Implement saving the account to the device.
	- Acceptance Criteria:
		- When I open the app as an attendee, I can go directly to checking in.
		- When I open the app as an attendee, I can go directly to my profile.

## Both:

- US 03.02.01 As a user, I want the option to enable or disable geolocation tracking for event verification.
	- Requirements:
		- The system provides an option to users to opt out of geolocation tracking.
		- The system does not track any location data when opted out.
	- Tasks:
		- Implement opt out of geolocation.
	- Acceptance Criteria:
		- Organizers cannot see locations of attendees who have opted out.
		- The app does not have access to location data when users have opted.

## Administrator:

- US 04.01.01 As an administrator, I want to be able to remove events.

- US 04.02.01 As an administrator, I want to be able to remove profiles.

- US 04.03.01 As an administrator, I want to be able to remove images.

- US 04.04.01 As an administrator, I want to be able to browse events.

- US 04.05.01 As an administrator, I want to be able to browse profiles.

- US 04.06.01 As an administrator, I want to be able to browse images.
