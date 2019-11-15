package de.fraunhofer.iml.empalink;

import android.content.Context;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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

        File internal_storage = new File(context.getResources().getString(R.string.path));
        internal_storage.mkdirs();

        filePath = context.getResources().getString(R.string.path)+ File.separator + new Date(starttime).toString() + ".csv";
    }

    public void save()
    {
//        BVPData.add(new BVP(100,10));
//        BVPData.add(new BVP(1020,11));
//        BVPData.add(new BVP(1030,12));
//        BVPData.add(new BVP(1040,13));
//        BVPData.add(new BVP(1050,14));
//        BVPData.add(new BVP(1060,15));
//
//        EDAData.add(new EDA(20,11));
//        EDAData.add(new EDA(30,12));
//        EDAData.add(new EDA(40,13));
//        EDAData.add(new EDA(50,14));
//        EDAData.add(new EDA(60,15));
//
//        IBIData.add(new IBI(100,9));
//        IBIData.add(new IBI(200,13));
//        IBIData.add(new IBI(300,15));

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

            String[] header = {"timestamp", "BVP", "EDA", "IBI", "temperature", "acceleration", "physical stress", "mental stress"};
            writer.writeNext(header);

//            String[] firstline = {"0", ""+BVPData.get(0), ""+EDAData.get(0), ""+IBIData.get(0), ""+tempData.get(0), ""+accData.get(0), "", ""};//um die Anzeige überall bei 0 beginnen zu lassen TODO Sonderfall das eine Liste leer ist
//            writer.writeNext(firstline);

            String[] data = new String[8];
            int b = 0, e = 0, i = 0, t = 0, a = 0, p = 0, m = 0;
            double bTime = Double.MAX_VALUE, eTime = Double.MAX_VALUE, iTime = Double.MAX_VALUE, tTime = Double.MAX_VALUE, aTime = Double.MAX_VALUE, pTime = Double.MAX_VALUE, mTime = Double.MAX_VALUE;

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

            double temp = Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(bTime, eTime), iTime), tTime),aTime), pTime), mTime); //TODO evtl effizienter machen
            long startStamp = (long)(temp*100000); //Das ganze hier gemacht damit in der Schleife eine Abfrage weniger ist

            double curStamp;
            while(!(b == -1 && e == -1 && i == -1 && t == -1 && a == -1 && p == -1 && m == -1))
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

                curStamp =  Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(bTime, eTime), iTime), tTime),aTime), pTime), mTime); //TODO evtl effizienter machen

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
                    data[5] = ""+accData.get(a).x+","+accData.get(a).y+","+accData.get(a).z;//TODO evtl. effizienter mit get(a) rausziehen
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

    public Double getLatestTimestamp()
    {
        return accData.get(accData.size()-1).timestamp; //TODO evtl schöner lösen
    }
}
