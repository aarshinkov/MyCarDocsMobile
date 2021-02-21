package com.atanasvasil.mobile.mycardocs.activities.cars;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.atanasvasil.mobile.mycardocs.R;

public class CarCreateActivity extends AppCompatActivity {

    private EditText brandET;
    private EditText modelET;
    private EditText colorET;
    private EditText yearNumberText;
    private EditText licensePlateET;
    private EditText aliasET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_create);

        brandET=findViewById(R.id.BrandET);
        modelET=findViewById(R.id.ModelET);
        colorET=findViewById(R.id.ColorET);
        yearNumberText=findViewById(R.id.YearNumberText);
        licensePlateET=findViewById(R.id.LicensePlateET);
        aliasET=findViewById(R.id.AliasET);
    }
}