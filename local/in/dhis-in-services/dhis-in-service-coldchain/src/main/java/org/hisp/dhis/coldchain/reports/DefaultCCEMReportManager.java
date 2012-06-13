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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultCCEMReportManager implements CCEMReportManager
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Implementation Methods
    // -------------------------------------------------------------------------

    public Map<String, Integer> getFacilityWiseEquipmentRoutineData( String orgUnitIdsByComma, String periodIdsByComma, String dataElementIdsByComma, String optComboIdsByComma )
    {
        Map<String, Integer> equipmentDataValueMap = new HashMap<String, Integer>();
        try
        {
            String query = "SELECT equipmentinstance.organisationunitid, dataelementid, periodid, value FROM equipmentdatavalue "+ 
                            "INNER JOIN equipmentinstance " +
                                " ON equipmentinstance.equipmentinstanceid = equipmentdatavalue.equipmentinstanceid " +                                
                            " WHERE "+
                                " equipmentinstance.organisationunitid IN ("+ orgUnitIdsByComma +") AND "+
                                " dataelementid IN ("+ dataElementIdsByComma +") AND " +
                                " periodid IN ("+ periodIdsByComma +")"; 

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer dataElementId = rs.getInt( 2 );
                Integer periodId = rs.getInt( 3 );                
                Integer value = rs.getInt( 4 );
                
                equipmentDataValueMap.put( orgUnitId+":"+dataElementId+":"+periodId, value );
            }
        }
        catch( Exception e )
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
            String query = "SELECT periodid FROM period " +
            		    " INNER JOIN periodtype ON period.periodtypeid = periodtype.periodtypeid " +
            		    " WHERE " +
            		        " periodtype.name = '"+ periodType +"' AND " +
            		        " period.startdate = '"+ startDate +"'"; 
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            if( rs!= null && rs.next() )
            {
                periodId = rs.getInt( 1 );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return periodId;
    }
    
    public Map<String, Integer> getDataValueCountforDataElements( String dataElementIdsByComma, String optComboIdsByComma, Integer periodId, String orgUnitIdsBycomma )
    {
        Map<String, Integer> dataValueCountMap = new HashMap<String, Integer>();
        try
        {
            String query = "SELECT dataelementid, categoryoptioncomboid, value, COUNT(*) FROM datavalue " +
            		    " WHERE " +
            		        " dataelementid IN ("+ dataElementIdsByComma +") AND " +
            		    	" periodid = "+ periodId +" AND " +
            		    	" sourceid IN ( "+ orgUnitIdsBycomma +" ) " +
            		    	" GROUP BY dataelementid,categoryoptioncomboid,value";
                
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer dataElementId = rs.getInt( 1 );
                Integer optComboId = rs.getInt( 2 );                
                String dataElementValue = rs.getString( 3 );
                Integer totCount = rs.getInt( 4 );
                
                dataValueCountMap.put( dataElementId+":"+optComboId+":"+dataElementValue, totCount );
            }
        }
        catch( Exception e )
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
            String query = "SELECT DISTINCT(value) FROM datavalue " +                            
                            " WHERE " +
                                " dataelementid = "+ dataelementID +" AND " +
                                " periodid = "+ periodId +" AND " +
                                " categoryoptioncomboid = "+ optComboId;
                                

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                String dataElementValue = rs.getString( 1 );
                distinctDataElementValues.add( dataelementID+":"+optComboId+":"+dataElementValue );
            }
        }
        catch( Exception e )
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
            String query = "select orgunitgroupmembers.organisationunitid from orgunitgroupmembers " +
                            " inner join _orgunitstructure on orgunitgroupmembers.organisationunitid = _orgunitstructure.organisationunitid " + 
                            " where orgunitgroupid in (" + orgUnitGroupId + ") and ( ";
            for( int i = 1; i <= maxOULevels; i++ )
            {
                query += " idlevel"+ i +" in (" + selOrgUnitsByComma + ") OR ";
            }
            query = query.substring( 0, query.length()-4 );
            
            query += ")";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                orgUnitIds.add( orgUnitId );
            }
        }
        catch( Exception e )
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
            String query = "select orgunitgroupmembers.organisationunitid from orgunitgroupmembers " +
                            " inner join _orgunitstructure on orgunitgroupmembers.organisationunitid = _orgunitstructure.organisationunitid " + 
                            " where orgunitgroupid in (" + selOrgUnitGroupsByComma + ") and ( ";
            for( int i = 1; i <= maxOULevels; i++ )
            {
                query += " idlevel"+ i +" in (" + selOrgUnitsByComma + ") OR ";
            }
            query = query.substring( 0, query.length()-4 );
            
            query += ")";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                orgUnitIdsByComma += "," + orgUnitId;
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return orgUnitIdsByComma;
    }
    
    public Map<String, Integer> getCatalogTypeAttributeValueByAge( String orgUnitIdsByComma, Integer inventoryTypeId, Integer catalogTypeAttributeId, Integer yearInvTypeAttId, Integer ageStart, Integer ageEnd )
    {
        Map<String, Integer> CatalogTypeAttributeValueMap = new HashMap<String, Integer>();
        try
        {
            /*
            String query = "SELECT catalogdatavalue.value, COUNT(*) FROM equipment INNER JOIN equipmentinstance ON "+ 
                            " equipmentinstance.equipmentinstanceid = equipment.equipmentinstanceid INNER JOIN catalog ON equipment.value = catalog.name " +
                            " INNER JOIN catalogdatavalue ON catalog.catalogid = catalogdatavalue.catalogid " + 
                            " WHERE equipmentinstance.equipmentinstanceid IN " +
                                            "( SELECT equipmentinstanceid FROM equipment WHERE inventorytypeattributeid = "+yearInvTypeAttId+" AND " +
                                            		"( YEAR(CURDATE()) - value ) >= "+ageStart+" AND ( YEAR(CURDATE()) - value ) <= "+ageEnd+") AND " +
                            "organisationunitid IN ("+ orgUnitIdsByComma +") AND inventorytypeattributeid = "+ inventoryTypeAttributeId +" AND " +
                            " catalog.catalogtypeid = "+ catalogTypeId +" AND catalogdatavalue.catalogtypeattributeid = "+ catalogTypeAttributeId +" GROUP BY catalogdatavalue.value";
            */
            
            String query = "SELECT catalogdatavalue.value, COUNT(*) FROM catalogdatavalue "+
                           " INNER JOIN equipmentinstance ON catalogdatavalue.catalogid = equipmentinstance.catalogid "+ 
                           " INNER JOIN equipment on equipmentinstance.equipmentinstanceid = equipment.equipmentinstanceid "+ 
                           " WHERE "+ 
                                " equipmentinstance.inventorytypeid = "+ inventoryTypeId +" AND "+ 
                                " catalogdatavalue.catalogtypeattributeid = "+ catalogTypeAttributeId +" AND "+ 
                                " equipment.inventorytypeattributeid = "+ yearInvTypeAttId +" AND "+
                                " ( YEAR(CURDATE()) - equipment.value ) >= "+ ageStart +" AND "; 
                                
                                if( ageEnd != -1 )
                                {
                                   query +=  " ( YEAR(CURDATE()) - equipment.value ) <= "+ ageEnd +" AND ";
                                }
                                
                                query += " equipmentinstance.organisationunitid IN ( "+orgUnitIdsByComma+" ) " +
                                " GROUP BY catalogdatavalue.value";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                String catalogDataValue = rs.getString( 1 );
                Integer catalogDataValueCount = rs.getInt( 2 );
                CatalogTypeAttributeValueMap.put( catalogDataValue, catalogDataValueCount );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return CatalogTypeAttributeValueMap;
    }
    
    
    public Map<String, Integer> getCatalogTypeAttributeValue( String orgUnitIdsByComma, Integer inventoryTypeId, Integer catalogTypeAttributeId )
    {
        Map<String, Integer> CatalogTypeAttributeValueMap = new HashMap<String, Integer>();
        try
        {
            /*
            String query = "SELECT catalogdatavalue.value, COUNT(*) FROM equipment INNER JOIN equipmentinstance ON "+ 
                            " equipmentinstance.equipmentinstanceid = equipment.equipmentinstanceid INNER JOIN catalog ON equipment.value = catalog.name " +
                            " INNER JOIN catalogdatavalue ON catalog.catalogid = catalogdatavalue.catalogid " + 
                            " WHERE organisationunitid IN ("+ orgUnitIdsByComma +") AND inventorytypeattributeid = "+ inventoryTypeAttributeId +" AND " +
                            " catalog.catalogtypeid = "+ catalogTypeId +" AND catalogdatavalue.catalogtypeattributeid = "+ catalogTypeAttributeId +" GROUP BY catalogdatavalue.value";
            */
            
            String query = "SELECT catalogdatavalue.value, COUNT(*) FROM catalogdatavalue " +
            		    " INNER JOIN equipmentinstance on catalogdatavalue.catalogid = equipmentinstance.catalogid "+
            		    " WHERE " +
            		        " equipmentinstance.inventorytypeid = "+ inventoryTypeId +" AND " +
            		        " catalogdatavalue.catalogtypeattributeid = "+ catalogTypeAttributeId +" AND "+            		        
            		        " equipmentinstance.organisationunitid in ("+ orgUnitIdsByComma +")" +
            		    " GROUP BY catalogdatavalue.value";    
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                String catalogDataValue = rs.getString( 1 );
                Integer catalogDataValueCount = rs.getInt( 2 );
                CatalogTypeAttributeValueMap.put( catalogDataValue, catalogDataValueCount );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return CatalogTypeAttributeValueMap;
    }
    
    public Map<String,String> getCCEMSettings()
    {
        String fileName = "ccemSettings.xml";
        String path = System.getenv( "DHIS2_HOME" )+ File.separator + "ccemreports" + File.separator + fileName;
        
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
        String path = System.getenv( "DHIS2_HOME" )+ File.separator + "ccemreports" + File.separator + designXMLFile;
        
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
        String path = System.getenv( "DHIS2_HOME" )+ File.separator + "ccemreports" + File.separator + fileName;
        
        CCEMReport reportObj = new CCEMReport( );
        
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

                    if( !reportId.equalsIgnoreCase( selReportId ) )
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

    
}
