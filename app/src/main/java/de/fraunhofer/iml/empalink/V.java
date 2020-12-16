package de.fraunhofer.iml.empalink;

import java.util.ArrayList;

public final class V
{
    public static final int STRESS_RANGE = 100;
    public static final int MAX_X_DATA = 100; //Wieviele Sekunden sollen auf den Graphen maximal auf einmal anzeigbar sein
    public static final float INIT_ZOOM = 10f; //Um wieviel soll zu beginn hineingezoomt werden (siehe MAX_X_DATA / x)
    public static final int MAX_GRAPHS = 5; //Wieviele Graphen sollen gleichzeitig maximal anzeigbar sein
    public static final double MED_PULSE_RANGE = 10; //aus den letzten x sec soll der durchschnittliche Puls ermittelt werden
    public static final double MED_PULSE_OVERLAP = 2; //wieviele sec soll man nach vorne überlappen
    public static final double TOLERANCE_ADD_PEAK = 0.07; //Toleranzbereich (+-) um die peak timestamps in die Liste einzufügen (um keinen doppelt einzufügen)

    public static final double RISE_TOLERANCE = 0.1; //Toleranz im BVP Wertebereich um zwischen vallie und peak einen Extrempunkt zu entdecken
    public static final double TIMEFRAME_TOLERANCE = 0.25; //Zeit (in sek) die vallie und peak auseinanderliegen dürfen

    public static final String FILENAME_EXTRA = "filename";
    public static final int REQUEST_FILENAME = 5;

    public static final int BUFFER_SIZE = 64 * 1024; //Buffergröße beim reinschreiben in die CSV Datei

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

    /**
     * Verbessert die Auswahl der Peaktimes und nimmt Objekte aus der peaks Liste die keine echten peaks sind.
     * Guckt ob der peak direkt hinter einem vallie ohne Steigungsänderung zwischen den Punkten kommmt
     * @param peaks
     * @param vallies
     * @param bvp
     */
    static public void improvePeaks(ArrayList<Integer> peaks, ArrayList<Integer> vallies, double[] bvp)
    {
        for (int it = 0; it < peaks.size(); it++)
        {
            for(int j = 0; j < vallies.size(); j++)
            {
                if(vallies.get(j) < peaks.get(it))
                {
                    for( int k = j+1; k < vallies.size(); k++)
                    {
                        if(vallies.get(k) >= peaks.get(it))
                        {
                            j = k-1;
                            break;
                        }
                    }

                    if(EPbetween(vallies.get(j), peaks.get(it), bvp) && !inTimeFrame(vallies.get(j), peaks.get(it)))
                    {
                        peaks.remove(it);
                    }
                    else if(it+1 < peaks.size())
                        it++;
                    else
                        break;
                }
                else
                {
                    peaks.remove(it);
                    j--;
                }
            }
        }
    }

    static private boolean EPbetween(int from, int to, double[] bvp)
    {
        for(int it = from+1; it < to && it < bvp.length-1; it++)
        {
            if(bvp[it]-RISE_TOLERANCE >= bvp[it+1]) //Fehlertoleranz in der Messung
                return true;
        }
        return false;
    }

    static private boolean inTimeFrame(int from, int to)
    {
        int range = to - from;
        double inHZ = range/64;
        if(inHZ > TIMEFRAME_TOLERANCE)
            return false;
        else
            return true;
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
