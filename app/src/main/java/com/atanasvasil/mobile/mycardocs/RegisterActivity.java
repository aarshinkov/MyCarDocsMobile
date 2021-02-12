package com.atanasvasil.mobile.mycardocs;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.responses.users.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmailЕТ;
    private EditText registerPasswordЕТ;
    private EditText registerConfirmPasswordЕТ;
    private EditText registerFirstNameЕТ;
    private EditText registerLastNameЕT;
    private Button registerCancelBtn;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle(R.string.registration);

        //Initialize fields
        //...

        registerCancelBtn = findViewById(R.id.registerCancelBtn);
        registerBtn = findViewById(R.id.registerBtn);

        registerCancelBtn.setOnClickListener(v -> {
            // Cancel button
        });

        registerBtn.setOnClickListener(v -> {
            if (v.getId() == R.id.registerBtn) {
                if (registerEmailЕТ.getText().length() > 0
                        && registerPasswordЕТ.getText().length() > 0
                        && registerPasswordЕТ.getText().toString().equals(registerConfirmPasswordЕТ.getText().toString())) {

                    String password = registerPasswordЕТ.getText().toString();
                    String firstName = registerFirstNameЕТ.getText().toString();
                    String lastName = registerLastNameЕT.getText().toString();

                    User user = new User();
                    user.setPassword(password);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);

                    new RegisterAsyncTask(user).execute();

                } else {
                    Toast.makeText(RegisterActivity.this,
                            "please fill in all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        private class RegisterAsyncTask extends AsyncTask<Void, Void, Void> {

            User user;
            boolean isSuccess = false;

            ProgressDialog dialog;

            RegisterAsyncTask(User user) {
                this.user = user;
                dialog = new ProgressDialog(RegisterActivity.this);
            }

            @Override
            protected void onPreExecute() {
                dialog.setTitle("Registration in progress...");
                dialog.show();
            }
            // Registration button

        }
    }
}