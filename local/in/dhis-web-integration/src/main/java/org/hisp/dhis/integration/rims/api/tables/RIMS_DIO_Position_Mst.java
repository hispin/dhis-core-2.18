/*
 * Copyright (c) 2004-2008, University of Oslo
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
package org.hisp.dhis.integration.rims.api.tables;

import java.sql.SQLException;

import org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement;

/**
 * @author Leif Arne Storset
 * @version $Id$
 */
public class RIMS_DIO_Position_Mst
    extends DistrictTable
{
    public String getDescription()
    {
        return "District budget information";
    }
    public void fillInTotals() throws SQLException
    {
        String query = "UPDATE "
            + getTableName()
            + " SET"
            + " budget_cbal = budget_obal + budget_received - budget_released - budget_disbursed,"
            + " utilized_received = budget_received,"
            + " cuml_exp = utilizedtillpre_month + utilizedduring_month,"
            + " balance = utilized_released - utilizedtillpre_month - utilizedduring_month - ret_state"
            + " WHERE budget_cbal IS NULL" 
            + "   OR utilized_received IS NULL"
            + "   OR cuml_exp IS NULL" 
            + "   OR balance IS NULL";
        executeUpdate( query );
    }

    public String getTableName()
    {
        return "dio_position_mst";
    }

    public void markIfComplete()
    {
        // TODO Auto-generated method stub
    }

    public int insertData( RIMSDistrict rimsOrgUnit, int month,
        int year, RIMS_Mapping_DataElement mappingDataElement, String resValue ) throws SQLException
    {
        // TODO Generate utilized_released and utilizedtillpre_month based on
        // previous month's data. Perhaps using a form of CalculatedDataElements?
        
        // Need to insert first, since dio_position is a required field.
        String update = "INSERT INTO "+ getTableName() +
                       " ( mnth, yr, district_code, state_code, dio_position," +
                       "   created_by, created_date )" +
                       " VALUES ( ?, ?, ?, ?, ?, ?, ? )";
        Object params[] = 
        {
            month,
            year,
            rimsOrgUnit.getDistrict_code(),
            rimsOrgUnit.getState_code(),
            "N",
            "DHIS2",
            new java.sql.Date( new java.util.Date().getTime() )
        };
        
        int ret = executeUpdate( update, params );
        // Now we can add the actual data
        return ret + updateData( rimsOrgUnit, month, year, mappingDataElement, resValue );
    }

    public boolean isData( RIMSDistrict district, int month, int year,
        RIMS_Mapping_DataElement mappingDataelement ) throws SQLException
    {
        String query = "SELECT *" +
                      " FROM "+ getTableName() +
                      " WHERE district_code = ?" +
                      "   AND mnth = ?" +
                      "   AND yr = ?";
        Object[] params = 
        {
          district.getCode(),
          month,
          year
        };
        return existingData( query, params );
    }

    public int updateData( RIMSDistrict rimsOrgUnit, int month,
        int year, RIMS_Mapping_DataElement mappingDataElement, String resValue ) throws SQLException
    {
        // TODO Generate utilized_released and utilizedtillpre_month based on
        // previous month's data. Perhaps using modified CalculatedDataElements?

        // RIMS codes booleans as Y/N
        if ( mappingDataElement.getRimsColumn().equals( "dio_position" ) )
        {
            resValue = resValue.equals( "true" ) ? "Y" : "N"; 
        }
        
        String update = "UPDATE "+ getTableName() +
                       " SET "+ mappingDataElement.getRimsColumn() +" = ?"+
                       " WHERE district_code = ?" +
                       "   AND mnth = ?" +
                       "   AND yr = ?";
        Object[] params =
        {
            resValue,
            rimsOrgUnit.getDistrict_code(),
            month,
            year
        };
        return executeUpdate( update, params );
    }
    
    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMSDistrict orgUnit, int month, int year ) throws SQLException
    {
        String getColumn = mappingDataElement.getRimsColumn();
        String query = "SELECT "+ getColumn +
                      " FROM "+ getTableName() +
                      " WHERE district_code = ?" +
                      "   AND mnth = ?" +
                      "   AND yr = ?";
        Object[] params =
        {
            orgUnit.getDistrict_code(),
            month,
            year
        };
        String ret = executeQuery( query, getColumn, params );
        // RIMS codes booleans as Y/N
        if ( mappingDataElement.getRimsColumn().equals( "dio_position" ) )
        {
            if ( ret.equals( "Y" ) )
            {
                return "true";
            }
            else if ( ret.equals( "N" ) )
            {
                return "false";
            } 
            return null; 
        }
        return ret;
    }
}
