package com.example.homecontrollerandroid.main;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.example.homecontrollerandroid.R;
import com.example.homecontrollerandroid.supla.Sensors;
import com.example.homecontrollerandroid.supla.SuplaDevice;

import java.util.List;

import weborb.util.log.Log;

public class LoginActivity extends AppCompatActivity {

    EditText etUserName, etUserPassword;
    Button btnLogin, btnRegister;
    TextView tvResetPass, tvLoadStatus;
    ProgressBar pbLoginProgress;
    LinearLayout llLoginLayout;

    private static int suplaScenesDataCompleted = 0;
    private static int suplaDevicesDataCompleted = 0;
    private static int sensorsDataCompleted = 0;
    private SettingsSingleton  settingsSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUserName = findViewById(R.id.etUserMail);
        etUserPassword = findViewById(R.id.etUserPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvResetPass = findViewById(R.id.tvResetPass);
        tvLoadStatus = findViewById(R.id.tvLoadStatus);
        pbLoginProgress = findViewById(R.id.pbLoginProgress);

        llLoginLayout = findViewById(R.id.llLoginLayout);/*
        settingsSingleton = SettingsSingleton.getInstance(LoginActivity.this);
        settingsSingleton.addSetting(SettingsSingleton.SETTING_NAME.SUPLA_ROWS, "1");*/

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etUserName.getText().toString().isEmpty() || etUserPassword.getText().toString().isEmpty())
                    Toast.makeText(LoginActivity.this, R.string.please_enter_all_fields, Toast.LENGTH_SHORT).show();

                else
                {
                    String name = etUserName.getText().toString().trim();
                    String password = etUserPassword.getText().toString().trim();
                    showLayout(false);

                    tvLoadStatus.setText(R.string.logging);
                    tvLoadStatus.setVisibility(View.VISIBLE);
                    pbLoginProgress.setVisibility(View.VISIBLE);

                    Backendless.UserService.login(name, password, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {

                            ApplicationClass.user = response;
                            MainActivity.showToast(getString(R.string.login_success), LoginActivity.this);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            MainActivity.showToast(getString(R.string.log_error), LoginActivity.this);
                            showLayout(true);

                        }
                    }, true);
                }

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });

        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {

                if(response)
                {
                    String userObjectId = UserIdStorageFactory.instance().getStorage().get();
                    tvLoadStatus.setText(R.string.logging);
                    tvLoadStatus.setVisibility(View.VISIBLE);
                    pbLoginProgress.setVisibility(View.VISIBLE);
                    showLayout(false);


                    Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {

                            ApplicationClass.user = response;
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();
                            tvLoadStatus.setText(R.string.login_success);

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            MainActivity.showToast(getString(R.string.error) + ": " + fault.getMessage(), LoginActivity.this);
                            showLayout(true);

                        }
                    });
                }
                else
                    showLayout(true);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                showLayout(true);

            }
        });

    }

    private void showLayout(boolean show){

        Animation animationIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.slide_down);
        Animation animationOut = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.slide_up);

        if(show)
        {
            /*etUserName.setVisibility(View.VISIBLE);
            etUserPassword.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
            tvResetPass.setVisibility(View.VISIBLE);*/
            llLoginLayout.setAnimation(animationIn);
            llLoginLayout.setVisibility(View.VISIBLE);

        }
        else {
            /*etUserName.setVisibility(View.GONE);
            etUserPassword.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            tvResetPass.setVisibility(View.GONE);*/
            llLoginLayout.setAnimation(animationOut);
            llLoginLayout.setVisibility(View.GONE);
        }
    }


}