package org.hisp.dhis.integration.rims.api.tables;

import java.sql.SQLException;
import org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement;
import org.hisp.dhis.integration.rims.util.RIMSTableAdapter;

public class RIMS_Vaccine_Supply_Dtl
    extends RIMSTableAdapter
{
    public void fillInTotals() throws SQLException
    {
        String query = "UPDATE "
            + getTableName()
            + " SET "
            + "vaccine_bal = opn_balance + vaccine_rcvd - vaccine_consd - vaccine_unused";
        executeUpdate( query );
    }


    public boolean isData( RIMS_PHC phc, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement ) throws SQLException
    {
        String vac_code = mappingDataElement.getVaccine_code();
        String query = "SELECT * FROM " + getTableName() + " WHERE phc_code LIKE '" + phc.getCode()
                + "' AND mnth = " + month + " AND yr = " + year + " AND vaccine_code LIKE '" + vac_code + "' ";
        return existingData( query );
    }

    @Override
    public boolean isData( RIMSDistrict district, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement )
        throws SQLException
    {
        String vac_code = mappingDataElement.getVaccine_code();
        String query = "SELECT * FROM " + getTableName()
            + " WHERE district_code LIKE '" + district.getCode() + "' AND mnth = "
            + month + " AND yr = " + year + " AND vaccine_code LIKE '"
            + vac_code + "' ";
        return existingData( query );
    }


    public int insertData( RIMS_PHC rimsOrgUnit, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String resValue ) throws SQLException
    {
        String vac_code = mappingDataElement.getVaccine_code();

        String query = "INSERT INTO " + getTableName()
            + " (vaccine_code, phc_code, district_code, mnth, yr, state_code, " + mappingDataElement.getRimsColumn()
            + ", created_by, complete )" + " VALUES (?,?,?,?,?,?,?,?,1)";

        int numberResult = Integer.parseInt( resValue );

        if ( numberResult != -1 || numberResult != 0 )
        {
            Object[] params = { vac_code, rimsOrgUnit.getPhc_code(),
                rimsOrgUnit.getDistrict_code(), month, year,
                rimsOrgUnit.getState_code(), numberResult, "DHIS2" };

            return executeUpdate( query, params );
        }
        return 0;
    }
    
    @Override
    public int insertData( RIMSDistrict district, int month, int year, RIMS_Mapping_DataElement mappingDataElement, String value )
        throws SQLException
    {
        String vac_code = mappingDataElement.getVaccine_code();

        String query = "INSERT INTO " + getTableName()
            + " (vaccine_code, district_code, mnth, yr, state_code, " + mappingDataElement.getRimsColumn()
            + ", created_by)" + " VALUES (?,?,?,?,?,?,?)";

        int numberResult = Integer.parseInt( value );

        if ( numberResult != -1 || numberResult != 0 )
        {
            Object[] params = { vac_code,
                district.getDistrict_code(), month, year,
                district.getState_code(), numberResult, "DHIS2" };

            return executeUpdate( query, params );
        }
        return 0;
    }


    public int updateData( RIMS_PHC rimsOrgUnit, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String resValue ) throws SQLException
    {
        String vac_code = mappingDataElement.getVaccine_code();
        String phc_code = rimsOrgUnit.getPhc_code();
    
        String query = "UPDATE " + getTableName() + " SET " + mappingDataElement.getRimsColumn() + " = ? "
            + ", updated_by = ?" + " WHERE phc_code LIKE '" + phc_code + "' AND mnth = " + month + " AND yr = " + year
            + " AND vaccine_code LIKE '" + vac_code + "' ";
    
        int numberResult = Integer.parseInt( resValue );
        
        if ( numberResult != -1 || numberResult != 0 )
        {
            Object[] params = new Object[] { numberResult, "DHIS2" };
            return executeUpdate( query, params );
        }
        return 0;
    }


    @Override
    public int updateData( RIMSDistrict district, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String value )
        throws SQLException
    {
        String vac_code = mappingDataElement.getVaccine_code();
        String district_code = district.getCode();

        String query = "UPDATE " + getTableName() + 
                      " SET " + mappingDataElement.getRimsColumn() + " = ? " + 
                      ", updated_by = ?" + 
                      " WHERE district_code LIKE '" + district_code + "' " +
                      "   AND phc_code IS NULL"+
                      "   AND mnth = "+ month + 
                      "   AND yr = " + year + 
                      "   AND vaccine_code LIKE '"+ vac_code + "' ";

        int numberResult = Integer.parseInt( value );

        if ( numberResult != -1 || numberResult != 0 )
        {
            Object[] params = new Object[] { numberResult, "DHIS2" };
            return executeUpdate( query, params );
        }
        return 0;
    }


    public String getTableName()
    {
        return "vaccine_supply_dtl";
    }

    public void markIfComplete() throws SQLException
    {
        // Exception for vaccine 8, 9, and 14 since they are syringes and it
        // is not recorded whether they are unusable.
        // TODO Move these exceptions into a configuration file.
        String query = "UPDATE " + getTableName() + " SET" + " complete = 1"
            + " WHERE opn_balance IS NOT NULL"
            + " AND vaccine_rcvd IS NOT NULL"
            + " AND vaccine_consd IS NOT NULL"
            + " AND ( vaccine_unused IS NOT NULL OR vaccine_code IN ( 'vac8', 'vac9', 'vac14' ) )"
            + " AND vaccine_bal IS NOT NULL" + " AND vaccine_dispd IS NOT NULL";
        executeUpdate( query );
        
        String query2 = "UPDATE "+ getTableName()
            + " SET vaccine_unused = 0"
            + " WHERE vaccine_unused IS NULL" +
                " AND vaccine_code IN ( 'vac8', 'vac9', 'vac14' )";
        executeUpdate( query2 );
    }


    public String getDescription()
    {
        return "Vaccine stocks";
    }


    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMS_PHC orgUnit, int month, int year ) throws SQLException
    {
        String getColumn = mappingDataElement.getRimsColumn();
        String query = "SELECT "+ getColumn +
                      " FROM "+ mappingDataElement.getTableName() +
                      " WHERE phc_code = ?" +
                      "   AND vaccine_code = ?" +
                      "   AND mnth = ?" +
                      "   AND yr = ?";
        Object[] params = { orgUnit.getPhc_code(),
                            mappingDataElement.getVaccine_code(),
                            month,
                            year
        };
        return executeQuery( query, getColumn, params );
    }


    @Override
    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMSDistrict orgUnit, int month, int year )
        throws SQLException
    {
        String getColumn = mappingDataElement.getRimsColumn();
        String query = "SELECT "+ getColumn +
                      " FROM "+ mappingDataElement.getTableName() +
                      " WHERE district_code = ?" +
                      "   AND phc_code IS NULL" +
                      "   AND vaccine_code = ?" +
                      "   AND mnth = ?" +
                      "   AND yr = ?";
        Object[] params = { orgUnit.getCode(),
                            mappingDataElement.getVaccine_code(),
                            month,
                            year
        };
        return executeQuery( query, getColumn, params );
    }

    public int deleteDistrictData()
    throws SQLException
    {
        String query = "DELETE FROM "+ getTableName() +
                      " WHERE phc_code IS NULL";
        return executeUpdate( query );
    }
}