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

import java.util.HashMap;
import java.util.Map;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
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
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    // -------------------------------------------------------------------------
    // ReportTableManager implementation
    // -------------------------------------------------------------------------

    public Map<String, Double> getAggregatedValueMap( ReportTable reportTable )
    {
        if ( reportTable.isOrganisationUnitGroupBased() )
        {
            return getAggregatedValueMapOrgUnitGroups( reportTable );
        }
        else
        {
            return getAggregatedValueMapOrgUnitHierarchy( reportTable );
        }
    }
    
    public Map<String, Double> getAggregatedValueMapOrgUnitGroups( ReportTable reportTable )
    {
        Map<String, Double> map = new HashMap<String, Double>();
        
        String dataElementIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( DataElement.class, reportTable.getDataElements() ) );
        String indicatorIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( Indicator.class, reportTable.getIndicators() ) );
        String periodIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( Period.class, reportTable.getAllPeriods() ) );
        String unitIds = TextUtils.getCommaDelimitedString( 
            ConversionUtils.getIdentifiers( NameableObject.class, reportTable.getAllUnits() ) );

        if ( reportTable.hasDataElements() )
        {
            final String sql = "SELECT dataelementid, periodid, organisationunitgroupid, SUM(value) FROM aggregatedorgunitdatavalue " + 
                "WHERE dataelementid IN (" + dataElementIds + ") AND periodid IN (" + periodIds + ") AND organisationunitgroupid IN (" + unitIds + ") " + 
                "AND organisationunitid = " + reportTable.getParentOrganisationUnit().getId() + " " +
                "GROUP BY dataelementid, periodid, organisationunitgroupid"; // Sum of category option combos

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
            
            while ( rowSet.next() )
            {
                String id = getIdentifier( getIdentifier( DataElement.class, rowSet.getInt( 1 ) ),
                    getIdentifier( Period.class, rowSet.getInt( 2 ) ),
                    getIdentifier( OrganisationUnitGroup.class, rowSet.getInt( 3 ) ) );

                map.put( id, rowSet.getDouble( 4 ) );
            }
        }

        if ( reportTable.hasIndicators() )
        {
            final String sql = "SELECT indicatorid, periodid, organisationunitgroupid, value FROM aggregatedorgunitindicatorvalue " + 
                "WHERE indicatorid IN (" + indicatorIds + ") AND periodid IN (" + periodIds + ") AND organisationunitgroupid IN (" + unitIds + ") " +
                "AND organisationunitid = " + reportTable.getParentOrganisationUnit().getId();

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
            
            while ( rowSet.next() )
            {
                String id = getIdentifier( getIdentifier( Indicator.class, rowSet.getInt( 1 ) ),
                    getIdentifier( Period.class, rowSet.getInt( 2 ) ),
                    getIdentifier( OrganisationUnitGroup.class, rowSet.getInt( 3 ) ) );

                map.put( id, rowSet.getDouble( 4 ) );
            }
        }

        return map;
    }
    
    public Map<String, Double> getAggregatedValueMapOrgUnitHierarchy( ReportTable reportTable )
    {
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
            ConversionUtils.getIdentifiers( NameableObject.class, reportTable.getAllUnits() ) );

        if ( reportTable.hasDataElements() )
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
        
        if ( reportTable.hasIndicators() )
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

        if ( reportTable.hasDataSets() )
        {
            final String sql = "SELECT datasetid, periodid, organisationunitid, value FROM aggregateddatasetcompleteness " + 
                "WHERE datasetid IN (" + dataSetIds + ") AND periodid IN (" + periodIds + ") AND organisationunitid IN (" + unitIds + ")";

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
            
            while ( rowSet.next() )
            {
                String id = getIdentifier( getIdentifier( DataSet.class, rowSet.getInt( 1 ) ),
                    getIdentifier( Period.class, rowSet.getInt( 2 ) ),
                    getIdentifier( OrganisationUnit.class, rowSet.getInt( 3 ) ) );

                map.put( id, rowSet.getDouble( 4 ) );
            }
        }
        
        if ( reportTable.isDimensional() )
        {
            final String sql = "SELECT dataelementid, categoryoptioncomboid, periodid, organisationunitid, value FROM aggregateddatavalue " + 
                "WHERE dataelementid IN (" + dataElementIds + ") AND periodid IN (" + periodIds + ") AND organisationunitid IN (" + unitIds + ")";

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
            
            while ( rowSet.next() )
            {
                String id = getIdentifier( getIdentifier( DataElement.class, rowSet.getInt( 1 ) ),
                    getIdentifier( DataElementCategoryOptionCombo.class, rowSet.getInt( 2 ) ),
                    getIdentifier( Period.class, rowSet.getInt( 3 ) ),
                    getIdentifier( OrganisationUnit.class, rowSet.getInt( 4 ) ) );

                map.put( id, rowSet.getDouble( 5 ) );
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

                SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
                
                while ( rowSet.next() )
                {
                    String id = getIdentifier( getIdentifier( DataElement.class, rowSet.getInt( 1 ) ),
                        getIdentifier( Period.class, rowSet.getInt( 2 ) ),
                        getIdentifier( OrganisationUnit.class, rowSet.getInt( 3 ) ),
                        getIdentifier( DataElementCategoryOption.class, categoryOption.getId() ) );
    
                    map.put( id, rowSet.getDouble( 4 ) );
                }
            }
        }

        return map;
    }

    public Map<String, Double> getAggregatedValueMap( Chart chart )
    {
        // A bit misplaced but we will merge chart and report table

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
    
    /**
     * TODO Temporary fix, we will phase out support for aggregation engine
     */
    public Map<String, Double> getAggregatedValueMapRealTime( Chart chart )
    {
        statementManager.initialise();
        
        Map<String, Double> map = new HashMap<String, Double>();

        if ( chart.hasDataElements() )
        {
            for ( DataElement dataElement : chart.getDataElements() )
            {
                for ( OrganisationUnit organisationUnit : chart.getOrganisationUnits() )
                {
                    for ( Period period : chart.getRelativePeriods() )
                    {
                        String id = getIdentifier( getIdentifier( DataElement.class, dataElement.getId() ),
                            getIdentifier( Period.class, period.getId() ),
                            getIdentifier( OrganisationUnit.class, organisationUnit.getId() ) );

                        map.put( id, aggregationService.getAggregatedDataValue( dataElement, null, period.getStartDate(), period.getEndDate(), organisationUnit ) );
                    }
                }
            }
        }
        
        if ( chart.hasIndicators() )
        {
            for ( Indicator indicator : chart.getIndicators() )
            {
                for ( OrganisationUnit organisationUnit : chart.getOrganisationUnits() )
                {
                    for ( Period period : chart.getRelativePeriods() )
                    {
                        String id = getIdentifier( getIdentifier( Indicator.class, indicator.getId() ),
                            getIdentifier( Period.class, period.getId() ),
                            getIdentifier( OrganisationUnit.class, organisationUnit.getId() ) );
                        
                        map.put( id, aggregationService.getAggregatedIndicatorValue( indicator, period.getStartDate(), period.getEndDate(), organisationUnit ) );
                    }
                }
            }
        }
        
        statementManager.destroy();
        
        return map;
    }
}
