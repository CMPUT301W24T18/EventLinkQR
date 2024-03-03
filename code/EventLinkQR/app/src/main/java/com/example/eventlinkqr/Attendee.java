package com.example.eventlinkqr;
import java.io.File;
import java.io.IOException;
import java.net.URL;


public class Attendee {

    private String name;

    public String getUuid() {
        return uuid;
    }

    private String uuid;

    private String phone_number;

    private String homepage;

//    private BufferedImage image;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }


    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
