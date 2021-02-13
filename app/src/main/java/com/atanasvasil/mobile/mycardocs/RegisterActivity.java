package com.atanasvasil.mobile.mycardocs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.api.UsersApi;
import com.atanasvasil.mobile.mycardocs.responses.users.User;

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
    private ProgressDialog dialog;

    boolean isUserExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle(R.string.registration);

        //Initialize fields
        //...

        registerEmailЕТ = findViewById(R.id.registerEmailЕТ);
        registerPasswordЕТ = findViewById(R.id.registerPasswordЕТ);
        registerConfirmPasswordЕТ = findViewById(R.id.registerConfirmPasswordЕТ);
        registerFirstNameET = findViewById(R.id.registerFirstNameET);
        registerLastNameET = findViewById(R.id.registerLastNameET);
        registerCancelBtn = findViewById(R.id.registerCancelBtn);
        registerBtn = findViewById(R.id.registerBtn);

        dialog = new ProgressDialog(this);

        registerCancelBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Cancel button
        });

        registerBtn.setOnClickListener(v -> {

            dialog.setTitle("Registration in progress...");
            dialog.show();

            boolean hasErrors = false;

            String email = registerEmailЕТ.getText().toString();
            String password = registerPasswordЕТ.getText().toString();
            String confirmPassword = registerConfirmPasswordЕТ.getText().toString();
            String firstName = registerFirstNameET.getText().toString();
            String lastName = registerLastNameET.getText().toString();

            if (email.isEmpty()) {
                registerEmailЕТ.setError("Email must not be empty!");
                hasErrors = true;
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            UsersApi usersApi = retrofit.create(UsersApi.class);

            usersApi.isUserExistByEmail(email).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    isUserExist = response.body();

                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                }
            });

            if (isUserExist) {
                registerEmailЕТ.setError("The Email is registered!");
                hasErrors = true;
            }
            if (!password.equals(confirmPassword)) {
                registerPasswordЕТ.setError("Passwords must match!");
                registerConfirmPasswordЕТ.setError("Passwords must match!");
                hasErrors = true;
            }
            if (firstName.isEmpty()) {
                registerFirstNameET.setError("The first name field is empty!");
                hasErrors = true;
            }

            if (lastName.isEmpty()) {
                registerLastNameET.setError("The last name field is empty!");
                hasErrors = true;
            }

            if (!hasErrors) {
                // DATABASE

                User user = new User();
                user.setEmail(email);
                user.setPassword(password);
                user.setFirstName(firstName);
                user.setLastName(lastName);

                usersApi.createUser(user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        User createdUser = response.body();

                        Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
            }

            dialog.hide();

//            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//            startActivity(intent);

        });
    }
}


