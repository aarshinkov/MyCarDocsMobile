package bg.forcar.mobile.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import com.aarshinkov.mobile.mycardocs.R;
import bg.forcar.mobile.api.ExpensesApi;
import bg.forcar.mobile.responses.expenses.ExpenseSummaryItem;
import bg.forcar.mobile.responses.expenses.ExpensesSummaryResponse;
import bg.forcar.mobile.utils.LoggedUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static bg.forcar.mobile.api.Api.getRetrofit;
import static bg.forcar.mobile.utils.AppConstants.SHARED_PREF_NAME;
import static bg.forcar.mobile.utils.Utils.getLoggedUser;

public class ChartActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private LoggedUser loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        loggedUser = getLoggedUser(pref);

        Retrofit retrofit = getRetrofit();
        ExpensesApi expensesApi = retrofit.create(ExpensesApi.class);

        final String userId = loggedUser.getUserId();
        final String carId = null;
        final Integer year = null;

        expensesApi.getExpensesSummary(userId, carId, year, loggedUser.getAuthorization()).enqueue(new Callback<ExpensesSummaryResponse>() {
            @Override
            public void onResponse(@NotNull Call<ExpensesSummaryResponse> call, @NotNull Response<ExpensesSummaryResponse> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Error getting expenses summary", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "Error getting expenses summary", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void generateChart(ExpensesSummaryResponse summary) {
        AnyChartView anyChartView = findViewById(R.id.anyChartView);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

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

        for (Map.Entry<String, CustomDataEntry> entry : entries.entrySet()) {
            // If you want all 12 months to be shown remove this IF statement and keep only the body
//            if (!entry.getValue().getFuel().equals(0) || !entry.getValue().getService().equals(0)) {
            seriesData.add(entry.getValue());
//            }
        }

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Data = set.mapAs("{ x: 'x', value: 'value2' }");

        final String fuelColor = String.format("#%06x", ContextCompat.getColor(this, R.color.danger) & 0xffffff);

        Bar series1 = barChart.bar(series1Data);
        series1.name(getString(R.string.fuel_label))
                .color(fuelColor);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER);

        final String serviceColor = String.format("#%06x", ContextCompat.getColor(this, R.color.colorAccent) & 0xffffff);

        Bar series2 = barChart.bar(series2Data);
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
        }

        public void setService(Number service) {
            this.service = service;
            super.setValue("value2", service);
        }

        public Number getFuel() {
            return fuel;
        }

        public Number getService() {
            return service;
        }
    }

//        AnyChartView anyChartView = findViewById(R.id.anyChartView);
//        anyChartView.setProgressBar(findViewById(R.id.progress_bar));
//
//        Cartesian cartesian = AnyChart.column();
//
//        List<DataEntry> data = new ArrayList<>();
//        data.add(new ValueDataEntry(getString(R.string.JAN), 254));
//        data.add(new ValueDataEntry(getString(R.string.FEB), 180));
//        data.add(new ValueDataEntry(getString(R.string.MAR), 196));
//        data.add(new ValueDataEntry(getString(R.string.APR), 200));
//        data.add(new ValueDataEntry(getString(R.string.MAY), 234));
//        data.add(new ValueDataEntry(getString(R.string.JUN), 287));
//        data.add(new ValueDataEntry(getString(R.string.JUL), 240));
//        data.add(new ValueDataEntry(getString(R.string.AUG), 240));
//        data.add(new ValueDataEntry(getString(R.string.SEP), 240));
//        data.add(new ValueDataEntry(getString(R.string.OCT), 240));
//        data.add(new ValueDataEntry(getString(R.string.NOV), 240));
//        data.add(new ValueDataEntry(getString(R.string.DEC), 240));
//
//        Column column = cartesian.column(data);
//
//        column.tooltip()
//                .titleFormat("{%X}")
//                .position(Position.CENTER_BOTTOM)
//                .anchor(Anchor.CENTER_BOTTOM)
//                .offsetX(0d)
//                .offsetY(5d)
//                .format("{%Value}{groupsSeparator: } " + getString(R.string.currency_bgn));
//
//        cartesian.animation(true);
//        final int year = 2021;
//        cartesian.title("Expenses for " + year);
//
//        cartesian.yScale().minimum(0d);
//
//        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");
//
//        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
//        cartesian.interactivity().hoverMode(HoverMode.BY_X);
//
//        cartesian.xAxis(0).title("Months");
//        cartesian.yAxis(0).title("Price");
//
//        anyChartView.setChart(cartesian);
//    }
}