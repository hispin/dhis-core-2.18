package org.hisp.dhis.datamart.aggregation.dataelement;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.OrgUnitOperand;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.datamart.crosstab.jdbc.CrossTabStore;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodHierarchy;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.MathUtils;

/**
 * @author Lars Helge Overland
 */
public class AverageIntAggregator
    implements DataElementAggregator
{
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

    public List<OrgUnitOperand> getAggregatedValues( DataElementOperand operand, Collection<Period> periods,
        Collection<Integer> organisationUnits, Collection<OrganisationUnitGroup> organisationUnitGroups, 
        PeriodHierarchy periodHierarchy, OrganisationUnitHierarchy orgUnitHierarchy, String key )
    {
        final Map<String, String> crossTabValues = crossTabService.getCrossTabDataValues( operand, 
            periodHierarchy.getIntersectingPeriods( periods ), orgUnitHierarchy.getChildren( organisationUnits ), key );
        
        final List<OrgUnitOperand> values = new ArrayList<OrgUnitOperand>();

        if ( crossTabValues.size() == 0 )
        {
            return values;
        }

        for ( Period period : periods )
        {
            final PeriodType periodType = period.getPeriodType();
            
            final Collection<Integer> intersectingPeriods = periodHierarchy.getIntersectingPeriods( period );
            
            for ( final Integer organisationUnit : organisationUnits )
            {
                final int unitLevel = operand.isHasAggregationLevels() ? aggregationCache.getLevelOfOrganisationUnit( organisationUnit ) : 0;
                
                for ( OrganisationUnitGroup group : organisationUnitGroups )
                {
                    final Set<Integer> orgUnitChildren = orgUnitHierarchy.getChildren( organisationUnit, group );

                    aggregationCache.filterForAggregationLevel( orgUnitChildren, operand, unitLevel );
                    
                    double value = 0d;
                    
                    for ( Integer orgUnitChild : orgUnitChildren )
                    {
                        double totalValue = 0d;
                        double totalRelevantDays = 0d;
                        
                        for ( Integer intersectingPeriod : intersectingPeriods )
                        {
                            final String val = crossTabValues.get( intersectingPeriod + CrossTabStore.SEPARATOR + orgUnitChild );
                            
                            if ( val != null )
                            {
                                final double[] entry = getAggregate( orgUnitChild, aggregationCache.getPeriod( intersectingPeriod ), val, period.getStartDate(), period.getEndDate(), unitLevel ); // <Org unit, [total value, total relevant days]>
                                
                                totalValue += entry[0];
                                totalRelevantDays += entry[1];
                            }
                        }
                                                
                        if ( !MathUtils.isZero( totalRelevantDays ) )
                        {
                            double average = totalValue / totalRelevantDays;
                            
                            value += average;
                        }
                    }
                    
                    if ( !MathUtils.isZero( value ) )
                    {
                        values.add( new OrgUnitOperand( period.getId(), periodType.getId(), organisationUnit, group != null ? group.getId() : 0, value ) );
                    }
                }
            }
        }
        
        return values;
    }

    public boolean isApplicable( DataElementOperand operand )
    {
        return operand.getValueType().equals( VALUE_TYPE_INT ) && operand.getAggregationOperator().equals( AGGREGATION_OPERATOR_AVERAGE );
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private double[] getAggregate( int organisationUnit, Period period, String val, Date startDate, Date endDate, int unitLevel )
    {
        double value = 0.0;
        double relevantDays = 0.0;
        
        final Date currentStartDate = period.getStartDate();
        final Date currentEndDate = period.getEndDate();

        final double duration = getDaysInclusive( currentStartDate, currentEndDate );
        
        if ( duration > 0 )
        {
            value = Double.parseDouble( val );
            
            if ( currentStartDate.compareTo( startDate ) >= 0 && currentEndDate.compareTo( endDate ) <= 0 ) // Value is within period
            {
                relevantDays = getDaysInclusive( currentStartDate, currentEndDate );
            }
            else if ( currentStartDate.compareTo( startDate ) <= 0 && currentEndDate.compareTo( endDate ) >= 0 ) // Value spans whole period
            {
                relevantDays = getDaysInclusive( startDate, endDate );
            }
            else if ( currentStartDate.compareTo( startDate ) <= 0 && currentEndDate.compareTo( startDate ) >= 0
                && currentEndDate.compareTo( endDate ) <= 0 ) // Value spans period start
            {
                relevantDays = getDaysInclusive( startDate, currentEndDate );
            }
            else if ( currentStartDate.compareTo( startDate ) >= 0 && currentStartDate.compareTo( endDate ) <= 0
                && currentEndDate.compareTo( endDate ) >= 0 ) // Value spans period end
            {
                relevantDays = getDaysInclusive( currentStartDate, endDate );
            }
            
            value = value * relevantDays;
        }
        
        final double[] values = { value, relevantDays };
        
        return values;
    }
}
