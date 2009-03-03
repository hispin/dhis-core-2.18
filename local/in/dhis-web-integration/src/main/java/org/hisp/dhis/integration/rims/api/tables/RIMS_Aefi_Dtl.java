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
public class RIMS_Aefi_Dtl
    extends PHCTable
{
    
    public int insertData( RIMS_PHC rimsOrgUnit, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String dhisValue ) throws SQLException
    {
        String query = "INSERT INTO " + getTableName() + " ("
            + "phc_code, district_code, mnth, yr, state_code, aefi_code, during_the_mnth"
            + ",created_by, created_date) VALUES (?,?,?,?,?,?,?,?,?)";
    
        Object[] params =
        {
            rimsOrgUnit.getPhc_code(),
            rimsOrgUnit.getDistrict_code(),
            month, 
            year,
            rimsOrgUnit.getState_code(),
            mappingDataElement.getAefiCode(),
            Integer.parseInt( dhisValue ),
            "DHIS2",
            new Date( new java.util.Date().getTime() )
        };
        
        return executeUpdate( query, params );
    }

    public boolean isData( RIMS_PHC phc, int month, int year, RIMS_Mapping_DataElement mappingDataElement ) throws SQLException
    {
        String aefi_code = mappingDataElement.getAefiCode();
    
        String query = "SELECT * FROM " + getTableName() + " WHERE phc_code LIKE '" + phc.getCode() + "' AND mnth = "
            + month + " AND yr = " + year + " AND aefi_code LIKE '" + aefi_code + "'";
    
        return existingData( query );
    }

    public int updateData( RIMS_PHC rimsOrgUnit, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String dhisValue ) throws SQLException
    {
        String query = "UPDATE " + getTableName() + " SET " + 
              "during_the_mnth = ?, updated_by = ?, updated_date = ? " +
              "WHERE phc_code = ? AND mnth = ? AND yr = ? "
            + "AND aefi_code = ?";
    
        Object[] params =
        {
            Integer.parseInt( dhisValue ),
            "DHIS2",
            new Date( new java.util.Date().getTime() ),
            rimsOrgUnit.getPhc_code(),
            month,
            year,
            mappingDataElement.getAefiCode()
        };
        return executeUpdate( query, params );
    }

    public void fillInTotals()
    {
        // No totals to fill in
    }

    public String getTableName()
    {
        return "aefi_dtl";
    }


    public void markIfComplete() throws SQLException
    {
        String query = "UPDATE "+ getTableName() +
                      " SET complete = 1 " +
                      " WHERE during_the_mnth IS NOT NULL";
        executeUpdate( query );
    }

    public String getDescription()
    {
        return "Adverse Events Following Immunization";
    }

    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement,
        RIMS_PHC orgUnit, int month, int year ) throws SQLException
    {
        String query = "  SELECT during_the_mnth" +
                        " FROM "+ getTableName() +
                        " WHERE phc_code = ?"+  
                        "   AND aefi_code = ?"+
                        "   AND mnth = ?" +
                        "   AND yr = ?";
        
        String getColumn = "during_the_mnth";

        Object[] params = new Object[] { 
            orgUnit.getPhc_code(),
            mappingDataElement.getAefiCode(), 
            month, 
            year };
        
        return executeQuery( query, getColumn, params );
    }
}
