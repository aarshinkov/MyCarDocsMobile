package com.atanasvasil.mobile.mycardocs.activities.fuel;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.api.ExpensesApi;
import com.atanasvasil.mobile.mycardocs.requests.expenses.fuel.FuelExpenseCreateRequest;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.responses.expenses.fuel.FuelExpense;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;
import com.google.android.material.button.MaterialButton;

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

public class FuelExpenseActivity extends AppCompatActivity {

    private EditText fuelExpensePricePerLitreET;
    private EditText fuelExpenseLitresET;
    private EditText fuelExpenseDiscountET;
    private Spinner fuelExpenseCarsSP;
    private EditText fuelExpenseTotalET;
    private EditText fuelExpenseMileageET;

    private TextView fuelExpensePricePerLitreSummaryTV;
    private TextView fuelExpenseLitresSummaryTV;
    private TextView fuelExpenseSubtotalSummaryTV;
    private TextView fuelExpenseDiscountSummaryTV;
    private TextView fuelExpenseTotalSummaryTV;

    private MaterialButton fuelExpenseSaveBtn;

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

    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_expense);

        getSupportActionBar().setTitle(R.string.fuel_expense_title);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        final String zeroFormatted = String.format(Locale.getDefault(), "%.2f", 0.00);

        fuelExpensePricePerLitreET = findViewById(R.id.fuelExpensePricePerLitreET);
        fuelExpenseLitresET = findViewById(R.id.fuelExpenseLitresET);
        fuelExpenseDiscountET = findViewById(R.id.fuelExpenseDiscountET);
        fuelExpenseCarsSP = findViewById(R.id.fuelExpenseCarsSP);
        fuelExpenseTotalET = findViewById(R.id.fuelExpenseTotalET);
        fuelExpenseMileageET = findViewById(R.id.fuelExpenseMileageET);

        fuelExpensePricePerLitreSummaryTV = findViewById(R.id.fuelExpensePricePerLitreSummaryTV);
        fuelExpenseLitresSummaryTV = findViewById(R.id.fuelExpenseLitresSummaryTV);
        fuelExpenseSubtotalSummaryTV = findViewById(R.id.fuelExpenseSubtotalSummaryTV);
        fuelExpenseDiscountSummaryTV = findViewById(R.id.fuelExpenseDiscountSummaryTV);
        fuelExpenseTotalSummaryTV = findViewById(R.id.fuelExpenseTotalSummaryTV);

        fuelExpenseSaveBtn = findViewById(R.id.fuelExpenseSaveBtn);

        fuelExpensePricePerLitreSummaryTV.setText(zeroFormatted);
        fuelExpenseLitresSummaryTV.setText(zeroFormatted);
        fuelExpenseSubtotalSummaryTV.setText(zeroFormatted);
        fuelExpenseDiscountSummaryTV.setText(zeroFormatted);
        fuelExpenseTotalSummaryTV.setText(zeroFormatted);

        defaultTextColor = fuelExpenseDiscountSummaryTV.getTextColors();

        loadCars();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cars);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fuelExpenseSaveBtn.setOnClickListener(v -> {
            ProgressDialog progress = new ProgressDialog(FuelExpenseActivity.this);
            progress.setMessage(getString(R.string.fuel_expense_create_progress));
            progress.setCanceledOnTouchOutside(false);
            progress.setCancelable(false);
            progress.show();

            FuelExpenseCreateRequest fecr = new FuelExpenseCreateRequest();
            fecr.setPricePerLitre(pricePerLitre);
            fecr.setLitres(litres);
            fecr.setDiscount(discount);
            if (!fuelExpenseMileageET.getText().toString().isEmpty()) {
                fecr.setMileage(Long.parseLong(fuelExpenseMileageET.getText().toString()));
            }

            final String licensePlate = fuelExpenseCarsSP.getSelectedItem().toString();
            final String carPId = userCarsMap.get(licensePlate);

            fecr.setCarId(carPId);
            fecr.setUserId(loggedUser.getUserId());

            Retrofit retrofit = getRetrofit();

            ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

            expensesApi.createFuelExpense(fecr, loggedUser.getAuthorization()).enqueue(new Callback<FuelExpense>() {
                @Override
                public void onResponse(@NotNull Call<FuelExpense> call, @NotNull Response<FuelExpense> response) {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), R.string.fuel_expense_create_error, Toast.LENGTH_LONG).show();
                        progress.hide();
                        return;
                    }

                    Toast.makeText(getApplicationContext(), R.string.fuel_expense_create_successful, Toast.LENGTH_LONG).show();
                    progress.hide();
                }

                @Override
                public void onFailure(@NotNull Call<FuelExpense> call, @NotNull Throwable t) {
                    Log.e("FUEL_EXPENSE_CREATE", t.getMessage());
                    Toast.makeText(getApplicationContext(), R.string.fuel_expense_create_error, Toast.LENGTH_LONG).show();
                    progress.hide();
                }
            });
        });

        fuelExpensePricePerLitreET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isLitresActive = fuelExpenseLitresET.isEnabled() && litres != null;
                final boolean isTotalActive = fuelExpenseTotalET.isEnabled() && total != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        pricePerLitre = Double.parseDouble(s.toString());
                        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", pricePerLitre);
                        fuelExpensePricePerLitreSummaryTV.setText(formattedPrice);

                        if (isLitresActive) {
                            calculateTotal();
                        }

                        if (isTotalActive) {
                            calculateLitres();
                        }
                    } else {
                        pricePerLitre = null;
                        fuelExpensePricePerLitreSummaryTV.setText(zeroFormatted);
                        fuelExpenseLitresET.setEnabled(true);
                        fuelExpenseTotalET.setEnabled(true);

                        if (isLitresActive) {
                            total = null;
                            isFromUser = false;
                            fuelExpenseTotalET.setText("");
                            fuelExpenseTotalSummaryTV.setText(zeroFormatted);
                            fuelExpenseSubtotalSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }

                        if (isTotalActive) {
                            litres = null;
                            isFromUser = false;
                            fuelExpenseLitresET.setText("");
                            fuelExpenseLitresSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fuelExpenseLitresET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isPricePerLitreActive = fuelExpensePricePerLitreET.isEnabled() && pricePerLitre != null;
                final boolean isTotalActive = fuelExpenseTotalET.isEnabled() && total != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        litres = Double.parseDouble(s.toString());
                        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", litres);
                        fuelExpenseLitresSummaryTV.setText(formattedPrice);

                        if (isPricePerLitreActive) {
                            calculateTotal();
                        }

                        if (isTotalActive) {
                            calculatePricePerLitre();
                        }
                    } else {
                        litres = null;
                        fuelExpenseLitresSummaryTV.setText(zeroFormatted);
                        fuelExpensePricePerLitreET.setEnabled(true);
                        fuelExpenseTotalET.setEnabled(true);

                        if (isPricePerLitreActive) {
                            total = null;
                            isFromUser = false;
                            fuelExpenseTotalET.setText("");
                            fuelExpenseTotalSummaryTV.setText(zeroFormatted);
                            fuelExpenseSubtotalSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }

                        if (isTotalActive) {
                            pricePerLitre = null;
                            isFromUser = false;
                            fuelExpensePricePerLitreET.setText("");
                            fuelExpensePricePerLitreSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fuelExpenseDiscountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    discount = Double.parseDouble(s.toString());
                    final String formattedPrice = String.format(Locale.getDefault(), "%.2f", discount);
                    fuelExpenseDiscountSummaryTV.setText(formattedPrice);
                } else {
                    discount = null;
                    fuelExpenseDiscountSummaryTV.setText(zeroFormatted);
                }
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fuelExpenseTotalET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isPricePerLitreActive = fuelExpensePricePerLitreET.isEnabled() && pricePerLitre != null;
                final boolean isLitresActive = fuelExpenseLitresET.isEnabled() && litres != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        total = Double.parseDouble(s.toString());
                        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", total);
//                        fuelExpenseTotalSummaryTV.setText(formattedPrice);
                        fuelExpenseSubtotalSummaryTV.setText(formattedPrice);

                        if (isPricePerLitreActive) {
                            calculateLitres();
                        }

                        if (isLitresActive) {
                            calculatePricePerLitre();
                        }
                    } else {
                        total = null;
                        fuelExpenseTotalSummaryTV.setText(zeroFormatted);
                        fuelExpenseSubtotalSummaryTV.setText(zeroFormatted);
                        fuelExpensePricePerLitreET.setEnabled(true);
                        fuelExpenseLitresET.setEnabled(true);

                        if (isPricePerLitreActive) {
                            litres = null;
                            isFromUser = false;
                            fuelExpenseLitresET.setText("");
                            fuelExpenseLitresSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }

                        if (isLitresActive) {
                            pricePerLitre = null;
                            isFromUser = false;
                            fuelExpensePricePerLitreET.setText("");
                            fuelExpensePricePerLitreSummaryTV.setText(zeroFormatted);
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

                for (Car car : storedCars) {
                    userCarsMap.put(car.getLicensePlate(), car.getCarId());
                    cars.add(car.getLicensePlate());
                }

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                fuelExpenseCarsSP.setAdapter(adapter);
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
        fuelExpensePricePerLitreET.setEnabled(false);
        fuelExpensePricePerLitreET.setText(pricePerLitre.toString());
        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", pricePerLitre);
        fuelExpensePricePerLitreSummaryTV.setText(formattedPrice);
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
        fuelExpenseLitresET.setEnabled(false);
        fuelExpenseLitresET.setText(litres.toString());
        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", litres);
        fuelExpenseLitresSummaryTV.setText(formattedPrice);
        isFromUser = true;
    }

    @SuppressLint("SetTextI18n")
    private void calculateTotal() {
        total = pricePerLitre * litres;

        final boolean hasDiscount = discount != null;

        isFromUser = false;
        fuelExpenseTotalET.setEnabled(false);
        fuelExpenseTotalET.setText(total.toString());
        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", total);

        if (!hasDiscount) {
            fuelExpenseTotalSummaryTV.setText(formattedPrice);
            fuelExpenseDiscountSummaryTV.setTextColor(defaultTextColor);
        } else {
            if (discount > 0) {
                fuelExpenseDiscountSummaryTV.setTextColor(getResources().getColor(R.color.success, null));
            } else {
                fuelExpenseDiscountSummaryTV.setTextColor(defaultTextColor);
            }
            final String formattedTotal = String.format(Locale.getDefault(), "%.2f", total - discount);
            fuelExpenseTotalSummaryTV.setText(formattedTotal);
        }

        fuelExpenseSubtotalSummaryTV.setText(formattedPrice);
        isFromUser = true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        onBackPressed();
        return true;
    }
}
