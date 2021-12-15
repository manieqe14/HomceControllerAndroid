package com.example.homecontrollerandroid.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.homecontrollerandroid.R;
import com.example.homecontrollerandroid.supla.CommandConverter;
import com.example.homecontrollerandroid.supla.SuplaDevice;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class newSceneDialog extends AppCompatDialogFragment implements SuplaDevicesAdapter.CallbackToNewSceneDialog{

    Button btnAddConfirmation;
    EditText etValue, etSceneName;
    TextView tvChooseDevice;
    TextInputLayout tilFloatingHintValue;
    Switch swOnOffDevice;

    RecyclerView devicesView;
    SuplaDevicesAdapter devicesAdapter;
    RecyclerView.LayoutManager devicesLayout;

    RecyclerView chosenDevicesView;
    SuplaDevicesAdapter chosenDevicesAdapter;
    RecyclerView.LayoutManager chosenDevicesLayout;

    OnExitListener onExitListener;

    View view;
    Toolbar toolbar;


    private ArrayList<SuplaDevice> suplaDevicesList;
    private ArrayList<SuplaDevice> suplaChosenDevicesList;
    private SettingsSingleton settingsSingleton;
    Context context;

    @Override
    public void isBrightnessVisible(boolean brighthness) {
        if(brighthness) {
            etValue.setVisibility(View.VISIBLE);
            swOnOffDevice.setVisibility(View.GONE);
        }
        else {
            etValue.setVisibility(View.GONE);
            swOnOffDevice.setVisibility(View.VISIBLE);
        }
    }

    public interface OnExitListener{
        void onDismiss();
    }

    public newSceneDialog (Context context, ArrayList<SuplaDevice> suplaDevices){

        this.context = context;
        this.suplaDevicesList = suplaDevices;
        onExitListener = (OnExitListener) context;
        settingsSingleton = SettingsSingleton.getInstance(context);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.new_scene_dialog,null);
        builder.setView(view);

        setStyle(DialogFragment.STYLE_NORMAL,R.style.AppTheme_FullScreenDialog);
        suplaChosenDevicesList = new ArrayList<>();

        try {
            toolbar = view.findViewById(R.id.new_scene_toolbar);
            toolbar.setTitle(R.string.new_scene);
            toolbar.inflateMenu(R.menu.new_scene_dialog_toolbar);

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (etSceneName.getText().toString().isEmpty()) {
                        MainActivity.showToast(getString(R.string.empty_scene_name), getContext());
                        return true;
                    } else {
                        SuplaScenes newScene = new SuplaScenes();
                        newScene.setTitle(etSceneName.getText().toString());
                        newScene.setBrightness(CommandConverter.fromDevicesList(suplaChosenDevicesList).get("brightness"));
                        newScene.setDevicesIds(CommandConverter.fromDevicesList(suplaChosenDevicesList).get("devicesIds"));
                        newScene.setStatus(CommandConverter.fromDevicesList(suplaChosenDevicesList).get("status"));
                        newScene.setUserEmail(settingsSingleton.USER_MAIL);

                        /*Backendless.Persistence.save(newScene, new AsyncCallback<SuplaScenes>() {
                            @Override
                            public void handleResponse(SuplaScenes response) {

                                MainActivity.showToast("Scene succesfully created!", context);

                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                MainActivity.showToast(fault.getMessage(), context);
                            }
                        });*/
                        settingsSingleton.addSuplaScene(newScene);
                        onExitListener.onDismiss();
                        dismiss();
                        return false;
                    }
                }

            });

        }

        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        tvChooseDevice = view.findViewById(R.id.tvChooseDevice);
        btnAddConfirmation = view.findViewById(R.id.btnAddConfirm);
        etValue = view.findViewById(R.id.etValue);
        etSceneName = view.findViewById(R.id.etSceneName);
        tilFloatingHintValue = view.findViewById(R.id.floating_hint_value);
        swOnOffDevice = view.findViewById(R.id.swOnOffDevice);


        try {
            chosenDevicesView = view.findViewById(R.id.rvSuplaDevicesAddScene);
            chosenDevicesView.setHasFixedSize(true);
            chosenDevicesLayout = new GridLayoutManager(context, 1);
            chosenDevicesView.setLayoutManager(chosenDevicesLayout);
            chosenDevicesAdapter = new SuplaDevicesAdapter(getContext(), newSceneDialog.this, suplaChosenDevicesList, 0, true);
            chosenDevicesView.setAdapter(chosenDevicesAdapter);
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        devicesView = view.findViewById(R.id.rvSuplaDevicesChoice);
        devicesView.setHasFixedSize(true);
        devicesLayout = new GridLayoutManager(context, 3);
        devicesView.setLayoutManager(devicesLayout);
        devicesAdapter = new SuplaDevicesAdapter(getContext(), newSceneDialog.this, suplaDevicesList,1, false);
        devicesView.setAdapter(devicesAdapter);

        btnAddConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                if(devicesAdapter.getSelectedDevice() == null)
                    throw new Exception("No device is selected!");

                SuplaDevice array = devicesAdapter.getSelectedDevice();

                    for (SuplaDevice i : suplaChosenDevicesList) {
                        if (i.getName().equals(array.getName()))
                            throw new IllegalArgumentException("Object already exists!");
                    }

                    if(array.isBrightness())
                        array.setBrightnessValue(Integer.parseInt(String.valueOf(etValue.getText())));
                    else
                        array.setStatus(swOnOffDevice.isChecked());

                    suplaChosenDevicesList.add(array);
                    chosenDevicesAdapter.notifyNewData(suplaChosenDevicesList);
                    chosenDevicesAdapter.notifyDataSetChanged();

                    etValue.setText("");
                    devicesAdapter.removeSelection();
                    etValue.setVisibility(View.GONE);
                    swOnOffDevice.setVisibility(View.GONE);
                }

                catch (IllegalArgumentException e)
                {
                    Toast.makeText(context, R.string.device_exists, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    MainActivity.showToast(e.getMessage(), context);
                }

            }
        });

        return builder.create();
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if(dialog!=null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width,height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);

        }


    }

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);
    }


}
