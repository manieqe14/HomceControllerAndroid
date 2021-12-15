package com.example.homecontrollerandroid.supla;

import org.json.JSONException;
import org.json.JSONObject;

public class suplaCommands extends suplaDirectExecutor{

    final public static int SUPLA_INFO = 0;
    final public static int SUPLA_SCENE = 1;


    public static double getHum(String data){

        JSONObject dataJSON = null;
        double hum = 0;

        try {
             dataJSON= new JSONObject(data);
             hum = dataJSON.getDouble("humidity");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return hum;

    }

    public static double getTemp(String data){

        JSONObject dataJSON = null;
        double temp = 0;

        try {
            dataJSON= new JSONObject(data);
            temp = dataJSON.getDouble("temperature");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return temp;

    }


}
