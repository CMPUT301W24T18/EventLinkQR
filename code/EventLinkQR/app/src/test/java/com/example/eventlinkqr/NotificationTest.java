package com.example.eventlinkqr;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

public class NotificationTest {

    private Notification notification;

    @BeforeEach
    public void setUp() {
        // Initialize a Notification object before each test
        notification = new Notification("Test Title", "Test Description", "1h");
    }

    @Test
    public void testNotificationConstructor() {
        // Test the constructor
        assertEquals("Test Title", notification.getTitle());
        assertEquals("Test Description", notification.getDescription());
        assertEquals("1h", notification.getTimeSinceNotification());
    }

    @Test
    public void testSetTitle() {
        // Test setTitle method
        notification.setTitle("New Title");
        assertEquals("New Title", notification.getTitle());
    }

    @Test
    public void testSetDescription() {
        // Test setDescription method
        notification.setDescription("New Description");
        assertEquals("New Description", notification.getDescription());
    }

    @Test
    public void testSetTimeSinceNotification() {
        // Test setTimeSinceNotification method
        notification.setTimeSinceNotification("2h");
        assertEquals("2h", notification.getTimeSinceNotification());
    }
}
