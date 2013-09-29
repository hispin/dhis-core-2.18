package org.hisp.dhis.analytics.event.data;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.system.util.DateUtils.getMediumDateString;
import static org.hisp.dhis.system.util.TextUtils.getQuotedCommaDelimitedString;
import static org.hisp.dhis.system.util.TextUtils.removeLast;

import java.util.Arrays;

import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.event.EventAnalyticsManager;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.analytics.event.QueryItem;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.system.util.TextUtils;
import org.hisp.dhis.system.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private StatementBuilder statementBuilder;

    // -------------------------------------------------------------------------
    // EventAnalyticsManager implementation
    // -------------------------------------------------------------------------

    public Grid getAggregatedEventData( EventQueryParams params, Grid grid )
    {
        String sql = "select count(psi) as value," + getSelectColumns( params ) + " ";

        // ---------------------------------------------------------------------
        // Criteria
        // ---------------------------------------------------------------------

        sql += getFromWhereClause( params );
        
        // ---------------------------------------------------------------------
        // Group by
        // ---------------------------------------------------------------------

        sql += "group by " + getSelectColumns( params );

        // ---------------------------------------------------------------------
        // Grid
        // ---------------------------------------------------------------------

        Timer t = new Timer().start();
        
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        t.getTime( "Analytics event aggregate SQL: " + sql );
        
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

        return grid;
    }
    
    public Grid getEvents( EventQueryParams params, Grid grid )
    {
        String sql = "select psi,ps,executiondate,ouname,oucode," + getSelectColumns( params ) + " ";

        // ---------------------------------------------------------------------
        // Criteria
        // ---------------------------------------------------------------------

        sql += getFromWhereClause( params );
        
        // ---------------------------------------------------------------------
        // Sorting
        // ---------------------------------------------------------------------

        if ( params.isSorting() )
        {
            sql += "order by ";
        
            for ( String item : params.getAsc() )
            {
                sql += item + " asc,";
            }
            
            for  ( String item : params.getDesc() )
            {
                sql += item + " desc,";
            }
            
            sql = removeLast( sql, 1 ) + " ";
        }
        
        // ---------------------------------------------------------------------
        // Paging
        // ---------------------------------------------------------------------

        if ( params.isPaging() )
        {
            sql += "limit " + params.getPageSizeWithDefault() + " offset " + params.getOffset();
        }

        // ---------------------------------------------------------------------
        // Grid
        // ---------------------------------------------------------------------

        int rowLength = grid.getHeaders().size();

        Timer t = new Timer().start();
        
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        t.getTime( "Analytics event query SQL: " + sql );
        
        while ( rowSet.next() )
        {
            grid.addRow();
            
            for ( int i = 0; i < rowLength; i++ )
            {
                int index = i + 1;
                
                grid.addValue( rowSet.getString( index ) );
            }
        }
        
        return grid;
    }

    public int getEventCount( EventQueryParams params )
    {
        String sql = "select count(psi) ";
        
        sql += getFromWhereClause( params );
        
        Timer t = new Timer().start();
        
        int count = jdbcTemplate.queryForObject( sql, Integer.class );

        t.getTime( "Analytics event count SQL: " + sql );
        
        return count;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String getSelectColumns( EventQueryParams params )
    {
        String sql = "";
        
        for ( DimensionalObject dimension : params.getDimensions() )
        {
            sql += dimension.getDimensionName() + ",";
        }
        
        for ( QueryItem queryItem : params.getItems() )
        {
            IdentifiableObject item = queryItem.getItem();
            
            sql += item.getUid() + ",";
        }
        
        return removeLast( sql, 1 );
    }
    
    private String getFromWhereClause( EventQueryParams params )
    {
        String sql = "";
        
        sql += "from " + params.getTableName() + " ";
        
        if ( params.hasStartEndDate() )
        {        
            sql += "where executiondate >= '" + getMediumDateString( params.getStartDate() ) + "' ";
            sql += "and executiondate <= '" + getMediumDateString( params.getEndDate() ) + "' ";
        }
        else // Periods
        {
            sql += "where " + params.getPeriodType() + " in (" + getQuotedCommaDelimitedString( getUids( params.getPeriods() ) ) + ") ";
        }
        
        if ( params.isOrganisationUnitMode( EventQueryParams.OU_MODE_SELECTED ) )
        {
            sql += "and ou in (" + getQuotedCommaDelimitedString( getUids( params.getOrganisationUnits() ) ) + ") ";
        }
        else if ( params.isOrganisationUnitMode( EventQueryParams.OU_MODE_CHILDREN ) )
        {
            sql += "and ou in (" + getQuotedCommaDelimitedString( getUids( params.getOrganisationUnitChildren() ) ) + ") ";
        }
        else // Descendants
        {
            sql += "and (";
            
            for ( NameableObject object : params.getOrganisationUnits() )
            {
                OrganisationUnit unit = (OrganisationUnit) object;
                sql += "uidlevel" + unit.getLevel() + " = '" + unit.getUid() + "' or ";
            }
            
            sql = TextUtils.removeLast( sql, 3 ) + ") ";
        }
        
        if ( params.getProgramStage() != null )
        {
            sql += "and ps = '" + params.getProgramStage().getUid() + "' ";
        }

        for ( QueryItem filter : params.getItems() )
        {
            if ( filter.hasFilter() )
            {                
                sql += "and lower(" + filter.getItem().getUid() + ") " + filter.getSqlOperator() + " " + getSqlFilter( filter ) + " ";
            }
        }
        
        for ( QueryItem filter : params.getItemFilters() )
        {
            if ( filter.hasFilter() )
            {                
                sql += "and lower(" + filter.getItem().getUid() + ") " + filter.getSqlOperator() + " " + getSqlFilter( filter ) + " ";
            }
        }

        return sql;
    }
    
    private String getSqlFilter( QueryItem item )
    {
        String operator = item.getOperator();
        String filter = item.getFilter();
        
        if ( operator == null || filter == null )
        {
            return null;
        }
        
        operator = operator.toLowerCase();
        filter = statementBuilder.encode( filter, false ).toLowerCase();
        
        if ( operator.equals( "like" ) )
        {
            return "'%" + filter + "%'";
        }
        else if ( operator.equals( "in" ) )
        {
            String[] split = filter.split( DataQueryParams.OPTION_SEP );
                        
            return "(" + getQuotedCommaDelimitedString( Arrays.asList( split ) ) + ")";
        }
        
        return "'" + filter + "'";
    }    
}
