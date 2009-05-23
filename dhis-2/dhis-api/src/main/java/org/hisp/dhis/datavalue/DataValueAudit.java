package org.hisp.dhis.datavalue;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;

/*
 * Copyright (c) 2004-2009, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @author Latifov Murodillo Abdusamadovich
 * @version $Id$
 */
public class DataValueAudit
    implements Serializable
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

    /**
     * Part of the DataValue's composite ID
     */
    private DataElementCategoryOptionCombo optionCombo;

    /**
     * Part of the DataValue's composite ID
     */
    private int rev;

    private String value;

    private String storedBy;

    private Date timestamp;

    private String comment;

    private String revisionType;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataValueAudit()
    {
    }

    public DataValueAudit( DataValue dataValue )
    {
        this.comment = dataValue.getComment();
        this.dataElement = dataValue.getDataElement();
        this.optionCombo = dataValue.getOptionCombo();
        this.period = dataValue.getPeriod();
        this.source = dataValue.getSource();
        this.storedBy = dataValue.getStoredBy();
        this.timestamp = dataValue.getTimestamp();
        this.value = dataValue.getValue();

    }

    public DataValueAudit( DataElement dataElement, DataElementCategoryOptionCombo optionCombo, Period period,
        Integer rev, Source source )
    {
        super();
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
        this.period = period;
        this.rev = rev;
        this.source = source;
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

    public DataElementCategoryOptionCombo getOptionCombo()
    {
        return optionCombo;
    }

    public void setOptionCombo( DataElementCategoryOptionCombo optionCombo )
    {
        this.optionCombo = optionCombo;
    }

    public int getRev()
    {
        return rev;
    }

    public void setRev( int rev )
    {
        this.rev = rev;
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

    public String getRevisionType()
    {
        return revisionType;
    }

    public void setRevisionType( String revisionType )
    {
        this.revisionType = revisionType;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((dataElement == null) ? 0 : dataElement.hashCode());
        result = prime * result + ((optionCombo == null) ? 0 : optionCombo.hashCode());
        result = prime * result + ((period == null) ? 0 : period.hashCode());
        result = prime * result + rev;
        result = prime * result + ((revisionType == null) ? 0 : revisionType.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((storedBy == null) ? 0 : storedBy.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        DataValueAudit other = (DataValueAudit) obj;
        if ( comment == null )
        {
            if ( other.comment != null )
                return false;
        }
        else if ( !comment.equals( other.comment ) )
            return false;
        if ( dataElement == null )
        {
            if ( other.dataElement != null )
                return false;
        }
        else if ( !dataElement.equals( other.dataElement ) )
            return false;
        if ( optionCombo == null )
        {
            if ( other.optionCombo != null )
                return false;
        }
        else if ( !optionCombo.equals( other.optionCombo ) )
            return false;
        if ( period == null )
        {
            if ( other.period != null )
                return false;
        }
        else if ( !period.equals( other.period ) )
            return false;
        if ( rev != other.rev )
            return false;
        if ( revisionType == null )
        {
            if ( other.revisionType != null )
                return false;
        }
        else if ( !revisionType.equals( other.revisionType ) )
            return false;
        if ( source == null )
        {
            if ( other.source != null )
                return false;
        }
        else if ( !source.equals( other.source ) )
            return false;
        if ( storedBy == null )
        {
            if ( other.storedBy != null )
                return false;
        }
        else if ( !storedBy.equals( other.storedBy ) )
            return false;
        if ( timestamp == null )
        {
            if ( other.timestamp != null )
                return false;
        }
        else if ( !timestamp.equals( other.timestamp ) )
            return false;
        if ( value == null )
        {
            if ( other.value != null )
                return false;
        }
        else if ( !value.equals( other.value ) )
            return false;
        return true;
    }
}
