package org.hisp.dhis.datamart.engine;

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

import static org.hisp.dhis.scheduling.TaskCategory.DATAMART;
import static org.hisp.dhis.setting.SystemSettingManager.DEFAULT_ORGUNITGROUPSET_AGG_LEVEL;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_ORGUNITGROUPSET_AGG_LEVEL;
import static org.hisp.dhis.system.notification.NotificationLevel.INFO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataMartEngine;
import org.hisp.dhis.datamart.DataMartManager;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.datamart.dataelement.DataElementDataMart;
import org.hisp.dhis.datamart.indicator.IndicatorDataMart;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.jdbc.batchhandler.AggregatedDataValueTempBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AggregatedIndicatorValueTempBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AggregatedOrgUnitDataValueTempBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AggregatedOrgUnitIndicatorValueTempBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.filter.AggregatableDataElementFilter;
import org.hisp.dhis.system.filter.DataElementWithAggregationFilter;
import org.hisp.dhis.system.filter.OrganisationUnitAboveOrEqualToLevelFilter;
import org.hisp.dhis.system.filter.PastAndCurrentPeriodFilter;
import org.hisp.dhis.system.notification.Notifier;
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
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataMartManager dataMartManager;

    public void setDataMartManager( DataMartManager dataMartManager )
    {
        this.dataMartManager = dataMartManager;
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
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }
    
    private Notifier notifier;

    public void setNotifier( Notifier notifier )
    {
        this.notifier = notifier;
    }

    // -------------------------------------------------------------------------
    // DataMartEngine implementation
    // -------------------------------------------------------------------------

    @Transactional
    public void export( Collection<Integer> dataElementIds, Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        export( dataElementIds, indicatorIds, periodIds, organisationUnitIds, new HashSet<Integer>(), null );
    }
    
    @Transactional
    public void export( Collection<Integer> dataElementIds, Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, Collection<Integer> organisationUnitGroupIds, TaskId id )
    {
        final int cpuCores = SystemUtils.getCpuCores();
        
        Clock clock = new Clock().startClock().logTime( "Data mart export process started, number of CPU cores: " + cpuCores + ", " + SystemUtils.getMemoryString() );
        notifier.clear( id, DATAMART ).notify( id, DATAMART, "Data mart export process started" );

        // ---------------------------------------------------------------------
        // Recreate temporary tables
        // ---------------------------------------------------------------------

        dataMartManager.dropTempAggregatedTables();
        dataMartManager.createTempAggregatedTables();
        
        clock.logTime( "Recreated temporary tables" );
        
        // ---------------------------------------------------------------------
        // Replace null with empty collection
        // ---------------------------------------------------------------------

        dataElementIds = dataElementIds != null ? dataElementIds : new ArrayList<Integer>();
        indicatorIds = indicatorIds != null ? indicatorIds : new ArrayList<Integer>();
        periodIds = periodIds != null ? periodIds : new ArrayList<Integer>();
        organisationUnitIds = organisationUnitIds != null ? organisationUnitIds : new ArrayList<Integer>();
        organisationUnitGroupIds = organisationUnitGroupIds != null ? organisationUnitGroupIds : new ArrayList<Integer>();
        
        clock.logTime( "Data elements: " + dataElementIds.size() + ", indicators: " + indicatorIds.size() + ", periods: " + periodIds.size() + ", org units: " + organisationUnitIds.size() );
        
        // ---------------------------------------------------------------------
        // Get objects
        // ---------------------------------------------------------------------

        final Collection<Indicator> indicators = indicatorService.getIndicators( indicatorIds );
        final Collection<Period> periods = periodService.getPeriods( periodIds );
        final List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnits( organisationUnitIds ) );
        final Collection<OrganisationUnitGroup> organisationUnitGroups = organisationUnitGroupService.getOrganisationUnitGroups( organisationUnitGroupIds );
        final Collection<DataElement> dataElements = dataElementService.getDataElements( dataElementIds );

        clock.logTime( "Retrieved meta-data objects" );
        notifier.notify( id, DATAMART, "Filtering meta-data" );

        // ---------------------------------------------------------------------
        // Filter objects
        // ---------------------------------------------------------------------

        organisationUnitService.filterOrganisationUnitsWithoutData( organisationUnits );
        FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );
        FilterUtils.filter( dataElements, new DataElementWithAggregationFilter() );
        expressionService.filterInvalidIndicators( indicators );

        clock.logTime( "Filtered objects" );
        notifier.notify( id, DATAMART, "Loading indicators" );

        // ---------------------------------------------------------------------
        // Explode indicator expressions
        // ---------------------------------------------------------------------

        for ( Indicator indicator : indicators )
        {
            indicator.setExplodedNumerator( expressionService.explodeExpression( indicator.getNumerator() ) );
            indicator.setExplodedDenominator( expressionService.explodeExpression( indicator.getDenominator() ) );
        }

        clock.logTime( "Exploded indicator expressions" );
        notifier.notify( id, DATAMART, "Loading data elements" );

        // ---------------------------------------------------------------------
        // Get operands
        // ---------------------------------------------------------------------
        
        final Collection<DataElementOperand> dataElementOperands = categoryService.getOperands( dataElements );
        final List<DataElementOperand> indicatorOperands = new ArrayList<DataElementOperand>( categoryService.populateOperands( expressionService.getOperandsInIndicators( indicators ) ) );
        
        Set<DataElementOperand> allOperands = new HashSet<DataElementOperand>();
        allOperands.addAll( dataElementOperands );
        allOperands.addAll( indicatorOperands );

        clock.logTime( "Retrieved operands: " + allOperands.size() );
        notifier.notify( id, DATAMART, "Loading periods" );

        // ---------------------------------------------------------------------
        // Filter out future periods
        // ---------------------------------------------------------------------

        FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );
        
        clock.logTime( "Number of periods: " + periods.size() );
        notifier.notify( id, DATAMART, "Filtering data elements without data" );
        
        // ---------------------------------------------------------------------
        // Remove operands without data
        // ---------------------------------------------------------------------

        allOperands = dataMartManager.getOperandsWithData( allOperands );

        indicatorOperands.retainAll( allOperands );
        
        clock.logTime( "Number of operands with data: " + allOperands.size() + ", " + SystemUtils.getMemoryString() );
        notifier.notify( id, DATAMART, "Populating crosstabulation table" );

        // ---------------------------------------------------------------------
        // Create crosstabtable
        // ---------------------------------------------------------------------

        //TODO improve index on datavalue for crosstab
        
        final Collection<Integer> intersectingPeriodIds = ConversionUtils.getIdentifiers( Period.class, periodService.getIntersectionPeriods( periods ) );
        final Set<Integer> orgUnitChildrenIds = organisationUnitService.getOrganisationUnitHierarchy().getChildren( organisationUnitIds );
        final List<Integer> crossTabOrgUnitIds = new ArrayList<Integer>( orgUnitChildrenIds );
        
        final String key = crossTabService.createCrossTabTable( crossTabOrgUnitIds );

        final List<DataElementOperand> operandList = new ArrayList<DataElementOperand>( allOperands );
        Collections.shuffle( operandList );
        
        final List<List<DataElementOperand>> operandPages = new PaginatedList<DataElementOperand>( operandList ).setNumberOfPages( cpuCores ).getPages();
        
        List<Future<?>> crossTabFutures = new ArrayList<Future<?>>();
        
        for ( List<DataElementOperand> operandPage : operandPages )
        {
            crossTabFutures.add( crossTabService.populateCrossTabTable( operandPage, intersectingPeriodIds, crossTabOrgUnitIds, key ) );
        }

        ConcurrentUtils.waitForCompletion( crossTabFutures );
        
        clock.logTime( "Populated crosstab table, " + SystemUtils.getMemoryString() );
        notifier.notify( id, DATAMART, "Exporting data element data" );

        final boolean isDataElements = true;
        
        final boolean isIndicators = indicators != null && indicators.size() > 0;

        final int groupLevel = (Integer) systemSettingManager.getSystemSetting( KEY_ORGUNITGROUPSET_AGG_LEVEL, DEFAULT_ORGUNITGROUPSET_AGG_LEVEL );
        
        final boolean isGroups = organisationUnitGroups != null && organisationUnitGroups.size() > 0 && groupLevel > 0;

        if ( isDataElements )
        {
            // -----------------------------------------------------------------
            // 1. Export data element values
            // -----------------------------------------------------------------
            
            if ( operandList.size() > 0 )
            {
                final OrganisationUnitHierarchy hierarchy = organisationUnitService.getOrganisationUnitHierarchy().prepareChildren( organisationUnits );
                
                List<Future<?>> futures = new ArrayList<Future<?>>();
                
                for ( List<DataElementOperand> operandPage : operandPages )
                {
                    futures.add( dataElementDataMart.exportDataValues( operandPage, periods, organisationUnits, 
                        null, hierarchy, AggregatedDataValueTempBatchHandler.class, key ) );
                }
                
                ConcurrentUtils.waitForCompletion( futures );
            }
            
            clock.logTime( "Exported values for data element operands (" + operandList.size() + "), pages: " + operandPages.size() + ", " + SystemUtils.getMemoryString() );
            notifier.notify( id, DATAMART, "Dropping data element index" );

            // -----------------------------------------------------------------
            // 2. Drop data element index
            // -----------------------------------------------------------------
    
            dataMartManager.dropDataValueIndex();
            
            clock.logTime( "Dropped data element index" );
            notifier.notify( id, DATAMART, "Deleting existing data element data" );
            
            // -----------------------------------------------------------------
            // 3. Delete existing aggregated data values
            // -----------------------------------------------------------------
    
            dataMartManager.deleteAggregatedDataValues( periodIds );
            
            clock.logTime( "Deleted existing data element data" );
            notifier.notify( id, DATAMART, "Copying data element data from temporary table" );
    
            // -----------------------------------------------------------------
            // 4. Copy aggregated data values from temporary table
            // -----------------------------------------------------------------
    
            dataMartManager.copyAggregatedDataValuesFromTemp();
            
            clock.logTime( "Copied data element data from temporary table" );
            notifier.notify( id, DATAMART, "Creating data element index" );
    
            // -----------------------------------------------------------------
            // 5. Create data element index
            // -----------------------------------------------------------------
    
            dataMartManager.createDataValueIndex();
    
            clock.logTime( "Created data element index" );
        }

        if ( isGroups && isDataElements )
        {
            // -----------------------------------------------------------------
            // 1. Export data element values
            // -----------------------------------------------------------------

            notifier.notify( id, DATAMART, "Exporting data element org unit data" );
            
            Collection<OrganisationUnit> groupOrganisationUnits = new HashSet<OrganisationUnit>( organisationUnits );
            
            FilterUtils.filter( groupOrganisationUnits, new OrganisationUnitAboveOrEqualToLevelFilter( groupLevel ) );
            
            if ( operandList.size() > 0 )
            {
                final OrganisationUnitHierarchy hierarchy = organisationUnitService.getOrganisationUnitHierarchy().prepareChildren( groupOrganisationUnits, organisationUnitGroups );
                
                List<Future<?>> futures = new ArrayList<Future<?>>();
                
                for ( List<DataElementOperand> operandPage : operandPages )
                {
                    futures.add( dataElementDataMart.exportDataValues( operandPage, periods, groupOrganisationUnits, 
                        organisationUnitGroups, hierarchy, AggregatedOrgUnitDataValueTempBatchHandler.class, key ) );
                }

                ConcurrentUtils.waitForCompletion( futures );
            }
            
            clock.logTime( "Exported values for data element operands (" + operandList.size() + "), pages: " + operandPages.size()  + ", " + SystemUtils.getMemoryString() );
            notifier.notify( id, DATAMART, "Dropping data element data indexes" );

            // -----------------------------------------------------------------
            // 2. Drop data element index
            // -----------------------------------------------------------------

            dataMartManager.dropOrgUnitDataValueIndex();

            clock.logTime( "Dropped org unit data element index" );
            notifier.notify( id, DATAMART, "Deleting existing org unit data element data" );

            // -----------------------------------------------------------------
            // 3. Delete existing aggregated data values
            // -----------------------------------------------------------------

            dataMartManager.deleteAggregatedOrgUnitDataValues( periodIds );
            
            clock.logTime( "Deleted existing aggregated org unit datavalues" );
            notifier.notify( id, DATAMART, "Copying org unit data element data" );

            // -----------------------------------------------------------------
            // 4. Copy aggregated org unit data values from temporary table
            // -----------------------------------------------------------------

            dataMartManager.copyAggregatedOrgUnitDataValuesFromTemp();
            
            clock.logTime( "Copied org unit data element data from temporary table" );
            notifier.notify( id, DATAMART, "Creating org unit data element index" );

            // -----------------------------------------------------------------
            // 5. Create org unit data element index
            // -----------------------------------------------------------------

            dataMartManager.createOrgUnitDataValueIndex();

            clock.logTime( "Created org unit data element index" );
            notifier.notify( id, DATAMART, "Exporting data for org unit indicator data" );
        }

        crossTabService.dropCrossTabTable( key );
        
        List<List<OrganisationUnit>> organisationUnitPages = new PaginatedList<OrganisationUnit>( organisationUnits ).setNumberOfPages( cpuCores ).getPages();
        
        if ( isIndicators )
        {
            // -----------------------------------------------------------------
            // 1. Create and populate aggregated data cache
            // -----------------------------------------------------------------

            notifier.notify( id, DATAMART, "Populating aggregated data cache" );
            
            crossTabService.createAggregatedDataCache( indicatorOperands, key );            

            List<Future<?>> aggregatedDataCacheFutures = new ArrayList<Future<?>>();

            for ( List<OrganisationUnit> organisationUnitPage : organisationUnitPages )
            {
                aggregatedDataCacheFutures.add( crossTabService.populateAggregatedDataCache( 
                    indicatorOperands, periods, organisationUnitPage, key ) );
            }

            ConcurrentUtils.waitForCompletion( aggregatedDataCacheFutures );
        
            clock.logTime( "Created aggregated data cache, number of indicator operands: " + indicatorOperands.size() );
            notifier.notify( id, DATAMART, "Exporting indicator data" );
            
            // -----------------------------------------------------------------
            // 2. Export indicator values
            // -----------------------------------------------------------------

            List<Future<?>> futures = new ArrayList<Future<?>>();

            for ( List<OrganisationUnit> organisationUnitPage : organisationUnitPages )
            {
                futures.add( indicatorDataMart.exportIndicatorValues( indicators, periods, organisationUnitPage,
                    null, indicatorOperands, AggregatedIndicatorValueTempBatchHandler.class, key ) );
            }

            ConcurrentUtils.waitForCompletion( futures );
        
            clock.logTime( "Exported values for indicators (" + indicators.size() + "), pages: " + organisationUnitPages.size() + ", " + SystemUtils.getMemoryString() );
            notifier.notify( id, DATAMART, "Dropping indicator index" );
            
            // -----------------------------------------------------------------
            // 3. Drop aggregated data cache and indicator index
            // -----------------------------------------------------------------
    
            crossTabService.dropAggregatedDataCache( key );
            dataMartManager.dropIndicatorValueIndex();
    
            clock.logTime( "Dropped indicator index, " + SystemUtils.getMemoryString() );
            notifier.notify( id, DATAMART, "Deleting existing indicator data" );
    
            // -----------------------------------------------------------------
            // 4. Delete existing aggregated indicator values
            // -----------------------------------------------------------------
    
            dataMartManager.deleteAggregatedIndicatorValues( periodIds );
            
            clock.logTime( "Deleted existing indicator data" );
            notifier.notify( id, DATAMART, "Copying indicator data from temporary table" );
    
            // -----------------------------------------------------------------
            // 5. Copy aggregated data values from temporary table
            // -----------------------------------------------------------------
    
            dataMartManager.copyAggregatedIndicatorValuesFromTemp();
            
            clock.logTime( "Copied indicator data from temporary table" );
            notifier.notify( id, DATAMART, "Creating indicator index" );
            
            // -----------------------------------------------------------------
            // 6. Create indicator index
            // -----------------------------------------------------------------
            
            dataMartManager.createIndicatorValueIndex();
            
            clock.logTime( "Created indicator index" );
        }

        if ( isGroups && isIndicators )
        {
            // -----------------------------------------------------------------
            // 1. Create aggregated data cache
            // -----------------------------------------------------------------

            notifier.notify( id, DATAMART, "Populating aggregated data cache" );
            
            crossTabService.createAggregatedOrgUnitDataCache( indicatorOperands, key );

            List<Future<?>> aggregatedDataCacheFutures = new ArrayList<Future<?>>();

            for ( List<OrganisationUnit> organisationUnitPage : organisationUnitPages )
            {
                aggregatedDataCacheFutures.add( crossTabService.populateAggregatedOrgUnitDataCache( 
                    indicatorOperands, periods, organisationUnitPage, organisationUnitGroups, key ) );
            }

            ConcurrentUtils.waitForCompletion( aggregatedDataCacheFutures );
        
            clock.logTime( "Created aggregated org unit data cache" );
            notifier.notify( id, DATAMART, "Exporting org unit indicator data" );

            // -----------------------------------------------------------------
            // 2. Export indicator values
            // -----------------------------------------------------------------

            List<Future<?>> futures = new ArrayList<Future<?>>();

            for ( List<OrganisationUnit> organisationUnitPage : organisationUnitPages )
            {
                futures.add( indicatorDataMart.exportIndicatorValues( indicators, periods, organisationUnitPage,
                    organisationUnitGroups, indicatorOperands, AggregatedOrgUnitIndicatorValueTempBatchHandler.class, key ) );
            }

            ConcurrentUtils.waitForCompletion( futures );
        
            clock.logTime( "Exported values for indicators (" + indicators.size() + "), pages: " + organisationUnitPages.size() + ", " + SystemUtils.getMemoryString() );
            notifier.notify( id, DATAMART, "Dropping org unit indicator index" );

            // -----------------------------------------------------------------
            // 3. Drop aggregated data cache and indicator index
            // -----------------------------------------------------------------

            crossTabService.dropAggregatedOrgUnitDataCache( key );
            dataMartManager.dropOrgUnitIndicatorValueIndex();
                        
            clock.logTime( "Dropped org unit indicator index, " + SystemUtils.getMemoryString() );
            notifier.notify( id, DATAMART, "Deleting existing org unit indicator data" );

            // -----------------------------------------------------------------
            // 4. Delete existing aggregated indicator values
            // -----------------------------------------------------------------

            dataMartManager.deleteAggregatedOrgUnitIndicatorValues( periodIds );
            
            clock.logTime( "Deleted existing aggregated org unit indicatorvalues" );
            notifier.notify( id, DATAMART, "Copying org unit indicator data from temporary table" );

            // -----------------------------------------------------------------
            // 5. Copy aggregated org unit indicator values from temp table
            // -----------------------------------------------------------------

            dataMartManager.copyAggregatedOrgUnitIndicatorValuesFromTemp();
            
            clock.logTime( "Copied org unit indicator data from temporary table" );
            notifier.notify( id, DATAMART, "Creating org unit indicator indexes" );
            
            // -----------------------------------------------------------------
            // 6. Create org unit indicator index
            // -----------------------------------------------------------------

            dataMartManager.createOrgUnitIndicatorValueIndex();
            
            clock.logTime( "Created org unit indicator index" );
        }
        
        // ---------------------------------------------------------------------
        // Drop temporary tables
        // ---------------------------------------------------------------------

        dataMartManager.dropTempAggregatedTables();
        
        clock.logTime( "Dropped crosstab table" );
        clock.logTime( "Data mart export process completed" );
        notifier.notify( id, DATAMART, INFO, "Data mart process completed", true );
    }
}
