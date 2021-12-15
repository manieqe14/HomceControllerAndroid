package com.example.homecontrollerandroid.volumio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RadioStation {
    private JSONObject JSONdata;
    private String title;

    public RadioStation(String data){
        try {
            JSONdata = new JSONObject(data);
            title = getTitleFromJSON(data);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getTitleFromJSON(String data){
        try {
            JSONObject obj = new JSONObject(data);

            return obj.getString("title");



        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ArrayList<String> getRadioObjectFromJSON(String data){

        JSONObject navigation = null;
        ArrayList<String> result = new ArrayList<>();
        try {
            navigation = new JSONObject(data).getJSONObject("navigation");
            JSONArray listsArray = navigation.getJSONArray("lists");
            JSONArray items = ((JSONObject)listsArray.get(0)).getJSONArray("items");

            for(int i = 0; i < items.length(); i++){
                result.add(items.getString(i));
            }
            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }
}
