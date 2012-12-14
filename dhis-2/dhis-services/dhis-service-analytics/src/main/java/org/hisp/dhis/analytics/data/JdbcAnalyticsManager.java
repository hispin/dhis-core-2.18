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

import static org.hisp.dhis.system.util.TextUtils.getQuotedCommaDelimitedString;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AnalyticsManager;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * This class is responsible for producing aggregated data values. It reads data
 * from the analytics table. Organisation units provided as arguments must be on
 * the same level in the hierarchy.
 * 
 * @author Lars Helge Overland
 */
public class JdbcAnalyticsManager
    implements AnalyticsManager
{
    private static final Log log = LogFactory.getLog( JdbcAnalyticsManager.class );
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private PeriodService periodService;
    
    @Autowired
    private ExpressionService expressionService;
    
    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    @Async
    public Future<Map<String, Double>> getAggregatedDataValueTotals( DataQueryParams params )
    {
        int level = organisationUnitService.getLevelOfOrganisationUnit( params.getOrganisationUnits().iterator().next() );
        
        String periodType = PeriodType.getPeriodTypeFromIsoString( params.getPeriods().iterator().next() ).getName().toLowerCase();
        
        final String sql = 
            "SELECT dataelement, 0 as categoryoptioncombo, " + 
            periodType + " as period, uidlevel" + level + " as organisationunit, SUM(value) as value " +
            "FROM " + params.getTableName() + " " +
            "WHERE dataelement IN ( " + getQuotedCommaDelimitedString( params.getDataElements() ) + " ) " +
            "AND " + periodType + " IN ( " + getQuotedCommaDelimitedString( params.getPeriods() ) + " ) " +
            "AND uidlevel" + level + " IN ( " + getQuotedCommaDelimitedString( params.getOrganisationUnits() ) + " ) " +
            "GROUP BY dataelement, " + periodType + ", uidlevel" + level;

        log.info( sql );
        
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
        
        Map<String, Double> map = new HashMap<String, Double>();
        
        while ( rowSet.next() )
        {
            String key = 
                rowSet.getString( "dataelement" ) + SEP +
                rowSet.getString( "categoryoptioncombo" ) + SEP +
                rowSet.getString( "period" ) + SEP +
                rowSet.getString( "organisationunit" );
            
            Double value = rowSet.getDouble( "value" );
            
            map.put( key, value );
        }
        
        return new AsyncResult<Map<String, Double>>( map );
    }
}
