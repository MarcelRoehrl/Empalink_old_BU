package de.fraunhofer.iml.empalink;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataDisplayActivity extends AppCompatActivity
{
    final int MAX_X_DATA = 30;
    LineChart bvpChart;
    LineChart edaChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        bvpChart = findViewById(R.id.BVP);
        edaChart = findViewById(R.id.EDA);

        List<Entry> entries = new ArrayList<Entry>();
        Random r = new Random();
        for(int it = 0; it < 2000; it++)
        {
            entries.add(new Entry(it, r.nextInt(50)));
        }

        LineData lineData = new LineData(createDataSet(entries, "BVP"));
        bvpChart.setData(lineData);
        bvpChart.getDescription().setText("BVP Daten");
        bvpChart.setVisibleXRangeMaximum(MAX_X_DATA);
        bvpChart.invalidate();


        entries = new ArrayList<Entry>();
        r = new Random();
        for(int it = 0; it < 2000; it++)
        {
            entries.add(new Entry(it, r.nextInt(50)));
        }

        lineData = new LineData(createDataSet(entries, "EDA"));
        edaChart.setData(lineData);
        edaChart.getDescription().setText("EDA Daten");
        edaChart.setVisibleXRangeMaximum(MAX_X_DATA);
        edaChart.invalidate();

        //Charts synchronisieren
        LineChart[] charts = {edaChart};
        LineChart[] charts2 = {bvpChart};
        bvpChart.setOnChartGestureListener(new CoupleChartGestureListener(bvpChart, charts));
        edaChart.setOnChartGestureListener(new CoupleChartGestureListener(edaChart, charts2));
    }

    private LineDataSet createDataSet(List<Entry> entries, String name)
    {
        LineDataSet set = new LineDataSet(entries, name);

        set.setDrawFilled(true);
        set.setDrawCircles(true);
        set.setLineWidth(1.8f);
        set.setCircleRadius(2f);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setFillAlpha(100);
        set.setCircleColor(Color.BLACK);
//        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        set.setCubicIntensity(0.2f);
//        set.setColor(Color.WHITE);
//        set.setFillColor(Color.WHITE);
        return set;
    }

}
