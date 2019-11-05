package de.fraunhofer.iml.empalink;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.opencsv.CSVReader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.fraunhofer.iml.empalink.SensorObjects.BVP;
import de.fraunhofer.iml.empalink.SensorObjects.EDA;

public class DataDisplayActivity extends AppCompatActivity
{
    protected final int MAX_X_DATA = 10;
    protected LineChart bvpChart;
    protected LineChart edaChart;
    protected LineChart tempChart;
    private String filePath;

    private ArrayList<Entry> accData;
    private ArrayList<Entry> BVPData;
    private ArrayList<Entry> EDAData;
    private ArrayList<Entry> IBIData;
    private ArrayList<Entry> tempData;
    private ArrayList<Entry> pStressData;
    private ArrayList<Entry> mStressData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        bvpChart = findViewById(R.id.BVP);
        edaChart = findViewById(R.id.EDA);
        tempChart= findViewById(R.id.Temperature);

        accData = new ArrayList<Entry>();
        BVPData = new ArrayList<Entry>();
        EDAData = new ArrayList<Entry>();
        IBIData = new ArrayList<Entry>();
        tempData = new ArrayList<Entry>();
        pStressData = new ArrayList<Entry>();
        mStressData = new ArrayList<Entry>();

        filePath = getApplicationContext().getResources().getString(R.string.path)+ File.separator + "test.csv";

        load();

        LineData lineData = new LineData(createDataSet(BVPData, "BVP"));
        bvpChart.setData(lineData);
        bvpChart.getDescription().setText("BVP Daten");
        bvpChart.setVisibleXRangeMaximum(MAX_X_DATA);
        bvpChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        bvpChart.getAxisRight().setEnabled(false);
        bvpChart.getLegend().setEnabled(false);
        bvpChart.invalidate();

        lineData = new LineData(createDataSet(EDAData, "EDA"));
        edaChart.setData(lineData);
        edaChart.getDescription().setText("EDA Daten");
        edaChart.setVisibleXRangeMaximum(MAX_X_DATA);
        edaChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        edaChart.getAxisRight().setEnabled(false);
        edaChart.getLegend().setEnabled(false);
        edaChart.invalidate();

        lineData = new LineData(createDataSet(tempData, "Temperature"));
        tempChart.setData(lineData);
        tempChart.getDescription().setText("Temparatur Daten");
        tempChart.setVisibleXRangeMaximum(MAX_X_DATA);
        tempChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        tempChart.getAxisRight().setEnabled(false);
        tempChart.getLegend().setEnabled(false);
        tempChart.invalidate();

        //Charts synchronisieren
        LineChart[] charts = {edaChart, tempChart};
        LineChart[] charts2 = {bvpChart, tempChart};
        LineChart[] charts3 = {bvpChart, edaChart};
        bvpChart.setOnChartGestureListener(new CoupleChartGestureListener(bvpChart, charts));
        edaChart.setOnChartGestureListener(new CoupleChartGestureListener(edaChart, charts2));
        tempChart.setOnChartGestureListener(new CoupleChartGestureListener(tempChart, charts3));
    }

    public void load()
    {
        try {
            CSVReader reader = new CSVReader(Files.newBufferedReader(Paths.get(filePath)));
            reader.skip(2);

            Iterator<String[]> it = reader.iterator();
            while(it.hasNext())
            {
                String[] line = it.next();
                float stamp = (float)Double.parseDouble(line[0]);
                for(int i = 1; i <= 7; i++)
                {
                    if(line[i].length() > 0)
                    {
                        switch (i) {
                            case 1: BVPData.add(new Entry(stamp, Float.valueOf(line[i]))); break;
                            case 2: EDAData.add(new Entry(stamp, Float.valueOf(line[i]))); break;
                            case 3: IBIData.add(new Entry(stamp, Float.valueOf(line[i]))); break;
                            case 4: tempData.add(new Entry(stamp, Float.valueOf(line[i]))); break;
                            case 5: {
                                String[] temp = line[i].split(",");
                                //accData.add(new Entry(Integer.valueOf(temp[0]),Integer.valueOf(temp[1]),Integer.valueOf(temp[2]),stamp)); TODO G wert ausrechnen
                                accData.add(new Entry(stamp, (float)Integer.valueOf(temp[0])));
                            } break;
                            case 6: pStressData.add(new Entry(stamp, (float)Integer.valueOf(line[i]))); break;
                            case 7: mStressData.add(new Entry(stamp, (float)Integer.valueOf(line[i]))); break;
                        }
                    }
                }
                it.hasNext();
            }

            //Daten anpassen um alle Graphen bei 0 bis zum höchsten X Wert darzustellen
            float max = Math.max(Math.max(Math.max(BVPData.get(BVPData.size()-1).getX(), EDAData.get(EDAData.size()-1).getX()), IBIData.get(IBIData.size()-1).getX()), tempData.get(tempData.size()-1).getX());
            BVPData.add(new Entry(max, BVPData.get(BVPData.size()-1).getY()));
            EDAData.add(new Entry(max, EDAData.get(EDAData.size()-1).getY()));
            IBIData.add(new Entry(max, IBIData.get(IBIData.size()-1).getY()));
            tempData.add(new Entry(max, tempData.get(tempData.size()-1).getY()));

            if( BVPData.get(0).getX() > 0 )
                BVPData.add(new Entry(0, BVPData.get(0).getY()));
            if( EDAData.get(0).getX() > 0 )
                EDAData.add(new Entry(0, EDAData.get(0).getY()));
            if( IBIData.get(0).getX() > 0 )
                IBIData.add(new Entry(0, IBIData.get(0).getY()));
            if( tempData.get(0).getX() > 0 )
                tempData.add(new Entry(0, tempData.get(0).getY()));
        }
        catch (Exception e)
        {}
    }

    private LineDataSet createDataSet(List<Entry> entries, String name)
    {
        LineDataSet set = new LineDataSet(entries, name);

//        set.setColor(Color.rgb(255, 153, 0));
//        set.setFillColor(Color.rgb(255, 153, 0));
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
