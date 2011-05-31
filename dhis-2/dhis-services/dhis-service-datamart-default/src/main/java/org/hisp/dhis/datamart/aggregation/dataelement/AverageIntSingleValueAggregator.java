package org.hisp.dhis.datamart.aggregation.dataelement;

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

import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_AVERAGE;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_INT;
import static org.hisp.dhis.system.util.DateUtils.getDaysInclusive;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.CrossTabDataValue;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.springframework.util.CollectionUtils;

/**
 * @author Lars Helge Overland
 */
public class AverageIntSingleValueAggregator
    implements DataElementAggregator
{
    private static final Log log = LogFactory.getLog( AverageIntSingleValueAggregator.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CrossTabService crossTabService;

    public void setCrossTabService( CrossTabService crossTabService )
    {
        this.crossTabService = crossTabService;
    }

    protected AggregationCache aggregationCache;
        
    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }

    // -------------------------------------------------------------------------
    // DataElementAggregator implementation
    // -------------------------------------------------------------------------

    public Map<DataElementOperand, Double> getAggregatedValues( final Collection<DataElementOperand> operands, 
        final Period period, final OrganisationUnit unit, int unitLevel, OrganisationUnitHierarchy hierarchy, String key )
    {
        if ( CollectionUtils.isEmpty( operands ) )
        {
            return new HashMap<DataElementOperand, Double>();
        }
        
        final Collection<CrossTabDataValue> crossTabValues = crossTabService.getCrossTabDataValues( operands, 
            aggregationCache.getIntersectingPeriods( period.getStartDate(), period.getEndDate() ), hierarchy.getChildren( unit.getId() ), key );
        
        final Map<DataElementOperand, double[]> entries = getAggregate( crossTabValues, period.getStartDate(), 
            period.getEndDate(), period.getStartDate(), period.getEndDate(), unitLevel ); // <Operand, [total value, total relevant days]>

        final Map<DataElementOperand, Double> values = new HashMap<DataElementOperand, Double>( entries.size() ); // <Operand, total value>
        
        for ( final Entry<DataElementOperand, double[]> entry : entries.entrySet() ) 
        {
            if ( entry.getValue() != null && entry.getValue()[ 1 ] > 0 )
            {
                values.put( entry.getKey(), entry.getValue()[ 0 ] );
            }
        }
        
        return values;
    }
    
    public Map<DataElementOperand, double[]> getAggregate( final Collection<CrossTabDataValue> crossTabValues, 
        final Date startDate, final Date endDate, final Date aggregationStartDate, final Date aggregationEndDate, int unitLevel )
    {
        final Map<DataElementOperand, double[]> totalSums = new HashMap<DataElementOperand, double[]>(); // <Operand, [total value, total relevant days]>

        for ( final CrossTabDataValue crossTabValue : crossTabValues )
        {
            final Period period = aggregationCache.getPeriod( crossTabValue.getPeriodId() );
            
            final Date currentStartDate = period.getStartDate();
            final Date currentEndDate = period.getEndDate();

            final int dataValueLevel = aggregationCache.getLevelOfOrganisationUnit( crossTabValue.getSourceId() );
            
            final double duration = getDaysInclusive( currentStartDate, currentEndDate );
            
            if ( duration > 0 )
            {            
                for ( final Entry<DataElementOperand, String> entry : crossTabValue.getValueMap().entrySet() ) // <Operand, value>
                {
                    if ( entry.getValue() != null && entry.getKey().aggregationLevelIsValid( unitLevel, dataValueLevel )  )
                    {
                        double value = 0.0;
                        double relevantDays = 0.0;             
                        
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
                            continue;
                        }
    
                        if ( currentStartDate.compareTo( endDate ) <= 0 && currentEndDate.compareTo( startDate ) >= 0 ) // Value is intersecting
                        {
                            relevantDays = getDaysInclusive( startDate, endDate );
                        }

                        final double[] totalSum = totalSums.get( entry.getKey() );
                        value += totalSum != null ? totalSum[0] : 0;
                        relevantDays += totalSum != null ? totalSum[1] : 0;
                        
                        final double[] values = { value, relevantDays };
                        
                        totalSums.put( entry.getKey(), values );
                    }
                }
            }
        }                    
        
        return totalSums;
    }

    public Collection<DataElementOperand> filterOperands( final Collection<DataElementOperand> operands, final PeriodType periodType )
    {
        final Collection<DataElementOperand> filteredOperands = new HashSet<DataElementOperand>();
        
        for ( final DataElementOperand operand : operands )
        {
            if ( operand.getValueType().equals( VALUE_TYPE_INT ) && operand.getAggregationOperator().equals( AGGREGATION_OPERATOR_AVERAGE ) &&
                operand.getFrequencyOrder() >= periodType.getFrequencyOrder() )
            {
                filteredOperands.add( operand );
            }
        }
        
        return filteredOperands;
    }    
}
