package com.atanasvasil.mobile.mycardocs.activities.cars;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.api.Api;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.requests.CarCreateRequest;
import com.atanasvasil.mobile.mycardocs.requests.CarUpdateRequest;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CarUpdateActivity extends AppCompatActivity {

    private EditText carUpdateBrandET;
    private EditText carUpdateModelET;
    private EditText carUpdateColorET;
    private EditText carUpdateYearET;
    private EditText carUpdateLicensePlateET;
    private EditText carUpdateAliasET;
    private Button carUpdateBtn;

    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_update);

        getSupportActionBar().setTitle("Edit a car");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setMessage("Updating a car...");

        carUpdateBrandET = findViewById(R.id.carUpdateBrandET);
        carUpdateModelET = findViewById(R.id.carUpdateModelET);
        carUpdateColorET = findViewById(R.id.carUpdateColorET);
        carUpdateYearET = findViewById(R.id.carUpdateYearET);
        carUpdateLicensePlateET = findViewById(R.id.carUpdateLicensePlateET);
        carUpdateAliasET = findViewById(R.id.carUpdateAliasET);
        carUpdateBtn = findViewById(R.id.carUpdateBtn);


        Retrofit retrofit = Api.getRetrofit();

        CarsApi carsApi = retrofit.create(CarsApi.class);

        String carId = getIntent().getStringExtra("carId");
        carsApi.getCar(carId).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                Car car = response.body();
                carUpdateBrandET.setText(car.getBrand());
                carUpdateModelET.setText(car.getModel());
                carUpdateColorET.setText(car.getColor());
                carUpdateYearET.setText(String.valueOf(car.getYear()));
                carUpdateLicensePlateET.setText(car.getLicensePlate());
                carUpdateAliasET.setText(car.getAlias());
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {

            }
        });

        carUpdateBtn.setOnClickListener(v -> {
            loadingDialog.show();
            CarUpdateRequest ccr = new CarUpdateRequest();
            ccr.setCarId(carId);
            ccr.setBrand(carUpdateBrandET.getText().toString());
            ccr.setModel(carUpdateModelET.getText().toString());
            ccr.setColor(carUpdateColorET.getText().toString());
            ccr.setYear(Integer.parseInt(carUpdateYearET.getText().toString()));
            ccr.setLicensePlate(carUpdateLicensePlateET.getText().toString());
            ccr.setAlias(carUpdateAliasET.getText().toString());

            carsApi.updateCar(ccr).enqueue(new Callback<Car>() {
                @Override
                public void onResponse(Call<Car> call, Response<Car> response) {
                    Toast.makeText(getApplicationContext(), "Car updated successfully", Toast.LENGTH_LONG).show();
                    loadingDialog.hide();
                }


                @Override
                public void onFailure(Call<Car> call, Throwable t) {

                }
            });


        });

    }
}