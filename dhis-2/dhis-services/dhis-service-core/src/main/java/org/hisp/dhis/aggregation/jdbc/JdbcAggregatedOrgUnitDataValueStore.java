package org.hisp.dhis.aggregation.jdbc;

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

import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.Collection;

import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedOrgUnitDataValueStore;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcAggregatedOrgUnitDataValueStore
    implements AggregatedOrgUnitDataValueStore
{
    private static final Log log = LogFactory.getLog( JdbcAggregatedOrgUnitDataValueStore.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    protected JdbcTemplate jdbcTemplate;
    
    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // AggregatedOrgUnitDataValueStore implementation
    // -------------------------------------------------------------------------

    public void deleteAggregatedDataValues( Collection<Integer> dataElementIds, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final String sql =
            "DELETE FROM aggregatedorgunitdatavalue " +
            "WHERE dataelementid IN ( " + getCommaDelimitedString( dataElementIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        jdbcTemplate.execute( sql );        
    }

    public void deleteAggregatedDataValues( Collection<Integer> periodIds )
    {
        final String sql =
            "DELETE FROM aggregatedorgunitdatavalue " +
            "WHERE periodid IN ( " + getCommaDelimitedString( periodIds ) + " )";
        
        jdbcTemplate.execute( sql );        
    }

    public void createIndex( boolean dataElement, boolean indicator )
    {
        if ( dataElement )
        {
            try
            {
                final String sql = "CREATE INDEX aggregatedorgunitdatavalue_index ON aggregatedorgunitdatavalue (dataelementid, categoryoptioncomboid, periodid, organisationunitid, organisationunitgroupid)";        
                jdbcTemplate.execute( sql );
            }
            catch ( Exception ex )
            {
                log.debug( "Index already exists" );
            }
        }
        
        if ( indicator )
        {
            try
            {
                final String sql = "CREATE INDEX aggregatedorgunitindicatorvalue_index ON aggregatedorgunitindicatorvalue (indicatorid, periodid, organisationunitid, organisationunitgroupid)";        
                jdbcTemplate.execute( sql );
            }
            catch ( Exception ex )
            {
                log.debug( "Index already exists" );
            }
        }
    }
    
    public void dropIndex( boolean dataElement, boolean indicator )
    {
        if ( dataElement )
        {
            try
            {
                final String sql = "DROP INDEX aggregatedorgunitdatavalue_index";
                jdbcTemplate.execute( sql );
            }
            catch ( Exception ex )
            {
                log.debug( "Index does not exist" );
            }
        }
        
        if ( indicator )
        {
            try
            {
                final String sql = "DROP INDEX aggregatedorgunitindicatorvalue_index";
                jdbcTemplate.execute( sql );
            }
            catch ( Exception ex )
            {
                log.debug( "Index does not exist" );
            }
        }
    }
    
    // -------------------------------------------------------------------------
    // AggregatedIndicatorValue
    // -------------------------------------------------------------------------

    public void deleteAggregatedIndicatorValues( Collection<Integer> indicatorIds, Collection<Integer> periodIds,
        Collection<Integer> organisationUnitIds )
    {
        final String sql =
            "DELETE FROM aggregatedorgunitindicatorvalue " +
            "WHERE indicatorid IN ( " + getCommaDelimitedString( indicatorIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        jdbcTemplate.execute( sql );        
    }

    public void deleteAggregatedIndicatorValues( Collection<Integer> periodIds )
    {
        final String sql =
            "DELETE FROM aggregatedorgunitindicatorvalue " +
            "WHERE periodid IN ( " + getCommaDelimitedString( periodIds ) + " )";

        jdbcTemplate.execute( sql );      
    }    
}
