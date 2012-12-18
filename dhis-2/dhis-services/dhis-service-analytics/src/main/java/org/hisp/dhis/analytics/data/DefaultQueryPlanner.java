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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.analytics.table.PartitionUtils;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.ListMap;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.system.util.PaginatedList;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultQueryPlanner
    implements QueryPlanner
{
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    // -------------------------------------------------------------------------
    // DefaultQueryPlanner implementation
    // -------------------------------------------------------------------------
    
    public List<DataQueryParams> planQuery( DataQueryParams params, int optimalQueries )
    {
        // ---------------------------------------------------------------------
        // Group queries by partition, period type and organisation unit level
        // ---------------------------------------------------------------------
        
        params = new DataQueryParams( params );
        
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();
        
        List<DataQueryParams> groupedByPartition = groupByPartition( params );
        
        for ( DataQueryParams byPartition : groupedByPartition )
        {
            List<DataQueryParams> groupedByPeriodType = groupByPeriodType( byPartition );
            
            for ( DataQueryParams byPeriodType : groupedByPeriodType )
            {
                List<DataQueryParams> groupedByOrgUnitLevel = groupByOrgUnitLevel( byPeriodType );
                
                for ( DataQueryParams byOrgUnitLevel : groupedByOrgUnitLevel )
                {
                    byOrgUnitLevel.setTableName( byPartition.getTableName() );
                    byOrgUnitLevel.setPeriodType( byPeriodType.getPeriodType() );
                    
                    queries.add( byOrgUnitLevel );
                }
            }
        }

        if ( queries.size() >= optimalQueries )
        {
            return queries;
        }

        // ---------------------------------------------------------------------
        // Group by organisation unit
        // ---------------------------------------------------------------------
        
        queries = splitByDimension( queries, DataQueryParams.ORGUNIT_DIM_ID, optimalQueries );

        if ( queries.size() >= optimalQueries )
        {
            return queries;
        }

        // ---------------------------------------------------------------------
        // Group by data element
        // ---------------------------------------------------------------------
        
        queries = splitByDimension( queries, DataQueryParams.DATAELEMENT_DIM_ID, optimalQueries );

        if ( queries.size() >= optimalQueries )
        {
            return queries;
        }

        // ---------------------------------------------------------------------
        // Group by dimensions
        // ---------------------------------------------------------------------

        return splitByBestDimension( queries, optimalQueries );
    }
    
    public String getPartitionDimension( DataQueryParams params, int optimalQueries )
    {
        SortedMap<String, List<String>> map = params.getDimensionValuesMap();
        
        for ( String dimension : map.keySet() )
        {
            if ( map.get( dimension ).size() >= optimalQueries )
            {
                return dimension;
            }
        }
        
        return params.getLargestDimension();
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
    private List<DataQueryParams> splitByDimension( List<DataQueryParams> queries, String dimension, int optimalQueries )
    {
        int pageNo = MathUtils.divideToCeil( optimalQueries, queries.size() );
        
        List<DataQueryParams> subQueries = new ArrayList<DataQueryParams>();
        
        for ( DataQueryParams query : queries )
        {
            List<String> values = query.getDimension( dimension );

            List<List<String>> valuePages = new PaginatedList<String>( values ).setNumberOfPages( pageNo ).getPages();
            
            for ( List<String> valuePage : valuePages )
            {
                DataQueryParams subQuery = new DataQueryParams( query );
                subQuery.setDimension( dimension, valuePage );
                subQueries.add( subQuery );
            }            
        }

        return subQueries;
    }
    
    /**
     * Splits the given list of queries in sub queries on the most favorable
     * dimension. This is determined by first checking if any dimensions will
     * satisfy the optimal number of queries, if not the dimension with most
     * options is selected.
     */
    private List<DataQueryParams> splitByBestDimension( List<DataQueryParams> queries, int optimalQueries )
    {
        int pageNo = MathUtils.divideToCeil( optimalQueries, queries.size() );
        
        List<DataQueryParams> subQueries = new ArrayList<DataQueryParams>();
        
        for ( DataQueryParams query : queries )
        {
            String dimension = getPartitionDimension( query, pageNo );
            
            List<String> values = query.getDimension( dimension );
            
            List<List<String>> valuePages = new PaginatedList<String>( values ).setNumberOfPages( pageNo ).getPages();
        
            for ( List<String> valuePage : valuePages )
            {
                DataQueryParams subQuery = new DataQueryParams( query );
                subQuery.setDimension( dimension, valuePage );
                subQueries.add( subQuery );
            }
        }
        
        return subQueries;
    }
    
    /**
     * Groups the given query into sub queries based on its periods and which 
     * partition it should be executed against. Sets the partition table name on
     * each query.
     */
    private List<DataQueryParams> groupByPartition( DataQueryParams params )
    {
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();

        ListMap<String, String> tablePeriodMap = PartitionUtils.getTablePeriodMap( params.getPeriods() );
        
        for ( String tableName : tablePeriodMap.keySet() )
        {
            DataQueryParams query = new DataQueryParams( params );
            query.setPeriods( tablePeriodMap.get( tableName ) );
            query.setTableName( tableName );
            queries.add( query );            
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
}
