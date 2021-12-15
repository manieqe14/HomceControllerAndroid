package com.example.homecontrollerandroid.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homecontrollerandroid.R;
import com.example.homecontrollerandroid.supla.SuplaDevice;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class SuplaDevicesAdapter extends RecyclerView.Adapter<SuplaDevicesAdapter.ViewHolder>{

    List<SuplaDevice> devices;
    Context devicesAdapterContext;

    public final int CHECKBOX_ON = 1;
    private int checkbox;
    private boolean values;

    private int selectedPosition = -1;

    View v;

    OnLongClickMethod onLongClickMethod;

    public interface OnLongClickMethod{
        void editionView(SuplaDevice device);
    }

    CallbackToNewSceneDialog callbackToNewSceneDialog;

    public interface  CallbackToNewSceneDialog{
        public void isBrightnessVisible(boolean brightness);

    }




    public SuplaDevicesAdapter(Context activity, newSceneDialog fragment, ArrayList<SuplaDevice> devices, int checkbox, boolean values){
        this.devices = devices;
        devicesAdapterContext = activity;
        onLongClickMethod = (OnLongClickMethod) activity;
        this.checkbox = checkbox;
        this.values = values;
        callbackToNewSceneDialog = (CallbackToNewSceneDialog) fragment;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvDeviceName, tvDeviceValue;
        CheckBox cbChooseDevice;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            if(checkbox == 0) {
                tvDeviceName = itemView.findViewById(R.id.tvDevice);
                tvDeviceValue = itemView.findViewById(R.id.tvDeviceValue);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        int number = 0;

                        for (int i = 0; i < devices.size(); i++) {
                            if (devices.get(i).getName().equals(tvDeviceName.getText().toString()))
                                number = i;
                        }

                        onLongClickMethod.editionView(devices.get(number));

                        return false;
                    }
                });

            }

            else
            {
                tvDeviceName = itemView.findViewById(R.id.tvDevice);
                cbChooseDevice = itemView.findViewById(R.id.cbChooseDevice);

            }

        }
    }


    @NonNull
    @Override
    public SuplaDevicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(checkbox == 0 && values == false) {
            v = LayoutInflater.from(devicesAdapterContext).inflate(R.layout.supla_device_layout, parent, false);
        }
        else if(checkbox == 0 && values == true)
        {
            v = LayoutInflater.from(devicesAdapterContext).inflate(R.layout.supla_device_layout_with_values, parent, false);
        }
        else
        {
            v = LayoutInflater.from(devicesAdapterContext).inflate(R.layout.supla_device_layout_with_checkbox, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.itemView.setTag(devices.get(position));
        holder.tvDeviceName.setText(devices.get(position).getName());
        if(values == true) {
            if(devices.get(position).isBrightness())
                holder.tvDeviceValue.setText("Bright: " + String.valueOf(devices.get(position).getBrightnessValue()));
            else{
                if(devices.get(position).isStatus())
                    holder.tvDeviceValue.setText("ON");
                else
                    holder.tvDeviceValue.setText("OFF");
            }
        }

        if(checkbox == 1) {
            if (position == selectedPosition) {
                holder.cbChooseDevice.setChecked(true);

                if(devices.get(position).isBrightness())
                    callbackToNewSceneDialog.isBrightnessVisible(true);
                else
                    callbackToNewSceneDialog.isBrightnessVisible(false);
            }
            else {
                holder.cbChooseDevice.setChecked(false);
            }

            try {
                holder.cbChooseDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notifyDataSetChanged();
                    }
                });

                holder.cbChooseDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        if (b == true) {
                            selectedPosition = holder.getAdapterPosition();
                        }

                    }
                });
            } catch (Exception e) {
                Toast.makeText(devicesAdapterContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    public void notifyNewData(ArrayList<SuplaDevice> newData)
    {
        devices = newData;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {

        return devices.size();
    }

    public SuplaDevice getSelectedDevice(){

        return devices.get(selectedPosition);
    }

    public void removeSelection(){

        selectedPosition = -1;
        notifyDataSetChanged();

    }


}
