package bg.forcar.mobile.activities.cars;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import bg.forcar.mobile.R;
import bg.forcar.mobile.activities.MainActivity;
import bg.forcar.mobile.api.Api;
import bg.forcar.mobile.api.CarsApi;
import bg.forcar.mobile.responses.cars.Car;
import bg.forcar.mobile.utils.LoggedUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_NAME;
import static bg.forcar.mobile.utils.Utils.getLoggedUser;
import static bg.forcar.mobile.utils.Utils.getStringResource;

public class CarActivity extends AppCompatActivity {

    private ConstraintLayout carActivityLayout;

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

    private MaterialButton carEditBtn;
    private MaterialButton carDeleteBtn;

    private SwipeRefreshLayout carRefresh;

    private CircularProgressIndicator carProgress;

    private LoggedUser loggedUser;
    private SharedPreferences pref;

    private String carId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        getSupportActionBar().setTitle(R.string.car_view_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        carActivityLayout = findViewById(R.id.carActivityLayout);

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

        carEditBtn = findViewById(R.id.carEditBtn);
        carDeleteBtn = findViewById(R.id.carDeleteBtn);

        carRefresh = findViewById(R.id.carRefresh);

        carProgress = findViewById(R.id.carProgress);
        carProgress.setVisibility(View.VISIBLE);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        getCar(carId);

        carRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCar(carId);
                carRefresh.setRefreshing(false);
            }
        });

        carEditBtn.setOnClickListener(v -> {
            editCar();
        });

        carDeleteBtn.setOnClickListener(v -> {
            deleteCar();
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

        carsApi.getCar(carId, loggedUser.getAuthorization()).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(@NotNull Call<Car> call, @NotNull Response<Car> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.car_get_error, Toast.LENGTH_LONG).show();
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
                        } else {
                            carPowerTypeIV.setImageResource(R.drawable.ic_gas_station);
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

                        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_1), Locale.getDefault());

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
                        Toast.makeText(getApplicationContext(), R.string.car_not_found, Toast.LENGTH_LONG).show();
                    }
                }

                carProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<Car> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
                carProgress.setVisibility(View.GONE);
            }
        });
    }

    private void editCar() {
        Intent intent = new Intent(getApplicationContext(), CarUpdateActivity.class);
        intent.putExtra("carId", getIntent().getStringExtra("carId"));
        startActivity(intent);
    }

    private void deleteCar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CarActivity.this);

        builder.setTitle(R.string.car_delete_title);

        builder.setMessage(R.string.car_delete_message);

        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.car_delete_process));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);

        builder.setPositiveButton(R.string.yes, (dialog, which) -> {

            progress.show();

            Retrofit retrofit = Api.getRetrofit();

            CarsApi carsApi = retrofit.create(CarsApi.class);

            String carId = getIntent().getStringExtra("carId");

            carsApi.deleteCar(carId, loggedUser.getAuthorization()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NotNull Call<Boolean> call, @NotNull Response<Boolean> response) {

                    if (response.code() == 404) {
                        Toast.makeText(getApplicationContext(), R.string.car_not_found, Toast.LENGTH_LONG).show();
                        progress.hide();
                        return;
                    }

                    Boolean result = response.body();

                    if (result != null) {
                        if (result) {
                            Toast.makeText(getApplicationContext(), R.string.car_delete_success, Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("fragment", "cars");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.car_delete_error, Toast.LENGTH_LONG).show();
                        }
                    }

                    progress.hide();
                }

                @Override
                public void onFailure(@NotNull Call<Boolean> call, @NotNull Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.car_delete_error, Toast.LENGTH_LONG).show();
                    progress.hide();
                }
            });
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        onBackPressed();
        return true;
    }
}