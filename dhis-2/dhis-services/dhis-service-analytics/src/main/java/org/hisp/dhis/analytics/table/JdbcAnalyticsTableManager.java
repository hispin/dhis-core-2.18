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
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AnalyticsTableManager;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodType;
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
 * The data records in this table are not aggregated. Typically, queries will
 * aggregate in organisation unit hierarchy dimension, in the period/time dimension,
 * and the category dimensions, as well as org unit group set dimensions.
 * 
 * @author Lars Helge Overland
 */
public class JdbcAnalyticsTableManager
    implements AnalyticsTableManager
{
    private static final Log log = LogFactory.getLog( JdbcAnalyticsTableManager.class );
    
    public static final String PREFIX_ORGUNITGROUPSET = "ougs_";
    public static final String PREFIX_ORGUNITLEVEL = "idlevel";
    public static final String PREFIX_INDEX = "index_";
    public static final String TABLE_NAME = "analytics";
    public static final String TABLE_NAME_TEMP = "analytics_temp";
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;
   
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------
  
    //TODO shard on data quarter based on start date
    //TODO average aggregation operator data, pre-aggregate in time dimension, not in org unit dimension
        
    public void createTable()
    {
        final String sqlDrop = "drop table " + TABLE_NAME_TEMP;
        
        executeSilently( sqlDrop );
        
        String sqlCreate = "create table " + TABLE_NAME_TEMP + " (";
        
        for ( String[] col : getDimensionColumns() )
        {
            sqlCreate += col[0] + " " + col[1] + ",";
        }
        
        sqlCreate += "value double precision)";
        
        log.info( "Create SQL: " + sqlCreate );
        
        executeSilently( sqlCreate );
    }
    
    @Async
    public Future<?> createIndexesAsync( List<String> columns )
    {
        for ( String column : columns )
        {        
            final String sql = "create index " + PREFIX_INDEX +
                column + " on " + TABLE_NAME_TEMP + " (" + column + ")";
                
            executeSilently( sql );
            
            log.info( "Created index on column: " + column );
        }
        
        log.info( "Indexes created" );
        
        return null;
    }

    public void swapTable()
    {
        final String sqlDrop = "drop table " + TABLE_NAME;
        
        executeSilently( sqlDrop );
        
        final String sqlAlter = "alter table " + TABLE_NAME_TEMP + " rename to " + TABLE_NAME;
        
        jdbcTemplate.execute( sqlAlter );
    }
    
    public void populateTable()
    {
        populateTable( "cast(dv.value as double precision)" , "int" );
        
        populateTable( "1 as value" , "bool" );
    }
    
    private void populateTable( String valueExpression, String valueType )
    {
        String insert = "insert into " + TABLE_NAME_TEMP + " (";
        
        for ( String[] col : getDimensionColumns() )
        {
            insert += col[0] + ",";
        }
        
        insert += "value) ";
        
        String select = "select ";
        
        for ( String[] col : getDimensionColumns() )
        {
            select += col[2] + col[0] + ",";
        }
        
        select = select.replace( "organisationunitid", "sourceid" ); // Legacy fix
        
        select += valueExpression + " " +
            "from datavalue dv " +
            "left join _organisationunitgroupsetstructure ougs on dv.sourceid=ougs.organisationunitid " +
            "left join _orgunitstructure ous on dv.sourceid=ous.organisationunitid " +
            "left join _period_no_disaggregation_structure ps on dv.periodid=ps.periodid " +
            "left join dataelement de on dv.dataelementid=de.dataelementid " +
            "left join period pe on dv.periodid=pe.periodid " +
            "where de.valuetype='" + valueType + "' and pe.startdate >= '2011-10-01'";

        final String sql = insert + select;
        
        log.info( "Populate SQL: "+ sql );
        
        jdbcTemplate.execute( sql );
    }

    public List<String[]> getDimensionColumns()
    {
        List<String[]> columns = new ArrayList<String[]>();

        Collection<OrganisationUnitGroupSet> orgUnitGroupSets = 
            organisationUnitGroupService.getCompulsoryOrganisationUnitGroupSets();
        
        Collection<OrganisationUnitLevel> levels =
            organisationUnitService.getOrganisationUnitLevels();

        for ( OrganisationUnitGroupSet groupSet : orgUnitGroupSets )
        {
            String[] col = { PREFIX_ORGUNITGROUPSET + groupSet.getUid(), "integer", "ougs." };
            columns.add( col );
        }
        
        for ( OrganisationUnitLevel level : levels )
        {
            String[] col = { PREFIX_ORGUNITLEVEL + level.getLevel(), "integer", "ous." };
            columns.add( col );
        }
        
        for ( PeriodType periodType : PeriodType.getAvailablePeriodTypes().subList( 0, 7 ) )
        {
            String[] col = { periodType.getName().toLowerCase(), "character varying(10)", "ps." };
            columns.add( col );
        }
        
        String[] de = { "dataelementid", "integer not null", "dv." };
        String[] pe = { "periodid", "integer not null", "dv." };
        String[] ou = { "organisationunitid", "integer not null", "dv." };
        String[] co = { "categoryoptioncomboid", "integer not null", "dv." };
        
        columns.addAll( Arrays.asList( de, pe, ou, co ) );
        
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
