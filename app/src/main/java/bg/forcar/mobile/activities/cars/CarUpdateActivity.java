package bg.forcar.mobile.activities.cars;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import bg.forcar.mobile.R;
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

    private TextInputLayout carUpdateBodyTypeLabelTV;
    private AutoCompleteTextView carUpdateBodyTypeDD;

    private TextInputLayout carUpdateTransmissionLabelTV;
    private AutoCompleteTextView carUpdateTransmissionDD;

    private TextInputLayout carUpdatePowerTypeLabelTV;
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

    private ArrayAdapter<String> colorsAdapter;
    private ArrayAdapter<String> bodyTypesAdapter;
    private ArrayAdapter<String> transmissionsAdapter;
    private ArrayAdapter<String> powerTypesAdapter;

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

        carUpdateBodyTypeLabelTV = findViewById(R.id.carUpdateBodyTypeLabelTV);
        carUpdateBodyTypeDD = findViewById(R.id.carUpdateBodyTypeDD);

        carUpdateTransmissionLabelTV = findViewById(R.id.carUpdateTransmissionLabelTV);
        carUpdateTransmissionDD = findViewById(R.id.carUpdateTransmissionDD);

        carUpdatePowerTypeLabelTV = findViewById(R.id.carUpdatePowerTypeLabelTV);
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

        final String carId = getIntent().getStringExtra("carId");

        final String[] colors = getResources().getStringArray(R.array.car_colors);

        colorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);

        colorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        carUpdateColorDD.setAdapter(colorsAdapter);

        final String[] bodyTypes = getResources().getStringArray(R.array.car_body_types);
        bodyTypesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bodyTypes);
        bodyTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carUpdateBodyTypeDD.setAdapter(bodyTypesAdapter);

        final String[] transmissions = getResources().getStringArray(R.array.car_transmissions);
        transmissionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, transmissions);
        transmissionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carUpdateTransmissionDD.setAdapter(transmissionsAdapter);

        final String[] powerTypes = getResources().getStringArray(R.array.car_power_types);
        powerTypesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, powerTypes);
        powerTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carUpdatePowerTypeDD.setAdapter(powerTypesAdapter);

        carsApi.getCar(carId, loggedUser.getAuthorization()).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(@NotNull Call<Car> call, @NotNull Response<Car> response) {
                Car car = response.body();
                carUpdateBrandET.setText(car.getBrand());
                carUpdateModelET.setText(car.getModel());
                carUpdateColorDD.setText(car.getColor());

                final int currentBodyType = car.getBodyType().getBodyType();
                final int currentTransmission = car.getTransmission();
                final int currentPowerType = car.getPowerType();

                carUpdateBodyTypeDD.setText(bodyTypes[currentBodyType], false);
                carUpdateTransmissionDD.setText(transmissions[currentTransmission], false);
                carUpdatePowerTypeDD.setText(powerTypes[currentPowerType], false);

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
            final int bodyType = Arrays.asList(bodyTypes).indexOf(carUpdateBodyTypeDD.getText().toString());
//            int bodyType = carUpdateBodyTypeDD.getSelectedItemPosition();
            cur.setBodyType(bodyType);
            final int transmission = Arrays.asList(transmissions).indexOf(carUpdateTransmissionDD.getText().toString());
//            int transmission = carUpdateTransmissionDD.getSelectedItemPosition();
            cur.setTransmission(transmission);
//            int powerType = carUpdatePowerTypeDD.getSelectedItemPosition();
            final int powerType = Arrays.asList(powerTypes).indexOf(carUpdatePowerTypeDD.getText().toString());
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

        final String brand = carUpdateBrandET.getText().toString();
        final String model = carUpdateModelET.getText().toString();
        final String color = carUpdateColorDD.getText().toString();
        Integer year = null;

        try {
            year = Integer.parseInt(carUpdateYearET.getText().toString());
            carUpdateYearLabelTV.setError(null);
        } catch (NumberFormatException e) {
            carUpdateYearLabelTV.setError(getString(R.string.car_operation_invalid_format));
            hasErrors = true;
        }

        final String licensePlate = carUpdateLicensePlateET.getText().toString();

        if (brand.isEmpty()) {
            carUpdateBrandLabelTV.setError(getString(R.string.car_operation_brand_empty));
            hasErrors = true;
        } else {
            carUpdateBrandLabelTV.setError(null);
        }

        if (model.isEmpty()) {
            carUpdateModelLabelTV.setError(getString(R.string.car_operation_model_empty));
            hasErrors = true;
        } else {
            carUpdateModelLabelTV.setError(null);
        }

        if (color.isEmpty()) {
            carUpdateColorLabelTV.setError(getString(R.string.car_operation_color_empty));
            hasErrors = true;
        } else {
            carUpdateColorLabelTV.setError(null);
        }

        if (year == null) {
            carUpdateYearLabelTV.setError(getString(R.string.car_operation_year_empty));
            hasErrors = true;
        } else {
            carUpdateYearLabelTV.setError(null);
        }

        if (licensePlate.isEmpty()) {
            carUpdateLicensePlateLabelTV.setError(getString(R.string.car_operation_license_plate_empty));
            hasErrors = true;
        } else {
            carUpdateLicensePlateLabelTV.setError(null);
        }

        return hasErrors;
    }
}