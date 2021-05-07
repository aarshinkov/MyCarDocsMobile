package com.atanasvasil.mobile.mycardocs.activities.fuel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.api.ExpensesApi;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.responses.expenses.fuel.FuelExpense;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.atanasvasil.mobile.mycardocs.api.Api.getRetrofit;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getLoggedUser;

public class FuelExpenseActivity extends AppCompatActivity {

    private TextView fuelExpenseSubtotalTV;
    private TextView fuelExpenseDiscountTV;
    private TextView fuelExpenseTotalTV;

    private TextView fuelExpenseCreatedOnTV;

    private TextView fuelExpensePricePerLitreTV;
    private TextView fuelExpenseLitresTV;
    private TextView fuelExpenseCarBrandModelTV;
    private TextView fuelExpenseCarLicensePlateTV;
    private TextView fuelExpenseCarMileageTV;

    private ColorStateList defaultTextColor;

    private SharedPreferences pref;
    private LoggedUser loggedUser;

    private String fuelExpenseId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_expense);

        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_time_2), Locale.getDefault());

        getSupportActionBar().setTitle(R.string.fuel_expense_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fuelExpenseSubtotalTV = findViewById(R.id.fuelExpenseSubtotalTV);
        fuelExpenseDiscountTV = findViewById(R.id.fuelExpenseDiscountTV);
        fuelExpenseTotalTV = findViewById(R.id.fuelExpenseTotalTV);
        fuelExpenseCreatedOnTV = findViewById(R.id.fuelExpenseCreatedOnTV);

        fuelExpensePricePerLitreTV = findViewById(R.id.fuelExpensePricePerLitreTV);
        fuelExpenseLitresTV = findViewById(R.id.fuelExpenseLitresTV);
        fuelExpenseCarBrandModelTV = findViewById(R.id.fuelExpenseCarBrandModelTV);
        fuelExpenseCarLicensePlateTV = findViewById(R.id.fuelExpenseCarLicensePlateTV);
        fuelExpenseCarMileageTV = findViewById(R.id.fuelExpenseCarMileageTV);

        defaultTextColor = fuelExpenseDiscountTV.getTextColors();

        Intent intent = getIntent();
        fuelExpenseId = intent.getStringExtra("fuelExpenseId");

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        Retrofit retrofit = getRetrofit();
        ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

        expensesApi.getFuelExpense(fuelExpenseId, loggedUser.getAuthorization()).enqueue(new Callback<FuelExpense>() {
            @Override
            public void onResponse(@NotNull Call<FuelExpense> call, @NotNull Response<FuelExpense> response) {
                if (!response.isSuccessful()) {
                    finish();
                    return;
                }

                FuelExpense fuelExpense = response.body();

                final Double subtotal = fuelExpense.getPricePerLitre() * fuelExpense.getLitres();
                final Double discount = fuelExpense.getDiscount() != null ? fuelExpense.getDiscount() : 0.00;
                final Double total = subtotal - discount;

                final String subTotalFormatted = String.format(Locale.getDefault(), "%.2f", subtotal);
                final String discountFormatted = String.format(Locale.getDefault(), "%.2f", discount);
                final String totalFormatted = String.format(Locale.getDefault(), "%.2f", total);

                if (discount != 0.00) {
                    fuelExpenseDiscountTV.setTextColor(getResources().getColor(R.color.success, null));
                    fuelExpenseDiscountTV.setText("-" + discountFormatted);
                } else {
                    fuelExpenseDiscountTV.setText(discountFormatted);
                    fuelExpenseDiscountTV.setTextColor(defaultTextColor);
                }

                fuelExpenseSubtotalTV.setText(subTotalFormatted);
                fuelExpenseTotalTV.setText(getString(R.string.negative_number, getString(R.string.price_bgn, totalFormatted)));

                Date date = new Date();
                date.setTime(fuelExpense.getCreatedOn().getTime());
                fuelExpenseCreatedOnTV.setText(sdf.format(date));

                final String pricePerLitre = String.format(Locale.getDefault(), "%.2f", fuelExpense.getPricePerLitre());
                fuelExpensePricePerLitreTV.setText(getString(R.string.fuel_expense_price_per_litre_data, pricePerLitre));

                final String litres = String.format(Locale.getDefault(), "%.2f", fuelExpense.getLitres());
                fuelExpenseLitresTV.setText(getString(R.string.fuel_expense_litres_data, litres));

                final Car car = fuelExpense.getCar();

                fuelExpenseCarBrandModelTV.setText(car.getBrand() + " " + car.getModel());
                fuelExpenseCarLicensePlateTV.setText(car.getLicensePlate());

                if (fuelExpense.getMileage() != null) {
                    fuelExpenseCarMileageTV.setTypeface(null, Typeface.NORMAL);
                    fuelExpenseCarMileageTV.setText(getString(R.string.fuel_expense_car_mileage_data, fuelExpense.getMileage()));
                } else {
                    fuelExpenseCarMileageTV.setTypeface(null, Typeface.ITALIC);
                    fuelExpenseCarMileageTV.setText(R.string.fuel_expense_car_mileage_unknown);
                }
            }

            @Override
            public void onFailure(@NotNull Call<FuelExpense> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }
}
