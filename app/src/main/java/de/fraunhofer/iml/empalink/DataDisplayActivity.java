package de.fraunhofer.iml.empalink;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        final LineChart bvpChart = findViewById(R.id.BVP);

        List<Entry> entries = new ArrayList<Entry>();

        Random r = new Random();
        for(int it = 0; it < 2000; it++)
        {
            entries.add(new Entry(it, r.nextInt(50)));
        }

        LineDataSet set1 = new LineDataSet(entries, "Label"); // add entries to dataset
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setLineWidth(1.8f);
        set1.setCircleRadius(4f);
        //set1.setCircleColor(Color.WHITE);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        //set1.setColor(Color.WHITE);
        //set1.setFillColor(Color.WHITE);
        set1.setFillAlpha(100);


        LineData lineData = new LineData(set1);
        bvpChart.setData(lineData);

        bvpChart.setVisibleXRangeMaximum(20);

        bvpChart.invalidate();
    }
}
