package org.hisp.dhis.dataprune.jdbc;

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

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataprune.DataPruneStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Quang Nguyen
 * @version Apr 6, 2010 5:48:15 PM
 */
public class JdbcDataPruneStore 
    implements DataPruneStore
{
    private static final Log log = LogFactory.getLog( JdbcDataPruneStore.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    // -------------------------------------------------------------------------
    // DataPruneService implementation
    // -------------------------------------------------------------------------

    public int deleteMultiOrganisationUnit( List<OrganisationUnit> orgUnits )
    {
        try
        {
            String orgUnitIds = TextUtils.getCommaDelimitedString( ConversionUtils.getIdentifiers( OrganisationUnit.class, orgUnits ) );

            // delete values into datasetlocksource table
            String sql = "delete from datasetlocksource where sourceid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into datasetlocksource sucessfully" );
            
            // delete values into completedatasetregistration table
            sql = "delete from completedatasetregistration where sourceid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into completedatasetregistration sucessfully" );
    
            // delete values into datasetsource table
            sql = "delete from datasetsource where sourceid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into datasetsource sucessfully" );
    
            // delete values into minmaxdataelement table
            sql = "delete from minmaxdataelement where sourceid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into minmaxdataelement sucessfully" );
    
            // delete values into orgunitgroupmembers table
            sql = "delete from orgunitgroupmembers where organisationunitid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into orgunitgroupmembers sucessfully" );
    
            // delete values into usermembership table
            sql = "delete from usermembership where organisationunitid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into usermembership sucessfully" );
    
            // delete values into datamartexportorgunits table
            sql = "delete from datamartexportorgunits where orgunitid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into datamartexportorgunits sucessfully" );
    
            // delete values into patientidentifier table
            sql = "delete from patientidentifier where patientid in "+
                    "( select patientid from patient where organisationunitid in (" + orgUnitIds + ") );";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into patientidentifier sucessfully" );
            
            // delete values into patientattributevalue table
            sql = "delete from patientdatavalue where programstageinstanceid in " +
                    "( select programstageinstanceid from programstageinstance "+
                        "join programinstance on programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                        "join patient on programinstance.patientid = patient.patientid "+
                        "where patient.organisationunitid in  (" + orgUnitIds + ") );";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into patientattributevalue sucessfully" );
            
            // delete values into patientattributevalue table
            sql = "delete from patientattributevalue where patientid in " +
                    "( select patientid from patient where organisationunitid in  (" + orgUnitIds + ") );";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into patientattributevalue sucessfully" );
            
            // delete values into patient_attributes table
            sql = "delete from patient_attributes where patientid in "+
                    "( select patientid from patient where organisationunitid in (" + orgUnitIds + ") );";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into patient_attributes sucessfully" );
            
            // delete values into patient_programs table
            sql = "delete from patient_programs where patientid in "+
                    "( select patientid from patient where organisationunitid in (" + orgUnitIds + ") );";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into patient_programs sucessfully" );
            
            // delete values into relationship table
            sql = "delete from relationship where patientaid in "+
                    "( select patientid from patient where organisationunitid in ( " + orgUnitIds + ")) OR "+
                    "patientbid in ( select patientid from patient where organisationunitid in ( " + orgUnitIds + ")); ";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into patient sucessfully" );
            
            // update values into representativeid column of patient table
            deleteRepresentative(orgUnitIds);
            log.info( "Updating values into representativeid column of patient table sucessfully" );
            
            // delete values into patientdatavalue table
            sql = "delete from patientdatavalue where organisationunitid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into patientdatavalue sucessfully" );

            // delete values into patientdatavaluearchive table
            if ( isExistTable( "patientdatavaluearchive" ) )
            {
                sql = "delete from patientdatavaluearchive where organisationunitid in (" + orgUnitIds + ");";
                jdbcTemplate.execute( sql );
                log.info( "Deleting values into patientdatavaluearchive sucessfully" );
            }

            // delete values into programinstance_attributes table
            sql = "delete from programinstance_attributes where programinstanceid in "+
                    "( select programinstanceid from programinstance "+
                    "join patient on programinstance.patientid = patient.patientid "+
                    "where patient.organisationunitid in ( " + orgUnitIds + "));";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into programinstance_attributes sucessfully" );
            
            // delete values into programattributevalue table
            sql = "delete from programattributevalue where programinstanceid in "+
                    "( select programinstanceid from programinstance " +
                    "join patient on programinstance.patientid = patient.patientid "+
                    "where patient.organisationunitid in ( " + orgUnitIds + "));";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into programattributevalue sucessfully" );
            
            // delete values into programstageinstance table
            sql = "delete from programstageinstance where programinstanceid in "+
                    "( select programinstanceid from programinstance "+
                    "join patient on programinstance.patientid = patient.patientid "+
                    "where patient.organisationunitid in ( " + orgUnitIds + "));";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into programstageinstance sucessfully" );
            
            // delete values into programinstance table
            sql = "delete from programinstance where patientid in "+
                    "( select patientid from patient where organisationunitid in ( " + orgUnitIds + "));";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into programinstance sucessfully" );
            
            
            // delete values into patient table
            deleteOrganisation(orgUnitIds );
            sql = "delete from patient where organisationunitid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into patient sucessfully" );
            
            // delete values into program_organisationunits table
            sql = "delete from program_organisationunits where organisationunitid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into program_organisationunits sucessfully" );
            
            // delete values into chart_organisationunits table
            sql = "delete from chart_organisationunits where organisationunitid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into chart_organisationunits sucessfully" );
    
            // delete values into reporttable_organisationunits table
            sql = "delete from reporttable_organisationunits where organisationunitid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into reporttable_organisationunits sucessfully" );

            // delete value into datavalue_audit table
            if ( isExistTable( "datavalue_audit" ) )
            {
                sql = "delete from datavalue_audit where (dataelementid, periodid, sourceid, categoryoptioncomboid) in (select dataelementid, periodid, sourceid, categoryoptioncomboid from datavalue where sourceid in ("
                    + orgUnitIds + "));";
                jdbcTemplate.execute( sql );
                log.info( "Deleting values into datavalue_audit sucessfully" );
            }

            // delete values into datavalue table
            sql = "delete from datavalue where sourceid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into datavalue sucessfully" );

            // delete values into datavaluearchive table
            if ( isExistTable( "datavaluearchive" ) )
            {
                sql = "delete from datavaluearchive where sourceid in (" + orgUnitIds + ");";
                jdbcTemplate.execute( sql );
                log.info( "Deleting values into datavaluearchive sucessfully" );
            }

            // delete values into mapfile table
            if ( isExistTable( "mapfile" ) )
            {
                sql = "delete from mapfile where organisationunitid in (" + orgUnitIds + ");";
                jdbcTemplate.execute( sql );
                log.info( "Deleting values into mapfile sucessfully" );
            }

            // delete values into feature table
            if ( isExistTable( "feature" ) )
            {
                sql = "delete from feature where organisationunitid in (" + orgUnitIds + ");";
                jdbcTemplate.execute( sql );
                log.info( "Deleting values into feature sucessfully" );
            }

            // delete values into orgunitgroupmembers table
            sql = "delete from orgunitgroupmembers where organisationunitid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into orgunitgroupmembers sucessfully" );

            // delete values into organisationunit table
            sql = "delete from organisationunit where organisationunitid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into organisationunit sucessfully" );
            
            // delete values into source table
            sql = "delete from source where sourceid in (" + orgUnitIds + ");";
            jdbcTemplate.execute( sql );
            log.info( "Deleting values into source sucessfully" );
            
            log.info( "Deleting " + orgUnits.size() + " organisations units successfully" );

            return 0;            
        }
        catch (Exception ex) 
        {
            ex.printStackTrace();
        }
        
        return 1;
    }
    
    private boolean isExistTable(String tableName)
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            holder.getStatement().executeQuery( "SELECT * FROM " + tableName );
            
            return true;
        }
        catch ( Exception ex )
        {
            return false;
        }
        finally
        {
            holder.close();
        }
    }
    
    private void deleteRepresentative( String orgUnitIds )
    {        
        StatementHolder holder = statementManager.getHolder();
        
        try
        {
            Statement statement = holder.getStatement();
            
            ResultSet patient = statement.executeQuery( "select patientid from patient where organisationunitid in ( " + orgUnitIds + ")" );

            String patientIds = "0";
            
            while ( patient.next() )
            {
                patientIds += "," + patient.getInt( 1 );
            }
            
            jdbcTemplate.execute( "UPDATE patient SET representativeid=null WHERE representativeid in ( " + patientIds + " );" );
        }
        catch ( Exception ex )
        {
            log.error( ex );
        }
        finally
        {
            holder.close();
        }        
    }
    
    public void deleteOrganisation(String orgUnitIds )
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();
            
            ResultSet parent = statement
                .executeQuery( "select organisationunitid from organisationunit where organisationunitid in  ( " + orgUnitIds + ")" );

            String parentIds = "0";
            
            while ( parent.next() )
            {
                parentIds += "," + parent.getInt( 1 );
            }
            
            jdbcTemplate.execute( "UPDATE organisationunit SET parentid=null WHERE parentid in ( " + parentIds + " );" );
        }
        catch ( Exception ex )
        {
            log.error( ex );
        }
        finally
        {
            holder.close();
        }
    }
}
