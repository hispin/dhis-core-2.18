package org.hisp.dhis.analytics.event.data;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PERIOD_DIM_ID;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.system.util.DateUtils.getMediumDateString;
import static org.hisp.dhis.system.util.TextUtils.getQuotedCommaDelimitedString;
import static org.hisp.dhis.system.util.TextUtils.removeLastComma;
import static org.hisp.dhis.system.util.TextUtils.removeLastOr;
import static org.hisp.dhis.system.util.TextUtils.trimEnd;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.event.EventAnalyticsManager;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.QueryFilter;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * TODO could use row_number() and filtering for paging, but not supported on MySQL.
 * 
 * @author Lars Helge Overland
 */
public class JdbcEventAnalyticsManager
    implements EventAnalyticsManager
{
    private static final Log log = LogFactory.getLog( JdbcEventAnalyticsManager.class );
    
    private static final String QUERY_ERR_MSG = "Query failed, likely because the requested analytics table does not exist";
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private StatementBuilder statementBuilder;
    
    // -------------------------------------------------------------------------
    // EventAnalyticsManager implementation
    // -------------------------------------------------------------------------

    @Override
    public Grid getAggregatedEventData( EventQueryParams params, Grid grid, int maxLimit )
    {
        String countClause = ( params.isProgramRegistration() && params.isUniqueInstances() ) ? "count(distinct tei)" : "count(psi)";
        
        String sql = "select " + countClause + " as value," + getSelectColumns( params ) + " ";

        // ---------------------------------------------------------------------
        // Criteria
        // ---------------------------------------------------------------------

        if ( params.spansMultiplePartitions() )
        {
            sql += getFromWhereMultiplePartitionsClause( params, Arrays.asList( "psi" ) );
        }
        else
        {
            sql += getFromWhereClause( params, params.getPartitions().getSinglePartition() );
        }

        // ---------------------------------------------------------------------
        // Group by
        // ---------------------------------------------------------------------

        sql += "group by " + getSelectColumns( params ) + " ";

        // ---------------------------------------------------------------------
        // Sort order
        // ---------------------------------------------------------------------

        if ( params.hasSortOrder() )
        {
            sql += "order by value " + params.getSortOrder().toString().toLowerCase() + " ";
        }
        
        // ---------------------------------------------------------------------
        // Limit, add one to max to enable later check against max limit
        // ---------------------------------------------------------------------

        if ( params.hasLimit() )
        {
            sql += "limit " + params.getLimit();
        }
        else if ( maxLimit > 0 )
        {
            sql += "limit " + ( maxLimit + 1 );
        }
        
        // ---------------------------------------------------------------------
        // Grid
        // ---------------------------------------------------------------------

        try
        {
            getAggregatedEventData( grid, params, sql );
        }
        catch ( BadSqlGrammarException ex )
        {
            log.info( QUERY_ERR_MSG, ex );
        }
        
        return grid;
    }
    
    private void getAggregatedEventData( Grid grid, EventQueryParams params, String sql )
    {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        log.debug( "Analytics event aggregate SQL: " + sql );
        
        while ( rowSet.next() )
        {
            int value = rowSet.getInt( "value" );
            
            grid.addRow();
            
            for ( DimensionalObject dimension : params.getDimensions() )
            {
                String dimensionValue = rowSet.getString( dimension.getDimensionName() );
                grid.addValue( dimensionValue );
            }
            
            for ( QueryItem queryItem : params.getItems() )
            {
                String itemValue = rowSet.getString( queryItem.getItem().getUid() );                
                grid.addValue( itemValue );
            }
            
            grid.addValue( value );
        }
    }
    
    @Override
    public Grid getEvents( EventQueryParams params, Grid grid, int maxLimit )
    {
        List<String> fixedCols = Arrays.asList( "psi", "ps", "executiondate", "longitude", "latitude", "ouname", "oucode" );
        
        String sql = "select " + getSelectString( fixedCols ) + getSelectColumns( params ) + " ";

        // ---------------------------------------------------------------------
        // Criteria
        // ---------------------------------------------------------------------

        if ( params.spansMultiplePartitions() )
        {
            sql += getFromWhereMultiplePartitionsClause( params, fixedCols );
        }
        else
        {
            sql += getFromWhereClause( params, params.getPartitions().getSinglePartition() );
        }
        
        // ---------------------------------------------------------------------
        // Sorting
        // ---------------------------------------------------------------------

        if ( params.isSorting() )
        {
            sql += "order by ";
        
            for ( String item : params.getAsc() )
            {
                sql += statementBuilder.columnQuote( item ) + " asc,";
            }
            
            for  ( String item : params.getDesc() )
            {
                sql += statementBuilder.columnQuote( item ) + " desc,";
            }
            
            sql = removeLastComma( sql ) + " ";
        }
        
        // ---------------------------------------------------------------------
        // Paging
        // ---------------------------------------------------------------------

        if ( params.isPaging() )
        {
            sql += "limit " + params.getPageSizeWithDefault() + " offset " + params.getOffset();
        }
        else if ( maxLimit > 0 )
        {
            sql += "limit " + ( maxLimit + 1 );
        }

        // ---------------------------------------------------------------------
        // Grid
        // ---------------------------------------------------------------------

        try
        {
            getEvents( grid, params, sql );
        }
        catch ( BadSqlGrammarException ex )
        {
            log.info( QUERY_ERR_MSG, ex );
        }
        
        return grid;
    }

    private void getEvents( Grid grid, EventQueryParams params, String sql )
    {
        int rowLength = grid.getHeaders().size();

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        log.debug( "Analytics event query SQL: " + sql );
z        
        while ( rowSet.next() )
        {
            grid.addRow();
            
            for ( int i = 0; i < rowLength; i++ )
            {
                int index = i + 1;
                
                grid.addValue( rowSet.getString( index ) );
            }
        }
    }
    
    @Override
    public int getEventCount( EventQueryParams params )
    {
        String sql = "select count(psi) ";
        
        if ( params.spansMultiplePartitions() )
        {
            sql += getFromWhereMultiplePartitionsClause( params, Arrays.asList( "psi" ) );
        }
        else
        {
            sql += getFromWhereClause( params, params.getPartitions().getSinglePartition() );
        }
        
        int count = 0;
        
        try
        {
            count = getEventCount( sql );          
        }
        catch ( BadSqlGrammarException ex )
        {
            log.info( QUERY_ERR_MSG, ex );
        }

        return count;
    }
    
    private int getEventCount( String sql )
    {
        int count = jdbcTemplate.queryForObject( sql, Integer.class );

        log.debug( "Analytics event count SQL: " + sql );
        
        return count;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Returns the dynamic select columns. Dimensions come first and query items
     * second.
     */
    private String getSelectColumns( EventQueryParams params )
    {
        String sql = "";
        
        for ( DimensionalObject dimension : params.getDimensions() )
        {
            sql += statementBuilder.columnQuote( dimension.getDimensionName() ) + ",";
        }
                
        for ( QueryItem queryItem : params.getItems() )
        {
            IdentifiableObject item = queryItem.getItem();
            
            sql += statementBuilder.columnQuote( item.getUid() ) + ",";
        }
        
        return removeLastComma( sql );
    }

    private String getFromWhereMultiplePartitionsClause( EventQueryParams params, List<String> fixedColumns )
    {
        String sql = "from (";
        
        for ( String partition : params.getPartitions().getPartitions() )
        {
            sql += "select " + getSelectString( fixedColumns ) + getSelectColumns( params );
            
            sql += " " + getFromWhereClause( params, partition );
            
            sql += "union all ";
        }

        sql = trimEnd( sql, "union all ".length() ) + ") as data ";
        
        return sql;
    }
    
    private String getFromWhereClause( EventQueryParams params, String partition )
    {
        String sql = "from " + partition + " ";

        // ---------------------------------------------------------------------
        // Periods
        // ---------------------------------------------------------------------

        if ( params.hasStartEndDate() )
        {        
            sql += "where executiondate >= '" + getMediumDateString( params.getStartDate() ) + "' ";
            sql += "and executiondate <= '" + getMediumDateString( params.getEndDate() ) + "' ";
        }
        else // Periods
        {
            sql += "where " + params.getPeriodType() + " in (" + getQuotedCommaDelimitedString( getUids( params.getDimensionOrFilter( PERIOD_DIM_ID ) ) ) + ") ";
        }

        // ---------------------------------------------------------------------
        // Organisation units
        // ---------------------------------------------------------------------

        if ( params.isOrganisationUnitMode( DimensionalObject.OU_MODE_SELECTED ) )
        {
            sql += "and ou in (" + getQuotedCommaDelimitedString( getUids( params.getDimensionOrFilter( ORGUNIT_DIM_ID ) ) ) + ") ";
        }
        else if ( params.isOrganisationUnitMode( DimensionalObject.OU_MODE_CHILDREN ) )
        {
            sql += "and ou in (" + getQuotedCommaDelimitedString( getUids( params.getOrganisationUnitChildren() ) ) + ") ";
        }
        else // Descendants
        {
            sql += "and (";
            
            for ( NameableObject object : params.getDimensionOrFilter( ORGUNIT_DIM_ID ) )
            {
                OrganisationUnit unit = (OrganisationUnit) object;
                sql += "uidlevel" + unit.getLevel() + " = '" + unit.getUid() + "' or ";
            }
            
            sql = removeLastOr( sql ) + ") ";
        }

        // ---------------------------------------------------------------------
        // Organisation unit group sets
        // ---------------------------------------------------------------------

        for ( DimensionalObject dim : params.getDimensionsAndFilters( DimensionType.ORGANISATIONUNIT_GROUPSET ) )
        {
            if ( !dim.isAllItems() )
            {
                String col = statementBuilder.columnQuote( dim.getDimensionName() );
                
                sql += "and " + col + " in (" + getQuotedCommaDelimitedString( getUids( dim.getItems() ) ) + ") ";
            }
        }

        // ---------------------------------------------------------------------
        // Program stage
        // ---------------------------------------------------------------------

        if ( params.getProgramStage() != null )
        {
            sql += "and ps = '" + params.getProgramStage().getUid() + "' ";
        }

        // ---------------------------------------------------------------------
        // Query items and filters
        // ---------------------------------------------------------------------

        for ( QueryItem item : params.getItems() )
        {
            if ( item.hasFilter() )
            {
                for ( QueryFilter filter : item.getFilters() )
                {
                    sql += "and " + getColumn( item ) + " " + filter.getSqlOperator() + " " + getSqlFilter( filter, item.isNumeric() ) + " ";
                }
            }
        }
        
        for ( QueryItem item : params.getItemFilters() )
        {
            if ( item.hasFilter() )
            {
                for ( QueryFilter filter : item.getFilters() )
                {
                    sql += "and " + getColumn( item ) + " " + filter.getSqlOperator() + " " + getSqlFilter( filter, item.isNumeric() ) + " ";
                }
            }
        }
        
        if ( params.isCoordinatesOnly() )
        {
            sql += "and (longitude is not null and latitude is not null) ";
        }

        return sql;
    }
    
    /**
     * Returns an encoded column name wrapped in lower directive if not numeric.
     */
    private String getColumn( QueryItem item )
    {
        String col = statementBuilder.columnQuote( item.getItem().getUid() );
        
        return item.isNumeric() ? col : "lower(" + col + ")"; 
    }
    
    /**
     * Returns the filter value for the given query item.
     */
    private String getSqlFilter( QueryFilter filter, boolean numeric )
    {
        String encodedFilter = statementBuilder.encode( filter.getFilter(), false );
        
        String sqlFilter = filter.getSqlFilter( encodedFilter );
        
        return numeric ? sqlFilter : sqlFilter.toLowerCase();
    }

    /**
     * Creates a comma separated string based on the items in the given lists.
     * Appends a comma at the end of the string if not empty.
     */
    private String getSelectString( List<String> columns )
    {
        if ( columns == null || columns.isEmpty() )
        {
            return "";
        }
        
        String fixedCols = StringUtils.join( columns, ", " );
        
        return StringUtils.defaultIfEmpty( fixedCols + ", ", fixedCols );
    }
}
