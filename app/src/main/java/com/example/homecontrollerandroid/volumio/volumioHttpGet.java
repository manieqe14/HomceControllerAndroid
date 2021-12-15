package com.example.homecontrollerandroid.volumio;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class volumioHttpGet {


    private static URL url;
    private static StringBuilder response;
    private static StringBuilder address = new StringBuilder();
    final static String onet = "http://android.com";
    private static HttpURLConnection urlConnection;

    final private static String ERR_TIMEOUT = "Connection Timed out!";

    public static String sendGetRequest(controls command){
/*

        response = new StringBuilder();
        address.append("http://192.168.1.19");

            if(command == controls.STOP)
                address.append("/api/v1/commands/?cmd=stop");

            if(command == controls.PLAY)
                address.append("/api/v1/commands/?cmd=play");


            if(command == controls.getINFO)
                address.append("/api/getState");
            try {
                URL url = new URL(onet);
                urlConnection = (HttpURLConnection) url.openConnection();
                response.append(readStream(urlConnection.getInputStream()));
                //InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                //response.append(in);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }

            catch (IOException e) {
            e.printStackTrace();
            }
            finally
            {
            urlConnection.disconnect();
            }*/

        return response.toString().trim();
        //return new String("done");
    }

    public enum controls
    {
        STOP, PLAY, getINFO;

    }




}
