package com.atanasvasil.mobile.mycardocs.activities.cars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.policies.PolicyActivity;
import com.atanasvasil.mobile.mycardocs.api.Api;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.requests.cars.CarUpdateRequest;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getLoggedUser;

public class CarUpdateActivity extends AppCompatActivity {

    private EditText carUpdateBrandET;
    private EditText carUpdateModelET;
    private EditText carUpdateColorET;
    private Spinner carUpdateTransmissionSP;
    private Spinner carUpdatePowerTypeSP;
    private EditText carUpdateYearET;
    private EditText carUpdateLicensePlateET;
    private EditText carUpdateAliasET;
    private Button carUpdateBtn;

    private LinearProgressIndicator progress;

    private LoggedUser loggedUser;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_update);

        getSupportActionBar().setTitle(getString(R.string.car_update_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = findViewById(R.id.carUpdateProgress);

        carUpdateBrandET = findViewById(R.id.carUpdateBrandET);
        carUpdateModelET = findViewById(R.id.carUpdateModelET);
        carUpdateColorET = findViewById(R.id.carUpdateColorET);
        carUpdateTransmissionSP = findViewById(R.id.carUpdateTransmissionSP);
        carUpdatePowerTypeSP = findViewById(R.id.carUpdatePowerTypeSP);
        carUpdateYearET = findViewById(R.id.carUpdateYearET);
        carUpdateLicensePlateET = findViewById(R.id.carUpdateLicensePlateET);
        carUpdateAliasET = findViewById(R.id.carUpdateAliasET);
        carUpdateBtn = findViewById(R.id.carUpdateBtn);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        Retrofit retrofit = Api.getRetrofit();

        CarsApi carsApi = retrofit.create(CarsApi.class);

        String carId = getIntent().getStringExtra("carId");

        carsApi.getCar(carId, loggedUser.getAuthorization()).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(@NotNull Call<Car> call, @NotNull Response<Car> response) {
                Car car = response.body();
                carUpdateBrandET.setText(car.getBrand());
                carUpdateModelET.setText(car.getModel());
                carUpdateColorET.setText(car.getColor());
                carUpdateTransmissionSP.setSelection(car.getTransmission(), true);
                carUpdatePowerTypeSP.setSelection(car.getPowerType(), true);
                carUpdateYearET.setText(String.valueOf(car.getYear()));
                carUpdateLicensePlateET.setText(car.getLicensePlate());
                carUpdateAliasET.setText(car.getAlias());
            }

            @Override
            public void onFailure(@NotNull Call<Car> call, @NotNull Throwable t) {

            }
        });

        carUpdateBtn.setOnClickListener(v -> {

            progress.setVisibility(View.VISIBLE);

            if (isFieldsInvalid()) {
                progress.setVisibility(View.GONE);
                return;
            }

            CarUpdateRequest cur = new CarUpdateRequest();
            cur.setBrand(carUpdateBrandET.getText().toString());
            cur.setModel(carUpdateModelET.getText().toString());
            cur.setColor(carUpdateColorET.getText().toString());
            int transmission = carUpdateTransmissionSP.getSelectedItemPosition();
            cur.setTransmission(transmission);
            int powerType = carUpdatePowerTypeSP.getSelectedItemPosition();
            cur.setPowerType(powerType);
            cur.setYear(Integer.parseInt(carUpdateYearET.getText().toString()));
            cur.setLicensePlate(carUpdateLicensePlateET.getText().toString());
            cur.setAlias(carUpdateAliasET.getText().toString());
            cur.setUserId(loggedUser.getUserId());

            carsApi.updateCar(carId, cur, loggedUser.getAuthorization()).enqueue(new Callback<Car>() {
                @Override
                public void onResponse(@NotNull Call<Car> call, @NotNull Response<Car> response) {

                    Toast.makeText(getApplicationContext(), R.string.car_update_success, Toast.LENGTH_LONG).show();
                    finish();

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

        String brand = carUpdateBrandET.getText().toString();
        String model = carUpdateModelET.getText().toString();
        String color = carUpdateColorET.getText().toString();
        Integer year = 0;

        try {
            year = Integer.parseInt(carUpdateYearET.getText().toString());
        } catch (NumberFormatException e) {
            carUpdateYearET.setError(getString(R.string.car_operation_invalid_format));
            hasErrors = true;
        }

        String licensePlate = carUpdateLicensePlateET.getText().toString();

        if (brand == null || brand.isEmpty()) {
            carUpdateBrandET.setError(getString(R.string.car_operation_brand_empty));
            hasErrors = true;
        }

        if (model == null || model.isEmpty()) {
            carUpdateModelET.setError(getString(R.string.car_operation_model_empty));
            hasErrors = true;
        }

        if (color == null || color.isEmpty()) {
            carUpdateColorET.setError(getString(R.string.car_operation_color_empty));
            hasErrors = true;
        }

        if (year == null) {
            carUpdateYearET.setError(getString(R.string.car_operation_year_empty));
            hasErrors = true;
        }

        if (licensePlate == null || licensePlate.isEmpty()) {
            carUpdateLicensePlateET.setError(getString(R.string.car_operation_license_plate_empty));
            hasErrors = true;
        }

        return hasErrors;
    }
}