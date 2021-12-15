package com.example.homecontrollerandroid.airly;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.homecontrollerandroid.main.MainActivity;
import com.example.homecontrollerandroid.main.SettingsSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class airlyDataGetter {

    Context context;

    private final String STATIONS_OPTION = "stations";
    //private final String LOCATION_URL = "https://airapi.airly.eu/v2/installations/nearest?lat=50.00555&lng=20.01394&maxDistanceKM=5&maxResults=1";
    private final String LOCATION_URL = "https://airapi.airly.eu/v2/installations/";
    private final String[] HEADER1 = new String[]{"Accept", "application/json"};
    private final String[] HEADER2 = new String[]{"apikey", "6ZO3Q2RuscqmTvAOlX5fuqYjxfEXFXOo"};

    private final String LOCATION_MEASUREMENT_URL = "https://airapi.airly.eu/v2/measurements/installation?installationId=13167";

    private JSONObject result = null;
    private JSONArray locationJSON = null;
    HashMap<String, Double> measurementMap = new HashMap<>();
    AirlyFrag airlyFrag;
    AirlyInfoDialog airlyInfoDialog;

    SettingsSingleton settingsSingleton;



    public static HashMap<String, String> headers = new HashMap<>();

    private int code;

    public airlyDataGetter(AirlyInfoDialog airlyInfoDialog){
        this.airlyInfoDialog = airlyInfoDialog;
        settingsSingleton = SettingsSingleton.getInstance(null);
    }

    public static String getNearestLink(double lat, double lng, double maxDist, int maxResults){
        StringBuffer link = new StringBuffer("https://airapi.airly.eu/v2/installations/nearest?");
        link.append("lat=" + lat + "&lng=" + lng + "&maxDistanceKM=" + maxDist + "&maxResults=" + maxResults);

        return link.toString();
    }

    public airlyDataGetter(AirlyFrag airlyFrag) {

        this.airlyFrag = airlyFrag;

    }

    public void getDataForLocation(){

        try {
            JSONObject obj = new JSONObject(settingsSingleton.getSetting(SettingsSingleton.SETTING_NAME.AIRLY_NEAREST));
            String locationId = obj.getString("locationId");
            new RetrieveAirlyData().execute(LOCATION_URL + locationId, "location");

        } catch (JSONException e) {
            MainActivity.showToast(e.getMessage(), context);
        }


    }

    public void getDataForMeasurements(){
        new RetrieveAirlyData().execute(LOCATION_MEASUREMENT_URL, null);
    }
    public void getDataForStations(String link){
        new RetrieveAirlyData().execute(link, STATIONS_OPTION);
    }

    public Map<String, String> getStation(){
        JSONObject obj1 = null;
        JSONObject obj2 = null;
        Map<String, String> result = new HashMap<>();

        try {
             obj1 = (JSONObject) locationJSON.get(0);
             result.put("locationId" , obj1.get("locationId").toString());

             obj2 = (JSONObject) obj1.get("address");

             result.put("displayAddress1", obj2.get("displayAddress1").toString());
            result.put("displayAddress2", obj2.get("displayAddress2").toString());
            result.put("number", obj2.get("number").toString());


        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    public HashMap<String, Double> getDataFromJSON(){

        try {
            JSONObject obj2 = (JSONObject) result.get("current");
            JSONArray array = (JSONArray) obj2.get("values");
            measurementMap.clear();

            for(int i = 0; i < array.length(); i++)
            {
                measurementMap.put((String)((JSONObject)(array.get(i))).get("name"),(Double)((JSONObject)array.get(i)).get("value"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return measurementMap;

    }


    class RetrieveAirlyData extends AsyncTask<String, Void, String[]> {

        protected String[] doInBackground(String... urls) {

            StringBuffer response = new StringBuffer();

            try {
                URL url = new URL(urls[0]);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestProperty(HEADER1[0], HEADER1[1]);
                httpsURLConnection.setRequestProperty(HEADER2[0], HEADER2[1]);
                code = httpsURLConnection.getResponseCode();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));

                String line = "";
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                headers.put("X-RateLimit-Remaining-day", httpsURLConnection.getHeaderField("X-RateLimit-Remaining-day"));
                br.close();

            } catch (MalformedURLException e) {
                Toast.makeText(context, "Bad URL", Toast.LENGTH_SHORT).show();
            } catch (IOException e)
            {
                response.append(e.getMessage());
                //Toast.makeText(context, "IOException", Toast.LENGTH_SHORT).show();
            } catch (Exception e)
            {
                response.append(e.getMessage());
                //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }



            return new String[]{response.toString(), urls[1]};

        }

        protected void onPostExecute(String[] s) {

            try {
                if(airlyFrag != null) {
                    if (code != 429) {
                        if (s[1] == null) {
                            result = new JSONObject(s[0]);
                            airlyFrag.ViewMeasurementData();
                        } else if (s[1].equals("location")) {
                            locationJSON = new JSONArray(s[0]);
                            //airlyFrag.ViewLocationData();
                        }
                    } else
                        airlyFrag.tooManyRequests();
                }
                else if(s[1].equals(STATIONS_OPTION)){
                    airlyInfoDialog.addMarkers(s[0]);
                }

            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

            }

        }
    }

    public JSONArray getLocationJSON() {
        return locationJSON;
    }
}
