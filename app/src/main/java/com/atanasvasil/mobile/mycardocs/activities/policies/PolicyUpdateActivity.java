package com.atanasvasil.mobile.mycardocs.activities.policies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.api.PoliciesApi;
import com.atanasvasil.mobile.mycardocs.requests.policies.PolicyUpdateRequest;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.responses.policies.Policy;
import com.atanasvasil.mobile.mycardocs.responses.users.User;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.atanasvasil.mobile.mycardocs.api.Api.getRetrofit;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getLoggedUser;

public class PolicyUpdateActivity extends AppCompatActivity {

    private EditText policyUpdateNumberET;
    private Spinner policyUpdateTypeSP;
    private EditText policyUpdateInsNameET;
    private Spinner policyUpdateCarsSP;
    private EditText policyUpdateStartDateET;
    private EditText policyUpdateEndDateET;

    private Button policyUpdateBtn;

    private List<String> cars = new ArrayList<>();

    private SharedPreferences pref;

    private ArrayAdapter<String> adapter;

    private LinearProgressIndicator progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_update);

        getSupportActionBar().setTitle(R.string.policy_update_title);

        progress = findViewById(R.id.policyUpdateProgress);

        String policyId = getIntent().getStringExtra("policyId");

        policyUpdateNumberET = findViewById(R.id.policyUpdateNumberET);
        policyUpdateTypeSP = findViewById(R.id.policyUpdateTypeSP);
        policyUpdateInsNameET = findViewById(R.id.policyUpdateInsNameET);
        policyUpdateCarsSP = findViewById(R.id.policyUpdateCarsSP);
        policyUpdateStartDateET = findViewById(R.id.policyUpdateStartDateET);
        policyUpdateEndDateET = findViewById(R.id.policyUpdateEndDateET);

        policyUpdateBtn = findViewById(R.id.policyUpdateBtn);

        pref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        LoggedUser loggedUser = getLoggedUser(pref);

        loadCars(loggedUser.getUserId());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cars);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        policyUpdateStartDateET.setInputType(InputType.TYPE_NULL);
        policyUpdateEndDateET.setInputType(InputType.TYPE_NULL);

        policyUpdateStartDateET.setOnClickListener(v -> {
            showDateTimeDialog(policyUpdateStartDateET);
        });

        policyUpdateEndDateET.setOnClickListener(v -> {
            showDateTimeDialog(policyUpdateEndDateET);
        });

        Retrofit retrofit = getRetrofit();

        PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);
        policiesApi.getPolicy(policyId).enqueue(new Callback<Policy>() {
            @Override
            public void onResponse(Call<Policy> call, Response<Policy> response) {

                Policy policy = response.body();
                policyUpdateNumberET.setText(policy.getNumber());
                policyUpdateTypeSP.setSelection(policy.getType() - 1);
                policyUpdateInsNameET.setText(policy.getInsName());

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_time_1), Locale.getDefault());

                date.setTime(policy.getStartDate().getTime());
                policyUpdateStartDateET.setText(sdf.format(date));

                date = new Date();
                date.setTime(policy.getEndDate().getTime());
                policyUpdateEndDateET.setText(sdf.format(date));

                ArrayAdapter<String> adapter = (ArrayAdapter<String>) policyUpdateCarsSP.getAdapter();
                policyUpdateCarsSP.setSelection(adapter.getPosition(policy.getCar().getLicensePlate()));
            }

            @Override
            public void onFailure(Call<Policy> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
            }
        });

        policyUpdateBtn.setOnClickListener(v -> {

            progress.setVisibility(View.VISIBLE);

            if (isFieldsInvalid()) {
                return;
            }

            PolicyUpdateRequest pur = new PolicyUpdateRequest();

            pur.setPolicyId(policyId);
            pur.setNumber(policyUpdateNumberET.getText().toString());
            pur.setType(policyUpdateTypeSP.getSelectedItemPosition() + 1);
            pur.setInsName(policyUpdateInsNameET.getText().toString());

            String licensePlate = policyUpdateCarsSP.getSelectedItem().toString();

            try {
                DateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_time_default), Locale.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_time_1), Locale.getDefault());

                Date startDate = sdf.parse(policyUpdateStartDateET.getText().toString());
                String startFDate = dateFormat.format(startDate);
                pur.setStartDate(startFDate);

                Date endDate = sdf.parse(policyUpdateEndDateET.getText().toString());
                String endFDate = dateFormat.format(endDate);
                pur.setEndDate(endFDate);

            } catch (Exception e) {
                Log.e("ERROR", "Some error", e);
            }

            CarsApi carsApi = retrofit.create(CarsApi.class);
            carsApi.getCarByLicensePlate(licensePlate).enqueue(new Callback<Car>() {
                @Override
                public void onResponse(Call<Car> call, Response<Car> response) {
                    Car car = response.body();
                    pur.setCarId(car.getCarId());

                    PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);
                    policiesApi.updatePolicy(pur).enqueue(new Callback<Policy>() {
                        @Override
                        public void onResponse(Call<Policy> call, Response<Policy> response) {

                            Toast.makeText(getApplicationContext(), R.string.policy_update_success, Toast.LENGTH_LONG).show();
                            finish();

                            progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<Policy> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
                            progress.setVisibility(View.GONE);
                        }
                    });

                }

                @Override
                public void onFailure(Call<Car> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
                    progress.setVisibility(View.GONE);
                }
            });
        });
    }

    public void loadCars(String userId) {

        Retrofit retrofit = getRetrofit();
        CarsApi carsApi = retrofit.create(CarsApi.class);

        carsApi.getUserCars(userId).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {

                if (response.code() == 400) {
                    return;
                }

                List<Car> storedCars = response.body();

                for (Car car : storedCars) {
                    cars.add(car.getLicensePlate());
                }

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                policyUpdateCarsSP.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
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

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_time_1), Locale.getDefault());

                        field.setText(simpleDateFormat.format(calendar.getTime()));
                        field.setError(null);
                    }
                };

                new TimePickerDialog(PolicyUpdateActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(PolicyUpdateActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private boolean isFieldsInvalid() {

        boolean hasErrors = false;

        String number = policyUpdateNumberET.getText().toString();
        Integer type = policyUpdateTypeSP.getSelectedItemPosition() + 1;
        String insName = policyUpdateInsNameET.getText().toString();
        String startDate = policyUpdateStartDateET.getText().toString();
        String endDate = policyUpdateEndDateET.getText().toString();

        if (number == null || number.isEmpty()) {
            policyUpdateNumberET.setError(getString(R.string.policy_operation_policy_number_empty));
            hasErrors = true;
        }

        if (insName == null || insName.isEmpty()) {
            policyUpdateInsNameET.setError(getString(R.string.policy_operation_insurer_name_empty));
            hasErrors = true;
        }

        if (startDate == null || startDate.isEmpty()) {
            policyUpdateStartDateET.setError(getString(R.string.policy_operation_start_date_empty));
            hasErrors = true;
        }

        if (endDate == null || startDate.isEmpty()) {
            policyUpdateEndDateET.setError(getString(R.string.policy_operation_end_date_empty));
            hasErrors = true;
        }

        return hasErrors;
    }
}