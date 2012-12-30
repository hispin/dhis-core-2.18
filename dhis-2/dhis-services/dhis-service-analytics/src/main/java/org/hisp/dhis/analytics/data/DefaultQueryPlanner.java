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

import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_AVERAGE;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_SUM;
import static org.hisp.dhis.analytics.AggregationType.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.analytics.AggregationType;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.analytics.table.PartitionUtils;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.ListMap;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.system.util.PaginatedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class DefaultQueryPlanner
    implements QueryPlanner
{
    //TODO call getLevelOrgUnitMap once
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private DataElementService dataElementService;
    
    // -------------------------------------------------------------------------
    // DefaultQueryPlanner implementation
    // -------------------------------------------------------------------------
    
    public List<DataQueryParams> planQuery( DataQueryParams params, int optimalQueries )
    {
        Assert.isTrue( !params.getDimensions().isEmpty() );
        Assert.isTrue( params.dimensionsAsFilters().isEmpty() );
        Assert.isTrue( params.hasPeriods() );

        // ---------------------------------------------------------------------
        // Group queries by partition, period type and organisation unit level
        // ---------------------------------------------------------------------
        
        params = new DataQueryParams( params );

        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();
        
        List<DataQueryParams> groupedByPartition = groupByPartition( params );
        
        for ( DataQueryParams byPartition : groupedByPartition )
        {
            List<DataQueryParams> groupedByOrgUnitLevel = groupByOrgUnitLevel( byPartition );
            
            for ( DataQueryParams byOrgUnitLevel : groupedByOrgUnitLevel )
            {
                List<DataQueryParams> groupedByPeriodType = groupByPeriodType( byOrgUnitLevel );
                
                for ( DataQueryParams byPeriodType : groupedByPeriodType )
                {
                    List<DataQueryParams> groupedByAggregationType = groupByAggregationType( byPeriodType );
                    
                    for ( DataQueryParams byAggregationType : groupedByAggregationType )
                    {
                        if ( AVERAGE_DISAGGREGATION.equals( byAggregationType.getAggregationType() ) )
                        {
                            List<DataQueryParams> groupedByDataPeriodType = groupByDataPeriodType( byAggregationType );
                            
                            for ( DataQueryParams byDataPeriodType : groupedByDataPeriodType )
                            {
                                byDataPeriodType.setTableName( byPartition.getTableName() );
                                byDataPeriodType.setOrganisationUnitLevel( byOrgUnitLevel.getOrganisationUnitLevel() );
                                byDataPeriodType.setPeriodType( byPeriodType.getPeriodType() );
                                byDataPeriodType.setAggregationType( byAggregationType.getAggregationType() );
                                
                                queries.add( byDataPeriodType );
                            }
                        }
                        else
                        {
                            byAggregationType.setTableName( byPartition.getTableName() );
                            byAggregationType.setOrganisationUnitLevel( byOrgUnitLevel.getOrganisationUnitLevel() );
                            byAggregationType.setPeriodType( byPeriodType.getPeriodType() );
                            
                            queries.add( byAggregationType );
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // Set filters for each period type and organisation unit level
        // ---------------------------------------------------------------------
        
        queries = setFilterByPeriodType( queries );
        queries = setFilterByOrgUnitLevel( queries );
        
        if ( queries.size() >= optimalQueries )
        {
            return queries;
        }

        // ---------------------------------------------------------------------
        // Group by organisation unit
        // ---------------------------------------------------------------------
        
        queries = splitByDimensionOrFilter( queries, DataQueryParams.ORGUNIT_DIM_ID, optimalQueries );

        if ( queries.size() >= optimalQueries )
        {
            return queries;
        }

        // ---------------------------------------------------------------------
        // Group by data element
        // ---------------------------------------------------------------------
        
        return splitByDimensionOrFilter( queries, DataQueryParams.DATAELEMENT_DIM_ID, optimalQueries );
    }
        
    public boolean canQueryFromDataMart( DataQueryParams params )
    {
        return true;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    /**
     * Splits the given list of queries in sub queries on the given dimension.
     */
    private List<DataQueryParams> splitByDimensionOrFilter( List<DataQueryParams> queries, String dimension, int optimalQueries )
    {
        int optimalForSubQuery = MathUtils.divideToCeil( optimalQueries, queries.size() );
        
        List<DataQueryParams> subQueries = new ArrayList<DataQueryParams>();
        
        for ( DataQueryParams query : queries )
        {
            List<String> values = query.getDimensionOrFilter( dimension );

            if ( values == null || values.isEmpty() )
            {
                subQueries.add( new DataQueryParams( query ) );
                continue;
            }
            
            List<List<String>> valuePages = new PaginatedList<String>( values ).setNumberOfPages( optimalForSubQuery ).getPages();
            
            for ( List<String> valuePage : valuePages )
            {
                DataQueryParams subQuery = new DataQueryParams( query );
                subQuery.resetDimensionOrFilter( dimension, valuePage );
                subQueries.add( subQuery );
            }
        }

        return subQueries;
    }
        
    /**
     * Groups the given query into sub queries based on its periods and which 
     * partition it should be executed against. Sets the partition table name on
     * each query. Queries are grouped based on both dimensions and filters.
     */
    private List<DataQueryParams> groupByPartition( DataQueryParams params )
    {
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();

        if ( params.getPeriods() != null && !params.getPeriods().isEmpty() )
        {
            ListMap<String, String> tablePeriodMap = PartitionUtils.getTablePeriodMap( params.getPeriods() );
            
            for ( String tableName : tablePeriodMap.keySet() )
            {
                DataQueryParams query = new DataQueryParams( params );
                query.setPeriods( tablePeriodMap.get( tableName ) );
                query.setTableName( tableName );
                queries.add( query );            
            }
        }
        else
        {
            ListMap<String, String> tablePeriodMap = PartitionUtils.getTablePeriodMap( params.getFilterPeriods() );
            
            for ( String tableName : tablePeriodMap.keySet() )
            {
                DataQueryParams query = new DataQueryParams( params );
                query.setFilterPeriods( tablePeriodMap.get( tableName ) );
                query.setTableName( tableName );
                queries.add( query );            
            }
        }
        
        return queries;
    }
    
    /**
     * Groups the given query into sub queries based on the period type of its
     * periods. Sets the period type name on each query.
     */
    private List<DataQueryParams> groupByPeriodType( DataQueryParams params )
    {
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();

        if ( params.getPeriods() == null || params.getPeriods().isEmpty() )
        {
            queries.add( new DataQueryParams( params ) );
            return queries;
        }
        
        ListMap<String, String> periodTypePeriodMap = getPeriodTypePeriodMap( params.getPeriods() );

        for ( String periodType : periodTypePeriodMap.keySet() )
        {
            DataQueryParams query = new DataQueryParams( params );
            query.setPeriods( periodTypePeriodMap.get( periodType ) );
            query.setPeriodType( periodType );
            queries.add( query );            
        }
        
        return queries;        
    }
    
    /**
     * Groups the given query into sub queries based on the level of its organisation 
     * units. Sets the organisation unit level on each query.
     */
    private List<DataQueryParams> groupByOrgUnitLevel( DataQueryParams params )
    {
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();

        if ( params.getOrganisationUnits() == null || params.getOrganisationUnits().isEmpty() )
        {
            queries.add( new DataQueryParams( params ) );
            return queries;
        }
        
        ListMap<Integer, String> levelOrgUnitMap = getLevelOrgUnitMap( params.getOrganisationUnits() );
        
        for ( Integer level : levelOrgUnitMap.keySet() )
        {
            DataQueryParams query = new DataQueryParams( params );
            query.setOrganisationUnits( levelOrgUnitMap.get( level ) );
            query.setOrganisationUnitLevel( level );
            queries.add( query );
        }
        
        return queries;    
    }
    
    /**
     * Groups the given query in sub queries based on the aggregation type of its
     * data elements. The aggregation type can be sum, average aggregation or
     * average disaggregation. Sum means that the data elements have sum aggregation
     * operator. Average aggregation means that the data elements have the average
     * aggregation operator and that the period type of the data elements have 
     * higher or equal frequency than the aggregation period type. Average disaggregation
     * means that the data elements have the average aggregation operator and
     * that the period type of the data elements have lower frequency than the
     * aggregation period type.
     */
    private List<DataQueryParams> groupByAggregationType( DataQueryParams params )
    {
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();

        if ( params.getDatElements() == null || params.getDatElements().isEmpty() )
        {
            queries.add( new DataQueryParams( params ) );
            return queries;
        }
     
        PeriodType periodType = PeriodType.getPeriodTypeByName( params.getPeriodType() );
        
        ListMap<AggregationType, String> aggregationTypeDataElementMap = getAggregationTypeDataElementMap( params.getDatElements(), periodType );
        
        for ( AggregationType aggregationType : aggregationTypeDataElementMap.keySet() )
        {
            DataQueryParams query = new DataQueryParams( params );
            query.setDataElements( aggregationTypeDataElementMap.get( aggregationType ) );
            query.setAggregationType( aggregationType );
            queries.add( query );
        }
        
        return queries;
    }
    
    /**
     * Groups the given query in sub queries based on the period type of its
     * data elements. Sets the data period type on each query.
     */
    private List<DataQueryParams> groupByDataPeriodType( DataQueryParams params )
    {
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();

        if ( params.getDatElements() == null || params.getDatElements().isEmpty() )
        {
            queries.add( new DataQueryParams( params ) );
            return queries;
        }
        
        ListMap<PeriodType, String> periodTypeDataElementMap = getPeriodTypeDataElementMap( params.getDatElements() );
        
        for ( PeriodType periodType : periodTypeDataElementMap.keySet() )
        {
            DataQueryParams query = new DataQueryParams( params );
            query.setDataElements( periodTypeDataElementMap.get( periodType ) );
            query.setDataPeriodType( periodType );
            queries.add( query );
        }
        
        return queries;
    }
    
    /**
     * Replaces the period filter with individual filters for each period type.
     */
    private List<DataQueryParams> setFilterByPeriodType( List<DataQueryParams> queries )
    {
        for ( DataQueryParams params : queries )
        {
            if ( params.getFilterPeriods() != null && !params.getFilterPeriods().isEmpty() )
            {
                params.getFilters().putAll( getPeriodTypePeriodMap( params.getFilterPeriods() ) );
                params.getFilters().remove( DataQueryParams.PERIOD_DIM_ID );
            }
        }
        
        return queries;
    }
    
    /**
     * Replaces the organisation unit filter with individual filters for each
     * organisation unit level.
     */
    private List<DataQueryParams> setFilterByOrgUnitLevel( List<DataQueryParams> queries )
    {
        for ( DataQueryParams params : queries )
        {
            if ( params.getFilterOrganisationUnits() != null && !params.getFilterOrganisationUnits().isEmpty() )
            {
                params.getFilters().putAll( getLevelColumnOrgUnitMap( params.getFilterOrganisationUnits() ) );
                params.getFilters().remove( DataQueryParams.ORGUNIT_DIM_ID );
            }
        }
        
        return queries;
    }
    
    /**
     * Creates a mapping between period type name and period for the given periods.
     */
    private ListMap<String, String> getPeriodTypePeriodMap( Collection<String> isoPeriods )
    {
        ListMap<String, String> map = new ListMap<String, String>();
        
        for ( String period : isoPeriods )
        {
            String periodTypeName = PeriodType.getPeriodTypeFromIsoString( period ).getName();
            
            map.putValue( periodTypeName, period );
        }
        
        return map;
    }
    
    /**
     * Creates a mapping between level and organisation unit for the given organisation
     * units.
     */
    private ListMap<Integer, String> getLevelOrgUnitMap( Collection<String> orgUnits )
    {
        ListMap<Integer, String> map = new ListMap<Integer, String>();
        
        for ( String orgUnit : orgUnits )
        {
            int level = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );
            
            map.putValue( level, orgUnit );
        }
        
        return map;
    }
    
    /**
     * Creates a mapping between the level column and organisation unit for the 
     * given organisation units.
     */
    private ListMap<String, String> getLevelColumnOrgUnitMap( Collection<String> orgUnits )
    {
        ListMap<String, String> map = new ListMap<String, String>();
        
        for ( String orgUnit : orgUnits )
        {
            int level = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );
            
            map.putValue( DataQueryParams.LEVEL_PREFIX + level, orgUnit );
        }
        
        return map;
    }
        
    /**
     * Creates a mapping between the aggregation type and data element for the
     * given data elements and period type.
     */
    private ListMap<AggregationType, String> getAggregationTypeDataElementMap( Collection<String> dataElements, PeriodType aggregationPeriodType )
    {
        ListMap<AggregationType, String> map = new ListMap<AggregationType, String>();
        
        for ( String element : dataElements )
        {
            DataElement dataElement = dataElementService.getDataElement( element );
            
            if ( AGGREGATION_OPERATOR_SUM.equals( dataElement.getAggregationOperator() ) )
            {
                map.putValue( AggregationType.SUM, element );
            }
            else if ( AGGREGATION_OPERATOR_AVERAGE.equals( dataElement.getAggregationOperator() ) )
            {
                PeriodType dataPeriodType = dataElement.getPeriodType();
                
                if ( dataPeriodType == null || aggregationPeriodType.getFrequencyOrder() >= dataPeriodType.getFrequencyOrder() )
                {
                    map.putValue( AggregationType.AVERAGE_AGGREGATION, element );
                }
                else
                {
                    map.putValue( AggregationType.AVERAGE_DISAGGREGATION, element );
                }
            }
        }
        
        return map;
    }

    /**
     * Creates a mapping between the period type and the data element for the
     * given data elements.
     */
    private ListMap<PeriodType, String> getPeriodTypeDataElementMap( Collection<String> dataElements )
    {
        ListMap<PeriodType, String> map = new ListMap<PeriodType, String>();
        
        for ( String element : dataElements )
        {
            DataElement dataElement = dataElementService.getDataElement( element );
            
            map.putValue( dataElement.getPeriodType(), element );
        }
        
        return map;
    }
}
