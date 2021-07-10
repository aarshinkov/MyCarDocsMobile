package bg.forcar.mobile.activities.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import bg.forcar.mobile.R;
import bg.forcar.mobile.api.ExpensesApi;
import bg.forcar.mobile.responses.cars.Car;
import bg.forcar.mobile.responses.expenses.service.ServiceExpense;
import bg.forcar.mobile.utils.LoggedUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
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
import static bg.forcar.mobile.utils.Utils.getStringResource;

public class ServiceExpenseActivity extends AppCompatActivity {

    private TextView serviceExpenseTotalTV;
    private TextView serviceExpenseCreatedOnTV;

    private Chip serviceExpenseTypeChip;

    private TextView serviceExpenseNotesTV;

    private TextView serviceExpenseCarBrandModelTV;
    private TextView serviceExpenseCarLicensePlateTV;
    private TextView serviceExpenseCarMileageTV;

    private MaterialButton serviceExpenseBackBtn;

    private CircularProgressIndicator serviceExpenseProgress;

    private SharedPreferences pref;
    private LoggedUser loggedUser;

    private String serviceExpenseId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_expense);

        getSupportActionBar().setTitle(R.string.service_expense_title);

        final SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_time_2), Locale.getDefault());

        serviceExpenseTotalTV = findViewById(R.id.serviceExpenseTotalTV);
        serviceExpenseCreatedOnTV = findViewById(R.id.serviceExpenseCreatedOnTV);

        serviceExpenseTypeChip = findViewById(R.id.serviceExpenseTypeChip);

        serviceExpenseNotesTV = findViewById(R.id.serviceExpenseNotesTV);

        serviceExpenseCarBrandModelTV = findViewById(R.id.serviceExpenseCarBrandModelTV);
        serviceExpenseCarLicensePlateTV = findViewById(R.id.serviceExpenseCarLicensePlateTV);
        serviceExpenseCarMileageTV = findViewById(R.id.serviceExpenseCarMileageTV);

        serviceExpenseBackBtn = findViewById(R.id.serviceExpenseBackBtn);

        serviceExpenseProgress = findViewById(R.id.serviceExpenseProgress);

        serviceExpenseProgress.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        serviceExpenseId = intent.getStringExtra("serviceExpenseId");

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        Retrofit retrofit = getRetrofit();
        ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

        expensesApi.getServiceExpense(serviceExpenseId, loggedUser.getAuthorization()).enqueue(new Callback<ServiceExpense>() {
            @Override
            public void onResponse(@NotNull Call<ServiceExpense> call, @NotNull Response<ServiceExpense> response) {

                if (!response.isSuccessful()) {
                    finish();
                    serviceExpenseProgress.setVisibility(View.INVISIBLE);
                    return;
                }

                ServiceExpense serviceExpense = response.body();

                if (serviceExpense == null) {
                    serviceExpenseProgress.setVisibility(View.INVISIBLE);
                    finish();
                    return;
                }

                final String totalFormatted = String.format(Locale.getDefault(), "%.2f", serviceExpense.getPrice());

                serviceExpenseTotalTV.setText(getString(R.string.negative_number, getString(R.string.price_bgn, totalFormatted)));

                Date date = new Date();
                date.setTime(serviceExpense.getCreatedOn().getTime());

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int dow = cal.get(Calendar.DAY_OF_WEEK);

                final String dayOfWeek = getDayOfWeek(getApplicationContext(), dow);

                serviceExpenseCreatedOnTV.setText(getString(R.string.date_with_week, dayOfWeek, sdf.format(date)));

                if (serviceExpense.getNotes() != null) {
                    if (serviceExpense.getNotes().isEmpty()) {
                        serviceExpenseNotesTV.setTypeface(null, Typeface.ITALIC);
                        serviceExpenseNotesTV.setText(getString(R.string.service_expense_notes_not_found));
                    } else {
                        serviceExpenseNotesTV.setTypeface(null, Typeface.NORMAL);
                        serviceExpenseNotesTV.setText(Html.fromHtml(serviceExpense.getNotes(), 0));
                    }
                } else {
                    serviceExpenseNotesTV.setTypeface(null, Typeface.ITALIC);
                    serviceExpenseNotesTV.setText(getString(R.string.service_expense_notes_not_found));
                }

                final Integer type = serviceExpense.getType().getType();

                serviceExpenseTypeChip.setText(getStringResource(getApplicationContext(), "service_type_" + type));

                switch (type) {
                    case 1:
                        serviceExpenseTypeChip.setChipIconResource(R.drawable.ic_service);
                        break;
                    case 2:
                        serviceExpenseTypeChip.setChipIconResource(R.drawable.ic_engine);
                        break;
                    case 3:
                        serviceExpenseTypeChip.setChipIconResource(R.drawable.ic_tyre);
                        break;
                    case 4:
                        serviceExpenseTypeChip.setChipIconResource(R.drawable.ic_oil);
                        break;
                    case 5:
                    case 6:
                        serviceExpenseTypeChip.setChipIconResource(R.drawable.ic_car_filter);
                        break;
                    case 7:
                        serviceExpenseTypeChip.setChipIconResource(R.drawable.ic_battery);
                        break;
                    case 8:
                        serviceExpenseTypeChip.setChipIconResource(R.drawable.ic_balance);
                        break;
                    case 9:
                        serviceExpenseTypeChip.setChipIconResource(R.drawable.ic_belt);
                        break;
                    case 10:
                        serviceExpenseTypeChip.setChipIconResource(R.drawable.ic_tow);
                        break;
                    case 11:
                        serviceExpenseTypeChip.setChipIconResource(R.drawable.ic_lights);
                        break;
                    case 12:
                        serviceExpenseTypeChip.setChipIconResource(R.drawable.ic_repair);
                        break;
                }

                final Car car = serviceExpense.getCar();

                serviceExpenseCarBrandModelTV.setText(getString(R.string.service_expense_car_brand_model, car.getBrand(), car.getModel()));
                serviceExpenseCarLicensePlateTV.setText(car.getLicensePlate());

                if (serviceExpense.getMileage() != null) {
                    serviceExpenseCarMileageTV.setTypeface(null, Typeface.NORMAL);
                    serviceExpenseCarMileageTV.setText(getString(R.string.service_expense_car_mileage_data, serviceExpense.getMileage()));
                } else {
                    serviceExpenseCarMileageTV.setTypeface(null, Typeface.ITALIC);
                    serviceExpenseCarMileageTV.setText(R.string.service_expense_car_mileage_unknown);
                }

                serviceExpenseProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NotNull Call<ServiceExpense> call, @NotNull Throwable t) {
                serviceExpenseProgress.setVisibility(View.INVISIBLE);
            }
        });

        serviceExpenseBackBtn.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        onBackPressed();
        return true;
    }
}

