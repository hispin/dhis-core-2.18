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

import java.sql.Date;
import java.sql.SQLException;

import org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement;

/**
 * @author Leif Arne Storset
 * @version $Id$
 */
public class RIMS_Surveillance_Dtl
    extends PHCTable
{
    public String getDescription()
    {
        return "Disease surveillance";
    }
    public boolean isData( RIMS_PHC phc, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement ) throws SQLException
    {
        String disease_code = mappingDataElement.getDisease_code();

        String query = "";
        query = "SELECT * FROM " + getTableName()
            + " WHERE phc_code LIKE '" + phc.getCode() + "' AND mnth = " + month
            + " AND yr = " + year + " AND disease_code LIKE '" + disease_code
            + "'";

        return existingData( query, null );
    }

    public int insertData( RIMS_PHC rimsOrgUnit,
        int month, int year, RIMS_Mapping_DataElement mappingDataElement,
        String value ) throws SQLException
    {
        String query = "INSERT INTO " + getTableName()
            + " (disease_code," + "phc_code,district_code,mnth,yr,state_code,"
            + mappingDataElement.getRimsColumn()
            + ",created_by,created_date) VALUES (?,?,?,?,?,?,?,?,?)";
        Date curDate = new Date( new java.util.Date().getTime() );
        int numberResult = 0;
        numberResult = Integer.parseInt( value );
        if ( numberResult != -1 || numberResult != 0 )
        {
            Object[] params =
            {
                mappingDataElement.getDisease_code(),
                rimsOrgUnit.getPhc_code(),
                rimsOrgUnit.getDistrict_code(),
                month,
                year,
                rimsOrgUnit.getState_code(),
                numberResult,
                "DHIS2",
                curDate
            };

            return executeUpdate( query, params );
        }
        return 0;
    }

    public int updateData( RIMS_PHC rimsOrgUnit,
        int month, int year, RIMS_Mapping_DataElement mappingDataElement,
        String value ) throws SQLException
    {
        String phc_code = rimsOrgUnit.getPhc_code();
        String query = "UPDATE "
            + getTableName()
            + " SET "
            + mappingDataElement.getRimsColumn()
            + " = ?, "
            + "updated_by = ?, updated_date = ?"
            + " WHERE phc_code = ? AND mnth = ? AND yr = ? AND disease_code = ?";
        
        Date curDate = new Date( new java.util.Date().getTime() );
        int numberResult = Integer.parseInt( value );
        if ( numberResult != -1 || numberResult != 0 )
        {
            Object[] params =
            {
                numberResult,
                "DHIS2",
                curDate,
                phc_code,
                month,
                year,
                mappingDataElement.getDisease_code()
            };

            return executeUpdate( query, params );
        }
        return 0;
    }

    public void fillInTotals()
    {
        // No totals to fill in
    }

    public String getTableName()
    {
        return "surveillance_dtl";
    }

    public void markIfComplete() throws SQLException
    {
        String query = "UPDATE "+ getTableName() +
                      " SET complete = 1" +
                      " WHERE cases IS NOT NULL" +
                      "   AND deaths IS NOT NULL";
        executeUpdate( query );
    }
    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMS_PHC orgUnit, int month, int year ) throws SQLException
    {
        String getColumn = mappingDataElement.getRimsColumn();
        String query = "SELECT "+ getColumn +
                      " FROM "+ mappingDataElement.getTableName() +
                      " WHERE disease_code = ?" +
                      "   AND phc_code = ?" +
                      "   AND mnth = ?" +
                      "   AND yr = ?";
        Object[] params = { mappingDataElement.getDisease_code(),
                            orgUnit.getPhc_code(),
                            month,
                            year
        };
        return executeQuery( query, getColumn, params );
    }
}
