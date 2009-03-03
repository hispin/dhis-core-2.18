package org.hisp.dhis.integration.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DataBaseConnection
{
    public static final String RIMS = "rims";

    String dbConnectionXMLFileName = System.getProperty( "user.home" )
        + File.separator + "dhis" + File.separator + "di" + File.separator
        + "DBConnections.xml";

    // -------------------------------------------------------------------------
    // To Open Connection for Specified connection Type, For ex:- RIMS,NACO etc
    // -------------------------------------------------------------------------
    public Connection openConnection( String type, String name )
        throws SQLException
    {
        Connection con = null;

        try
        {
            // To get From XML File
            ConnectionDetails details = getDBDetailsFromXML( type ).get( name );

            Class.forName( details.getDriver() ).newInstance();
            con = DriverManager.getConnection( details.getUrl(), details.getUsername(),
                details.getPassword() );
        }
        catch ( Exception e )
        {
            throw new SQLException( "Exception while opening connection: "
                + e.getMessage(), e );
        }

        return con;
    }// openConnection end

    // -------------------------------------------------------------------------
    // To retrieve the db details from xml file ie userName, password,
    // database URL, Driver
    // -------------------------------------------------------------------------
    public Map<String, ConnectionDetails> getDBDetailsFromXML( String connectionType )
    {
        Map<String, ConnectionDetails> connections = new HashMap<String, ConnectionDetails>();

        try
        {
            String newpath = System.getenv( "USER_HOME" );
            if ( newpath != null )
            {
                dbConnectionXMLFileName = newpath + File.separator + "dhis"
                    + File.separator + "ra" + File.separator
                    + "orgUnitGroupList.xml";
            }
        }
        catch ( NullPointerException npe )
        {
            System.out
                .println( "DBConnections XML File Not Found, Make sure the Path" );
        }

        try
        {
            // Init
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder
                .parse( new File( dbConnectionXMLFileName ) );

            // List of connections
            NodeList listOfDBConnections = doc.getElementsByTagName( connectionType );

            for ( int i = 0; i < listOfDBConnections.getLength(); i++)
            {
                Node dbConnectionsNode = listOfDBConnections.item( i );
                ConnectionDetails details = new ConnectionDetails();
                if ( dbConnectionsNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element dbConnectionElement = (Element) dbConnectionsNode;
                    details.setName( dbConnectionElement.getAttribute( "name" ) );
                    details.setId( dbConnectionElement.getAttribute( "id" ));
    
                    NodeList dbUserNameList = dbConnectionElement
                        .getElementsByTagName( "uname" );
                    Element dbUserNameElement = (Element) dbUserNameList.item( 0 );
                    NodeList textDBUNList = dbUserNameElement.getChildNodes();
                    details.setUsername( ((Node) textDBUNList.item( 0 )).getNodeValue()
                        .trim() );
    
                    NodeList dbUserPwdList = dbConnectionElement
                        .getElementsByTagName( "upwd" );
                    Element dbUserPwdElement = (Element) dbUserPwdList.item( 0 );
                    NodeList textDUPwdList = dbUserPwdElement.getChildNodes();
                    details.setPassword( ((Node) textDUPwdList.item( 0 )).getNodeValue()
                        .trim() );
    
                    NodeList dbURLList = dbConnectionElement
                        .getElementsByTagName( "dburl" );
                    Element dbURLElement = (Element) dbURLList.item( 0 );
                    NodeList textDBURLList = dbURLElement.getChildNodes();
                    details.setUrl( ((Node) textDBURLList.item( 0 )).getNodeValue()
                        .trim() );
    
                    NodeList dbDriverList = dbConnectionElement
                        .getElementsByTagName( "dbdriver" );
                    Element dbDriverElement = (Element) dbDriverList.item( 0 );
                    NodeList textDBDriverList = dbDriverElement.getChildNodes();
                    details.setDriver( ((Node) textDBDriverList.item( 0 )).getNodeValue()
                        .trim() );
                }// end of if clause
                connections.put( details.getId(), details );
            }
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line "
                + err.getLineNumber() + ", uri " + err.getSystemId() );
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

        return connections;
    }// getDBDeatilsFromXML Function end

    public static class ConnectionDetails
    {
        private String username, password, url, driver, name, id;
        
        public String getDatabaseName()
        {
            return url.substring( url.indexOf( "DBQ=" ) + 4 );
        }
    
        public String getDriver()
        {
            return driver;
        }
    
        public void setDriver( String driver )
        {
            this.driver = driver;
        }
    
        public String getPassword()
        {
            return password;
        }
    
        public void setPassword( String password )
        {
            this.password = password;
        }
    
        public String getUrl()
        {
            return url;
        }
    
        public void setUrl( String url )
        {
            this.url = url;
        }
    
        public String getUsername()
        {
            return username;
        }
    
        public void setUsername( String username )
        {
            this.username = username;
        }

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }

        public String getId()
        {
            return id;
        }

        public void setId( String id )
        {
            this.id = id;
        }
    }

}
