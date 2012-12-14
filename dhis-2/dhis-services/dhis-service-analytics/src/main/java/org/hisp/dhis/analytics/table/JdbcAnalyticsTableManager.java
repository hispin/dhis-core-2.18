package org.hisp.dhis.analytics.table;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AnalyticsTableManager;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;

/**
 * This class manages the analytics table. The analytics table is a denormalized
 * table designed for analysis which contains raw data values. It has columns for
 * each organisation unit group set and organisation unit level. Also, columns
 * for dataelementid, periodid, organisationunitid, categoryoptioncomboid, value.
 * 
 * The analytics table is horizontally partitioned. The partition key is the start 
 * date of the  period of the data record. The table is partitioned according to 
 * time span with one partition per calendar quarter.
 * 
 * The data records in this table are not aggregated. Typically, queries will
 * aggregate in organisation unit hierarchy dimension, in the period/time dimension,
 * and the category dimensions, as well as organisation unit group set dimensions.
 * 
 * @author Lars Helge Overland
 */
public class JdbcAnalyticsTableManager
    implements AnalyticsTableManager
{
    private static final Log log = LogFactory.getLog( JdbcAnalyticsTableManager.class );

    public static final String PREFIX_ORGUNITGROUPSET = "ougs_";
    public static final String PREFIX_ORGUNITLEVEL = "uidlevel";
    public static final String PREFIX_INDEX = "in_";
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;
   
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------
  
    //TODO average aggregation operator data, pre-aggregate in time dimension, not in org unit dimension
    
    public void createTable( String tableName )
    {
        final String sqlDrop = "drop table " + tableName;
        
        executeSilently( sqlDrop );
        
        String sqlCreate = "create table " + tableName + " (";
        
        for ( String[] col : getDimensionColumns() )
        {
            sqlCreate += col[0] + " " + col[1] + ",";
        }
        
        sqlCreate += "value double precision)";
        
        log.info( "Create SQL: " + sqlCreate );
        
        executeSilently( sqlCreate );
    }
    
    @Async
    public Future<?> createIndexesAsync( String tableName, List<String> columns )
    {
        for ( String column : columns )
        {        
            final String index = PREFIX_INDEX + column + "_" + tableName + "_" + CodeGenerator.generateCode();
            
            final String sql = "create index " + index + " on " + tableName + " (" + column + ")";
                
            executeSilently( sql );
            
            log.info( "Created index: " + index );
        }
        
        log.info( "Indexes created" );
        
        return null;
    }

    public void swapTable( String tableName )
    {
        final String realTable = tableName.replaceFirst( TABLE_TEMP_SUFFIX, "" );
        
        final String sqlDrop = "drop table " + realTable;
        
        executeSilently( sqlDrop );
        
        final String sqlAlter = "alter table " + tableName + " rename to " + realTable;
        
        executeSilently( sqlAlter );
    }
    
    @Async
    public Future<?> populateTableAsync( String tableName, Date startDate, Date endDate )
    {
        populateTable( tableName, startDate, endDate, "cast(dv.value as double precision)", "int" );
        
        populateTable( tableName, startDate, endDate, "1 as value" , "bool" );
        
        return null;
    }
    
    private void populateTable( String tableName, Date startDate, Date endDate, String valueExpression, String valueType )
    {
        final String start = DateUtils.getMediumDateString( startDate );
        final String end = DateUtils.getMediumDateString( endDate );
        
        String insert = "insert into " + tableName + " (";
        
        for ( String[] col : getDimensionColumns() )
        {
            insert += col[0] + ",";
        }
        
        insert += "value) ";
        
        String select = "select ";
        
        for ( String[] col : getDimensionColumns() )
        {
            select += col[2] + ",";
        }
        
        select = select.replace( "organisationunitid", "sourceid" ); // Legacy fix
        
        select += valueExpression + " " +
            "from datavalue dv " +
            "left join _dataelementgroupsetstructure degs on dv.dataelementid=degs.dataelementid " +
            "left join _organisationunitgroupsetstructure ougs on dv.sourceid=ougs.organisationunitid " +
            "left join _orgunitstructure ous on dv.sourceid=ous.organisationunitid " +
            "left join _period_no_disagg_structure ps on dv.periodid=ps.periodid " +
            "left join dataelement de on dv.dataelementid=de.dataelementid " +
            "left join categoryoptioncombo coc on dv.categoryoptioncomboid=coc.categoryoptioncomboid " +
            "left join period pe on dv.periodid=pe.periodid " +
            "where de.valuetype='" + valueType + "' " +
            "and pe.startdate >= '" + start + "' " +
            "and pe.startdate <= '" + end + "'";

        final String sql = insert + select;
        
        log.info( "Populate SQL: "+ sql );
        
        jdbcTemplate.execute( sql );
    }

    /**
     * Returns a list of dimension columns. Each entry is an array with:
     * 
     * 0 = column name
     * 1 = data type
     * 2 = column alias and name
     */
    public List<String[]> getDimensionColumns()
    {
        List<String[]> columns = new ArrayList<String[]>();

        Collection<DataElementGroupSet> dataElementGroupSets =
            dataElementService.getCompulsoryDataElementGroupSets();
        
        Collection<OrganisationUnitGroupSet> orgUnitGroupSets = 
            organisationUnitGroupService.getCompulsoryOrganisationUnitGroupSets();
        
        Collection<OrganisationUnitLevel> levels =
            organisationUnitService.getOrganisationUnitLevels();

        for ( DataElementGroupSet groupSet : dataElementGroupSets )
        {
            String[] col = { groupSet.getUid(), "character(11)", "degs." + groupSet.getUid() };
            columns.add( col );
        }
        
        for ( OrganisationUnitGroupSet groupSet : orgUnitGroupSets )
        {
            String[] col = { groupSet.getUid(), "character(11)", "ougs." + groupSet.getUid() };
            columns.add( col );
        }
        
        for ( OrganisationUnitLevel level : levels )
        {
            String column = PREFIX_ORGUNITLEVEL + level.getLevel();
            String[] col = { column, "character(11)", "ous." + column };
            columns.add( col );
        }
        
        for ( PeriodType periodType : PeriodType.getAvailablePeriodTypes().subList( 0, 7 ) )
        {
            String column = periodType.getName().toLowerCase();
            String[] col = { column, "character varying(10)", "ps." + column };
            columns.add( col );
        }
        
        String[] de = { "de", "character(11) not null", "de.uid" };
        String[] co = { "coc", "character(11) not null", "coc.uid" };
        
        columns.addAll( Arrays.asList( de, co ) );
        
        return columns;
    }
    
    public List<String> getDimensionColumnNames()
    {
        List<String[]> columns = getDimensionColumns();
        
        List<String> columnNames = new ArrayList<String>();
        
        for ( String[] column : columns )
        {
            columnNames.add( column[0] );
        }
        
        return columnNames;
    }

    public Date getEarliestData()
    {
        final String sql = "select min(pe.startdate) from datavalue dv " +
            "join period pe on dv.periodid=pe.periodid";
        
        return jdbcTemplate.queryForObject( sql, Date.class );
    }

    public Date getLatestData()
    {
        final String sql = "select max(pe.startdate) from datavalue dv " +
            "join period pe on dv.periodid=pe.periodid";
        
        return jdbcTemplate.queryForObject( sql, Date.class );
    }
    
    public void pruneTable( String tableName )
    {
        final String sqlCount = "select count(*) from " + tableName;
        
        log.info( "Count SQL: " + sqlCount );
        
        final boolean empty = jdbcTemplate.queryForInt( sqlCount ) == 0;
        
        if ( empty )
        {
            final String sqlDrop = "drop table " + tableName;
            
            executeSilently( sqlDrop );
            
            log.info( "Drop SQL: " + sqlDrop );
        }
    }
    
    public void dropTable( String tableName )
    {
        final String realTable = tableName.replaceFirst( TABLE_TEMP_SUFFIX, "" );
        
        executeSilently( "drop table " + tableName );
        executeSilently( "drop table " + realTable );
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
  
    /**
     * Executes a SQL statement. Ignores existing tables/indexes when attempting
     * to create new.
     */
    private void executeSilently( String sql )
    {
        try
        {
            jdbcTemplate.execute( sql );
        }
        catch ( BadSqlGrammarException ex )
        {
            log.warn( ex.getMessage() );
        }
    }
}
