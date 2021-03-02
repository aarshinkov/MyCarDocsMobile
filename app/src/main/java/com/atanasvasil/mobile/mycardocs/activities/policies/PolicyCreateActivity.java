package com.atanasvasil.mobile.mycardocs.activities.policies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PolicyCreateActivity extends AppCompatActivity {

    private EditText policyCreateStartDateET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_create);

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a starting date");
        MaterialDatePicker picker = builder.build();

        policyCreateStartDateET = findViewById(R.id.policyCreateStartDateET);

//        policyCreateStartDateET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                picker.show(getSupportFragmentManager(), picker.toString());
//            }
//        });

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                // Get the offset from our timezone and UTC.
                TimeZone timeZoneUTC = TimeZone.getDefault();
                // It will be negative, so that's the -1
                int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;

                // Create a date format, then a date object with our offset
                SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                Date date = new Date(selection + offsetFromUTC);

                Toast.makeText(getApplicationContext(), simpleFormat.format(date), Toast.LENGTH_LONG).show();
            }
        });

//        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
//
//        MaterialDatePicker<Long> picker = builder.build();
//
//        picker.show(getSupportFragmentManager(), picker.toString());
//
//        picker.addOnPositiveButtonClickListener(selection -> {
//            Toast.makeText(getApplicationContext(), picker.getHeaderText(), Toast.LENGTH_LONG).show();
//        });

//            Log.d("DatePicker Activity", "Date String = ${picker.headerText}:: Date epoch value = ${it}")
//        }
    }
}