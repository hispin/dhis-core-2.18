package org.hisp.dhis.mapgeneration;

public class MapValue
{
    private String ou;
    
    private double value;

    public MapValue( String ou, double value )
    {
        this.ou = ou;
        this.value = value;
    }

    public String getOu()
    {
        return ou;
    }

    public void setOu( String ou )
    {
        this.ou = ou;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue( double value )
    {
        this.value = value;
    }
}
