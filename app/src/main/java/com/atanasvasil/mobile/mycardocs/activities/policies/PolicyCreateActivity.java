package com.atanasvasil.mobile.mycardocs.activities.policies;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.MainActivity;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.api.PoliciesApi;
import com.atanasvasil.mobile.mycardocs.requests.policies.PolicyCreateRequest;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.responses.policies.Policy;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

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

public class PolicyCreateActivity extends AppCompatActivity {

    private EditText policyCreateNumberET;
    private Spinner policyCreateTypeSP;
    private EditText policyCreateInsNameET;
    private Spinner policyCreateCarsSP;
    private EditText policyCreateStartDateET;
    private EditText policyCreateEndDateET;

    private MaterialButton policyCreateBtn;

    private List<String> cars = new ArrayList<>();

    private LoggedUser loggedUser;
    private SharedPreferences pref;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_create);

        getSupportActionBar().setTitle(R.string.policy_create_title);

        policyCreateNumberET = findViewById(R.id.policyCreateNumberET);
        policyCreateTypeSP = findViewById(R.id.policyCreateTypeSP);
        policyCreateInsNameET = findViewById(R.id.policyCreateInsNameET);
        policyCreateCarsSP = findViewById(R.id.policyCreateCarsSP);
        policyCreateStartDateET = findViewById(R.id.policyCreateStartDateET);
        policyCreateEndDateET = findViewById(R.id.policyCreateEndDateET);

        policyCreateBtn = findViewById(R.id.policyCreateBtn);

        pref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        loadCars(loggedUser.getUserId());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cars);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        policyCreateStartDateET.setInputType(InputType.TYPE_NULL);
        policyCreateEndDateET.setInputType(InputType.TYPE_NULL);

        policyCreateStartDateET.setOnClickListener(v -> {
            showDateTimeDialog(policyCreateStartDateET);
        });

        policyCreateEndDateET.setOnClickListener(v -> {
            showDateTimeDialog(policyCreateEndDateET);
        });

        policyCreateBtn.setOnClickListener(v -> {

            if (isFieldsInvalid()) {
                return;
            }

            PolicyCreateRequest pcr = new PolicyCreateRequest();

            pcr.setNumber(policyCreateNumberET.getText().toString());
            pcr.setType(policyCreateTypeSP.getSelectedItemPosition() + 1);
            pcr.setInsName(policyCreateInsNameET.getText().toString());

            String licensePlate = policyCreateCarsSP.getSelectedItem().toString();

            try {
                DateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_time_default), Locale.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_time_1), Locale.getDefault());

                Date startDate = sdf.parse(policyCreateStartDateET.getText().toString());
                String startFDate = dateFormat.format(startDate);
                pcr.setStartDate(startFDate);

                Date endDate = sdf.parse(policyCreateEndDateET.getText().toString());
                String endFDate = dateFormat.format(endDate);
                pcr.setEndDate(endFDate);

            } catch (Exception e) {

            }
            Retrofit retrofit = getRetrofit();
            CarsApi carsApi = retrofit.create(CarsApi.class);
            carsApi.getCarByLicensePlate(licensePlate, loggedUser.getAuthorization()).enqueue(new Callback<Car>() {
                @Override
                public void onResponse(@NotNull Call<Car> call, @NotNull Response<Car> response) {
                    Car car = response.body();
                    pcr.setCarId(car.getCarId());
                    pcr.setUserId(loggedUser.getUserId());

                    PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);
                    policiesApi.createPolicy(pcr, loggedUser.getAuthorization()).enqueue(new Callback<Policy>() {
                        @Override
                        public void onResponse(@NotNull Call<Policy> call, @NotNull Response<Policy> response) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("fragment", "policies");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(@NotNull Call<Policy> call, @NotNull Throwable t) {

                        }
                    });

                }

                @Override
                public void onFailure(@NotNull Call<Car> call, @NotNull Throwable t) {

                }
            });
        });
    }

    public void loadCars(String userId) {

        Retrofit retrofit = getRetrofit();
        CarsApi carsApi = retrofit.create(CarsApi.class);

        carsApi.getUserCars(userId, loggedUser.getAuthorization()).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(@NotNull Call<List<Car>> call, @NotNull Response<List<Car>> response) {

                if (response.code() == 400) {
                    return;
                }

                List<Car> storedCars = response.body();

                for (Car car : storedCars) {
                    cars.add(car.getLicensePlate());
                }

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                policyCreateCarsSP.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NotNull Call<List<Car>> call, @NotNull Throwable t) {
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
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_1), Locale.getDefault());
                policyCreateStartDateET.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean isFieldsInvalid() {

        boolean hasErrors = false;

        String number = policyCreateNumberET.getText().toString();
        Integer type = policyCreateTypeSP.getSelectedItemPosition() + 1;
        String insName = policyCreateInsNameET.getText().toString();
        String startDate = policyCreateStartDateET.getText().toString();
        String endDate = policyCreateEndDateET.getText().toString();

        if (number == null || number.isEmpty()) {
            policyCreateNumberET.setError(getString(R.string.policy_operation_policy_number_empty));
            hasErrors = true;
        }

        if (insName == null || insName.isEmpty()) {
            policyCreateInsNameET.setError(getString(R.string.policy_operation_insurer_name_empty));
            hasErrors = true;
        }

        if (startDate == null || startDate.isEmpty()) {
            policyCreateStartDateET.setError(getString(R.string.policy_operation_start_date_empty));
            hasErrors = true;
        }

        if (endDate == null || startDate.isEmpty()) {
            policyCreateEndDateET.setError(getString(R.string.policy_operation_end_date_empty));
            hasErrors = true;
        }

        return hasErrors;
    }
}