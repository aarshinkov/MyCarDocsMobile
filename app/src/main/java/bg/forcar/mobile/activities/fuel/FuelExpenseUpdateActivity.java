package bg.forcar.mobile.activities.fuel;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import bg.forcar.mobile.R;
import bg.forcar.mobile.api.CarsApi;
import bg.forcar.mobile.api.ExpensesApi;
import bg.forcar.mobile.requests.expenses.fuel.FuelExpenseCreateRequest;
import bg.forcar.mobile.requests.expenses.fuel.FuelExpenseUpdateRequest;
import bg.forcar.mobile.responses.cars.Car;
import bg.forcar.mobile.responses.expenses.fuel.FuelExpense;
import bg.forcar.mobile.utils.LoggedUser;
import bg.forcar.mobile.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static bg.forcar.mobile.api.Api.getRetrofit;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_NAME;
import static bg.forcar.mobile.utils.Utils.getLoggedUser;

public class FuelExpenseUpdateActivity extends AppCompatActivity {

    private TextInputLayout feuPricePerLitreLabelTV;
    private TextInputEditText feuPricePerLitreET;
    private TextInputLayout feuLitresLabelTV;
    private TextInputEditText feuLitresET;
    private TextInputLayout feuDiscountLabelTV;
    private TextInputEditText feuDiscountET;
    private TextInputLayout feuCarsLabelTV;
    private AutoCompleteTextView feuCarsDD;
    private TextInputLayout feuTotalLabelTV;
    private TextInputEditText feuTotalET;
    private TextInputLayout feuMileageLabelTV;
    private TextInputEditText feuMileageET;
    private TextInputLayout feuCreatedOnLabelTV;
    private TextInputEditText feuCreatedOnET;

    private TextView feuPricePerLitreSummaryTV;
    private TextView feuLitresSummaryTV;
    private TextView feuSubtotalSummaryTV;
    private TextView feuDiscountSummaryTV;
    private TextView feuTotalSummaryTV;

    private MaterialButton feuSaveBtn;

    private SharedPreferences pref;
    private LoggedUser loggedUser;

    private String fuelExpenseId;

    private Double pricePerLitre = null;
    private Double litres = null;
    private Double discount = null;
    private Double total = null;

    private Boolean isFromUser = true;

    private ColorStateList defaultTextColor;

    private Map<String, String> userCarsMap = new HashMap<>();
    private List<String> cars = new ArrayList<>();

    private ArrayAdapter<String> carsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_expense_update);

        getSupportActionBar().setTitle(R.string.fuel_expense_update_title);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        Intent intent = getIntent();
        fuelExpenseId = intent.getStringExtra("fuelExpenseId");

        final String zeroFormatted = String.format(Locale.getDefault(), "%.2f", 0.00);

        feuPricePerLitreLabelTV = findViewById(R.id.feuPricePerLitreLabelTV);
        feuPricePerLitreET = findViewById(R.id.feuPricePerLitreET);
        feuLitresLabelTV = findViewById(R.id.feuLitresLabelTV);
        feuLitresET = findViewById(R.id.feuLitresET);
        feuDiscountLabelTV = findViewById(R.id.feuDiscountLabelTV);
        feuDiscountET = findViewById(R.id.feuDiscountET);
        feuCarsLabelTV = findViewById(R.id.feuCarsLabelTV);
        feuCarsDD = findViewById(R.id.feuCarsDD);
        feuTotalLabelTV = findViewById(R.id.feuTotalLabelTV);
        feuTotalET = findViewById(R.id.feuTotalET);
        feuMileageLabelTV = findViewById(R.id.feuMileageLabelTV);
        feuMileageET = findViewById(R.id.feuMileageET);
        feuCreatedOnLabelTV = findViewById(R.id.feuCreatedOnLabelTV);
        feuCreatedOnET = findViewById(R.id.feuCreatedOnET);

        feuPricePerLitreSummaryTV = findViewById(R.id.feuPricePerLitreSummaryTV);
        feuLitresSummaryTV = findViewById(R.id.feuLitresSummaryTV);
        feuSubtotalSummaryTV = findViewById(R.id.feuSubtotalSummaryTV);
        feuDiscountSummaryTV = findViewById(R.id.feuDiscountSummaryTV);
        feuTotalSummaryTV = findViewById(R.id.feuTotalSummaryTV);

        feuSaveBtn = findViewById(R.id.feuSaveBtn);

        feuPricePerLitreSummaryTV.setText(zeroFormatted);
        feuLitresSummaryTV.setText(zeroFormatted);
        feuSubtotalSummaryTV.setText(zeroFormatted);
        feuDiscountSummaryTV.setText(zeroFormatted);
        feuTotalSummaryTV.setText(zeroFormatted);

        defaultTextColor = feuDiscountSummaryTV.getTextColors();

        loadCars();

        carsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cars);
        carsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ProgressDialog progress = new ProgressDialog(FuelExpenseUpdateActivity.this);
        progress.setMessage(getString(R.string.fuel_expense_update_progress));
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);

        feuCreatedOnET.setInputType(InputType.TYPE_NULL);

        feuCreatedOnET.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                Utils.showDateTimeDialog(feuCreatedOnET, feuCreatedOnLabelTV, FuelExpenseUpdateActivity.this);
            }
        });

        feuSaveBtn.setOnClickListener(v -> {
            progress.show();

            if (hasErrors()) {
                progress.hide();
                return;
            }

            FuelExpenseUpdateRequest feur = new FuelExpenseUpdateRequest();
            feur.setPricePerLitre(pricePerLitre);
            feur.setLitres(litres);
            feur.setDiscount(discount);
            if (!feuMileageET.getText().toString().isEmpty()) {
                feur.setMileage(Long.parseLong(feuMileageET.getText().toString()));
            }

            final String licensePlate = feuCarsDD.getText().toString().split(" - ")[0].trim();
            final String carId = userCarsMap.get(licensePlate);

            feur.setCarId(carId);

            try {
                DateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_time_default), Locale.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_time_1), Locale.getDefault());

                if (feuCreatedOnET != null) {
                    Date createdOnDate = sdf.parse(feuCreatedOnET.getText().toString());
                    final String createdOnFDate = dateFormat.format(createdOnDate);
                    feur.setCreatedOn(createdOnFDate);
                }

            } catch (Exception ignored) {

            }

            feur.setUserId(loggedUser.getUserId());

            final Retrofit retrofit = getRetrofit();

            ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

            expensesApi.updateFuelExpense(fuelExpenseId, feur, loggedUser.getAuthorization()).enqueue(new Callback<FuelExpense>() {
                @Override
                public void onResponse(@NotNull Call<FuelExpense> call, @NotNull Response<FuelExpense> response) {

                    if (!response.isSuccessful()) {
                        Snackbar.make(v, R.string.fuel_expense_update_error, Snackbar.LENGTH_LONG).show();
                        progress.hide();
                        return;
                    }

                    Toast.makeText(getApplicationContext(), R.string.fuel_expense_update_success, Toast.LENGTH_LONG).show();
                    progress.hide();

                    finish();
                }

                @Override
                public void onFailure(@NotNull Call<FuelExpense> call, @NotNull Throwable t) {
                    Log.e("FUEL_EXPENSE_UPDATE", t.getMessage());
                    Snackbar.make(v, R.string.fuel_expense_update_error, Snackbar.LENGTH_LONG).show();
                    progress.hide();
                }
            });
        });

        feuPricePerLitreET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isLitresActive = feuLitresET.isEnabled() && litres != null;
                final boolean isTotalActive = feuTotalET.isEnabled() && total != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        pricePerLitre = Double.parseDouble(s.toString());
                        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", pricePerLitre);
                        feuPricePerLitreSummaryTV.setText(formattedPrice);

                        if (isLitresActive) {
                            calculateTotal();
                        }

                        if (isTotalActive) {
                            calculateLitres();
                        }
                    } else {
                        pricePerLitre = null;
                        feuPricePerLitreSummaryTV.setText(zeroFormatted);
                        feuLitresET.setEnabled(true);
                        feuTotalET.setEnabled(true);

                        if (isLitresActive) {
                            total = null;
                            isFromUser = false;
                            feuTotalET.setText("");
                            feuTotalSummaryTV.setText(zeroFormatted);
                            feuSubtotalSummaryTV.setText(zeroFormatted);
                            feuDiscountET.setEnabled(false);
                            isFromUser = true;
                        }

                        if (isTotalActive) {
                            litres = null;
                            isFromUser = false;
                            feuLitresET.setText("");
                            feuLitresSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        feuLitresET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isPricePerLitreActive = feuPricePerLitreET.isEnabled() && pricePerLitre != null;
                final boolean isTotalActive = feuTotalET.isEnabled() && total != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        litres = Double.parseDouble(s.toString());
                        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", litres);
                        feuLitresSummaryTV.setText(formattedPrice);

                        if (isPricePerLitreActive) {
                            calculateTotal();
                        }

                        if (isTotalActive) {
                            calculatePricePerLitre();
                        }
                    } else {
                        litres = null;
                        feuLitresSummaryTV.setText(zeroFormatted);
                        feuPricePerLitreET.setEnabled(true);
                        feuTotalET.setEnabled(true);

                        if (isPricePerLitreActive) {
                            total = null;
                            isFromUser = false;
                            feuTotalET.setText("");
                            feuTotalSummaryTV.setText(zeroFormatted);
                            feuSubtotalSummaryTV.setText(zeroFormatted);
                            feuDiscountET.setEnabled(false);
                            isFromUser = true;
                        }

                        if (isTotalActive) {
                            pricePerLitre = null;
                            isFromUser = false;
                            feuPricePerLitreET.setText("");
                            feuPricePerLitreSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        feuDiscountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    discount = Double.parseDouble(s.toString());
                    final String formattedPrice = String.format(Locale.getDefault(), "%.2f", discount);
                    feuDiscountSummaryTV.setText(formattedPrice);
                } else {
                    discount = null;
                    feuDiscountSummaryTV.setText(zeroFormatted);
                }
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        feuTotalET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isPricePerLitreActive = feuPricePerLitreET.isEnabled() && pricePerLitre != null;
                final boolean isLitresActive = feuLitresET.isEnabled() && litres != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        total = Double.parseDouble(s.toString());
                        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", total);
                        feuTotalSummaryTV.setText(formattedPrice);
                        feuDiscountET.setEnabled(true);
                        feuSubtotalSummaryTV.setText(formattedPrice);

                        if (isPricePerLitreActive) {
                            calculateLitres();
                        }

                        if (isLitresActive) {
                            calculatePricePerLitre();
                        }
                    } else {
                        total = null;
                        feuTotalSummaryTV.setText(zeroFormatted);
                        feuDiscountET.setEnabled(false);
                        feuSubtotalSummaryTV.setText(zeroFormatted);
                        feuPricePerLitreET.setEnabled(true);
                        feuLitresET.setEnabled(true);

                        if (isPricePerLitreActive) {
                            litres = null;
                            isFromUser = false;
                            feuLitresET.setText("");
                            feuLitresSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }

                        if (isLitresActive) {
                            pricePerLitre = null;
                            isFromUser = false;
                            feuPricePerLitreET.setText("");
                            feuPricePerLitreSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
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

                if (storedCars != null) {
                    for (Car car : storedCars) {
                        cars.add(car.getLicensePlate() + " - " + car.getBrand() + " " + car.getModel());
                        userCarsMap.put(car.getLicensePlate(), car.getCarId());
                    }

                    carsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    feuCarsDD.setAdapter(carsAdapter);

                    Car firstCar = storedCars.get(0);
                    feuCarsDD.setText(firstCar.getLicensePlate() + " - " + firstCar.getBrand() + " " + firstCar.getModel(), false);

                    ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);
                    expensesApi.getFuelExpense(fuelExpenseId, loggedUser.getAuthorization()).enqueue(new Callback<FuelExpense>() {
                        @Override
                        public void onResponse(@NotNull Call<FuelExpense> call, @NotNull Response<FuelExpense> response) {

                            if (!response.isSuccessful()) {
                                return;
                            }

                            FuelExpense fuelExpense = response.body();

                            feuPricePerLitreET.setText(Double.toString(fuelExpense.getPricePerLitre()));
                            feuLitresET.setText(Double.toString(fuelExpense.getLitres()));

                            if (fuelExpense.getDiscount() != null) {
                                feuDiscountET.setText(Double.toString(fuelExpense.getDiscount()));
                            }

                            if (fuelExpense.getMileage() != null) {
                                feuMileageET.setText(Long.toString(fuelExpense.getMileage()));
                            }

                            Date date = new Date();
                            final SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_time_1), Locale.getDefault());

                            date.setTime(fuelExpense.getCreatedOn().getTime());
                            feuCreatedOnET.setText(sdf.format(date));
                        }

                        @Override
                        public void onFailure(@NotNull Call<FuelExpense> call, @NotNull Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Car>> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void calculatePricePerLitre() {
        if (litres == 0.00) {
            pricePerLitre = 0.00;
        } else {
            pricePerLitre = total / litres;
        }
        isFromUser = false;
        feuPricePerLitreET.setEnabled(false);
        feuPricePerLitreET.setText(pricePerLitre.toString());
        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", pricePerLitre);
        feuPricePerLitreSummaryTV.setText(formattedPrice);
        isFromUser = true;
    }

    @SuppressLint("SetTextI18n")
    private void calculateLitres() {
        if (pricePerLitre == 0.00) {
            litres = 0.00;
        } else {
            litres = total / pricePerLitre;
        }
        isFromUser = false;
        feuLitresET.setEnabled(false);
        feuLitresET.setText(litres.toString());
        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", litres);
        feuLitresSummaryTV.setText(formattedPrice);
        isFromUser = true;
    }

    @SuppressLint("SetTextI18n")
    private void calculateTotal() {
        total = pricePerLitre * litres;

        final boolean hasDiscount = discount != null;

        isFromUser = false;
        feuTotalET.setEnabled(false);
        feuTotalET.setText(total.toString());
        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", total);

        if (!hasDiscount) {
            feuTotalSummaryTV.setText(formattedPrice);
            feuDiscountSummaryTV.setTextColor(defaultTextColor);
        } else {
            if (discount > 0) {
                feuDiscountSummaryTV.setTextColor(getResources().getColor(R.color.success, null));
            } else {
                feuDiscountSummaryTV.setTextColor(defaultTextColor);
            }
            final String formattedTotal = String.format(Locale.getDefault(), "%.2f", total - discount);
            feuTotalSummaryTV.setText(formattedTotal);
        }

        feuDiscountET.setEnabled(true);
        feuSubtotalSummaryTV.setText(formattedPrice);
        isFromUser = true;
    }

    private boolean hasErrors() {

        boolean hasErrors = false;

        if (feuPricePerLitreET.getText().toString().isEmpty()) {
            feuPricePerLitreLabelTV.setError(getString(R.string.fuel_expense_update_ppl_empty));
            hasErrors = true;
        } else {
            feuPricePerLitreLabelTV.setError(null);
        }

        if (feuLitresET.getText().toString().isEmpty()) {
            feuLitresLabelTV.setError(getString(R.string.fuel_expense_update_litres_empty));
            hasErrors = true;
        } else {
            feuLitresLabelTV.setError(null);
        }

//        if (feuTotalET.getText().toString().isEmpty()) {
//            feuTotalLabelTV.setError(getString(R.string.login_email_empty));
//            hasErrors = true;
//        } else {
//            feuTotalLabelTV.setError(null);
//        }

        if (discount != null && total != null) {
            if (discount > total) {
                feuDiscountLabelTV.setError(getString(R.string.fuel_expense_update_discount_invalid));
                hasErrors = true;
            } else {
                feuDiscountLabelTV.setError(null);
            }
        }

//        if (feuCreatedOnET.getText().toString().isEmpty()) {
//            feuCreatedOnLabelTV.setError(getString(R.string.fuel_expense_update_date_empty));
//            hasErrors = true;
//        } else {
//            feuCreatedOnLabelTV.setError(null);
//        }

        return hasErrors;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        onBackPressed();
        return true;
    }
}
