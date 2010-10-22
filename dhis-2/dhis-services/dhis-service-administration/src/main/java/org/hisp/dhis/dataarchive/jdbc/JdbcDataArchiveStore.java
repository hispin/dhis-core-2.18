package org.hisp.dhis.dataarchive.jdbc;

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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataarchive.DataArchiveStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.hisp.dhis.system.util.DateUtils.*;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    public void archiveData( Date startDate, Date endDate )
    {
        // Move data from datavalue to datavaluearchive
        
        String sql =
            "INSERT INTO datavaluearchive ( " +
                "SELECT d.* FROM datavalue AS d " +
                "JOIN period as p USING (periodid) " +
                "WHERE p.startdate>='" + getMediumDateString( startDate ) + "' " +
                "AND p.enddate<='" + getMediumDateString( endDate ) + "' );";

        log.info( sql );        
        jdbcTemplate.execute( sql );
        
        // Delete data from datavalue
        
        sql =
            "DELETE FROM datavalue AS d " +
            "USING period as p " +
            "WHERE d.periodid=p.periodid " +
            "AND p.startdate>='" + getMediumDateString( startDate ) + "' " +
            "AND p.enddate<='" + getMediumDateString( endDate ) + "';";
    
        log.info( sql );        
        jdbcTemplate.execute( sql ); 
    }

    public void unArchiveData( Date startDate, Date endDate )
    {
        // Move data from datavalue to datavaluearchive
        
        String sql =
            "INSERT INTO datavalue ( " +
                "SELECT a.* FROM datavaluearchive AS a " +
                "JOIN period as p USING (periodid) " +
                "WHERE p.startdate>='" + getMediumDateString( startDate ) + "' " +
                "AND p.enddate<='" + getMediumDateString( endDate ) + "' );";

        log.info( sql );        
        jdbcTemplate.execute( sql ); 
        
        // Delete data from datavalue
        
        sql =
            "DELETE FROM datavaluearchive AS a " +
            "USING period AS p " +
            "WHERE a.periodid=p.periodid " +
            "AND p.startdate>='" + getMediumDateString( startDate ) + "' " +
            "AND p.enddate<='" + getMediumDateString( endDate ) + "';";

        log.info( sql );        
        jdbcTemplate.execute( sql ); 
    }
    
    public int getNumberOfOverlappingValues()
    {
        String sql =
            "SELECT COUNT(*) FROM datavaluearchive " +
            "JOIN datavalue USING (dataelementid, periodid, sourceid, categoryoptioncomboid);";

        log.info( sql );        
        return jdbcTemplate.queryForInt( sql );
    }
    
    public int getNumberOfArchivedValues()
    {
        String sql = "SELECT COUNT(*) FROM datavaluearchive;";
        
        log.info( sql );        
        return jdbcTemplate.queryForInt( sql );
    }
    
    public void deleteRegularOverlappingData()
    {
        String sql = 
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
        String sql = 
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
        // Delete overlaps from datavalue which are older than datavaluearchive
        
        String sql = 
            "DELETE FROM datavalue AS d " +
            "USING datavaluearchive AS a " +
            "WHERE d.dataelementid=a.dataelementid " +
            "AND d.periodid=a.periodid " +
            "AND d.sourceid=a.sourceid " +
            "AND d.categoryoptioncomboid=a.categoryoptioncomboid " +
            "AND d.lastupdated<a.lastupdated;";

        log.info( sql );        
        jdbcTemplate.execute( sql );
        
        // Delete overlaps from datavaluearchive which are older than datavalue
            
        sql =            
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
