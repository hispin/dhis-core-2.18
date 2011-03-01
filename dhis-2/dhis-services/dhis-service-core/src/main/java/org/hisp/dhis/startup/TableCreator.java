package org.hisp.dhis.startup;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Lars Helge Overland
 */
public class TableCreator
    extends AbstractStartupRoutine
{
    private Log log = LogFactory.getLog( TableCreator.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }
    
    // -------------------------------------------------------------------------
    // StartupRoutine implementation
    // -------------------------------------------------------------------------

    public void execute()
    {
        // -----------------------------------------------------------------
        // AggregatedDataValue
        // -----------------------------------------------------------------

        try
        {
            jdbcTemplate.execute( statementBuilder.getCreateAggregatedDataValueTable() );
            
            log.info( "Created table aggregateddatavalue" );
        }
        catch ( Exception ex )
        {
            log.info( "Table aggregateddatavalue exists" );
        }

        // -----------------------------------------------------------------
        // AggregatedIndicatorValue
        // -----------------------------------------------------------------

        try
        {
            jdbcTemplate.execute( statementBuilder.getCreateAggregatedIndicatorTable() );
            
            log.info( "Created table aggregatedindicatorvalue" );
        }
        catch ( Exception ex )
        {
            log.info( "Table aggregatedindicatorvalue exists" );
        }
        
        // -----------------------------------------------------------------
        // Crosstab index on DataValue table
        // -----------------------------------------------------------------

        try
        {
            final String sql = "CREATE INDEX crosstab ON datavalue ( periodid, sourceid )";
            
            jdbcTemplate.execute( sql );
            
            log.info( "Created index crosstab on table datavalue" );
        }
        catch ( Exception ex )
        {
            log.info( "Index crosstab exists on table datavalue" );
        }
        
        // -----------------------------------------------------------------
        // DataSetCompleteness
        // -----------------------------------------------------------------

        try
        {
            jdbcTemplate.execute( statementBuilder.getCreateDataSetCompletenessTable() );
            
            log.info( "Created table aggregateddatasetcompleteness" );
        }
        catch ( Exception ex )
        {
            log.info( "Table aggregateddatasetcompleteness exists" );
        }
        
        // -----------------------------------------------------------------
        // ArchivedDataValue
        // -----------------------------------------------------------------

        try
        {
            final String sql = 
                "CREATE TABLE datavaluearchive ( " +
                "dataelementid INTEGER NOT NULL, " +
                "periodid INTEGER NOT NULL, " +
                "sourceid INTEGER NOT NULL, " +
                "categoryoptioncomboid INTEGER NOT NULL, " +
                "value VARCHAR(255), " +
                "storedby VARCHAR(31), " +
                "lastupdated TIMESTAMP, " +
                "comment VARCHAR(360), " +
                "followup BOOLEAN, " +
                "CONSTRAINT datavaluearchive_pkey PRIMARY KEY (dataelementid, periodid, sourceid, categoryoptioncomboid), " +
                "CONSTRAINT fk_datavaluearchive_categoryoptioncomboid FOREIGN KEY (categoryoptioncomboid) " +
                    "REFERENCES categoryoptioncombo (categoryoptioncomboid), " +
                "CONSTRAINT fk_datavaluearchive_dataelementid FOREIGN KEY (dataelementid) " +
                    "REFERENCES dataelement (dataelementid), " +
                "CONSTRAINT fk_datavaluearchive_periodid FOREIGN KEY (periodid) " +
                    "REFERENCES period (periodid), " +
                "CONSTRAINT fk_datavaluearchive_sourceid FOREIGN KEY (sourceid) " +
                    "REFERENCES source (sourceid) );";
            
            jdbcTemplate.execute( sql );
            
            log.info( "Created table datavaluearchive" );
        }
        catch ( Exception ex )
        {
            log.info( "Table datavaluearchive exists" );
        }
        
        // -----------------------------------------------------------------
        // ArchivedPatientDataValue
        // -----------------------------------------------------------------

        try
        {
            final String sql = 
                "CREATE TABLE patientdatavaluearchive ( " +
                 " programstageinstanceid integer NOT NULL, " +
                 " dataelementid integer NOT NULL, " +
                 " organisationunitid integer NOT NULL, " +
                 " categoryoptioncomboid integer default NULL, " +
                 " value varchar(255) default NULL, " +
                 " providedbyanotherfacility boolean NOT NULL, " +
                 " timestamp TIMESTAMP, " +
                 " PRIMARY KEY  (programstageinstanceid,dataelementid,organisationunitid), " +
                 " CONSTRAINT fk_patientdatavaluearchive_organisationunitid FOREIGN KEY (organisationunitid) REFERENCES organisationunit (organisationunitid), " +
                 " CONSTRAINT fk_patientdatavaluearchive_programstageinstanceid FOREIGN KEY (programstageinstanceid) REFERENCES programstageinstance (programstageinstanceid) " +
                 "        );";
                            
            jdbcTemplate.execute( sql );
            
            log.info( "Created table patientdatavaluearchive" );
        }
        catch ( Exception ex )
        {
            log.info( "Table patientdatavaluearchive exists" );
        }
    }
}
