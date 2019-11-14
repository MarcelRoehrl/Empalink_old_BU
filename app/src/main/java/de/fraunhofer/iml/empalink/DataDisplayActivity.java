package de.fraunhofer.iml.empalink;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.chip.Chip;
import com.opencsv.CSVReader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataDisplayActivity extends AppCompatActivity
{
    protected final int MAX_X_DATA = 10;
    protected CombinedChart edaChart, tempChart, ibiChart, bvpChart;
    protected Chip bvpChip, edaChip, ibiChip, tempChip;
    private String filePath;

    private ArrayList<Entry> accData, tempData, BVPData, EDAData, IBIData;
    private ArrayList<BarEntry> pStressData, mStressData;
    private CoupleChartGestureListener bvpListener, edaListener, tempListener, ibiListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        bvpChart = findViewById(R.id.BVP);
        edaChart = findViewById(R.id.EDA);
        tempChart= findViewById(R.id.Temperature);
        ibiChart = findViewById(R.id.IBI);
        bvpChip = findViewById(R.id.BVP_chip);
        edaChip = findViewById(R.id.EDA_chip);
        ibiChip = findViewById(R.id.IBI_chip);
        tempChip = findViewById(R.id.Temp_chip);

        accData = new ArrayList<Entry>();
        BVPData = new ArrayList<Entry>();
        EDAData = new ArrayList<Entry>();
        IBIData = new ArrayList<Entry>();
        tempData = new ArrayList<Entry>();
        pStressData = new ArrayList<BarEntry>();
        mStressData = new ArrayList<BarEntry>();

        filePath = getApplicationContext().getResources().getString(R.string.path)+ File.separator + getIntent().getStringExtra(V.FILENAME_EXTRA);

        load();

        CombinedData combinedData = new CombinedData();
        combinedData.setData(new LineData(createLineDataSet(BVPData, "BVP")));
        PointF extremes = maxYEntry(BVPData);
        combinedData.setData(new BarData(createBarDataSet(adjustEntries(pStressData, mStressData, extremes.x, extremes.y), "mentaler Stress")));
        bvpChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});
        bvpChart.setData(combinedData);
        bvpChart.getDescription().setText("BVP Daten");
        bvpChart.setVisibleXRangeMaximum(MAX_X_DATA);
        bvpChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        bvpChart.getAxisRight().setEnabled(false);
        bvpChart.getLegend().setEnabled(false);
        bvpChart.invalidate();

        combinedData = new CombinedData();
        combinedData.setData(new LineData(createLineDataSet(EDAData, "EDA")));
        extremes = maxYEntry(EDAData);
        combinedData.setData(new BarData(createBarDataSet(adjustEntries(pStressData, mStressData, extremes.x, extremes.y), "EDA Daten")));
        edaChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});
        edaChart.setData(combinedData);
        edaChart.getDescription().setText("EDA Daten");
        edaChart.setVisibleXRangeMaximum(MAX_X_DATA);
        edaChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        edaChart.getAxisRight().setEnabled(false);
        edaChart.getLegend().setEnabled(false);
        edaChart.invalidate();

        combinedData = new CombinedData();
        combinedData.setData(new LineData(createLineDataSet(tempData, "Temperature")));
        extremes = maxYEntry(tempData);
        combinedData.setData(new BarData(createBarDataSet(adjustEntries(pStressData, mStressData, extremes.x, extremes.y), "Temperatur Daten")));
        tempChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});
        tempChart.setData(combinedData);
        tempChart.getDescription().setText("Temparatur Daten");
        tempChart.setVisibleXRangeMaximum(MAX_X_DATA);
        tempChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        tempChart.getAxisRight().setEnabled(false);
        tempChart.getLegend().setEnabled(false);
        tempChart.invalidate();

        combinedData = new CombinedData();
        combinedData.setData(new LineData(createLineDataSet(IBIData, "IBI")));
        extremes = maxYEntry(IBIData);
        combinedData.setData(new BarData(createBarDataSet(adjustEntries(pStressData, mStressData, extremes.x, extremes.y), "IBI Daten")));
        ibiChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});
        ibiChart.setData(combinedData);
        ibiChart.getDescription().setText("IBI Daten");
        ibiChart.setVisibleXRangeMaximum(MAX_X_DATA);
        ibiChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        ibiChart.getAxisRight().setEnabled(false);
        ibiChart.getLegend().setEnabled(false);
        ibiChart.invalidate();

        //Charts synchronisieren
        bvpListener = new CoupleChartGestureListener(bvpChart);
        bvpListener.addDstChart(edaChart); bvpListener.addDstChart(tempChart);

        edaListener = new CoupleChartGestureListener(edaChart);
        edaListener.addDstChart(bvpChart); edaListener.addDstChart(tempChart);

        tempListener = new CoupleChartGestureListener(tempChart);
        tempListener.addDstChart(bvpChart); tempListener.addDstChart(edaChart);

        bvpChart.setOnChartGestureListener(bvpListener);
        edaChart.setOnChartGestureListener(edaListener);
        tempChart.setOnChartGestureListener(tempListener);

        ibiListener = new CoupleChartGestureListener(ibiChart);
        ibiListener.addDstChart(bvpChart); ibiListener.addDstChart(edaChart); ibiListener.addDstChart(tempChart);
        ibiChart.setOnChartGestureListener(ibiListener);

        bvpChip.setChecked(true);
        edaChip.setChecked(true);
        tempChip.setChecked(true);
        setChipListener();
    }

    private void setChipListener()
    {
        bvpChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bvpChip.isChecked())
                {
                    if(checkedChips() <= 3)
                    {
                        bvpChart.setVisibility(View.VISIBLE);
//                        if(edaChip.isChecked())
                            edaListener.addDstChart(bvpChart);
//                        if(tempChip.isChecked())
                            tempListener.addDstChart(bvpChart);
//                        if(ibiChip.isChecked())
                            ibiListener.addDstChart(bvpChart);
                    } else {
                        bvpChip.setChecked(false);
                    }
                } else {
                    bvpChart.setVisibility(View.GONE);
//                    if(edaChip.isChecked())
                        edaListener.removeDstChart(bvpChart);
//                    if(tempChip.isChecked())
                        tempListener.removeDstChart(bvpChart);
//                    if(ibiChip.isChecked())
                        ibiListener.removeDstChart(bvpChart);
                }
            }
        });

        edaChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edaChip.isChecked())
                {
                    if(checkedChips() <= 3)
                    {
                        edaChart.setVisibility(View.VISIBLE);
//                        if(bvpChip.isChecked())
                            bvpListener.addDstChart(edaChart);
//                        if(tempChip.isChecked())
                            tempListener.addDstChart(edaChart);
//                        if(ibiChip.isChecked())
                            ibiListener.addDstChart(edaChart);
                    } else {
                        edaChip.setChecked(false);
                    }
                } else {
                    edaChart.setVisibility(View.GONE);
//                    if(bvpChip.isChecked())
                        bvpListener.removeDstChart(edaChart);
//                    if(tempChip.isChecked())
                        tempListener.removeDstChart(edaChart);
//                    if(ibiChip.isChecked())
                        ibiListener.removeDstChart(edaChart);
                }
            }
        });

        tempChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tempChip.isChecked())
                {
                    if(checkedChips() <= 3)
                    {
                        tempChart.setVisibility(View.VISIBLE);
//                        if(edaChip.isChecked())
                            edaListener.addDstChart(tempChart);
//                        if(bvpChip.isChecked())
                            bvpListener.addDstChart(tempChart);
//                        if(ibiChip.isChecked())
                            ibiListener.addDstChart(tempChart);
                    } else {
                        tempChip.setChecked(false);
                    }
                } else {
                    tempChart.setVisibility(View.GONE);
//                    if(edaChip.isChecked())
                        edaListener.removeDstChart(tempChart);
//                    if(bvpChip.isChecked())
                        bvpListener.removeDstChart(tempChart);
//                    if(ibiChip.isChecked())
                        ibiListener.removeDstChart(tempChart);
                }
            }
        });

        ibiChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ibiChip.isChecked())
                {
                    if(checkedChips() <= 3)
                    {
                        ibiChart.setVisibility(View.VISIBLE);
//                        if(edaChip.isChecked())
                            edaListener.addDstChart(ibiChart);
//                        if(tempChip.isChecked())
                            tempListener.addDstChart(ibiChart);
//                        if(bvpChip.isChecked())
                            bvpListener.addDstChart(ibiChart);
                    } else {
                        ibiChip.setChecked(false);
                    }
                } else {
                    ibiChart.setVisibility(View.GONE);
//                    if(edaChip.isChecked())
                        edaListener.removeDstChart(ibiChart);
//                    if(tempChip.isChecked())
                        tempListener.removeDstChart(ibiChart);
//                    if(bvpChip.isChecked())
                        bvpListener.removeDstChart(ibiChart);
                }
            }
        });
    }

    private ArrayList<BarEntry> adjustEntries(ArrayList<BarEntry> pe, ArrayList<BarEntry> me, float max, float min)
    {
        ArrayList<BarEntry> adj = new ArrayList<BarEntry>();
        float mult = (max-min)/5; //Stressangabe von 1-5
        for(int it = 0; it < pe.size(); it++)
        {
            BarEntry ine = new BarEntry(pe.get(it).getX(), pe.get(it).getY());
            ine.setIcon(new TextDrawable("P"+(int)ine.getY()));
            ine.setY(ine.getY()*mult+min);
            adj.add(ine);
        }

        int j = 0;
        int pos = 0;
        for(int it = 0; it < me.size(); it++)
        {
            BarEntry ine = new BarEntry(me.get(it).getX(), me.get(it).getY());
            ine.setIcon(new TextDrawable("M"+(int)ine.getY()));
            ine.setY(ine.getY()*mult+min);
            while(pe.get(j).getX() <= ine.getX())
            {
                if(j < pe.size()-1)
                    j++;
                pos++;
            }
            adj.add(pos,ine);
            pos++;
        }
        return adj;
    }

    private PointF maxYEntry(ArrayList<Entry> in)
    {
        float max = 0;
        float min = Float.MAX_VALUE;
        for(int it = 0; it < in.size(); it++)
        {
            float y = in.get(it).getY();
            if (y > max)
                max = y;
            if (y < min)
                min = y;
        }
        if(min < 0)
            min = 0;
        return new PointF(max, min);
    }

    private int checkedChips()
    {
        int res = 0;
        if(bvpChip.isChecked())
            res++;
        if(edaChip.isChecked())
            res++;
        if(tempChip.isChecked())
            res++;
        if(ibiChip.isChecked())
            res++;

        return res;
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
                            case 6: pStressData.add(new BarEntry(stamp, (float)Integer.valueOf(line[i]))); break;
                            case 7: mStressData.add(new BarEntry(stamp, (float)Integer.valueOf(line[i]))); break;
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

    private LineDataSet createLineDataSet(List<Entry> entries, String name)
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

    private BarDataSet createBarDataSet(List<BarEntry> entries, String name)
    {
        BarDataSet set = new BarDataSet(entries, name);
        set.setColor(Color.rgb(183,28,28));
        set.setDrawValues(false);
        set.setDrawIcons(true);
        set.setBarBorderWidth(0.75f);
        return set;
    }
}
