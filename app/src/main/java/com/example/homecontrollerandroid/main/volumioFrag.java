package com.example.homecontrollerandroid.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.homecontrollerandroid.R;
import com.example.homecontrollerandroid.volumio.ChooseStationDialog;
import com.example.homecontrollerandroid.volumio.RadioStation;
import com.example.homecontrollerandroid.volumio.VolumioTasks;
import com.example.homecontrollerandroid.volumio.volumioRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class volumioFrag extends Fragment {

    ImageButton ibVolumioPlay, ibVolumioStop;
    SettingsSingleton settingsSingleton;
    Button btnChooseStation;
    View v;
    VolumioTasks volumioTasks;

    ArrayList<RadioStation> radioStations;


    public volumioFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_volumio, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        settingsSingleton = SettingsSingleton.getInstance(getContext());

        ibVolumioPlay = v.findViewById(R.id.ibVolumioPlay);
        ibVolumioStop = v.findViewById(R.id.ibVolumioStop);

        btnChooseStation = v.findViewById(R.id.btnChooseRadio);

        btnChooseStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseStationDialog chooseStationDialog = new ChooseStationDialog(getContext(), radioStations);
                chooseStationDialog.show(getFragmentManager(), null);

            }
        });

        ibVolumioPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new VolumioTasks(getContext()).execute(volumioRequest.PLAY);
            }
        });

        ibVolumioStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new VolumioTasks(getContext()).execute(volumioRequest.STOP);

            }
        });
        radioStations = new ArrayList<>();
        volumioTasks = new VolumioTasks(getContext());
        volumioTasks.getVolumioRadios(this);
    }

    public void setStations(String s){

        ArrayList<String> JSONStations;
        JSONStations = RadioStation.getRadioObjectFromJSON(s);

        for(int i = 0; i <JSONStations.size(); i++){
            radioStations.add(new RadioStation(JSONStations.get(i)));
        }

    }


}