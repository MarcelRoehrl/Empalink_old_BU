package de.fraunhofer.iml.empalink;

import java.util.ArrayList;

public final class V
{
    public static final int MAX_X_DATA = 10; //Wieviele Sekunden sollen auf den Graphen maximal auf einmal anzeigbar sein
    public static final int MAX_GRAPHS = 5; //Wieviele Graphen sollen gleichzeitig maximal anzeigbar sein
    public static final double MED_PULSE_RANGE = 10; //aus den letzten x sec soll der durchschnittliche Puls ermittelt werden
    public static final double MED_PULSE_OVERLAP = 0; //wieviele sec soll man nach vorne überlappen

    public static final String FILENAME_EXTRA = "filename";
    public static final int REQUEST_FILENAME = 5;

    static public float calcNormedAcc(float x, float y, float z)
    {
        float inG = (float)(Math.sqrt(x * x + y * y + z * z)/64);
        if(inG >= 1)
            return inG-1;
        else
            return 1-inG;
    }

    static public double[] calcPulse(double[] peak_times)
    {
        double[] pulse_values = new double[peak_times.length-1];
        for(int it = 0; it < peak_times.length-1; it++)
        {
            pulse_values[it] = peak_times[it+1]-peak_times[it];
        }
        return pulse_values;
    }

    static public double calcMedPulse(double[] pulse_values)
    {
        double sum = 0;
        for(int it = 0; it < pulse_values.length; it++)
        {
            sum += pulse_values[it];
        }
        return 60/(sum / pulse_values.length);
    }
}
