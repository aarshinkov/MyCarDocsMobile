package com.aarshinkov.mobile.mycardocs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aarshinkov.mobile.mycardocs.R;
import com.aarshinkov.mobile.mycardocs.api.UsersApi;
import com.aarshinkov.mobile.mycardocs.responses.users.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.aarshinkov.mobile.mycardocs.utils.AppConstants.API_URL;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmailЕТ;
    private EditText registerPasswordЕТ;
    private EditText registerConfirmPasswordЕТ;
    private EditText registerFirstNameET;
    private EditText registerLastNameET;
    private MaterialButton registerCancelBtn;
    private MaterialButton registerBtn;

    private LinearProgressIndicator registerProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle(R.string.registration_title);

        registerEmailЕТ = findViewById(R.id.registerEmailЕТ);
        registerPasswordЕТ = findViewById(R.id.registerPasswordЕТ);
        registerConfirmPasswordЕТ = findViewById(R.id.registerConfirmPasswordЕТ);
        registerFirstNameET = findViewById(R.id.registerFirstNameET);
        registerLastNameET = findViewById(R.id.registerLastNameET);
        registerCancelBtn = findViewById(R.id.registerCancelBtn);
        registerBtn = findViewById(R.id.registerBtn);

        registerProgress = findViewById(R.id.registerProgress);

        registerCancelBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        registerBtn.setOnClickListener(v -> {
            boolean hasErrors = false;

            registerProgress.setVisibility(View.VISIBLE);
            registerBtn.setEnabled(false);

            final String email = registerEmailЕТ.getText().toString();
            final String password = registerPasswordЕТ.getText().toString();
            final String confirmPassword = registerConfirmPasswordЕТ.getText().toString();
            final String firstName = registerFirstNameET.getText().toString();
            final String lastName = registerLastNameET.getText().toString();

            boolean isEmailEmpty = false;

            if (email.isEmpty()) {
                registerEmailЕТ.setError(getString(R.string.registration_email_empty));
                hasErrors = true;
                isEmailEmpty = true;
            }

            if (password.isEmpty()) {
                registerPasswordЕТ.setError(getString(R.string.registration_password_empty));
                hasErrors = true;
            }

            if (confirmPassword.isEmpty()) {
                registerConfirmPasswordЕТ.setError(getString(R.string.registration_confirm_password_empty));
                hasErrors = true;
            }

            if (!password.isEmpty() && !confirmPassword.isEmpty()) {
                if (!password.equals(confirmPassword)) {
                    registerPasswordЕТ.setError(getString(R.string.registration_passwords_match));
                    registerConfirmPasswordЕТ.setError(getString(R.string.registration_passwords_match));
                    hasErrors = true;
                } else {
                    registerPasswordЕТ.setError(null);
                    registerConfirmPasswordЕТ.setError(null);
                }
            }

            if (firstName.isEmpty()) {
                registerFirstNameET.setError(getString(R.string.registration_first_name_empty));
                hasErrors = true;
            }

            if (lastName.isEmpty()) {
                registerLastNameET.setError(getString(R.string.registration_last_name_empty));
                hasErrors = true;
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            UsersApi usersApi = retrofit.create(UsersApi.class);

            boolean isExist = false;

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            if (!isEmailEmpty) {
                try {
                    isExist = usersApi.isUserExistByEmail(email).execute().body();
                } catch (IOException ignore) {
                }
            }

            if (isExist) {
                registerEmailЕТ.setError(getString(R.string.registration_email_exists));
                hasErrors = true;
            }

            if (hasErrors) {
                registerProgress.setVisibility(View.INVISIBLE);
                registerBtn.setEnabled(true);
                return;
            }

            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);

            usersApi.createUser(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    response.body();

                    Toast.makeText(getApplicationContext(), R.string.registration_success, Toast.LENGTH_LONG).show();

                    registerProgress.setVisibility(View.INVISIBLE);
                    registerBtn.setEnabled(true);

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }


                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.registration_error, Toast.LENGTH_LONG).show();
                    registerProgress.setVisibility(View.INVISIBLE);
                    registerBtn.setEnabled(true);
                }
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }
}


