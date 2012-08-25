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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.CrossTabDataValue;
import org.hisp.dhis.datamart.OrgUnitOperand;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.MathUtils;

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

    public List<OrgUnitOperand> getAggregatedValues( DataElementOperand operand, Period period, 
        Collection<Integer> organisationUnits, Collection<OrganisationUnitGroup> organisationUnitGroups, OrganisationUnitHierarchy hierarchy, String key )
    {
        final Collection<CrossTabDataValue> crossTabValues = crossTabService.getCrossTabDataValues( operand, 
            aggregationCache.getIntersectingPeriods( period.getStartDate(), period.getEndDate() ), hierarchy.getChildren( organisationUnits ), key );
        
        final List<OrgUnitOperand> values = new ArrayList<OrgUnitOperand>();

        if ( crossTabValues.size() == 0 )
        {
            return values;
        }
        
        for ( Integer organisationUnit : organisationUnits )
        {
            final int unitLevel = operand.isHasAggregationLevels() ? aggregationCache.getLevelOfOrganisationUnit( organisationUnit ) : 0; //TODO aggregation level
            
            for ( OrganisationUnitGroup group : organisationUnitGroups )
            {
                final Collection<Integer> orgUnitChildren = hierarchy.getChildren( organisationUnit, group ); // TODO group

                double value = 0d;
                
                for ( Integer orgUnitChild : orgUnitChildren )
                {
                    final int dataValueLevel = operand.isHasAggregationLevels() ? aggregationCache.getLevelOfOrganisationUnit( orgUnitChild ) : 0; //TODO aggregation level
                    
                    if ( operand.isHasAggregationLevels() && !operand.aggregationLevelIsValid( unitLevel, dataValueLevel ) )
                    {
                        continue;
                    }
                    
                    for ( CrossTabDataValue crossTabValue : crossTabValues ) // Iterate periods
                    {
                        String val = crossTabValue.getValueMap().get( orgUnitChild ); // TODO make set of periods, avoid iteration?
    
                        if ( val == null )
                        {
                            continue;
                        }
                        
                        try
                        {
                            value += Double.parseDouble( val );
                        }
                        catch ( NumberFormatException ex )
                        {
                            log.warn( "Value skipped, not numeric: '" + val );
                            continue;
                        }
                    }
                }
                
                if ( !MathUtils.isZero( value ) )
                {
                    values.add( new OrgUnitOperand( organisationUnit, group != null ? group.getId() : 0, value ) );
                }
            }
        }
        
        return values;
    }
    
    public boolean isApplicable( DataElementOperand operand, PeriodType periodType )
    {
        return ( operand.getValueType().equals( VALUE_TYPE_INT ) && operand.getAggregationOperator().equals( AGGREGATION_OPERATOR_AVERAGE ) &&
            operand.getFrequencyOrder() >= periodType.getFrequencyOrder() );
    }    
}
