package com.example.homecontrollerandroid.volumio;

import org.json.JSONException;
import org.json.JSONObject;

public class getFromVolumioJSON {

    public static String getArtist(String volumioData){

        if (volumioData.equals(null)) return "";

        JSONObject data = null;
        String artist = new String();

        try {
             data = new JSONObject(volumioData);
             artist = data.getString("artist");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return artist;
    }

    public static String getAlbum(String volumioData){
        JSONObject data = null;
        String album = new String();

        try {
            data = new JSONObject(volumioData);
            album = data.getString("album");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return album;
    }

    public static String getTitle(String volumioData){
        JSONObject data = null;
        String title = new String();

        try {
            data = new JSONObject(volumioData);
            title = data.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return title;
    }



}
