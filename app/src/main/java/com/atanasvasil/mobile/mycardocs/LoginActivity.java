package com.atanasvasil.mobile.mycardocs;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.api.Api;
import com.atanasvasil.mobile.mycardocs.api.AuthApi;
import com.atanasvasil.mobile.mycardocs.api.UsersApi;
import com.atanasvasil.mobile.mycardocs.responses.users.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailET;
    private EditText loginPasswordET;
    private Button loginBtn;
    private TextView loginRegisterTV;
    private TextView loginForgotPasswordTV;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmailET = findViewById(R.id.loginEmailET);
        loginPasswordET = findViewById(R.id.loginPasswordET);
        loginBtn = findViewById(R.id.loginBtn);
        loginRegisterTV = findViewById(R.id.loginRegisterTV);
        loginForgotPasswordTV = findViewById(R.id.loginForgotPasswordTV);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in...");

        loginBtn.setOnClickListener(v -> {
            dialog.show();

            String email = loginEmailET.getText().toString();
            String password = loginPasswordET.getText().toString();

            boolean hasErrors = false;

            if (email.isEmpty()) {
                loginEmailET.setError("Email must not be empty");
                hasErrors = true;
            }

            if (password.isEmpty()) {
                loginPasswordET.setError("Password must not be empty");
                hasErrors = true;
            }

            if (hasErrors) {
                dialog.hide();
                return;
            }

            Retrofit retrofit = Api.getRetrofit();

            AuthApi authApi = retrofit.create(AuthApi.class);
            authApi.login(email, password).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() == 400) {
                        Toast.makeText(getApplicationContext(), "Invalid email/password!", Toast.LENGTH_LONG).show();
                        dialog.hide();
                        return;
                    }

                    User user = response.body();

                    Toast.makeText(getApplicationContext(), "Hello, " + user.getFullName(), Toast.LENGTH_LONG).show();
                    dialog.hide();

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error logging you in!", Toast.LENGTH_LONG).show();
                    dialog.hide();
                }
            });
        });

        loginRegisterTV.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginForgotPasswordTV.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Forgot password button clicked", Toast.LENGTH_LONG).show();
        });
    }
}