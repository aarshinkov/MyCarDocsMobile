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
import com.atanasvasil.mobile.mycardocs.responses.AuthenticationResponse;
import com.atanasvasil.mobile.mycardocs.responses.users.User;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_AUTH;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_ROLES;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_USER_EMAIL;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_USER_FIRST_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_USER_ID;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_USER_LAST_NAME;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailET;
    private EditText loginPasswordET;
    private Button loginBtn;
    private TextView loginRegisterTV;
    private TextView loginForgotPasswordTV;
    private ProgressDialog progress;

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

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.login_process));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);

        loginBtn.setOnClickListener(v -> {
            progress.show();

            String email = loginEmailET.getText().toString();
            String password = loginPasswordET.getText().toString();

            boolean hasErrors = false;

            if (email.isEmpty()) {
                loginEmailET.setError(getString(R.string.login_email_empty));
                hasErrors = true;
            }

            if (password.isEmpty()) {
                loginPasswordET.setError(getString(R.string.login_password_empty));
                hasErrors = true;
            }

            if (hasErrors) {
                progress.hide();
                return;
            }

            Retrofit retrofit = Api.getRetrofit();

            AuthApi authApi = retrofit.create(AuthApi.class);
            authApi.login(email, password).enqueue(new Callback<AuthenticationResponse>() {
                @Override
                public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                    if (response.code() == 400) {
                        Toast.makeText(getApplicationContext(), "Invalid email/password!", Toast.LENGTH_LONG).show();
                        loginEmailET.setError("Invalid email/password!");
                        loginPasswordET.setError("Invalid email/password!");
                        progress.hide();
                        return;
                    }

                    AuthenticationResponse auth = response.body();

                    if (auth == null) {
                        return;
                    }

                    String authorizationHeader = auth.getTokenType() + " " + auth.getAccessToken();
                    editor.putString(SHARED_PREF_AUTH, authorizationHeader);

                    editor.putString(SHARED_PREF_USER_ID, auth.getUser().getUserId());
                    editor.putString(SHARED_PREF_USER_EMAIL, auth.getUser().getEmail());
                    editor.putString(SHARED_PREF_USER_FIRST_NAME, auth.getUser().getFirstName());
                    editor.putString(SHARED_PREF_USER_LAST_NAME, auth.getUser().getLastName());

                    Gson gson = new Gson();
                    String json = gson.toJson(auth.getUser().getRoles());
                    editor.putString(SHARED_PREF_ROLES, json);

                    editor.apply();

                    Toast.makeText(getApplicationContext(), getString(R.string.login_welcome, auth.getUser().getFullName()), Toast.LENGTH_LONG).show();
                    progress.hide();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                    progress.hide();
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