package com.example.homecontrollerandroid.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.homecontrollerandroid.AddDeviceDialog;
import com.example.homecontrollerandroid.R;
import com.example.homecontrollerandroid.supla.SuplaDevice;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class suplaDevicesList extends AppCompatActivity implements AddDeviceDialog.addDeviceDialogListener, SuplaDevicesAdapter.OnLongClickMethod, modifyDeviceDialog.addDeviceDialogListener{

    RecyclerView suplaDevicesView;
    SuplaDevicesAdapter suplaDevicesAdapter;
    RecyclerView.LayoutManager suplaDevicesLayout;

    FloatingActionButton fabAddDevice;
    ArrayList<SuplaDevice> devices;
    SwipeRefreshLayout swipeRefreshLayout;
    public static SuplaDevice deviceToModify;

    SettingsSingleton settingsSingleton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supla_devices_list);
        settingsSingleton = SettingsSingleton.getInstance(getApplicationContext());

        devices = settingsSingleton.getSuplaDevicesList();
        //devices = (ArrayList)ApplicationClass.suplaDevices;

        suplaDevicesView = findViewById(R.id.rvSuplaDevicesList);
        suplaDevicesView.setHasFixedSize(true);

        suplaDevicesLayout = new GridLayoutManager(this, 1);
        suplaDevicesView.setLayoutManager(suplaDevicesLayout);

        suplaDevicesAdapter = new SuplaDevicesAdapter(this,null, devices,0, false);
        suplaDevicesView.setAdapter(suplaDevicesAdapter);

        fabAddDevice = findViewById(R.id.fabAddDevice);

        fabAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(DialogType.add);

            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshDeviceList);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDevices();
                suplaDevicesAdapter.notifyNewData(devices);
                swipeRefreshLayout.setRefreshing(false);

            }
        });


    }

    public void openDialog(DialogType dialogType){

        if(dialogType == DialogType.add) {
            AddDeviceDialog addDeviceDialog = new AddDeviceDialog();
            addDeviceDialog.show(getSupportFragmentManager(), null);
        }

        else if (dialogType == DialogType.modify){

            try{
           modifyDeviceDialog myDialog = new modifyDeviceDialog();
           myDialog.show(getSupportFragmentManager(),null);
            }
            catch (Exception e)
            {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void getData(final SuplaDevice device) {

        Backendless.Persistence.save(device, new AsyncCallback<SuplaDevice>() {
            @Override
            public void handleResponse(SuplaDevice response) {

                //MainActivity.showToast("New device added!", getApplicationContext());


            }

            @Override
            public void handleFault(BackendlessFault fault) {
                MainActivity.showToast(fault.getMessage(), getApplicationContext());
            }
        });
            settingsSingleton.addSuplaDevice(device);
            refreshDevices();
            suplaDevicesAdapter.notifyNewData(devices);

    }

    public void updateData(final SuplaDevice device) {
        Backendless.Persistence.save(device, new AsyncCallback<SuplaDevice>() {
            @Override
            public void handleResponse(SuplaDevice response) {

                //MainActivity.showToast("New device added!", getApplicationContext());

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                MainActivity.showToast(fault.getMessage(), getApplicationContext());
            }
        });

            suplaDevicesAdapter.notifyNewData(devices);
            settingsSingleton.updateSuplaDevice(device, false);
            refreshDevices();
            MainActivity.showToast(getString(R.string.update_succes), this);

    }

    @Override
    public void deleteData(final SuplaDevice device) {

        Backendless.Data.of(SuplaDevice.class).remove(device, new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {

                MainActivity.showToast(suplaDevicesList.this.getString(R.string.device_deleted), suplaDevicesList.this);

            }

            @Override
            public void handleFault(BackendlessFault fault) {

                MainActivity.showToast("Error: " + fault.getMessage(), suplaDevicesList.this);

            }
        });

        settingsSingleton.updateSuplaDevice(device, true);
        refreshDevices();
        suplaDevicesAdapter.notifyNewData(devices);

    }



    protected void refreshDevices(){
        devices = settingsSingleton.getSuplaDevicesList();
    }

    @Override
    public void editionView(SuplaDevice device) {
                deviceToModify = device;
                openDialog(DialogType.modify);
    }


    private enum DialogType{

        add, modify;

    }


}