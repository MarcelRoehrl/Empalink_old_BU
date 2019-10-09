package de.fraunhofer.iml.empalink;

import android.content.Context;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
    private ArrayList<Stress> stressData;

    private String filePath;

    public Session(long starttime, Context context)
    {
        this.starttime = starttime;

        accData = new ArrayList<Acceleration>();
        BVPData = new ArrayList<BVP>();
        EDAData = new ArrayList<EDA>();
        IBIData = new ArrayList<IBI>();
        tempData = new ArrayList<Temperature>();
        stressData = new ArrayList<Stress>();

        File internal_storage = new File(context.getResources().getString(R.string.path));
        internal_storage.mkdirs();

        filePath = context.getResources().getString(R.string.path)+ File.separator + new Date(starttime).toString() + ".csv";
    }

    public void save()
    {
        File file = new File(filePath);

        CSVWriter csvWriter;

        try{
            Writer writer =  Files.newBufferedWriter(Paths.get(filePath));

            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();

            try {
                beanToCsv.write(BVPData);
            } catch (CsvDataTypeMismatchException e) {
                e.printStackTrace();
            } catch (CsvRequiredFieldEmptyException e) {
                e.printStackTrace();
            }
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

    public void addStress(int stress, double timestamp)
    {
        stressData.add(new Stress(stress, timestamp));
    }
}
