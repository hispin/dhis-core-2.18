package org.hisp.dhis.integration.rims.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.integration.rims.api.RIMSService;
import org.hisp.dhis.integration.rims.api.RIMSTable;
import org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement;
import org.hisp.dhis.integration.rims.api.RIMS_Mapping_Orgunit;
import org.hisp.dhis.integration.rims.api.RIMS_Tables;
import org.hisp.dhis.integration.rims.api.tables.RIMSDistrict;
import org.hisp.dhis.integration.rims.api.tables.RIMS_PHC;
import org.hisp.dhis.integration.rims.util.Configuration;
import org.hisp.dhis.integration.util.DataBaseConnection;
import org.hisp.dhis.period.PeriodService;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class DefaultRIMSService
    implements RIMSService
{    
    private DataSetService datasetService;
    
    private DataElementService deService;
    
    private PeriodService periodService;

    public DataSetService getDataSetService()
    {
        return datasetService;
    }

    public void setDataSetService( DataSetService datasetService )
    {
        this.datasetService = datasetService;
    }
    
    public DataElementService getDataElementService()
    {
        return deService;
    }

    public void setDataElementService( DataElementService deService )
    {
        this.deService = deService;
    }

    public Map<String, String> getAllRIMSDEGroups()
    {

        Map<String, String> rimsDEGroups = new HashMap<String, String>();
        for ( RIMSTable table: new Configuration().getTables() )
        {
            rimsDEGroups.put( table.getTableName(), table.getDescription() );
        }
        return rimsDEGroups;
    }

    public RIMS_Mapping_Orgunit getMappingOrgUnit( String orgUnitCode )
    {
        String ouMappingXMLFileName = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
            + "di" + File.separator + "rims" + File.separator + "orgUnitMapping.xml";

        RIMS_Mapping_Orgunit mappingOU = null;

        try
        {
            String newpath = System.getenv( "USER_HOME" );
            if ( newpath != null )
            {
                ouMappingXMLFileName = newpath + File.separator + "dhis" + File.separator + "di" + File.separator
                    + "rims" + File.separator + "orgUnitMapping.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            System.out.println( "DataElement Mapping XML File Not Found, Make sure the Path" );
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( ouMappingXMLFileName ) );

            NodeList listOfPHCs = doc.getElementsByTagName( "organisationunit" );
            mappingOU = getMappingElement( orgUnitCode, listOfPHCs );

            if ( mappingOU == null )
            {
                NodeList listOfDistricts = doc.getElementsByTagName( "district" );
                mappingOU = getMappingElement( orgUnitCode, listOfDistricts );
            }
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

        return mappingOU;
    }

    private RIMS_Mapping_Orgunit getMappingElement( String orgUnitCode, NodeList listOfOrgunits )
        throws DOMException
    {
        int totalOrgUnits = listOfOrgunits.getLength();

        RIMS_Mapping_Orgunit mappingOU = null;
        for ( int s = 0; s < totalOrgUnits; s++ )
        {
            Node ousNode = listOfOrgunits.item( s );

            if ( ousNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element ouElement = (Element) ousNode;

                NodeList rimsOUList = ouElement.getElementsByTagName( "rims" );
                Element rimsOUElement = (Element) rimsOUList.item( 0 );
                NodeList textRIMSOUList = rimsOUElement.getChildNodes();
                String rimsOU = ((Node) textRIMSOUList.item( 0 )).getNodeValue().trim();

                NodeList dhisOUList = ouElement.getElementsByTagName( "dhis" );
                Element dhisOUElement = (Element) dhisOUList.item( 0 );
                NodeList textDHISOUList = dhisOUElement.getChildNodes();
                String dhisOU = ((Node) textDHISOUList.item( 0 )).getNodeValue().trim();

                String ouName = ouElement.getAttribute( "name" );

                if ( rimsOU.equalsIgnoreCase( orgUnitCode ) )
                {
                    mappingOU = new RIMS_Mapping_Orgunit( dhisOU, rimsOU, ouName );
                    break;
                }

            }// if block end
        }// end of for loop
        return mappingOU;
    }

    public List<RIMS_PHC> getPHCsByDistrict( String district_code, String connectionName ) throws SQLException
    {
        List<RIMS_PHC> rimsPHCList = new ArrayList<RIMS_PHC>();
        List<RIMS_Mapping_Orgunit> rimsMappingOrgUnits = new ArrayList<RIMS_Mapping_Orgunit>( getAllMappingOrgunits() );

        Connection con = (new DataBaseConnection()).openConnection( DataBaseConnection.RIMS, connectionName );

        Statement st1 = null;
        ResultSet rs1 = null;

        String phc_code = "";
        String phc_name = "";
        String state_code = "";
        String district_name = "";

        String query = "";
        try
        {
            query = "SELECT " + RIMS_Tables.PROFILE_PHC_MST + ".phc_code, " + RIMS_Tables.DISTRICT_MST
                + ".district_code, " + RIMS_Tables.PROFILE_PHC_MST + ".phc_name, " + RIMS_Tables.PROFILE_PHC_MST
                + ".state_code, " + RIMS_Tables.DISTRICT_MST + ".district_name " + "FROM " + RIMS_Tables.DISTRICT_MST
                + " INNER JOIN " + RIMS_Tables.PROFILE_PHC_MST + " ON " + RIMS_Tables.DISTRICT_MST
                + ".district_code = " + RIMS_Tables.PROFILE_PHC_MST + ".district_code" + " WHERE (("
                + RIMS_Tables.DISTRICT_MST + ".district_code LIKE '" + district_code + "'))";

            st1 = con.createStatement();
            rs1 = st1.executeQuery( query );

            while ( rs1.next() )
            {
                phc_code = rs1.getString( 1 );
                district_code = rs1.getString( 2 );
                phc_name = rs1.getString( 3 );
                state_code = rs1.getString( 4 );
                district_name = rs1.getString( 5 );

                int flag = 0;
                Iterator<RIMS_Mapping_Orgunit> iterator1 = rimsMappingOrgUnits.iterator();
                while ( iterator1.hasNext() )
                {
                    RIMS_Mapping_Orgunit mappingOU = (RIMS_Mapping_Orgunit) iterator1.next();
                    if ( mappingOU.getRimsid().equalsIgnoreCase( phc_code ) )
                    {
                        flag = 1;
                        break;
                    }
                }
                if ( flag == 1 )
                {
                    RIMS_PHC rimsPHCObject = new RIMS_PHC( phc_code, district_code, state_code,
                        phc_name, district_name );
                    rimsPHCList.add( rimsPHCObject );
                }
            }
        }
        finally
        {
            try
            {
                if ( rs1 != null )
                    rs1.close();
                if ( st1 != null )
                    st1.close();

                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( e.getMessage() );
            }
        }// finally block end

        return rimsPHCList;
    }

    public RIMS_PHC getPHC( String rimsOU, String connection ) throws SQLException
    {
        RIMS_PHC rimsPHCObject = new RIMS_PHC();

        Connection con = (new DataBaseConnection()).openConnection( DataBaseConnection.RIMS, connection );

        Statement st1 = null;
        ResultSet rs1 = null;

        Statement st2 = null;
        ResultSet rs2 = null;

        String phc_code = "";
        String district_code = "";
        String phc_name = "";
        String state_code = "";
        String district_name = "";

        String query = "";
        try
        {
            query = "SELECT phc_code,district_code,phc_name,state_code FROM " + RIMS_Tables.PROFILE_PHC_MST
                + " WHERE phc_code LIKE '" + rimsOU + "'";
            st1 = con.createStatement();
            rs1 = st1.executeQuery( query );

            if ( rs1.next() )
            {
                phc_code = rs1.getString( 1 );
                district_code = rs1.getString( 2 );
                phc_name = rs1.getString( 3 );
                state_code = rs1.getString( 4 );

                query = "SELECT district_name FROM " + RIMS_Tables.DISTRICT_MST + " WHERE district_code LIKE '"
                    + district_code + "'";
                st2 = con.createStatement();
                rs2 = st2.executeQuery( query );
                if ( rs2.next() )
                {
                    district_name = rs2.getString( 1 );
                }
            }

            rimsPHCObject = new RIMS_PHC( phc_code, district_code, state_code, phc_name, district_name );
        }
        finally
        {
            try
            {
                if ( rs1 != null )
                    rs1.close();
                if ( st1 != null )
                    st1.close();
                if ( rs2 != null )
                    rs2.close();
                if ( st2 != null )
                    st2.close();

                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( e.getMessage() );
            }
        }// finally block end

        return rimsPHCObject;
    }
    
    public RIMSDistrict getDistrict( String rimsOU, String connection ) throws SQLException
    {
        RIMSDistrict district = new RIMSDistrict();

        Connection con = (new DataBaseConnection()).openConnection( DataBaseConnection.RIMS, connection );

        Statement st1 = null;
        ResultSet rs1 = null;
        
        String district_code = "";
        String district_name = "";
        String state_code = "";

        String query = "";
        try
        {
            query = "SELECT district_code,district_name,state_code FROM " + RIMS_Tables.DISTRICT_MST
                + " WHERE district_code LIKE '" + rimsOU + "'";
            st1 = con.createStatement();
            rs1 = st1.executeQuery( query );

            if ( rs1.next() )
            {
                district_code = rs1.getString( "district_code" );
                district_name = rs1.getString( "district_name" );
                state_code = rs1.getString( "state_code" );
            }

            district = new RIMSDistrict( district_code, district_name, state_code);
        }
        finally
        {
            try
            {
                if ( rs1 != null )
                    rs1.close();
                if ( st1 != null )
                    st1.close();

                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( e.getMessage() );
            }
        }// finally block end

        return district;
    }

    public List<RIMS_PHC> getAllDistrictsofAllPHCs( String connection ) throws IOException, SAXException, SQLException
    {
        String ouMappingXMLFileName = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
            + "di" + File.separator + "rims" + File.separator + "orgUnitMapping.xml";

        List<RIMS_PHC> rimsOrgUnits = new ArrayList<RIMS_PHC>();

        try
        {
            String newpath = System.getenv( "USER_HOME" );
            if ( newpath != null )
            {
                ouMappingXMLFileName = newpath + File.separator + "dhis" + File.separator + "di" + File.separator
                    + "rims" + File.separator + "orgUnitMapping.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            throw new IOException( "DataElement Mapping XML File Not Found, Make sure the Path", npe );
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( ouMappingXMLFileName ) );

            NodeList listOfOrgunits = doc.getElementsByTagName( "organisationunit" );

            int totalOrgUnits = listOfOrgunits.getLength();

            for ( int s = 0; s < totalOrgUnits; s++ )
            {
                Node ousNode = listOfOrgunits.item( s );

                if ( ousNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element ouElement = (Element) ousNode;

                    NodeList rimsOUList = ouElement.getElementsByTagName( "rims" );
                    Element rimsOUElement = (Element) rimsOUList.item( 0 );
                    NodeList textRIMSOUList = rimsOUElement.getChildNodes();
                    String rimsOU = ((Node) textRIMSOUList.item( 0 )).getNodeValue().trim();

                    // String tableName = ouElement.getAttribute( "tname" );

                    /* When districts are included, discriminate between PHCs and
                     * districts by their XML tag names.
                    if ( tableName.equalsIgnoreCase( RIMS_Tables.PROFILE_PHC_MST ) )
                    {
                    */
                        RIMS_PHC phcOU = getPHC( rimsOU, connection );

                        int flag = 0;
                        Iterator<RIMS_PHC> iterator1 = rimsOrgUnits.iterator();
                        while ( iterator1.hasNext() )
                        {
                            RIMS_PHC phc = (RIMS_PHC) iterator1.next();

                            if ( phc.getDistrict_code().equalsIgnoreCase( phcOU.getDistrict_code() ) )
                            {
                                flag = 1;
                                break;
                            }
                        }
                        if ( flag == 0 )
                            rimsOrgUnits.add( phcOU );

                    // }
                }// if block end
            }// end of for loop
        }// try block end
        catch ( SAXParseException err )
        {
            throw (SAXParseException) new SAXParseException( "** Parsing error" + ", line "
                + err.getLineNumber() + ", uri " + err.getSystemId(), 
                err.getPublicId(), err.getSystemId(), err.getLineNumber(),
                err.getColumnNumber(), err).initCause( err );
        }
        catch ( ParserConfigurationException e )
        {
            throw new SAXException( e );
        }

        return rimsOrgUnits;

    }

    public List<RIMS_Mapping_DataElement> getRIMSDataElementsByDEGroup( String deGroupName ) throws SAXException, IOException, ParserConfigurationException
    {
        String deMappingXMLFileName = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
            + "di" + File.separator + "rims" + File.separator + "dataelementMapping.xml";

        List<RIMS_Mapping_DataElement> rimsDataElements = new ArrayList<RIMS_Mapping_DataElement>();

        try
        {
            String newpath = System.getenv( "USER_HOME" );
            if ( newpath != null )
            {
                deMappingXMLFileName = newpath + File.separator + "dhis" + File.separator + "ra" + File.separator
                    + "orgUnitGroupList.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            System.out.println( "DataElement Mapping XML File Not Found, Make sure the Path" );
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( deMappingXMLFileName ) );

            Element table = findTable( deGroupName, doc );
            NodeList listOfDataElements = table.getElementsByTagName( "dataelement" );

            int totalDataElements = listOfDataElements.getLength();

            for ( int s = 0; s < totalDataElements; s++ )
            {
                Node desNode = listOfDataElements.item( s );

                if ( desNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element deElement = (Element) desNode;

                    NodeList rimsDeList = deElement.getElementsByTagName( "rims" );
                    Element rimsDEElement = (Element) rimsDeList.item( 0 );
                    NodeList textRIMSDEList = rimsDEElement.getChildNodes();
                    String rimsDE = ((Node) textRIMSDEList.item( 0 )).getNodeValue().trim();

                    NodeList dhisDeList = deElement.getElementsByTagName( "dhis" );
                    Element dhisDEElement = (Element) dhisDeList.item( 0 );
                    NodeList textDHISDEList = dhisDEElement.getChildNodes();
                    String dhisDE = ((Node) textDHISDEList.item( 0 )).getNodeValue().trim();

                    String vCode = deElement.getAttribute( "vaccine_code" );
                    String antigen = deElement.getAttribute( "antigen" );
                    String deName = deElement.getAttribute( "name" );
                    String diseaseCode = deElement.getAttribute( "disease_code" );
                    String aefiCode = deElement.getAttribute( "aefi_code" );
                    String ageCode = deElement.getAttribute( "age_code" );
                    boolean isTotal = !deElement.getAttribute( "istotal" ).isEmpty();

                    RIMS_Mapping_DataElement rimsMappingDE = new RIMS_Mapping_DataElement(
                        table.getAttribute( "name" ), deName, dhisDE, rimsDE,
                        vCode, antigen, diseaseCode, aefiCode, ageCode );
                    rimsMappingDE.setTotal( isTotal );
                    rimsDataElements.add( rimsMappingDE );

                }// end of if clause
            }// end of for loop
        }// try block end
        catch ( SAXParseException err )
        {
            throw (SAXParseException) new SAXParseException( "** Parsing error" + ", line "
                + err.getLineNumber() + ", uri " + err.getSystemId(), 
                err.getPublicId(), err.getSystemId(), err.getLineNumber(),
                err.getColumnNumber(), err).initCause( err );
        }
        return rimsDataElements;
    }

    /**
     * @param tableName
     * @param doc
     * @return
     * @throws RuntimeException
     */
    private Element findTable( String tableName, Document doc )
        throws RuntimeException
    {
        Element desiredTable = null;
        NodeList tables = doc.getElementsByTagName( "table" );
        for ( int t = 0; t < tables.getLength(); t++ )
        {
            Node tableNode = tables.item( t );
            if ( tableNode instanceof Element )
            {
                Element table = (Element) tableNode;
                if ( table.getAttribute( "name" ).equalsIgnoreCase( tableName ) )
                {
                    desiredTable = table;
                }
            }
        }
        if ( desiredTable == null )
        {
            throw new RuntimeException( "Table " + tableName + " not found" );
        }
        return desiredTable;
    }

    public List<RIMS_Mapping_DataElement> getAllMappingDataElements() throws Exception
    {
        String deMappingXMLFileName = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
            + "di" + File.separator + "rims" + File.separator + "dataelementMapping.xml";

        List<RIMS_Mapping_DataElement> rimsDataElements = new ArrayList<RIMS_Mapping_DataElement>();

        try
        {
            String newpath = System.getenv( "USER_HOME" );
            if ( newpath != null )
            {
                deMappingXMLFileName = newpath + File.separator + "dhis" + File.separator + "di" + File.separator
                    + "rims" + File.separator + "dataelementMapping.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            System.out.println( "DataElement Mapping XML File Not Found, Make sure the Path" );
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( deMappingXMLFileName ) );

            // Iterate through tables
            NodeList tablesNL = doc.getElementsByTagName( "table" );
            for ( int t = 0; t < tablesNL.getLength(); t++ )
            {
                Element table = (Element) tablesNL.item( t );
                NodeList listOfDataElements = table.getElementsByTagName( "dataelement" );

                int totalDataElements = listOfDataElements.getLength();

                // Iterate through dataelements
                for ( int s = 0; s < totalDataElements; s++ )
                {
                    Node desNode = listOfDataElements.item( s );

                    if ( desNode.getNodeType() == Node.ELEMENT_NODE )
                    {
                        Element deElement = (Element) desNode;
                        
                        NodeList rimsDeList = deElement.getElementsByTagName( "rims" );
                        if ( rimsDeList.getLength() == 0 )
                        {
                            System.out.println( "Skipping data element "
                                + deElement.getAttribute( "name" ) + ": no RIMS mapping specified" );
                            continue;
                        }
                        Element rimsDEElement = (Element) rimsDeList.item( 0 );
                        NodeList textRIMSDEList = rimsDEElement.getChildNodes();
                        String rimsDE = ((Node) textRIMSDEList.item( 0 )).getNodeValue().trim();

                        NodeList dhisDeList = deElement.getElementsByTagName( "dhis" );
                        if ( dhisDeList.getLength() == 0 )
                        {
                            System.out.println( "Skipping data element "
                                + deElement.getAttribute( "name" ) + ": no DHIS mapping specified" );
                            continue;
                        }
                        Element dhisDEElement = (Element) dhisDeList.item( 0 );
                        NodeList textDHISDEList = dhisDEElement.getChildNodes();
                        String dhisDE = ((Node) textDHISDEList.item( 0 )).getNodeValue().trim();

                        String vCode = deElement.getAttribute( "vaccine_code" );
                        String antigen = deElement.getAttribute( "antigen" );
                        String diseaseCode = deElement.getAttribute( "disease_code" );
                        String aefiCode = deElement.getAttribute( "aefi_code" );
                        String deName = deElement.getAttribute( "name" );
                        String ageCode = deElement.getAttribute( "age_code" );
                        boolean isTotal = !deElement.getAttribute( "istotal" ).isEmpty();

                        if ( !deName.equalsIgnoreCase( "NO_SUCH_DE" ) )
                        {
                            RIMS_Mapping_DataElement mappingDE = new RIMS_Mapping_DataElement( table
                                .getAttribute( "name" ), deName, dhisDE, rimsDE, vCode, antigen, diseaseCode, 
                                aefiCode, ageCode );
                            mappingDE.setTotal( isTotal );
                            rimsDataElements.add( mappingDE );
                        }
                    }// if block end
                }
            }// end of for loop
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

        return rimsDataElements;
    }

    public List<RIMS_Mapping_Orgunit> getAllMappingOrgunits()
    {
        String ouMappingXMLFileName = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
            + "di" + File.separator + "rims" + File.separator + "orgUnitMapping.xml";

        List<RIMS_Mapping_Orgunit> rimsOrgUnits = new ArrayList<RIMS_Mapping_Orgunit>();

        try
        {
            String newpath = System.getenv( "USER_HOME" );
            if ( newpath != null )
            {
                ouMappingXMLFileName = newpath + File.separator + "dhis" + File.separator + "di" + File.separator
                    + "rims" + File.separator + "orgUnitMapping.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            System.out.println( "DataElement Mapping XML File Not Found, Make sure the Path" );
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( ouMappingXMLFileName ) );

            NodeList listOfOrgunits = doc.getElementsByTagName( "organisationunit" );

            int totalOrgUnits = listOfOrgunits.getLength();

            for ( int s = 0; s < totalOrgUnits; s++ )
            {
                Node ousNode = listOfOrgunits.item( s );

                if ( ousNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element ouElement = (Element) ousNode;

                    NodeList rimsOUList = ouElement.getElementsByTagName( "rims" );
                    Element rimsOUElement = (Element) rimsOUList.item( 0 );
                    NodeList textRIMSOUList = rimsOUElement.getChildNodes();
                    String rimsOU = ((Node) textRIMSOUList.item( 0 )).getNodeValue().trim();

                    NodeList dhisOUList = ouElement.getElementsByTagName( "dhis" );
                    Element dhisOUElement = (Element) dhisOUList.item( 0 );
                    NodeList textDHISOUList = dhisOUElement.getChildNodes();
                    String dhisOU = ((Node) textDHISOUList.item( 0 )).getNodeValue().trim();

                    String ouName = ouElement.getAttribute( "name" );

                    RIMS_Mapping_Orgunit mappingOU = new RIMS_Mapping_Orgunit( dhisOU, rimsOU, ouName );
                    rimsOrgUnits.add( mappingOU );
                }// if block end
            }// end of for loop
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

        return rimsOrgUnits;
    }
    
    /**
     * Creates a monthly dataset of all dataelements mentioned in the mapping files.
     * 
     * @param name the desired name of the dataset.
     * @return {@code null} if the dataset was successfully created, or a list
     *         of dataelements that were not found. (The dataset is created
     *         despite any elements not found.)
     * @throws Exception 
     */
    public List<RIMS_Mapping_DataElement> createDataset( String name ) throws Exception {
        // Find the dataelements
        List<RIMS_Mapping_DataElement> dataelements = getAllMappingDataElements();
        // Create a dataset
        DataSet dataset = new DataSet();
        Collection<DataElement> datasetelements = dataset.getDataElements();
        
        // Dataelements mentioned in the mapping file but not found in the database
        List<RIMS_Mapping_DataElement> notFound = new ArrayList<RIMS_Mapping_DataElement>();
        
        // ---------------------------------------------------------------------
        // Add all mapped dataelements to the dataset
        // ---------------------------------------------------------------------       
        for( RIMS_Mapping_DataElement mappingDE: dataelements )
        {
            DataElement de = deService.getDataElement( mappingDE.getDhisDataElementId() );
            if ( de == null )
            {
                // Dataelement not found
                notFound.add( mappingDE );
            }
            else
            {
                datasetelements.add( de );
            }
        }

        // Other properties
        dataset.setName( name );
        dataset.setPeriodType( periodService.getPeriodTypeByName( "Monthly" ) );
                
        // Store
        datasetService.addDataSet( dataset );
        // Inform of result
        if ( notFound.isEmpty() )
        {
            return null;
        }
        else
        {
            return notFound;
        }
    }

    public PeriodService getPeriodService()
    {
        return periodService;
    }

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
}
