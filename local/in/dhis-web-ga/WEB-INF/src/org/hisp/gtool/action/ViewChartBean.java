package org.hisp.gtool.action;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ViewChartBean {

	String indicatorList[];
	String orgUnitName;
	String s_Date;
	String e_Date;
	String i_category;
	String ide_type;
	
	Connection con = (new DBConnection()).openConnection();
	int periodType=1;
	int orgUnitID;
	List periodIDs;
	List indicatorIDs;
	
	String tempEDate="";
	int noOfPeriods;
	
	
		
/*	public Hashtable findChildOrgUnits()
	{
		Statement st = null;
		ResultSet rs = null;
		
		Hashtable ht = new Hashtable();
		
		try
		{
			String query = "select shortname,id from organisationunit where parent ="+orgUnitID;
			st = con.createStatement();
			rs = st.executeQuery(query);
						
			while(rs.next())
			{
				ht.put(rs.getString(1),new Integer(rs.getInt(2)));
			}// while loop end			
		} // try block end
		catch(Exception e)
		{
			System.out.println(e.getMessage()); return null;
		}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){System.out.println(e.getMessage());return null;}
		}// finally block end	
		
		return ht;
	} // findChildOrgUnits end
	
*/	
	
	public Hashtable setPeriodIDs()
	{
		Statement st = null;
		ResultSet rs = null;
		
		Statement st1 = null;
		ResultSet rs1 = null;
		
		String partsOfEDate[] = e_Date.split("-");
		tempEDate = partsOfEDate[0]+"-"+partsOfEDate[1]+"-01";
		
		Hashtable ht = new Hashtable();
					
		try
		{
			String query = "";
			if(i_category.equals("Period")) 
				/*
				query ="select startdate,id from period where startDate between '"+s_Date+"' and '"+tempEDate+"' and periodType="+periodType+" order by startdate";
				*/
				query ="SELECT startdate,periodid FROM period " +
							"WHERE startDate BETWEEN '"+s_Date+"' AND" +
									" '"+tempEDate+"' AND" +
									" periodtypeid="+periodType+" ORDER BY startdate";
			else if(i_category.equals("Facility"))
				/*
				query = "select shortname,id from organisationunit where parent ="+orgUnitID;
				*/
				query = "SELECT shortname,organisationunitid FROM organisationunit WHERE parentid ="+orgUnitID;						
			st = con.createStatement();
			st1 = con.createStatement();
			
			rs = st.executeQuery(query);
						
			while(rs.next())
			{							
				ht.put(rs.getString(1),new Integer(rs.getInt(2)));
			}// while loop end	
			
			
			if(i_category.equals("Period"))
			{
				noOfPeriods = 1;
			}				
			else if(i_category.equals("Facility"))
			{
				/*
				query = "select count(*) from period where startDate between '"+s_Date+"' and '"+tempEDate+"' and periodType="+periodType+" order by startdate";
				*/
				query = "SELECT COUNT(*) FROM period " +
							"WHERE startDate BETWEEN '"+s_Date+"' AND '"+tempEDate+"' AND" +
									" periodtypeid = "+periodType+" ORDER BY startdate";
				rs1 = st1.executeQuery(query);
				if(rs1.next()) noOfPeriods = rs1.getInt(1);
				else noOfPeriods = 1;							
			}									
		} // try block end
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ return null;}
		}// finally block end				
		return ht;							
	} // setPeriodID end
	
	
	
	public Hashtable setIndicatorIDs()
	{
									
		Statement st = null;
		ResultSet rs = null;				
		
		Hashtable ht = new Hashtable();
				
		int count=0;
		
		try
		{
			String query = "";
			st = con.createStatement();
			while(count<indicatorList.length)
			{
				if(ide_type.equals("indicatorsRadio")) 					
					//query = "select id,Target from indicator where name like '"+indicatorList[count]+"'";
					query = "SELECT indicatorid,Target FROM indicator WHERE name LIKE '"+indicatorList[count]+"'";
				else 
					//query = "select id from dataelement where shortname like '"+indicatorList[count]+"'";
					query = "SELECT dataelementid FROM dataelement WHERE alternativeName LIKE '"+indicatorList[count]+"'";
				rs = st.executeQuery(query);
				
				if(rs.next()) 	{ ht.put(indicatorList[count],new Integer(rs.getInt(1)));}
				count++;
			}// while loop end	
		} // try block end
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ return null;}
		}// finally block end
		return ht;
	} // setPeriodID end
	
	
	// Target Values
	public Hashtable getTargetValues()
	{
									
		Statement st = null;
		ResultSet rs = null;				
		
		Hashtable ht = new Hashtable();
				
		int count=0;
		
		try
		{
			st = con.createStatement();
			while(count<indicatorList.length)
			{
				String query = "select Target from indicator where name like '"+indicatorList[count]+"'";
								
				rs = st.executeQuery(query);
				
				if(rs.next()) 	{ ht.put(indicatorList[count],new Double(rs.getDouble(1)));}
				else { ht.put(indicatorList[count],new Double(0.0));} 
				count++;
			}// while loop end	
		} // try block end
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ return null;}
		}// finally block end
		return ht;
	} // getTargetValues end
	
	
	// Indicator Numerator Formula
	public Hashtable getIndNumeratorFormula()
	{
									
		Statement st = null;
		ResultSet rs = null;				
		
		Hashtable ht = new Hashtable();
				
		int count=0;
		
		try
		{
			st = con.createStatement();
			while(count<indicatorList.length)
			{
				//String query = "select numeratorDescription from indicator where name like '"+indicatorList[count]+"'";
				String query = "SELECT numeratordescription FROM indicator WHERE name LIKE '"+indicatorList[count]+"'";				
				rs = st.executeQuery(query);
				
				if(rs.next()) 	{ ht.put(indicatorList[count],rs.getString(1));}
				else { ht.put(indicatorList[count]," ");}
								
				count++;
			}// while loop end	
		} // try block end
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ return null;}
		}// finally block end
		return ht;
	} // getIndNumeratorFormula end
	
	
	
	// Indicator Denominator Formula
	public Hashtable getIndDenominatorFormula()
	{
									
		Statement st = null;
		ResultSet rs = null;				
		
		Hashtable ht = new Hashtable();
				
		int count=0;
		
		try
		{
			st = con.createStatement();
			while(count<indicatorList.length)
			{
				//String query = "select denominatorDescription from indicator where name like '"+indicatorList[count]+"'";
				String query = "SELECT denominatordescription FROM indicator WHERE name LIKE '"+indicatorList[count]+"'";
				
				rs = st.executeQuery(query);
				
				if(rs.next()) 	{ ht.put(indicatorList[count],rs.getString(1));}
				else { ht.put(indicatorList[count],"");} 
				count++;
			}// while loop end	
		} // try block end
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ return null;}
		}// finally block end
		return ht;
	} // getIndDenominatorFormula end


	//	 Indicator Denominator DataElements
	public Hashtable getIndDenominatorDEs()
	{
									
		Statement st = null;
		ResultSet rs = null;				
		
		Hashtable ht = new Hashtable();
		
		int count=0;
		
		try
		{
			st = con.createStatement();
			while(count<indicatorList.length)
			{
				String deNames = "";
				String query = "select denominator from indicator where name like '"+indicatorList[count]+"'";								
				rs = st.executeQuery(query);				
				if(rs.next()) 	
					{ 
						deNames = getDEsofNandD(rs.getString(1));
						ht.put(indicatorList[count],deNames);
					}// if end
				else { ht.put(indicatorList[count],"");}
				 
				count++;
			}// while loop end	
		} // try block end
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ return null;}
		}// finally block end
		return ht;
	} // getIndDenominatorDEs end
	
	
//	 Indicator Numerator DataElements
	public Hashtable getIndNumeratorDEs()
	{
									
		Statement st = null;
		ResultSet rs = null;				
		
		Hashtable ht = new Hashtable();
		
		int count=0;
		
		try
		{
			st = con.createStatement();
			while(count<indicatorList.length)
			{
				String deNames = "";
				String query = "select numerator from indicator where name like '"+indicatorList[count]+"'";								
				rs = st.executeQuery(query);				
				if(rs.next()) 	
					{ 
						deNames = getDEsofNandD(rs.getString(1));
						ht.put(indicatorList[count],deNames);
					}// if end
				else { ht.put(indicatorList[count],"");}
				 
				count++;
			}// while loop end	
		} // try block end
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ return null;}
		}// finally block end
		return ht;
	} // getIndNumeratorDEs end
	
	// get DataelementList of Numerator or Denominator 
	public String getDEsofNandD(String tempSD)
	{
		Statement st = null;
		ResultSet rs = null;	
		
		char[] tempCD = tempSD.toCharArray();
		String deNames="";
		String temp1="";
		int flag=0;
		try
		{
			st = con.createStatement();
			
			for(int i=0;i<tempCD.length;i++)
			{							   
				if(tempCD[i]=='[') { flag=1;temp1 = ""; }
				else if(tempCD[i]==']') 
				{
					flag=2;
					int itemp = Integer.parseInt(temp1);
					//String query = "select alternativeName from dataelement where id="+itemp;
					String query = "select alternativeName from dataelement where dataelementid="+itemp;
					rs = st.executeQuery(query);					
					if(rs.next())	{	deNames+=rs.getString(1)+", ";	}
				}
				else 	if(flag==1) temp1 += tempCD[i];
			}// for end		
		} // try block end
		catch(Exception e)	{	return null; }
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ return null;}
		}// finally block end
		
		return deNames;
	}// end function getDEsofNandD
	
	//	 Indicator Factor
	public Hashtable getIndFactor()
	{
									
		Statement st = null;
		ResultSet rs = null;				
		
		Hashtable ht = new Hashtable();
				
		int count=0;
		
		try
		{
			st = con.createStatement();
			while(count<indicatorList.length)
			{
				//String query = "select indicatortype.factor from indicatortype inner join indicator on indicatortype.id = indicator.indicatorType where indicator.name like '"+indicatorList[count]+"'";
				String query = "SELECT indicatortype.indicatorfactor FROM indicatortype " +
									"INNER JOIN indicator ON indicatortype.indicatortypeid = indicator.indicatortypeid " +
									"WHERE indicator.name LIKE '"+indicatorList[count]+"'";				
				rs = st.executeQuery(query);
				
				if(rs.next()) 	{ ht.put(indicatorList[count],rs.getString(1));}
				else { ht.put(indicatorList[count],"");} 
				count++;
			}// while loop end	
		} // try block end
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ return null;}
		}// finally block end
		return ht;
	} // getIndFactor end
	
	
	
	public Hashtable getValuesByPeriod()
	{
		Statement st1 = null;
		ResultSet rs1 = null;
		
		Statement st2 = null;
		ResultSet rs2 = null;
		
		//int orgUnitLevel = 4;
					
		Hashtable ht = new Hashtable();
					
		try
		{			
			st1 = con.createStatement();
			st2 = con.createStatement();
			
			//rs2 =  st2.executeQuery("select level from organisationunitstructure where source = "+orgUnitID);
			//if(rs2.next()) orgUnitLevel = rs2.getInt(1);
			
			//if(orgUnitLevel==1)
			
			Hashtable htForIndicator = setIndicatorIDs();
			Enumeration keysForIndicator = htForIndicator.keys();
			
			while(keysForIndicator.hasMoreElements())
			{
				String keyI = (String) keysForIndicator.nextElement();
				int iID = ((Integer)htForIndicator.get(keyI)).intValue();
				
				List liForValues = new ArrayList();
				Hashtable htForPeriods = setPeriodIDs();
				//Enumeration keysForPeriod = htForPeriods.keys();
				
				Vector vForPeriods = new Vector(htForPeriods.keySet());
				Collections.sort(vForPeriods);
			    Iterator iteratorForPeriod = vForPeriods.iterator();
				while(iteratorForPeriod.hasNext())
				{				
					String keyP = (String) iteratorForPeriod.next();;
					int pID = ((Integer)htForPeriods.get(keyP)).intValue();
					String query = "";
					if(i_category.equals("Period")) 
					{
						if(ide_type.equals("indicatorsRadio")) 
							//query ="select sum(value) from aggregatedindicatorvalue where periodTypeId ="+periodType+" and organisationUnitId in (select id from organisationunit where id ="+orgUnitID+") and indicatorID="+iID+" and periodID="+pID;
							query ="SELECT SUM(value) FROM aggregatedindicatorvalue " +
										"WHERE periodtypeid ="+periodType+" AND " +
												"organisationunitid ="+orgUnitID+" AND " +
												"indicatorid="+iID+" AND " +
												"periodid="+pID;
						else	
							//query ="select value from aggregateddatavalue where periodTypeId ="+periodType+" and organisationUnitId="+orgUnitID+" and dataElementId="+iID+" and periodID="+pID;
							query ="SELECT value FROM aggregateddatavalue " +
										"WHERE periodtypeid ="+periodType+" AND " +
												"organisationunitid="+orgUnitID+" AND " +
												"dataelementid="+iID+" AND " +
												"periodid="+pID;
					}	
					else if(i_category.equals("Facility"))
					{											
						if(ide_type.equals("indicatorsRadio")) 
							//query = "select sum(value) from aggregatedindicatorvalue where periodTypeId ="+periodType+" and organisationUnitId in (select id from organisationunit where id ="+pID+") and indicatorID="+iID+" and periodID in (select id from period where startdate between '"+s_Date+"' and '"+tempEDate+"' and periodtype="+periodType+")";
							query = "SELECT sum(value) FROM aggregatedindicatorvalue " +
										"WHERE periodtypeid ="+periodType+" AND " +
												"organisationunitid ="+pID+" AND " +
												"indicatorid="+iID+" AND " +
												"periodid IN (SELECT periodid FROM period WHERE startdate BETWEEN '"+s_Date+"' AND '"+tempEDate+"' AND periodtypeid="+periodType+")";
						else 
						{
							//query = "select sum(value) from aggregateddatavalue where periodTypeId ="+periodType+" and organisationUnitId="+pID+" and dataElementId="+iID+" and periodID in (select id from period where startdate between '"+s_Date+"' and '"+tempEDate+"' and periodtype="+periodType+")";
							query = "SELECT SUM(value) FROM aggregateddatavalue " +
										"WHERE periodtypeid ="+periodType+" AND " +
												"organisationunitid="+pID+" AND " +
												"dataelementid="+iID+" AND " +
												"periodid IN (SELECT periodid FROM period WHERE startdate BETWEEN '"+s_Date+"' AND '"+tempEDate+"' AND periodtypeid="+periodType+")";
							noOfPeriods=1;
						}
					}	
					rs1 = st1.executeQuery(query);					
					if(rs1.next()) liForValues.add(new Double( (Math.round(rs1.getDouble(1)*Math.pow(10,2))/Math.pow(10,2))/noOfPeriods));
					else liForValues.add(new Double(0));																									
				}// period while loop end
				ht.put(keyI,liForValues);											
				
			}// indicator while loop end
		} // try block end
		catch(Exception e)
		{
			System.out.println(e.getMessage());return null;
		}
		finally
		{
			try
			{
				if(rs1!=null) rs1.close();
				if(st1!=null) st1.close();
				
				if(rs2!=null) rs2.close();
				if(st2!=null) st2.close();
			}
			catch(Exception e){System.out.println(e.getMessage());return null;}
		}// finally block end		
		
		return ht;
		
		
	}// getValues end

	

	public String getI_category() {
		return i_category;
	}


	public void setI_category(String i_category) {
		this.i_category = i_category;
	}


	public Connection getCon() {
		return con;
	}


	public void setCon(Connection con) {
		this.con = con;
	}


	public List getIndicatorIDs() {
		return indicatorIDs;
	}


	public void setIndicatorIDs(List indicatorIDs) {
		this.indicatorIDs = indicatorIDs;
	}


	public String[] getIndicatorList() {
		return indicatorList;
	}


	public void setIndicatorList(String[] indicatorList) {
		this.indicatorList = indicatorList;
	}


	public int getOrgUnitID() {
		return orgUnitID;
	}


	public void setOrgUnitID(int orgUnitID) {
		this.orgUnitID = orgUnitID;
	}


	public String getOrgUnitName() {
		return orgUnitName;
	}


	public void setOrgUnitName(String orgUnitName) {
		this.orgUnitName = orgUnitName;
	}


	public List getPeriodIDs() {
		return periodIDs;
	}


	public void setPeriodIDs(List periodIDs) {
		this.periodIDs = periodIDs;
	}


	public int getPeriodType() {
		return periodType;
	}


	public void setPeriodType(int periodType) {
		this.periodType = periodType;
	}


	public String getE_Date() {
		return e_Date;
	}


	public void setE_Date(String date) {
		e_Date = date;
	}


	public String getS_Date() {
		return s_Date;
	}


	public void setS_Date(String date) {
		s_Date = date;
	}



	public int getNoOfPeriods() {
		return noOfPeriods;
	}



	public void setNoOfPeriods(int noOfPeriods) {
		this.noOfPeriods = noOfPeriods;
	}

	public String getTempEDate() {
		return tempEDate;
	}



	public void setTempEDate(String tempEDate) {
		this.tempEDate = tempEDate;
	}



	public String getIde_type() {
		return ide_type;
	}



	public void setIde_type(String ide_type) {
		this.ide_type = ide_type;
	}
}
