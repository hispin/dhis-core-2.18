package org.hisp.dhis.datamart.engine;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataElementOperandList;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.datamart.dataelement.DataElementDataMart;
import org.hisp.dhis.datamart.indicator.IndicatorDataMart;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.jdbc.batchhandler.AggregatedDataValueBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.filter.AggregatableDataElementFilter;
import org.hisp.dhis.system.filter.PastAndCurrentPeriodFilter;
import org.hisp.dhis.system.util.Clock;
import org.hisp.dhis.system.util.ConcurrentUtils;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.system.util.PaginatedList;
import org.hisp.dhis.system.util.SystemUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
public class DefaultDataMartEngine
    implements DataMartEngine
{    
    private static final Set<OrganisationUnitGroup> DUMMY_ORG_UNIT_GROUPS = new HashSet<OrganisationUnitGroup>()
    { {
        add( new OrganisationUnitGroup( "" ) );
    } };
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AggregatedDataValueService aggregatedDataValueService;

    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    private CrossTabService crossTabService;

    public void setCrossTabService( CrossTabService crossTabService )
    {
        this.crossTabService = crossTabService;
    }

    private DataElementDataMart dataElementDataMart;

    public void setDataElementDataMart( DataElementDataMart dataElementDataMart )
    {
        this.dataElementDataMart = dataElementDataMart;
    }

    private IndicatorDataMart indicatorDataMart;

    public void setIndicatorDataMart( IndicatorDataMart indicatorDataMart )
    {
        this.indicatorDataMart = indicatorDataMart;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // DataMartEngine implementation
    // -------------------------------------------------------------------------

    @Transactional
    public void export( Collection<Integer> dataElementIds, Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, boolean completeExport, ProcessState state )
    {
        final int cpuCores = SystemUtils.getCpuCores();
        
        Clock clock = new Clock().startClock().logTime( "Data mart export process started, number of CPU cores: " + cpuCores );
        
        // ---------------------------------------------------------------------
        // Get objects
        // ---------------------------------------------------------------------

        Collection<Indicator> indicators = indicatorService.getIndicators( indicatorIds );
        Collection<Period> periods = periodService.getPeriods( periodIds );
        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnits( organisationUnitIds ) );
        Collection<DataElement> dataElements = dataElementService.getDataElements( dataElementIds );

        clock.logTime( "Retrieved objects" );

        // ---------------------------------------------------------------------
        // Filter objects
        // ---------------------------------------------------------------------

        organisationUnitService.filterOrganisationUnitsWithoutData( organisationUnits );
        Collections.shuffle( organisationUnits );
        FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );
        expressionService.filterInvalidIndicators( indicators );

        clock.logTime( "Filtered objects" );

        // ---------------------------------------------------------------------
        // Explode indicator expressions
        // ---------------------------------------------------------------------

        for ( Indicator indicator : indicators )
        {
            indicator.setExplodedNumerator( expressionService.explodeExpression( indicator.getNumerator() ) );
            indicator.setExplodedDenominator( expressionService.explodeExpression( indicator.getDenominator() ) );
        }

        clock.logTime( "Exploded indicator expressions" );

        // ---------------------------------------------------------------------
        // Get operands
        // ---------------------------------------------------------------------
        
        Collection<DataElementOperand> dataElementOperands = categoryService.getOperands( dataElements );
        List<DataElementOperand> indicatorOperands = new ArrayList<DataElementOperand>( categoryService.populateOperands( expressionService.getOperandsInIndicators( indicators ) ) );
        
        Set<DataElementOperand> allOperands = new HashSet<DataElementOperand>();
        allOperands.addAll( dataElementOperands );
        allOperands.addAll( indicatorOperands );

        clock.logTime( "Retrieved operands: " + allOperands.size() );

        // ---------------------------------------------------------------------
        // Filter out future periods
        // ---------------------------------------------------------------------

        FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );
        
        clock.logTime( "Number of periods: " + periods.size() );
        
        // ---------------------------------------------------------------------
        // Remove operands without data
        // ---------------------------------------------------------------------

        allOperands = crossTabService.getOperandsWithData( allOperands );

        indicatorOperands.retainAll( allOperands );
        
        clock.logTime( "Number of operands with data: " + allOperands.size() );

        // ---------------------------------------------------------------------
        // Create crosstabtable
        // ---------------------------------------------------------------------

        state.setMessage( "crosstabulating_data" );

        Collection<Integer> intersectingPeriodIds = ConversionUtils.getIdentifiers( Period.class, periodService.getIntersectionPeriods( periods ) );
        Set<Integer> childrenIds = organisationUnitService.getOrganisationUnitHierarchy().getChildren( organisationUnitIds );
        List<List<Integer>> childrenPages = new PaginatedList<Integer>( childrenIds ).setNumberOfPages( cpuCores ).getPages();

        List<DataElementOperand> crossTabOperands = new ArrayList<DataElementOperand>( allOperands );
        String key = crossTabService.createCrossTabTable( crossTabOperands );
        
        List<Future<?>> crossTabFutures = new ArrayList<Future<?>>();
        
        for ( List<Integer> childrenPage : childrenPages )
        {
            crossTabFutures.add( crossTabService.populateCrossTabTable( crossTabOperands, intersectingPeriodIds, childrenPage, key ) );
        }

        ConcurrentUtils.waitForCompletion( crossTabFutures );
        
        clock.logTime( "Populated crosstab table" );

        // ---------------------------------------------------------------------
        // Create aggregated data cache
        // ---------------------------------------------------------------------

        crossTabService.createAggregatedDataCache( indicatorOperands, key );
        
        clock.logTime( "Created aggregated data cache" );
        
        // ---------------------------------------------------------------------
        // Drop potential indexes
        // ---------------------------------------------------------------------

        final boolean isIndicators = indicators != null && indicators.size() > 0;
        
        aggregatedDataValueService.dropIndex( true, isIndicators );
        
        clock.logTime( "Dropped potential indexes" );
        
        // ---------------------------------------------------------------------
        // Delete existing aggregated datavalues
        // ---------------------------------------------------------------------

        if ( completeExport )
        {
            aggregatedDataValueService.deleteAggregatedDataValues( periodIds );
        }
        else
        {
            aggregatedDataValueService.deleteAggregatedDataValues( dataElementIds, periodIds, organisationUnitIds );
        }

        clock.logTime( "Deleted existing aggregated datavalues" );
        
        // ---------------------------------------------------------------------
        // Export data element values
        // ---------------------------------------------------------------------

        state.setMessage( "exporting_data_for_data_elements" );

        List<List<OrganisationUnit>> organisationUnitPages = new PaginatedList<OrganisationUnit>( organisationUnits ).setNumberOfPages( cpuCores ).getPages();
        
        if ( allOperands.size() > 0 )
        {
            List<Future<?>> futures = new ArrayList<Future<?>>();
            
            for ( List<OrganisationUnit> organisationUnitPage : organisationUnitPages )
            {
                futures.add( dataElementDataMart.exportDataValues( allOperands, periods, organisationUnitPage, 
                    DUMMY_ORG_UNIT_GROUPS, new DataElementOperandList( indicatorOperands ), AggregatedDataValueBatchHandler.class, key ) );
            }

            ConcurrentUtils.waitForCompletion( futures );
            
            clock.logTime( "Exported values for data element operands (" + allOperands.size() + "), number of pages: " + organisationUnitPages.size() );
        }

        // ---------------------------------------------------------------------
        // Drop crosstab table
        // ---------------------------------------------------------------------

        crossTabService.dropCrossTabTable( key );
        
        clock.logTime( "Dropped crosstab table" );

        // ---------------------------------------------------------------------
        // Delete existing aggregated indicatorvalues
        // ---------------------------------------------------------------------

        if ( completeExport )
        {
            aggregatedDataValueService.deleteAggregatedIndicatorValues( periodIds );
        }
        else
        {
            aggregatedDataValueService.deleteAggregatedIndicatorValues( indicatorIds, periodIds, organisationUnitIds );
        }

        clock.logTime( "Deleted existing aggregated indicatorvalues" );
        
        // ---------------------------------------------------------------------
        // Export indicator values
        // ---------------------------------------------------------------------

        state.setMessage( "exporting_data_for_indicators" );

        if ( isIndicators )
        {
            List<Future<?>> futures = new ArrayList<Future<?>>();

            for ( List<OrganisationUnit> organisationUnitPage : organisationUnitPages )
            {
                futures.add( indicatorDataMart.exportIndicatorValues( indicators, periods, organisationUnitPage, indicatorOperands, key ) );
            }

            ConcurrentUtils.waitForCompletion( futures );
            
            clock.logTime( "Exported values for indicators (" + indicators.size() + "), number of pages: " + organisationUnitPages.size() );
        }

        // ---------------------------------------------------------------------
        // Drop aggregated data cache
        // ---------------------------------------------------------------------

        crossTabService.dropAggregatedDataCache( key );
        
        clock.logTime( "Dropped aggregated data cache" );

        // ---------------------------------------------------------------------
        // Create potential indexes
        // ---------------------------------------------------------------------

        if ( completeExport )
        {
            aggregatedDataValueService.createIndex( true, isIndicators );
            
            clock.logTime( "Created indexes" );
        }

        clock.logTime( "Data mart export process completed" );
    }
}
