package com.example.homecontrollerandroid.supla;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.homecontrollerandroid.R;
import com.example.homecontrollerandroid.main.ApplicationClass;
import com.example.homecontrollerandroid.main.MainActivity;
import com.example.homecontrollerandroid.main.SettingsSingleton;
import com.example.homecontrollerandroid.main.dataFrag;

import java.util.List;

public class SensorsAdapter extends RecyclerView.Adapter<SensorsAdapter.ViewHolder> {

    private List<Sensors> sensors;
    Context context;
    private dataFrag DataFrag;

    SettingsSingleton settingsSingleton;

    public SensorsAdapter(List<Sensors> sensors, Context context, dataFrag DataFrag) {
        this.sensors = sensors;
        this.context = context;
        this.DataFrag = DataFrag;
        settingsSingleton = SettingsSingleton.getInstance(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTempSensors, tvHumSensors, tvSensorName;
        ImageView ivTempSensors, ivHumSensors;
        ImageButton ibDeleteSensor;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvTempSensors = itemView.findViewById(R.id.tvTempSensors);
            tvHumSensors = itemView.findViewById(R.id.tvHumSensors);
            tvSensorName = itemView.findViewById(R.id.tvSensorName);

            ivTempSensors = itemView.findViewById(R.id.ivTempSensors);
            ivHumSensors = itemView.findViewById(R.id.ivHumSensors);

            ibDeleteSensor = itemView.findViewById(R.id.ibDeleteSensor);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    ibDeleteSensor.setVisibility(View.VISIBLE);
                    return false;
                }
            });

            ibDeleteSensor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //ApplicationClass.sensorsList.remove(Integer.parseInt(String.valueOf(itemView.getTag())));

                    settingsSingleton.updateSensor(sensors.get(Integer.parseInt(String.valueOf(itemView.getTag()))), true);
                    DataFrag.notifyDataUpdated();
                    /*Backendless.Data.of(Sensors.class).remove(sensors.get(Integer.parseInt(String.valueOf(itemView.getTag()))), new AsyncCallback<Long>() {
                        @Override
                        public void handleResponse(Long response) {
                            MainActivity.showToast(sensors.get(Integer.parseInt(String.valueOf(itemView.getTag()))).getName() + context.getString(R.string.sensor_deleted), context);
                            ApplicationClass.sensorsList.remove(sensors.get(Integer.parseInt(String.valueOf(itemView.getTag()))));
                            DataFrag.notifyDataUpdated();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText(context, R.string.error + ": " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });*/


                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }

    @NonNull
    @Override
    public SensorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor_view, parent, false);

        return new SensorsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorsAdapter.ViewHolder holder, int position) {

        if(sensors.get(position).isTempCheck()) {
            if(Integer.parseInt(settingsSingleton.getSetting(SettingsSingleton.SETTING_NAME.SENSORS_UNITS)) == settingsSingleton.CELSIUS)
                holder.tvTempSensors.setText(String.valueOf(sensors.get(position).getTemp()) + "°C");
            else
                holder.tvTempSensors.setText(String.valueOf((sensors.get(position).getTemp())*1.8 + 32) + "°F");
        }
        else {
            holder.tvTempSensors.setVisibility(View.GONE);
            holder.ivTempSensors.setVisibility(View.GONE);
        }

        if(sensors.get(position).isHumidityCheck())
            holder.tvHumSensors.setText(String.valueOf(sensors.get(position).getHumidity()) + "%");
        else {
            holder.tvHumSensors.setVisibility(View.GONE);
            holder.ivHumSensors.setVisibility(View.GONE);
        }

        holder.tvSensorName.setText(String.valueOf(sensors.get(position).getName()));
        holder.ibDeleteSensor.setVisibility(View.GONE);

        holder.itemView.setTag(position);
    }

    public void notifyNewData(){
        sensors = settingsSingleton.getSensorsList();
        notifyDataSetChanged();
    }
}
