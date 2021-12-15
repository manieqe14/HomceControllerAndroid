package com.example.homecontrollerandroid.airly;

import android.location.Location;

import com.example.homecontrollerandroid.main.MainActivity;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class MarkerMap {
    private String id;
    private LatLng latLng;
    private String locationId;
    private String address;
    private JSONObject stationJSON;

    public MarkerMap(JSONObject marker){
        id = getIdFromJSON(marker);
        latLng = getLatLngFromJSON(marker);
        locationId = gelocationIdFromJSON(marker);
        address = getAddressFromJSON(marker);
        stationJSON = marker;
    }

    private String getIdFromJSON(JSONObject marker) {
        String id;
        try {
            id = marker.getString("id");
            return id;
        } catch (JSONException e) {
            return "";
        }

    }

    private LatLng getLatLngFromJSON(JSONObject marker){

        try {
            JSONObject LatLngObj = marker.getJSONObject("location");
            LatLng location = new LatLng(LatLngObj.getDouble("latitude"), LatLngObj.getDouble("longitude"));

            return location;
        } catch (JSONException e) {
            return new LatLng(0, 0);
        }

    }
    private String gelocationIdFromJSON(JSONObject marker){
        String locationId;
        try {
            locationId = marker.getString("locationId");
            return locationId;
        } catch (JSONException e) {
            return "";
        }


    }

    private String getAddressFromJSON(JSONObject marker){
        String address;
        try {
            JSONObject addresObj = marker.getJSONObject("address");
            return addresObj.getString("city") + ", " + addresObj.getString("street") + " " + addresObj.getString("number");
        } catch (JSONException e) {
            return "";
        }


    }

    public String getId() {
        return id;
    }


    public LatLng getLatLng() {
        return latLng;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getAddress() {
        return address;
    }

    public JSONObject getStationJSON() {
        return stationJSON;
    }
}
