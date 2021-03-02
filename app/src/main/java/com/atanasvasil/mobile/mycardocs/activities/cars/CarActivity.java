package com.atanasvasil.mobile.mycardocs.activities.cars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.MainActivity;
import com.atanasvasil.mobile.mycardocs.api.Api;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CarActivity extends AppCompatActivity {

    private TextView carBrandTV;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        getSupportActionBar().setTitle("View a car");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String carId = intent.getStringExtra("carId");

        carBrandTV = findViewById(R.id.carBrandTV);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading car...");
        loadingDialog.show();

        Retrofit retrofit = Api.getRetrofit();

        CarsApi carsApi = retrofit.create(CarsApi.class);

        carsApi.getCar(carId).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Error getting car", Toast.LENGTH_LONG).show();
                    loadingDialog.hide();
                    return;
                }

                if (response.code() == 200) {
                    Car car = response.body();

                    if (car != null) {
                        carBrandTV.setText(car.getBrand());

                    } else {
                        Toast.makeText(getApplicationContext(), "Car not found", Toast.LENGTH_LONG).show();
                    }
                }

                loadingDialog.hide();
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
                loadingDialog.hide();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.car_operations, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.car_action_edit) {

            Intent intent = new Intent(getApplicationContext(), CarUpdateActivity.class);
            intent.putExtra("carId", getIntent().getStringExtra("carId"));
            startActivity(intent);

        } else if (item.getItemId() == R.id.car_action_delete) {

            AlertDialog.Builder builder = new AlertDialog.Builder(CarActivity.this);

            builder.setTitle("Delete car.");

            builder.setMessage("Are you sure you want to delete this car?");

            ProgressDialog loadingDialog = new ProgressDialog(this);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setMessage("Deleting car...");

            builder.setPositiveButton("Yes", (dialog, which) -> {

                loadingDialog.show();

                Retrofit retrofit = Api.getRetrofit();

                CarsApi carsApi = retrofit.create(CarsApi.class);

                String carId = getIntent().getStringExtra("carId");

                carsApi.deleteCar(carId).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                        if (response.code() == 404) {
                            Toast.makeText(getApplicationContext(), "Car not found", Toast.LENGTH_LONG).show();
                            loadingDialog.hide();
                            return;
                        }

                        Boolean result = response.body();

                        if (result != null) {
                            if (result) {
                                loadingDialog.hide();
                                Toast.makeText(getApplicationContext(), "Car deleted successfully!", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("fragment", "cars");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                return;
                            }
                        }

                        Toast.makeText(getApplicationContext(), "Error deleting the car!", Toast.LENGTH_LONG).show();
                        loadingDialog.hide();
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error deleting the car!", Toast.LENGTH_LONG).show();
                        loadingDialog.hide();
                    }
                });
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
            });

            AlertDialog dialog = builder.create();

            dialog.show();
            return false;
        }

        onBackPressed();
        return true;
    }
}