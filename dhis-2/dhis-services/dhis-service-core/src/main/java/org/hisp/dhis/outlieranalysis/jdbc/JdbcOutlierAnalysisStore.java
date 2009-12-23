package org.hisp.dhis.outlieranalysis.jdbc;

/*
 * Copyright (c) 2004-${year}, University of Oslo
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
import java.sql.SQLException;
import java.util.Collection;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.amplecode.quick.mapper.ObjectMapper;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.outlieranalysis.OutlierAnalysisStore;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.objectmapper.DeflatedDataValueNameMinMaxRowMapper;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.TextUtils;

/**
 * @author Lars Helge Overland
 */
public class JdbcOutlierAnalysisStore
    implements OutlierAnalysisStore
{
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    private StatementBuilder statementBuilder;
            
    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    public Double getStandardDeviation( DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo, OrganisationUnit organisationUnit )
    {
        final String sql = 
            "SELECT STDDEV( CAST( value AS " + statementBuilder.getDoubleColumnType() + " ) ) FROM datavalue " +
            "WHERE dataelementid='" + dataElement.getId() + "' " +
            "AND categoryoptioncomboid='" + categoryOptionCombo.getId() + "' " +
            "AND sourceid='" + organisationUnit.getId() + "'";
        
        return statementManager.getHolder().queryForDouble( sql );
    }
    
    public Double getAverage( DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo, OrganisationUnit organisationUnit )
    {
        final String sql = 
            "SELECT AVG( CAST( value AS " + statementBuilder.getDoubleColumnType() + " ) ) FROM datavalue " +
            "WHERE dataelementid='" + dataElement.getId() + "' " +
            "AND categoryoptioncomboid='" + categoryOptionCombo.getId() + "' " +
            "AND sourceid='" + organisationUnit.getId() + "'";
        
        return statementManager.getHolder().queryForDouble( sql );
    }
    
    public Collection<DeflatedDataValue> getDeflatedDataValues( DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo,
        Collection<Period> periods, OrganisationUnit organisationUnit, int lowerBound, int upperBound )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        final ObjectMapper<DeflatedDataValue> mapper = new ObjectMapper<DeflatedDataValue>();
        
        final String periodIds = TextUtils.getCommaDelimitedString( ConversionUtils.getIdentifiers( Period.class, periods ) );
        
        try
        {
            final String sql =
                "SELECT dv.dataelementid, dv.periodid, dv.sourceid, dv.categoryoptioncomboid, dv.value, dv.storedby, dv.lastupdated, " +
                "dv.comment, dv.followup, '" + lowerBound + "' AS minvalue, '" + upperBound + "' AS maxvalue, de.name AS dataelementname, " +
                "pe.startdate, pe.enddate, pt.name as periodtypename, ou.name AS sourcename, cc.categoryoptioncomboname " +
                "FROM datavalue AS dv " +                
                "JOIN dataelement AS de USING (dataelementid) " +
                "JOIN period AS pe USING (periodid) " +
                "JOIN periodtype AS pt USING (periodtypeid) " +
                "JOIN source AS sr USING (sourceid) " +
                "JOIN organisationunit AS ou ON ou.organisationunitid=sr.sourceid " +
                "LEFT JOIN categoryoptioncomboname AS cc USING (categoryoptioncomboid) " +
                "WHERE dv.dataelementid='" + dataElement.getId() + "' " +
                "AND dv.categoryoptioncomboid='" + categoryOptionCombo.getId() + "' " +
                "AND dv.periodid IN (" + periodIds + ") " +
                "AND dv.sourceid='" + organisationUnit.getId() + "' " +
                "AND ( CAST( dv.value AS " + statementBuilder.getDoubleColumnType() + " ) < '" + lowerBound + "' " +
                "OR CAST( dv.value AS " + statementBuilder.getDoubleColumnType() + " ) > '" + upperBound + "' )";
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return mapper.getCollection( resultSet, new DeflatedDataValueNameMinMaxRowMapper() );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get deflated data values", ex );
        }
        finally
        {
            holder.close();
        }
    }
}
