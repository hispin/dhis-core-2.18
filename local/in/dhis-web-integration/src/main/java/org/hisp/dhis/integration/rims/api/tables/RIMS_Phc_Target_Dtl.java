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
public class RIMS_Phc_Target_Dtl
    extends PHCTable
{
    public void fillInTotals()
        throws SQLException
    {
        // No totals to fill in.
    }

    /**
     * Month is ignored by this table. The year indicates in which calendar
     * year the financial year starts. For instance, year == 2007 will give
     * financial_year = "2007-2008"
     */
    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement,
        RIMS_PHC orgUnit, int month, int year )
        throws SQLException
    {
        String getColumn = mappingDataElement.getRimsColumn();
        String financial_year = financialYear( year );
        String query = "SELECT "+ getColumn +
                      " FROM "+ getTableName() +
                      " WHERE phc_code = ?" +
                      "   AND age_code = ?" +
                      "   AND financial_year = ?";
        Object[] params = { orgUnit.getPhc_code(),
                            mappingDataElement.getAgeCode(),
                            financial_year
        };
        return executeQuery( query, getColumn, params );
    }

    /**
     * @param year
     * @return
     */
    private String financialYear( int year )
    {
        return year +"-"+ (year + 1);
    }

    public String getDescription()
    {
        return "Yearly immunization targets";
    }

    public String getTableName()
    {
        return "phc_target_dtl";
    }

    /* (non-Javadoc)
     * @see org.hisp.dhis.integration.rims.api.RIMSTable#insertData(org.hisp.dhis.integration.rims.api.tables.RIMS_PHC, int, int, org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement, java.lang.String)
     */
    public int insertData( RIMS_PHC rimsOrgUnit, int month,
        int year, RIMS_Mapping_DataElement mappingDataElement, String resValue ) throws SQLException
    {
        String update = "INSERT INTO "+ getTableName() +
                       " ( state_code, district_code, phc_code, age_code, " +
                       "   financial_year, cur_target, lst_target," +
                       "   snd_target, created_by, created_date )" + 
                       " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Object[] params = {
            rimsOrgUnit.getState_code(),
            rimsOrgUnit.getDistrict_code(),
            rimsOrgUnit.getPhc_code(),
            mappingDataElement.getAgeCode(),
            financialYear( year ),
            Integer.parseInt( resValue ),
            0,
            0,
            "DHIS2",
            new java.sql.Date( new java.util.Date().getTime() )
        };
        return executeUpdate( update, params );
    }

    /* (non-Javadoc)
     * @see org.hisp.dhis.integration.rims.api.RIMSTable#isData(java.lang.String, int, int, org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement)
     */
    public boolean isData( RIMS_PHC phc, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement )
        throws SQLException
    {
        String query = "SELECT *" +
                      " FROM "+ getTableName() +
                      " WHERE phc_code = ?" +
                      "   AND age_code = ?" +
                      "   AND financial_year = ?";
        Object[] params = { phc.getCode(), 
                            mappingDataElement.getAgeCode(),
                            financialYear( year ) };

        return existingData( query, params );
    }

    public void markIfComplete()
    {
        // No field "complete"
    }

    /* (non-Javadoc)
     * @see org.hisp.dhis.integration.rims.api.RIMSTable#updateData(org.hisp.dhis.integration.rims.api.tables.RIMS_PHC, int, int, org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement, java.lang.String)
     */
    public int updateData( RIMS_PHC orgUnit, int month,
        int year, RIMS_Mapping_DataElement mappingDataElement, String resValue ) throws SQLException
    {
        String update = "UPDATE "+ getTableName() +
                       " SET cur_target = ?" +
                       " WHERE phc_code = ?" +
                       "   AND age_code = ?" +
                       "   AND financial_year = ?";
        
        Object[] params = {
            Integer.parseInt( resValue ),
            orgUnit.getPhc_code(),
            mappingDataElement.getAgeCode(),
            financialYear( year )
        };
        return executeUpdate( update, params );
    }

}
