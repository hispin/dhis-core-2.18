package org.hisp.dhis.integration.rims.api.tables;

import java.sql.SQLException;

import org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement;
import org.hisp.dhis.integration.rims.util.RIMSTableAdapter;

public abstract class DistrictTable
    extends RIMSTableAdapter
{

    public DistrictTable()
    {
        super();
    }

    @Override
    public int insertData( RIMS_PHC phc, int month, int year, RIMS_Mapping_DataElement mappingDataElement, String value )
        throws SQLException
    {
        // No data for PHCs
        return 0;
    }

    @Override
    public int updateData( RIMS_PHC phc, int month, int year, RIMS_Mapping_DataElement mappingDataElement, String value )
        throws SQLException
    {
        // No data for PHCs
        return 0;
    }

    @Override
    public boolean isData( RIMS_PHC phc, int month, int year, RIMS_Mapping_DataElement mappingDataElement )
    {
        // No data for PHCs
        return false;
    }

    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMS_PHC orgUnit, int month, int year )
    {
        return null;
    }

    public int deleteDistrictData()
        throws SQLException
    {
        String query = "DELETE FROM " + getTableName();
        return executeUpdate( query );
    }
}