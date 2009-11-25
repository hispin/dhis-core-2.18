package org.hisp.dhis.outlieranalysis;

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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * The OutlierValue class wraps an outlier DataValue. The value is outside of
 * the interval (getLowerBound(), getUpperBound()).
 * 
 * @author Dag Haavi Finstad
 * @version $Id: OutlierValue.java 1020 2009-06-05 01:30:07Z daghf $
 * 
 */
public class OutlierValue
{
    private DataElement dataElement;
    
    private Period period;
    
    private OrganisationUnit organisationUnit;
    
    private DataElementCategoryOptionCombo categoryOptionCombo;
    
    private double value;

    private double lowerBound;

    private double upperBound;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    
    public OutlierValue( DataElement dataElement, Period period, OrganisationUnit organisationUnit,
        DataElementCategoryOptionCombo categoryOptionCombo, double value, double lowerBound, double upperBound )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.organisationUnit = organisationUnit;
        this.categoryOptionCombo = categoryOptionCombo;
        this.value = value;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    
    public OutlierValue( DataValue dataValue, double lowerBound, double upperBound )
    {
        this.dataElement = dataValue.getDataElement();
        this.period = dataValue.getPeriod();
        this.organisationUnit = (OrganisationUnit) dataValue.getSource();
        this.categoryOptionCombo = dataValue.getOptionCombo();
        this.value = Double.valueOf( dataValue.getValue() );
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public String getLowerBoundFormatted() 
    {
        return String.format("%.2f", lowerBound);
    }
    

    public String getUpperBoundFormatted() 
    {
        return String.format("%.2f", upperBound);
    }
    
    // -------------------------------------------------------------------------
    // Setters and getters
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

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public DataElementCategoryOptionCombo getCategoryOptionCombo()
    {
        return categoryOptionCombo;
    }

    public void setCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo )
    {
        this.categoryOptionCombo = categoryOptionCombo;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue( double value )
    {
        this.value = value;
    }

    public double getLowerBound()
    {
        return lowerBound;
    }

    public void setLowerBound( double lowerBound )
    {
        this.lowerBound = lowerBound;
    }

    public double getUpperBound()
    {
        return upperBound;
    }

    public void setUpperBound( double upperBound )
    {
        this.upperBound = upperBound;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = prime * result + ( ( categoryOptionCombo == null ) ? 0 : categoryOptionCombo.hashCode() );
        result = prime * result + ( ( dataElement == null ) ? 0 : dataElement.hashCode() );
        result = prime * result + ( ( organisationUnit == null ) ? 0 : organisationUnit.hashCode() );
        result = prime * result + ( ( period == null ) ? 0 : period.hashCode() );

        return result;
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

        if ( !(o instanceof OutlierValue) )
        {
            return false;
        }

        final OutlierValue other = (OutlierValue) o;

        return dataElement.equals( other.dataElement ) && period.equals( other.period ) &&
            organisationUnit.equals( other.organisationUnit ) && categoryOptionCombo.equals( other.categoryOptionCombo );
    }
}
