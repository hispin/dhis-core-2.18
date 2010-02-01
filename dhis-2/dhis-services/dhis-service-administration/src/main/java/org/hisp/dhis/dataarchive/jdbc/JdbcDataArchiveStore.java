package org.hisp.dhis.dataarchive.jdbc;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataarchive.DataArchiveStore;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Lars Helge Overland
 */
public class JdbcDataArchiveStore
    implements DataArchiveStore
{
    private static final Log log = LogFactory.getLog( JdbcDataArchiveStore.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;
    
    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    public void archiveData( Date startDate, Date endDate )
    {
        final String sql =
            
            // Move data from datavalue to datavaluearchive
            
            "INSERT INTO datavaluearchive ( " +
                "SELECT d.* FROM datavalue AS d " +
                "JOIN period as p USING (periodid) " +
                "WHERE p.startdate>'" + startDate + "' " +
                "AND p.enddate<='" + endDate + "' );" +
            
            // Delete data from datavalue
            
            "DELETE FROM datavalue AS d " +
            "USING period as p " +
            "WHERE d.periodid=p.periodid " +
            "AND p.startdate>'" + startDate + "' " +
            "AND p.enddate<='" + endDate + "';";
    
        log.info( sql );
        
        jdbcTemplate.execute( sql ); 
    }

    public void unArchiveData( Date startDate, Date endDate )
    {
        final String sql =
            
            // Move data from datavalue to datavaluearchive
            
            "INSERT INTO datavalue ( " +
                "SELECT a.* FROM datavaluearchive AS a " +
                "JOIN period as p USING (periodid) " +
                "WHERE p.startdate>'" + startDate + "' " +
                "AND p.enddate<='" + endDate + "' );" +
            
            // Delete data from datavalue
            
            "DELETE FROM datavaluearchive AS a " +
            "USING period AS p " +
            "WHERE a.periodid=p.periodid " +
            "AND p.startdate>'" + startDate + "' " +
            "AND p.enddate<='" + endDate + "';";

        log.info( sql );
        
        jdbcTemplate.execute( sql ); 
    }
    
    public int getNumberOfOverlappingValues()
    {
        final String sql =
            "SELECT COUNT(*) FROM datavaluearchive " +
            "JOIN datavalue USING (dataelementid, periodid, sourceid, categoryoptioncomboid);";

        log.info( sql );
        
        return jdbcTemplate.queryForInt( sql );
    }
    
    public int getNumberOfArchivedValues()
    {
        final String sql =
            "SELECT COUNT(*) FROM datavaluearchive;";
        
        log.info( sql );
        
        return jdbcTemplate.queryForInt( sql );
    }
    
    public void deleteRegularOverlappingData()
    {
        final String sql = 
            "DELETE FROM datavalue AS d " +
            "USING datavaluearchive AS a " +
            "WHERE d.dataelementid=a.dataelementid " +
            "AND d.periodid=a.periodid " +
            "AND d.sourceid=a.sourceid " +
            "AND d.categoryoptioncomboid=a.categoryoptioncomboid;";

        log.info( sql );
        
        jdbcTemplate.execute( sql );
    }
    
    public void deleteArchivedOverlappingData()
    {
        final String sql = 
            "DELETE FROM datavaluearchive AS a " +
            "USING datavalue AS d " +
            "WHERE a.dataelementid=d.dataelementid " +
            "AND a.periodid=d.periodid " +
            "AND a.sourceid=d.sourceid " +
            "AND a.categoryoptioncomboid=d.categoryoptioncomboid;";

        log.info( sql );
        
        jdbcTemplate.execute( sql );
    }    

    public void deleteOldestOverlappingData()
    {
        final String sql = 
            
            // Delete overlaps from datavalue which are older than datavaluearchive
            
            "DELETE FROM datavalue AS d " +
            "USING datavaluearchive AS a " +
            "WHERE d.dataelementid=a.dataelementid " +
            "AND d.periodid=a.periodid " +
            "AND d.sourceid=a.sourceid " +
            "AND d.categoryoptioncomboid=a.categoryoptioncomboid " +
            "AND d.lastupdated<a.lastupdated;" +
            
            // Delete overlaps from datavaluearchive which are older than datavalue
            
            "DELETE FROM datavaluearchive AS a " +
            "USING datavalue AS d " +
            "WHERE a.dataelementid=d.dataelementid " +
            "AND a.periodid=d.periodid " +
            "AND a.sourceid=d.sourceid " +
            "AND a.categoryoptioncomboid=d.categoryoptioncomboid " +
            "AND a.lastupdated<=d.lastupdated;";

        log.info( sql );
        
        jdbcTemplate.execute( sql );
    }    
}
