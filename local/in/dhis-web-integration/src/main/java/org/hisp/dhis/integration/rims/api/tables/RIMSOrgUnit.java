package org.hisp.dhis.integration.rims.api.tables;

public abstract class RIMSOrgUnit
{
    protected String state_code;

    public RIMSOrgUnit()
    {
        super();
    }

    public String getState_code()
    {
        return state_code;
    }

    public void setState_code( String state_code )
    {
        this.state_code = state_code;
    }

    public abstract String getName(); 
    public abstract String getCode();
}