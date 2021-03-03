package com.atanasvasil.mobile.mycardocs.activities.cars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.atanasvasil.mobile.mycardocs.activities.MainActivity;
import com.atanasvasil.mobile.mycardocs.api.Api;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.requests.CarCreateRequest;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.responses.users.User;
import com.google.android.material.progressindicator.LinearProgressIndicator;

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

        getSupportActionBar().setTitle("Create a car");
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
        User user = getLoggedUser(pref);

        carCreateBtn.setOnClickListener(v -> {

            progress.setVisibility(View.VISIBLE);

            if (isFieldsValid()) {
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
            ccr.setUserId(user.getUserId());

            Retrofit retrofit = Api.getRetrofit();
            CarsApi carsApi = retrofit.create(CarsApi.class);

            carsApi.createCar(ccr).enqueue(new Callback<Car>() {
                @Override
                public void onResponse(Call<Car> call, Response<Car> response) {
                    Toast.makeText(getApplicationContext(), "Car created successfully", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("fragment", "cars");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<Car> call, Throwable t) {
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

    private boolean isFieldsValid() {

        boolean hasErrors = false;

        String brand = carCreateBrandET.getText().toString();
        String model = carCreateModelET.getText().toString();
        String color = carCreateColorET.getText().toString();
        Integer year = 0;

        try {
            year = Integer.parseInt(carCreateYearET.getText().toString());
        } catch (NumberFormatException e) {
            carCreateYearET.setError("Invalid format");
            hasErrors = true;
        }

        String licensePlate = carCreateLicensePlateET.getText().toString();

        if (brand == null || brand.isEmpty()) {
            carCreateBrandET.setError("Brand must not be empty");
            hasErrors = true;
        }

        if (model == null || model.isEmpty()) {
            carCreateModelET.setError("Model must not be empty");
            hasErrors = true;
        }

        if (color == null || color.isEmpty()) {
            carCreateColorET.setError("Color must not be empty");
            hasErrors = true;
        }

        if (year == null) {
            carCreateYearET.setError("Year must not be empty");
            hasErrors = true;
        }

        if (licensePlate == null || licensePlate.isEmpty()) {
            carCreateLicensePlateET.setError("License plate must not be empty");
            hasErrors = true;
        }

        return hasErrors;
    }
}