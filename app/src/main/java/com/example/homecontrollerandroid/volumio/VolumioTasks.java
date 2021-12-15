package com.example.homecontrollerandroid.volumio;

import android.content.Context;
import android.os.AsyncTask;

import com.example.homecontrollerandroid.main.MainActivity;
import com.example.homecontrollerandroid.main.SettingsSingleton;
import com.example.homecontrollerandroid.main.volumioFrag;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VolumioTasks extends AsyncTask<String, Integer, String> {

    Context context;
    SettingsSingleton settingsSingleton;
    volumioFrag volumioFragment;

    public VolumioTasks(Context context) {
        this.context = context;
        settingsSingleton = SettingsSingleton.getInstance(context);
    }

    public void getVolumioRadios(volumioFrag volumioFragment){
        this.volumioFragment = volumioFragment;
        execute(volumioRequest.GET_RADIOS);
    }

    StringBuffer link;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        SettingsSingleton settingsSingleton = SettingsSingleton.getInstance(null);
        link = new StringBuffer(settingsSingleton.getSetting(SettingsSingleton.SETTING_NAME.VOLUMIO_ADDRESS));
    }

    @Override
    protected String doInBackground(String... strings) {
        StringBuffer response = new StringBuffer();

        link.append(strings[0]);
        URL url = null;
        try {
            url = new URL(link.toString());

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = "";
            while((line = br.readLine()) != null){
                response.append(line);
            }

        } catch (MalformedURLException e) {
           // MainActivity.showToast(e.getMessage(), context);
        } catch (IOException e) {
            //MainActivity.showToast(e.getMessage(), context);
        }

        return response.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(volumioFragment !=null){
            volumioFragment.setStations(s);
        }else {
            try {
                JSONObject responseObj = new JSONObject(s);
                MainActivity.showToast(responseObj.get("response").toString(), context);

            } catch (JSONException e) {
                MainActivity.showToast(e.getMessage(), context);
            }

            if (volumioFragment != null) {
                volumioFragment.setStations(s);
            }
        }


    }


}
