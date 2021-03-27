package de.fraunhofer.iml.empalink;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Session
{
    private long starttime;
    private long startStamp = -1;
    private long polarStart = -1;
    private long polarAdd = 0;

    private FileWriter writer;
    private BufferedWriter bufferedWriter;

    private String filePath;

    public boolean recording = false;

    public void startWriter(long starttime, Context context)
    {
        this.starttime = starttime;

        File internal_storage = new File(context.getResources().getString(R.string.path));
        internal_storage.mkdirs();
        filePath = context.getResources().getString(R.string.path)+ File.separator + new Date(starttime).toString() + ".csv";

        try {
            writer = new FileWriter(filePath, true);
            bufferedWriter = new BufferedWriter(writer, V.BUFFER_SIZE);
            bufferedWriter.write("timestamp,BVP,EDA,IBI,temperature,acceleration,physical stress,mental stress,markers,surveys,Polar PPG,Polar PPI,Polar ACC,Polar ECG,"+starttime);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recording = true;
    }

    public void closeWriter()
    {
        try {
            bufferedWriter.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine(double timestamp, String line, boolean empatica)
    {
        if(startStamp == -1 || polarStart == -1) {
            if(empatica)
                startStamp = (long) (timestamp * 100000);
            else
                polarStart = (long)timestamp;
        }
        try {
            if(empatica)
                bufferedWriter.write((double)((long)(timestamp*100000)-startStamp)/100000+","+line);
            else
                bufferedWriter.write((double)((long)(timestamp-polarStart))/1000000000+","+line);

            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine(String line)
    {
        try {
            bufferedWriter.write(getCurrentTimestamp()+","+line);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addAcc(int x, int y, int z, double timestamp)
    {
        writeLine(timestamp,",,,,"+x+";"+y+";"+z+",,,,,,,,", true);
    }

    public void addBVP(float bvp, double timestamp)
    {
        writeLine(timestamp,bvp+",,,,,,,,,,,,", true);
    }

    public void addEDA(float gsr, double timestamp)
    {
        writeLine(timestamp,","+gsr+",,,,,,,,,,,", true);
    }

    public void addIBI(float ibi, double timestamp)
    {
        writeLine(timestamp,",,"+ibi+",,,,,,,,,,", true);
    }

    public void addTemp(float temp, double timestamp)
    {
        writeLine(timestamp,",,,"+temp+",,,,,,,,,", true);
    }

    public void addSurvey(String survey)
    {
        writeLine(",,,,,,,,"+survey+",,,,");
    }

    public void addPStress(int stress)
    {
        writeLine(",,,,,"+stress+",,,,,,,");
    }

    public void addMStress(int stress)
    {
        writeLine(",,,,,,"+stress+",,,,,,");
    }

    public void addMarker()
    {
        writeLine(",,,,,,,X,,,,,");
    }

    public void addPolarPPG(String ppg, double timestamp)
    {
        writeLine(timestamp, ",,,,,,,,,"+ppg+",,,", false);
    }

    public void addPolarPPI(String ppi)
    {
        writeLine(",,,,,,,,,,"+ppi+",,");
    }

    public void addPolarACC(String acc, double timestamp)
    {
        writeLine(timestamp, ",,,,,,,,,,,"+acc+",", false);
    }

    public void addPolarECG(String ecg, double timestamp)
    {
        writeLine(timestamp, ",,,,,,,,,,,,"+ecg, false);
    }


    public Double getCurrentTimestamp()
    {
        return ((double)(System.currentTimeMillis()-starttime))/1000;
    }
}
