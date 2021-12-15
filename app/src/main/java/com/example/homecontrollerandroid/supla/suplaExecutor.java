package com.example.homecontrollerandroid.supla;

import android.content.Context;
import android.os.AsyncTask;
import com.example.homecontrollerandroid.main.ApplicationClass;
import com.example.homecontrollerandroid.main.SettingsSingleton;
import com.example.homecontrollerandroid.main.dataFrag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class suplaExecutor extends AsyncTask<String, Void, String> {

    StringBuilder response = new StringBuilder();
    private int sceneCommand;
    dataFrag dataFragment;
    RequestSource source;
    SettingsSingleton settingsSingleton;


    public suplaExecutor(int sceneCommand, RequestSource source){
       this.sceneCommand = sceneCommand;
       this.source = source;
    }

    public suplaExecutor(int sceneCommand, dataFrag dataFragment, RequestSource source)
    {
        this.sceneCommand = sceneCommand;
        this.dataFragment = dataFragment;
        this.source = source;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

// Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {

        }

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    @Override
    protected String doInBackground(String... strings) {

        response = new StringBuilder();
        URL url = null;
        //

        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {

            System.out.println("Bad URL!");
        }

        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("GET");
            String line = "";
            InputStreamReader inputStreamReader = new InputStreamReader(httpsURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

        }

        if((sceneCommand == suplaCommands.SUPLA_INFO) && (strings.length > 1)) {
            settingsSingleton = SettingsSingleton.getInstance(null);

            for (int i = 0; i < settingsSingleton.getSensorsList().size(); i++) {
                if(strings[1].equals(settingsSingleton.getSensorsList().get(i).getName()))
                {
                    settingsSingleton.getSensorsList().get(i).setTemp(suplaCommands.getTemp(response.toString()));
                    settingsSingleton.getSensorsList().get(i).setHumidity(suplaCommands.getHum(response.toString()));
                }

            }


        }

        return response.toString();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(source == RequestSource.SUPLA_DATA)
            dataFragment.notifyDataUpdated();

    }

    public enum RequestSource{
        SUPLA_DATA, SUPLA_SCENE;
    }

}
