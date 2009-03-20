package org.hisp.dhis.datamart.aggregation.dataelement;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import static org.hisp.dhis.system.util.MathUtils.getDays;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.datamart.CrossTabDataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.period.Period;

/**
 * @author Lars Helge Overland
 * @version $Id: AverageIntAggregator.java 6049 2008-10-28 09:36:17Z larshelg $
 */
public class AverageIntAggregator
    extends DataElementAggregator
{
    private static final Log log = LogFactory.getLog( AverageIntAggregator.class );
    
    public Map<Operand, Double> getAggregatedValues( final Map<Operand, Integer> operandIndexMap, final Period period, final OrganisationUnit unit )
    {
        final OrganisationUnitHierarchy hierarchy = aggregationCache.getLatestOrganisationUnitHierarchy();
        
        final Collection<Integer> unitIds = aggregationCache.getChildren( hierarchy, unit.getId() );
        
        final Map<Operand, Double> values = new HashMap<Operand, Double>(); // <Operand, total value>
        
        double average = 0.0;
        double existingAverage = 0.0;
        
        for ( final Integer unitId : unitIds )
        {
            final Collection<CrossTabDataValue> crossTabValues = 
                getCrossTabDataValues( operandIndexMap, period.getStartDate(), period.getEndDate(), unitId, hierarchy );
            
            final Map<Operand, Double[]> entries = getAggregate( crossTabValues, period.getStartDate(), 
                period.getEndDate(), period.getStartDate(), period.getEndDate() ); // <Operand, [total value, total relevant days]>
            
            for ( final Entry<Operand, Double[]> entry : entries.entrySet() ) 
            {
                if ( entry.getValue() != null && entry.getValue()[ 1 ] > 0 )
                {
                    average = entry.getValue()[ 0 ] / entry.getValue()[ 1 ];
                    
                    existingAverage = values.containsKey( entry.getKey() ) ? values.get( entry.getKey() ) : 0;
                    
                    values.put( entry.getKey(), average + existingAverage );
                }
            }
        }  
        
        return values;
    }
    
    protected Collection<CrossTabDataValue> getCrossTabDataValues( final Map<Operand, Integer> operandIndexMap, 
        final Date startDate, final Date endDate, final int parentId, final OrganisationUnitHierarchy hierarchy )
    {
        final Collection<Period> periods = aggregationCache.getIntersectingPeriods( startDate, endDate );
        
        final Collection<Integer> periodIds = new ArrayList<Integer>( periods.size() );
        
        for ( final Period period : periods )
        {
            periodIds.add( period.getId() );
        }
        
        return dataMartStore.getCrossTabDataValues( operandIndexMap, periodIds, parentId );
    }
    
    protected Map<Operand, Double[]> getAggregate( final Collection<CrossTabDataValue> crossTabValues, 
        final Date startDate, final Date endDate, final Date aggregationStartDate, final Date aggregationEndDate )
    {
        final Map<Operand, Double[]> totalSums = new HashMap<Operand, Double[]>(); // <Operand, [total value, total relevant days]>

        Period period = null;
        Date currentStartDate = null;
        Date currentEndDate = null;
        
        double value = 0.0;
        double relevantDays = 0.0;
        double existingValue = 0.0;
        double existingRelevantDays = 0.0;
        
        for ( final CrossTabDataValue crossTabValue : crossTabValues )
        {
            period = aggregationCache.getPeriod( crossTabValue.getPeriodId() );
            
            currentStartDate = period.getStartDate();
            currentEndDate = period.getEndDate();
            
            for ( final Entry<Operand, String> entry : crossTabValue.getValueMap().entrySet() ) // <Operand, value>
            {
                if ( entry.getValue() != null )
                {
                    value = 0.0;
                    
                    try
                    {
                        value = Double.parseDouble( entry.getValue() );
                    }
                    catch ( NumberFormatException ex )
                    {
                        log.warn( "Value skipped, not numeric: '" + entry.getValue() + 
                            "', for data element with id: '" + entry.getKey() +
                            "', for period with id: '" + crossTabValue.getPeriodId() +
                            "', for source with id: '" + crossTabValue.getSourceId() + "'" );
                    }
                                        
                    relevantDays = 0.0;
                    
                    if ( currentStartDate.compareTo( startDate ) >= 0 && currentEndDate.compareTo( endDate ) <= 0 ) // Value is within period
                    {
                        relevantDays = getDays( currentEndDate ) - getDays( currentStartDate );
                    }
                    else if ( currentStartDate.compareTo( startDate ) <= 0 && currentEndDate.compareTo( endDate ) >= 0 ) // Value spans whole period
                    {
                        relevantDays = getDays( endDate ) - getDays( startDate );
                    }
                    else if ( currentStartDate.compareTo( startDate ) <= 0 && currentEndDate.compareTo( startDate ) >= 0
                        && currentEndDate.compareTo( endDate ) <= 0 ) // Value spans period start
                    {
                        relevantDays = getDays( currentEndDate ) - getDays( startDate );
                    }
                    else if ( currentStartDate.compareTo( startDate ) >= 0 && currentStartDate.compareTo( endDate ) <= 0
                        && currentEndDate.compareTo( endDate ) >= 0 ) // Value spans period end
                    {
                        relevantDays = getDays( endDate ) - getDays( currentStartDate );
                    }
                    
                    value = value * relevantDays;
                    
                    existingValue = totalSums.containsKey( entry.getKey() ) ? totalSums.get( entry.getKey() )[ 0 ] : 0;
                    existingRelevantDays = totalSums.containsKey( entry.getKey() ) ? totalSums.get( entry.getKey() )[ 1 ] : 0;
                    
                    final Double[] values = { ( value + existingValue ), ( relevantDays + existingRelevantDays ) };
                    
                    totalSums.put( entry.getKey(), values );
                }
            }
        }                    
        
        return totalSums;
    }
}
