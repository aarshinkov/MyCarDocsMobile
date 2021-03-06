package bg.forcar.mobile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import bg.forcar.mobile.R;
import bg.forcar.mobile.api.Api;
import bg.forcar.mobile.api.AuthApi;
import bg.forcar.mobile.responses.AuthenticationResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_AUTH;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_NAME;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_ROLES;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_USER_EMAIL;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_USER_FIRST_NAME;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_USER_ID;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_USER_LAST_NAME;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout loginEmailLabelTV;
    private TextInputEditText loginEmailET;

    private TextInputLayout loginPasswordLabelTV;
    private TextInputEditText loginPasswordET;

    private MaterialButton loginBtn;
    private TextView loginRegisterTV;
    private TextView loginForgotPasswordTV;
    private LinearProgressIndicator loginProgress;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmailLabelTV = findViewById(R.id.loginEmailLabelTV);
        loginEmailET = findViewById(R.id.loginEmailET);

        loginPasswordLabelTV = findViewById(R.id.loginPasswordLabelTV);
        loginPasswordET = findViewById(R.id.loginPasswordET);

        loginBtn = findViewById(R.id.loginBtn);
        loginRegisterTV = findViewById(R.id.loginRegisterTV);
        loginForgotPasswordTV = findViewById(R.id.loginForgotPasswordTV);

        loginProgress = findViewById(R.id.loginProgress);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = pref.edit();

        loginBtn.setOnClickListener(v -> {

            loginProgress.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);

            String email = loginEmailET.getText().toString();
            String password = loginPasswordET.getText().toString();

            boolean hasErrors = false;

            if (email.isEmpty()) {
                loginEmailLabelTV.setError(getString(R.string.login_email_empty));
                hasErrors = true;
            }

            if (password.isEmpty()) {
                loginPasswordLabelTV.setError(getString(R.string.login_password_empty));
                hasErrors = true;
            }

            if (hasErrors) {
                loginProgress.setVisibility(View.INVISIBLE);
                loginBtn.setEnabled(true);
                return;
            }

            Retrofit retrofit = Api.getRetrofit();

            AuthApi authApi = retrofit.create(AuthApi.class);
            authApi.login(email, password).enqueue(new Callback<AuthenticationResponse>() {
                @Override
                public void onResponse(@NotNull Call<AuthenticationResponse> call, @NotNull Response<AuthenticationResponse> response) {

                    if (response.code() == 400) {
                        Snackbar.make(v, R.string.login_invalid_credentials, Snackbar.LENGTH_LONG).show();
                        loginEmailLabelTV.setError(getString(R.string.login_invalid_credentials));
                        loginPasswordLabelTV.setError(getString(R.string.login_invalid_credentials));
                        loginProgress.setVisibility(View.INVISIBLE);
                        loginBtn.setEnabled(true);
                        return;
                    }

                    AuthenticationResponse auth = response.body();

                    if (auth == null) {
                        loginProgress.setVisibility(View.INVISIBLE);
                        loginBtn.setEnabled(true);
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
                    loginProgress.setVisibility(View.INVISIBLE);
                    loginBtn.setEnabled(true);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                @Override
                public void onFailure(@NotNull Call<AuthenticationResponse> call, @NotNull Throwable t) {
                    Snackbar.make(v, R.string.login_error, Snackbar.LENGTH_LONG).show();
                    loginProgress.setVisibility(View.INVISIBLE);
                    loginBtn.setEnabled(true);
                }
            });
        });


        loginRegisterTV.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginForgotPasswordTV.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgottenPasswordActivity.class);
            startActivity(intent);
        });
    }
}