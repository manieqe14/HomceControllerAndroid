package com.example.homecontrollerandroid.main;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.homecontrollerandroid.airly.AirlyFrag;
import com.example.homecontrollerandroid.supla.SuplaDevice;
import com.example.homecontrollerandroid.volumio.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homecontrollerandroid.R;
import com.nambimobile.widgets.efab.FabOption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SuplaDevicesAdapter.OnLongClickMethod, newSceneDialog.OnExitListener{


    TextView tvVolumioTitle, tvVolumioArtist, tvVolumioAlbum;
    TextView tvVOLUMIO_TITLE, tvVOLUMIO_ALBUM, tvVOLUMIO_ARTIST;
    private final int SETTINGS = 3;
    SharedPreferences prefs;
    suplaFrag SuplaFrag;
    volumioFrag VolumioFrag;
    AirlyFrag airlyFrag;
    FragmentManager fragmentManager;
    DecimalFormat dataFormat;
    FabOption fabOptionCreateNewScene, fabOptionRefreshLayout, fabOptionNewSensor;
    dataFrag dataFragment;
    SettingsSingleton settingsSingleton;

    private ArrayList<SuplaDevice> devices;

    private String volumioAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvVolumioTitle = findViewById(R.id.tvVolumioTitle);
        tvVolumioAlbum = findViewById(R.id.tvVolumioAlbum);
        tvVolumioArtist = findViewById(R.id.tvVolumioArtist);
        tvVOLUMIO_ALBUM = findViewById(R.id.tvVOLUMIO_ALBUM);
        tvVOLUMIO_ARTIST = findViewById(R.id.tvVOLUMIO_ARTIST);
        tvVOLUMIO_TITLE = findViewById(R.id.tvVOLUMIO_TITLE);

        settingsSingleton = SettingsSingleton.getInstance(MainActivity.this);

        fabOptionCreateNewScene = findViewById(R.id.fabOptionCreateNewScene);
        fabOptionCreateNewScene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refreshDB();

                newSceneDialog NewSceneDialog = new newSceneDialog(MainActivity.this, devices);
                NewSceneDialog.show(getSupportFragmentManager(), null);

            }
        });

        fabOptionRefreshLayout = findViewById(R.id.fabOptionRefreshLayout);
        fabOptionRefreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new httpInBackground().execute(volumioRequest.getINFO(volumioAddress));
                airlyFrag.forceToRetrieveNewData();
                dataFragment.retrieveNewData();
                showToast(getString(R.string.data_update), MainActivity.this);
            }
        });

        fabOptionNewSensor = findViewById(R.id.fabOptionNewSensor);
        fabOptionNewSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataFragment.ShowDialogNewSensor();

            }
        });

        fragmentManager = this.getSupportFragmentManager();
        dataFragment = (dataFrag) fragmentManager.findFragmentById(R.id.dataFrag);

        dataFormat = new DecimalFormat("##.##");

        SuplaFrag = (suplaFrag) fragmentManager.findFragmentById(R.id.suplaFrag);
        VolumioFrag = (volumioFrag) fragmentManager.findFragmentById(R.id.volumioFrag);
        airlyFrag = (AirlyFrag) fragmentManager.findFragmentById(R.id.airlyFrag);

        refreshLayout();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                break;

            case R.id.settings:

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS);
                break;

            case R.id.devices:
                refreshDB();
                Intent intent1 = new Intent(MainActivity.this, suplaDevicesList.class);
                startActivity(intent1);
                break;

            case R.id.logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.sure)
                        .setTitle(R.string.logout);

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                });

                builder.setPositiveButton(R.string.im_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Backendless.UserService.logout(new AsyncCallback<Void>() {
                            @Override
                            public void handleResponse(Void response) {
                                Toast.makeText(MainActivity.this, "User logged out!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(MainActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        ApplicationClass.user.clearProperties();
                        dialogInterface.dismiss();
                    }
                });

                builder.show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void editionView(SuplaDevice device) {

    }

    @Override
    public void onDismiss() {

        SuplaFrag.notifySuplaAdapterChanged();
    }

    public class httpInBackground extends AsyncTask<String, Integer, String> {

        StringBuffer server_response = new StringBuffer();
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(5);
            dialog.show();


        }

        @Override
        protected String doInBackground(String... strings) {

            publishProgress(1);
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                publishProgress(2);
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                server_response.append(readStream(urlConnection.getInputStream()));
                publishProgress(3);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, R.string.wrong_address, Toast.LENGTH_SHORT).show();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                    publishProgress(4);
                }
                return server_response.toString();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            dialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.isEmpty())
                Toast.makeText(MainActivity.this, R.string.vol_data_error, Toast.LENGTH_SHORT).show();
            else {
                tvVolumioAlbum.setText(getFromVolumioJSON.getAlbum(s));
                tvVolumioArtist.setText(getFromVolumioJSON.getArtist(s));
                tvVolumioTitle.setText(getFromVolumioJSON.getTitle(s));
            }
            dialog.dismiss();
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == SETTINGS) {
            SuplaFrag.notifySuplaAdapterChanged();
            refreshLayout();

        } else {
            showToast(getString(R.string.data_not_saved), this);
        }

    }

    private void refreshLayout() {

        Set<String> volumioVsibility = settingsSingleton.getSettingSet(SettingsSingleton.SETTING_NAME.VOLUMIO_VISIBILITY);
        Set<String> generalVisibility = settingsSingleton.getSettingSet(SettingsSingleton.SETTING_NAME.GENERAL_VISIBILITY);
        volumioAddress = settingsSingleton.getSetting(SettingsSingleton.SETTING_NAME.VOLUMIO_ADDRESS);

        if(generalVisibility!=null) {
            if (generalVisibility.contains("sensors"))
                fragmentManager.beginTransaction().show(dataFragment).commit();
            else
                fragmentManager.beginTransaction().hide(dataFragment).commit();

            if (generalVisibility.contains("airly"))
                fragmentManager.beginTransaction().show(airlyFrag).commit();
            else
                fragmentManager.beginTransaction().hide(airlyFrag).commit();

            if (generalVisibility.contains("scenes"))
                fragmentManager.beginTransaction().show(SuplaFrag).commit();
            else
                fragmentManager.beginTransaction().hide(SuplaFrag).commit();

            if (generalVisibility.contains("volumio"))
                fragmentManager.beginTransaction().show(VolumioFrag).commit();
            else
                fragmentManager.beginTransaction().hide(VolumioFrag).commit();
        }



        if (volumioVsibility != null){
            if (volumioVsibility.contains("0")) {
                tvVolumioAlbum.setVisibility(View.VISIBLE);
                tvVOLUMIO_ALBUM.setVisibility(View.VISIBLE);
            } else {
                tvVolumioAlbum.setVisibility(View.GONE);
                tvVOLUMIO_ALBUM.setVisibility(View.GONE);
            }

            if (volumioVsibility.contains("1")) {
                tvVolumioArtist.setVisibility(View.VISIBLE);
                tvVOLUMIO_ARTIST.setVisibility(View.VISIBLE);
            } else {
                tvVolumioArtist.setVisibility(View.GONE);
                tvVOLUMIO_ARTIST.setVisibility(View.GONE);
            }

            if (volumioVsibility.contains("2")) {
                tvVolumioTitle.setVisibility(View.VISIBLE);
                tvVOLUMIO_TITLE.setVisibility(View.VISIBLE);
            } else {
                tvVolumioTitle.setVisibility(View.GONE);
                tvVolumioTitle.setVisibility(View.GONE);
            }
        }
        new httpInBackground().execute(volumioRequest.getINFO(volumioAddress));

    }

    public static void showToast(String message, Context contextToast){

        LayoutInflater inflater = ((Activity) contextToast).getLayoutInflater();

        View toastView = inflater.inflate(R.layout.custom_toast, (ViewGroup) ((Activity) contextToast).findViewById(R.id.linlay));

        TextView tvText = toastView.findViewById(R.id.tvText);
        tvText.setText(message);

        Toast toast = new Toast(contextToast);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastView);
        toast.setGravity(Gravity.BOTTOM|Gravity.FILL_HORIZONTAL,0, 0);
        toast.show();
    }

    protected void refreshDB(){
        devices = settingsSingleton.getSuplaDevicesList();

    }




}
