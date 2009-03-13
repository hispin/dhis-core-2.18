package org.hisp.dhis.sandbox.mobileimport.api;

import java.io.Serializable;
import java.util.Map;

public class MobileImportParameters implements Serializable
{

    private String mobileNumber;
    
    private String startDate;
    
    private String smsTime;
    
    private Map<Integer, Integer> dataValues;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public MobileImportParameters()
    {
        
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getMobileNumber()
    {
        return mobileNumber;
    }

    public void setMobileNumber( String mobileNumber )
    {
        this.mobileNumber = mobileNumber;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public String getSmsTime()
    {
        return smsTime;
    }

    public void setSmsTime( String smsTime )
    {
        this.smsTime = smsTime;
    }

    public Map<Integer, Integer> getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( Map<Integer, Integer> dataValues )
    {
        this.dataValues = dataValues;
    }

    
}
