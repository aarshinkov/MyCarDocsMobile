package com.atanasvasil.mobile.mycardocs;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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
            // Registration button
        });
    }
}