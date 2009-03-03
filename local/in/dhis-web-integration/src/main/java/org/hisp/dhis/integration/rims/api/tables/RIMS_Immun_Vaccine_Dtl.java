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
public class RIMS_Immun_Vaccine_Dtl
    extends PHCTable
{
    public String getDescription()
    {
        return "Immunization statistics";
    }
    
    public String getTableName()
    {
        return "immun_vaccine_dtl";
    }

    public void fillInTotals()
        throws SQLException
    {
        String query = "UPDATE " + getTableName()
            + " SET under1year_total = under1year_male + under1year_female"
            + " WHERE under1year_male IS NOT NULL AND under1year_female IS NOT NULL";
        super.executeUpdate( query );
        
        query = "UPDATE " + getTableName()
             + " SET over1year_total = over1year_male + over1year_female" +
               " WHERE over1year_male IS NOT NULL AND over1year_female IS NOT NULL";
        super.executeUpdate( query );
    }

    public boolean isData( RIMS_PHC phc, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement ) throws SQLException
    {
        String vac_code = mappingDataElement.getVaccine_code();
        String antigen = mappingDataElement.getAntigen();

        String query = "SELECT * FROM " + getTableName() + " WHERE phc_code LIKE '" + phc.getCode()
            + "' AND mnth = " + month + " AND yr = " + year + " AND vaccine_code LIKE '" + vac_code + "' "
            + " AND antigen LIKE '" + antigen + "'";

        return existingData( query, null );
    }

    public int updateData( RIMS_PHC rimsOrgUnit, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String resValue ) throws SQLException
    {
        String vac_code = mappingDataElement.getVaccine_code();
        String antigen = mappingDataElement.getAntigen();
        String phc_code = rimsOrgUnit.getPhc_code();

        String update = "UPDATE " + getTableName() + " SET " + mappingDataElement.getRimsColumn() + " = ? "
            + ", updated_by = ?" + " WHERE phc_code LIKE '" + phc_code + "' AND mnth = " + month + " AND yr = " + year
            + " AND vaccine_code LIKE '" + vac_code + "' " + " AND antigen LIKE '" + antigen + "'";

        int numberResult = Integer.parseInt( resValue );

        if ( numberResult != -1 || numberResult != 0 )
        {
            Object[] params = { numberResult, "DHIS2" };
            return executeUpdate( update, params );
        }
        else 
        {
            throw new RuntimeException( "No data available for data element "+ 
                mappingDataElement.getDhisExpression() );
        }
    }

    public int insertData( RIMS_PHC rimsOrgUnit, int month,
        int year, RIMS_Mapping_DataElement mappingDataElement, String resValue ) throws SQLException
    {
        String vac_code = mappingDataElement.getVaccine_code();
        String antigen = mappingDataElement.getAntigen();

        String insert = "INSERT INTO "
            + getTableName()
            + " (vaccine_code, antigen, phc_code, district_code, mnth, yr, state_code, "
            + mappingDataElement.getRimsColumn() + ", created_by)"
            + " VALUES (?,?,?,?,?,?,?,?,?)";

        int numberResult = Integer.parseInt( resValue );

        if ( numberResult != -1 || numberResult != 0 )
        {
            Object[] params = { vac_code, antigen, rimsOrgUnit.getPhc_code(),
                rimsOrgUnit.getDistrict_code(), month, year,
                rimsOrgUnit.getState_code(), numberResult, "DHIS2" };

            return executeUpdate( insert, params );
        }
        else

        {
            throw new RuntimeException( "No data available for data element "
                + mappingDataElement.getDhisExpression() );
        }
    }
    
    public void markIfComplete() throws SQLException
    {
        // TODO Make hard-coded vaccines configurable
        // Mark pregnant women
        String updateWomen = "UPDATE " + getTableName() + " SET" + " complete = 1"
            + " WHERE vaccine_code LIKE 'vac4'"
            + " AND preg_women IS NOT NULL";
        executeUpdate( updateWomen );

        // Mark under 1 year
        String updateUnder1 = "UPDATE " + getTableName() + " SET" + " complete = 1"
        + " WHERE vaccine_code LIKE 'vac0'"
        + " AND antigen = '0'"
        + " AND under1year_male IS NOT NULL"
        + " AND under1year_female IS NOT NULL";
        executeUpdate( updateUnder1 );
        
        // Mark over 1 year
        String updateOver1 = "UPDATE " + getTableName() + " SET" + " complete = 1"
        + " WHERE vaccine_code IN ( 'vac12', 'vac13', 'vac6', 'vac10', 'vac11' )"
        + " OR ( vaccine_code LIKE 'vac5' AND antigen IN ( '2', '3', '4', '5' ) ) "
        + " AND over1year_male IS NOT NULL"
        + " AND over1year_female IS NOT NULL";
        executeUpdate( updateOver1 );
        
        // Mark all children
        String updateChildren = "UPDATE " + getTableName() + " SET" + " complete = 1"
        + " WHERE vaccine_code IN ( 'vac1', 'vac3', 'vac7', 'vac2' ) "
        + " OR ( vaccine_code LIKE 'vac0' AND antigen IN ( '1', '2', '3' ) )"
        + " OR ( vaccine_code LIKE 'vac5' AND antigen LIKE '1' )"
        + " AND over1year_male IS NOT NULL"
        + " AND over1year_female IS NOT NULL";
        executeUpdate( updateChildren );
    }
    
    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMS_PHC orgUnit, int month, int year ) throws SQLException
    {
        String getColumn = mappingDataElement.getRimsColumn();
        String query = "SELECT "+ getColumn +
                      " FROM "+ mappingDataElement.getTableName() +
                      " WHERE vaccine_code = ?"+
                      " AND antigen = ? "+
                      " AND phc_code = ?"+
                      " AND mnth = ?"+
                      " AND yr = ?";
        Object[] params = { mappingDataElement.getVaccine_code(),
                            mappingDataElement.getAntigen(),
                            orgUnit.getPhc_code(),
                            month,
                            year
        };
        
        return executeQuery( query, getColumn, params );
    }


}
