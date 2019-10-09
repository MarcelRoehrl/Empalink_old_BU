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
import de.fraunhofer.iml.empalink.SensorObjects.Temperature;

public class Session
{
    private long starttime;
    private ArrayList<Acceleration> accData;
    private ArrayList<BVP> BVPData;
    private ArrayList<EDA> EDAData;
    private ArrayList<IBI> IBIData;
    private ArrayList<Temperature> tempData;

    private String filePath;

    public Session(long starttime, Context context)
    {
        this.starttime = starttime;

        accData = new ArrayList<Acceleration>(); 
        BVPData = new ArrayList<BVP>();
        EDAData = new ArrayList<EDA>();
        IBIData = new ArrayList<IBI>();
        tempData = new ArrayList<Temperature>();

        File internal_storage = new File(context.getResources().getString(R.string.path));
        internal_storage.mkdirs();

        filePath = context.getResources().getString(R.string.path)+ File.separator + new Date(starttime).toString() + ".csv";
    }

    public void save()
    {
        File file = new File(filePath);

        CSVWriter writer;

        try{
            if (!file.exists()) {
                file.createNewFile();
            }

            writer = new CSVWriter(new FileWriter(filePath), ',');
            //String[] entries = "first#second#third".split("#"); // array of your values
            for(int it = 0; it < BVPData.size(); it++)
            {
                String[] data = {""+BVPData.get(it).bvp, ""+BVPData.get(it).timestamp};
                writer.writeNext(data);
            }
            writer.close();
        } catch (IOException e) {
        e.printStackTrace();
}
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
}
