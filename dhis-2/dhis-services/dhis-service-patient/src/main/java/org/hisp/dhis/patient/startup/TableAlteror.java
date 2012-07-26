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

package org.hisp.dhis.patient.startup;

import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_STAGE_DATAELEMENT;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.SEPARATOR_ID;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.SEPARATOR_OBJECT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version TableAlteror.java Sep 9, 2010 10:22:29 PM
 */
public class TableAlteror
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( TableAlteror.class );

    Pattern IDENTIFIER_PATTERN = Pattern.compile( "DE:(\\d+)\\.(\\d+)\\.(\\d+)" );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Transactional
    public void execute()
        throws Exception
    {
        executeSql( "ALTER TABLE relationshiptype RENAME description TO name" );

        updateProgramStageInstanceOrgunit();

        executeSql( "ALTER TABLE programstage_dataelements DROP COLUMN showOnReport" );

        executeSql( "ALTER TABLE patientdatavalue DROP COLUMN categoryoptioncomboid" );
        executeSql( "ALTER TABLE patientdatavaluearchive DROP COLUMN providedbyanotherfacility" );
        executeSql( "ALTER TABLE patientdatavaluearchive DROP COLUMN organisationunitid" );
        executeSql( "ALTER TABLE patientdatavaluearchive DROP COLUMN storedby" );
        executeSql( "DROP TABLE patientchart" );

        executeSql( "ALTER TABLE program DROP COLUMN hidedateofincident" );

        executeSql( "UPDATE program SET type=2 where singleevent=true" );
        executeSql( "UPDATE program SET type=3 where anonymous=true" );
        executeSql( "ALTER TABLE program DROP COLUMN singleevent" );
        executeSql( "ALTER TABLE program DROP COLUMN anonymous" );
        executeSql( "UPDATE program SET type=1 where type is null" );

        executeSql( "UPDATE programstage SET irregular=false WHERE irregular is null" );

        executeSql( "DROP TABLE programattributevalue" );
        executeSql( "DROP TABLE programinstance_attributes" );
        executeSql( "DROP TABLE programattributeoption" );
        executeSql( "DROP TABLE programattribute" );

        executeSql( "ALTER TABLE patientattribute DROP COLUMN noChars" );
        executeSql( "ALTER TABLE programstageinstance ALTER executiondate TYPE date" );

        executeSql( "ALTER TABLE patientidentifier ALTER COLUMN patientid DROP NOT NULL" );
        executeSql( "ALTER TABLE patient DROP COLUMN bloodgroup" );
        executeSql( "ALTER TABLE patientmobilesetting DROP COLUMN bloodGroup" );

        executeSql( "ALTER TABLE caseaggregationcondition RENAME description TO name" );
        updateCaseAggregationCondition();

        executeSql( "UPDATE programstage_dataelements SET allowProvidedElsewhere=false WHERE allowProvidedElsewhere is null" );
        executeSql( "UPDATE patientdatavalue SET providedElsewhere=false WHERE providedElsewhere is null" );
        executeSql( "ALTER TABLE programstageinstance DROP COLUMN providedbyanotherfacility" );

        updateMultiOrgunitTabularReportTable();
        updateProgramStageTabularReportTable();
        moveStoredByFormStageInstanceToDataValue();
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    private void updateProgramStageInstanceOrgunit()
    {
        try
        {
            String sql = "SELECT distinct programstageinstanceid, organisationunitid, providedByAnotherFacility FROM patientdatavalue";

            jdbcTemplate.query( sql, new RowMapper<Boolean>()
            {
                public Boolean mapRow( ResultSet rs, int rowNum )
                    throws SQLException
                {
                    executeSql( "UPDATE programstageinstance SET organisationunitid=" + rs.getInt( 2 )
                        + ", providedByAnotherFacility=" + rs.getBoolean( 3 ) + "  WHERE programstageinstanceid="
                        + rs.getInt( 1 ) );
                    return true;
                }
            } );

            executeSql( "ALTER TABLE patientdatavalue DROP COLUMN organisationUnitid" );
            executeSql( "ALTER TABLE patientdatavalue DROP COLUMN providedByAnotherFacility" );
            executeSql( "ALTER TABLE patientdatavalue ADD PRIMARY KEY ( programstageinstanceid, dataelementid )" );
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
    }

    private void updateCaseAggregationCondition()
    {

        try
        {
            String sql = "SELECT caseaggregationconditionid, aggregationExpression FROM caseaggregationcondition";

            jdbcTemplate.query( sql, new RowMapper<Boolean>()
            {
                public Boolean mapRow( ResultSet rs, int rowNum )
                    throws SQLException
                {
                    String regExp = "\\[" + OBJECT_PROGRAM_STAGE_DATAELEMENT + SEPARATOR_OBJECT + "[0-9]+"
                        + SEPARATOR_ID + "[0-9]+" + "\\]";

                    StringBuffer formula = new StringBuffer();

                    // ---------------------------------------------------------------------
                    // parse expressions
                    // ---------------------------------------------------------------------

                    Pattern pattern = Pattern.compile( regExp );

                    Matcher matcher = pattern.matcher( rs.getString( 2 ) );

                    while ( matcher.find() )
                    {
                        String match = matcher.group();
                        match = match.replaceAll( "[\\[\\]]", "" );

                        String[] info = match.split( SEPARATOR_OBJECT );
                        String[] ids = info[1].split( SEPARATOR_ID );
                        int programStageId = Integer.parseInt( ids[0] );

                        String subSQL = "SELECT programid FROM programstage where programstageid=" + programStageId;

                        int programId = jdbcTemplate.queryForInt( subSQL );

                        String aggregationExpression = "[" + OBJECT_PROGRAM_STAGE_DATAELEMENT + SEPARATOR_OBJECT
                            + programId + "." + programStageId + "." + ids[1] + "]";

                        matcher.appendReplacement( formula, aggregationExpression );
                    }

                    matcher.appendTail( formula );

                    executeSql( "UPDATE caseaggregationcondition SET aggregationExpression='" + formula.toString()
                        + "'  WHERE caseaggregationconditionid=" + rs.getInt( 1 ) );

                    return true;
                }
            } );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    private void updateMultiOrgunitTabularReportTable()
    {
        try
        {
            String sql = "SELECT patienttabularreportid, organisationunitid FROM patienttabularreport";

            jdbcTemplate.query( sql, new RowMapper<Boolean>()
            {
                public Boolean mapRow( ResultSet rs, int rowNum )
                    throws SQLException
                {
                    executeSql( " INSERT INTO patienttabularreport_organisationUnits ( patienttabularreportid, organisationunitid ) VALUES ( "
                        + rs.getInt( 1 ) + ", " + rs.getInt( 2 ) + ")" );
                    return true;
                }
            } );

            executeSql( "ALTER TABLE patienttabularreport DROP COLUMN organisationunitid" );
        }
        catch ( Exception e )
        {

        }
    }

    private void updateProgramStageTabularReportTable()
    {
        try
        {
            String sql = "SELECT pd.patienttabularreportid, tr.programstageid, pd.elt, sort_order "
                + " FROM patienttabularreport_dataelements pd inner join patienttabularreport  tr"
                + " on pd.patienttabularreportid=tr.patienttabularreportid" + " order by pd.patienttabularreportid";

            jdbcTemplate.query( sql, new RowMapper<Boolean>()
            {
                public Boolean mapRow( ResultSet rs, int rowNum )
                    throws SQLException
                {
                    executeSql( "INSERT INTO patienttabularreport_programstagedataelements ( patienttabularreportid, programstageid, dataelementid, sort_order ) VALUES ( "
                        + rs.getInt( 1 ) + ", " + rs.getInt( 2 ) + ", " + rs.getInt( 3 ) + ", " + rs.getInt( 4 ) + ")" );
                    return true;
                }
            } );

            executeSql( "ALTER TABLE patienttabularreport DROP COLUMN programstageid" );
            executeSql( "DROP TABLE patienttabularreport_dataelements" );
        }
        catch ( Exception e )
        {

        }
    }

    private void moveStoredByFormStageInstanceToDataValue()
    {
        try
        {
            String sql = "SELECT programstageinstanceid, storedBy"
                + " FROM programstageinstance where storedBy is not null";

            jdbcTemplate.query( sql, new RowMapper<Boolean>()
            {
                public Boolean mapRow( ResultSet rs, int rowNum )
                    throws SQLException
                {
                    executeSql( "UPDATE patientdatavalue SET storedBy='" + rs.getString( 2 )
                        + "' where programstageinstanceid=" + rs.getInt( 1 ) );
                    return true;
                }
            } );

            executeSql( "ALTER TABLE programstageinstance DROP COLUMN storedBy" );
        }
        catch ( Exception ex )
        {
        }
    }
    
    private void executeSql(String sql)
    {
        jdbcTemplate.execute( sql );
    }
}
