package com.example.homecontrollerandroid.main;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.homecontrollerandroid.airly.airlyDataGetter;
import com.example.homecontrollerandroid.supla.Sensors;
import com.example.homecontrollerandroid.supla.SuplaDevice;
import com.example.homecontrollerandroid.supla.suplaCommands;
import com.example.homecontrollerandroid.supla.suplaExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApplicationClass extends Application {

    public static final String APPLICATION_ID = "6511D8F1-EC64-8848-FF71-0E814E80D600";
    public static final String API_KEY = "C5D85992-60A9-46C6-A58B-706377404679";
    public static final String SERVER_URL = "https://api.backendless.com";


    public static BackendlessUser user;

    public static final int CELSIUS_UNITS = 0;
    public static String SUPLA_SERVER_ADDRESS = "https://192.168.1.167";





    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl(SERVER_URL);
        Backendless.initApp(getApplicationContext(), APPLICATION_ID, API_KEY);

    }



}
