package org.hisp.dhis.jdbc.statementbuilder;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.RelativePeriodType;

/**
 * @author Lars Helge Overland
 * @version $Id: H2StatementBuilder.java 5715 2008-09-17 14:05:28Z larshelg $
 */
public class H2StatementBuilder
    extends AbstractStatementBuilder
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public H2StatementBuilder()
    {
        super();
    }    

    // -------------------------------------------------------------------------
    // AbstractStatementBuilder implementation
    // -------------------------------------------------------------------------
 
    public String getInsertStatementOpening( String table )
    {
        final StringBuffer buffer = new StringBuffer();
        
        buffer.append( "INSERT INTO " + table + " (" );
        
        for ( String column : columns )
        {
            buffer.append( column + SEPARATOR );
        }
        
        if ( columns.size() > 0 )
        {
            buffer.deleteCharAt( buffer.length() - 1 );
        }
        
        buffer.append( BRACKET_END + " VALUES " );
        
        columns.clear();
        
        return buffer.toString();
    }
    
    public String getNoColumnInsertStatementOpening( String table )
    {
        return "INSERT INTO " + table + " VALUES ";
    }
    
    public String getInsertStatementValues()
    {
        final StringBuffer buffer = new StringBuffer();
        
        buffer.append( BRACKET_START );
        
        for ( String value : values )
        {
            buffer.append( value + SEPARATOR );
        }
        
        if ( values.size() > 0 )
        {
            buffer.deleteCharAt( buffer.length() - 1 );
        }
        
        buffer.append( BRACKET_END + SEPARATOR );
        
        values.clear();
        
        return buffer.toString();
    }
    
    public String getUpdateStatement( String table )
    {
        final StringBuffer buffer = new StringBuffer();
        
        buffer.append( "UPDATE " + table + " SET " );

        Iterator<String> columnIterator = columns.iterator();
        Iterator<String> valueIterator = values.iterator();
        
        while ( columnIterator.hasNext() )
        {
            buffer.append( columnIterator.next() + "=" + valueIterator.next() + SEPARATOR );
        }
        
        if ( columns.size() > 0 && values.size() > 0 )
        {
            buffer.deleteCharAt( buffer.length() - 1 );
        }
        
        buffer.append( " WHERE " + identifierColumnName + "=" + identifierColumnValue );
        
        columns.clear();
        values.clear();
        
        return buffer.toString();
    }
    
    public String getValueStatement( String table, String returnField, String compareField, String value )
    {
        return "SELECT " + returnField + " FROM " + table + " WHERE " + compareField + " = '" + sqlEncode( value ) + "'";
    }
    
    public String getValueStatement( String table, String returnField1, String returnField2, String compareField1, String value1, String compareField2, String value2 )
    {
        return "SELECT " + returnField1 + ", " + returnField2 + " FROM " + table + " WHERE " + compareField1 + "='" + sqlEncode( value1 ) + "' AND " + compareField2 + "='" + value2 + "'";
    }
    
    public String getValueStatement( String table, String returnField, Map<String, String> fieldMap, boolean union )
    {
        final String operator = union ? " AND " : " OR ";
        
        final StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append( "SELECT " ).append( returnField ).append( " FROM " ).append( table ).append( " WHERE " );
        
        for ( Entry<String, String> entry : fieldMap.entrySet() )
        {
            sqlBuffer.append( entry.getKey() ).append( "='" ).append( sqlEncode( entry.getValue() ) ).append( "'" ).append( operator );
        }

        String sql = sqlBuffer.toString();        
        sql = sql.substring( 0, sql.length() - operator.length() );
        
        return sql;
    }

    public String getDoubleColumnType()
    {
        return "DOUBLE";
    }
    
    public String getPeriodIdentifierStatement( Period period )
    {
        return
            "SELECT periodid FROM period WHERE periodtypeid=" + period.getPeriodType().getId() + " " + 
            "AND startdate='" + getDateString( period.getStartDate() ) + "' " +
            "AND enddate='" + getDateString( period.getEndDate() ) + "'";
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
            "value DOUBLE );";
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
            "factor DOUBLE, " +
            "value DOUBLE, " +
            "numeratorvalue DOUBLE, " +
            "denominatorvalue DOUBLE );";
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
            "value DOUBLE, " +
            "valueOnTime DOUBLE );";
    }
    
    public String getCreateDataValueIndex()
    {
        return
            "CREATE INDEX crosstab " +
            "ON datavalue ( periodid, sourceid );";
    }

    public String getDeleteRelativePeriods()
    {
        return
            "DELETE FROM period " +
            "USING periodtype " +
            "WHERE period.periodtypeid = periodtype.periodtypeid " +
            "AND periodtype.name = '" + RelativePeriodType.NAME + "';";
    }

    public String getDeleteZeroDataValues()
    {
        return
            "DELETE FROM datavalue " +
            "WHERE datavalue.value IN ( '0', '0.', '.0', '0.0', ' 0', '0 ', '0 0' )";
    }

    public int getMaximumNumberOfColumns()
    {
        return 1580; // TODO verify
    }

    // -------------------------------------------------------------------------
    // AbstractStatementBuilder override methods
    // -------------------------------------------------------------------------
    
    @Override
    protected String sqlEncode( String string )
    {
        if ( string != null )
        {
            string = string.endsWith( "\\" ) ? string.substring( 0, string.length() - 1 ) : string;
            string = string.replaceAll( QUOTE, QUOTE + QUOTE );
            
            return string;
        }
        
        return null;
    }
}
