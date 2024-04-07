package com.example.eventlinkqr;

public class ImageModel {
    private String base64Image;

    // Default constructor is necessary for Firestore's data to object conversion
    public ImageModel() {
    }

    public String getBase64Image() {
        return base64Image;
    }

}
