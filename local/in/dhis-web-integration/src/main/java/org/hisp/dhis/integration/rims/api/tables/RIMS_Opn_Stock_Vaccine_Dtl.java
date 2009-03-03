package org.hisp.dhis.integration.rims.api.tables;

import java.sql.SQLException;
import org.hisp.dhis.integration.rims.api.FinancialYearly;
import org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement;
import org.hisp.dhis.integration.rims.util.RIMSTableAdapter;

// TODO Should the class also support MSDs? (There seems to be none in Anand, Mehsana and Valsad.)
public class RIMS_Opn_Stock_Vaccine_Dtl 
    extends RIMSTableAdapter
    implements FinancialYearly
{
    public String getDescription()
    {
        return "Yearly opening stocks for vaccines"; 
    }

    public String getTableName()
    {
        return "opn_stock_vaccine_dtl";
    }
    
    // TODO Move to configuration file.
    /**
     * The start of the financial year, counting from 0 (i. e. 0 is January).
     */
    private int financialYearStart = java.util.Calendar.APRIL;

    public boolean isData( RIMS_PHC phc, int month, int year, RIMS_Mapping_DataElement mappingDataElement )
        throws SQLException
    {
        String query = "SELECT *" +
                      " FROM "+ getTableName() +
                      " WHERE phc_code = ?" +
                      "   AND vaccine_code = ?" +
                      "   AND yr = ?";
        Object[] params = 
        { 
            phc.getCode(), 
            mappingDataElement.getVaccine_code(),
            year
        };
        return existingData( query, params );
    }

    @Override
    public boolean isData( RIMSDistrict district, int month, int year, RIMS_Mapping_DataElement mappingDataElement )
        throws SQLException
    {
        String query = "SELECT *" +
        " FROM "+ getTableName() +
        " WHERE district_code = ?" +
        "   AND vaccine_code = ?" +
        "   AND yr = ?";
        Object[] params = 
        { 
            district.getCode(), 
            mappingDataElement.getVaccine_code(),
            year
        };
        return existingData( query, params );
    }

    public int insertData( RIMS_PHC rimsOrgUnit, int month, int year, RIMS_Mapping_DataElement mappingDataElement, String resValue )
        throws SQLException
    {
        String update = "INSERT INTO "+ getTableName() +
                      " (state_code, district_code, phc_code, vaccine_code," +
                      "  yr, opening_bal, created_by, created_date)" +
                      " VALUES (?,?,?,?,?,?,?,?)";
        Object[] params = 
        {
            rimsOrgUnit.getState_code(),
            rimsOrgUnit.getDistrict_code(),
            rimsOrgUnit.getPhc_code(),
            mappingDataElement.getVaccine_code(),
            year,
            Integer.parseInt( resValue ),
            "DHIS2",
            new java.sql.Date( new java.util.Date().getTime() )
        };
        return executeUpdate( update, params );
    }

    @Override
    public int insertData( RIMSDistrict district, int month, int year, RIMS_Mapping_DataElement mappingDataElement, String value )
        throws SQLException
    {
        String update = "INSERT INTO " + getTableName()
            + " (state_code, district_code, vaccine_code,"
            + "  yr, opening_bal, created_by, created_date)"
            + " VALUES (?,?,?,?,?,?,?)";
        Object[] params = { 
            district.getState_code(),
            district.getDistrict_code(), 
            mappingDataElement.getVaccine_code(), year,
            Integer.parseInt( value ), "DHIS2",
            new java.sql.Date( new java.util.Date().getTime() ) };
        return executeUpdate( update, params );
    }

    public int updateData( RIMS_PHC rimsOrgUnit, int month, int year, RIMS_Mapping_DataElement mappingDataElement, String resValue )
        throws SQLException
    {
        String update = "UPDATE "+ getTableName() +
                       " SET opening_bal = ?" +
                       " WHERE phc_code = ?" +
                       "   AND vaccine_code = ?" +
                       "   AND yr = ?";
        Object[] params = 
        {
          Integer.parseInt( resValue ),
          rimsOrgUnit.getPhc_code(),
          mappingDataElement.getVaccine_code(),
          year
        };
        
        return executeUpdate( update, params );
    }

    @Override
    public int updateData( RIMSDistrict district, int month, int year, RIMS_Mapping_DataElement mappingDataElement, String value )
        throws SQLException
    {
        String update = "UPDATE "+ getTableName() +
        " SET opening_bal = ?" +
        " WHERE district_code = ?" +
        "   AND phc_code IS NULL" +
        "   AND vaccine_code = ?" +
        "   AND yr = ?";
        Object[] params = 
        {
            Integer.parseInt( value ),
            district.getDistrict_code(),
            mappingDataElement.getVaccine_code(),
            year
        };
        
        return executeUpdate( update, params );
    }

    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMS_PHC orgUnit, int month, int year )
        throws SQLException
    {
        String getColumn = mappingDataElement.getRimsColumn();
        String query = "SELECT "+ getColumn +
                      " FROM "+ getTableName() +
                      " WHERE phc_code = ?" +
                      "   AND vaccine_code = ?" +
                      "   AND yr = ?";
        Object[] params =
        {
            orgUnit.getPhc_code(),
            mappingDataElement.getVaccine_code(),
            year            
        };
        return executeQuery( query, getColumn, params );
    }

    public void fillInTotals()
        throws SQLException
    {
        // No totals to fill in.
    }

    public void markIfComplete()
        throws SQLException
    {
        // No column named "complete"
    }

    public int getFinancialYearStart()
    {
        return financialYearStart;
    }

    @Override
    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMSDistrict orgUnit, int month, int year )
        throws SQLException
    {
        String getColumn = mappingDataElement.getRimsColumn();
        String query = "SELECT "+ getColumn +
                      " FROM "+ getTableName() +
                      " WHERE district_code = ?" +
                      "   AND phc_code IS NULL" +
                      "   AND vaccine_code = ?" +
                      "   AND yr = ?";
        Object[] params =
        {
            orgUnit.getCode(),
            mappingDataElement.getVaccine_code(),
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