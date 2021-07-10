package bg.forcar.mobile.activities.cars;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aarshinkov.mobile.mycardocs.R;
import bg.forcar.mobile.api.Api;
import bg.forcar.mobile.api.CarsApi;
import bg.forcar.mobile.requests.cars.CarUpdateRequest;
import bg.forcar.mobile.responses.cars.Car;
import bg.forcar.mobile.utils.LoggedUser;
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

import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_NAME;
import static bg.forcar.mobile.utils.Utils.getLoggedUser;

public class CarUpdateActivity extends AppCompatActivity {

    private TextInputLayout carUpdateBrandLabelTV;
    private TextInputEditText carUpdateBrandET;

    private TextInputLayout carUpdateModelLabelTV;
    private TextInputEditText carUpdateModelET;

    private TextInputLayout carUpdateColorLabelTV;
    private AutoCompleteTextView carUpdateColorDD;

    private AutoCompleteTextView carUpdateTransmissionDD;
    private AutoCompleteTextView carUpdatePowerTypeDD;

    private TextInputLayout carUpdateYearLabelTV;
    private TextInputEditText carUpdateYearET;

    private TextInputLayout carUpdateLicensePlateLabelTV;
    private TextInputEditText carUpdateLicensePlateET;

    private TextInputLayout carUpdateAliasLabelTV;
    private TextInputEditText carUpdateAliasET;

    private MaterialButton carUpdateBtn;

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

        carUpdateBrandLabelTV = findViewById(R.id.carUpdateBrandLabelTV);
        carUpdateBrandET = findViewById(R.id.carUpdateBrandET);

        carUpdateModelLabelTV = findViewById(R.id.carUpdateModelLabelTV);
        carUpdateModelET = findViewById(R.id.carUpdateModelET);

        carUpdateColorLabelTV = findViewById(R.id.carUpdateColorLabelTV);
        carUpdateColorDD = findViewById(R.id.carUpdateColorDD);

        carUpdateTransmissionDD = findViewById(R.id.carUpdateTransmissionDD);
        carUpdatePowerTypeDD = findViewById(R.id.carUpdatePowerTypeDD);

        carUpdateYearLabelTV = findViewById(R.id.carUpdateYearLabelTV);
        carUpdateYearET = findViewById(R.id.carUpdateYearET);

        carUpdateLicensePlateLabelTV = findViewById(R.id.carUpdateLicensePlateLabelTV);
        carUpdateLicensePlateET = findViewById(R.id.carUpdateLicensePlateET);

        carUpdateAliasLabelTV = findViewById(R.id.carUpdateAliasLabelTV);
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
                carUpdateColorDD.setText(car.getColor());
                carUpdateTransmissionDD.setSelection(car.getTransmission());
                carUpdatePowerTypeDD.setSelection(car.getPowerType());
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
            cur.setColor(carUpdateColorDD.getText().toString());
            final int transmission = Arrays.asList(transmissions).indexOf(carCreateTransmissionDD.getText().toString());
            int transmission = carUpdateTransmissionDD.getSelectedItemPosition();
            cur.setTransmission(transmission);
            int powerType = carUpdatePowerTypeDD.getSelectedItemPosition();
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