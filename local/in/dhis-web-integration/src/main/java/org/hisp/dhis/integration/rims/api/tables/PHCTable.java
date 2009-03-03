package org.hisp.dhis.integration.rims.api.tables;

import java.sql.SQLException;

import org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement;
import org.hisp.dhis.integration.rims.util.RIMSTableAdapter;

public abstract class PHCTable
    extends RIMSTableAdapter
{

    public PHCTable()
    {
        super();
    }

    @Override
    public int insertData( RIMSDistrict district, int month, int year, RIMS_Mapping_DataElement mappingDataElement, String value )
        throws SQLException
    {
        // No data for districts
        return 0;
    }

    @Override
    public int updateData( RIMSDistrict district, int month, int year, RIMS_Mapping_DataElement mappingDataElement, String value )
        throws SQLException
    {
        // No data for districts
        return 0;
    }

    @Override
    public boolean isData( RIMSDistrict district, int month, int year, RIMS_Mapping_DataElement mappingDataElement )
    {
        // No data for districts
        return false;
    }
    
    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMSDistrict orgUnit, int month, int year )
    {
        return null;
    }
    
    public int deleteDistrictData()
    {
        return 0;
    }
}