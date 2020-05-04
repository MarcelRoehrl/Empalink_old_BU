package de.fraunhofer.iml.empalink;

import android.content.Context;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import de.fraunhofer.iml.empalink.AMPDAlgorithm.AMPDAlgo;
import de.fraunhofer.iml.empalink.SensorObjects.Acceleration;
import de.fraunhofer.iml.empalink.SensorObjects.BVP;
import de.fraunhofer.iml.empalink.SensorObjects.EDA;
import de.fraunhofer.iml.empalink.SensorObjects.IBI;
import de.fraunhofer.iml.empalink.SensorObjects.Stress;
import de.fraunhofer.iml.empalink.SensorObjects.Temperature;

public class Session
{
    private long starttime;
    private ArrayList<Acceleration> accData;
    private ArrayList<BVP> BVPData;
    private ArrayList<EDA> EDAData;
    private ArrayList<IBI> IBIData;
    private ArrayList<Temperature> tempData;
    private ArrayList<Stress> pStressData;
    private ArrayList<Stress> mStressData;
    private ArrayList<Double> BVP_peaks;
    private ArrayList<Double> markers;

    private String filePath;

    public Session(long starttime, Context context)
    {
        this.starttime = starttime;

        accData = new ArrayList<Acceleration>();
        BVPData = new ArrayList<BVP>();
        EDAData = new ArrayList<EDA>();
        IBIData = new ArrayList<IBI>();
        tempData = new ArrayList<Temperature>();
        pStressData = new ArrayList<Stress>();
        mStressData = new ArrayList<Stress>();
        BVP_peaks = new ArrayList<Double>();
        markers = new ArrayList<Double>();

        File internal_storage = new File(context.getResources().getString(R.string.path));
        internal_storage.mkdirs();

        filePath = context.getResources().getString(R.string.path)+ File.separator + new Date(starttime).toString() + ".csv";
    }

    public double getLatestPulse(double updated_pulse, boolean save)
    {
        double pulse = 0;

        LinkedList<BVP> data = new LinkedList<BVP>();
        int it = BVPData.size()-1;
        for(; it >= 0; it--)
        {
            BVP temp = BVPData.get(it);
            data.addFirst(temp);
            if(temp.timestamp <= updated_pulse)
                break;
        }
        double[] bvp = new double[data.size()];
        for(int j = 0; j < data.size(); j++)
        {
            bvp[j] = data.get(j).bvp;
        }

        AMPDAlgo algo = new AMPDAlgo(bvp);
        try {
//  Code um peaks zu verbessern
//            ArrayList<Integer> vallies = algo.ampdVallies();
//            double[] vallies_times = new double[vallies.size()];
//            for(int j = 0; j < vallies.size(); j++)
//            {
//                vallies_times[j] = data.get(vallies.get(j)).timestamp;
//            }

            ArrayList<Integer> peaks = algo.ampdPeaks();
//            V.improvePeaks(peaks, vallies, bvp);
            double[] peaks_times = new double[peaks.size()];
            for(int j = 0; j < peaks.size(); j++)
            {
                peaks_times[j] = data.get(peaks.get(j)).timestamp;
            }
            if(save)
                addPeaks(peaks_times);

            pulse = V.calcMedPulse(V.calcPulse(peaks_times));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pulse;
    }

    private void addPeaks(double[] peak_times)
    {
        for(int it = 0; it < peak_times.length; it++)
        {
            int k = BVP_peaks.size()-1-(int)Math.ceil(V.MED_PULSE_OVERLAP/0.4);
            if(k < 0)
                k = 0;
            boolean add = true;
            for(; k < BVP_peaks.size(); k++)
            {
                if(peak_times[it] >= BVP_peaks.get(k)-V.TOLERANCE_ADD_PEAK && peak_times[it] <= BVP_peaks.get(k)+V.TOLERANCE_ADD_PEAK) {
                    add = false;
                    break;
                }
            }
            if(add)
                BVP_peaks.add(peak_times[it]);
        }
    }

    public void save()
    {
        try {
            File f = new File(filePath);
            CSVWriter writer;

            // File exist
            if (f.exists() && !f.isDirectory()) {
                FileWriter mFileWriter = new FileWriter(filePath, true);
                writer = new CSVWriter(mFileWriter);
            } else {
                writer = new CSVWriter(new FileWriter(filePath));
            }

            String[] excel = {"sep=,"}; //Excel Befehl um das Trennzeichen festzulegen
            writer.writeNext(excel);

            String[] header = {"timestamp", "BVP", "EDA", "IBI", "temperature", "acceleration", "physical stress", "mental stress", "markers", "peak times"};
            writer.writeNext(header);

//            String[] firstline = {"0", ""+BVPData.get(0), ""+EDAData.get(0), ""+IBIData.get(0), ""+tempData.get(0), ""+accData.get(0), "", ""};//um die Anzeige überall bei 0 beginnen zu lassen TODO Sonderfall das eine Liste leer ist
//            writer.writeNext(firstline);

            String[] data = new String[10];
            int b = 0, e = 0, i = 0, t = 0, a = 0, p = 0, m = 0, mp = 0;
            double bTime = Double.MAX_VALUE, eTime = Double.MAX_VALUE, iTime = Double.MAX_VALUE, tTime = Double.MAX_VALUE, aTime = Double.MAX_VALUE, pTime = Double.MAX_VALUE, mTime = Double.MAX_VALUE, mpTime = Double.MAX_VALUE; //mp ^= markerPoint

            //Für den Sonderfall das eine Liste leer ist
            if(BVPData.size() == 0){
                b = -1;
            } else
                bTime = BVPData.get(0).timestamp;
            if(EDAData.size() == 0){
                e = -1;
            } else
                eTime = EDAData.get(0).timestamp;
            if(IBIData.size() == 0){
                i = -1;
            } else
                iTime = IBIData.get(0).timestamp;
            if(tempData.size() == 0){
                t = -1;
            } else
                tTime = tempData.get(0).timestamp;
            if(accData.size() == 0){
                a = -1;
            } else
                aTime = accData.get(0).timestamp;
            if(pStressData.size() == 0){
                p = -1;
            } else
                pTime = pStressData.get(0).timestamp;
            if(mStressData.size() == 0){
                m = -1;
            } else
                mTime = mStressData.get(0).timestamp;
            if(markers.size() == 0){
                mp = -1;
            } else
                mpTime = markers.get(0);

            double temp = Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(bTime, eTime), iTime), tTime),aTime), pTime), mTime), mpTime); //TODO evtl effizienter machen
            long startStamp = (long)(temp*100000); //Das ganze hier gemacht damit in der Schleife eine Abfrage weniger ist

            int peak_counter = 0;
            double curStamp;
            while(!(b == -1 && e == -1 && i == -1 && t == -1 && a == -1 && p == -1 && m == -1 && mp == -1))
            {
                if(b != -1)
                    bTime = BVPData.get(b).timestamp;
                if(e != -1)
                    eTime = EDAData.get(e).timestamp;
                if(i != -1)
                    iTime = IBIData.get(i).timestamp;
                if(t != -1)
                    tTime = tempData.get(t).timestamp;
                if(a != -1)
                    aTime = accData.get(a).timestamp;
                if(p != -1)
                    pTime = pStressData.get(p).timestamp;
                if(m != -1)
                    mTime = mStressData.get(m).timestamp;
                if(mp != -1)
                    mpTime = markers.get(mp);

                curStamp =  Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(bTime, eTime), iTime), tTime),aTime), pTime), mTime), mpTime); //TODO evtl effizienter machen

                long curTemp = (long)(curStamp*100000);
                double entry = (double)(curTemp-startStamp);
                data[0] = ""+(entry/100000);

                if(b != -1 && bTime == curStamp)
                {
                    data[1] = ""+BVPData.get(b).bvp;
                    b++;
                    if(b >= BVPData.size())
                    {
                        b = -1;
                        bTime = Double.MAX_VALUE;
                    }
                }
                else {
                    data[1] = "";
                }

                if(e != -1 && eTime == curStamp)
                {
                    data[2] = ""+EDAData.get(e).gsr;
                    e++;
                    if(e >= EDAData.size())
                    {
                        e = -1;
                        eTime = Double.MAX_VALUE;
                    }
                }
                else {
                    data[2] = "";
                }

                if(i != -1 && iTime == curStamp)
                {
                    data[3] = ""+IBIData.get(i).ibi;
                    i++;
                    if(i >= IBIData.size())
                    {
                        i = -1;
                        iTime = Double.MAX_VALUE;
                    }
                }
                else {
                    data[3] = "";
                }

                if(t != -1 && tTime == curStamp)
                {
                    data[4] = ""+tempData.get(t).temp;
                    t++;
                    if(t >= tempData.size())
                    {
                        t = -1;
                        tTime = Double.MAX_VALUE;
                    }
                }
                else {
                    data[4] = "";
                }

                if(a != -1 && aTime == curStamp)
                {
                    data[5] = ""+accData.get(a).x+";"+accData.get(a).y+";"+accData.get(a).z;//TODO evtl. effizienter mit get(a) rausziehen
                    a++;
                    if(a >= accData.size())
                    {
                        a = -1;
                        aTime = Double.MAX_VALUE;
                    }
                }
                else {
                    data[5] = "";
                }

                if(p != -1 && pTime == curStamp)
                {
                    data[6] = ""+ pStressData.get(p).stress;
                    p++;
                    if(p >= pStressData.size())
                    {
                        p = -1;
                        pTime = Double.MAX_VALUE;
                    }
                } else {
                    data[6] = null;
                }

                if(m != -1 && mTime == curStamp)
                {
                    data[7] = ""+ mStressData.get(m).stress;
                    m++;
                    if(m >= mStressData.size())
                    {
                        m = -1;
                        mTime = Double.MAX_VALUE;
                    }
                } else {
                    data[7] = null;
                }

                if(mp != -1 && mpTime == curStamp)
                {
                    data[8] = "X";
                    mp++;
                    if(mp >= markers.size())
                    {
                        mp = -1;
                        mpTime = Double.MAX_VALUE;
                    }
                } else {
                    data[8] = null;
                }

                if(peak_counter < BVP_peaks.size())
                {
                    long curPeak = (long)(BVP_peaks.get(peak_counter)*100000);
                    double entryPeak = (double)(curPeak-startStamp);
                    data[9] = ""+(entryPeak/100000);
                    peak_counter++;
                }
                else
                    data[9] = null;

                /*if(marker_counter < markers.size())
                {
                    long curMark = (long)(markers.get(marker_counter)*100000);
                    double entryMark = (double)(curMark-startStamp);
                    data[9] = ""+(entryMark/100000);
                    marker_counter++;
                }
                else
                    data[9] = null;*/

                writer.writeNext(data);
            }
            writer.close();
        }
        catch (IOException e){}
    }

    public void addAcc(int x, int y, int z, double timestamp)
    {
        accData.add(new Acceleration(x,y,z,timestamp));
    }

    public void addBVP(float bvp, double timestamp)
    {
        BVPData.add(new BVP(bvp, timestamp));
    }

    public void addEDA(float gsr, double timestamp)
    {
        EDAData.add(new EDA(gsr, timestamp));
    }

    public void addIBI(float ibi, double timestamp)
    {
        IBIData.add(new IBI(ibi, timestamp));
    }

    public void addTemp(float temp, double timestamp)
    {
        tempData.add(new Temperature(temp, timestamp));
    }

    public void addPStress(int stress, double timestamp)
    {
        pStressData.add(new Stress(stress, timestamp));
    }

    public void addMStress(int stress, double timestamp)
    {
        mStressData.add(new Stress(stress, timestamp));
    }

    public void addMarker()
    {
        markers.add(getLatestTimestamp());
    }

    public Double getLatestTimestamp()
    {
        if(accData.size() > 0)
         return accData.get(accData.size()-1).timestamp; //TODO evtl schöner lösen
        else
            return (double)(System.currentTimeMillis()-starttime)/1000; //TODO passt glaube ich nicht
    }
}
