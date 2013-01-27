package org.hisp.dhis.analytics.data;

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

import static org.hisp.dhis.analytics.AnalyticsTableManager.ANALYTICS_TABLE_NAME;
import static org.hisp.dhis.analytics.AnalyticsTableManager.COMPLETENESS_TABLE_NAME;
import static org.hisp.dhis.analytics.DataQueryParams.DATAELEMENT_DIM_ID;
import static org.hisp.dhis.analytics.DataQueryParams.DATASET_DIM_ID;
import static org.hisp.dhis.analytics.DataQueryParams.DIMENSION_SEP;
import static org.hisp.dhis.analytics.DataQueryParams.INDICATOR_DIM_ID;
import static org.hisp.dhis.analytics.DataQueryParams.ORGUNIT_DIM_ID;
import static org.hisp.dhis.analytics.DataQueryParams.PERIOD_DIM_ID;
import static org.hisp.dhis.analytics.DataQueryParams.getDimensionFromParam;
import static org.hisp.dhis.analytics.DataQueryParams.getDimensionOptionsFromParam;
import static org.hisp.dhis.common.IdentifiableObjectUtils.asList;
import static org.hisp.dhis.common.IdentifiableObjectUtils.asTypedList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.hisp.dhis.analytics.AggregationType;
import org.hisp.dhis.analytics.AnalyticsManager;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.Dimension;
import org.hisp.dhis.analytics.DimensionOption;
import org.hisp.dhis.analytics.DimensionType;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.RelativePeriodEnum;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.system.util.SystemUtils;
import org.hisp.dhis.system.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class DefaultAnalyticsService
    implements AnalyticsService
{
    private static final String VALUE_HEADER_NAME = "Value";
    
    //TODO completeness
    
    @Autowired
    private AnalyticsManager analyticsManager;
    
    @Autowired
    private QueryPlanner queryPlanner;
    
    @Autowired
    private IndicatorService indicatorService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    @Autowired
    private ExpressionService expressionService;
    
    @Autowired
    private ConstantService constantService;

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    public Grid getAggregatedDataValues( DataQueryParams params ) throws Exception
    {
        Grid grid = new ListGrid();

        // ---------------------------------------------------------------------
        // Headers and meta-data
        // ---------------------------------------------------------------------

        grid.setMetaData( params.getUidNameMap() );
        
        for ( Dimension col : params.getSelectDimensions() )
        {
            grid.addHeader( new GridHeader( col.getDimensionName(), col.getDimension(), String.class.getName(), false, true ) );
        }
        
        grid.addHeader( new GridHeader( DataQueryParams.VALUE_ID, VALUE_HEADER_NAME, Double.class.getName(), false, false ) );

        // ---------------------------------------------------------------------
        // Indicators
        // ---------------------------------------------------------------------

        if ( params.getIndicators() != null )
        {         
            int indicatorIndex = params.getDataElementOrIndicatorDimensionIndex();

            List<Indicator> indicators = asTypedList( params.getIndicators() );
            
            DataQueryParams dataSourceParams = setDataElementsFromIndicators( params );

            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( dataSourceParams, ANALYTICS_TABLE_NAME );

            Map<String, Map<DataElementOperand, Double>> permutationOperandValueMap = dataSourceParams.getPermutationOperandValueMap( aggregatedDataMap );

            List<List<DimensionOption>> dimensionOptionPermutations = dataSourceParams.getDimensionOptionPermutations();

            Map<String, Double> constantMap = constantService.getConstantMap();

            for ( Indicator indicator : indicators )
            {
                for ( List<DimensionOption> options : dimensionOptionPermutations )
                {
                    String permKey = DimensionOption.asOptionKey( options );

                    Map<DataElementOperand, Double> valueMap = permutationOperandValueMap.get( permKey );

                    if ( valueMap != null )
                    {
                        Period period = (Period) DimensionOption.getPeriodOption( options );
                        
                        Assert.notNull( period );
                        
                        Double value = expressionService.getIndicatorValue( indicator, period, valueMap, constantMap, null );
                        
                        if ( value != null )
                        {
                            List<DimensionOption> row = new ArrayList<DimensionOption>( options );
                            
                            row.add( indicatorIndex, new DimensionOption( INDICATOR_DIM_ID, indicator ) );
                            
                            grid.addRow();
                            grid.addValues( DimensionOption.getOptionIdentifiers( row ) );
                            grid.addValue( value );
                        }
                    }                    
                }
            }
        }

        // ---------------------------------------------------------------------
        // Data elements
        // ---------------------------------------------------------------------

        if ( params.getDataElements() != null )
        {
            DataQueryParams dataSourceParams = new DataQueryParams( params );
            dataSourceParams.removeDimension( INDICATOR_DIM_ID );
            dataSourceParams.removeDimension( DATASET_DIM_ID );
            
            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( dataSourceParams, ANALYTICS_TABLE_NAME );
            
            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                grid.addRow();
                grid.addValues( entry.getKey().split( DIMENSION_SEP ) );
                grid.addValue( entry.getValue() );
            }
        }

        // ---------------------------------------------------------------------
        // Data sets / completeness
        // ---------------------------------------------------------------------

        if ( params.getDataSets() != null )
        {
            DataQueryParams dataSourceParams = new DataQueryParams( params );
            dataSourceParams.removeDimension( INDICATOR_DIM_ID );
            dataSourceParams.removeDimension( DATAELEMENT_DIM_ID );
            dataSourceParams.setCategories( false );
            dataSourceParams.setAggregationType( AggregationType.COUNT_AGGREGATION );

            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( dataSourceParams, COMPLETENESS_TABLE_NAME );

            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                grid.addRow();
                grid.addValues( entry.getKey().split( DIMENSION_SEP ) );
                grid.addValue( entry.getValue() );
            }
        }

        // ---------------------------------------------------------------------
        // Other dimensions
        // ---------------------------------------------------------------------

        if ( params.getIndicators() == null && params.getDataElements() == null && params.getDataSets() == null )
        {
            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( new DataQueryParams( params ), ANALYTICS_TABLE_NAME );
            
            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                grid.addRow();
                grid.addValues( entry.getKey().split( DIMENSION_SEP ) );
                grid.addValue( entry.getValue() );
            }
        }        
        
        return grid;
    }
    
    public Map<String, Double> getAggregatedDataValueMap( DataQueryParams params, String tableName ) throws Exception
    {
        Timer t = new Timer().start();

        int optimalQueries = MathUtils.getWithin( SystemUtils.getCpuCores(), 1, 6 );
        
        List<DataQueryParams> queries = queryPlanner.planQuery( params, optimalQueries, tableName );
        
        t.getTime( "Planned query for optimal: " + optimalQueries + ", got: " + queries.size() );
        
        List<Future<Map<String, Double>>> futures = new ArrayList<Future<Map<String, Double>>>();
        
        Map<String, Double> map = new HashMap<String, Double>();
        
        for ( DataQueryParams query : queries )
        {
            futures.add( analyticsManager.getAggregatedDataValues( query ) );
        }
        
        for ( Future<Map<String, Double>> future : futures )
        {
            Map<String, Double> taskValues = future.get();
            
            if ( taskValues != null )
            {
                map.putAll( taskValues );
            }
        }
        
        t.getTime( "Got aggregated values" );
        
        return map;
    }
    
    public DataQueryParams getFromUrl( Set<String> dimensionParams, Set<String> filterParams, boolean categories, I18nFormat format )
    {
        DataQueryParams params = new DataQueryParams();

        params.setCategories( categories );
        
        if ( dimensionParams != null && !dimensionParams.isEmpty() )
        {
            for ( String param : dimensionParams )
            {
                String dimension = getDimensionFromParam( param );
                List<String> options = getDimensionOptionsFromParam( param );
                
                if ( dimension != null && options != null )
                {
                    params.getDimensions().add( getDimension( dimension, options, format ) );
                }
            }
        }

        if ( filterParams != null && !filterParams.isEmpty() )
        {
            for ( String param : filterParams )
            {
                String dimension = DataQueryParams.getDimensionFromParam( param );
                List<String> options = DataQueryParams.getDimensionOptionsFromParam( param );
                
                if ( dimension != null && options != null )
                {
                    params.getFilters().add( getDimension( dimension, options, format ) );
                }
            }
        }

        return params;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private Dimension getDimension( String dimension, List<String> options, I18nFormat format )
    {
        if ( INDICATOR_DIM_ID.equals( dimension ) )
        {
            return new Dimension( dimension, DimensionType.INDICATOR, asList( indicatorService.getIndicatorsByUid( options ) ) );
        }
        else if ( DATAELEMENT_DIM_ID.equals( dimension ) )
        {
            return new Dimension( dimension, DimensionType.DATAELEMENT, asList( dataElementService.getDataElementsByUid( options ) ) );
        }
        else if ( DATASET_DIM_ID.equals( dimension ) )
        {
            return new Dimension( dimension, DimensionType.DATASET, asList( dataSetService.getDataSetsByUid( options ) ) );
        }
        else if ( ORGUNIT_DIM_ID.equals( dimension ) )
        {
            return new Dimension( dimension, DimensionType.ORGANISATIONUNIT, asList( organisationUnitService.getOrganisationUnitsByUid( options ) ) );
        }
        else if ( PERIOD_DIM_ID.equals( dimension ) )
        {
            List<IdentifiableObject> list = new ArrayList<IdentifiableObject>();
                        
            periodLoop : for ( String isoPeriod : options )
            {
                Period period = PeriodType.getPeriodFromIsoString( isoPeriod );
                
                if ( period != null )
                {
                    period.setName( format != null ? format.formatPeriod( period ) : null );
                    list.add( period );
                    continue periodLoop;
                }
                
                if ( RelativePeriodEnum.contains( isoPeriod ) )
                {
                    RelativePeriodEnum relativePeriod = RelativePeriodEnum.valueOf( isoPeriod );
                    list.addAll( RelativePeriods.getRelativePeriodsFromEnum( relativePeriod, format, true ) );
                    continue periodLoop;
                }
            }
            
            return new Dimension( dimension, DimensionType.PERIOD, list );
        }
        
        OrganisationUnitGroupSet orgUnitGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( dimension );
            
        if ( orgUnitGroupSet != null )
        {
            return new Dimension( dimension, DimensionType.ORGANISATIONUNIT_GROUPSET, asList( organisationUnitGroupService.getOrganisationUnitGroupsByUid( options ) ) );
        }
        
        DataElementGroupSet dataElementGroupSet = dataElementService.getDataElementGroupSet( dimension );
        
        if ( dataElementGroupSet != null )
        {
            return new Dimension( dimension, DimensionType.DATAELEMENT_GROUPSET, asList( dataElementService.getDataElementGroupsByUid( options ) ) );
        }
        
        return null;
    }
    
    private DataQueryParams setDataElementsFromIndicators( DataQueryParams params )
    {
        DataQueryParams immutableParams = new DataQueryParams( params );
        
        List<Indicator> indicators = asTypedList( immutableParams.getIndicators() );            
        List<IdentifiableObject> dataElements = asList( expressionService.getDataElementsInIndicators( indicators ) );
        
        immutableParams.setDataElements( dataElements );
        immutableParams.removeDimension( INDICATOR_DIM_ID );
        immutableParams.removeDimension( DATASET_DIM_ID );
        immutableParams.setCategories( true );
        
        return immutableParams;
    }
}
