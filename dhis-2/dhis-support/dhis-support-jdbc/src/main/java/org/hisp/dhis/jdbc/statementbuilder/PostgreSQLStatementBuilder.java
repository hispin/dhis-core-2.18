package org.hisp.dhis.jdbc.statementbuilder;

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

import static org.hisp.dhis.system.util.DateUtils.getSqlDateString;

import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.period.Period;

/**
 * @author Lars Helge Overland
 * @version $Id: PostgreSQLStatementBuilder.java 5715 2008-09-17 14:05:28Z larshelg $
 */
public class PostgreSQLStatementBuilder
    implements StatementBuilder
{    
    public String getDoubleColumnType()
    {
        return "DOUBLE PRECISION";
    }
    
    public String encode( String value )
    {
        if ( value != null )
        {
            value = value.endsWith( "\\" ) ? value.substring( 0, value.length() - 1 ) : value;
            value = value.replaceAll( QUOTE, "\\\\" + QUOTE );
        }
        
        return QUOTE + value + QUOTE;
    }
    
    public String getPeriodIdentifierStatement( Period period )
    {
        return
            "SELECT periodid FROM period WHERE periodtypeid=" + period.getPeriodType().getId() + " " + 
            "AND startdate='" + getSqlDateString( period.getStartDate() ) + "' " +
            "AND enddate='" + getSqlDateString( period.getEndDate() ) + "'";
    }

    public String getCreateAggregatedDataValueTable()
    {
        return
            "CREATE TABLE aggregateddatavalue ( " +
            "dataelementid INTEGER, " +
            "categoryoptioncomboid INTEGER, " +
            "periodid INTEGER, " +
            "organisationunitid INTEGER, " +
            "periodtypeid INTEGER, " +
            "level INTEGER, " +
            "value DOUBLE PRECISION );";
    }
    
    public String getCreateAggregatedIndicatorTable()
    {
        return
            "CREATE TABLE aggregatedindicatorvalue ( " +
            "indicatorid INTEGER, " +
            "periodid INTEGER, " +
            "organisationunitid INTEGER, " +
            "periodtypeid INTEGER, " +
            "level INTEGER, " +
            "annualized VARCHAR( 10 ), " +
            "factor DOUBLE PRECISION, " +
            "value DOUBLE PRECISION, " +
            "numeratorvalue DOUBLE PRECISION, " +
            "denominatorvalue DOUBLE PRECISION );";
    }

    public String getCreateDataSetCompletenessTable()
    {
        return
            "CREATE TABLE aggregateddatasetcompleteness ( " +
            "datasetid INTEGER, " +
            "periodid INTEGER, " +
            "periodname VARCHAR( 30 ), " +
            "organisationunitid INTEGER, " +
            "reporttableid INTEGER, " +
            "sources INTEGER, " +
            "registrations INTEGER, " +
            "registrationsOnTime INTEGER, " +
            "value DOUBLE PRECISION, " +
            "valueOnTime DOUBLE PRECISION );";
    }

    public String getCreateDataValueIndex()
    {
        return
            "CREATE INDEX crosstab " +
            "ON datavalue ( periodid, sourceid );";
    }
    
    public String getDeleteZeroDataValues()
    {
        return
            "DELETE FROM datavalue " +
            "USING dataelement " +
            "WHERE datavalue.dataelementid = dataelement.dataelementid " +
            "AND dataelement.aggregationtype = 'sum' " +
            "AND datavalue.value = '0'";
    }

    public int getMaximumNumberOfColumns()
    {
        return 1580; // TODO verify
    }

    public String getDropDatasetForeignKeyForDataEntryFormTable()
    {
        return  "ALTER TABLE dataentryform DROP CONSTRAINT fk_dataentryform_datasetid;" ;
    }

    @Override
    public String getMoveDataValueToDestination( int sourceId, int destinationId )
    {
        return "UPDATE datavalue AS d1 SET sourceid=" + destinationId + " " + "WHERE sourceid=" + sourceId + " "
        + "AND NOT EXISTS ( " + "SELECT * from datavalue AS d2 " + "WHERE d2.sourceid=" + destinationId + " "
        + "AND d1.dataelementid=d2.dataelementid " + "AND d1.periodid=d2.periodid "
        + "AND d1.categoryoptioncomboid=d2.categoryoptioncomboid );";
    }

    @Override
    public String getSummarizeDestinationAndSourceWhereMatching( int sourceId, int destId )
    {
        return "UPDATE datavalue AS d1 SET value=( " + "SELECT SUM( CAST( value AS "
            + getDoubleColumnType() + " ) ) " + "FROM datavalue as d2 "
            + "WHERE d1.dataelementid=d2.dataelementid " + "AND d1.periodid=d2.periodid "
            + "AND d1.categoryoptioncomboid=d2.categoryoptioncomboid " + "AND d2.sourceid IN ( " + destId + ", "
            + sourceId + " ) ) " + "FROM dataelement AS de " + "WHERE d1.sourceid=" + destId + " "
            + "AND d1.dataelementid=de.dataelementid " + "AND de.valuetype='int';";
    }

    @Override
    public String getUpdateDestination( int destDataElementId, int destCategoryOptionComboId,
        int sourceDataElementId, int sourceCategoryOptionComboId )
    {
        return "UPDATE datavalue AS d1 SET dataelementid=" + destDataElementId + ", categoryoptioncomboid="
            + destCategoryOptionComboId + " " + "WHERE dataelementid=" + sourceDataElementId
            + " and categoryoptioncomboid=" + sourceCategoryOptionComboId + " " + "AND NOT EXISTS ( "
            + "SELECT * FROM datavalue AS d2 " + "WHERE d2.dataelementid=" + destDataElementId + " "
            + "AND d2.categoryoptioncomboid=" + destCategoryOptionComboId + " " + "AND d1.periodid=d2.periodid "
            + "AND d1.sourceid=d2.sourceid );";

    }

    @Override
    public String getMoveFromSourceToDestination( int destDataElementId, int destCategoryOptionComboId,
        int sourceDataElementId, int sourceCategoryOptionComboId )
    {
        return "UPDATE datavalue SET value=d2.value,storedby=d2.storedby,lastupdated=d2.lastupdated,comment=d2.comment,followup=d2.followup "
            + "FROM datavalue AS d2 "
            + "WHERE datavalue.periodid=d2.periodid "
            + "AND datavalue.sourceid=d2.sourceid "
            + "AND datavalue.lastupdated<d2.lastupdated "
            + "AND datavalue.dataelementid="
            + destDataElementId
            + " AND datavalue.categoryoptioncomboid="
            + destCategoryOptionComboId
            + " "
            + "AND d2.dataelementid="
            + sourceDataElementId + " AND d2.categoryoptioncomboid=" + sourceCategoryOptionComboId + ";";
    }
}
