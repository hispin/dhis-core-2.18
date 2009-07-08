package org.hisp.dhis.den.api;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;

public class LLDataValue implements Serializable
{
    /**
     * Part of the DataValue's composite ID
     */
    private DataElement dataElement;

    /**
     * Part of the DataValue's composite ID
     */
    private Period period;

    /**
     * Part of the DataValue's composite ID
     */
    private Source source;

    private String value;

    private String storedBy;

    private Date timestamp;

    private String comment;
    
    private DataElementCategoryOptionCombo optionCombo;
    
    private Integer recordNo;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public LLDataValue()
    {
    }

    public LLDataValue( DataElement dataElement, Period period, Source source )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
    }

    public LLDataValue( DataElement dataElement, Period period, Source source, DataElementCategoryOptionCombo optionCombo )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.optionCombo = optionCombo;
    }

    public LLDataValue( DataElement dataElement, Period period, Source source, String value )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.value = value;
    }

    public LLDataValue( DataElement dataElement, Period period, Source source, String value, DataElementCategoryOptionCombo optionCombo )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.value = value;
        this.optionCombo = optionCombo;
    }

    public LLDataValue( DataElement dataElement, Period period, Source source, String value, String storedBy,
        Date timestamp, String comment )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.value = value;
        this.storedBy = storedBy;
        this.timestamp = timestamp;
        this.comment = comment;
    }
    
    public LLDataValue( DataElement dataElement, Period period, Source source, String value, String storedBy,
            Date timestamp, String comment, DataElementCategoryOptionCombo optionCombo, Integer recordNo )
    {
            this.dataElement = dataElement;
            this.period = period;
            this.source = source;
            this.value = value;
            this.storedBy = storedBy;
            this.timestamp = timestamp;
            this.comment = comment;
            this.optionCombo = optionCombo;
            this.recordNo = recordNo;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

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

        if ( !(o instanceof DataValue) )
        {
            return false;
        }

        final DataValue other = (DataValue) o;

        return dataElement.equals( other.getDataElement() ) && optionCombo.equals( other.getOptionCombo() ) 
            && period.equals( other.getPeriod() ) && source.equals( other.getSource() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + optionCombo.hashCode();
        result = result * prime + period.hashCode();
        result = result * prime + dataElement.hashCode();
        result = result * prime + source.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
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

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
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

    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }
    
    public DataElementCategoryOptionCombo getOptionCombo()
    {
        return optionCombo;
    }

    public void setOptionCombo( DataElementCategoryOptionCombo optionCombo )
    {
        this.optionCombo = optionCombo;
    }

    public Integer getRecordNo()
    {
        return recordNo;
    }

    public void setRecordNo( Integer recordNo )
    {
        this.recordNo = recordNo;
    }



}
