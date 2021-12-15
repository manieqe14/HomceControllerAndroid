package com.example.homecontrollerandroid.supla;

public class SuplaDevice {
    private String name;
    private String objectId;
    private String address;
    private  boolean status;
    private boolean brightness;
    private int brightnessValue;
    private String userEmail;

    public SuplaDevice(String name, String objectId, String address, boolean status, boolean brightness, int brightnessValue, String userEmail) {
        this.name = name;
        this.objectId = objectId;
        this.address = address;
        this.brightness = brightness;
        this.userEmail = userEmail;
        this.brightnessValue = brightnessValue;
        this.status = status;
    }

    public SuplaDevice(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isBrightness() {
        return brightness;
    }

    public void setBrightness(boolean brightness) {
        this.brightness = brightness;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getBrightnessValue() {
        return brightnessValue;
    }

    public void setBrightnessValue(int brightnessValue) {
        this.brightnessValue = brightnessValue;
    }
}
