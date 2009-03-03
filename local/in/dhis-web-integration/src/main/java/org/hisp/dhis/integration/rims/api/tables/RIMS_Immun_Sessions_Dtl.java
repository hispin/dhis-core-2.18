package org.hisp.dhis.integration.rims.api.tables;

import java.sql.SQLException;
import org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement;

public class RIMS_Immun_Sessions_Dtl
    extends PHCTable
{
    public String getDescription() 
    {
        return "Immunization sessions";
    }
    public void fillInTotals()
    {
        // No totals to fill in.
    }

    public String getTableName()
    {
        return "immun_sessions_dtl";
    }

    public void markIfComplete()
    {
        // Table has no "completed" column.
    }
    
    public boolean isData( RIMS_PHC phc, int month, int year, RIMS_Mapping_DataElement mappingDataelement ) throws SQLException
    {
        String query = "SELECT * FROM " + getTableName() + " WHERE phc_code like '" + phc.getCode()
            + "' AND mnth = " + month + " AND yr = " + year;

        return existingData( query, null );
    }
    
    public int updateData( RIMS_PHC rimsOrgUnit, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String resValue ) throws SQLException
    {
        String query = "UPDATE " + getTableName() + " SET " + mappingDataElement.getRimsColumn() + " = ? "
            + ", updated_by = ?" + " WHERE phc_code LIKE '" + rimsOrgUnit.getPhc_code() + "' AND " + " mnth = " + month
            + " AND yr = " + year;
        
        int numberResult = Integer.parseInt( resValue );

        if ( numberResult != -1 || numberResult != 0 )
        {
            Object [] params =
            {
                numberResult,
                "DHIS2"
            };

            return executeUpdate( query, params );
        }
        return 0;
    }
    


    public int insertData( RIMS_PHC rimsOrgUnit, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String resValue )
        throws SQLException
    {
        String query = "INSERT INTO " + getTableName()
            + " (phc_code,district_code,mnth,yr,state_code,"
            + mappingDataElement.getRimsColumn() + ",created_by, authorized)"
            + " VALUES (?,?,?,?,?,?,?,?)";
        int numberResult = Integer.parseInt( resValue );
        if ( numberResult != -1 || numberResult != 0 )
        {
            Object[] params = 
            { 
                rimsOrgUnit.getPhc_code(),
                rimsOrgUnit.getDistrict_code(), 
                month, 
                year,
                rimsOrgUnit.getState_code(), 
                numberResult, 
                "DHIS2",
                0
            };

            return executeUpdate( query, params );
        }
        return 0;
    }

    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMS_PHC orgUnit, int month, int year ) throws SQLException
    {
        String getColumn = mappingDataElement.getRimsColumn();
        String query = "SELECT "+ getColumn +
                       " FROM "+ mappingDataElement.getTableName() +
                       " WHERE phc_code = ?" +
                       "  AND mnth = ?" +
                       "  AND yr = ?";
        
        Object[] params = { orgUnit.getPhc_code(), month, year };
        return executeQuery( query, getColumn, params );
    }
}