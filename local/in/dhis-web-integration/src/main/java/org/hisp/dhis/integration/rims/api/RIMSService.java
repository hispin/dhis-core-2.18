package org.hisp.dhis.integration.rims.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.integration.rims.api.tables.RIMSDistrict;
import org.hisp.dhis.integration.rims.api.tables.RIMS_PHC;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public interface RIMSService
{
    /**
     * returns all Dataelment related tables as RIMS Degroups
     * 
     * @return RIMS DataElement Table Names
     */
    Map<String, String> getAllRIMSDEGroups();
    
    public DataSetService getDataSetService();

    public void setDataSetService( DataSetService datasetService );
    
    public DataElementService getDataElementService();

    public void setDataElementService( DataElementService deService );
    
    public List<RIMS_Mapping_DataElement> createDataset( String name ) throws Exception;
    
    List<RIMS_Mapping_DataElement> getRIMSDataElementsByDEGroup( String deGroupName ) throws SAXParseException, SAXException, IOException, ParserConfigurationException;

    List<RIMS_Mapping_DataElement> getAllMappingDataElements() throws Exception;

    List<RIMS_Mapping_Orgunit> getAllMappingOrgunits();

    List<RIMS_PHC> getAllDistrictsofAllPHCs( String connection ) throws IOException, SAXException, SQLException;

    List<RIMS_PHC> getPHCsByDistrict( String district_code, String connection ) throws SQLException;

    RIMS_PHC getPHC( String rimsOU, String connection ) throws SQLException;
    
    RIMSDistrict getDistrict( String rimsOU, String connection ) throws SQLException;

    RIMS_Mapping_Orgunit getMappingOrgUnit( String orgUnitCode );

}
