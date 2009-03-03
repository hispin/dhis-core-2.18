package org.hisp.gtool.action;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DBConnection 
{
	
	Connection con = null;
	String dbConnectionXMLFileName = "C:\\Program Files\\DHIS2\\XMLFiles\\DBConnections.xml";
	
	
	/*
	 *  To retrieve all db details
	 */
	/*
	public Hashtable getAllDBConnections()
    {
		Hashtable ht = new Hashtable();
		List li = null;
		String dbConnectionID = "";
		
		try 
    	{
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(dbConnectionXMLFileName));

            NodeList listOfDBConnections = doc.getElementsByTagName("db-connection");
            int totalDBConnections = listOfDBConnections.getLength();
            System.out.println("Total no of Connections : " + totalDBConnections);

            for(int s=0; s<totalDBConnections ; s++)
            {
                Node dbConnectionsNode = listOfDBConnections.item(s);
                li = new ArrayList();
                if(dbConnectionsNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element dbConnectionElement = (Element)dbConnectionsNode;
                    dbConnectionID = dbConnectionElement.getAttribute("id");
                    
                    NodeList dbUserNameList = dbConnectionElement.getElementsByTagName("uname");
                    Element dbUserNameElement = (Element)dbUserNameList.item(0);
                    NodeList textDBUNList = dbUserNameElement.getChildNodes();
                    li.add(0,((Node)textDBUNList.item(0)).getNodeValue().trim());                    
                    
                    NodeList dbUserPwdList = dbConnectionElement.getElementsByTagName("upwd");
                    Element dbUserPwdElement = (Element)dbUserPwdList.item(0);
                    NodeList textDUPwdList = dbUserPwdElement.getChildNodes();
                    li.add(1,((Node)textDUPwdList.item(0)).getNodeValue().trim());                   
                    
                    NodeList dbURLList = dbConnectionElement.getElementsByTagName("dburl");
                    Element dbURLElement = (Element)dbURLList.item(0);
                    NodeList textDBURLList = dbURLElement.getChildNodes();
                    li.add(2,((Node)textDBURLList.item(0)).getNodeValue().trim());
                                        
                    NodeList dbStateNameList = dbConnectionElement.getElementsByTagName("state-name");
                    Element dbStateNameElement = (Element)dbStateNameList.item(0);
                    NodeList textDBSNameList = dbStateNameElement.getChildNodes();
                    li.add(3,((Node)textDBSNameList.item(0)).getNodeValue().trim());
                    
                    System.out.println(li.get(0)+"   "+li.get(1)+"   "+li.get(2)+"   "+li.get(3));
                    ht.put(dbConnectionID,li);                                                                     	
                	li=null;
                }//end of if clause
            }//end of for loop with s var
        }// try block end
    	catch (SAXParseException err) 
    	{
    		System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
    		System.out.println(" " + err.getMessage ());
        }
    	catch (SAXException e) 
    	{
    		Exception x = e.getException ();
    		((x == null) ? e : x).printStackTrace ();
        }
    	catch (Throwable t) 
    	{
    		t.printStackTrace ();
        }
        
    	return ht;
    }//end of getAllDBConnections	
	*/
	
	/*
	 * To retrieve the db details
	 */
	public List getDBDeatils()
	{
		List li = null;
		
		try 
    	{
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(dbConnectionXMLFileName));

            NodeList listOfDBConnections = doc.getElementsByTagName("db-connection");
            
            Node dbConnectionsNode = listOfDBConnections.item(0);
            li = new ArrayList();
            if(dbConnectionsNode.getNodeType() == Node.ELEMENT_NODE)
            {
            	Element dbConnectionElement = (Element)dbConnectionsNode;                
                    
                NodeList dbUserNameList = dbConnectionElement.getElementsByTagName("uname");
                Element dbUserNameElement = (Element)dbUserNameList.item(0);
                NodeList textDBUNList = dbUserNameElement.getChildNodes();
                li.add(0,((Node)textDBUNList.item(0)).getNodeValue().trim());                    
                    
                NodeList dbUserPwdList = dbConnectionElement.getElementsByTagName("upwd");
                Element dbUserPwdElement = (Element)dbUserPwdList.item(0);
                NodeList textDUPwdList = dbUserPwdElement.getChildNodes();
                li.add(1,((Node)textDUPwdList.item(0)).getNodeValue().trim());                   
                    
                NodeList dbURLList = dbConnectionElement.getElementsByTagName("dburl");
                Element dbURLElement = (Element)dbURLList.item(0);
                NodeList textDBURLList = dbURLElement.getChildNodes();
                li.add(2,((Node)textDBURLList.item(0)).getNodeValue().trim());
                                        
                NodeList dbStateNameList = dbConnectionElement.getElementsByTagName("state-name");
                Element dbStateNameElement = (Element)dbStateNameList.item(0);
                NodeList textDBSNameList = dbStateNameElement.getChildNodes();
                li.add(3,((Node)textDBSNameList.item(0)).getNodeValue().trim());                                        
            }//end of if clause            
        }// try block end
    	catch (SAXParseException err) 
    	{
    		System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
    		System.out.println(" " + err.getMessage ());
        }
    	catch (SAXException e) 
    	{
    		Exception x = e.getException ();
    		((x == null) ? e : x).printStackTrace ();
        }
    	catch (Throwable t) 
    	{
    		t.printStackTrace ();
        }        
    	return li;		
	}
	
	public Connection openConnection()
	{
		
		try
		{
			//To get From XML File
		 
			List li = (ArrayList) getDBDeatils();
			
			String userName = (String) li.get(0);
			String userPass = (String) li.get(1);
			String urlForConnection = (String) li.get(2);
			
		/*	
			String userName = "dhis";
			String userPass = "";
			String urlForConnection = "jdbc:mysql://localhost/gj_dhis2";
		*/
			
			Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			con = DriverManager.getConnection (urlForConnection, userName, userPass);
		}
		catch(Exception e){ return null;}
		return con;
	} // openConnection end
	
	public void closeConnection()
	{
		try
		{		
		}
		finally
		{
		 try  {	 if(con!=null) con.close();		 }
		 catch(Exception e){ System.out.println(e.getMessage());} 	
		}
	} // closeConnection end
	

}
