package org.hisp.dhis.reporttable.jdbc;

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

import static org.hisp.dhis.reporttable.ReportTable.getIdentifier;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class JDBCReportTableManager
    implements ReportTableManager
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // ReportTableManager implementation
    // -------------------------------------------------------------------------

    public Map<String, Double> getAggregatedValueMap( ReportTable reportTable )
    {
        // TODO use jdbc template

        StatementHolder holder = statementManager.getHolder();

        Map<String, Double> map = new HashMap<String, Double>();

        String dataElementIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( DataElement.class, reportTable.getDataElements() ) );
        String indicatorIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( Indicator.class, reportTable.getIndicators() ) );
        String dataSetIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( DataSet.class,reportTable.getDataSets() ) );
        String periodIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( Period.class, reportTable.getAllPeriods() ) );
        String unitIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( OrganisationUnit.class, reportTable.getAllUnits() ) );

        try
        {
            if ( reportTable.hasDataElements() )
            {
                final String sql = "SELECT dataelementid, periodid, organisationunitid, SUM(value) FROM aggregateddatavalue " + 
                    "WHERE dataelementid IN (" + dataElementIds + ") AND periodid IN (" + periodIds + ") AND organisationunitid IN (" + unitIds + ") " + 
                    "GROUP BY dataelementid, periodid, organisationunitid"; // Sum of category option combos

                ResultSet resultSet = holder.getStatement().executeQuery( sql );

                while ( resultSet.next() )
                {
                    String id = getIdentifier( getIdentifier( DataElement.class, resultSet.getInt( 1 ) ),
                        getIdentifier( Period.class, resultSet.getInt( 2 ) ),
                        getIdentifier( OrganisationUnit.class, resultSet.getInt( 3 ) ) );

                    map.put( id, resultSet.getDouble( 4 ) );
                }
            }
            
            if ( reportTable.hasIndicators() )
            {
                final String sql = "SELECT indicatorid, periodid, organisationunitid, value FROM aggregatedindicatorvalue " + 
                    "WHERE indicatorid IN (" + indicatorIds + ") AND periodid IN (" + periodIds + ") AND organisationunitid IN (" + unitIds + ")";

                ResultSet resultSet = holder.getStatement().executeQuery( sql );

                while ( resultSet.next() )
                {
                    String id = getIdentifier( getIdentifier( Indicator.class, resultSet.getInt( 1 ) ),
                        getIdentifier( Period.class, resultSet.getInt( 2 ) ),
                        getIdentifier( OrganisationUnit.class, resultSet.getInt( 3 ) ) );

                    map.put( id, resultSet.getDouble( 4 ) );
                }
            }

            if ( reportTable.hasDataSets() )
            {
                final String sql = "SELECT datasetid, periodid, organisationunitid, value FROM aggregateddatasetcompleteness " + 
                    "WHERE datasetid IN (" + dataSetIds + ") AND periodid IN (" + periodIds + ") AND organisationunitid IN (" + unitIds + ")";

                ResultSet resultSet = holder.getStatement().executeQuery( sql );

                while ( resultSet.next() )
                {
                    String id = getIdentifier( getIdentifier( DataSet.class, resultSet.getInt( 1 ) ),
                        getIdentifier( Period.class, resultSet.getInt( 2 ) ),
                        getIdentifier( OrganisationUnit.class, resultSet.getInt( 3 ) ) );

                    map.put( id, resultSet.getDouble( 4 ) );
                }
            }
            
            if ( reportTable.isDimensional() )
            {
                final String sql = "SELECT dataelementid, categoryoptioncomboid, periodid, organisationunitid, value FROM aggregateddatavalue " + 
                    "WHERE dataelementid IN (" + dataElementIds + ") AND periodid IN (" + periodIds + ") AND organisationunitid IN (" + unitIds + ")";

                ResultSet resultSet = holder.getStatement().executeQuery( sql );
    
                while ( resultSet.next() )
                {
                    String id = getIdentifier( getIdentifier( DataElement.class, resultSet.getInt( 1 ) ),
                        getIdentifier( DataElementCategoryOptionCombo.class, resultSet.getInt( 2 ) ),
                        getIdentifier( Period.class, resultSet.getInt( 3 ) ),
                        getIdentifier( OrganisationUnit.class, resultSet.getInt( 4 ) ) );
    
                    map.put( id, resultSet.getDouble( 5 ) );
                }
            }
            
            if ( reportTable.doTotal() )
            {
                for ( DataElementCategoryOption categoryOption : reportTable.getCategoryCombo().getCategoryOptions() )
                {
                    String cocIds = TextUtils.getCommaDelimitedString( 
                        ConversionUtils.getIdentifiers( DataElementCategoryOptionCombo.class, categoryOption.getCategoryOptionCombos() ) );
                    
                    final String sql = "SELECT dataelementid, periodid, organisationunitid, SUM(value) FROM aggregateddatavalue " +
                        "WHERE dataelementid IN (" + dataElementIds + ") AND categoryoptioncomboid IN (" + cocIds +
                        ") AND periodid IN (" + periodIds + ") AND organisationunitid IN (" + unitIds +
                        ") GROUP BY dataelementid, periodid, organisationunitid"; // Sum of category option combos
                    
                    ResultSet resultSet = holder.getStatement().executeQuery( sql );
                    
                    while ( resultSet.next() )
                    {
                        String id = getIdentifier( getIdentifier( DataElement.class, resultSet.getInt( 1 ) ),
                            getIdentifier( Period.class, resultSet.getInt( 2 ) ),
                            getIdentifier( OrganisationUnit.class, resultSet.getInt( 3 ) ),
                            getIdentifier( DataElementCategoryOption.class, categoryOption.getId() ) );
        
                        map.put( id, resultSet.getDouble( 4 ) );
                    }
                }
            }

            return map;
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to get aggregated value map", ex );
        }
        finally
        {
            holder.close();
        }
    }

    public Map<String, Double> getAggregatedValueMap( Chart chart )
    {
        // A bit misplaced but we will merge chart and report table soon

        Map<String, Double> map = new HashMap<String, Double>();

        String dataElementIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( DataElement.class, chart.getDataElements() ) );
        String indicatorIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( Indicator.class, chart.getIndicators() ) );
        String periodIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( Period.class, chart.getRelativePeriods() ) );
        String unitIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( OrganisationUnit.class, chart.getAllOrganisationUnits() ) );

        if ( chart.hasDataElements() )
        {
            final String sql = "SELECT dataelementid, periodid, organisationunitid, SUM(value) FROM aggregateddatavalue " + 
                "WHERE dataelementid IN (" + dataElementIds + ") AND periodid IN (" + periodIds + ") AND organisationunitid IN (" + unitIds + ") " + 
                "GROUP BY dataelementid, periodid, organisationunitid"; // Sum of category option combos

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
            
            while ( rowSet.next() )
            {
                String id = getIdentifier( getIdentifier( DataElement.class, rowSet.getInt( 1 ) ),
                    getIdentifier( Period.class, rowSet.getInt( 2 ) ),
                    getIdentifier( OrganisationUnit.class, rowSet.getInt( 3 ) ) );

                map.put( id, rowSet.getDouble( 4 ) );
            }
        }
        
        if ( chart.hasIndicators() )
        {
            final String sql = "SELECT indicatorid, periodid, organisationunitid, value FROM aggregatedindicatorvalue " + 
                "WHERE indicatorid IN (" + indicatorIds + ") AND periodid IN (" + periodIds + ") AND organisationunitid IN (" + unitIds + ")";

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
            
            while ( rowSet.next() )
            {
                String id = getIdentifier( getIdentifier( Indicator.class, rowSet.getInt( 1 ) ),
                    getIdentifier( Period.class, rowSet.getInt( 2 ) ),
                    getIdentifier( OrganisationUnit.class, rowSet.getInt( 3 ) ) );

                map.put( id, rowSet.getDouble( 4 ) );
            }
        }
        
        return map;
    }
}
