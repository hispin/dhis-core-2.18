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

import org.hisp.dhis.datavalue.DeflatedDataValue;

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
    /**
     * Outlier datavalue.
     */
    private DeflatedDataValue outlier;

    /**
     * Lower bound. This is the lower cut-off point for 
     * whether a value is considered an outlier or not.
     */
    private double lowerBound;

    /**
     * Upper boundary.
     */
    private double upperBound;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    
    public OutlierValue( DeflatedDataValue outlier, double lowerBound, double upperBound )
    {
        this.outlier = outlier;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    // -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------

    /**
     * Returns the lower bound. The value is either smaller than the lower
     * bound, or bigger than the upper bound.
     * 
     * @return The lower bound for non-outlier values.
     */
    public double getLowerBound()
    {
        return lowerBound;
    }

    /**
     * Returns the upper bound.
     * 
     * @see OutlierCollection#getLowerBound()
     * @return The upper bound for non-outlier values.
     */
    public double getUpperBound()
    {
        return upperBound;
    }

    
    public String getLowerBoundFormatted() {
        return String.format("%.2f", lowerBound);
    }
    

    public String getUpperBoundFormatted() {
        return String.format("%.2f", upperBound);
    }
    
    /**
     * Returns the outlier.
     * 
     * @return The outlier DataValue.
     */
    public DeflatedDataValue getOutlier()
    {
        return outlier;
    }

    /**
     * Sets the outlier DataValue.
     * 
     * @param outlier An outlier DataValue.
     */
    public void setOutlier( DeflatedDataValue outlier )
    {
        this.outlier = outlier;
    }

    /**
     * @param lowerBound the lowerBound to set
     */
    public void setLowerBound( double lowerBound )
    {
        this.lowerBound = lowerBound;
    }

    /**
     * @param upperBound the upperBound to set
     */
    public void setUpperBound( double upperBound )
    {
        this.upperBound = upperBound;
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

        return outlier.equals( other.outlier ) && lowerBound == other.lowerBound && upperBound == other.upperBound;
    }

    @Override
    public int hashCode()
    {
        return outlier.hashCode();
    }
}
