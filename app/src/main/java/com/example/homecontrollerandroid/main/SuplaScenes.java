package com.example.homecontrollerandroid.main;


import java.util.HashMap;

public class SuplaScenes {
    private String title;
    private String status;
    private String brightness;
    private String objectId;
    private String devicesIds;
    private String userEmail;

    public SuplaScenes(String title, String status, String brightness, String objectId, String devicesIds, String userEmail) {
        this.title = title;
        this.status = status;
        this.brightness = brightness;
        this.objectId = objectId;
        this.devicesIds = devicesIds;
        this.userEmail = userEmail;
    }

    public SuplaScenes(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userMail) {
        this.userEmail = userMail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBrightness() {
        return brightness;
    }

    public void setUserMail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDevicesIds() {
        return devicesIds;
    }

    public void setDevicesIds(String devicesIds) {
        this.devicesIds = devicesIds;
    }

    public void setBrightness(String brightness) {
        this.brightness = brightness;
    }



}
