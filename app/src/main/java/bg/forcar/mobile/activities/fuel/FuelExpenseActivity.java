package bg.forcar.mobile.activities.fuel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import bg.forcar.mobile.R;
import bg.forcar.mobile.activities.MainActivity;
import bg.forcar.mobile.activities.policies.PolicyActivity;
import bg.forcar.mobile.activities.policies.PolicyUpdateActivity;
import bg.forcar.mobile.api.ExpensesApi;
import bg.forcar.mobile.api.PoliciesApi;
import bg.forcar.mobile.responses.cars.Car;
import bg.forcar.mobile.responses.expenses.fuel.FuelExpense;
import bg.forcar.mobile.utils.LoggedUser;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static bg.forcar.mobile.api.Api.getRetrofit;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_NAME;
import static bg.forcar.mobile.utils.Utils.getDayOfWeek;
import static bg.forcar.mobile.utils.Utils.getLoggedUser;

public class FuelExpenseActivity extends AppCompatActivity {

    private TextView fuelExpenseSubtotalTV;
    private TextView fuelExpenseDiscountTV;
    private TextView fuelExpenseTotalTV;

    private TextView fuelExpenseCreatedOnTV;

    private TextView fuelExpensePricePerLitreDetailsTV;
    private TextView fuelExpenseLitresDetailsTV;
    private TextView fuelExpenseDiscountDetailsTV;
    private TextView fuelExpenseDiscountPricePerDetailsLitreTV;

    private TextView fuelExpenseCarBrandModelTV;
    private TextView fuelExpenseCarLicensePlateTV;
    private TextView fuelExpenseCarMileageTV;
    private MaterialButton fuelExpenseEditBtn;
    private MaterialButton fuelExpenseDeleteBtn;
    private MaterialButton fuelExpenseBackBtn;

    private CircularProgressIndicator fuelExpenseProgress;

    SimpleDateFormat sdf;

    private ColorStateList defaultTextColor;

    private SharedPreferences pref;
    private LoggedUser loggedUser;

    private String fuelExpenseId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_expense);

        sdf = new SimpleDateFormat(getString(R.string.date_time_2), Locale.getDefault());

        getSupportActionBar().setTitle(R.string.fuel_expense_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fuelExpenseSubtotalTV = findViewById(R.id.fuelExpenseSubtotalTV);
        fuelExpenseDiscountTV = findViewById(R.id.fuelExpenseDiscountTV);
        fuelExpenseTotalTV = findViewById(R.id.fuelExpenseTotalTV);
        fuelExpenseCreatedOnTV = findViewById(R.id.fuelExpenseCreatedOnTV);

        fuelExpensePricePerLitreDetailsTV = findViewById(R.id.fuelExpensePricePerLitreDetailsTV);
        fuelExpenseLitresDetailsTV = findViewById(R.id.fuelExpenseLitresDetailsTV);
        fuelExpenseDiscountDetailsTV = findViewById(R.id.fuelExpenseDiscountDetailsTV);
        fuelExpenseDiscountPricePerDetailsLitreTV = findViewById(R.id.fuelExpenseDiscountPricePerDetailsLitreTV);
        fuelExpenseCarBrandModelTV = findViewById(R.id.fuelExpenseCarBrandModelTV);
        fuelExpenseCarLicensePlateTV = findViewById(R.id.fuelExpenseCarLicensePlateTV);
        fuelExpenseCarMileageTV = findViewById(R.id.fuelExpenseCarMileageTV);
        fuelExpenseEditBtn = findViewById(R.id.fuelExpenseEditBtn);
        fuelExpenseDeleteBtn = findViewById(R.id.fuelExpenseDeleteBtn);
        fuelExpenseBackBtn = findViewById(R.id.fuelExpenseBackBtn);
        fuelExpenseProgress = findViewById(R.id.fuelExpenseProgress);

        defaultTextColor = fuelExpenseDiscountTV.getTextColors();

        Intent intent = getIntent();
        fuelExpenseId = intent.getStringExtra("fuelExpenseId");

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        fuelExpenseProgress.setVisibility(View.VISIBLE);

        getFuelExpense(fuelExpenseId);

        fuelExpenseEditBtn.setOnClickListener(v -> {
            editFuelExpense();
        });

        fuelExpenseDeleteBtn.setOnClickListener(v -> {
            deleteFuelExpense();
        });

        fuelExpenseBackBtn.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void getFuelExpense(String fuelExpenseId) {

        fuelExpenseProgress.setVisibility(View.VISIBLE);

        Retrofit retrofit = getRetrofit();
        ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

        expensesApi.getFuelExpense(fuelExpenseId, loggedUser.getAuthorization()).enqueue(new Callback<FuelExpense>() {
            @Override
            public void onResponse(@NotNull Call<FuelExpense> call, @NotNull Response<FuelExpense> response) {
                if (!response.isSuccessful()) {
                    finish();
                    fuelExpenseProgress.setVisibility(View.INVISIBLE);
                    return;
                }

                FuelExpense fuelExpense = response.body();

                if (fuelExpense == null) {
                    fuelExpenseProgress.setVisibility(View.INVISIBLE);
                    finish();
                    return;
                }

                final Double subtotal = fuelExpense.getPricePerLitre() * fuelExpense.getLitres();
                final Double discount = fuelExpense.getDiscount() != null ? fuelExpense.getDiscount() : 0.00;
                final Double total = subtotal - discount;

                final String subTotalFormatted = String.format(Locale.getDefault(), "%.2f", subtotal);
                final String discountFormatted = String.format(Locale.getDefault(), "%.2f", discount);
                final String totalFormatted = String.format(Locale.getDefault(), "%.2f", total);

                if (discount != 0.00) {
                    fuelExpenseDiscountTV.setTextColor(getResources().getColor(R.color.success, null));
                    fuelExpenseDiscountTV.setText(getString(R.string.negative_number, discountFormatted));
                } else {
                    fuelExpenseDiscountTV.setText(discountFormatted);
                    fuelExpenseDiscountTV.setTextColor(defaultTextColor);
                }

                fuelExpenseSubtotalTV.setText(subTotalFormatted);
                fuelExpenseTotalTV.setText(getString(R.string.negative_number, getString(R.string.price_bgn, totalFormatted)));

                Date date = new Date();
                date.setTime(fuelExpense.getCreatedOn().getTime());

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int dow = cal.get(Calendar.DAY_OF_WEEK);

                final String dayOfWeek = getDayOfWeek(getApplicationContext(), dow);

                fuelExpenseCreatedOnTV.setText(getString(R.string.date_with_week, dayOfWeek, sdf.format(date)));

                final String pricePerLitre = String.format(Locale.getDefault(), "%.2f", fuelExpense.getPricePerLitre());
                fuelExpensePricePerLitreDetailsTV.setText(getString(R.string.fuel_expense_price_per_litre_data, pricePerLitre));

                final String litres = String.format(Locale.getDefault(), "%.2f", fuelExpense.getLitres());
                fuelExpenseLitresDetailsTV.setText(getString(R.string.fuel_expense_litres_data, litres));

                fuelExpenseDiscountDetailsTV.setText(getString(R.string.price_bgn, discountFormatted));

                final String discountPricePerLitre = String.format(Locale.getDefault(), "%.2f", (total / fuelExpense.getLitres()));

                fuelExpenseDiscountPricePerDetailsLitreTV.setText(getString(R.string.fuel_expense_price_per_litre_data, discountPricePerLitre));

                final Car car = fuelExpense.getCar();

                fuelExpenseCarBrandModelTV.setText(getString(R.string.fuel_expense_car_brand_model, car.getBrand(), car.getModel()));
                fuelExpenseCarLicensePlateTV.setText(car.getLicensePlate());

                if (fuelExpense.getMileage() != null) {
                    fuelExpenseCarMileageTV.setTypeface(null, Typeface.NORMAL);
                    fuelExpenseCarMileageTV.setText(getString(R.string.fuel_expense_car_mileage_data, fuelExpense.getMileage()));
                } else {
                    fuelExpenseCarMileageTV.setTypeface(null, Typeface.ITALIC);
                    fuelExpenseCarMileageTV.setText(R.string.fuel_expense_car_mileage_unknown);
                }

                fuelExpenseProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NotNull Call<FuelExpense> call, @NotNull Throwable t) {
                fuelExpenseProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void editFuelExpense() {

        Intent intent = new Intent(getApplicationContext(), FuelExpenseUpdateActivity.class);
        intent.putExtra("fuelExpenseId", fuelExpenseId);
        startActivity(intent);
    }

    private void deleteFuelExpense() {

        AlertDialog.Builder builder = new AlertDialog.Builder(FuelExpenseActivity.this);

        builder.setTitle(R.string.fuel_expense_delete_title);

        builder.setMessage(R.string.fuel_expense_delete_message);

        ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setMessage(getString(R.string.fuel_expense_delete_process));

        builder.setPositiveButton(R.string.yes, (dialog, which) -> {

            loadingDialog.show();

            Retrofit retrofit = getRetrofit();

            ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

            expensesApi.deleteFuelExpense(fuelExpenseId, loggedUser.getAuthorization()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NotNull Call<Boolean> call, @NotNull Response<Boolean> response) {

                    if (response.code() == 404) {
                        Toast.makeText(getApplicationContext(), R.string.fuel_expense_not_found, Toast.LENGTH_LONG).show();
                        loadingDialog.hide();
                        return;
                    }

                    final Boolean result = response.body();

                    if (result != null) {
                        if (result) {
                            loadingDialog.hide();
                            Toast.makeText(getApplicationContext(), R.string.fuel_expense_delete_success, Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("fragment", "fuel_expenses");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
//                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.fuel_expense_delete_error, Toast.LENGTH_LONG).show();
                        }
                    }

                    loadingDialog.hide();
                }

                @Override
                public void onFailure(@NotNull Call<Boolean> call, @NotNull Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.fuel_expense_delete_error, Toast.LENGTH_LONG).show();
                    loadingDialog.hide();
                }
            });
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    @Override
    protected void onResume() {
        getFuelExpense(fuelExpenseId);
        fuelExpenseProgress.setVisibility(View.GONE);
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }
}
