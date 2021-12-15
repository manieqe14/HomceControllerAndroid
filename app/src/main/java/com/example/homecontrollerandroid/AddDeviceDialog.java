package com.example.homecontrollerandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.homecontrollerandroid.main.ApplicationClass;
import com.example.homecontrollerandroid.main.SettingsSingleton;
import com.example.homecontrollerandroid.main.SuplaDevicesAdapter;
import com.example.homecontrollerandroid.supla.SuplaDevice;

public class AddDeviceDialog extends AppCompatDialogFragment {

    private EditText etDeviceName, etLink;
    private Switch swLED;
    private addDeviceDialogListener listener;
    SettingsSingleton settingsSingleton;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_device,null);

        settingsSingleton = SettingsSingleton.getInstance(getContext());


        builder.setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(R.string.add_device, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SuplaDevice device = new SuplaDevice();
                        device.setName(etDeviceName.getText().toString().trim());
                        device.setAddress(etLink.getText().toString().trim());
                        device.setBrightness(swLED.isChecked());
                        device.setUserEmail(settingsSingleton.USER_MAIL);

                        listener.getData(device);

                    }
                });
        etDeviceName = view.findViewById(R.id.etDeviceName);
        etLink = view.findViewById(R.id.etLink);
        swLED = view.findViewById(R.id.swLED);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (addDeviceDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must impement addDialogListener");
        }
    }


    public interface addDeviceDialogListener{

        void getData(SuplaDevice device);

    }
}
