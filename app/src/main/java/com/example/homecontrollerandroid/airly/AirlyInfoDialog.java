package com.example.homecontrollerandroid.airly;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import com.example.homecontrollerandroid.R;
import com.example.homecontrollerandroid.main.MainActivity;
import com.example.homecontrollerandroid.main.SettingsSingleton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class AirlyInfoDialog extends AppCompatDialogFragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean locationPermissionGranted;
    private Context context;
    private GoogleMap airlyMap;
    View v;

    private static final String TAG = AirlyInfoDialog.class.getSimpleName();
    private CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final LatLng defaultLocation = new LatLng(50.015242, 20.027013);
    private static final int DEFAULT_ZOOM = 15;
    private Location lastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private ArrayList<MarkerMap> markersList = new ArrayList<>();

    SettingsSingleton settingsSingleton;
    Button btnAirlyMapCancel, btnAirlyMapOK;
    TextView tvAirlyMarkerDetail;

    MarkerMap selectedMarker;
    AirlyFrag airlyFrag;

    public AirlyInfoDialog(final Context context, AirlyFrag airlyFrag) {
        this.context = context;
        this.airlyFrag = airlyFrag;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if(dialog != null){
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.horizontalMargin = 10;
            params.verticalMargin = 10;

            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.dialog_airly_info, null);

        final SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.airlyMap);
        mapFragment.getMapAsync(this);
        settingsSingleton = SettingsSingleton.getInstance(context);

        btnAirlyMapCancel = v.findViewById(R.id.btnAirlyMapCancel);
        btnAirlyMapOK = v.findViewById(R.id.btnAirlyMapOK);

        tvAirlyMarkerDetail = v.findViewById(R.id.tvAirlyMarkerDetail);

        btnAirlyMapOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedMarker == null)
                {
                    MainActivity.showToast("Select some statoon!", context);
                }
                else
                {
                    settingsSingleton.addSetting(SettingsSingleton.SETTING_NAME.AIRLY_NEAREST, selectedMarker.getStationJSON().toString());
                    airlyFrag.ViewLocationData();
                    airlyFrag.forceToRetrieveNewData();
                    dismiss();
                }


            }
        });

        btnAirlyMapCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });


        return v;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        airlyMap = googleMap;
        airlyMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        updateLocationUI();
        getDeviceLocation();

        airlyMap.setInfoWindowAdapter(new MarkerWindow());

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:
                if((grantResults.length > 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    MainActivity.showToast("Permission granted!", context);
                    locationPermissionGranted = true;
                }


        }
    }

    private void updateLocationUI(){
        if(airlyMap == null){
            return;
        }
        try{
            if(locationPermissionGranted){
                airlyMap.setMyLocationEnabled(true);
                airlyMap.getUiSettings().setMyLocationButtonEnabled(true);
            }else{
                airlyMap.setMyLocationEnabled(false);
                airlyMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        }catch(SecurityException e)
        {
            MainActivity.showToast(e.getMessage(), context);
        }
    }

    private void getLocationPermission(){
        if((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            locationPermissionGranted = true;
        }
        else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation(){
        try{
            if(locationPermissionGranted){
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>(){
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()) {
                            lastKnownLocation = task.getResult();

                            if (lastKnownLocation != null) {
                                LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                airlyMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title("Your position");
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                airlyMap.addMarker(markerOptions);

                                airlyDataGetter airlyGetter = new airlyDataGetter(AirlyInfoDialog.this);
                                airlyGetter.getDataForStations(airlyDataGetter.getNearestLink(latLng.latitude, latLng.longitude,
                                        Double.parseDouble(settingsSingleton.getSetting(SettingsSingleton.SETTING_NAME.AIRLY_MAX_DIST)),
                                        Integer.parseInt(settingsSingleton.getSetting(SettingsSingleton.SETTING_NAME.AIRLY_STATION_QUANTITY))));

                                MainActivity.showToast("Location retrieved!", context);
                            }
                        }
                        else{
                            airlyMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,DEFAULT_ZOOM));
                            airlyMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }

                    }
                });
            }
        }catch (SecurityException e)
        {
            MainActivity.showToast(e.getMessage(), context);
        }
    }

    public void addMarkers(String markers){

        JSONArray markersArray = null;
        markersList = new ArrayList<>();
        try {
            markersArray = new JSONArray(markers);
            for (int i = 0; i < markersArray.length(); i++) {
                markersList.add(new MarkerMap(markersArray.getJSONObject(i)));
            }
        }
        catch (JSONException e) {
            MainActivity.showToast(e.getMessage(),context);
        }

        for (MarkerMap m : markersList){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(m.getLatLng());
            markerOptions.title(m.getAddress());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            airlyMap.addMarker(markerOptions).setTag(m);
        }

    }

    private class MarkerWindow implements GoogleMap.InfoWindowAdapter{
        @Override
        public View getInfoWindow(final Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(final Marker marker) {

            for(MarkerMap m : markersList) {
                if(marker.getTag() == m){
                    tvAirlyMarkerDetail.setText("Chosen station: \n" + (m.getAddress()));
                    selectedMarker = m;
                }
            }
            return null;
        }
    }

}
