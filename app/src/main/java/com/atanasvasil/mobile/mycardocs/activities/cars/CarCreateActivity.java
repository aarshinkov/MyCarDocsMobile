package com.atanasvasil.mobile.mycardocs.activities.cars;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.atanasvasil.mobile.mycardocs.R;

public class CarCreateActivity extends AppCompatActivity {

    private EditText carCreateBrandET;
    private EditText carCreateModelET;
    private EditText carCreateColorET;
    private EditText carCreateYearET;
    private EditText carCreateLicensePlateET;
    private EditText carCreateAliasET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_create);

        carCreateBrandET = findViewById(R.id.carCreateBrandET);
        carCreateModelET = findViewById(R.id.carCreateModelET);
        carCreateColorET = findViewById(R.id.carCreateColorET);
        carCreateYearET = findViewById(R.id.carCreateYearET);
        carCreateLicensePlateET = findViewById(R.id.carCreateLicensePlateET);
        carCreateAliasET = findViewById(R.id.carCreateAliasET);
    }
}