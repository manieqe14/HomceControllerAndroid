package com.example.homecontrollerandroid.supla;

public class Sensors{
    private String name;
    private Double temp;
    private Double humidity;
    private String address;
    private boolean humidityCheck;
    private boolean tempCheck;
    private String userEmail;
    private String objectId;

    public Sensors(String name, Double temp, Double humidity, String address, boolean humidityCheck, boolean tempCheck, String userEmail) {
        this.name = name;
        this.temp = temp;
        this.humidity = humidity;
        this.address = address;
        this.humidityCheck = humidityCheck;
        this.tempCheck = tempCheck;
        this.userEmail = userEmail;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectID) {
        this.objectId = objectID;
    }

    public Sensors(){

    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isHumidityCheck() {
        return humidityCheck;
    }

    public void setHumidityCheck(boolean humidityCheck) {
        this.humidityCheck = humidityCheck;
    }

    public boolean isTempCheck() {
        return tempCheck;
    }

    public void setTempCheck(boolean tempCheck) {
        this.tempCheck = tempCheck;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }
}
