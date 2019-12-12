package de.fraunhofer.iml.empalink;

public final class V
{
    public static final int MAX_X_DATA = 10; //Wieviele Sekunden sollen auf den Graphen maximal auf einmal anzeigbar sein
    public static final int MAX_GRAPHS = 5; //Wieviele Graphen sollen gleichzeitig maximal anzeigbar sein

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
}
