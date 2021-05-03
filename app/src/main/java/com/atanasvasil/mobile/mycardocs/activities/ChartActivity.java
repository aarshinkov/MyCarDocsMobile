package com.atanasvasil.mobile.mycardocs.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
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

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry(getString(R.string.JAN), 254));
        data.add(new ValueDataEntry(getString(R.string.FEB), 180));
        data.add(new ValueDataEntry(getString(R.string.MAR), 196));
        data.add(new ValueDataEntry(getString(R.string.APR), 200));
        data.add(new ValueDataEntry(getString(R.string.MAY), 234));
        data.add(new ValueDataEntry(getString(R.string.JUN), 287));
        data.add(new ValueDataEntry(getString(R.string.JUL), 240));
        data.add(new ValueDataEntry(getString(R.string.AUG), 240));
        data.add(new ValueDataEntry(getString(R.string.SEP), 240));
        data.add(new ValueDataEntry(getString(R.string.OCT), 240));
        data.add(new ValueDataEntry(getString(R.string.NOV), 240));
        data.add(new ValueDataEntry(getString(R.string.DEC), 240));

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: } " + getString(R.string.currency_bgn));

        cartesian.animation(true);
        final int year = 2021;
        cartesian.title("Expenses for " + year);

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: } " + getString(R.string.currency_bgn));

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Product");
        cartesian.yAxis(0).title("Revenue");

        anyChartView.setChart(cartesian);
    }
}