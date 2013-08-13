package org.hisp.dhis.coldchain.reports;

import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.database.DatabaseInfo;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultCCEMReportManager
    implements CCEMReportManager
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private DatabaseInfoProvider databaseInfoProvider;

    public void setDatabaseInfoProvider( DatabaseInfoProvider databaseInfoProvider )
    {
        this.databaseInfoProvider = databaseInfoProvider;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Implementation Methods
    // -------------------------------------------------------------------------

    public Map<Integer, String> getOrgunitAndOrgUnitGroupMap( String orgUnitGroupIdsByComma, String orgUnitIdsByComma )
    {
        Map<Integer, String> orgUnitGroupMap = new HashMap<Integer, String>();
        int prevOrgUnitId = 0;
        try
        {
            String query = "SELECT organisationunitid, orgunitgroup.name FROM orgunitgroupmembers "
                + " INNER JOIN orgunitgroup ON orgunitgroupmembers.orgunitgroupid = orgunitgroup.orgunitgroupid "
                + " WHERE " + " orgunitgroup.orgunitgroupid IN (" + orgUnitGroupIdsByComma + ") AND "
                + " organisationunitid IN (" + orgUnitIdsByComma + ") ORDER BY organisationunitid";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitID = rs.getInt( 1 );
                String ouGroupName = rs.getString( 2 );

                if ( prevOrgUnitId == orgUnitID )
                {
                    String temp = orgUnitGroupMap.get( orgUnitID );
                    if ( temp == null )
                        temp = "";
                    temp += "," + ouGroupName;
                    orgUnitGroupMap.put( orgUnitID, temp );
                }
                else
                {
                    orgUnitGroupMap.put( orgUnitID, ouGroupName );
                }

                prevOrgUnitId = orgUnitID;
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return orgUnitGroupMap;
    }

    public Map<String, String> getOrgUnitGroupAttribDataForRequirement( String orgUnitGroupIdsByComma,
        String orgUnitGroupAttribIds )
    {
        Map<String, String> orgUnitGroupAttribDataForRequirement = new HashMap<String, String>();
        try
        {
            String query = "SELECT orgunitgroupmembers.organisationunitid, attributevalue.attributeid, value FROM attributevalue "
                + " INNER JOIN orgunitgroupattributevalues ON attributevalue.attributevalueid = orgunitgroupattributevalues.attributevalueid "
                + " INNER JOIN orgunitgroupmembers ON orgunitgroupmembers.orgunitgroupid = orgunitgroupattributevalues.orgunitgroupid "
                + " WHERE "
                + " attributeid IN ("
                + orgUnitGroupAttribIds
                + ") AND "
                + " orgunitgroupattributevalues.orgunitgroupid IN (" + orgUnitGroupIdsByComma + ")";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitID = rs.getInt( 1 );
                Integer attribId = rs.getInt( 2 );
                String value = rs.getString( 3 );

                orgUnitGroupAttribDataForRequirement.put( orgUnitID + ":" + attribId, value );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return orgUnitGroupAttribDataForRequirement;
    }

    public Map<String, String> getDataElementDataForCatalogOptionsForRequirement( String orgUnitIdsByComma,
        String catalogOption_DataelementIds, Integer periodId )
    {
        Map<String, String> dataElementDataForRequirement = new HashMap<String, String>();
        try
        {
            String query = "SELECT dataelementid, periodid, sourceid, value FROM datavalue " + " WHERE "
                + " dataelementid IN ( " + catalogOption_DataelementIds + ") AND " + " sourceid IN ( "
                + orgUnitIdsByComma + " ) AND " + " periodid = " + periodId;

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer deId = rs.getInt( 1 );
                Integer pId = rs.getInt( 2 );
                Integer sourceId = rs.getInt( 3 );
                String value = rs.getString( 4 );

                dataElementDataForRequirement.put( deId + ":" + pId + ":" + sourceId, value );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return dataElementDataForRequirement;
    }

    public Map<String, String> getCatalogDataForRequirement( Integer vsReqCatalogTypeId, Integer vsReqStorageTempId,
        String vsReqStorageTemp, Integer vsReqNationalSupplyId, String vsReqNationalSupply, String vsReqCatalogAttribIds )
    {
        Map<String, String> catalogDataForRequirement = new HashMap<String, String>();
        try
        {
            String query = "SELECT catalog.catalogid, catalogtypeattributeid, value FROM catalogdatavalue "
                + " INNER JOIN catalog ON catalog.catalogid = catalogdatavalue.catalogid "
                + " WHERE "
                + " catalog.catalogid IN "
                + "( SELECT cd1.catalogid FROM catalogdatavalue AS cd1 INNER JOIN catalogdatavalue AS cd2 ON cd1.catalogid = cd2.catalogid "
                + " WHERE cd1.catalogtypeattributeid = " + vsReqNationalSupplyId + " AND cd1.value = '"
                + vsReqNationalSupply + "' AND cd2.catalogtypeattributeid = " + vsReqStorageTempId
                + " AND cd2.value = '" + vsReqStorageTemp + "') " + " AND catalogtypeattributeid IN ("
                + vsReqCatalogAttribIds + ") AND " + " catalog.catalogtypeid = " + vsReqCatalogTypeId;

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer catalogId = rs.getInt( 1 );
                Integer catalogTypeAttribId = rs.getInt( 2 );
                String value = rs.getString( 3 );

                catalogDataForRequirement.put( catalogId + ":" + catalogTypeAttribId, value );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return catalogDataForRequirement;
    }

    public List<Integer> getCatalogIdsForRequirement( Integer vsReqCatalogTypeId, Integer vsReqStorageTempId,
        String vsReqStorageTemp, Integer vsReqNationalSupplyId, String vsReqNationalSupply )
    {
        List<Integer> catalogIdsForRequirement = new ArrayList<Integer>();
        try
        {
            String query = "SELECT DISTINCT(catalog.catalogid) FROM catalogdatavalue "
                + " INNER JOIN catalog ON catalog.catalogid = catalogdatavalue.catalogid "
                + " WHERE "
                + " catalog.catalogid IN "
                + "( SELECT cd1.catalogid FROM catalogdatavalue AS cd1 INNER JOIN catalogdatavalue AS cd2 ON cd1.catalogid = cd2.catalogid "
                + " WHERE cd1.catalogtypeattributeid = " + vsReqNationalSupplyId + " AND cd1.value = '"
                + vsReqNationalSupply + "' AND cd2.catalogtypeattributeid = " + vsReqStorageTempId
                + " AND cd2.value = '" + vsReqStorageTemp + "') " + " AND catalog.catalogtypeid = "
                + vsReqCatalogTypeId;

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer catalogId = rs.getInt( 1 );

                catalogIdsForRequirement.add( catalogId );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return catalogIdsForRequirement;
    }

    public Map<Integer, Double> getSumOfEquipmentDatabyInventoryType( String orgUnitIdsByComma,
        Integer inventoryTypeId, Integer inventoryTypeAttributeId, Double factor )
    {
        Map<Integer, Double> equipmentSumByInventoryTypeMap = new HashMap<Integer, Double>();
        try
        {
            String query = "SELECT organisationunitid, SUM(value*"
                + factor
                + ") FROM equipment "
                + " INNER JOIN equipmentinstance on equipment.equipmentinstanceid = equipmentinstance.equipmentinstanceid "
                + " WHERE " + " equipmentinstance.working = 1 AND " + " equipmentinstance.inventorytypeid =  "
                + inventoryTypeId + " AND " + " equipmentinstance.organisationunitid in (" + orgUnitIdsByComma
                + ") AND " + " equipment.inventorytypeattributeid = " + inventoryTypeAttributeId + " "
                + " GROUP BY organisationunitid";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Double catalogDataValueSum = rs.getDouble( 2 );
                equipmentSumByInventoryTypeMap.put( orgUnitId, catalogDataValueSum );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return equipmentSumByInventoryTypeMap;
    }

    public Map<Integer, Double> getCatalogDataSumByEquipmentData( String orgUnitIdsByComma, Integer inventoryTypeId,
        Integer catalogTypeAttributeId, Integer inventoryTypeAttributeId, String equipmentValue )
    {
        Map<Integer, Double> catalogSumByEquipmentDataMap = new HashMap<Integer, Double>();
        try
        {
            String query = "SELECT equipmentinstance.organisationunitid, sum(catalogdatavalue.value) FROM catalogdatavalue "
                + " INNER JOIN equipmentinstance on catalogdatavalue.catalogid = equipmentinstance.catalogid "
                + " WHERE "
                + " equipmentinstance.working = 1 AND "
                + " equipmentinstance.inventorytypeid =  "
                + inventoryTypeId
                + " AND "
                + " catalogdatavalue.catalogtypeattributeid =  "
                + catalogTypeAttributeId
                + " AND "
                + " equipmentinstance.organisationunitid in ("
                + orgUnitIdsByComma
                + ") AND "
                + " equipmentinstance.equipmentinstanceid in "
                + "( SELECT equipmentinstanceid FROM equipment WHERE inventorytypeattributeid = "
                + inventoryTypeAttributeId
                + " AND equipment.value IN ('"
                + equipmentValue
                + "') ) "
                + " GROUP BY equipmentinstance.organisationunitid";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Double catalogDataValueSum = rs.getDouble( 2 );
                catalogSumByEquipmentDataMap.put( orgUnitId, catalogDataValueSum );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return catalogSumByEquipmentDataMap;
    }

    public Map<String, Integer> getFacilityWiseEquipmentRoutineData( String orgUnitIdsByComma, String periodIdsByComma,
        String dataElementIdsByComma, String optComboIdsByComma )
    {
        Map<String, Integer> equipmentDataValueMap = new HashMap<String, Integer>();
        try
        {
            String query = "SELECT equipmentinstance.organisationunitid, dataelementid, periodid, value FROM equipmentdatavalue "
                + "INNER JOIN equipmentinstance "
                + " ON equipmentinstance.equipmentinstanceid = equipmentdatavalue.equipmentinstanceid "
                + " WHERE "
                + " equipmentinstance.organisationunitid IN ("
                + orgUnitIdsByComma
                + ") AND "
                + " dataelementid IN ("
                + dataElementIdsByComma + ") AND " + " periodid IN (" + periodIdsByComma + ")";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer dataElementId = rs.getInt( 2 );
                Integer periodId = rs.getInt( 3 );
                Integer value = rs.getInt( 4 );

                equipmentDataValueMap.put( orgUnitId + ":" + dataElementId + ":" + periodId, value );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return equipmentDataValueMap;
    }

    public Integer getPeriodId( String startDate, String periodType )
    {
        Integer periodId = 0;
        try
        {
            String query = "SELECT periodid FROM period "
                + " INNER JOIN periodtype ON period.periodtypeid = periodtype.periodtypeid " + " WHERE "
                + " periodtype.name = '" + periodType + "' AND " + " period.startdate = '" + startDate + "'";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            if ( rs != null && rs.next() )
            {
                periodId = rs.getInt( 1 );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return periodId;
    }

    public Map<String, Integer> getDataValueCountforDataElements( String dataElementIdsByComma,
        String optComboIdsByComma, Integer periodId, String orgUnitIdsBycomma )
    {
        Map<String, Integer> dataValueCountMap = new HashMap<String, Integer>();
        try
        {
            String query = "SELECT dataelementid, categoryoptioncomboid, value, COUNT(*) FROM datavalue " + " WHERE "
                + " dataelementid IN (" + dataElementIdsByComma + ") AND " + " periodid = " + periodId + " AND "
                + " sourceid IN ( " + orgUnitIdsBycomma + " ) " + " GROUP BY dataelementid,categoryoptioncomboid,value";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer dataElementId = rs.getInt( 1 );
                Integer optComboId = rs.getInt( 2 );
                String dataElementValue = rs.getString( 3 );
                Integer totCount = rs.getInt( 4 );

                dataValueCountMap.put( dataElementId + ":" + optComboId + ":" + dataElementValue, totCount );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return dataValueCountMap;
    }

    public List<String> getDistinctDataElementValue( Integer dataelementID, Integer optComboId, Integer periodId )
    {
        List<String> distinctDataElementValues = new ArrayList<String>();
        try
        {
            String query = "SELECT DISTINCT(value) FROM datavalue " + " WHERE " + " dataelementid = " + dataelementID
                + " AND " + " periodid = " + periodId + " AND " + " categoryoptioncomboid = " + optComboId;

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                String dataElementValue = rs.getString( 1 );
                distinctDataElementValues.add( dataelementID + ":" + optComboId + ":" + dataElementValue );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return distinctDataElementValues;
    }

    public List<Integer> getOrgunitIds( List<Integer> selOrgUnitList, Integer orgUnitGroupId )
    {
        String selOrgUnitsByComma = getCommaDelimitedString( selOrgUnitList );

        int maxOULevels = organisationUnitService.getMaxOfOrganisationUnitLevels();

        List<Integer> orgUnitIds = new ArrayList<Integer>();

        try
        {
            String query = "select orgunitgroupmembers.organisationunitid from orgunitgroupmembers "
                + " inner join _orgunitstructure on orgunitgroupmembers.organisationunitid = _orgunitstructure.organisationunitid "
                + " where orgunitgroupid in (" + orgUnitGroupId + ") and ( ";
            for ( int i = 1; i <= maxOULevels; i++ )
            {
                query += " idlevel" + i + " in (" + selOrgUnitsByComma + ") OR ";
            }
            query = query.substring( 0, query.length() - 4 );

            query += ")";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                orgUnitIds.add( orgUnitId );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return orgUnitIds;
    }
    

    public String getOrgunitIdsByComma( List<Integer> selOrgUnitList, List<Integer> orgunitGroupList )
    {
        String selOrgUnitsByComma = getCommaDelimitedString( selOrgUnitList );
        String selOrgUnitGroupsByComma = getCommaDelimitedString( orgunitGroupList );

        int maxOULevels = organisationUnitService.getMaxOfOrganisationUnitLevels();

        String orgUnitIdsByComma = "-1";

        try
        {
            String query = "select orgunitgroupmembers.organisationunitid from orgunitgroupmembers "
                + " inner join _orgunitstructure on orgunitgroupmembers.organisationunitid = _orgunitstructure.organisationunitid "
                + " where orgunitgroupid in (" + selOrgUnitGroupsByComma + ") and ( ";
            for ( int i = 1; i <= maxOULevels; i++ )
            {
                query += " idlevel" + i + " in (" + selOrgUnitsByComma + ") OR ";
            }
            query = query.substring( 0, query.length() - 4 );

            query += ")";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                orgUnitIdsByComma += "," + orgUnitId;
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return orgUnitIdsByComma;
    }

    public Map<String, Integer> getCatalogTypeAttributeValueByAge( String orgUnitIdsByComma, Integer inventoryTypeId,
        Integer catalogTypeAttributeId, Integer yearInvTypeAttId, Integer ageStart, Integer ageEnd )
    {
        Map<String, Integer> CatalogTypeAttributeValueMap = new HashMap<String, Integer>();
        try
        {

            String query = "SELECT catalogdatavalue.value, COUNT(*) FROM catalogdatavalue "
                + " INNER JOIN equipmentinstance ON catalogdatavalue.catalogid = equipmentinstance.catalogid "
                + " INNER JOIN equipment on equipmentinstance.equipmentinstanceid = equipment.equipmentinstanceid "
                + " WHERE " + " equipmentinstance.inventorytypeid = " + inventoryTypeId + " AND "
                + " catalogdatavalue.catalogtypeattributeid = " + catalogTypeAttributeId + " AND "
                + " equipment.inventorytypeattributeid = " + yearInvTypeAttId + " AND "
                + " ( YEAR(CURDATE()) - equipment.value ) >= " + ageStart + " AND ";

            if ( ageEnd != -1 )
            {
                query += " ( YEAR(CURDATE()) - equipment.value ) <= " + ageEnd + " AND ";
            }

            query += " equipmentinstance.organisationunitid IN ( " + orgUnitIdsByComma + " ) "
                + " GROUP BY catalogdatavalue.value";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                String catalogDataValue = rs.getString( 1 );
                Integer catalogDataValueCount = rs.getInt( 2 );
                CatalogTypeAttributeValueMap.put( catalogDataValue, catalogDataValueCount );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return CatalogTypeAttributeValueMap;
    }

    public Map<String, Integer> getCatalogTypeAttributeValue( String orgUnitIdsByComma, Integer inventoryTypeId,
        Integer catalogTypeAttributeId )
    {
        Map<String, Integer> CatalogTypeAttributeValueMap = new HashMap<String, Integer>();
        try
        {
            String query = "SELECT catalogdatavalue.value, COUNT(*) FROM catalogdatavalue "
                + " INNER JOIN equipmentinstance on catalogdatavalue.catalogid = equipmentinstance.catalogid "
                + " WHERE " + " equipmentinstance.inventorytypeid = " + inventoryTypeId + " AND "
                + " catalogdatavalue.catalogtypeattributeid = " + catalogTypeAttributeId + " AND "
                + " equipmentinstance.organisationunitid in (" + orgUnitIdsByComma + ")"
                + " GROUP BY catalogdatavalue.value";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                String catalogDataValue = rs.getString( 1 );
                Integer catalogDataValueCount = rs.getInt( 2 );
                CatalogTypeAttributeValueMap.put( catalogDataValue, catalogDataValueCount );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return CatalogTypeAttributeValueMap;
    }

    public Map<String, String> getCCEMSettings()
    {
        String fileName = "ccemSettings.xml";
        String path = System.getenv( "DHIS2_HOME" ) + File.separator + "ccemreports" + File.separator + fileName;

        Map<String, String> ccemSettingsMap = new HashMap<String, String>();

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "XML File Not Found at user home" );
                return null;
            }

            NodeList listOfReports = doc.getElementsByTagName( "ccemSetting" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element reportElement = (Element) reportNode;

                    String commonId = reportElement.getAttribute( "commonId" );
                    String ccemId = reportElement.getAttribute( "ccemId" );

                    ccemSettingsMap.put( commonId, ccemId );
                }
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }

        return ccemSettingsMap;
    }

    public List<CCEMReportDesign> getCCEMReportDesign( String designXMLFile )
    {
        String path = System.getenv( "DHIS2_HOME" ) + File.separator + "ccemreports" + File.separator + designXMLFile;

        List<CCEMReportDesign> ccemReportDesignList = new ArrayList<CCEMReportDesign>();

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "XML File Not Found at user home" );
                return null;
            }

            NodeList listOfReports = doc.getElementsByTagName( "ccemreportcell" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element reportElement = (Element) reportNode;

                    Integer row = Integer.parseInt( reportElement.getAttribute( "row" ) );
                    String content = reportElement.getAttribute( "content" );
                    String displayheading = reportElement.getAttribute( "displayheading" );

                    CCEMReportDesign ccemReportDesign = new CCEMReportDesign();
                    ccemReportDesign.setRow( row );
                    ccemReportDesign.setContent( content );
                    ccemReportDesign.setDisplayheading( displayheading );

                    ccemReportDesignList.add( ccemReportDesign );
                }
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }

        return ccemReportDesignList;
    }

    public CCEMReport getCCEMReportByReportId( String selReportId )
    {
        String fileName = "ccemReportList.xml";
        String path = System.getenv( "DHIS2_HOME" ) + File.separator + "ccemreports" + File.separator + fileName;

        CCEMReport reportObj = new CCEMReport();

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "XML File Not Found at user home" );
                return null;
            }

            NodeList listOfReports = doc.getElementsByTagName( "ccemReport" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element reportElement = (Element) reportNode;

                    NodeList nodeList1 = reportElement.getElementsByTagName( "reportId" );
                    Element element1 = (Element) nodeList1.item( 0 );
                    NodeList textNodeList1 = element1.getChildNodes();
                    String reportId = ((Node) textNodeList1.item( 0 )).getNodeValue().trim();

                    if ( !reportId.equalsIgnoreCase( selReportId ) )
                    {
                        continue;
                    }

                    NodeList nodeList2 = reportElement.getElementsByTagName( "reportName" );
                    Element element2 = (Element) nodeList2.item( 0 );
                    NodeList textNodeList2 = element2.getChildNodes();
                    String reportName = ((Node) textNodeList2.item( 0 )).getNodeValue().trim();

                    NodeList nodeList3 = reportElement.getElementsByTagName( "xmlTemplateName" );
                    Element element3 = (Element) nodeList3.item( 0 );
                    NodeList textNodeList3 = element3.getChildNodes();
                    String xmlTemplateName = ((Node) textNodeList3.item( 0 )).getNodeValue().trim();

                    NodeList nodeList4 = reportElement.getElementsByTagName( "outputType" );
                    Element element4 = (Element) nodeList4.item( 0 );
                    NodeList textNodeList4 = element4.getChildNodes();
                    String outputType = ((Node) textNodeList4.item( 0 )).getNodeValue().trim();

                    NodeList nodeList5 = reportElement.getElementsByTagName( "reportType" );
                    Element element5 = (Element) nodeList5.item( 0 );
                    NodeList textNodeList5 = element5.getChildNodes();
                    String reportType = ((Node) textNodeList5.item( 0 )).getNodeValue().trim();

                    NodeList nodeList6 = reportElement.getElementsByTagName( "periodRequire" );
                    Element element6 = (Element) nodeList6.item( 0 );
                    NodeList textNodeList6 = element6.getChildNodes();
                    String periodRequire = ((Node) textNodeList6.item( 0 )).getNodeValue().trim();

                    reportObj.setOutputType( outputType );
                    reportObj.setReportId( reportId );
                    reportObj.setReportName( reportName );
                    reportObj.setXmlTemplateName( xmlTemplateName );
                    reportObj.setReportType( reportType );
                    reportObj.setPeriodRequire( periodRequire );

                }
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }

        return reportObj;

    }// getReportList end

    public String getMinMaxAvgValues( String orgunitid, String periodid, Integer dataElementid, Integer optionCombo )
    {
        String dataValue = null;
        
        DatabaseInfo dataBaseInfo = databaseInfoProvider.getDatabaseInfo();
        
        try
        {
            String query = "";
            
            if ( dataBaseInfo.getType().equalsIgnoreCase( "postgresql" ) )
            {
                query = "SELECT Min(CAST(value AS NUMERIC)), Max(CAST(value AS NUMERIC)), ROUND(AVG(CAST(value AS NUMERIC))) FROM datavalue " +
                            " WHERE " +
                                " sourceid IN (" + orgunitid + ") AND " +
                                " dataelementid = " + dataElementid + " AND " +
                                " periodid IN ( " + periodid + ") AND " +
                                " categoryoptioncomboid = " + optionCombo;
            }
            else if ( dataBaseInfo.getType().equalsIgnoreCase( "mysql" ) )
            {
                query = "SELECT Min(CAST(value AS UNSIGNED)), Max(CAST(value AS UNSIGNED)), ROUND(AVG(CAST(value AS UNSIGNED))) FROM datavalue " +
                            " WHERE " +
                                " sourceid IN (" + orgunitid + ") AND " +
                                " dataelementid = " + dataElementid + " AND " +
                                " periodid IN ( " + periodid + ") AND " +
                                " categoryoptioncomboid = " + optionCombo;
            }
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                dataValue = rs.getInt( 1 ) + "," + rs.getInt( 2 ) + "," + rs.getInt( 3 );
                // System.out.println("Minimum and Maximum and Average Value is: "+dataValue);
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return dataValue;
    }

    public Integer getCountByOrgUnitGroup( Integer rootOrgUnitId, Integer orgUnitGroupId )
    {
    	Integer orgUnitCount = 0;
    	
        int maxOULevels = organisationUnitService.getMaxOfOrganisationUnitLevels();

        try
        {
            String query = "SELECT COUNT(orgunitgroupmembers.organisationunitid) FROM orgunitgroupmembers " + 
                			" INNER JOIN _orgunitstructure ON orgunitgroupmembers.organisationunitid = _orgunitstructure.organisationunitid " + 
                			" WHERE " + 
                				" orgunitgroupid IN (" + orgUnitGroupId +") AND ( "; 
            		
            for ( int i = 1; i <= maxOULevels; i++ )
            {
                query += " idlevel" + i + " IN (" + rootOrgUnitId + ") OR ";
            }

            query = query.substring( 0, query.length() - 4 );

            query += ")";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            if ( rs.next() )
            {
            	orgUnitCount = rs.getInt( 1 );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

    	return orgUnitCount;
    }
    
    public Integer getGrandTotalValue( String orgunitid, String periodid, Integer dataElementid )
    {
        Integer grandTotal = 0;
        try
        {

            String query = "SELECT count(*) FROM datavalue " + " WHERE " + " datavalue.sourceid IN (" + orgunitid
                + ") AND " + " datavalue.dataelementid = " + dataElementid + " AND " + " datavalue.periodid IN ( "
                + periodid + ") ";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                grandTotal = grandTotal + rs.getInt( 1 );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return grandTotal;
    }

    public Map<String, Integer> getCatalogDatavalueId( String orgUnitIdsByComma, Integer inventoryTypeId,
        Integer catalogTypeAttributeId )
    {
        Map<String, Integer> CatalogTypeAttributeValueMap = new HashMap<String, Integer>();
        try
        {
            String query = "SELECT catalogdatavalue.catalogid, catalogdatavalue.value FROM catalogdatavalue "
                + " INNER JOIN equipmentinstance on catalogdatavalue.catalogid = equipmentinstance.catalogid "
                + " WHERE " + " equipmentinstance.inventorytypeid = " + inventoryTypeId + " AND "
                + " catalogdatavalue.catalogtypeattributeid = " + catalogTypeAttributeId + " AND "
                + " equipmentinstance.organisationunitid IN (" + orgUnitIdsByComma + ")"
                + " GROUP BY catalogdatavalue.value";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer catalogDataValueId = rs.getInt( 1 );
                String catalogDataValue = rs.getString( 2 );
                CatalogTypeAttributeValueMap.put( catalogDataValue, catalogDataValueId );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return CatalogTypeAttributeValueMap;
    }

    public List<String> getModelName( Integer inventoryTypeId, Integer catalogTypeAttributeId, String orgUnitIds )
    {
        List<String> ModelNameList = new ArrayList<String>();
        try
        {
            String query = "SELECT catalogdatavalue.value FROM catalogdatavalue "
                + " INNER JOIN equipmentinstance on catalogdatavalue.catalogid = equipmentinstance.catalogid "
                + " WHERE " + " equipmentinstance.inventorytypeid = " + inventoryTypeId + " AND "
                + " catalogdatavalue.catalogtypeattributeid = " + catalogTypeAttributeId + " AND "
                + " equipmentinstance.organisationunitid IN (" + orgUnitIds + ")" + " GROUP BY catalogdatavalue.value";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                String catalogDataValue = rs.getString( 1 );
                ModelNameList.add( catalogDataValue );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return ModelNameList;
    }

    public String getEquipmentValue( String catalogTypeAttributeValue, Integer catalogid, String euipmentValue,
        String orgUnitIdsByComma, Integer inventoryTypeId )
    {
        String EquipmentValue = null;
        try
        {
            String query = "SELECT COUNT(*) FROM equipment "
                + "INNER JOIN equipmentinstance on equipment.equipmentinstanceid =equipmentinstance.equipmentinstanceid"
                + " WHERE " + " equipmentinstance.catalogid = " + catalogid + " AND " + " equipment.value like '%"
                + euipmentValue + "%' AND " + " equipmentinstance.organisationunitid IN (" + orgUnitIdsByComma
                + ") AND " + " equipmentinstance.inventorytypeid = " + inventoryTypeId + "";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                EquipmentValue = rs.getInt( 1 ) + "";
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return EquipmentValue;
    }

    public Map<String, Integer> getModelNameAndCount( Integer catalogTypeAttributeId, Integer inventoryTypeId,
        String equipmentValue, String orgUnitIdsByComma )
    {
        Map<String, Integer> EquipmentValue = new HashMap<String, Integer>();
        try
        {
            String query = "SELECT catalogdatavalue.value, COUNT(*) FROM catalogdatavalue "
                + "INNER JOIN equipmentinstance ON catalogdatavalue.catalogid = equipmentinstance.catalogid "
                + "INNER JOIN equipment ON equipment.equipmentinstanceid = equipmentinstance.equipmentinstanceid "
                + " WHERE " + " equipmentinstance.inventorytypeid =" + inventoryTypeId + " AND "
                + " equipment.value LIKE '%" + equipmentValue + "%' AND "
                + " catalogdatavalue.catalogtypeattributeid = " + catalogTypeAttributeId + " AND "
                + " equipmentinstance.organisationunitid IN (" + orgUnitIdsByComma + ")"
                + " GROUP BY catalogdatavalue.value ";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                EquipmentValue.put( rs.getString( 1 ), rs.getInt( 2 ) );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return EquipmentValue;
    }

    public Map<String, Integer> getEquipmentValue_Count( Integer inventoryTypeId, Integer inventoryTypeAttributeId, String orgUnitIds )
    {
        Map<String, Integer> equipmentValue_CountMap = new HashMap<String, Integer>();
        try
        {
        	String query = "SELECT equipment.value, COUNT(*) FROM equipment " +
					" INNER JOIN equipmentinstance ON equipment.equipmentinstanceid = equipmentinstance.equipmentinstanceid " + 
					" WHERE " + 
						" equipmentinstance.inventorytypeid = "+ inventoryTypeId +" AND " + 
						" equipment.inventorytypeattributeid = "+ inventoryTypeAttributeId +" AND "+ 
						" equipmentinstance.organisationunitid IN ( "+ orgUnitIds+" ) " +
						" GROUP BY equipment.value";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                String equipmentDataValue = rs.getString( 1 );
                Integer count = rs.getInt( 2 );
                equipmentValue_CountMap.put( equipmentDataValue, count );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return equipmentValue_CountMap;
    }
    
    public Map<String, Integer> getModelName_Count( Integer inventoryTypeId, Integer catalogTypeAttributeId, String orgUnitIds )
    {
        Map<String, Integer> modelName_CountMap = new HashMap<String, Integer>();
        try
        {
            String query = "SELECT catalogdatavalue.value, COUNT(*) FROM catalogdatavalue "
                + " INNER JOIN equipmentinstance on catalogdatavalue.catalogid = equipmentinstance.catalogid "
                + " WHERE " + " equipmentinstance.inventorytypeid = " + inventoryTypeId + " AND "
                + " catalogdatavalue.catalogtypeattributeid = " + catalogTypeAttributeId + " AND "
                + " equipmentinstance.organisationunitid IN (" + orgUnitIds + ")" + " GROUP BY catalogdatavalue.value";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                String catalogDataValue = rs.getString( 1 );
                Integer count = rs.getInt( 2 );
                modelName_CountMap.put( catalogDataValue, count );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return modelName_CountMap;
    }
    
    public Map<String, Map<String,Integer>> getModelName_EquipmentUtilization_Count( Integer inventoryTypeId, Integer catalogTypeAttributeId, Integer inventoryTypeAttributeId, String orgUnitIdsByComma )
    {
    	Map<String, Map<String,Integer>> modelName_EquipmentUnilization_CountMap = new HashMap<String, Map<String,Integer>>();
        
    	try
        {
        	String query = "SELECT catalogdatavalue.value, equipment.value, COUNT(*) FROM equipment " +
        						" INNER JOIN equipmentinstance ON equipment.equipmentinstanceid = equipmentinstance.equipmentinstanceid " + 
        						" INNER JOIN catalogdatavalue ON catalogdatavalue.catalogid = equipmentinstance.catalogid " + 
        						" WHERE " + 
        							" equipmentinstance.inventorytypeid = "+ inventoryTypeId +" AND " + 
        							" catalogdatavalue.catalogtypeattributeid = "+ catalogTypeAttributeId +" AND " + 
        							" equipment.inventorytypeattributeid = "+ inventoryTypeAttributeId +" AND "+ 
        							" equipmentinstance.organisationunitid IN ( "+ orgUnitIdsByComma+" ) " +
        							" GROUP BY catalogdatavalue.value,equipment.value";
        	
        	//System.out.println( query );
        	
        	SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
            	String modelName = rs.getString( 1 );
            	String equipmentUtilisation = rs.getString( 2 );
            	Integer count = rs.getInt( 3 );
            	
            	//System.out.println( modelName + " : " + equipmentUtilisation + " : " + count );
            	
            	Map<String,Integer> equipmentUtilisationMap = modelName_EquipmentUnilization_CountMap.get( modelName );
            	if( equipmentUtilisationMap == null )
            	{
            		equipmentUtilisationMap = new HashMap<String, Integer>();
            		equipmentUtilisationMap.put( equipmentUtilisation, count );
            	}
            	else
            	{
            		equipmentUtilisationMap.put( equipmentUtilisation, count );
            	}
            	
            	modelName_EquipmentUnilization_CountMap.put( modelName, equipmentUtilisationMap );
            }
        }
        catch ( Exception e )
        {
        	throw new RuntimeException( "Exception: ", e );
        }
        
        return modelName_EquipmentUnilization_CountMap;
    }
    

    public Map<String, Map<String,Integer>> getEquipmentType_ElectricityAvailability_Count( Integer inventoryTypeId, Integer catalogTypeAttributeId, Integer dataElementId, String periodIdsByComma, String orgUnitIdsByComma )
    {
    	Map<String, Map<String,Integer>> equiplmentType_ElectricityAvailability_CountMap = new HashMap<String, Map<String,Integer>>();
        
    	try
        {
        	String query = "SELECT catalogdatavalue.value, datavalue.value, COUNT(*) FROM catalogdatavalue " + 
        						" INNER JOIN equipmentinstance ON catalogdatavalue.catalogid = equipmentinstance.catalogid " + 
        						" INNER JOIN datavalue ON datavalue.sourceid = equipmentinstance.organisationunitid " +
        						" WHERE " +
        							" equipmentinstance.inventorytypeid = "+ inventoryTypeId +" AND " + 
        							" catalogdatavalue.catalogtypeattributeid = "+ catalogTypeAttributeId +" AND " + 
        							" datavalue.dataelementid = " + dataElementId + " AND " + 
        							" datavalue.periodid in ( " + periodIdsByComma + " ) AND " +  
        							" datavalue.sourceid IN ( "+ orgUnitIdsByComma +" ) " + 
        							" GROUP BY catalogdatavalue.value, datavalue.value " + 
        							" ORDER BY catalogdatavalue.value"; 
        		
        	SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
            	String equipmentType = rs.getString( 1 );
            	String electricityAvailability = rs.getString( 2 );
            	Integer count = rs.getInt( 3 );
            	
            	Map<String,Integer> electricityAvailabilityMap = equiplmentType_ElectricityAvailability_CountMap.get( equipmentType );
            	if( electricityAvailabilityMap == null )
            	{
            		electricityAvailabilityMap = new HashMap<String, Integer>();
            		electricityAvailabilityMap.put( electricityAvailability, count );
            	}
            	else
            	{
            		electricityAvailabilityMap.put( electricityAvailability, count );
            	}
            	
            	equiplmentType_ElectricityAvailability_CountMap.put( equipmentType, electricityAvailabilityMap );
            }
        }
        catch ( Exception e )
        {
        	throw new RuntimeException( "Exception: ", e );
        }
        
        return equiplmentType_ElectricityAvailability_CountMap;
    }

    
    public Integer getDataValue( String dataelementId, String dataValue, String orgUnitByIds,String periodId )
    {
        Integer count = 0;
        try
        {   
            String query = "SELECT COUNT(*) FROM datavalue " + " WHERE " + " dataelementid IN ( " + dataelementId
                + ") AND " + "sourceid IN ( " + orgUnitByIds + " )";
            
                if(dataValue != null)
                {
                    query+= " AND value LIKE '%" + dataValue + "%'";
                } 
                if(periodId != null)
                {
                    query+= " AND periodid IN ("+periodId+")"; 
                }

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                count = rs.getInt( 1 );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return count;
    }
    
    public Map<String,Integer> getDataValueAndCount(String dataelementId, String orgUnitByIds, String periodId )
    {
        Map<String,Integer> datavalueMap = new HashMap<String, Integer>();
        try
        {   
            String query = "SELECT value,COUNT(*) FROM datavalue " + " WHERE " + " dataelementid IN ( " + dataelementId
                + ") AND " + "sourceid IN ( " + orgUnitByIds + " )";
                            
                if(periodId != null)
                {
                    query+= " AND periodid IN ("+periodId+")"; 
                }
                query+= " GROUP BY value";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                datavalueMap.put( rs.getString( 1 ),  rs.getInt( 2 ) );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return datavalueMap; 
    }
    
    public Map<String,Integer> getDataValueAndCount( String dataelementId )
    {
        Map<String,Integer> datavalueMap = new HashMap<String, Integer>();
        try
        {   
            String query = "SELECT value,COUNT(*) FROM datavalue " + 
            				" WHERE " + 
            					" dataelementid IN ( " + dataelementId + ")  GROUP BY value";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                datavalueMap.put( rs.getString( 1 ),  rs.getInt( 2 ) );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return datavalueMap; 
    }
    
    
    public List<String> getEquipmentValueAndData( Integer catalogTypeAttributeId, String orgUnitIdsByComma,
        Integer inventoryTypeId )
    {
        List<String> values = new ArrayList<String>();

        try
        {
            String query = "SELECT equipmentinstance.organisationunitid ,catalogdatavalue.value , count(*) ,equipment.value ,catalogdatavalue.catalogid FROM catalogdatavalue "
                + "INNER JOIN equipmentinstance ON catalogdatavalue.catalogid = equipmentinstance.catalogid "
                + "INNER JOIN equipment ON equipment.equipmentinstanceid = equipmentinstance.equipmentinstanceid "
                + " WHERE "
                + " equipmentinstance.inventorytypeid ="
                + inventoryTypeId
                + " AND "
                + " catalogdatavalue.catalogtypeattributeid = "
                + catalogTypeAttributeId
                + " AND "
                + " equipment.value = "
                + "'Not working'"
                + " OR equipment.value = "
                + "'Working but needs maintenance'"
                + " AND "
                + " equipmentinstance.organisationunitid IN ("
                + orgUnitIdsByComma + ") " + " GROUP BY equipmentinstance.organisationunitid";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {

                String EquipmentValue = rs.getString( 1 ) + "," + rs.getString( 2 ) + "," + rs.getString( 3 ) + ","
                    + rs.getString( 4 ) + "," + rs.getString( 5 );
                values.add( EquipmentValue.trim() );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return values;
    }

    public List<String> equipmentCatalogies( String orgUnitIdsByComma, Integer inventoryTypeId )
    {
        List<String> catalogIds = new ArrayList<String>();
        try
        {
            String query = "SELECT equipmentinstance.catalogid FROM equipmentinstance"
                + " INNER JOIN equipment ON equipment.equipmentinstanceid = equipmentinstance.equipmentinstanceid "
                + " WHERE equipmentinstance.inventorytypeid = " + inventoryTypeId + " AND "
                + " equipmentinstance.organisationunitid IN (" + orgUnitIdsByComma + ") "
                + " AND equipment.value = 'Not working' OR equipment.value = 'Working but needs maintenance'"
                + " AND equipmentinstance.catalogid IS NOT NULL" + " GROUP BY equipmentinstance.catalogid";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs != null && rs.next() )
            {
                catalogIds.add( rs.getString( 1 ) );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return catalogIds;
    }

    public Map<String, String> equipmentCatalogyValues( String orgUnitIdsByComma, Integer inventoryTypeId,
        Integer inventoryTypeAttributeId )
    {
        Map<String, String> catalogValues = new HashMap<String, String>();
        try
        {
            String query = "SELECT equipmentinstance.catalogid , equipment.value FROM equipment"
                + " INNER JOIN equipmentinstance ON equipment.equipmentinstanceid = equipmentinstance.equipmentinstanceid"
                + " WHERE equipmentinstance.inventorytypeid = " + inventoryTypeId
                + " AND equipment.inventorytypeattributeid = " + inventoryTypeAttributeId
                + " AND equipmentinstance.organisationunitid IN (" + orgUnitIdsByComma + ") "
                + " GROUP BY equipmentinstance.catalogid";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs != null && rs.next() )
            {
                catalogValues.put( rs.getString( 1 ), rs.getString( 2 ) );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return catalogValues;
    }

    public Map<String, String> equipmentOrgUnit( String orgUnitIdsByComma, Integer inventoryTypeId )
    {
        Map<String, String> catalogs = new HashMap<String, String>();
        try
        {
            String query = "SELECT equipmentinstance.catalogid, equipmentinstance.organisationunitid FROM equipmentinstance"
                + " INNER JOIN equipment ON equipment.equipmentinstanceid = equipmentinstance.equipmentinstanceid "
                + " WHERE equipmentinstance.inventorytypeid = "
                + inventoryTypeId
                + " AND "
                + " equipmentinstance.organisationunitid IN ("
                + orgUnitIdsByComma
                + ") "
                + " AND equipment.value = 'Not working' OR equipment.value = 'Working but needs maintenance'"
                + " AND equipmentinstance.catalogid IS NOT NULL" + " GROUP BY equipmentinstance.catalogid";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                catalogs.put( rs.getString( 1 ), rs.getString( 2 ) );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return catalogs;
    }

    public Map<String, String> getEquipmentNameWithOrgUnit( Integer inventoryTypeId, Integer catalogTypeAttributeId,
        String orgUnitIds )
    {
        Map<String, String> equipmentMap = new HashMap<String, String>();
        try
        {
            String query = "SELECT catalogdatavalue.value ,equipmentinstance.organisationunitid FROM catalogdatavalue "
                + " INNER JOIN equipmentinstance on catalogdatavalue.catalogid = equipmentinstance.catalogid "
                + " INNER JOIN equipment on equipmentinstance.equipmentinstanceid = equipment.equipmentinstanceid "
                + " WHERE " + " equipmentinstance.inventorytypeid = " + inventoryTypeId + " AND "
                + " catalogdatavalue.catalogtypeattributeid = " + catalogTypeAttributeId + " AND "
                + " equipmentinstance.organisationunitid IN (" + orgUnitIds + ") ORDER BY catalogdatavalue.value";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            String orgUnitIdByComma = null;
            while ( rs.next() )
            {
                String catalogDataValue = rs.getString( 1 );
                String orgUnitId = rs.getString( 2 );

                if ( equipmentMap.containsKey( catalogDataValue ) )
                {
                    orgUnitIdByComma = orgUnitIdByComma + "," + orgUnitId;
                }
                else
                {
                    orgUnitIdByComma = orgUnitId;
                }
                equipmentMap.put( catalogDataValue, orgUnitIdByComma );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return equipmentMap;
    }

    public Integer getTotalFacilitiesWithOrgUnit( String orgUnitIdsById )
    {
        Integer totalValue = 0;
        try
        {
            String query = "SELECT count(*) FROM datavalue " + " WHERE " + " datavalue.sourceid IN (" + orgUnitIdsById
                + ") GROUP BY dataelementid";
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                totalValue = rs.getInt( 1 );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return totalValue;
    }

    public Map<String, String> getTotalColdRoomValue( Integer inventorytypeid, String orgUnitIdByComma,
        String inventoryTypeAttributeId, String equipmentValue )
    {
        Map<String, String> countMap = new HashMap<String, String>();

        try
        {
            String query = "SELECT equipment.inventorytypeattributeid ,equipment.value FROM equipmentinstance"
                + " INNER JOIN equipment ON equipment.equipmentinstanceid = equipmentinstance.equipmentinstanceid"
                + " WHERE " + " equipmentinstance.inventorytypeid = " + inventorytypeid
                + " AND  equipmentinstance.organisationunitid IN (" + orgUnitIdByComma
                + ") AND equipment.inventorytypeattributeid IN (" + inventoryTypeAttributeId + ")";

            if ( equipmentValue != null )
            {
                query = query + " AND equipment.value like '%" + equipmentValue + "%'";
            }

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            Integer count = 1;
            String eValue = null;
            while ( rs.next() )
            {
                String eqValue = rs.getString( 2 );               
                System.out.println( "EQUIPMENT VALUE IS: " + rs.getString( 1 ) );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        System.out.println( "Size is: " + countMap.size() );
        return countMap;
    }

    public Map<String, Integer> getModelNameAndCountForColdBox( Integer catalogTypeAttributeId,
        Integer inventoryTypeId, String workingStatus, String orgUnitIdsByComma )
    {
        Map<String, Integer> EquipmentValue = new HashMap<String, Integer>();
        try
        {
            String query = "SELECT catalogdatavalue.value, COUNT(*) FROM catalogdatavalue "
                + " INNER JOIN equipmentinstance ON catalogdatavalue.catalogid = equipmentinstance.catalogid "
                + " WHERE " + " equipmentinstance.inventorytypeid =" + inventoryTypeId + " AND "
                + " equipmentinstance.working = " + workingStatus + " AND "
                + " catalogdatavalue.catalogtypeattributeid = " + catalogTypeAttributeId + " AND "
                + " equipmentinstance.organisationunitid IN (" + orgUnitIdsByComma + ")"
                + " GROUP BY catalogdatavalue.value ";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                EquipmentValue.put( rs.getString( 1 ), rs.getInt( 2 ) );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return EquipmentValue;
    }

    public String getModelNameAndCountForQuantityOfColdbox( Integer inventoryTypeId, String catalogValue,
        String orgUnitIdsByComma )
    {
        String EquipmentValue = null;
        try
        {
            String query = "SELECT MIN(CAST(equipment.value AS SIGNED)),MAx(CAST(equipment.value AS SIGNED)),AVG(CAST(equipment.value AS SIGNED)) FROM catalogdatavalue "
                + "INNER JOIN equipmentinstance ON catalogdatavalue.catalogid = equipmentinstance.catalogid "
                + " INNER JOIN equipment ON equipment.equipmentinstanceid = equipmentinstance.equipmentinstanceid"
                + " WHERE "
                + " equipmentinstance.inventorytypeid ="
                + inventoryTypeId
                + " AND "
                + " catalogdatavalue.value like '%"
                + catalogValue
                + "%' AND"
                + " equipmentinstance.organisationunitid IN (" + orgUnitIdsByComma + ")" + " ";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                EquipmentValue = rs.getString( 1 );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return EquipmentValue;
    }

    public Map<String, Double> getSumOfEquipmentAndCatalogValue( Integer inventoryTypeId,
        Integer inventoryTypeAttributeId, Integer catalogTypeAttributeId, String orgUnitIdsByComma )
    {
        Map<String, Double> catalogSumAndValue = new HashMap<String, Double>();
        try
        {
            String query = "SELECT catalogdatavalue.value, SUM(equipment.value) FROM equipment"
                + " INNER JOIN equipmentinstance ON equipment.equipmentinstanceid = equipmentinstance.equipmentinstanceid "
                + " INNER JOIN catalog ON catalog.catalogid = equipmentinstance.catalogid "
                + " INNER JOIN catalogdatavalue ON catalogdatavalue.catalogid = equipmentinstance.catalogid"
                + " WHERE " + " equipmentinstance.inventorytypeid =" + inventoryTypeId + " AND "
                + " equipment.inventorytypeattributeid = " + inventoryTypeAttributeId + " AND"
                + " catalogdatavalue.catalogtypeattributeid = " + catalogTypeAttributeId + " AND"
                + " equipmentinstance.organisationunitid IN (" + orgUnitIdsByComma + ")"
                + " GROUP BY equipmentinstance.catalogid";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                String catalogValue = rs.getString( 1 );
                Double equipmentSum = rs.getDouble( 2 );
                catalogSumAndValue.put( catalogValue, equipmentSum );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return catalogSumAndValue;
    } 
    public List<String> getDataValueFacility( Integer dataElementId, String dataValue, String orgUnitIdByComma,
        String periodIds )
    {
        List<String> facilityList = new ArrayList<String>();
        try
        {
            String query = "SELECT distinct(sourceid) FROM datavalue " + " WHERE " + " datavalue.dataelementid = "
                + dataElementId + "" + " AND datavalue.sourceid IN (" + orgUnitIdByComma + ")"
                + " AND datavalue.value IN (" + dataValue + ")" + " AND datavalue.periodid IN (" + periodIds + ")";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                facilityList.add( rs.getString( 1 ) );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return facilityList;
    }

    
    public Integer getEquipmentInstanceCount( Integer inventoryTypeId, String orgUnitIds )
    {
    	Integer equipmentInstanceCount = 0;
        try
        {
            String query = "SELECT COUNT(*) FROM equipmentinstance " +
            					" WHERE " +
            						" inventorytypeid = "+ inventoryTypeId +" AND " +
            						" organisationunitid IN ( "+ orgUnitIds +")";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
            	equipmentInstanceCount = rs.getInt( 1 );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return equipmentInstanceCount;
    }
    
    
    public Integer getCatalogDataValueCount( Integer inventoryTypeId, Integer catalogTypeAttributeId,
        String catogDataValue, String orgUnitIds )
    {
        Integer catalogValueCount = 0;
        try
        {
            String query = "SELECT COUNT(*) FROM catalogdatavalue "
                + " INNER JOIN equipmentinstance on catalogdatavalue.catalogid = equipmentinstance.catalogid "
                + " WHERE " + " equipmentinstance.inventorytypeid = " + inventoryTypeId + " AND "
                + " catalogdatavalue.catalogtypeattributeid = " + catalogTypeAttributeId + " AND "
                + " equipmentinstance.organisationunitid in (" + orgUnitIds + ")";
            if ( catogDataValue != null )
            {
                query += " AND catalogdatavalue.value in (" + catogDataValue + ")";
            }

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                catalogValueCount = rs.getInt( 1 );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return catalogValueCount;
    }
}
