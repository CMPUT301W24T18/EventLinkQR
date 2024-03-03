package com.example.eventlinkqr;

public class Event {
    private QRCode qr;
    private String name;
    private String description;
    private String category;
    private String date;
    private String location;
    private Boolean geoTracking;

    public Event(QRCode qr, String name, String description, String category, String date, String location, Boolean geoTracking) {
        this.qr = qr;
        this.name = name;
        this.description = description;
        this.category = category;
        this.date = date;
        this.location = location;
        this.geoTracking = geoTracking;
    }

    public QRCode getQr() {
        return qr;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public Boolean getGeoTracking() {
        return geoTracking;
    }
}
