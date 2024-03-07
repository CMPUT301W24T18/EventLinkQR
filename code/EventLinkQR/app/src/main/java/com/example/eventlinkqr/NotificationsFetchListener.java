package com.example.eventlinkqr;

import java.util.List;

public interface NotificationsFetchListener {
    void onNotificationsFetched(List<Notification> notifications);
    void onError(Exception e);
}