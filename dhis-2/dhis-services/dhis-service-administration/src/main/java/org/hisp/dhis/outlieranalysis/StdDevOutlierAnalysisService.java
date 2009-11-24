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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * 
 * @author Dag Haavi Finstad
 * @version $Id: DefaultStdDevOutlierAnalysisService.java 1020 2009-06-05 01:30:07Z daghf $
 */
public class StdDevOutlierAnalysisService
    extends AbstractOutlierAnalysisService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    // -------------------------------------------------------------------------
    // OutlierAnalysisService implementation
    // -------------------------------------------------------------------------

    public Collection<OutlierValue> findOutliers( OrganisationUnit organisationUnit, 
        DataElement dataElement, Collection<Period> periods, Double stdDevFactor )
    {
        final Collection<OutlierValue> outlierValues = new ArrayList<OutlierValue>();

        if ( !dataElement.getType().equals( DataElement.VALUE_TYPE_INT ) )
        {
            return outlierValues;
        }

        final Collection<DataValue> dataValues = dataValueService.getDataValues( organisationUnit, dataElement );
        final DescriptiveStatistics statistics = new DescriptiveStatistics();
        
        final Map<Period, DataValue> dataValueMap = new HashMap<Period, DataValue>();        
        
        for ( DataValue dataValue : dataValues )
        {
            statistics.addValue( Double.parseDouble( dataValue.getValue() ) );
            dataValueMap.put( dataValue.getPeriod(), dataValue );
        }

        double mean = statistics.getMean();
        double deviation = statistics.getStandardDeviation() * stdDevFactor;
        
        double lowerBound = mean - deviation;
        double upperBound = mean + deviation;

        for ( Period period : periods )
        {
            final DataValue dataValue = dataValueMap.get( period );

            if ( dataValue == null )
            {
                continue;
            }

            final double value = Double.parseDouble( dataValue.getValue() );

            if ( value < lowerBound || value > upperBound )
            {
                outlierValues.add( new OutlierValue( dataValue, lowerBound, upperBound ) );
            }
        }

        return outlierValues;
    }
}
