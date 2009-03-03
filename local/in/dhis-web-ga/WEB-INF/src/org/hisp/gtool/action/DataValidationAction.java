package org.hisp.gtool.action;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DataValidationAction {

	Connection con = (new DBConnection()).openConnection();
	String dataValidationXMLFileName = "C:\\Program Files\\DHIS2\\XMLFiles\\DataValidations.xml";
	
	/*
	 *  To retrieve all the data validations details
	 */
	public Hashtable getAllDataValidations()
    {
		Hashtable ht = new Hashtable();
		List li = null;
		String dataValidationID = "";
		
		try 
    	{
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(dataValidationXMLFileName));

            // normalize text representation            doc.getDocumentElement ().normalize ();
            //System.out.println ("Root element of the doc is " + 
             //    doc.getDocumentElement().getNodeName());


            NodeList listOfDataValidations = doc.getElementsByTagName("data-validation");
            int totalDataValidations = listOfDataValidations.getLength();
            System.out.println("File Name : "+dataValidationXMLFileName);
            System.out.println("Total no of Rules : " + totalDataValidations);

            for(int s=0; s<totalDataValidations ; s++)
            {
                Node dataValidationNode = listOfDataValidations.item(s);
                li = new ArrayList();
                if(dataValidationNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element dataValidationElement = (Element)dataValidationNode;
                    dataValidationID = dataValidationElement.getAttribute("id");
                    
                    NodeList dataValidationNameList = dataValidationElement.getElementsByTagName("name");
                    Element dataValidationNameElement = (Element)dataValidationNameList.item(0);

                    NodeList textDVNList = dataValidationNameElement.getChildNodes();
                    li.add(0,((Node)textDVNList.item(0)).getNodeValue().trim());
                    System.out.println("ID : "+dataValidationID);
                    System.out.println("Name : " + 
                           ((Node)textDVNList.item(0)).getNodeValue().trim());

                    NodeList dataValidationLeftDEIdsList = dataValidationElement.getElementsByTagName("left-deids");
                    Element dataValidationLeftDEIdsElement = (Element)dataValidationLeftDEIdsList.item(0);

                    NodeList textDVLdeIdsList = dataValidationLeftDEIdsElement.getChildNodes();
                    li.add(1,((Node)textDVLdeIdsList.item(0)).getNodeValue().trim());
                    String leftDENames = getDVDataElementList(((Node)textDVLdeIdsList.item(0)).getNodeValue().trim());
                    System.out.println("LeftDEIds : " + 
                           ((Node)textDVLdeIdsList.item(0)).getNodeValue().trim());

                    NodeList dataValidationRightDEIdsList = dataValidationElement.getElementsByTagName("right-deids");
                    Element dataValidationRightDEIdsElement = (Element)dataValidationRightDEIdsList.item(0);

                    NodeList textDVRdeIdsList = dataValidationRightDEIdsElement.getChildNodes();
                    li.add(2,((Node)textDVRdeIdsList.item(0)).getNodeValue().trim());
                    String rightDENames = getDVDataElementList(((Node)textDVRdeIdsList.item(0)).getNodeValue().trim());
                    System.out.println("RightDEIds : " + 
                           ((Node)textDVRdeIdsList.item(0)).getNodeValue().trim());

                    NodeList dataValidationOperatorList = dataValidationElement.getElementsByTagName("operator");
                    Element dataValidationOperatorElement = (Element)dataValidationOperatorList.item(0);

                    NodeList textDVOperatorList = dataValidationOperatorElement.getChildNodes();
                    li.add(3,((Node)textDVOperatorList.item(0)).getNodeValue().trim());
                    System.out.println("Operator : " + 
                           ((Node)textDVOperatorList.item(0)).getNodeValue().trim());                    
                
                    NodeList dataValidationTypeList = dataValidationElement.getElementsByTagName("type");
                    Element dataValidationTypeElement = (Element)dataValidationTypeList.item(0);

                    NodeList textDVTypeList = dataValidationTypeElement.getChildNodes();
                    li.add(4,((Node)textDVTypeList.item(0)).getNodeValue().trim());
                    System.out.println("Type : " + 
                           ((Node)textDVTypeList.item(0)).getNodeValue().trim());                    

                    NodeList dataValidationLeftDescList = dataValidationElement.getElementsByTagName("left-desc");
                    Element dataValidationLeftDescElement = (Element)dataValidationLeftDescList.item(0);

                    NodeList textDVLeftDescList = dataValidationLeftDescElement.getChildNodes();
                    li.add(5,((Node)textDVLeftDescList.item(0)).getNodeValue().trim());
                    System.out.println("LeftDescription : " + 
                           ((Node)textDVLeftDescList.item(0)).getNodeValue().trim());                    

                    NodeList dataValidationRightDescList = dataValidationElement.getElementsByTagName("right-desc");
                    Element dataValidationRightDescElement = (Element)dataValidationRightDescList.item(0);

                    NodeList textDVRightDescList = dataValidationRightDescElement.getChildNodes();
                    li.add(6,((Node)textDVRightDescList.item(0)).getNodeValue().trim());
                    System.out.println("RightDescription : " + 
                           ((Node)textDVRightDescList.item(0)).getNodeValue().trim());                    

                    li.add(7,leftDENames);
                    System.out.println("Left DataElements : " +leftDENames);    
                    li.add(8,rightDENames);
                    System.out.println("Right DataElements : " +rightDENames);
                    
                    NodeList dataValidationLeftPercentList = dataValidationElement.getElementsByTagName("left-percent");
                    Element dataValidationLeftPercentElement = (Element)dataValidationLeftPercentList.item(0);

                    NodeList textDVLeftPercentList = dataValidationLeftPercentElement.getChildNodes();
                    li.add(9,((Node)textDVLeftPercentList.item(0)).getNodeValue().trim());
                    System.out.println("LeftPercentage : " + 
                           ((Node)textDVLeftPercentList.item(0)).getNodeValue().trim());                    

                    NodeList dataValidationRightPercentList = dataValidationElement.getElementsByTagName("right-percent");
                    Element dataValidationRightPercentElement = (Element)dataValidationRightPercentList.item(0);

                    NodeList textDVRightPercentList = dataValidationRightPercentElement.getChildNodes();
                    li.add(10,((Node)textDVRightPercentList.item(0)).getNodeValue().trim());
                    System.out.println("RightPercentage : " + 
                           ((Node)textDVRightPercentList.item(0)).getNodeValue().trim());                    

                    ht.put(dataValidationID,li);
                                                                     		
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
    }//end of getAllDataValidations
			
	/*
	 * To get the value for leftside deids and rightside deids and finally the datavalidation status 
	 */
	public List getDataValidationStatus( int selOrgUnitID,
											String dvleftdeids,
											String dvrightdeids,
											String dvoperator,
											int periodID,
											String localLang,
											double leftPercent,
											double rightPercent)
	{
		
		String dvStatus="";
		int dvleftvalue = 0;
		int dvrightvalue = 0;
		
		List li = new ArrayList();
		
		Hashtable htForDVOperator = new Hashtable();
		htForDVOperator.put("Equal","=");
		htForDVOperator.put("Less","<");
		htForDVOperator.put("LessOrEqual","<=");
		htForDVOperator.put("Greater",">");
		htForDVOperator.put("GreaterOrEqual",">=");
				
		Statement st1 = null;
		Statement st2 = null;
		ResultSet rs1 = null;		
		ResultSet rs2 = null;
		
		String displayOpt = "dataelement.name";
		if(localLang == null) displayOpt = "dataelement.alternativeName";
				
		/*	
		    -- Aggregateddatavalue
		 	String query = "SELECT SUM(value) FROM aggregateddatavalue " +
							"WHERE  organisationUnitId = "+selOrgUnitID+" AND " +
									"periodId = "+periodID+" AND " +
									"dataElementId IN ("+dvleftdeids+")";
		*/
		
		/*
		   String query = "SELECT value FROM datavalue " +
							"WHERE  source = "+selOrgUnitID+" AND " +
									"period = "+periodID+" AND " +
									"dataElement IN ("+dvleftdeids+")";
        */
		/*
		String query = "SELECT dataelement.id,"+displayOpt+",datavalue.value " +
							"FROM datavalue INNER JOIN dataelement " +
											"ON datavalue.dataElement = dataelement.id " +
							"WHERE  source = "+selOrgUnitID+" AND period = "+periodID+" AND dataElement IN ("+dvleftdeids+")";
		*/
		String query = "SELECT dataelement.dataelementid,"+displayOpt+",datavalue.value " +
							"FROM datavalue INNER JOIN dataelement " +
							"ON datavalue.dataelementid = dataelement.dataelementid " +
							"WHERE  datavalue.sourceid = "+selOrgUnitID+" AND datavalue.periodid = "+periodID+" AND datavalue.dataelementid IN ("+dvleftdeids+")";
		
		String strLeftDEID = "";
		String strLeftDENames = "";
		String strLeftDEValues = "";
		
		String strRightDEID = "";
		String strRightDENames = "";
		String strRightDEValues = "";
		
		try
		{
			st1 = con.createStatement();
			st2 = con.createStatement();
			
			rs1 = st1.executeQuery(query);			
			while(rs1.next())	
			{
				strLeftDEID += rs1.getInt(1)+",";
				strLeftDENames += rs1.getString(2)+",";
				strLeftDEValues += rs1.getInt(3)+",";
				dvleftvalue += rs1.getInt(3);
			}
			
			double tempdvleftvalue = dvleftvalue + (dvleftvalue * leftPercent/100);  
			System.out.println("LeftValue: "+dvleftvalue);
			
			/*
			  -- Aggregateddatavalue
			  query = "SELECT SUM(value) FROM aggregateddatavalue " +
						"WHERE  organisationUnitId = "+selOrgUnitID+" AND " +
								"periodId = "+periodID+" AND " +
								"dataElementId IN ("+dvrightdeids+")"; 
			*/		
/*
			query = "SELECT SUM(value) FROM datavalue " +
						"WHERE  source = "+selOrgUnitID+" AND " +
								"period = "+periodID+" AND " +
								"dataElement IN ("+dvrightdeids+")"; 		
*/
			/*
			 	query = "SELECT dataelement.id,"+displayOpt+",datavalue.value " +
						"FROM datavalue INNER JOIN dataelement " +
											"ON datavalue.dataElement = dataelement.id " +
		                "WHERE  source = "+selOrgUnitID+" AND period = "+periodID+" AND dataElement IN ("+dvrightdeids+")";

			 */
			
			query = "SELECT dataelement.dataelementid,"+displayOpt+",datavalue.value " +
								"FROM datavalue INNER JOIN dataelement " +
								"ON datavalue.dataelementid = dataelement.dataelementid " +
								"WHERE  datavalue.sourceid = "+selOrgUnitID+" AND datavalue.periodid = "+periodID+" AND datavalue.dataelementid IN ("+dvrightdeids+")";
			
			rs2 = st2.executeQuery(query);
			while(rs2.next())	
			{
				strRightDEID += rs2.getInt(1)+",";
				strRightDENames += rs2.getString(2)+",";
				strRightDEValues += rs2.getInt(3)+",";
				dvrightvalue += rs2.getInt(3);
			}
			
			double tempdvrightvalue = dvrightvalue + (dvrightvalue * rightPercent/100);
			System.out.println("OUID: "+selOrgUnitID+"Period: "+periodID+"RV: "+dvrightvalue);
			
			if(getDVStatusValue(tempdvleftvalue,tempdvrightvalue,dvoperator)) dvStatus = "T";
			else dvStatus = "F";
			li.add(0,""+dvleftvalue);
			li.add(1,""+dvrightvalue);
			li.add(2,dvStatus);
			li.add(3,(String) htForDVOperator.get(dvoperator));
			li.add(4,strLeftDEID);
			li.add(5,strLeftDENames);
			li.add(6,strLeftDEValues);
			li.add(7,strRightDEID);
			li.add(8,strRightDENames);
			li.add(9,strRightDEValues);
			
		} // try block end
		catch(Exception e) 	{ System.out.println("Some Exception : "+e.getMessage()); return null;	}
		finally
		{
			try
			{
				if(rs1!=null) rs1.close();								
				if(rs2!=null) rs2.close();				
				
				if(st1!=null) st1.close();
				if(st2!=null) st2.close();
			}
			catch(Exception e){ System.out.println("Some Exception : "+e.getMessage()); return null;}
		}// finally block end
		
		return li;
	}//getDataValidationStatus end
	
	
	/*
	 * To retrieve the data validation status value 
	 */
	public boolean getDVStatusValue(double dvleftvalue,double dvrightvalue,String dvoperator)
	{				
		if( (dvoperator.equals("Equal")) && (dvleftvalue == dvrightvalue) )  return true;
		else if( (dvoperator.equals("Less")) && (dvleftvalue < dvrightvalue) )  return true;
		else if( (dvoperator.equals("LessOrEqual") || dvoperator.equals("Correlates")) && (dvleftvalue <= dvrightvalue) )  return true;
		else if( (dvoperator.equals("Greater")) && (dvleftvalue > dvrightvalue) )  return true;
		else if( (dvoperator.equals("GreaterOrEqual")) && (dvleftvalue >= dvrightvalue) )  return true;
		
		return false;
	}// getDVStatusValue end
	
	
	/*
	 * To get the names of Leftside DataElements and Rightside DataElements
	 */
	public String getDVDataElementList(String deIds)
	{
		Statement st1 = null;
		ResultSet rs1 = null;
		
		//String query ="SELECT shortName FROM dataelement WHERE id IN ("+deIds+")";
		String query ="SELECT name FROM dataelement WHERE dataelementid IN ("+deIds+")";
		
		String denames = "";
		try
		{
			st1 = con.createStatement();
			rs1 = st1.executeQuery(query);			
			while(rs1.next())
			{
				denames += rs1.getString(1)+",";
			}// while loop end		
		} // try block end
		catch(Exception e) 	{ System.out.println("Some Exception : "+e.getMessage()); return null;	}
		finally
		{
			try
			{
				if(rs1!=null) rs1.close();
				if(st1!=null) st1.close();						
			}
			catch(Exception e){ System.out.println("Some Exception : "+e.getMessage()); return null;}
		}// finally block end
	
		return denames;
	}//getDVDataElementList end

	/*
	 * To add new validation Rule
	 */
	public boolean addDataValidation(List li)
    {				
		String dvID = (String) li.get(0);
		String dvName = (String) li.get(1);
		String leftDesc = (String) li.get(2);
		String rightDesc = (String) li.get(3); 
		String dvOperator = (String) li.get(4); 
		String dvType = (String) li.get(5); 				
		String leftSelDEs = (String) li.get(6);
		String rightSelDEs = (String) li.get(7);
		String leftPercent = (String) li.get(8);
		String rightPercent = (String) li.get(9);
		
		try 
    	{			
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();            
            Document doc = docBuilderFactory.newDocumentBuilder().parse(new File(dataValidationXMLFileName));
                        						            
            Element dv_ele = doc.createElement("data-validation");
            Element name_ele = doc.createElement("name");
            Element leftdeids_ele = doc.createElement("left-deids");
            Element rightdeids_ele = doc.createElement("right-deids");
            Element ope_ele = doc.createElement("operator");
            Element type_ele = doc.createElement("type");
            Element leftdesc_ele = doc.createElement("left-desc");
            Element rightdesc_ele = doc.createElement("right-desc");
            Element leftPercent_ele = doc.createElement("left-percent");
            Element rightPercent_ele = doc.createElement("right-percent");
            
            Node name_txt = doc.createTextNode(dvName);
            Node leftdeids_txt = doc.createTextNode(leftSelDEs);
            Node rightdeids_txt = doc.createTextNode(rightSelDEs);
            Node ope_txt = doc.createTextNode(dvOperator);
            Node type_txt = doc.createTextNode(dvType);
            Node leftdesc_txt = doc.createTextNode(leftDesc);
            Node rightdesc_txt = doc.createTextNode(rightDesc);
            Node leftPercent_txt = doc.createTextNode(leftPercent);
            Node rightPercent_txt = doc.createTextNode(rightPercent);
            
            name_ele.appendChild(name_txt);
            leftdeids_ele.appendChild(leftdeids_txt);
            rightdeids_ele.appendChild(rightdeids_txt);
            ope_ele.appendChild(ope_txt);
            type_ele.appendChild(type_txt);
            leftdesc_ele.appendChild(leftdesc_txt);
            rightdesc_ele.appendChild(rightdesc_txt);
            leftPercent_ele.appendChild(leftPercent_txt);
            rightPercent_ele.appendChild(rightPercent_txt);
            
            dv_ele.appendChild(name_ele);
            dv_ele.appendChild(leftdeids_ele);
            dv_ele.appendChild(rightdeids_ele);
            dv_ele.appendChild(ope_ele);
            dv_ele.appendChild(type_ele);
            dv_ele.appendChild(leftdesc_ele);
            dv_ele.appendChild(rightdesc_ele);
            dv_ele.appendChild(leftPercent_ele);
            dv_ele.appendChild(rightPercent_ele);
            
            dv_ele.setAttribute("id",dvID);
                        
            Element root_ele = doc.getDocumentElement();
            root_ele.appendChild(dv_ele);            
            try 
            {
                Source source = new DOMSource(doc);
                File file = new File(dataValidationXMLFileName);
                Result result = new StreamResult(file);
                Transformer xformer = TransformerFactory.newInstance().newTransformer();
                xformer.transform(source, result);
            } 
            catch (TransformerConfigurationException e) { System.out.println(e.getMessage()); return false; } 
            catch (TransformerException e) { System.out.println(e.getMessage()); return false; }                       
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
        return true;
    }//end addDataValidation

	public boolean editDataValidation(List li)
	{
		boolean removeStatus = removeDataValidation((String) li.get(0));
		boolean addStatus = true;
		if(removeStatus) addStatus = addDataValidation(li);
		else return false;
		if(addStatus) return true;
		else return false;		
	}
	
	/*
	 * To edit validation Rule based on its id
	 */
	/*
	public boolean editDataValidation(List li)
    {				
		String dvID = (String) li.get(0);
		String dvName = (String) li.get(1);
		String leftDesc = (String) li.get(2);
		String rightDesc = (String) li.get(3); 
		String dvOperator = (String) li.get(4); 
		String dvType = (String) li.get(5); 				
		String leftSelDEs = (String) li.get(6);
		String rightSelDEs = (String) li.get(7);
		
		try 
    	{			
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();            
            Document doc = docBuilderFactory.newDocumentBuilder().parse(new File(dataValidationXMLFileName));
                        						            
            Element dv_ele = doc.createElement("data-validation");
            Element name_ele = doc.createElement("name");
            Element leftdeids_ele = doc.createElement("left-deids");
            Element rightdeids_ele = doc.createElement("right-deids");
            Element ope_ele = doc.createElement("operator");
            Element type_ele = doc.createElement("type");
            Element leftdesc_ele = doc.createElement("left-desc");
            Element rightdesc_ele = doc.createElement("right-desc");
            
            Node name_txt = doc.createTextNode(dvName);
            Node leftdeids_txt = doc.createTextNode(leftSelDEs);
            Node rightdeids_txt = doc.createTextNode(rightSelDEs);
            Node ope_txt = doc.createTextNode(dvOperator);
            Node type_txt = doc.createTextNode(dvType);
            Node leftdesc_txt = doc.createTextNode(leftDesc);
            Node rightdesc_txt = doc.createTextNode(rightDesc);
            
            name_ele.appendChild(name_txt);
            leftdeids_ele.appendChild(leftdeids_txt);
            rightdeids_ele.appendChild(rightdeids_txt);
            ope_ele.appendChild(ope_txt);
            type_ele.appendChild(type_txt);
            leftdesc_ele.appendChild(leftdesc_txt);
            rightdesc_ele.appendChild(rightdesc_txt);
            
            dv_ele.appendChild(name_ele);
            dv_ele.appendChild(leftdeids_ele);
            dv_ele.appendChild(rightdeids_ele);
            dv_ele.appendChild(ope_ele);
            dv_ele.appendChild(type_ele);
            dv_ele.appendChild(leftdesc_ele);
            dv_ele.appendChild(rightdesc_ele);
            
            dv_ele.setAttribute("id",dvID);
                        
            Element root_ele = doc.getDocumentElement();
            //root_ele.appendChild(dv_ele);
            
            NodeList nl=root_ele.getElementsByTagName("data-validation");
            for (int i=0;i<nl.getLength();i++)
            {
            	Element existing_dv =(Element) nl.item(i);
            	if (existing_dv.getAttribute("id").equals(dvID))
            		root_ele.replaceChild(dv_ele, existing_dv);            
            }// for end

            try 
            {
                Source source = new DOMSource(doc);
                File file = new File(dataValidationXMLFileName);
                Result result = new StreamResult(file);
                Transformer xformer = TransformerFactory.newInstance().newTransformer();
                xformer.transform(source, result);
            } 
            catch (TransformerConfigurationException e) { System.out.println(e.getMessage()); return false; } 
            catch (TransformerException e) { System.out.println(e.getMessage()); return false; }                       
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
        return true;
    }//end editDataValidation
*/
	
	/*
	 * To Remove the Validation based on validation id
	 */
	public boolean removeDataValidation(String dvID)
	{						
		int i=0;		
		try 
    	{			
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            Document doc = docBuilderFactory.newDocumentBuilder().parse(new File(dataValidationXMLFileName));            						
        
            Element root_ele = doc.getDocumentElement();
            NodeList nl = root_ele.getElementsByTagName("data-validation");
            for(i=0; i<nl.getLength();i++)
            {
            	Element dv_ele = (Element) nl.item(i);
            	if(dv_ele.getAttribute("id").equals(dvID))
            	{
            		root_ele.removeChild(dv_ele);
            		i=-1;
            		break;
            	}
            }
            if(i != -1) { System.out.println("Validation Not Found with that id"); return false;} 
            try 
            {
                // Prepare the DOM document for writing
                Source source = new DOMSource(doc);        
                // Prepare the output file
                File file = new File(dataValidationXMLFileName);
                Result result = new StreamResult(file);        
                // Write the DOM document to the file
                Transformer xformer = TransformerFactory.newInstance().newTransformer();
                xformer.transform(source, result);
            } 
            catch (TransformerConfigurationException e) { System.out.println(e.getMessage());  return false;} 
            catch (TransformerException e) {System.out.println(e.getMessage()); return false;}                                            
        }// try block end
    	catch (SAXParseException err) 
    	{
    		System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
    		System.out.println(" " + err.getMessage ());
    		return false;
        }
    	catch (SAXException e) 
    	{
    		Exception x = e.getException ();
    		((x == null) ? e : x).printStackTrace ();
    		return false;
        }
    	catch (Throwable t) 
    	{
    		t.printStackTrace ();
    		return false;
        }
    	System.out.println("Successfully Removed");
    	return true;
	} //removeDataValidation
	
	/*
	 * Returns the status
	 */
	public int correctDVDataValues(int oUID,int dEID, int pID, int dValue)
	{
		PreparedStatement ps = null;
				
		//String query ="UPDATE datavalue SET value=? WHERE source="+oUID+" AND dataElement="+dEID+" AND period="+pID;
		String query ="UPDATE datavalue SET value=? WHERE sourceid="+oUID+" AND dataelementid="+dEID+" AND periodid="+pID;
		int result = 1;
		try
		{
			ps = con.prepareStatement(query);
			ps.setInt(1,dValue);
			result = ps.executeUpdate();	
			System.out.println(oUID+"  "+dEID+"  "+pID+"  "+dValue+"  "+result);
		} // try block end
		catch(Exception e) 	{ System.out.println("Some Exception : "+e.getMessage()); return -1;	}
		finally
		{
			try
			{
				if(ps!=null) ps.close();						
			}
			catch(Exception e){ System.out.println("Some Exception : "+e.getMessage()); return -1;}
		}// finally block end
		return result;	
	}//correctDVDataValues
}// class end
