package com.atanasvasil.mobile.mycardocs.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.axes.Linear;
import com.anychart.core.cartesian.series.Bar;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LabelsOverlapMode;
import com.anychart.enums.Orientation;
import com.anychart.enums.ScaleStackMode;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.enums.TooltipPositionMode;
import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.api.ExpensesApi;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.responses.expenses.ExpenseSummaryItem;
import com.atanasvasil.mobile.mycardocs.responses.expenses.ExpensesSummaryResponse;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static com.atanasvasil.mobile.mycardocs.api.Api.getRetrofit;
import static com.atanasvasil.mobile.mycardocs.utils.AppConstants.SHARED_PREF_NAME;
import static com.atanasvasil.mobile.mycardocs.utils.Utils.getLoggedUser;

public class ExpensesFragment extends Fragment {

    private Spinner expensesCarsSP;
    private MaterialButton expensesFilterBtn;

    private SharedPreferences pref;
    private LoggedUser loggedUser;

    private Map<String, String> userCarsMap = new HashMap<>();
    private List<String> cars = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private View root;

    private Bar fuelBar;
    private Bar serviceBar;

    private boolean isSetup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_expenses, container, false);

        expensesFilterBtn = root.findViewById(R.id.expensesFilterBtn);

        pref = requireContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cars);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        initData(loggedUser.getUserId());

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);

        final View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_dialog, root.findViewById(R.id.bottomSheetContainer));
        expensesCarsSP = bottomSheetView.findViewById(R.id.expensesCarsSP);

        loadCars();
        bottomSheetDialog.setContentView(bottomSheetView);

        expensesFilterBtn.setOnClickListener(v -> {
            bottomSheetDialog.show();
        });

        isSetup = true;

        expensesCarsSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // При първото отваряне на диалоговия прозорец за филтриране го прибира заради долната логика.
                // На следващите кликания нямаше проблем, затова правим тази проверка дали сме първо кликане.
                if (isSetup) {
                    isSetup = false;
                    return;
                }

                if (position == 0) {
                    reloadData(loggedUser.getUserId(), null, null);
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                    return;
                }

                final String licensePlate = expensesCarsSP.getSelectedItem().toString();
                final String carId = userCarsMap.get(licensePlate);

                reloadData(loggedUser.getUserId(), carId, null);
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }

    private void reloadData(String userId, String carId, Integer year) {
        Retrofit retrofit = getRetrofit();
        ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

        expensesApi.getExpensesSummary(userId, carId, year, loggedUser.getAuthorization()).enqueue(new Callback<ExpensesSummaryResponse>() {
            @Override
            public void onResponse(@NotNull Call<ExpensesSummaryResponse> call, @NotNull Response<ExpensesSummaryResponse> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Error getting expenses summary", Toast.LENGTH_LONG).show();
                    return;
                }

                ExpensesSummaryResponse summary = response.body();

                if (summary == null) {
                    return;
                }

                List<DataEntry> fuelEntries = initFuelData(summary.getFuel());
                List<DataEntry> serviceEntries = initServiceData(summary.getService());

                fuelBar.data(fuelEntries);
                serviceBar.data(serviceEntries);
            }

            @Override
            public void onFailure(@NotNull Call<ExpensesSummaryResponse> call, @NotNull Throwable t) {
                Log.e("EXPENSES_SUMMARY", t.getMessage());
                Toast.makeText(requireContext(), "Error getting expenses summary", Toast.LENGTH_LONG).show();
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

                if (storedCars == null) {
                    return;
                }

                cars.add(requireContext().getString(R.string.expenses_cars_any));

                for (Car car : storedCars) {
                    userCarsMap.put(car.getLicensePlate(), car.getCarId());
                    cars.add(car.getLicensePlate());
                }

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                expensesCarsSP.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NotNull Call<List<Car>> call, @NotNull Throwable t) {
                Toast.makeText(requireContext(), R.string.error_server, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Initial set of the data chart
     *
     * @param userId the user ID for the expenses report
     */
    private void initData(String userId) {
        Retrofit retrofit = getRetrofit();
        ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

        expensesApi.getExpensesSummary(userId, null, null, loggedUser.getAuthorization()).enqueue(new Callback<ExpensesSummaryResponse>() {
            @Override
            public void onResponse(@NotNull Call<ExpensesSummaryResponse> call, @NotNull Response<ExpensesSummaryResponse> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Error getting expenses summary", Toast.LENGTH_LONG).show();
                    return;
                }

                ExpensesSummaryResponse summary = response.body();

                if (summary == null) {
                    return;
                }

                generateChart(summary);
            }

            @Override
            public void onFailure(@NotNull Call<ExpensesSummaryResponse> call, @NotNull Throwable t) {
                Log.e("EXPENSES_SUMMARY", t.getMessage());
                Toast.makeText(requireContext(), "Error getting expenses summary", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void generateChart(ExpensesSummaryResponse summary) {

        AnyChartView anyChartView = root.findViewById(R.id.expensesChart);
        anyChartView.setProgressBar(root.findViewById(R.id.expensesProgress));

        Cartesian barChart = AnyChart.bar();

        barChart.animation(true);

        barChart.padding(10d, 20d, 5d, 20d);

        barChart.yScale().stackMode(ScaleStackMode.VALUE);

        barChart.yAxis(0).labels().format(
                "function() {\n" +
                        "    return Math.abs(this.value).toLocaleString();\n" +
                        "  }");

        barChart.yAxis(0d).title("Expenses in lv.");

        barChart.xAxis(0d).overlapMode(LabelsOverlapMode.ALLOW_OVERLAP);

        Linear xAxis1 = barChart.xAxis(1d);
        xAxis1.enabled(true);
        xAxis1.orientation(Orientation.RIGHT);
        xAxis1.overlapMode(LabelsOverlapMode.ALLOW_OVERLAP);

        Calendar calendar = Calendar.getInstance();
        final int currentYear = calendar.get(Calendar.YEAR);


        barChart.title("Expenses for " + currentYear);

        barChart.interactivity().hoverMode(HoverMode.BY_X);

        barChart.tooltip()
                .title(false)
                .separator(false)
                .displayMode(TooltipDisplayMode.SEPARATED)
                .positionMode(TooltipPositionMode.POINT)
                .useHtml(true)
                .fontSize(12d)
                .offsetX(5d)
                .offsetY(0d)
                .format(
                        "function() {\n" +
                                "      return Math.abs(this.value).toLocaleString() + ' <span style=\"color: #D9D9D9\">" + getString(R.string.currency_bgn) + "</span>'\n" +
                                "    }");

        List<DataEntry> fuelData = initFuelData(summary.getFuel());

        List<DataEntry> serviceData = initServiceData(summary.getService());

        final String fuelColor = String.format("#%06x", ContextCompat.getColor(requireContext(), R.color.danger) & 0xffffff);

        fuelBar = barChart.bar(fuelData);
        fuelBar.name(getString(R.string.fuel_label))
                .color(fuelColor);
        fuelBar.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER);

        final String serviceColor = String.format("#%06x", ContextCompat.getColor(requireContext(), R.color.colorAccent) & 0xffffff);

        serviceBar = barChart.bar(serviceData);
        serviceBar.name(getString(R.string.service_label))
                .color(serviceColor);
        serviceBar.tooltip()
                .position("left")
                .anchor(Anchor.RIGHT_CENTER);

        barChart.legend().enabled(true);
        barChart.legend().inverted(true);
        barChart.legend().fontSize(13d);
        barChart.legend().padding(0d, 0d, 20d, 0d);

        anyChartView.setChart(barChart);
    }

    private List<DataEntry> initFuelData(List<ExpenseSummaryItem> fuel) {

        List<DataEntry> entries = new ArrayList<>();

        if (fuel.size() == 0) {
            for (int i = 1; i <= 12; i++) {
                CustomDataEntry entry = new CustomDataEntry(getMonth(i), 0, true);
                entries.add(entry);
            }

            return entries;
        }

        final int elementsNum = fuel.size() - 1;
        for (int i = 1, j = 0; i <= 12; i++) {
            ExpenseSummaryItem item = fuel.get(j);
            if (item.getMonth().equals(i)) {
                CustomDataEntry entry = new CustomDataEntry(getMonth(i), item.getTotal(), true);
                entries.add(entry);
                if (j < elementsNum) {
                    j++;
                }
            } else {
                CustomDataEntry entry = new CustomDataEntry(getMonth(i), 0, true);
                entries.add(entry);
            }
        }

        return entries;
    }

    private List<DataEntry> initServiceData(List<ExpenseSummaryItem> service) {

        List<DataEntry> entries = new ArrayList<>();

        if (service.size() == 0) {
            for (int i = 1; i <= 12; i++) {
                CustomDataEntry entry = new CustomDataEntry(getMonth(i), 0, false);
                entries.add(entry);
            }

            return entries;
        }

        final int elementsNum = service.size() - 1;
        for (int i = 1, j = 0; i <= 12; i++) {
            ExpenseSummaryItem item = service.get(j);
            if (item.getMonth().equals(i)) {
                CustomDataEntry entry = new CustomDataEntry(getMonth(i), item.getTotal() * -1, false);
                entries.add(entry);
                if (j < elementsNum) {
                    j++;
                }
            } else {
                CustomDataEntry entry = new CustomDataEntry(getMonth(i), 0, false);
                entries.add(entry);
            }
        }

        return entries;
    }

    private String getMonth(Integer month) {
        switch (month) {
            case 1:
                return getString(R.string.JAN);
            case 2:
                return getString(R.string.FEB);
            case 3:
                return getString(R.string.MAR);
            case 4:
                return getString(R.string.APR);
            case 5:
                return getString(R.string.MAY);
            case 6:
                return getString(R.string.JUN);
            case 7:
                return getString(R.string.JUL);
            case 8:
                return getString(R.string.AUG);
            case 9:
                return getString(R.string.SEP);
            case 10:
                return getString(R.string.OCT);
            case 11:
                return getString(R.string.NOV);
            default:
                return getString(R.string.DEC);
        }
    }

    private static class CustomDataEntry extends ValueDataEntry {

        private final String label;
        private Number fuel;
        private Number service;

        public CustomDataEntry(String label, Number expense, boolean isFuel) {
            super(label, expense);
            this.label = label;

            if (isFuel) {
                this.fuel = expense;
            } else {
                this.service = expense;
            }
        }

        public void setService(Number service) {
            this.service = service;
            super.setValue("value2", service);
        }

        public String getLabel() {
            return label;
        }

        public Number getFuel() {
            return fuel;
        }

        public Number getService() {
            return service;
        }
    }
}
