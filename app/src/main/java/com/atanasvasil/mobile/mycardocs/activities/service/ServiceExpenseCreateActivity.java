package com.atanasvasil.mobile.mycardocs.activities.service;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.atanasvasil.mobile.mycardocs.api.Api.getRetrofit;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getLoggedUser;

public class ServiceExpenseCreateActivity extends AppCompatActivity {
    private Spinner seCarsSP;
    private SharedPreferences pref;
    private LoggedUser loggedUser;

    private Map<String, String> userCarsMap = new HashMap<>();
    private List<String> cars = new ArrayList<>();

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_expense_create);

        getSupportActionBar().setTitle(R.string.service_expense_title);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        seCarsSP = findViewById(R.id.seCarsSP);

        final String zeroFormatted = String.format(Locale.getDefault(), "%.2f", 0.00);

        loadCars();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cars);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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

                for (Car car : storedCars) {
                    userCarsMap.put(car.getLicensePlate(), car.getCarId());
                    cars.add(car.getLicensePlate());
                }

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                seCarsSP.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NotNull Call<List<Car>> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
            }
        });
    }

}
