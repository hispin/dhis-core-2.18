package org.hisp.dhis.integration.rims.api.tables;

public class RIMS_PHC
    extends RIMSOrgUnit
{

    private String phc_code;
    private String district_code;
    private String phc_name;
    private String district_name;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    public RIMS_PHC()
    {

    }

    public RIMS_PHC( String phc_code, String district_code, String state_code,
        String phc_name, String district_name )
    {
        this.phc_code = phc_code;
        this.district_code = district_code;
        this.state_code = state_code;
        this.phc_name = phc_name;
        this.district_name = district_name;
    }

    // ------------------------------------------------------------------------
    // HashCode and equals
    // ------------------------------------------------------------------------
    @Override
    public int hashCode()
    {
        return phc_code.hashCode();
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

        if ( !(o instanceof RIMS_PHC) )
        {
            return false;
        }

        final RIMS_PHC other = (RIMS_PHC) o;

        return phc_code.equals( other.getPhc_code() );
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
    public String getPhc_code()
    {
        return phc_code;
    }

    public void setPhc_code( String phc_code )
    {
        this.phc_code = phc_code;
    }

    public String getDistrict_code()
    {
        return district_code;
    }

    public void setDistrict_code( String district_code )
    {
        this.district_code = district_code;
    }

    public String getPhc_name()
    {
        return phc_name;
    }

    public void setPhc_name( String phc_name )
    {
        this.phc_name = phc_name;
    }

    public String getDistrict_name()
    {
        return district_name;
    }

    public void setDistrict_name( String district_name )
    {
        this.district_name = district_name;
    }

    public String getName()
    {
        return getPhc_name();
    }

    @Override
    public String getCode()
    {
        return getPhc_code();
    }
}
