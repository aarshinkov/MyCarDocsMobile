package com.atanasvasil.mobile.mycardocs.activities.cars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.api.Api;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.requests.CarCreateRequest;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_USER_ID;

public class CarCreateActivity extends AppCompatActivity {

    private EditText carCreateBrandET;
    private EditText carCreateModelET;
    private EditText carCreateColorET;
    private EditText carCreateYearET;
    private EditText carCreateLicensePlateET;
    private EditText carCreateAliasET;
    private Button carCreateBtn;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_create);

        getSupportActionBar().setTitle("Create a car");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        carCreateBrandET = findViewById(R.id.carCreateBrandET);
        carCreateModelET = findViewById(R.id.carCreateModelET);
        carCreateColorET = findViewById(R.id.carCreateColorET);
        carCreateYearET = findViewById(R.id.carCreateYearET);
        carCreateLicensePlateET = findViewById(R.id.carCreateLicensePlateET);
        carCreateAliasET = findViewById(R.id.carCreateAliasET);
        carCreateBtn = findViewById(R.id.carCreateBtn);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        Long userId = pref.getLong(SHARED_PREF_USER_ID, 0);

        carCreateBtn.setOnClickListener(v -> {

            CarCreateRequest ccr = new CarCreateRequest();
            ccr.setBrand(carCreateBrandET.getText().toString());
            ccr.setModel(carCreateModelET.getText().toString());
            ccr.setColor(carCreateColorET.getText().toString());
            ccr.setYear(Integer.parseInt(carCreateYearET.getText().toString()));
            ccr.setLicensePlate(carCreateLicensePlateET.getText().toString());
            ccr.setAlias(carCreateAliasET.getText().toString());
            ccr.setUserId(userId);


            Retrofit retrofit = Api.getRetrofit();
            CarsApi carsApi = retrofit.create(CarsApi.class);
            carsApi.createCar(ccr).enqueue(new Callback<Car>() {
                @Override
                public void onResponse(Call<Car> call, Response<Car> response) {
                    Toast.makeText(getApplicationContext(), "Car created successfully",
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<Car> call, Throwable t) {

                }
            });

        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }
}