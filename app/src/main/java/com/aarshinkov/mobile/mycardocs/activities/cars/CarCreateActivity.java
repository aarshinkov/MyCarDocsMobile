package com.aarshinkov.mobile.mycardocs.activities.cars;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aarshinkov.mobile.mycardocs.R;
import com.aarshinkov.mobile.mycardocs.activities.MainActivity;
import com.aarshinkov.mobile.mycardocs.api.Api;
import com.aarshinkov.mobile.mycardocs.api.CarsApi;
import com.aarshinkov.mobile.mycardocs.requests.cars.CarCreateRequest;
import com.aarshinkov.mobile.mycardocs.responses.cars.Car;
import com.aarshinkov.mobile.mycardocs.utils.LoggedUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.aarshinkov.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.aarshinkov.mobile.mycardocs.utils.Utils.getLoggedUser;

public class CarCreateActivity extends AppCompatActivity {

    private TextInputLayout carCreateBrandLabelTV;
    private TextInputEditText carCreateBrandET;

    private TextInputLayout carCreateModelLabelTV;
    private TextInputEditText carCreateModelET;

    private TextInputLayout carCreateColorLabelTV;
    private AutoCompleteTextView carCreateColorDD;

    private AutoCompleteTextView carCreateTransmissionDD;
    private AutoCompleteTextView carCreatePowerTypeDD;

    private TextInputLayout carCreateYearLabelTV;
    private TextInputEditText carCreateYearET;

    private TextInputLayout carCreateLicensePlateLabelTV;
    private TextInputEditText carCreateLicensePlateET;

    private EditText carCreateAliasET;
    private MaterialButton carCreateBtn;

    private LoggedUser loggedUser;
    private SharedPreferences pref;

    private LinearProgressIndicator progress;

    private ArrayAdapter<String> colorsAdapter;
    private ArrayAdapter<String> transmissionsAdapter;
    private ArrayAdapter<String> powerTypesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_create);

        getSupportActionBar().setTitle(getString(R.string.car_create_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = findViewById(R.id.carCreateProgress);

        carCreateBrandLabelTV = findViewById(R.id.carCreateBrandLabelTV);
        carCreateBrandET = findViewById(R.id.carCreateBrandET);

        carCreateModelLabelTV = findViewById(R.id.carCreateModelLabelTV);
        carCreateModelET = findViewById(R.id.carCreateModelET);

        carCreateColorLabelTV = findViewById(R.id.carCreateColorLabelTV);

        carCreateColorDD = findViewById(R.id.carCreateColorDD);
        carCreateTransmissionDD = findViewById(R.id.carCreateTransmissionDD);
        carCreatePowerTypeDD = findViewById(R.id.carCreatePowerTypeDD);

        carCreateYearLabelTV = findViewById(R.id.carCreateYearLabelTV);
        carCreateYearET = findViewById(R.id.carCreateYearET);

        carCreateLicensePlateLabelTV = findViewById(R.id.carCreateLicensePlateLabelTV);
        carCreateLicensePlateET = findViewById(R.id.carCreateLicensePlateET);

        final String[] colors = getResources().getStringArray(R.array.car_colors);

        colorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);

        colorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        carCreateColorDD.setAdapter(colorsAdapter);

        final String[] transmissions = getResources().getStringArray(R.array.car_transmissions);

        transmissionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, transmissions);

        transmissionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        carCreateTransmissionDD.setAdapter(transmissionsAdapter);
        carCreateTransmissionDD.setText(transmissions[0], false);

        final String[] powerTypes = getResources().getStringArray(R.array.car_power_types);

        powerTypesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, powerTypes);

        powerTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        carCreatePowerTypeDD.setAdapter(powerTypesAdapter);
        carCreatePowerTypeDD.setText(powerTypes[0], false);

        carCreateAliasET = findViewById(R.id.carCreateAliasET);
        carCreateBtn = findViewById(R.id.carCreateBtn);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        carCreateLicensePlateET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.equals(text.toUpperCase())) {
                    text = text.toUpperCase();
                    carCreateLicensePlateET.setText(text);
                    carCreateLicensePlateET.setSelection(carCreateLicensePlateET.length()); //fix reverse texting
                }
            }
        });

        carCreateBtn.setOnClickListener(v -> {

            progress.setVisibility(View.VISIBLE);

            if (isFieldsInvalid()) {
                progress.setVisibility(View.GONE);
                return;
            }

            CarCreateRequest ccr = new CarCreateRequest();
            ccr.setBrand(carCreateBrandET.getText().toString());
            ccr.setModel(carCreateModelET.getText().toString());
            ccr.setColor(carCreateColorDD.getText().toString());
            final int transmission = Arrays.asList(transmissions).indexOf(carCreateTransmissionDD.getText().toString());
            ccr.setTransmission(transmission);
            final int powerType = Arrays.asList(powerTypes).indexOf(carCreatePowerTypeDD.getText().toString());
            ccr.setPowerType(powerType);
            ccr.setYear(Integer.parseInt(carCreateYearET.getText().toString().trim()));
            ccr.setLicensePlate(carCreateLicensePlateET.getText().toString().trim());
            ccr.setAlias(carCreateAliasET.getText().toString());
            ccr.setUserId(loggedUser.getUserId());

            Retrofit retrofit = Api.getRetrofit();
            CarsApi carsApi = retrofit.create(CarsApi.class);

            carsApi.createCar(ccr, loggedUser.getAuthorization()).enqueue(new Callback<Car>() {
                @Override
                public void onResponse(@NotNull Call<Car> call, @NotNull Response<Car> response) {

                    if (response.code() == 403) {
                        progress.setVisibility(View.GONE);
                        return;
                    }

                    Toast.makeText(getApplicationContext(), R.string.car_create_success, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("fragment", "cars");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NotNull Call<Car> call, @NotNull Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
                    progress.setVisibility(View.GONE);
                }
            });
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }

    private boolean isFieldsInvalid() {

        boolean hasErrors = false;

        final String brand = carCreateBrandET.getText().toString();
        final String model = carCreateModelET.getText().toString();
        final String color = carCreateColorDD.getText().toString();
        Integer year = null;

        try {
            year = Integer.parseInt(carCreateYearET.getText().toString());
            carCreateYearLabelTV.setError(null);
        } catch (NumberFormatException e) {
            carCreateYearLabelTV.setError(getString(R.string.car_operation_invalid_format));
            hasErrors = true;
        }

        final String licensePlate = carCreateLicensePlateET.getText().toString();

        if (brand.isEmpty()) {
            carCreateBrandLabelTV.setError(getString(R.string.car_operation_brand_empty));
            hasErrors = true;
        } else {
            carCreateBrandLabelTV.setError(null);
        }

        if (model.isEmpty()) {
            carCreateModelLabelTV.setError(getString(R.string.car_operation_model_empty));
            hasErrors = true;
        } else {
            carCreateModelLabelTV.setError(null);
        }

        if (color.isEmpty()) {
            carCreateColorLabelTV.setError(getString(R.string.car_operation_color_empty));
            hasErrors = true;
        } else {
            carCreateColorLabelTV.setError(null);
        }

        if (year == null) {
            carCreateYearLabelTV.setError(getString(R.string.car_operation_year_empty));
            hasErrors = true;
        } else {
            carCreateYearLabelTV.setError(null);
        }

        if (licensePlate.isEmpty()) {
            carCreateLicensePlateLabelTV.setError(getString(R.string.car_operation_license_plate_empty));
            hasErrors = true;
        } else {
            carCreateLicensePlateLabelTV.setError(null);
        }

        return hasErrors;
    }
}