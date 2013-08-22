package org.hisp.dhis.analytics.event.data;

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

import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.system.util.DateUtils.getMediumDateString;
import static org.hisp.dhis.system.util.TextUtils.getQuotedCommaDelimitedString;
import static org.hisp.dhis.system.util.TextUtils.removeLast;

import org.hisp.dhis.analytics.event.EventAnalyticsManager;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.analytics.event.QueryItem;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.system.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Lars Helge Overland
 */
public class JdbcEventAnalyticsManager
    implements EventAnalyticsManager
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // -------------------------------------------------------------------------
    // EventAnalyticsManager implementation
    // -------------------------------------------------------------------------

    public Grid getEvents( EventQueryParams params, Grid grid )
    {
        String sql = "select psi,ps,executiondate,ou,";
        
        for ( QueryItem queryItem : params.getItems() )
        {
            IdentifiableObject item = queryItem.getItem();
            
            sql += item.getUid() + ",";
        }
        
        sql = removeLast( sql, 1 ) + " ";
        
        sql += "from " + params.getTableName() + " ";        
        sql += "where executiondate >= '" + getMediumDateString( params.getStartDate() ) + "' ";
        sql += "and executiondate <= '" + getMediumDateString( params.getEndDate() ) + "' ";
        
        if ( params.hasOrganisationUnits() )
        {
            sql += "and ou in (" + getQuotedCommaDelimitedString( getUids( params.getOrganisationUnits() ) ) + ") ";
        }
        
        if ( params.getProgramStage() != null )
        {
            sql += "and ps = '" + params.getProgramStage().getUid() + "' ";
        }
        
        for ( QueryItem filter : params.getItems() )
        {
            if ( filter.hasFilter() )
            {
                sql += "and lower(" + filter.getItem().getUid() + ") " + filter.getSqlOperator() + " " + filter.getSqlFilter() + " ";
            }
        }

        int rowLength = grid.getHeaders().size();

        Timer t = new Timer().start();
        
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        t.getTime( "Analytics event SQL: " + sql );
        
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
}
