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
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LabelsOverlapMode;
import com.anychart.enums.Orientation;
import com.anychart.enums.ScaleStackMode;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.enums.TooltipPositionMode;
import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.ChartActivity;
import com.atanasvasil.mobile.mycardocs.api.CarsApi;
import com.atanasvasil.mobile.mycardocs.api.ExpensesApi;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.responses.expenses.ExpenseSummaryItem;
import com.atanasvasil.mobile.mycardocs.responses.expenses.ExpensesSummaryResponse;
import com.atanasvasil.mobile.mycardocs.utils.LoggedUser;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private MaterialButton someID;

    private SharedPreferences pref;
    private LoggedUser loggedUser;

    private Map<String, String> userCarsMap = new HashMap<>();
    private List<String> cars = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_expenses, container, false);

        expensesCarsSP = root.findViewById(R.id.expensesCarsSP);
        someID = root.findViewById(R.id.someID);

        pref = requireContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        loadCars();

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cars);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        getData(loggedUser.getUserId(), null, null);

//        expensesCarsSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                if (position == 0) {
//                    getData(loggedUser.getUserId(), null, null);
//                    return;
//                }
//
//                final String licensePlate = expensesCarsSP.getSelectedItem().toString();
//                final String carId = userCarsMap.get(licensePlate);
//
//                getData(loggedUser.getUserId(), carId, null);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        someID.setOnClickListener(v -> {
            int position = expensesCarsSP.getSelectedItemPosition();

            if (position == 0) {
                getData(loggedUser.getUserId(), null, null);
                return;
            }

            final String licensePlate = expensesCarsSP.getSelectedItem().toString();
            final String carId = userCarsMap.get(licensePlate);

            getData(loggedUser.getUserId(), carId, null);
        });

        return root;
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

                cars.add("Any");

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

    private void getData(String userId, String carId, Integer year) {
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

        List<DataEntry> seriesData = new ArrayList<>();

        Map<String, CustomDataEntry> entries = initData(summary.getFuel());

        for (ExpenseSummaryItem serviceItem : summary.getService()) {
            final double negative = serviceItem.getTotal() * -1;
            entries.get(getMonth(serviceItem.getMonth())).setService(negative);
        }

        List<DataEntry> fuelData = new ArrayList<>();
        List<DataEntry> serviceData = new ArrayList<>();

        for (Map.Entry<String, CustomDataEntry> entry : entries.entrySet()) {
            // If you want all 12 months to be shown remove this IF statement and keep only the body
//            if (!entry.getValue().getFuel().equals(0) || !entry.getValue().getService().equals(0)) {
            Log.d("Test", "" + entry.getValue().getLabel());

            DataEntry fuel = new DataEntry();
            fuel.setValue(entry.getKey(), entry.getValue().fuel);
            fuelData.add(fuel);

            DataEntry service = new DataEntry();
            service.setValue(entry.getKey(), entry.getValue().service);
            seriesData.add(service);
//            CustomDataEntry dataEntry = entry.getValue().;

//            seriesData.add(dataEntry);
//            }
        }


        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Data = set.mapAs("{ x: 'x', value: 'value2' }");

        final String fuelColor = String.format("#%06x", ContextCompat.getColor(requireContext(), R.color.danger) & 0xffffff);

        Bar series1 = barChart.bar(fuelData);
        series1.name(getString(R.string.fuel_label))
                .color(fuelColor);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER);

        final String serviceColor = String.format("#%06x", ContextCompat.getColor(requireContext(), R.color.colorAccent) & 0xffffff);

        Bar series2 = barChart.bar(seriesData);
        series2.name(getString(R.string.service_label))
                .color(serviceColor);
        series2.tooltip()
                .position("left")
                .anchor(Anchor.RIGHT_CENTER);

        barChart.legend().enabled(true);
        barChart.legend().inverted(true);
        barChart.legend().fontSize(13d);
        barChart.legend().padding(0d, 0d, 20d, 0d);

        anyChartView.setChart(barChart);
    }

    private Map<String, CustomDataEntry> initData(List<ExpenseSummaryItem> fuel) {

        // LinkedHashMap is used because it keeps the insertion order
        Map<String, CustomDataEntry> entries = new LinkedHashMap<>();

        if (fuel.size() == 0) {
            for (int i = 1, j = 0; i <= 12; i++) {
                CustomDataEntry entry = new CustomDataEntry(getMonth(i), 0);
                entry.setService(0);
                entries.put(getMonth(i), entry);
            }

            return entries;
        }

        final int elementsNum = fuel.size() - 1;
        for (int i = 1, j = 0; i <= 12; i++) {
            ExpenseSummaryItem item = fuel.get(j);
            if (item.getMonth().equals(i)) {
                CustomDataEntry entry = new CustomDataEntry(getMonth(i), item.getTotal());
                entry.setService(0);
                entries.put(getMonth(i), entry);
                if (j < elementsNum) {
                    j++;
                }
            } else {
                CustomDataEntry entry = new CustomDataEntry(getMonth(i), 0);
                entry.setService(0);
                entries.put(getMonth(i), entry);
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

        private String label;
        private final Number fuel;
        private Number service;

        public CustomDataEntry(final String label, final Number fuel) {
            super(label, fuel);
            this.fuel = fuel;
            this.label = label;
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
