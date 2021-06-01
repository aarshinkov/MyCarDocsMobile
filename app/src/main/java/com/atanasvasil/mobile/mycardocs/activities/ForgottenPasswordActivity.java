package com.atanasvasil.mobile.mycardocs.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.api.UsersApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.atanasvasil.mobile.mycardocs.api.Api.getRetrofit;

public class ForgottenPasswordActivity extends AppCompatActivity {

    private LinearProgressIndicator fpProgress;

    private EditText fpEmailET;
    private MaterialButton fpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);

        getSupportActionBar().setTitle(R.string.forgot_password_title);

        fpProgress = findViewById(R.id.fpProgress);

        fpEmailET = findViewById(R.id.fpEmailET);
        fpBtn = findViewById(R.id.fpBtn);

        fpBtn.setOnClickListener(v -> {

            fpProgress.setVisibility(View.VISIBLE);
            fpBtn.setEnabled(false);

            boolean hasErrors = false;

            if (fpEmailET.getText().toString().isEmpty()) {
                fpEmailET.setError(getString(R.string.forgot_password_email_empty));
                hasErrors = true;
            }

            if (hasErrors) {
                fpProgress.setVisibility(View.INVISIBLE);
                fpBtn.setEnabled(true);
                return;
            }

            Retrofit retrofit = getRetrofit();
            UsersApi usersApi = retrofit.create(UsersApi.class);

            usersApi.forgotPassword(fpEmailET.getText().toString()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NotNull Call<Boolean> call, @NotNull Response<Boolean> response) {

                    if (!response.isSuccessful()) {
                        Snackbar.make(v, R.string.forgot_password_error, Snackbar.LENGTH_LONG).show();
                        fpProgress.setVisibility(View.INVISIBLE);
                        fpBtn.setEnabled(true);
                        return;
                    }

                    Intent intent = new Intent(ForgottenPasswordActivity.this, LoginActivity.class);
                    Toast.makeText(getApplicationContext(), R.string.forgot_password_success, Toast.LENGTH_LONG).show();
                    fpProgress.setVisibility(View.INVISIBLE);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                @Override
                public void onFailure(@NotNull Call<Boolean> call, @NotNull Throwable t) {
                    Snackbar.make(v, R.string.forgot_password_error, Snackbar.LENGTH_LONG).show();
                    fpProgress.setVisibility(View.INVISIBLE);
                    fpBtn.setEnabled(true);
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
