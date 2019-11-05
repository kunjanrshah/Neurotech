package com.krs.neurotech;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    EditText edtPass;
    ImageView imgPass;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        setTitle(getResources().getString(R.string.neurotech));

        if (ContextCompat.checkSelfPermission(
                LoginActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 555);
        }

        sharedPreferences = getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        edtPass = findViewById(R.id.edtPass);
        edtPass.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        imgPass = findViewById(R.id.imgPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            String pass = edtPass.getText().toString().trim();

            if (pass.equalsIgnoreCase(getResources().getString(R.string.login_password))) {
                editor.putString(getResources().getString(R.string.pass_key_sp), pass);
                editor.commit();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

        });


        imgPass.setOnClickListener(v -> {
            if (isVisible) {
                //hide password
                edtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imgPass.setBackground(getResources().getDrawable(R.drawable.hide));
                isVisible = false;
            } else {
                // show password
                edtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imgPass.setBackground(getResources().getDrawable(R.drawable.view));
                isVisible = true;
            }
        });
    }
}
