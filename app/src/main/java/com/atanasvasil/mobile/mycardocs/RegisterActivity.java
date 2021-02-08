package com.atanasvasil.mobile.mycardocs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private TextView registerEmailTV;
    private TextView registerPasswordTV;
    private TextView registerConfirmPasswordTV;
    private TextView registerFirstNameTV;
    private TextView registerLastNameTV;
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
            // Registration button
        });
    }
}