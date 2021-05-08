package com.atanasvasil.mobile.mycardocs.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.axes.Linear;
import com.anychart.core.cartesian.series.Bar;
import com.anychart.core.cartesian.series.Column;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LabelsOverlapMode;
import com.anychart.enums.Orientation;
import com.anychart.enums.Position;
import com.anychart.enums.ScaleStackMode;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.enums.TooltipPositionMode;
import com.atanasvasil.mobile.mycardocs.R;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

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

        barChart.yAxis(0d).title("Revenue in Dollars");

        barChart.xAxis(0d).overlapMode(LabelsOverlapMode.ALLOW_OVERLAP);

        Linear xAxis1 = barChart.xAxis(1d);
        xAxis1.enabled(true);
        xAxis1.orientation(Orientation.RIGHT);
        xAxis1.overlapMode(LabelsOverlapMode.ALLOW_OVERLAP);

        barChart.title("Cosmetic Sales by Gender");

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
        seriesData.add(new CustomDataEntry(getString(R.string.JAN), 5376, -229));
        seriesData.add(new CustomDataEntry(getString(R.string.FEB), 10987, -932));
        seriesData.add(new CustomDataEntry(getString(R.string.MAR), 7624, -5221));
        seriesData.add(new CustomDataEntry(getString(R.string.APR), 8814, -256));
        seriesData.add(new CustomDataEntry(getString(R.string.MAY), 8998, -308));
        seriesData.add(new CustomDataEntry(getString(R.string.JUN), 9321, -432));
        seriesData.add(new CustomDataEntry(getString(R.string.JUL), 8342, -701));
        seriesData.add(new CustomDataEntry(getString(R.string.AUG), 6998, -908));
        seriesData.add(new CustomDataEntry(getString(R.string.SEP), 9261, -712));
        seriesData.add(new CustomDataEntry(getString(R.string.OCT), 5376, -9229));
        seriesData.add(new CustomDataEntry(getString(R.string.NOV), 10987, -13932));
        seriesData.add(new CustomDataEntry(getString(R.string.DEC), 7624, -10221));

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Data = set.mapAs("{ x: 'x', value: 'value2' }");

        Bar series1 = barChart.bar(series1Data);
        series1.name(getString(R.string.fuel_label))
                .color("Red");
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER);

        Bar series2 = barChart.bar(series2Data);
        series2.name(getString(R.string.service_label));
        series2.tooltip()
                .position("left")
                .anchor(Anchor.RIGHT_CENTER);

        barChart.legend().enabled(true);
        barChart.legend().inverted(true);
        barChart.legend().fontSize(13d);
        barChart.legend().padding(0d, 0d, 20d, 0d);

        anyChartView.setChart(barChart);
    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);
        }
    }
//
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