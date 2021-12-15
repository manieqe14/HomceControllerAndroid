package com.example.homecontrollerandroid.supla;

import com.example.homecontrollerandroid.main.ApplicationClass;
import com.example.homecontrollerandroid.main.SuplaScenes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandConverter {

    private static String SUPLA_ADDRESS = ApplicationClass.SUPLA_SERVER_ADDRESS;

    public static HashMap<String, String> fromDevicesList(ArrayList<SuplaDevice> devices){

        HashMap<String, String> result = new HashMap<>();

        JSONArray statuses = new JSONArray();
        JSONArray brightnesses = new JSONArray();
        JSONArray devicesIds = new JSONArray();

        for(SuplaDevice i : devices)
        {
            statuses.put(i.isStatus());
            brightnesses.put(i.getBrightnessValue());
            devicesIds.put(i.getName());
        }

        result.put("devicesIds", devicesIds.toString());
        result.put("brightness", brightnesses.toString());
        result.put("status", statuses.toString());

        return result;
    }

    public static ArrayList<SuplaDevice> getDevicesForScene(SuplaScenes scene, List<SuplaDevice> devices){

        JSONArray brightnessValues = null;
        JSONArray statuses = null;
        JSONArray devicesIds = null;

        try {
            brightnessValues = new JSONArray(scene.getBrightness());
            statuses = new JSONArray(scene.getStatus());
            devicesIds = new JSONArray(scene.getDevicesIds());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<SuplaDevice> result = new ArrayList<>();

        try {
            for(int x = 0; x < statuses.length(); x++){

                String name = devicesIds.getString(x);

                for(SuplaDevice y : devices)
                {
                    if(y.getName().equals(name))
                    {
                        if(statuses.getString(x).equals("true"))
                            y.setStatus(true);
                        else
                            y.setStatus(false);

                        if (y.isBrightness())
                            y.setBrightnessValue(brightnessValues.getInt(x));
                        result.add(y);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Exception e)
        {

        }

        return result;
    }

    public static ArrayList<String> listOfHttpCommands(ArrayList<SuplaDevice> devicesList){
        ArrayList<String> result = new ArrayList<>();

        for(SuplaDevice i : devicesList)
        {
            if(i.isBrightness())
                result.add(SUPLA_ADDRESS + i.getAddress() + "/set-rgbw-parameters?brightness=" + i.getBrightnessValue());
            else {
                if(i.isStatus())
                    result.add(SUPLA_ADDRESS + i.getAddress() + "/turn-on");
                else
                    result.add(SUPLA_ADDRESS + i.getAddress() + "/turn-off");
            }

        }

        return result;
    }

    public static ArrayList<SuplaDevice> changeStatusOfDevices(ArrayList<SuplaDevice> devices){


            for(SuplaDevice device : devices)
            {
                if(device.isStatus())
                    device.setStatus(false);
                else
                    device.setStatus(true);

                if(device.isBrightness()){

                    if(device.getBrightnessValue()>0)
                        device.setBrightnessValue(0);

                }
            }

        return devices;
    }

}
