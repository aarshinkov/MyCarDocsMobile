package com.atanasvasil.mobile.mycardocs.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.api.Api;
import com.atanasvasil.mobile.mycardocs.api.AuthApi;
import com.atanasvasil.mobile.mycardocs.responses.users.User;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_USER;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailET;
    private EditText loginPasswordET;
    private Button loginBtn;
    private TextView loginRegisterTV;
    private TextView loginForgotPasswordTV;
    private ProgressDialog dialog;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmailET = findViewById(R.id.loginEmailET);
        loginPasswordET = findViewById(R.id.loginPasswordET);
        loginBtn = findViewById(R.id.loginBtn);
        loginRegisterTV = findViewById(R.id.loginRegisterTV);
        loginForgotPasswordTV = findViewById(R.id.loginForgotPasswordTV);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = pref.edit();

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

                    Gson gson = new Gson();
                    String json = gson.toJson(user);

                    editor.putString(SHARED_PREF_USER, json);
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "Hello, " + user.getFullName(), Toast.LENGTH_LONG).show();
                    dialog.hide();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


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

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
            i.putExtra(Intent.EXTRA_TEXT, "body of email");
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(LoginActivity.this,
                        "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}