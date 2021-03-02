package com.atanasvasil.mobile.mycardocs.activities.policies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PolicyCreateActivity extends AppCompatActivity {

    private EditText policyCreateStartDateET;

    private Button policyCreateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_create);

        policyCreateStartDateET = findViewById(R.id.policyCreateStartDateET);
        policyCreateBtn = findViewById(R.id.policyCreateBtn);

        policyCreateStartDateET.setInputType(InputType.TYPE_NULL);

        policyCreateStartDateET.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_LONG).show();
            showDateTimeDialog(policyCreateStartDateET);
        });

        policyCreateBtn.setOnClickListener(v -> {

            try {
                Date date1 = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(policyCreateStartDateET.getText().toString());
                Toast.makeText(getApplicationContext(), date1.toString(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }
        });
    }

    private void showDateTimeDialog(final EditText field) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                        field.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(PolicyCreateActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(PolicyCreateActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void showDateDialog(final EditText date_in) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                policyCreateStartDateET.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}