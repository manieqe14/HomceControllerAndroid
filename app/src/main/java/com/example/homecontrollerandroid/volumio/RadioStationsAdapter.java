package com.example.homecontrollerandroid.volumio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homecontrollerandroid.R;

import java.util.ArrayList;

public class RadioStationsAdapter extends RecyclerView.Adapter<RadioStationsAdapter.ViewHolder> {

    Context context;
    ArrayList<RadioStation> radioStations;

    public RadioStationsAdapter(Context context, ArrayList<RadioStation> radioStations){
        this.context = context;
        this.radioStations = radioStations;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivRadioLogo;
        TextView tvRadioName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRadioLogo = itemView.findViewById(R.id.ivRadioLogo);
            tvRadioName = itemView.findViewById(R.id.tvRadioName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }

    @NonNull
    @Override
    public RadioStationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_single_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RadioStationsAdapter.ViewHolder holder, int position) {
            holder.itemView.setTag(radioStations.get(position));
            holder.tvRadioName.setText(radioStations.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return radioStations.size();
    }
}
