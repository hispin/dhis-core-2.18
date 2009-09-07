package org.hisp.dhis.linelisting;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;

public class LineListDataValue
    implements Serializable
{

    /**
     * The unique and auto-generated record number for the LineListing Group / Program Value Table
     */
    private int recordNumber;

    /**
     * The period for which the values are to be saved for the specific  LineListing Group
     */
    private Period period;

    /**
     * The period for which the values are to be saved for the specific LineListing Group
     */
    /**
     * The organisation unit the values are saved for the specific LineListing Group
     */
    private Source source;

    private String storedBy;

    private Date timestamp;

    private Map<String, String> lineListValues;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public LineListDataValue()
    {
        // TODO Auto-generated constructor stub
    }

    public LineListDataValue (Period period, Source source,Map<String, String>  lineListValues)
    {
        this.period = period;
        this.source = source;
        this.lineListValues =lineListValues;
    }

    public LineListDataValue (Period period, Source source,Map<String, String>  lineListValues, String storedBy)
    {
        this.period = period;
        this.source = source;
        this.lineListValues =lineListValues;
        this.storedBy = storedBy;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public int getRecordNumber()
    {
        return recordNumber;
    }

    public void setRecordNumber( int recordNumber )
    {
        this.recordNumber = recordNumber;
    }

    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    public Source getSource()
    {
        return source;
    }

    public void setSource( Source source )
    {
        this.source = source;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public Map<String, String>  getLineListValues()
    {
        return lineListValues;
    }

    public void setLineListValues(Map<String, String>  lineListValues )
    {
        this.lineListValues = lineListValues;
    }

}