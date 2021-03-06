package bg.forcar.mobile.activities.fuel;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import bg.forcar.mobile.R;
import bg.forcar.mobile.api.CarsApi;
import bg.forcar.mobile.api.ExpensesApi;
import bg.forcar.mobile.requests.expenses.fuel.FuelExpenseCreateRequest;
import bg.forcar.mobile.responses.cars.Car;
import bg.forcar.mobile.responses.expenses.fuel.FuelExpense;
import bg.forcar.mobile.utils.LoggedUser;

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

import bg.forcar.mobile.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static bg.forcar.mobile.api.Api.getRetrofit;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_NAME;
import static bg.forcar.mobile.utils.Utils.getLoggedUser;

public class FuelExpenseCreateActivity extends AppCompatActivity {

    private TextInputLayout fecPricePerLitreLabelTV;
    private TextInputEditText fecPricePerLitreET;
    private TextInputLayout fecLitresLabelTV;
    private TextInputEditText fecLitresET;
    private TextInputLayout fecDiscountLabelTV;
    private TextInputEditText fecDiscountET;
    private TextInputLayout fecCarsLabelTV;
    private AutoCompleteTextView fecCarsDD;
    private TextInputLayout fecTotalLabelTV;
    private TextInputEditText fecTotalET;
    private TextInputLayout fecMileageLabelTV;
    private TextInputEditText fecMileageET;
    private TextInputLayout fecCreatedOnLabelTV;
    private TextInputEditText fecCreatedOnET;

    private TextView fecPricePerLitreSummaryTV;
    private TextView fecLitresSummaryTV;
    private TextView fecSubtotalSummaryTV;
    private TextView fecDiscountSummaryTV;
    private TextView fecTotalSummaryTV;

    private MaterialButton fecSaveBtn;

    private SharedPreferences pref;
    private LoggedUser loggedUser;

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
        setContentView(R.layout.activity_fuel_expense_create);

        getSupportActionBar().setTitle(R.string.fuel_expense_create_title);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        final String zeroFormatted = String.format(Locale.getDefault(), "%.2f", 0.00);

        fecPricePerLitreLabelTV = findViewById(R.id.fecPricePerLitreLabelTV);
        fecPricePerLitreET = findViewById(R.id.fecPricePerLitreET);
        fecLitresLabelTV = findViewById(R.id.fecLitresLabelTV);
        fecLitresET = findViewById(R.id.fecLitresET);
        fecDiscountLabelTV = findViewById(R.id.fecDiscountLabelTV);
        fecDiscountET = findViewById(R.id.fecDiscountET);
        fecCarsLabelTV = findViewById(R.id.fecCarsLabelTV);
        fecCarsDD = findViewById(R.id.fecCarsDD);
        fecTotalLabelTV = findViewById(R.id.fecTotalLabelTV);
        fecTotalET = findViewById(R.id.fecTotalET);
        fecMileageLabelTV = findViewById(R.id.fecMileageLabelTV);
        fecMileageET = findViewById(R.id.fecMileageET);
        fecCreatedOnLabelTV = findViewById(R.id.fecCreatedOnLabelTV);
        fecCreatedOnET = findViewById(R.id.fecCreatedOnET);

        fecPricePerLitreSummaryTV = findViewById(R.id.fecPricePerLitreSummaryTV);
        fecLitresSummaryTV = findViewById(R.id.fecLitresSummaryTV);
        fecSubtotalSummaryTV = findViewById(R.id.fecSubtotalSummaryTV);
        fecDiscountSummaryTV = findViewById(R.id.fecDiscountSummaryTV);
        fecTotalSummaryTV = findViewById(R.id.fecTotalSummaryTV);

        fecSaveBtn = findViewById(R.id.fecSaveBtn);

        fecPricePerLitreSummaryTV.setText(zeroFormatted);
        fecLitresSummaryTV.setText(zeroFormatted);
        fecSubtotalSummaryTV.setText(zeroFormatted);
        fecDiscountSummaryTV.setText(zeroFormatted);
        fecTotalSummaryTV.setText(zeroFormatted);

        defaultTextColor = fecDiscountSummaryTV.getTextColors();

        loadCars();

        carsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cars);
        carsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ProgressDialog progress = new ProgressDialog(FuelExpenseCreateActivity.this);
        progress.setMessage(getString(R.string.fuel_expense_create_progress));
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);

        fecCreatedOnET.setInputType(InputType.TYPE_NULL);

        fecCreatedOnET.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                Utils.showDateTimeDialog(fecCreatedOnET, fecCreatedOnLabelTV, FuelExpenseCreateActivity.this);
            }
        });

        fecSaveBtn.setOnClickListener(v -> {
            progress.show();

            if (hasErrors()) {
                progress.hide();
                return;
            }

            FuelExpenseCreateRequest fecr = new FuelExpenseCreateRequest();
            fecr.setPricePerLitre(pricePerLitre);
            fecr.setLitres(litres);
            fecr.setDiscount(discount);
            if (!fecMileageET.getText().toString().isEmpty()) {
                fecr.setMileage(Long.parseLong(fecMileageET.getText().toString()));
            }

            final String licensePlate = fecCarsDD.getText().toString().split(" - ")[0].trim();
            final String carId = userCarsMap.get(licensePlate);

            fecr.setCarId(carId);

            try {
                DateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_time_default), Locale.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_time_1), Locale.getDefault());

                if (fecCreatedOnET != null) {
                    Date createdOnDate = sdf.parse(fecCreatedOnET.getText().toString());
                    final String createdOnFDate = dateFormat.format(createdOnDate);
                    fecr.setCreatedOn(createdOnFDate);
                }

            } catch (Exception ignored) {

            }

            final Retrofit retrofit = getRetrofit();

            ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

            expensesApi.createFuelExpense(fecr, loggedUser.getUserId(), loggedUser.getAuthorization()).enqueue(new Callback<FuelExpense>() {
                @Override
                public void onResponse(@NotNull Call<FuelExpense> call, @NotNull Response<FuelExpense> response) {

                    if (!response.isSuccessful()) {
                        Snackbar.make(v, R.string.fuel_expense_create_error, Snackbar.LENGTH_LONG).show();
                        progress.hide();
                        return;
                    }

                    Toast.makeText(getApplicationContext(), R.string.fuel_expense_create_successful, Toast.LENGTH_LONG).show();
                    progress.hide();

                    finish();
                }

                @Override
                public void onFailure(@NotNull Call<FuelExpense> call, @NotNull Throwable t) {
                    Log.e("FUEL_EXPENSE_CREATE", t.getMessage());
                    Toast.makeText(getApplicationContext(), R.string.fuel_expense_create_error, Toast.LENGTH_LONG).show();
                    progress.hide();
                }
            });
        });

        fecPricePerLitreET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isLitresActive = fecLitresET.isEnabled() && litres != null;
                final boolean isTotalActive = fecTotalET.isEnabled() && total != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        pricePerLitre = Double.parseDouble(s.toString());
                        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", pricePerLitre);
                        fecPricePerLitreSummaryTV.setText(formattedPrice);

                        if (isLitresActive) {
                            calculateTotal();
                        }

                        if (isTotalActive) {
                            calculateLitres();
                        }
                    } else {
                        pricePerLitre = null;
                        fecPricePerLitreSummaryTV.setText(zeroFormatted);
                        fecLitresET.setEnabled(true);
                        fecTotalET.setEnabled(true);

                        if (isLitresActive) {
                            total = null;
                            isFromUser = false;
                            fecTotalET.setText("");
                            fecTotalSummaryTV.setText(zeroFormatted);
                            fecSubtotalSummaryTV.setText(zeroFormatted);
                            fecDiscountET.setEnabled(false);
                            isFromUser = true;
                        }

                        if (isTotalActive) {
                            litres = null;
                            isFromUser = false;
                            fecLitresET.setText("");
                            fecLitresSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fecLitresET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isPricePerLitreActive = fecPricePerLitreET.isEnabled() && pricePerLitre != null;
                final boolean isTotalActive = fecTotalET.isEnabled() && total != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        litres = Double.parseDouble(s.toString());
                        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", litres);
                        fecLitresSummaryTV.setText(formattedPrice);

                        if (isPricePerLitreActive) {
                            calculateTotal();
                        }

                        if (isTotalActive) {
                            calculatePricePerLitre();
                        }
                    } else {
                        litres = null;
                        fecLitresSummaryTV.setText(zeroFormatted);
                        fecPricePerLitreET.setEnabled(true);
                        fecTotalET.setEnabled(true);

                        if (isPricePerLitreActive) {
                            total = null;
                            isFromUser = false;
                            fecTotalET.setText("");
                            fecTotalSummaryTV.setText(zeroFormatted);
                            fecSubtotalSummaryTV.setText(zeroFormatted);
                            fecDiscountET.setEnabled(false);
                            isFromUser = true;
                        }

                        if (isTotalActive) {
                            pricePerLitre = null;
                            isFromUser = false;
                            fecPricePerLitreET.setText("");
                            fecPricePerLitreSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fecDiscountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    discount = Double.parseDouble(s.toString());
                    final String formattedPrice = String.format(Locale.getDefault(), "%.2f", discount);
                    fecDiscountSummaryTV.setText(formattedPrice);
                } else {
                    discount = null;
                    fecDiscountSummaryTV.setText(zeroFormatted);
                }
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fecTotalET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isPricePerLitreActive = fecPricePerLitreET.isEnabled() && pricePerLitre != null;
                final boolean isLitresActive = fecLitresET.isEnabled() && litres != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        total = Double.parseDouble(s.toString());
                        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", total);
                        fecTotalSummaryTV.setText(formattedPrice);
                        fecDiscountET.setEnabled(true);
                        fecSubtotalSummaryTV.setText(formattedPrice);

                        if (isPricePerLitreActive) {
                            calculateLitres();
                        }

                        if (isLitresActive) {
                            calculatePricePerLitre();
                        }
                    } else {
                        total = null;
                        fecTotalSummaryTV.setText(zeroFormatted);
                        fecDiscountET.setEnabled(false);
                        fecSubtotalSummaryTV.setText(zeroFormatted);
                        fecPricePerLitreET.setEnabled(true);
                        fecLitresET.setEnabled(true);

                        if (isPricePerLitreActive) {
                            litres = null;
                            isFromUser = false;
                            fecLitresET.setText("");
                            fecLitresSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }

                        if (isLitresActive) {
                            pricePerLitre = null;
                            isFromUser = false;
                            fecPricePerLitreET.setText("");
                            fecPricePerLitreSummaryTV.setText(zeroFormatted);
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
                    fecCarsDD.setAdapter(carsAdapter);

                    Car firstCar = storedCars.get(0);
                    fecCarsDD.setText(firstCar.getLicensePlate() + " - " + firstCar.getBrand() + " " + firstCar.getModel(), false);
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
        fecPricePerLitreET.setEnabled(false);
        fecPricePerLitreET.setText(pricePerLitre.toString());
        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", pricePerLitre);
        fecPricePerLitreSummaryTV.setText(formattedPrice);
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
        fecLitresET.setEnabled(false);
        fecLitresET.setText(litres.toString());
        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", litres);
        fecLitresSummaryTV.setText(formattedPrice);
        isFromUser = true;
    }

    @SuppressLint("SetTextI18n")
    private void calculateTotal() {

        if (pricePerLitre != null && litres != null) {
            total = pricePerLitre * litres;
        } else {
            total = null;
        }

        final boolean hasDiscount = discount != null;

        isFromUser = false;
        fecTotalET.setEnabled(false);

        if (total != null) {
            fecTotalET.setText(total.toString());
        } else {
            fecTotalET.setText("");
        }

        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", total);

        if (!hasDiscount) {
            fecTotalSummaryTV.setText(formattedPrice);
            fecDiscountSummaryTV.setTextColor(defaultTextColor);
        } else {
            if (discount > 0) {
                fecDiscountSummaryTV.setTextColor(getResources().getColor(R.color.success, null));
            } else {
                fecDiscountSummaryTV.setTextColor(defaultTextColor);
            }
            final String formattedTotal = String.format(Locale.getDefault(), "%.2f", total - discount);
            fecTotalSummaryTV.setText(formattedTotal);
        }

        fecDiscountET.setEnabled(true);
        fecSubtotalSummaryTV.setText(formattedPrice);
        isFromUser = true;
    }

    private boolean hasErrors() {

        boolean hasErrors = false;

        if (fecPricePerLitreET.getText().toString().isEmpty()) {
            fecPricePerLitreLabelTV.setError(getString(R.string.fuel_expense_create_ppl_empty));
            hasErrors = true;
        } else {
            fecPricePerLitreLabelTV.setError(null);
        }

        if (fecLitresET.getText().toString().isEmpty()) {
            fecLitresLabelTV.setError(getString(R.string.fuel_expense_create_litres_empty));
            hasErrors = true;
        } else {
            fecLitresLabelTV.setError(null);
        }

//        if (fecTotalET.getText().toString().isEmpty()) {
//            fecTotalLabelTV.setError(getString(R.string.login_email_empty));
//            hasErrors = true;
//        } else {
//            fecTotalLabelTV.setError(null);
//        }

        if (discount != null && total != null) {
            if (discount > total) {
                fecDiscountLabelTV.setError(getString(R.string.fuel_expense_create_discount_invalid));
                hasErrors = true;
            } else {
                fecDiscountLabelTV.setError(null);
            }
        }

//        if (fecCreatedOnET.getText().toString().isEmpty()) {
//            fecCreatedOnLabelTV.setError(getString(R.string.fuel_expense_create_date_empty));
//            hasErrors = true;
//        } else {
//            fecCreatedOnLabelTV.setError(null);
//        }

        return hasErrors;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        onBackPressed();
        return true;
    }
}
