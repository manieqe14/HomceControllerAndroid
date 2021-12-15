package com.example.homecontrollerandroid.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.homecontrollerandroid.R;
import com.example.homecontrollerandroid.supla.Sensors;

public class DialogAddSensor extends DialogFragment {

    View v;
    EditText etSensorAddress, etSensorName;
    CheckBox cbSensorTemp, cbSensorHum;
    SettingsSingleton settingsSingleton;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_new_sensor, null);

        etSensorAddress = v.findViewById(R.id.etSensorAddress);
        etSensorName = v.findViewById(R.id.etSensorName);
        cbSensorTemp = v.findViewById(R.id.cbSensorTemp);
        cbSensorHum = v.findViewById(R.id.cbSensorHum);
        settingsSingleton = SettingsSingleton.getInstance(getContext());

        builder.setView(v)
                .setMessage("New sensor")
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(!etSensorAddress.getText().toString().equals(""))
                        {
                            Sensors sensors = new Sensors(etSensorName.getText().toString(),0.0, 0.0,
                                    etSensorAddress.getText().toString(), cbSensorHum.isChecked(), cbSensorTemp.isChecked(), settingsSingleton.USER_MAIL);

                            settingsSingleton.addSensor(sensors);

                            /*Backendless.Persistence.save(sensors, new AsyncCallback<Sensors>() {
                                @Override
                                public void handleResponse(Sensors response) {

                                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
                                    dismiss();
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {

                                }
                            });*/


                        }

                        if(etSensorAddress.getText().toString().equals(""))
                        {
                            MainActivity.showToast("Please, fill address of sensor!", getContext());
                        }

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }
}
