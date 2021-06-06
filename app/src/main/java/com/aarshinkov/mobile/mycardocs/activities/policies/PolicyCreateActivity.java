package com.aarshinkov.mobile.mycardocs.activities.policies;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aarshinkov.mobile.mycardocs.R;
import com.aarshinkov.mobile.mycardocs.activities.MainActivity;
import com.aarshinkov.mobile.mycardocs.api.CarsApi;
import com.aarshinkov.mobile.mycardocs.api.PoliciesApi;
import com.aarshinkov.mobile.mycardocs.requests.policies.PolicyCreateRequest;
import com.aarshinkov.mobile.mycardocs.responses.cars.Car;
import com.aarshinkov.mobile.mycardocs.responses.policies.Policy;
import com.aarshinkov.mobile.mycardocs.utils.LoggedUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.aarshinkov.mobile.mycardocs.api.Api.getRetrofit;
import static com.aarshinkov.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.aarshinkov.mobile.mycardocs.utils.Utils.getLoggedUser;

public class PolicyCreateActivity extends AppCompatActivity {

    private TextInputLayout policyCreateNumberLabelTV;
    private TextInputEditText policyCreateNumberET;
    private TextInputLayout policyCreateTypeLabelTV;
    private AutoCompleteTextView policyCreateTypeDD;
    private TextInputLayout policyCreateInsNameLabelTV;
    private TextInputEditText policyCreateInsNameET;
    private TextInputLayout policyCreateCarsLabelTV;
    private AutoCompleteTextView policyCreateCarsDD;
    private TextInputLayout policyCreateStartDateLabelTV;
    private TextInputEditText policyCreateStartDateET;
    private TextInputLayout policyCreateEndDateLabelTV;
    private TextInputEditText policyCreateEndDateET;

    private MaterialButton policyCreateBtn;

    private List<String> cars = new ArrayList<>();

    private LoggedUser loggedUser;
    private SharedPreferences pref;

    private ArrayAdapter<String> policiesAdapter;
    private ArrayAdapter<String> carsAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_create);

        getSupportActionBar().setTitle(R.string.policy_create_title);

        policyCreateNumberLabelTV = findViewById(R.id.policyCreateNumberLabelTV);
        policyCreateNumberET = findViewById(R.id.policyCreateNumberET);
        policyCreateTypeLabelTV = findViewById(R.id.policyCreateTypeLabelTV);
        policyCreateTypeDD = findViewById(R.id.policyCreateTypeDD);
        policyCreateInsNameLabelTV = findViewById(R.id.policyCreateInsNameLabelTV);
        policyCreateInsNameET = findViewById(R.id.policyCreateInsNameET);
        policyCreateCarsLabelTV = findViewById(R.id.policyCreateCarsLabelTV);
        policyCreateCarsDD = findViewById(R.id.policyCreateCarsDD);
        policyCreateStartDateLabelTV = findViewById(R.id.policyCreateStartDateLabelTV);
        policyCreateStartDateET = findViewById(R.id.policyCreateStartDateET);
        policyCreateEndDateLabelTV = findViewById(R.id.policyCreateEndDateLabelTV);
        policyCreateEndDateET = findViewById(R.id.policyCreateEndDateET);

        policyCreateBtn = findViewById(R.id.policyCreateBtn);

        pref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        final String[] policyTypes = getResources().getStringArray(R.array.policy_types);

        policiesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, policyTypes);

        policiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        policyCreateTypeDD.setAdapter(policiesAdapter);
        policyCreateTypeDD.setText(policyTypes[0], false);

        loadCars();

        carsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cars);
        carsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        policyCreateStartDateET.setInputType(InputType.TYPE_NULL);
        policyCreateEndDateET.setInputType(InputType.TYPE_NULL);

        policyCreateStartDateET.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                showDateTimeDialog(policyCreateStartDateET, policyCreateStartDateLabelTV);
            }
        });

        policyCreateEndDateET.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                showDateTimeDialog(policyCreateEndDateET, policyCreateEndDateLabelTV);
            }
        });

        policyCreateBtn.setOnClickListener(v -> {

            if (isFieldsInvalid()) {
                return;
            }

            PolicyCreateRequest pcr = new PolicyCreateRequest();

            pcr.setNumber(policyCreateNumberET.getText().toString());
//            Toast.makeText(getApplicationContext(), "Type: " + Arrays.asList(policyTypes).indexOf(policyCreateTypeDD.getText().toString()), Toast.LENGTH_SHORT).show();
            pcr.setType(Arrays.asList(policyTypes).indexOf(policyCreateTypeDD.getText().toString()) + 1);
            pcr.setInsName(policyCreateInsNameET.getText().toString());

//            Toast.makeText(getApplicationContext(), "Car: " + policyCreateCarsDD.getText().toString(), Toast.LENGTH_SHORT).show();
            final String licensePlate = policyCreateCarsDD.getText().toString().split(" - ")[0].trim();

            try {
                DateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_time_default), Locale.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_time_1), Locale.getDefault());

                Date startDate = sdf.parse(policyCreateStartDateET.getText().toString());
                String startFDate = dateFormat.format(startDate);
                pcr.setStartDate(startFDate);

                Date endDate = sdf.parse(policyCreateEndDateET.getText().toString());
                String endFDate = dateFormat.format(endDate);
                pcr.setEndDate(endFDate);

                if (startDate.after(endDate)) {
                    policyCreateEndDateLabelTV.setError(getString(R.string.policy_operation_end_date_before_start_date));
                    return;
                } else {
                    policyCreateEndDateLabelTV.setError(null);
                }

            } catch (Exception ignored) {

            }

            Retrofit retrofit = getRetrofit();
            CarsApi carsApi = retrofit.create(CarsApi.class);
            carsApi.getCarByLicensePlate(licensePlate, loggedUser.getAuthorization()).enqueue(new Callback<Car>() {
                @Override
                public void onResponse(@NotNull Call<Car> call, @NotNull Response<Car> response) {
                    Car car = response.body();

                    if (car == null) {
                        Snackbar.make(v, R.string.policy_create_error, Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    pcr.setCarId(car.getCarId());
                    pcr.setUserId(loggedUser.getUserId());

                    PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);
                    policiesApi.createPolicy(pcr, loggedUser.getAuthorization()).enqueue(new Callback<Policy>() {
                        @Override
                        public void onResponse(@NotNull Call<Policy> call, @NotNull Response<Policy> response) {

                            if (!response.isSuccessful()) {
                                Snackbar.make(v, R.string.policy_create_error, Snackbar.LENGTH_LONG).show();
                                return;
                            }

                            Toast.makeText(getApplicationContext(), R.string.policy_create_success, Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("fragment", "policies");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(@NotNull Call<Policy> call, @NotNull Throwable t) {
                            Snackbar.make(v, R.string.policy_create_error, Snackbar.LENGTH_LONG).show();
                        }
                    });

                }

                @Override
                public void onFailure(@NotNull Call<Car> call, @NotNull Throwable t) {
                    Snackbar.make(v, R.string.policy_create_error, Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }

    public void loadCars() {

        Retrofit retrofit = getRetrofit();
        CarsApi carsApi = retrofit.create(CarsApi.class);

        carsApi.getUserCars(loggedUser.getUserId(), loggedUser.getAuthorization()).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(@NotNull Call<List<Car>> call, @NotNull Response<List<Car>> response) {

                if (response.code() == 400) {
                    return;
                }

                List<Car> storedCars = response.body();

                if (storedCars != null) {
                    for (Car car : storedCars) {
                        cars.add(car.getLicensePlate() + " - " + car.getBrand() + " " + car.getModel());
                    }

                    carsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    policyCreateCarsDD.setAdapter(carsAdapter);

                    Car firstCar = storedCars.get(0);
                    policyCreateCarsDD.setText(firstCar.getLicensePlate() + " - " + firstCar.getBrand() + " " + firstCar.getModel(), false);
                }
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

    private void showDateTimeDialog(final EditText field, TextInputLayout label) {
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
                        label.setError(null);
                    }
                };

                new TimePickerDialog(PolicyCreateActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
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
//        Integer type = policyCreateTypeDD.getSelectedItemPosition() + 1;
        String insName = policyCreateInsNameET.getText().toString();
        String startDate = policyCreateStartDateET.getText().toString();
        String endDate = policyCreateEndDateET.getText().toString();

        if (number.isEmpty()) {
            policyCreateNumberLabelTV.setError(getString(R.string.policy_operation_policy_number_empty));
            hasErrors = true;
        } else {
            policyCreateNumberLabelTV.setError(null);
//            policyCreateNumberLabelTV.setBoxStrokeColor(getResources().getColor(R.color.success, null));
        }

        if (insName.isEmpty()) {
            policyCreateInsNameLabelTV.setError(getString(R.string.policy_operation_insurer_name_empty));
            hasErrors = true;
        } else {
            policyCreateInsNameLabelTV.setError(null);
        }

        if (startDate.isEmpty()) {
            policyCreateStartDateLabelTV.setError(getString(R.string.policy_operation_start_date_empty));
            hasErrors = true;
        } else {
            policyCreateStartDateLabelTV.setError(null);
        }

        if (endDate.isEmpty()) {
            policyCreateEndDateLabelTV.setError(getString(R.string.policy_operation_end_date_empty));
            hasErrors = true;
        } else {
            policyCreateEndDateLabelTV.setError(null);
        }

        return hasErrors;
    }
}