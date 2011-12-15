package org.hisp.dhis.aggregation;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.common.AggregatedValue;

/**
 * @author Lars Helge Overland
 */
public class AggregatedDataValue
    implements AggregatedValue, Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6569434956128470844L;

    private int dataElementId;

    private int categoryOptionComboId;
    
    private int periodId;
    
    private int periodTypeId;
    
    private int organisationUnitId;
    
    private int organisationUnitGroupId;
    
    private int level;
    
    private double value;

    private Date modified;
    
    private transient String dataElementName;
    
    private transient String periodName;
    
    private transient String organisationUnitName;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------
    
    public AggregatedDataValue()
    {   
    }

    /**
     * @param dataElementId data element id
     * @param categoryOptionComboId category option combo id
     * @param periodId period id
     * @param periodTypeId period type id
     * @param organisationUnitId organisation unit id
     * @param level level
     * @param value value
     */
    public AggregatedDataValue( int dataElementId, int categoryOptionComboId, int periodId, int periodTypeId, int organisationUnitId, int level, double value ) 
    {
        this.dataElementId = dataElementId;
        this.categoryOptionComboId = categoryOptionComboId;
        this.periodId = periodId;
        this.periodTypeId = periodTypeId;
        this.organisationUnitId = organisationUnitId;
        this.level = level;
        this.value = value;
        this.modified = new Date();
    }

    /**
     * @param dataElementId data element id
     * @param categoryOptionComboId category option combo id
     * @param periodId period id
     * @param periodTypeId period type id
     * @param organisationUnitId organisation unit id
     * @param level level
     * @param value value
     * @param timestamp modification timestamp
     */
    public AggregatedDataValue( int dataElementId, int categoryOptionComboId, int periodId, int periodTypeId, int organisationUnitId, int level, double value, Date timestamp )
    {
        this.dataElementId = dataElementId;
        this.categoryOptionComboId = categoryOptionComboId;
        this.periodId = periodId;
        this.periodTypeId = periodTypeId;
        this.organisationUnitId = organisationUnitId;
        this.level = level;
        this.value = value;
        this.modified = timestamp;
    }

    // ----------------------------------------------------------------------
    // Logic
    // ----------------------------------------------------------------------
    
    public void clear()
    {
        this.dataElementId = 0;
        this.categoryOptionComboId = 0;
        this.periodId = 0;
        this.periodTypeId = 0;
        this.organisationUnitId = 0;
        this.level = 0;
        this.value = 0.0;
    }

    @Override
    public int getElementId()
    {
        return dataElementId;
    }
    
    // ----------------------------------------------------------------------
    // Getters and setters
    // ----------------------------------------------------------------------

    public int getDataElementId()
    {
        return dataElementId;
    }

    public void setDataElementId( int dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    public int getCategoryOptionComboId()
    {
        return categoryOptionComboId;
    }

    public void setCategoryOptionComboId( int categoryOptionComboId )
    {
        this.categoryOptionComboId = categoryOptionComboId;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel( int level )
    {
        this.level = level;
    }

    public int getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    public void setOrganisationUnitId( int organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    public int getOrganisationUnitGroupId()
    {
        return organisationUnitGroupId;
    }

    public void setOrganisationUnitGroupId( int organisationUnitGroupId )
    {
        this.organisationUnitGroupId = organisationUnitGroupId;
    }

    public int getPeriodId()
    {
        return periodId;
    }

    public void setPeriodId( int periodId )
    {
        this.periodId = periodId;
    }

    public int getPeriodTypeId()
    {
        return periodTypeId;
    }

    public void setPeriodTypeId( int periodTypeId )
    {
        this.periodTypeId = periodTypeId;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue( double value )
    {
        this.value = value;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified( Date modified )
    {
        this.modified = modified;
    }

    public String getDataElementName()
    {
        return dataElementName;
    }

    public void setDataElementName( String dataElementName )
    {
        this.dataElementName = dataElementName;
    }

    public String getPeriodName()
    {
        return periodName;
    }

    public void setPeriodName( String periodName )
    {
        this.periodName = periodName;
    }

    public String getOrganisationUnitName()
    {
        return organisationUnitName;
    }

    public void setOrganisationUnitName( String organisationUnitName )
    {
        this.organisationUnitName = organisationUnitName;
    }

    // ----------------------------------------------------------------------
    // hashCode and equals
    // ----------------------------------------------------------------------

    public int hashCode()
    {
        return dataElementId * periodId * organisationUnitId * 17;
    }
    
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( object == null || !( object instanceof AggregatedDataValue ) )
        {
            return false;
        }
        
        AggregatedDataValue that = (AggregatedDataValue) object;
        
        return this.dataElementId == that.getDataElementId() &&
            this.categoryOptionComboId == that.categoryOptionComboId &&
    	    this.periodId == that.getPeriodId() &&
    	    this.organisationUnitId == that.getOrganisationUnitId();
    }
}
