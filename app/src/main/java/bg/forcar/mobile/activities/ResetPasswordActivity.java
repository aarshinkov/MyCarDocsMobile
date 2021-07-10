package bg.forcar.mobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import bg.forcar.mobile.R;
import bg.forcar.mobile.api.UsersApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static bg.forcar.mobile.api.Api.getRetrofit;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputLayout resetPasswordNewPasswordLabelTV;
    private TextInputEditText resetPasswordNewPasswordET;

    private TextInputLayout resetPasswordConfirmPasswordLabelTV;
    private TextInputEditText resetPasswordConfirmPasswordET;

    private MaterialButton resetPasswordSaveBtn;

    private LinearProgressIndicator resetPasswordProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetPasswordNewPasswordLabelTV = findViewById(R.id.resetPasswordNewPasswordLabelTV);
        resetPasswordNewPasswordET = findViewById(R.id.resetPasswordNewPasswordET);

        resetPasswordConfirmPasswordLabelTV = findViewById(R.id.resetPasswordConfirmPasswordLabelTV);
        resetPasswordConfirmPasswordET = findViewById(R.id.resetPasswordConfirmPasswordET);

        resetPasswordSaveBtn = findViewById(R.id.resetPasswordSaveBtn);

        resetPasswordProgress = findViewById(R.id.resetPasswordProgress);

        Intent receiveIntent = getIntent();
//        String action = receiveIntent.getAction();
        Uri data = receiveIntent.getData();
        final String code = data.getQueryParameter("c");

        resetPasswordSaveBtn.setOnClickListener(v -> {

            resetPasswordProgress.setVisibility(View.VISIBLE);
            resetPasswordSaveBtn.setEnabled(false);

            if (hasErrors()) {
                resetPasswordProgress.setVisibility(View.INVISIBLE);
                resetPasswordSaveBtn.setEnabled(true);
                return;
            }

            Retrofit retrofit = getRetrofit();
            UsersApi usersApi = retrofit.create(UsersApi.class);

            usersApi.resetPassword(resetPasswordNewPasswordET.getText().toString(), code).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NotNull Call<Boolean> call, @NotNull Response<Boolean> response) {

                    if (!response.isSuccessful()) {
                        Snackbar.make(v, R.string.reset_password_error, Snackbar.LENGTH_LONG).show();
                        resetPasswordProgress.setVisibility(View.INVISIBLE);
                        resetPasswordSaveBtn.setEnabled(true);
                        return;
                    }

                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    Toast.makeText(getApplicationContext(), R.string.reset_password_success, Toast.LENGTH_LONG).show();
                    resetPasswordProgress.setVisibility(View.INVISIBLE);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                @Override
                public void onFailure(@NotNull Call<Boolean> call, @NotNull Throwable t) {
                    Snackbar.make(v, R.string.reset_password_error, Snackbar.LENGTH_LONG).show();
                    resetPasswordProgress.setVisibility(View.INVISIBLE);
                    resetPasswordSaveBtn.setEnabled(true);
                }
            });
        });
    }

    private boolean hasErrors() {

        boolean hasErrors = false;
        final String newPassword = resetPasswordNewPasswordET.getText().toString();
        final String confirmPassword = resetPasswordConfirmPasswordET.getText().toString();

        if (newPassword.isEmpty()) {
            resetPasswordNewPasswordLabelTV.setError(getString(R.string.reset_password_new_password_error));
            hasErrors = true;
        }

        if (confirmPassword.isEmpty()) {
            resetPasswordConfirmPasswordLabelTV.setError(getString(R.string.reset_password_confirm_password_error));
            hasErrors = true;
        }

        if (!hasErrors) {
            if (!newPassword.equals(confirmPassword)) {
                resetPasswordNewPasswordLabelTV.setError(getString(R.string.reset_password_passwords_mismatch));
                resetPasswordConfirmPasswordLabelTV.setError(getString(R.string.reset_password_passwords_mismatch));
                hasErrors = true;
            }
        }

        if (!hasErrors) {
            resetPasswordNewPasswordLabelTV.setError(null);
            resetPasswordConfirmPasswordLabelTV.setError(null);
        }

        return hasErrors;
    }
}
