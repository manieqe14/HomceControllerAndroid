package com.example.homecontrollerandroid.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.homecontrollerandroid.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.net.Inet4Address;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    FloatingActionButton fabSave;
    SettingsSingleton settingsSingleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        setTitle("Settings");

        settingsSingleton = SettingsSingleton.getInstance(SettingsActivity.this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fabSave = findViewById(R.id.fabSave);

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);

                    String volumioAddress = sharedPreferences.getString("volumio_address", "");
                    int suplaRows = Integer.parseInt(sharedPreferences.getString("suplaRows", "1"));
                    int units = Integer.parseInt(sharedPreferences.getString("tempUnit", Integer.toString(ApplicationClass.CELSIUS_UNITS)));
                    String suplaAddress = sharedPreferences.getString("supla_address", "");
                    Set<String> volumioVisibility = sharedPreferences.getStringSet("volumio_visibility", null);
                    Set<String> generalVisibility = sharedPreferences.getStringSet("general_visibility", null);
                    int airlyMaxDist = sharedPreferences.getInt(getString(R.string.max_dist), 5);
                    int airlyStationQuantity = sharedPreferences.getInt(getString(R.string.station_quantity), 3);

                    settingsSingleton.addSetting(SettingsSingleton.SETTING_NAME.VOLUMIO_ADDRESS, checkVolumioAddressFormat(volumioAddress));
                    settingsSingleton.addSetting(SettingsSingleton.SETTING_NAME.SUPLA_ROWS, String.valueOf(suplaRows));
                    settingsSingleton.addSetting(SettingsSingleton.SETTING_NAME.SENSORS_UNITS, String.valueOf(units));
                    settingsSingleton.addSetting(SettingsSingleton.SETTING_NAME.SUPLA_ADDRESS, checkSuplaAddressFormat(suplaAddress));
                    settingsSingleton.addSetting(SettingsSingleton.SETTING_NAME.VOLUMIO_VISIBILITY, volumioVisibility);
                    settingsSingleton.addSetting(SettingsSingleton.SETTING_NAME.GENERAL_VISIBILITY, generalVisibility);
                    settingsSingleton.addSetting(SettingsSingleton.SETTING_NAME.AIRLY_MAX_DIST,String.valueOf(airlyMaxDist));

                    Intent intent = new Intent();
                    intent.putExtra("volumio_address", volumioAddress);
                    intent.putExtra("suplaRows", suplaRows);
                    intent.putExtra("units", units);

                    setResult(RESULT_OK, intent);

                    SettingsActivity.this.finish();
            }
        });


    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);


        }

    }

    private String checkSuplaAddressFormat(String address){

        StringBuffer addressBuffer = new StringBuffer(address);

        if(address.charAt(address.length()-1) == '/')
        {
            addressBuffer.deleteCharAt(addressBuffer.length()-1);
        }

        addressBuffer.insert(0, "https://");
        return addressBuffer.toString();
    }

    private String checkVolumioAddressFormat(String address){

        StringBuffer addressBuffer = new StringBuffer(address);
        if((address.length() > 0) && (address != null)) {
            if (address.charAt(address.length() - 1) == '/') {
                addressBuffer.deleteCharAt(addressBuffer.length() - 1);
            }
        }

        addressBuffer.insert(0, "http://");
        return addressBuffer.toString();

    }


}