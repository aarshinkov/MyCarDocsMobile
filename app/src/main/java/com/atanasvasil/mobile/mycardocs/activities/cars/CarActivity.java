package com.atanasvasil.mobile.mycardocs.activities.cars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.MainActivity;
import com.atanasvasil.mobile.mycardocs.api.Api;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.atanasvasil.mobile.mycardocs.utils.Utils.getStringResource;

public class CarActivity extends AppCompatActivity {

    private TextView carInfoTV;
    private TextView carYearTV;
    private TextView carLicensePlateTV;
    private TextView carTransmissionTV;
    private ImageView carPowerTypeIV;
    private TextView carPowerTypeTV;
    private TextView carAliasLabelTV;
    private TextView carAliasTV;
    private TextView carColorTV;
    private TextView carAddedOnTV;
    private TextView carEditedOnLabelTV;
    private TextView carEditedOnTV;

    private SwipeRefreshLayout carRefresh;

    private CircularProgressIndicator carProgress;

    private String carId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        getSupportActionBar().setTitle("View a car");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        carId = intent.getStringExtra("carId");

        carInfoTV = findViewById(R.id.carInfoTV);
        carYearTV = findViewById(R.id.carYearTV);
        carLicensePlateTV = findViewById(R.id.carLicensePlateTV);
        carTransmissionTV = findViewById(R.id.carTransmissionTV);
        carPowerTypeIV = findViewById(R.id.carPowerTypeIV);
        carPowerTypeTV = findViewById(R.id.carPowerTypeTV);
        carAliasLabelTV = findViewById(R.id.carAliasLabelTV);
        carAliasTV = findViewById(R.id.carAliasTV);
        carColorTV = findViewById(R.id.carColorTV);
        carAddedOnTV = findViewById(R.id.carAddedOnTV);
        carEditedOnLabelTV = findViewById(R.id.carEditedOnLabelTV);
        carEditedOnTV = findViewById(R.id.carEditedOnTV);

        carRefresh = findViewById(R.id.carRefresh);

        carProgress = findViewById(R.id.carProgress);
        carProgress.setVisibility(View.VISIBLE);

        getCar(carId);

        carRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCar(carId);
                carRefresh.setRefreshing(false);
            }
        });

    }

    @Override
    protected void onResume() {
        getCar(carId);
        carProgress.setVisibility(View.GONE);
        super.onResume();
    }

    private void getCar(String carId) {

        carProgress.setVisibility(View.VISIBLE);

        Retrofit retrofit = Api.getRetrofit();

        CarsApi carsApi = retrofit.create(CarsApi.class);

        carsApi.getCar(carId).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Error getting car", Toast.LENGTH_LONG).show();
                    carProgress.setVisibility(View.GONE);
                    return;
                }

                if (response.code() == 200) {
                    Car car = response.body();

                    if (car != null) {

                        carInfoTV.setText(getString(R.string.car_info, car.getBrand(), car.getModel()));
                        carYearTV.setText(getString(R.string.car_year, car.getYear()));

                        carLicensePlateTV.setTooltipText(getString(R.string.car_license_plate));
                        carLicensePlateTV.setText(car.getLicensePlate());
                        carTransmissionTV.setText(getStringResource(getApplicationContext(), "car_transmission_" + car.getTransmission()));

                        // ELECTRIC
                        if (car.getPowerType() == 3) {
                            carPowerTypeIV.setImageResource(R.drawable.ic_electric_station);
                        }
                        carPowerTypeTV.setText(getStringResource(getApplicationContext(), "car_power_type_" + car.getPowerType()));

                        if (car.getAlias() != null) {
                            if (!car.getAlias().isEmpty()) {
                                carAliasTV.setText(car.getAlias());
                                carAliasLabelTV.setVisibility(View.VISIBLE);
                                carAliasTV.setVisibility(View.VISIBLE);
                            }
                        }

                        carColorTV.setText(car.getColor());

                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        Date date = new Date();
                        date.setTime(car.getAddedOn().getTime());
                        carAddedOnTV.setText(sdf.format(date));

                        if (car.getEditedOn() != null) {
                            date = new Date();
                            date.setTime(car.getEditedOn().getTime());
                            carEditedOnTV.setText(sdf.format(date));
                            carEditedOnLabelTV.setVisibility(View.VISIBLE);
                            carEditedOnTV.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Car not found", Toast.LENGTH_LONG).show();
                    }
                }

                carProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
                carProgress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_operations, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_edit) {

            Intent intent = new Intent(getApplicationContext(), CarUpdateActivity.class);
            intent.putExtra("carId", getIntent().getStringExtra("carId"));
            startActivity(intent);
            return false;

        } else if (item.getItemId() == R.id.action_delete) {

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
            });

            AlertDialog dialog = builder.create();

            dialog.show();
            return false;
        }

        onBackPressed();
        return true;
    }
}