package com.example.eventlinkqr;

/**
 * A model class representing an image in Base64 encoded string format.
 * This class is primarily used for storing and retrieving image data from Firestore.
 */
public class ImageModel {

    /**
     * The Base64 encoded string of the image.
     */
    private String base64Image;

    /**
     * Default constructor necessary for Firestore's automatic data-to-object conversion.
     */
    public ImageModel() {
    }

    /**
     * Gets the Base64 encoded string of the image.
     * @return The Base64 encoded string representing the image.
     */
    public String getBase64Image() {
        return base64Image;
    }

}
