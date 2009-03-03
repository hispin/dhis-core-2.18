package org.hisp.dhis.integration.rims.api.tables;

public class RIMSDistrict
    extends RIMSOrgUnit
{
    private String district_code;
    private String district_name;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    public RIMSDistrict()
    {

    }

    public RIMSDistrict( String district_code, String district_name,
        String state_code )
    {
        this.district_code = district_code;
        this.state_code = state_code;
        this.district_name = district_name;
    }

    // ------------------------------------------------------------------------
    // HashCode and equals
    // ------------------------------------------------------------------------
    @Override
    public int hashCode()
    {
        return district_code.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof RIMSDistrict) )
        {
            return false;
        }

        final RIMSDistrict other = (RIMSDistrict) o;

        return district_code.equals( other.getDistrict_code() );
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
    public String getDistrict_code()
    {
        return district_code;
    }

    public void setDistrict_code( String district_code )
    {
        this.district_code = district_code;
    }

    public String getDistrict_name()
    {
        return district_name;
    }

    public void setDistrict_name( String district_name )
    {
        this.district_name = district_name;
    }

    @Override
    public String getName()
    {
        return getDistrict_name();
    }

    @Override
    public String getCode()
    {
        return getDistrict_code();
    }

}
