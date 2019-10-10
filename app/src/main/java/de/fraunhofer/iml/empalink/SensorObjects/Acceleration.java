package de.fraunhofer.iml.empalink.SensorObjects;

public class Acceleration
{
    public int x,y,z;
    public double timestamp;

    public Acceleration(int x, int y, int z, double timestamp)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;
    }
}
