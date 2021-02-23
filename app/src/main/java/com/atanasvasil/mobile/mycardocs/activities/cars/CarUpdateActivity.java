package com.atanasvasil.mobile.mycardocs.activities.cars;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.atanasvasil.mobile.mycardocs.R;

public class CarUpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_update);

        getSupportActionBar().setTitle("Edit a car");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}