package com.example.homecontrollerandroid.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import com.example.homecontrollerandroid.R;
import com.example.homecontrollerandroid.main.SuplaDevicesAdapter;
import com.example.homecontrollerandroid.supla.SuplaDevice;

public class modifyDeviceDialog extends AppCompatDialogFragment{

    private EditText etDeviceNameMD, etLinkMD;
    private Switch swLED_MD;
    private addDeviceDialogListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_modify_device,null);


        builder.setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        suplaDevicesList.deviceToModify.setName(etDeviceNameMD.getText().toString().trim());
                        suplaDevicesList.deviceToModify.setAddress(etLinkMD.getText().toString().trim());
                        suplaDevicesList.deviceToModify.setBrightness(swLED_MD.isChecked());
                        listener.updateData(suplaDevicesList.deviceToModify);

                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.deleteData(suplaDevicesList.deviceToModify);

                    }
                });
        etDeviceNameMD = view.findViewById(R.id.etDeviceNameMD);
        etLinkMD = view.findViewById(R.id.etLinkMD);
        swLED_MD = view.findViewById(R.id.swLED_MD);


            try {
                    etDeviceNameMD.setText(suplaDevicesList.deviceToModify.getName());
                    etLinkMD.setText(suplaDevicesList.deviceToModify.getAddress());
                    if (suplaDevicesList.deviceToModify.isBrightness())
                        swLED_MD.setChecked(true);
                    else
                        swLED_MD.setChecked(false);

                }

            catch (Exception e)
            {
                MainActivity.showToast(getString(R.string.error), getContext());
            }
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (addDeviceDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement addDialogListener");
        }
    }

    public interface addDeviceDialogListener{

        void getData(SuplaDevice device);
        void updateData(SuplaDevice device);
        void deleteData(SuplaDevice device);

    }
}
