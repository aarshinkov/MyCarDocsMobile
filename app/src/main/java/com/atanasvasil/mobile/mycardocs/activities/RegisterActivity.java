package com.atanasvasil.mobile.mycardocs.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.api.UsersApi;
import com.atanasvasil.mobile.mycardocs.responses.users.User;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.API_URL;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmailЕТ;
    private EditText registerPasswordЕТ;
    private EditText registerConfirmPasswordЕТ;
    private EditText registerFirstNameET;
    private EditText registerLastNameET;
    private Button registerCancelBtn;
    private Button registerBtn;
    public static final String MyPreferences = "MyPrefs";

    private ProgressDialog dialog;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle(R.string.registration);

        registerEmailЕТ = findViewById(R.id.registerEmailЕТ);
        registerPasswordЕТ = findViewById(R.id.registerPasswordЕТ);
        registerConfirmPasswordЕТ = findViewById(R.id.registerConfirmPasswordЕТ);
        registerFirstNameET = findViewById(R.id.registerFirstNameET);
        registerLastNameET = findViewById(R.id.registerLastNameET);
        registerCancelBtn = findViewById(R.id.registerCancelBtn);
        registerBtn = findViewById(R.id.registerBtn);
        sharedpreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Registration in progress...");

        registerCancelBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        registerBtn.setOnClickListener(v -> {
//            Toast.makeText(getApplicationContext(), "Register clicked", Toast.LENGTH_LONG).show();
            boolean hasErrors = false;

            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            final String email = registerEmailЕТ.getText().toString();
            final String password = registerPasswordЕТ.getText().toString();
            final String confirmPassword = registerConfirmPasswordЕТ.getText().toString();
            final String firstName = registerFirstNameET.getText().toString();
            final String lastName = registerLastNameET.getText().toString();

            if (email.isEmpty()) {
                registerEmailЕТ.setError("Email must not be empty!");
                hasErrors = true;
            }

            if (password.isEmpty()) {
                registerPasswordЕТ.setError("Password must not be empty!");
                hasErrors = true;
            }

            if (confirmPassword.isEmpty()) {
                registerConfirmPasswordЕТ.setError("Confirm password must not be empty!");
                hasErrors = true;
            }

            if (!password.equals(confirmPassword)) {
                registerPasswordЕТ.setError("Passwords must match!");
                registerConfirmPasswordЕТ.setError("Passwords must match!");
                hasErrors = true;
            }
            if (firstName.isEmpty()) {
                registerFirstNameET.setError("The first name must not be empty!");
                hasErrors = true;
            }

            if (lastName.isEmpty()) {
                registerLastNameET.setError("The last name field must not be empty!");
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

            try {
                isExist = usersApi.isUserExistByEmail(email).execute().body();
            } catch (IOException ignore) {
            }

            if (isExist) {
                registerEmailЕТ.setError("This email is already registered!");
                hasErrors = true;
            }

            if (hasErrors) {
//                Toast.makeText(getApplicationContext(), "Some errors", Toast.LENGTH_LONG).show();
                dialog.hide();
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

                    Toast.makeText(getApplicationContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();

                    dialog.hide();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(email, registerEmailЕТ);
                    editor.putString(registerFirstNameET, firstName);
                    editor.putString(registerPasswordЕТ, password);
                    editor.commit();
                    Toast.makeText(RegisterActivity.this, "Thanks", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    dialog.hide();
                }
            });
        });
    }
}


