package com.atanasvasil.mobile.mycardocs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.api.Api;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmailET = findViewById(R.id.loginEmailET);
        loginPasswordET = findViewById(R.id.loginPasswordET);
        loginBtn = findViewById(R.id.loginBtn);
        loginRegisterTV = findViewById(R.id.loginRegisterTV);
        loginForgotPasswordTV = findViewById(R.id.loginForgotPasswordTV);

        loginBtn.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Login button clicked", Toast.LENGTH_LONG).show();
        });

        loginRegisterTV.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginForgotPasswordTV.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Forgot password button clicked", Toast.LENGTH_LONG).show();
        });

        Retrofit retrofit = Api.getRetrofit();

        UsersApi usersApi = retrofit.create(UsersApi.class);

        usersApi.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> users = response.body();

                for (User user : users) {
                    Log.d("USER", "user: " + user.getEmail());
                }

                // ... do whatever you want with users list
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("ERROR", "onFailure: ", t);
            }
        });
    }
}