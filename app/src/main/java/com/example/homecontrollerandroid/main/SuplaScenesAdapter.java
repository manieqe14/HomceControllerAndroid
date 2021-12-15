package com.example.homecontrollerandroid.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.homecontrollerandroid.R;
import com.example.homecontrollerandroid.supla.CommandConverter;
import com.example.homecontrollerandroid.supla.SuplaDevice;
import com.example.homecontrollerandroid.supla.suplaCommands;
import com.example.homecontrollerandroid.supla.suplaExecutor;

import java.util.ArrayList;
import java.util.List;

public class SuplaScenesAdapter extends RecyclerView.Adapter<SuplaScenesAdapter.ViewHolder> {

    private List<SuplaScenes> scenes;
    Context context;
    SettingsSingleton settingsSingleton;

    public SuplaScenesAdapter(Context context, ArrayList<SuplaScenes> scenes) {
        this.scenes = scenes;
        this.context = context;
        settingsSingleton = SettingsSingleton.getInstance(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvSceneTitle, tvSceneDetails, tvDeviceValue;
        Switch swScene;
        Button btnDeleteScene, btnHideExpendable;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvSceneTitle = itemView.findViewById(R.id.tvSceneTitle);
            swScene = itemView.findViewById(R.id.swScene);
            tvSceneDetails = itemView.findViewById(R.id.tvSceneDetails);
            tvDeviceValue = itemView.findViewById(R.id.tvDeviceValue);
            btnDeleteScene = itemView.findViewById(R.id.btnDeleteScene);
            btnHideExpendable = itemView.findViewById(R.id.btnHideExpendable);


            swScene.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    SuplaScenes sceneToExecute = scenes.get(scenes.indexOf(itemView.getTag()));
                    ArrayList<SuplaDevice> listOfDevices = CommandConverter.getDevicesForScene(sceneToExecute, settingsSingleton.getSuplaDevicesList());
                    ArrayList<String> commands;

                    if(!b)
                        commands = CommandConverter.listOfHttpCommands(CommandConverter.changeStatusOfDevices(listOfDevices));
                    else
                        commands = CommandConverter.listOfHttpCommands(listOfDevices);


                    for(String i : commands)
                        new suplaExecutor(suplaCommands.SUPLA_SCENE, suplaExecutor.RequestSource.SUPLA_SCENE).execute(i);

                    if(b)
                        MainActivity.showToast(sceneToExecute.getTitle() + context.getString(R.string.is_on), context);
                    else
                        MainActivity.showToast(sceneToExecute.getTitle() + context.getString(R.string.is_off), context);
                }
            });


    }
    }

    @NonNull
    @Override
    public SuplaScenesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.supla_row_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SuplaScenesAdapter.ViewHolder holder, final int position) {

        holder.itemView.setTag(scenes.get(position));
        holder.tvSceneTitle.setText(scenes.get(position).getTitle());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                ArrayList<SuplaDevice> devicesListForScene = CommandConverter.getDevicesForScene(scenes.get(position), settingsSingleton.getSuplaDevicesList());

                holder.tvSceneDetails.setText("");
                holder.tvDeviceValue.setText("");

                for(SuplaDevice i:devicesListForScene)
                {
                    holder.tvSceneDetails.setText(String.valueOf(holder.tvSceneDetails.getText()) + i.getName() + "\n");
                    if(i.isBrightness())
                        holder.tvDeviceValue.setText(String.valueOf(holder.tvDeviceValue.getText()) + "Bright:" + i.getBrightnessValue() + "\n");
                    else
                        holder.tvDeviceValue.setText(String.valueOf(holder.tvDeviceValue.getText()) + i.isStatus() + "\n");
                }

                CardView expendableCardView = holder.itemView.findViewById(R.id.cvExpendableCardView);
                expendableCardView.setVisibility(View.VISIBLE);

                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_down);
                expendableCardView.setAnimation(animation);

                return false;
            }
        });

        holder.btnDeleteScene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(context);

                confirmationDialog.setPositiveButton(R.string.im_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        /*Backendless.Data.of(SuplaScenes.class).remove(scenes.get(position), new AsyncCallback<Long>() {
                            @Override
                            public void handleResponse(Long response) {
                                MainActivity.showToast(context.getString(R.string.scene_deleted), context);
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                MainActivity.showToast("Error: " + fault.getMessage(), context);

                            }
                        });*/

                        settingsSingleton.updateSuplaScene(scenes.get(position), true);

                        dataUpdate(settingsSingleton.getSuplaScenesList());
                        notifyDataSetChanged();

                    }
                });

                confirmationDialog.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                confirmationDialog.setTitle(R.string.delete_scene)
                                    .setMessage(R.string.delete_message);

                confirmationDialog.show();



            }
        });

        holder.btnHideExpendable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CardView expendableCardView = holder.itemView.findViewById(R.id.cvExpendableCardView);
                expendableCardView.setVisibility(View.GONE);

            }
        });

       }

    @Override
    public int getItemCount() {
        return scenes.size();
    }

    public void dataUpdate(List<SuplaScenes> scenes){

        this.scenes = scenes;
        notifyDataSetChanged();

    }

}
