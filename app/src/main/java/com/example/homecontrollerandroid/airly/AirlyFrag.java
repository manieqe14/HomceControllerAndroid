package com.example.homecontrollerandroid.airly;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homecontrollerandroid.R;
import com.example.homecontrollerandroid.main.SettingsSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AirlyFrag extends Fragment {

    TextView tvPM1, tvPM25, tvPM10, tvPress, tvHum, tvTemp, tvStation, tvAirlyLog, tvRequestsRemaining;
    ImageButton  ibInfoAirly;
    View v;
    ImageView ivPM1, ivPM25, ivPM10, ivPress, ivHum, ivTemp;

    public HashMap<String, Double> airlyData;

    airlyDataGetter airlyGetter;

    SettingsSingleton settingsSingleton;

    public AirlyFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_airly, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ivPM1 = v.findViewById(R.id.ivPM1);
        ivPM10 = v.findViewById(R.id.ivPM10);
        ivPM25 = v.findViewById(R.id.ivPM25);
        ivPress = v.findViewById(R.id.ivPress);
        ivHum = v.findViewById(R.id.ivHum);
        ivTemp = v.findViewById(R.id.ivTemp);

        tvPM1 = v.findViewById(R.id.tvPM1);
        tvPM25 = v.findViewById(R.id.tvPM25);
        tvPM10 = v.findViewById(R.id.tvPM10);
        tvPress = v.findViewById(R.id.tvPress);
        tvHum = v.findViewById(R.id.tvHum);
        tvTemp = v.findViewById(R.id.tvTemp);
        tvStation = v.findViewById(R.id.tvStation);
        tvAirlyLog = v.findViewById(R.id.tvAirlyLog);
        tvRequestsRemaining = v.findViewById(R.id.tvRequestsRemaining);

        ibInfoAirly = v.findViewById(R.id.ibInfoAirly);

        settingsSingleton = SettingsSingleton.getInstance(getContext());


        ibInfoAirly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    AirlyInfoDialog airlyInfoDialog = new AirlyInfoDialog(getContext(), AirlyFrag.this);
                    airlyInfoDialog.show(getFragmentManager(), null);

            }
        });

        forceToRetrieveNewData();
        ViewLocationData();

    }

    public void ViewLocationData(){

        try {
            MarkerMap markerMap = new MarkerMap(new JSONObject(settingsSingleton.getSetting(SettingsSingleton.SETTING_NAME.AIRLY_NEAREST)));
            tvStation.setText(markerMap.getAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void ViewMeasurementData(){
        airlyData = airlyGetter.getDataFromJSON();
        tvRequestsRemaining.setText("Requests remaining: " + airlyDataGetter.headers.get("X-RateLimit-Remaining-day"));

        tvPM1.setText(airlyData.get("PM1").toString());

        tvPM25.setText(airlyData.get("PM25").toString());
        if(Double.parseDouble(airlyData.get("PM25").toString()) > 25)
            tvPM25.setTextColor(Color.RED);
        else
            tvPM25.setTextColor(Color.GREEN);

        tvPM10.setText(airlyData.get("PM10").toString());
        if(Double.parseDouble(airlyData.get("PM10").toString()) > 50)
            tvPM10.setTextColor(Color.RED);
        else
            tvPM10.setTextColor(Color.GREEN);
        tvPress.setText(airlyData.get("PRESSURE").toString() + "hPa");
        tvHum.setText(airlyData.get("HUMIDITY").toString() + "%");
        tvTemp.setText(airlyData.get("TEMPERATURE").toString() + "°C");

        setLayoutVisible(true);
    }

    public void tooManyRequests(){
        tvAirlyLog.setVisibility(View.VISIBLE);

    }

    private void setLayoutVisible(boolean visibility){
        if(visibility) {
            tvPM1.setVisibility(View.VISIBLE);
            tvPM25.setVisibility(View.VISIBLE);
            tvPM10.setVisibility(View.VISIBLE);
            tvPress.setVisibility(View.VISIBLE);
            tvHum.setVisibility(View.VISIBLE);
            tvTemp.setVisibility(View.VISIBLE);

            ivPM1.setVisibility(View.VISIBLE);
            ivPM25.setVisibility(View.VISIBLE);
            ivPM10.setVisibility(View.VISIBLE);
            ivPress.setVisibility(View.VISIBLE);
            ivHum.setVisibility(View.VISIBLE);
            ivTemp.setVisibility(View.VISIBLE);
        }
        else{
            tvPM1.setVisibility(View.GONE);
            tvPM25.setVisibility(View.GONE);
            tvPM10.setVisibility(View.GONE);
            tvPress.setVisibility(View.GONE);
            tvHum.setVisibility(View.GONE);
            tvTemp.setVisibility(View.GONE);

            ivPM1.setVisibility(View.GONE);
            ivPM25.setVisibility(View.GONE);
            ivPM10.setVisibility(View.GONE);
            ivPress.setVisibility(View.GONE);
            ivHum.setVisibility(View.GONE);
            ivTemp.setVisibility(View.GONE);
        }

    }

    public void forceToRetrieveNewData(){
        airlyGetter = new airlyDataGetter(this);
        //airlyGetter.getDataForLocation();
        airlyGetter.getDataForMeasurements();
    }

}