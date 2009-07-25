package org.hisp.dhis.outlieranalysis;

/*
 * Copyright (c) 2004-${year}, University of Oslo
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


import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * 
 * @author Dag Haavi Finstad
 * @version $Id: DefaultMinMaxOutlierAnalysisService.java 1047 2009-06-10 11:01:04Z daghf $
 */
public class MinMaxOutlierAnalysisService
    extends AbstractStdDevOutlierAnalysisService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MinMaxDataElementService minMaxDataElementService;

    public void setMinMaxDataElementService( MinMaxDataElementService minMaxDataElementService )
    {
        this.minMaxDataElementService = minMaxDataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    // -------------------------------------------------------------------------
    // MinMaxOutlierAnalysisService implementation
    // -------------------------------------------------------------------------

    public Collection<OutlierValue> findOutliers( OrganisationUnit organisationUnit, DataElement dataElement,
        Collection<Period> periods, Double stdDevFactor )
    {
        final Collection<OutlierValue> outlierValues = new ArrayList<OutlierValue>();

        if ( !dataElement.getType().equals( DataElement.TYPE_INT ) )
        {
            return outlierValues;
        }

        final Collection<MinMaxDataElement> minMaxDataElements = 
            minMaxDataElementService.getMinMaxDataElements( organisationUnit, dataElement );

        for ( MinMaxDataElement minMaxDataElement : minMaxDataElements )
        {
            int lowerBound = minMaxDataElement.getMin();
            int upperBound = minMaxDataElement.getMax();
            
            for ( Period period : periods )
            {    
                final DataValue dataValue = dataValueService.getDataValue( 
                    organisationUnit, dataElement, period, minMaxDataElement.getOptionCombo() );
    
                final int value = Integer.parseInt( dataValue.getValue() );
        
                if ( value < lowerBound || value > upperBound )
                {
                    outlierValues.add( new OutlierValue( dataValue, lowerBound, upperBound ) );
                }
            }
        }        

        return outlierValues;
    }
}
