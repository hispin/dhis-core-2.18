package org.hisp.dhis.datamart.dataelement;

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

import static org.hisp.dhis.system.util.MathUtils.getRounded;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.DataMartEngine;
import org.hisp.dhis.datamart.OrgUnitOperand;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.aggregation.dataelement.DataElementAggregator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.SystemUtils;
import org.springframework.scheduling.annotation.Async;

/**
 * @author Lars Helge Overland
 */
public class DefaultDataElementDataMart
    implements DataElementDataMart
{
    private static final Log log = LogFactory.getLog( DefaultDataElementDataMart.class );
    
    private static final int DECIMALS = 1;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }
    
    private AggregationCache aggregationCache;

    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private Set<DataElementAggregator> aggregators;

    public void setAggregators( Set<DataElementAggregator> aggregators )
    {
        this.aggregators = aggregators;
    }
    
    // -------------------------------------------------------------------------
    // DataMart functionality
    // -------------------------------------------------------------------------
    
    @Async
    public Future<?> exportDataValues( Collection<DataElementOperand> operands, Collection<Period> periods, 
        Collection<OrganisationUnit> organisationUnits, Collection<OrganisationUnitGroup> organisationUnitGroups, 
        OrganisationUnitHierarchy hierarchy, Class<? extends BatchHandler<AggregatedDataValue>> clazz, String key )
    {
        statementManager.initialise(); // Running in separate thread
        
        final BatchHandler<AggregatedDataValue> batchHandler = batchHandlerFactory.createBatchHandler( clazz ).init();
        
        final AggregatedDataValue aggregatedValue = new AggregatedDataValue();
        
        final Collection<Integer> organisationUnitIds = ConversionUtils.getIdentifiers( OrganisationUnit.class, organisationUnits );

        populateHasAggregationLevels( operands );
        
        organisationUnitGroups = organisationUnitGroups != null ? organisationUnitGroups : DataMartEngine.DUMMY_ORG_UNIT_GROUPS;
        
        for ( final Period period : periods )
        {
            operands : for ( final DataElementOperand operand : operands )
            {
                final PeriodType periodType = period.getPeriodType();
                
                final DataElementAggregator aggregator = getAggregator( operand, periodType );

                if ( aggregator == null )
                {
                    continue operands;
                }
                
                final List<OrgUnitOperand> values = aggregator.getAggregatedValues( operand, period, organisationUnitIds, organisationUnitGroups, hierarchy, key );
                
                for ( OrgUnitOperand orgUnitOperand : values )
                {
                    final int level = aggregationCache.getLevelOfOrganisationUnit( orgUnitOperand.getOrgUnitId() );
                    
                    final double value = getRounded( orgUnitOperand.getValue(), DECIMALS );

                    aggregatedValue.clear();
                    
                    aggregatedValue.setDataElementId( operand.getDataElementId() );
                    aggregatedValue.setCategoryOptionComboId( operand.getOptionComboId() );
                    aggregatedValue.setPeriodId( period.getId() );
                    aggregatedValue.setPeriodTypeId( period.getPeriodType().getId() );
                    aggregatedValue.setOrganisationUnitId( orgUnitOperand.getOrgUnitId() );
                    aggregatedValue.setOrganisationUnitGroupId( orgUnitOperand.getOrgUnitGroupId() );
                    aggregatedValue.setLevel( level );
                    aggregatedValue.setValue( value );
                    
                    batchHandler.addObject( aggregatedValue );
                }
            }
            
            log.debug( "Exported data values for period: " + period + ", " + SystemUtils.getMemoryString() );
        }
        
        batchHandler.flush();
        
        statementManager.destroy();
        
        aggregationCache.clearCache();
        
        log.info( "Data element export task done" );
        
        return null;
    }
    
    private DataElementAggregator getAggregator( DataElementOperand operand, PeriodType periodType )
    {
        for ( DataElementAggregator aggregator : aggregators )
        {
            if ( aggregator.isApplicable( operand, periodType ) )
            {
                return aggregator;
            }
        }
        
        return null;
    }
    
    public void populateHasAggregationLevels( Collection<DataElementOperand> operands ) //TODO check
    {
        for ( DataElementOperand operand : operands )
        {
            operand.setHasAggregationLevels( operand.hasAggregationLevels() );
        }
    }
}
