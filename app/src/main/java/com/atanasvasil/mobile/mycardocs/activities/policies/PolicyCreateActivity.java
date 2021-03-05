package com.atanasvasil.mobile.mycardocs.activities.policies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.MainActivity;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.api.PoliciesApi;
import com.atanasvasil.mobile.mycardocs.requests.policies.PolicyCreateRequest;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.responses.policies.Policy;
import com.atanasvasil.mobile.mycardocs.responses.users.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    private Button policyCreateBtn;

    private List<String> cars = new ArrayList<>();

    private SharedPreferences pref;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_create);

        getSupportActionBar().setTitle("Create a policy");

        policyCreateNumberET = findViewById(R.id.policyCreateNumberET);
        policyCreateTypeSP = findViewById(R.id.policyCreateTypeSP);
        policyCreateInsNameET = findViewById(R.id.policyCreateInsNameET);
        policyCreateCarsSP = findViewById(R.id.policyCreateCarsSP);
        policyCreateStartDateET = findViewById(R.id.policyCreateStartDateET);
        policyCreateEndDateET = findViewById(R.id.policyCreateEndDateET);

        policyCreateBtn = findViewById(R.id.policyCreateBtn);

        pref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        User user = getLoggedUser(pref);

        loadCars(user.getUserId());

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
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

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
            carsApi.getCarByLicensePlate(licensePlate).enqueue(new Callback<Car>() {
                @Override
                public void onResponse(Call<Car> call, Response<Car> response) {
                    Car car = response.body();
                    pcr.setCarId(car.getCarId());

                    PoliciesApi policiesApi = retrofit.create(PoliciesApi.class);
                    policiesApi.createPolicy(pcr).enqueue(new Callback<Policy>() {
                        @Override
                        public void onResponse(Call<Policy> call, Response<Policy> response) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("fragment", "policies");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<Policy> call, Throwable t) {

                        }
                    });

                }

                @Override
                public void onFailure(Call<Car> call, Throwable t) {

                }
            });
        });
    }

    public void loadCars(Long userId) {

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
                policyCreateCarsSP.setAdapter(adapter);
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

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

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
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
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
            policyCreateNumberET.setError("Policy number must not be empty");
            hasErrors = true;
        }

        if (insName == null || insName.isEmpty()) {
            policyCreateInsNameET.setError("Insurer name must not be empty");
            hasErrors = true;
        }

        if (startDate == null || startDate.isEmpty()) {
            policyCreateStartDateET.setError("Start date must not be empty");
            hasErrors = true;
        }

        if (endDate == null || startDate.isEmpty()) {
            policyCreateEndDateET.setError("End date must not be empty");
            hasErrors = true;
        }

        return hasErrors;
    }
}