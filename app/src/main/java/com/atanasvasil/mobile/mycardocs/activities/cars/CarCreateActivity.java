package com.atanasvasil.mobile.mycardocs.activities.cars;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.MainActivity;
import com.atanasvasil.mobile.mycardocs.api.Api;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.requests.cars.CarCreateRequest;
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

public class CarCreateActivity extends AppCompatActivity {

    private EditText carCreateBrandET;
    private EditText carCreateModelET;
    private EditText carCreateColorET;
    private Spinner carCreateTransmissionSP;
    private Spinner carCreatePowerTypeSP;
    private EditText carCreateYearET;
    private EditText carCreateLicensePlateET;
    private EditText carCreateAliasET;
    private Button carCreateBtn;

    private SharedPreferences pref;

    private LinearProgressIndicator progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_create);

        getSupportActionBar().setTitle(getString(R.string.car_create_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = findViewById(R.id.carCreateProgress);

        carCreateBrandET = findViewById(R.id.carCreateBrandET);
        carCreateModelET = findViewById(R.id.carCreateModelET);
        carCreateColorET = findViewById(R.id.carCreateColorET);
        carCreateTransmissionSP = findViewById(R.id.carCreateTransmissionSP);
        carCreatePowerTypeSP = findViewById(R.id.carCreatePowerTypeSP);
        carCreateYearET = findViewById(R.id.carCreateYearET);
        carCreateLicensePlateET = findViewById(R.id.carCreateLicensePlateET);
        carCreateAliasET = findViewById(R.id.carCreateAliasET);
        carCreateBtn = findViewById(R.id.carCreateBtn);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        LoggedUser loggedUser = getLoggedUser(pref);

        carCreateBtn.setOnClickListener(v -> {

            progress.setVisibility(View.VISIBLE);

            if (isFieldsInvalid()) {
                progress.setVisibility(View.GONE);
                return;
            }

            CarCreateRequest ccr = new CarCreateRequest();
            ccr.setBrand(carCreateBrandET.getText().toString());
            ccr.setModel(carCreateModelET.getText().toString());
            ccr.setColor(carCreateColorET.getText().toString());
            int transmission = carCreateTransmissionSP.getSelectedItemPosition();
            ccr.setTransmission(transmission);
            int powerType = carCreatePowerTypeSP.getSelectedItemPosition();
            ccr.setPowerType(powerType);
            ccr.setYear(Integer.parseInt(carCreateYearET.getText().toString()));
            ccr.setLicensePlate(carCreateLicensePlateET.getText().toString());
            ccr.setAlias(carCreateAliasET.getText().toString());
            ccr.setUserId(loggedUser.getUserId());

            Retrofit retrofit = Api.getRetrofit();
            CarsApi carsApi = retrofit.create(CarsApi.class);

            carsApi.createCar(ccr).enqueue(new Callback<Car>() {
                @Override
                public void onResponse(@NotNull Call<Car> call, @NotNull Response<Car> response) {
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

        String brand = carCreateBrandET.getText().toString();
        String model = carCreateModelET.getText().toString();
        String color = carCreateColorET.getText().toString();
        Integer year = 0;

        try {
            year = Integer.parseInt(carCreateYearET.getText().toString());
        } catch (NumberFormatException e) {
            carCreateYearET.setError(getString(R.string.car_operation_invalid_format));
            hasErrors = true;
        }

        String licensePlate = carCreateLicensePlateET.getText().toString();

        if (brand == null || brand.isEmpty()) {
            carCreateBrandET.setError(getString(R.string.car_operation_brand_empty));
            hasErrors = true;
        }

        if (model == null || model.isEmpty()) {
            carCreateModelET.setError(getString(R.string.car_operation_model_empty));
            hasErrors = true;
        }

        if (color == null || color.isEmpty()) {
            carCreateColorET.setError(getString(R.string.car_operation_color_empty));
            hasErrors = true;
        }

        if (year == null) {
            carCreateYearET.setError(getString(R.string.car_operation_year_empty));
            hasErrors = true;
        }

        if (licensePlate == null || licensePlate.isEmpty()) {
            carCreateLicensePlateET.setError(getString(R.string.car_operation_license_plate_empty));
            hasErrors = true;
        }

        return hasErrors;
    }
}