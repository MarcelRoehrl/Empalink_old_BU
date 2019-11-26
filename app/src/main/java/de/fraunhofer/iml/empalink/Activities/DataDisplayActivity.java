package de.fraunhofer.iml.empalink.Activities;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.chip.Chip;
import com.opencsv.CSVReader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.fraunhofer.iml.empalink.CoupleChartGestureListener;
import de.fraunhofer.iml.empalink.R;
import de.fraunhofer.iml.empalink.TextDrawable;
import de.fraunhofer.iml.empalink.V;

public class DataDisplayActivity extends AppCompatActivity
{
    protected CombinedChart edaChart, tempChart, ibiChart, bvpChart, accChart;
    protected Chip bvpChip, edaChip, ibiChip, tempChip, accChip;
    private String filePath;
    private float highest_x_value = 0;

    private ArrayList<Entry> accData, tempData, BVPData, EDAData, IBIData;
    private ArrayList<BarEntry> pStressData, mStressData;
    private CoupleChartGestureListener bvpListener, edaListener, tempListener, ibiListener, accListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        bvpChart = findViewById(R.id.BVP);
        edaChart = findViewById(R.id.EDA);
        tempChart= findViewById(R.id.Temperature);
        ibiChart = findViewById(R.id.IBI);
        accChart = findViewById(R.id.Acceleration);
        bvpChip = findViewById(R.id.BVP_chip);
        edaChip = findViewById(R.id.EDA_chip);
        ibiChip = findViewById(R.id.IBI_chip);
        tempChip = findViewById(R.id.Temp_chip);
        accChip = findViewById(R.id.Acc_chip);

        accData = new ArrayList<Entry>();
        BVPData = new ArrayList<Entry>();
        EDAData = new ArrayList<Entry>();
        IBIData = new ArrayList<Entry>();
        tempData = new ArrayList<Entry>();
        accData = new ArrayList<Entry>();
        pStressData = new ArrayList<BarEntry>();
        mStressData = new ArrayList<BarEntry>();

        filePath = getApplicationContext().getResources().getString(R.string.path)+ File.separator + getIntent().getStringExtra(V.FILENAME_EXTRA);

        load();

        CombinedData combinedData = new CombinedData();
        combinedData.setData(new LineData(createLineDataSet(BVPData, "BVP")));
        PointF extremes = getExtremes(BVPData);
        combinedData.setData(new BarData(createBarDataSet(adjustEntries(pStressData, mStressData, extremes.x, extremes.y), "mentaler Stress")));
        bvpChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});
        bvpChart.setData(combinedData);
        bvpChart.getXAxis().setAxisMaximum(highest_x_value);
        bvpChart.getXAxis().setAxisMinimum(0);
        bvpChart.moveViewToX(0);
        bvpChart.getDescription().setText("BVP Daten");
        bvpChart.setVisibleXRangeMaximum(V.MAX_X_DATA);
        bvpChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        bvpChart.getAxisRight().setEnabled(false);
        bvpChart.getLegend().setEnabled(false);
        bvpChart.setKeepPositionOnRotation(true);
        bvpChart.invalidate();

        combinedData = new CombinedData();
        combinedData.setData(new LineData(createLineDataSet(EDAData, "EDA")));
        extremes = getExtremes(EDAData);
        combinedData.setData(new BarData(createBarDataSet(adjustEntries(pStressData, mStressData, extremes.x, extremes.y), "EDA Daten")));
        edaChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});
        edaChart.setData(combinedData);
        edaChart.getXAxis().setAxisMaximum(highest_x_value);
        edaChart.getXAxis().setAxisMinimum(0);
        edaChart.moveViewToX(0);
        edaChart.getDescription().setText("EDA Daten - μS");
        edaChart.setVisibleXRangeMaximum(V.MAX_X_DATA);
        edaChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        edaChart.getAxisRight().setEnabled(false);
        edaChart.getLegend().setEnabled(false);
        edaChart.setKeepPositionOnRotation(true);
        edaChart.invalidate();

        combinedData = new CombinedData();
        combinedData.setData(new LineData(createLineDataSet(tempData, "Temperature")));
        extremes = getExtremes(tempData);
        combinedData.setData(new BarData(createBarDataSet(adjustEntries(pStressData, mStressData, extremes.x, extremes.y), "Temperatur Daten")));
        tempChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});
        tempChart.setData(combinedData);
        tempChart.getXAxis().setAxisMaximum(highest_x_value);
        tempChart.getXAxis().setAxisMinimum(0);
        tempChart.moveViewToX(0);
        tempChart.getDescription().setText("Temparatur Daten - °C");
        tempChart.setVisibleXRangeMaximum(V.MAX_X_DATA);
        tempChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        tempChart.getAxisRight().setEnabled(false);
        tempChart.getLegend().setEnabled(false);
        tempChart.setKeepPositionOnRotation(true);
        tempChart.invalidate();

        combinedData = new CombinedData();
        combinedData.setData(new LineData(createLineDataSet(IBIData, "IBI")));
        extremes = getExtremes(IBIData);
        combinedData.setData(new BarData(createBarDataSet(adjustEntries(pStressData, mStressData, extremes.x, extremes.y), "IBI Daten")));
        ibiChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});
        ibiChart.setData(combinedData);
        ibiChart.getXAxis().setAxisMaximum(highest_x_value);
        ibiChart.getXAxis().setAxisMinimum(0);
        ibiChart.moveViewToX(0);
        ibiChart.getDescription().setText("IBI Daten");
        ibiChart.setVisibleXRangeMaximum(V.MAX_X_DATA);
        ibiChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        ibiChart.getAxisRight().setEnabled(false);
        ibiChart.getLegend().setEnabled(false);
        ibiChart.setKeepPositionOnRotation(true);
        ibiChart.invalidate();

        combinedData = new CombinedData();
        combinedData.setData(new LineData(createLineDataSet(accData, "Acceleration")));
        extremes = getExtremes(accData);
        combinedData.setData(new BarData(createBarDataSet(adjustEntries(pStressData, mStressData, extremes.x, extremes.y), "Beschleunigung in G")));
        accChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});
        accChart.setData(combinedData);
        accChart.getXAxis().setAxisMaximum(highest_x_value);
        accChart.getXAxis().setAxisMinimum(0);
        accChart.moveViewToX(0);
        accChart.getDescription().setText("Beschleunigung - g");
        accChart.setVisibleXRangeMaximum(V.MAX_X_DATA);
        accChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        accChart.getAxisRight().setEnabled(false);
        accChart.getLegend().setEnabled(false);
        accChart.setKeepPositionOnRotation(true);
        accChart.invalidate();

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
        ibiListener.addDstChart(bvpChart); ibiListener.addDstChart(edaChart); ibiListener.addDstChart(tempChart); //die drei beim start angezeigt werden
        ibiChart.setOnChartGestureListener(ibiListener);

        accListener = new CoupleChartGestureListener(accChart);
        accListener.addDstChart(bvpChart); accListener.addDstChart(edaChart); accListener.addDstChart(tempChart); //die drei beim start angezeigt werden
        accChart.setOnChartGestureListener(accListener);

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

                    float x = bvpChart.getX();
                    if(checkedChips() <= V.MAX_GRAPHS)
                    {
                        bvpChart.setVisibility(View.VISIBLE);

                        edaListener.addDstChart(bvpChart);
                        tempListener.addDstChart(bvpChart);
                        ibiListener.addDstChart(bvpChart);
                        accListener.addDstChart(bvpChart);

                        syncCharts(bvpChip);
                    } else {
                        bvpChip.setChecked(false);
                    }
                } else {
                    bvpChart.setVisibility(View.GONE);

                    edaListener.removeDstChart(bvpChart);
                    tempListener.removeDstChart(bvpChart);
                    ibiListener.removeDstChart(bvpChart);
                    accListener.removeDstChart(bvpChart);
                }
            }
        });

        edaChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edaChip.isChecked())
                {
                    if(checkedChips() <= V.MAX_GRAPHS)
                    {
                        edaChart.setVisibility(View.VISIBLE);

                        bvpListener.addDstChart(edaChart);
                        tempListener.addDstChart(edaChart);
                        ibiListener.addDstChart(edaChart);
                        accListener.addDstChart(edaChart);

                        syncCharts(edaChip);
                    } else {
                        edaChip.setChecked(false);
                    }
                } else {
                    edaChart.setVisibility(View.GONE);

                    bvpListener.removeDstChart(edaChart);
                    tempListener.removeDstChart(edaChart);
                    ibiListener.removeDstChart(edaChart);
                    accListener.removeDstChart(edaChart);
                }
            }
        });

        tempChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tempChip.isChecked())
                {
                    if(checkedChips() <= V.MAX_GRAPHS)
                    {
                        tempChart.setVisibility(View.VISIBLE);

                        edaListener.addDstChart(tempChart);
                        bvpListener.addDstChart(tempChart);
                        ibiListener.addDstChart(tempChart);
                        accListener.addDstChart(tempChart);

                        syncCharts(tempChip);
                    } else {
                        tempChip.setChecked(false);
                    }
                } else {
                    tempChart.setVisibility(View.GONE);

                    edaListener.removeDstChart(tempChart);
                    bvpListener.removeDstChart(tempChart);
                    ibiListener.removeDstChart(tempChart);
                    accListener.removeDstChart(tempChart);
                }
            }
        });

        ibiChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ibiChip.isChecked())
                {
                    if(checkedChips() <= V.MAX_GRAPHS)
                    {
                        ibiChart.setVisibility(View.VISIBLE);

                        edaListener.addDstChart(ibiChart);
                        tempListener.addDstChart(ibiChart);
                        bvpListener.addDstChart(ibiChart);
                        accListener.addDstChart(ibiChart);

                        syncCharts(ibiChip);
                    } else {
                        ibiChip.setChecked(false);
                    }
                } else {
                    ibiChart.setVisibility(View.GONE);

                    edaListener.removeDstChart(ibiChart);
                    tempListener.removeDstChart(ibiChart);
                    bvpListener.removeDstChart(ibiChart);
                    accListener.removeDstChart(ibiChart);
                }
            }
        });

        accChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(accChip.isChecked())
                {
                    if(checkedChips() <= V.MAX_GRAPHS)
                    {
                        accChart.setVisibility(View.VISIBLE);

                        edaListener.addDstChart(accChart);
                        tempListener.addDstChart(accChart);
                        bvpListener.addDstChart(accChart);
                        ibiListener.addDstChart(accChart);

                        syncCharts(accChip);
                    } else {
                        accChip.setChecked(false);
                    }
                } else {
                    accChart.setVisibility(View.GONE);

                    edaListener.removeDstChart(accChart);
                    tempListener.removeDstChart(accChart);
                    bvpListener.removeDstChart(accChart);
                    ibiListener.removeDstChart(accChart);
                }
            }
        });
    }

    private void syncCharts(Chip exclude)
    {
        if(bvpChip.isChecked() && bvpChip != exclude)
            bvpListener.syncCharts();
        else if(edaChip.isChecked() && edaChip != exclude)
            edaListener.syncCharts();
        else if(tempChip.isChecked() && tempChip != exclude)
            tempListener.syncCharts();
        else if(ibiChip.isChecked() && ibiChip != exclude)
            ibiListener.syncCharts();
        else if(accChip.isChecked() && accChip != exclude)
            accListener.syncCharts();
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
                if(pos < adj.size())
                    pos++;
                if(j < pe.size()-1)
                    j++;
                else
                    break;
            }
            adj.add(pos,ine);
            pos++;
        }
        return adj;
    }

    private PointF getExtremes(ArrayList<Entry> in)
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
        if(accChip.isChecked())
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
                                float x = Float.valueOf(temp[0]);
                                float y = Float.valueOf(temp[1]);
                                float z = Float.valueOf(temp[2]);
                                accData.add(new Entry(stamp, (float)(Math.sqrt(x * x + y * y + z * z)/64)));
                            } break;
                            case 6: pStressData.add(new BarEntry(stamp, (float)Integer.valueOf(line[i]))); break;
                            case 7: mStressData.add(new BarEntry(stamp, (float)Integer.valueOf(line[i]))); break;
                        }
                    }
                }
                it.hasNext();
            }

            //Daten anpassen um alle Graphen bei 0 bis zum höchsten X Wert darzustellen
            highest_x_value = Math.max(Math.max(Math.max(Math.max(BVPData.get(BVPData.size()-1).getX(), EDAData.get(EDAData.size()-1).getX()), IBIData.get(IBIData.size()-1).getX()), tempData.get(tempData.size()-1).getX()), accData.get(accData.size()-1).getX());
//            BVPData.add(new Entry(max, BVPData.get(BVPData.size()-1).getY()));
//            EDAData.add(new Entry(max, EDAData.get(EDAData.size()-1).getY()));
//            IBIData.add(new Entry(max, IBIData.get(IBIData.size()-1).getY()));
//            tempData.add(new Entry(max, tempData.get(tempData.size()-1).getY()));
//            accData.add(new Entry(max, accData.get(accData.size()-1).getY()));
//
//            if( BVPData.get(0).getX() > 0 )
//                BVPData.add(new Entry(0, BVPData.get(0).getY()));
//            if( EDAData.get(0).getX() > 0 )
//                EDAData.add(new Entry(0, EDAData.get(0).getY()));
//            if( IBIData.get(0).getX() > 0 )
//                IBIData.add(new Entry(0, IBIData.get(0).getY()));
//            if( tempData.get(0).getX() > 0 )
//                tempData.add(new Entry(0, tempData.get(0).getY()));
//            if( accData.get(0).getX() > 0 )
//                accData.add(new Entry(0, accData.get(0).getY()));
        }
        catch (Exception e)
        {}
    }

    private LineDataSet createLineDataSet(List<Entry> entries, String name)
    {
        LineDataSet set = new LineDataSet(entries, name);

        set.setColor(Color.rgb(34,134,195));
        set.setFillColor(Color.rgb(155,231,255));
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

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of item position
        outState.putBoolean("bvpC", bvpChip.isChecked());
        outState.putBoolean("edaC", edaChip.isChecked());
        outState.putBoolean("tempC", tempChip.isChecked());
        outState.putBoolean("ibiC", ibiChip.isChecked());
        outState.putBoolean("accC", accChip.isChecked());

        CombinedChart chart;
        if(edaChip.isChecked())
            chart = edaChart;
        else if(tempChip.isChecked())
            chart = tempChart;
        else if(ibiChip.isChecked())
            chart = ibiChart;
        else if(accChip.isChecked())
            chart = accChart;
        else
            chart = bvpChart;

        outState.putFloat("x", Math.abs(chart.getViewPortHandler().getTransX()/chart.getViewPortHandler().contentWidth()*V.MAX_X_DATA));
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Read the state of item position
        boolean bvpC = savedInstanceState.getBoolean("bvpC");
        boolean edaC = savedInstanceState.getBoolean("edaC");
        boolean tempC = savedInstanceState.getBoolean("tempC");
        boolean ibiC = savedInstanceState.getBoolean("ibiC");
        boolean accC = savedInstanceState.getBoolean("accC");

        float x = savedInstanceState.getFloat("x");
        float xs = savedInstanceState.getFloat("xs");
        float y = savedInstanceState.getFloat("y");
        float ys = savedInstanceState.getFloat("ys");

        if(!bvpC)
            bvpChip.callOnClick();
        else
            bvpChart.moveViewToX(x);
        if(!edaC)
            edaChip.callOnClick();
        else
            edaChart.moveViewToX(x);
        if(!tempC)
            tempChip.callOnClick();
        else
            tempChart.moveViewToX(x);
        if(ibiC) {
            ibiChip.callOnClick();
            ibiChart.moveViewToX(x);
        }
        if(accC) {
            accChip.callOnClick();
            accChart.moveViewToX(x);
        }
    }
}
