package com.example.homecontrollerandroid.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homecontrollerandroid.R;
import com.example.homecontrollerandroid.supla.Sensors;
import com.example.homecontrollerandroid.supla.SensorsAdapter;
import com.example.homecontrollerandroid.supla.suplaCommands;
import com.example.homecontrollerandroid.supla.suplaExecutor;

public class dataFrag extends Fragment {

    public SensorsAdapter sensorsAdapter;
    RecyclerView rvSensors;
    RecyclerView.LayoutManager sensorsLayout;

    SettingsSingleton settingsSingleton;

    TextView tvNoSensorsCreated;

    View v;

    public dataFrag() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvNoSensorsCreated = v.findViewById(R.id.tvNoSensorsCreated);
        settingsSingleton = SettingsSingleton.getInstance(getContext());


        try {
                rvSensors = v.findViewById(R.id.rvSensors);
                sensorsAdapter = new SensorsAdapter(settingsSingleton.getSensorsList(), getContext(), this);
                sensorsLayout = new GridLayoutManager(getContext(), 2);
                rvSensors.setLayoutManager(sensorsLayout);
                rvSensors.setAdapter(sensorsAdapter);

            if(settingsSingleton.getSensorsList().size()!=0) {
                tvNoSensorsCreated.setVisibility(View.GONE);
            }
            else
                tvNoSensorsCreated.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
       retrieveNewData();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_data, container, false);

        return v;
    }


    public void notifyDataUpdated(){
        if(settingsSingleton.getSensorsList().size()!=0) {
            sensorsAdapter.notifyNewData();
            tvNoSensorsCreated.setVisibility(View.GONE);
        }
        else
            tvNoSensorsCreated.setVisibility(View.VISIBLE);
    }

    public void retrieveNewData(){
        for(Sensors i : settingsSingleton.getSensorsList())
        {
            new suplaExecutor(suplaCommands.SUPLA_INFO,this,
                    suplaExecutor.RequestSource.SUPLA_DATA).execute(settingsSingleton.getSetting(SettingsSingleton.SETTING_NAME.SUPLA_ADDRESS) +
                    i.getAddress(), i.getName());
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public void ShowDialogNewSensor(){

        DialogAddSensor dialogAddSensor = new DialogAddSensor();
        dialogAddSensor.setTargetFragment(dataFrag.this, 1);
        dialogAddSensor.show(getFragmentManager().beginTransaction(), "AddSensorDialog");

    }
}