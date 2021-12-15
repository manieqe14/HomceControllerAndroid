package com.example.homecontrollerandroid.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.homecontrollerandroid.supla.Sensors;
import com.example.homecontrollerandroid.supla.SuplaDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsSingleton{

    private static SettingsSingleton INSTANCE;

    private static final String[] SUPLA_DEVICE_KEYS = {"name", "objectId", "address", "status", "brightness", "brightnessValue", "userEmail"};
    private static final String[] SENSOR_KEYS = {"name", "objectId", "temp", "humidity", "address", "humidityCheck", "tempCheck", "userMail"};
    private static final String[] SUPLA_SCENE_KEYS = {"title", "status", "brightness", "objectId", "devicesIds", "userEmail"};
    private static final String PREF_ADDRESS = "com.example.homecontrollerandroid.prefs";

    public final int CELSIUS = 0;
    public final int FAHRENHEIT = 1;

    public final String USER_MAIL = "mariusz.pacyga@gmail.com";

    private List<Sensors> sensorsList;
    private List<SuplaDevice> suplaDevicesList;
    private List<SuplaScenes> suplaScenesList;

    public HashMap<SETTING_NAME, String> settingsMap;

    private Context context;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private SettingsSingleton(Context context){
        this.context = context;
        prefs = context.getSharedPreferences(PREF_ADDRESS, Context.MODE_PRIVATE);
        settingsMap = new HashMap<>();
        getSharedPreferences();
        sensorsList = new ArrayList<>();
        retrieveSuplaDevicesList();
        retrieveSuplaScenesList();
        retrieveSensorsList();
    };

    public static SettingsSingleton getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new SettingsSingleton(context);
        }

        return INSTANCE;
    }

    public enum SETTING_NAME {
        VOLUMIO_VISIBILITY, SUPLA_VISIBILITY, VOLUMIO_ADDRESS, SUPLA_ROWS, SENSORS_UNITS,
        SUPLA_ADDRESS, GENERAL_VISIBILITY, AIRLY_NEAREST, CHOSEN_RADIO, AIRLY_MAX_DIST, AIRLY_STATION_QUANTITY;
    }

    private String enum2String(SETTING_NAME name){
        if(name == SETTING_NAME.VOLUMIO_VISIBILITY)
            return "volumio_visibility";
        else if (name == SETTING_NAME.SUPLA_VISIBILITY)
            return "Supla_visibility";
        else if (name == SETTING_NAME.VOLUMIO_ADDRESS)
            return "volumio_address";
        else if (name == SETTING_NAME.SUPLA_ROWS)
            return "supla_rows";
        else if (name == SETTING_NAME.SENSORS_UNITS)
            return "sensor_units";
        else if (name == SETTING_NAME.SUPLA_ADDRESS)
            return "supla_address";
        else if (name == SETTING_NAME.GENERAL_VISIBILITY)
            return "general_visibility";
        else if (name == SETTING_NAME.AIRLY_NEAREST)
            return "airly_nearest";
        else if (name == SETTING_NAME.CHOSEN_RADIO)
            return "chosen_radio";
        else if (name == SETTING_NAME.AIRLY_MAX_DIST)
            return "airly_max_dist";
        else if (name == SETTING_NAME.AIRLY_STATION_QUANTITY)
            return "airly_station_quantity";
        else
            return "";
    }

    private Object readResolve(){
        return getInstance(context);
    }

    private void getSharedPreferences(){
        SETTING_NAME[] enums = SETTING_NAME.values();

        for(int i = 0; i < enums.length; i++)
        {
            /*if(enum2String(enums[i]) == "volumio_visibility"){
                settingsMap.put(enums[i], "1");
                Log.d("TUTAJ!", "Value: " + prefs.getString(enum2String(enums[i]), "1").getClass().getName());

            }
            else if(enum2String(enums[i]) == "general_visibility"){
                settingsMap.put(enums[i], "1");
                Log.d("TUTAJ2!", "Value: " + prefs.getString(enum2String(enums[i]), "1").getClass().getName());
            }
            else {
                settingsMap.put(enums[i], prefs.getString(enum2String(enums[i]), "1"));
            }*/

            settingsMap.put(enums[i], prefs.getString(enum2String(enums[i]), "1"));
        }

    }

    public void addSetting(SETTING_NAME name, String value){
        settingsMap.put(name, value);
        editor = prefs.edit();
        editor.putString(enum2String(name), value);
        editor.commit();
    }

    public void addSetting(SETTING_NAME name, Set<String> value){

        settingsMap.put(name, set2JSON(value));
        editor = prefs.edit();
        editor.putString(enum2String(name), set2JSON(value));
        editor.commit();
    }


    public String getSetting(SETTING_NAME name){
        if (settingsMap.get(name)!=null)
            return settingsMap.get(name);
        else
            return "1";
    }

    public Set<String> getSettingSet(SETTING_NAME name){

        return  JSON2set(settingsMap.get(name));
    }

    private void retrieveSuplaDevicesList(){
        suplaDevicesList = new ArrayList<>();

        try {
            JSONArray suplaDevicesArray = new JSONArray(prefs.getString("supla_devices_list", "[]"));

            for(int i = 0; i < suplaDevicesArray.length(); i++){
                JSONObject device = suplaDevicesArray.getJSONObject(i);
                SuplaDevice d = new SuplaDevice(device.getString(SUPLA_DEVICE_KEYS[0]), "",
                        device.getString(SUPLA_DEVICE_KEYS[2]), device.getBoolean(SUPLA_DEVICE_KEYS[3]), device.getBoolean(SUPLA_DEVICE_KEYS[4]),
                        device.getInt(SUPLA_DEVICE_KEYS[5]), device.getString(SUPLA_DEVICE_KEYS[6]));
                suplaDevicesList.add(d);
            }

        } catch (JSONException e) {
            MainActivity.showToast(e.getMessage(), context);
        }

    }

    private void retrieveSuplaScenesList(){
        suplaScenesList = new ArrayList<>();

        try {
            JSONArray suplaScenesArray = new JSONArray(prefs.getString("supla_scenes_list", "[]"));

            for(int i = 0; i < suplaScenesArray.length(); i++){
                JSONObject device = suplaScenesArray.getJSONObject(i);
                SuplaScenes d = new SuplaScenes(device.getString(SUPLA_SCENE_KEYS[0]), device.getString(SUPLA_SCENE_KEYS[1]),
                        device.getString(SUPLA_SCENE_KEYS[2]), "", device.getString(SUPLA_SCENE_KEYS[4]),
                        device.getString(SUPLA_SCENE_KEYS[5]));
                suplaScenesList.add(d);
            }

        } catch (JSONException e) {
            MainActivity.showToast(e.getMessage(), context);
        }

    }

    private void retrieveSensorsList(){

        sensorsList = new ArrayList<>();

        try {
            JSONArray sensorsArray = new JSONArray(prefs.getString("sensor_list", "[]"));

            for(int i = 0; i < sensorsArray.length(); i++){
                JSONObject device = sensorsArray.getJSONObject(i);
                sensorsList.add(new Sensors(device.getString(SENSOR_KEYS[0]), device.getDouble(SENSOR_KEYS[1]),
                        device.getDouble(SENSOR_KEYS[2]), device.getString(SENSOR_KEYS[3]), device.getBoolean(SENSOR_KEYS[4]),
                        device.getBoolean(SENSOR_KEYS[5]), device.getString(SENSOR_KEYS[6])));
            }



        } catch (JSONException e) {
            MainActivity.showToast(e.getMessage(), context);
        }

    }


    public void addSuplaDevice(SuplaDevice suplaDevice){
        try {
            JSONArray suplaDevicesArray = new JSONArray(prefs.getString("supla_devices_list", "[]"));
            JSONObject device = new JSONObject();

            device.put(SUPLA_DEVICE_KEYS[0], suplaDevice.getName());
            device.put(SUPLA_DEVICE_KEYS[1], suplaDevice.getObjectId());
            device.put(SUPLA_DEVICE_KEYS[2], suplaDevice.getAddress());
            device.put(SUPLA_DEVICE_KEYS[3], suplaDevice.isStatus());
            device.put(SUPLA_DEVICE_KEYS[4], suplaDevice.isBrightness());
            device.put(SUPLA_DEVICE_KEYS[5], suplaDevice.getBrightnessValue());
            device.put(SUPLA_DEVICE_KEYS[6], suplaDevice.getUserEmail());

            suplaDevicesArray.put(device);

            editor = prefs.edit();
            editor.putString("supla_devices_list", suplaDevicesArray.toString());
            editor.commit();

            suplaDevicesList.add(suplaDevice);


        } catch (JSONException e) {
            MainActivity.showToast("Error in creating Supla Device", context);
        }

    }

    public void updateSuplaDevice(SuplaDevice suplaDevice, boolean remove){
        try {
            JSONArray suplaDevicesArray = new JSONArray(prefs.getString("supla_devices_list", "[]"));
            JSONObject device = new JSONObject();

            device.put(SUPLA_DEVICE_KEYS[0], suplaDevice.getName());
            device.put(SUPLA_DEVICE_KEYS[1], suplaDevice.getObjectId());
            device.put(SUPLA_DEVICE_KEYS[2], suplaDevice.getAddress());
            device.put(SUPLA_DEVICE_KEYS[3], suplaDevice.isStatus());
            device.put(SUPLA_DEVICE_KEYS[4], suplaDevice.isBrightness());
            device.put(SUPLA_DEVICE_KEYS[5], suplaDevice.getBrightnessValue());
            device.put(SUPLA_DEVICE_KEYS[6], suplaDevice.getUserEmail());

            for(int i = 0; i <suplaDevicesArray.length(); i++){
                if(suplaDevicesArray.getJSONObject(i).get(SUPLA_DEVICE_KEYS[0]).equals(suplaDevice.getName()))
                {
                    suplaDevicesArray.remove(i);
                }
                if(suplaDevicesList.get(i).getName().equals(suplaDevice.getName()))
                {
                    suplaDevicesList.remove(i);
                }
            }

            if(!remove){
                suplaDevicesArray.put(device);
                suplaDevicesList.add(suplaDevice);
            }

            editor = prefs.edit();
            editor.putString("supla_devices_list", suplaDevicesArray.toString());
            editor.commit();




        } catch (JSONException e) {
            MainActivity.showToast("Error in creating Supla Device", context);
        }

    }

    private String set2JSON(Set<String> set){
        JSONArray array = new JSONArray();
        for(String s : set)
        {array.put(s);}

        return array.toString();
    }

    private Set<String> JSON2set(String array){
        Set<String> set = new HashSet<>();
        try {
            JSONArray JSONarray = new JSONArray(array);
            for(int i = 0; i < JSONarray.length(); i++)
            {
                set.add(JSONarray.getString(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return set;
    }

    public void addSensor(Sensors sensor){

        JSONArray sensorArray = null;
        try {
            sensorArray = new JSONArray(prefs.getString("sensor_list", "[]"));

            JSONObject device = new JSONObject();

            device.put(SENSOR_KEYS[0], sensor.getName());
            device.put(SENSOR_KEYS[1], sensor.getTemp());
            device.put(SENSOR_KEYS[2], sensor.getHumidity());
            device.put(SENSOR_KEYS[3], sensor.getAddress());
            device.put(SENSOR_KEYS[4], sensor.isHumidityCheck());
            device.put(SENSOR_KEYS[5], sensor.isTempCheck());
            device.put(SENSOR_KEYS[6], sensor.getUserEmail());

            sensorArray.put(device);

            editor = prefs.edit();
            editor.putString("sensor_list", sensorArray.toString());
            editor.commit();

            sensorsList.add(sensor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateSensor(Sensors sensor, boolean remove){
        try {
            JSONArray sensorArray = new JSONArray(prefs.getString("sensor_list", "[]"));
            JSONObject device = new JSONObject();

            device.put(SENSOR_KEYS[0], sensor.getName());
            device.put(SENSOR_KEYS[1], sensor.getTemp());
            device.put(SENSOR_KEYS[2], sensor.getHumidity());
            device.put(SENSOR_KEYS[3], sensor.getAddress());
            device.put(SENSOR_KEYS[4], sensor.isHumidityCheck());
            device.put(SENSOR_KEYS[5], sensor.isTempCheck());
            device.put(SENSOR_KEYS[6], sensor.getUserEmail());

            for(int i = 0; i <sensorArray.length(); i++){
                if(sensorArray.getJSONObject(i).get(SENSOR_KEYS[0]).equals(sensor.getName()))
                {
                    sensorArray.remove(i);
                }
                if(sensorsList.get(i).getName().equals(sensor.getName()))
                {
                    sensorsList.remove(i);
                }
            }

            if(!remove){
                sensorArray.put(device);
                sensorsList.add(sensor);
            }

            editor = prefs.edit();
            editor.putString("sensor_list", sensorArray.toString());
            editor.commit();




        } catch (JSONException e) {
            MainActivity.showToast("Error in creating Sensor", context);
        }

    }

    public ArrayList<SuplaDevice> getSuplaDevicesList(){
        return (ArrayList<SuplaDevice>) suplaDevicesList;
    }

    public ArrayList<SuplaScenes> getSuplaScenesList(){
        return (ArrayList<SuplaScenes>) suplaScenesList;
    }

    public ArrayList<Sensors> getSensorsList(){
        return (ArrayList<Sensors>) sensorsList;
    }

    public void getSuplaDevicesFromBackendless(){
        String whereClause = "userEmail = '" + ApplicationClass.user.getEmail() + "'";

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);

        Backendless.Persistence.of(SuplaDevice.class).find(queryBuilder, new AsyncCallback<List<SuplaDevice>>() {
            @Override
            public void handleResponse(List<SuplaDevice> response) {
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
    }

    public void getSuplaScenesFromBackendless(){
        String whereClause = "userEmail = '" + ApplicationClass.user.getEmail() + "'";

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);

        try {
            Backendless.Persistence.of(SuplaScenes.class).find(queryBuilder, new AsyncCallback<List<SuplaScenes>>() {
                @Override
                public void handleResponse(List<SuplaScenes> response) {
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    MainActivity.showToast("Error: " + fault.getMessage(), context);
                }
            });
        }catch(Exception e)
        {
            MainActivity.showToast("Error: " + e.getMessage(), context);
        }
    }

    public void getSensorsFromBackendless(){
        String whereClause = "userEmail = '" + ApplicationClass.user.getEmail() + "'";

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);

        Backendless.Persistence.of(Sensors.class).find(queryBuilder, new AsyncCallback<List<Sensors>>() {
            @Override
            public void handleResponse(List<Sensors> response) {

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
    }

    public void addSuplaScene(SuplaScenes suplaScene){
        try {
            JSONArray suplaScenesArray = new JSONArray(prefs.getString("supla_scenes_list", "[]"));
            JSONObject sceneObject = new JSONObject();

            sceneObject.put(SUPLA_SCENE_KEYS[0], suplaScene.getTitle());
            sceneObject.put(SUPLA_SCENE_KEYS[1], suplaScene.getStatus());
            sceneObject.put(SUPLA_SCENE_KEYS[2], suplaScene.getBrightness());
            sceneObject.put(SUPLA_SCENE_KEYS[3], suplaScene.getObjectId());
            sceneObject.put(SUPLA_SCENE_KEYS[4], suplaScene.getDevicesIds());
            sceneObject.put(SUPLA_SCENE_KEYS[5], suplaScene.getUserEmail());

            suplaScenesArray.put(sceneObject);

            editor = prefs.edit();
            editor.putString("supla_scenes_list", suplaScenesArray.toString());
            editor.commit();

            suplaScenesList.add(suplaScene);


        } catch (JSONException e) {
            MainActivity.showToast("Error in creating Supla Scene", context);
        }

    }

    public void updateSuplaScene(SuplaScenes suplaScene, boolean remove){
        try {
            JSONArray suplaScenesArray = new JSONArray(prefs.getString("supla_scenes_list", "[]"));
            JSONObject sceneObject = new JSONObject();

            sceneObject.put(SUPLA_SCENE_KEYS[0], suplaScene.getTitle());
            sceneObject.put(SUPLA_SCENE_KEYS[1], suplaScene.getStatus());
            sceneObject.put(SUPLA_SCENE_KEYS[2], suplaScene.getBrightness());
            sceneObject.put(SUPLA_SCENE_KEYS[3], "");
            sceneObject.put(SUPLA_SCENE_KEYS[4], suplaScene.getDevicesIds());
            sceneObject.put(SUPLA_SCENE_KEYS[5], suplaScene.getUserEmail());

            for(int i = 0; i <suplaScenesArray.length(); i++){
                if(suplaScenesArray.getJSONObject(i).get(SUPLA_SCENE_KEYS[0]).equals(suplaScene.getTitle()))
                {
                    suplaScenesArray.remove(i);
                }
                if(suplaScenesList.get(i).getTitle().equals(suplaScene.getTitle()))
                {
                    suplaScenesList.remove(i);
                }
            }

            if(!remove){
                suplaScenesArray.put(sceneObject);
                suplaScenesList.add(suplaScene);
            }

            editor = prefs.edit();
            editor.putString("supla_scenes_list", suplaScenesArray.toString());
            editor.commit();


        } catch (JSONException e) {
            MainActivity.showToast(e.getMessage(), context);
        }

    }
}
