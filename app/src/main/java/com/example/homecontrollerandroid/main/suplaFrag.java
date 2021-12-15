package com.example.homecontrollerandroid.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.homecontrollerandroid.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class suplaFrag extends Fragment {

    RecyclerView suplaRecyclerView;
    SuplaScenesAdapter suplaAdapter;
    RecyclerView.LayoutManager suplaLayout;
    View v;

    SettingsSingleton settingsSingleton;


    public suplaFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_supla, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        settingsSingleton = SettingsSingleton.getInstance(getContext());

        suplaRecyclerView = v.findViewById(R.id.supla_scenes_list);
        suplaRecyclerView.setHasFixedSize(true);

        suplaLayout = new GridLayoutManager(this.getActivity(), Integer.parseInt(settingsSingleton.getSetting((SettingsSingleton.SETTING_NAME.SUPLA_ROWS))));
        suplaRecyclerView.setLayoutManager(suplaLayout);

        suplaAdapter = new SuplaScenesAdapter(this.getActivity(), settingsSingleton.getSuplaScenesList());
        suplaRecyclerView.setAdapter(suplaAdapter);

    }

    public void notifySuplaAdapterChanged(){

        suplaLayout = new GridLayoutManager(this.getActivity(), Integer.parseInt(settingsSingleton.getSetting((SettingsSingleton.SETTING_NAME.SUPLA_ROWS))));
        suplaRecyclerView.setLayoutManager(suplaLayout);
        suplaAdapter.dataUpdate(settingsSingleton.getSuplaScenesList());
        //suplaAdapter.notifyDataSetChanged();
    }


}