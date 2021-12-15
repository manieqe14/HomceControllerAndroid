package com.example.homecontrollerandroid.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.homecontrollerandroid.R;

public class RegisterActivity extends AppCompatActivity {

    EditText etRegisterName, etRegisterMail, etRegisterPass, etRegisterRePass;
    Button btnRegisterUser;
    TextView tvRegisterLoadStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegisterName = findViewById(R.id.etRegisterName);
        etRegisterMail = findViewById(R.id.etRegisterMail);
        etRegisterPass = findViewById(R.id.etRegisterPass);
        etRegisterRePass = findViewById(R.id.etRegisterRePass);
        btnRegisterUser = findViewById(R.id.btnRegisterUser);
        tvRegisterLoadStatus = findViewById(R.id.tvRegisterLoadStatus);

        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etRegisterName.getText().toString().isEmpty() || etRegisterMail.getText().toString().isEmpty() ||
                    etRegisterPass.getText().toString().isEmpty() || etRegisterRePass.getText().toString().isEmpty())
                    Toast.makeText(RegisterActivity.this, R.string.please_enter_all_fields, Toast.LENGTH_SHORT).show();

                else{

                    if(etRegisterPass.getText().toString().equals(etRegisterRePass.getText().toString()))
                    {
                        String name = etRegisterName.getText().toString().trim();
                        String mail = etRegisterMail.getText().toString().trim();
                        String pass = etRegisterPass.getText().toString().trim();

                        BackendlessUser user = new BackendlessUser();
                        user.setEmail(mail);
                        user.setPassword(pass);
                        user.setProperty("name", name);

                        tvRegisterLoadStatus.setText(R.string.registering_user);

                        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {

                                Toast.makeText(RegisterActivity.this, R.string.register_succes, Toast.LENGTH_SHORT).show();
                                RegisterActivity.this.finish();

                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {

                                Toast.makeText(RegisterActivity.this, R.string.error + ": "+ fault.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });


                    }

                    else{
                        Toast.makeText(RegisterActivity.this, R.string.passwords_not_same, Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


    }
}