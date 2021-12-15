package com.example.homecontrollerandroid.volumio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homecontrollerandroid.R;

import java.util.ArrayList;

public class ChooseStationDialog extends AppCompatDialogFragment {

    View v;
    Context context;

    RecyclerView rvRadioStations;
    RadioStationsAdapter radioStationsAdapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<RadioStation> radioStations;

    public ChooseStationDialog (Context context, ArrayList<RadioStation> radioStations){
        this.context = context;
        this.radioStations = radioStations;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.choose_station_dialog, null);
        builder.setView(v);

        setStyle(DialogFragment.STYLE_NORMAL,R.style.AppTheme_FullScreenDialog);

        rvRadioStations = v.findViewById(R.id.rvRadioStations);
        layoutManager = new LinearLayoutManager(context);
        rvRadioStations.setLayoutManager(layoutManager);
        radioStationsAdapter = new RadioStationsAdapter(context, radioStations);
        rvRadioStations.setAdapter(radioStationsAdapter);



        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if(dialog != null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }
}
