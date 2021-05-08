package com.atanasvasil.mobile.mycardocs.activities.service;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.fuel.FuelExpenseCreateActivity;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.api.ExpensesApi;
import com.atanasvasil.mobile.mycardocs.requests.expenses.service.ServiceExpenseCreateRequest;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.responses.expenses.service.ServiceExpense;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

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

    private Spinner secTypeSP;
    private Spinner secCarsSP;
    private EditText secPriceET;
    private EditText secNotesET;
    private MaterialButton secSaveBtn;

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

        secTypeSP = findViewById(R.id.secTypeSP);
        secCarsSP = findViewById(R.id.secCarsSP);
        secPriceET = findViewById(R.id.secPriceET);
        secNotesET = findViewById(R.id.secNotesET);
        secSaveBtn = findViewById(R.id.secSaveBtn);

        final String zeroFormatted = String.format(Locale.getDefault(), "%.2f", 0.00);

        loadCars();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cars);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ProgressDialog progress = new ProgressDialog(ServiceExpenseCreateActivity.this);
        progress.setMessage(getString(R.string.service_expense_create_progress));
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);

        secSaveBtn.setOnClickListener(v -> {
            progress.show();

            if (hasErrors()) {
                progress.hide();
                return;
            }

            Integer type = secTypeSP.getSelectedItemPosition() + 1;

            final String licensePlate = secCarsSP.getSelectedItem().toString();
            final String carId = userCarsMap.get(licensePlate);

            ServiceExpenseCreateRequest secr = new ServiceExpenseCreateRequest();
            secr.setType(type);
            secr.setCarId(carId);
            secr.setPrice(Double.parseDouble(secPriceET.getText().toString()));
            secr.setNotes(secNotesET.getText().toString());

            Retrofit retrofit = getRetrofit();
            ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

            expensesApi.createServiceExpense(secr, loggedUser.getUserId(), loggedUser.getAuthorization()).enqueue(new Callback<ServiceExpense>() {
                @Override
                public void onResponse(@NotNull Call<ServiceExpense> call, @NotNull Response<ServiceExpense> response) {

                    if (!response.isSuccessful()) {
                        Snackbar.make(v, R.string.service_expense_create_error, Snackbar.LENGTH_LONG).show();
                        progress.hide();
                        return;
                    }

                    Toast.makeText(getApplicationContext(), R.string.service_expense_create_successful, Toast.LENGTH_LONG).show();
                    progress.hide();

                    finish();
                }

                @Override
                public void onFailure(@NotNull Call<ServiceExpense> call, @NotNull Throwable t) {
                    Log.e("SERVICE_EXPENSE_CREATE", t.getMessage());
                    Snackbar.make(v, R.string.service_expense_create_error, Snackbar.LENGTH_LONG).show();
                    progress.hide();
                }
            });
        });
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
                secCarsSP.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NotNull Call<List<Car>> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean hasErrors() {

        boolean hasErrors = false;

        if (secPriceET.getText().toString().isEmpty()) {
            secPriceET.setError(getString(R.string.service_expense_create_price_empty));
            hasErrors = true;
        }

        return hasErrors;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        onBackPressed();
        return true;
    }
}
