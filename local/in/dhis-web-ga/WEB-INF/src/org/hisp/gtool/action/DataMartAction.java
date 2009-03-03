package org.hisp.gtool.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataMartAction {
		
	Connection con = (new DBConnection()).openConnection();
			
	/*
	 * Returns the Period Ids for the periods between startdate and enddate
	 */
	public List getPeriodIDsList(String startDate,String endDate)
	{
		Statement st = null;
		ResultSet rs = null;
		
		/*
		String query = "SELECT id FROM period " +
							"WHERE startdate BETWEEN '"+startDate+"' AND '"+endDate+"'";		
		*/
		String query = "SELECT periodid FROM period " +
							"WHERE startdate BETWEEN '"+startDate+"' AND '"+endDate+"'";
		
		List periodIDsList = new ArrayList();
		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);			
			while(rs.next())
				{	
				periodIDsList.add(new Integer(rs.getInt(1))); 
				}							
		} // try block end
		catch(Exception e) 	{	return null;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){return null;}
		}// finally block end		
		return periodIDsList;
	}// getPeriodIDsList end
	
	
	/*
	 * Returns the Indicator Ids 
	 */
	public List getIndicatorIDsList(String ide_type,String[] indicatorNamesList)
	{
		
		//Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
				
		String query = "";
		
		if(ide_type.equals("indicatorsRadio"))
		{
			/*
			query = "SELECT id FROM indicator "+			
							"WHERE name LIKE ?";
			*/
			query = "SELECT indicatorid FROM indicator "+			
							"WHERE name LIKE ?";
		}
		else
		{
			/*
			query = "SELECT id FROM dataelement " +
							"WHERE shortname LIKE ?";
			*/
			query = "SELECT dataelementid FROM dataelement " +
							"WHERE alternativeName LIKE ?";
		}
								
		List indicatorIDsList = new ArrayList();
		try
		{
			//st = con.createStatement();
			ps = con.prepareStatement(query);
			
			System.out.println(indicatorNamesList.length);
			int count1 = 0;
			while(count1 < indicatorNamesList.length)
			{	
				//rs = st.executeQuery(query);
				
				ps.setString(1,indicatorNamesList[count1]);
				rs = ps.executeQuery();				
				if(rs.next())	indicatorIDsList.add(new Integer(rs.getInt(1)));					
				else indicatorIDsList.add(new Integer(0));
				count1++;
			} // while loop end							
		} // try block end
		catch(Exception e) 	{	return null;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				//if(st!=null) st.close();
				if(ps!=null) ps.close();
			}
			catch(Exception e){return null;}
		}// finally block end		
		return indicatorIDsList;
	}// getIndicatorIDsList end
	
	
	/*
	 * Returns the level number of selected orgunit
	 */
	public int getOrgUnitLevel(int orgUnitID)
	{
		Statement st = null;
		ResultSet rs = null;
		
		int orgUnitLevel = 0;
		/*
		String query = "SELECT level FROM organisationunitstructure " +
								"WHERE organisationUnitId = "+orgUnitID;
		*/
		String query = "SELECT level FROM orgunitstructure " +
								"WHERE organisationunitid = "+orgUnitID;
		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);			
			if(rs.next())	{	orgUnitLevel = rs.getInt(1); }							
		} // try block end
		catch(Exception e) 	{	return 0;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){return 0;}
		}// finally block end		
		return orgUnitLevel;		
	} // getOrgUnitLevel end
	
	
	/*
	 * Returns the Maximum level number of selected orgunit
	 */
	public int getMaxOrgUnitLevel()
	{
		Statement st = null;
		ResultSet rs = null;
		
		int maxOrgUnitLevel = 0;
		
		//String query = "SELECT MAX(level) FROM organisationunitstructure";
		String query = "SELECT MAX(level) FROM orgunitstructure";
		
		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);			
			if(rs.next())	{	maxOrgUnitLevel = rs.getInt(1); }							
		} // try block end
		catch(Exception e) 	{	return 0;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){return 0;}
		}// finally block end		
		return maxOrgUnitLevel;		
	} // getMaxOrgUnitLevel end
	
	
	
	/*
	 * Returns the maximum id in the aggregatedindicatorvalue OR aggregateddatavalue table
	 */
	public int getMaxIDofAggIndValue(String ide_type)
	{
		Statement st = null;
		ResultSet rs = null;
		
		int maXAggIndValueID = 0;
		String query = "";
		if(ide_type.equals("indicatorsRadio"))
		{
			query = "SELECT MAX(id) FROM aggregatedindicatorvalue";
		}
		else
		{
			query = "SELECT MAX(id) FROM aggregateddatavalue";
		}
				
		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);			
			if(rs.next())	{	maXAggIndValueID = rs.getInt(1); }							
		} // try block end
		catch(Exception e) 	{	return 0;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ return 0;}
		}// finally block end		
		return maXAggIndValueID;		
	} // getMaxIDofAggIndValue end
	
	
	/*
	 * This returns the list of organisationunit ids as an array list 
	 * 	which are at input level 
	 */
	public List getOUListForLevel(int selOULevel)
	{
		Statement st = null;
		ResultSet rs = null;
		
		List li = new ArrayList();
		/*
		String query = "SELECT organisationUnitId FROM organisationunitstructure " +
							"WHERE level = "+selOULevel;
		*/
		
		String query = "SELECT organisationunitid FROM orgunitstructure " +
							"WHERE level = "+selOULevel;

		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);			
			while(rs.next())
			{	
				li.add(new Integer(rs.getInt(1))); 
			}							
		} // try block end
		catch(Exception e) 	{	return null;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ return null;}
		}// finally block end		
		return li;
	} // getOUListForLevel end
	
	
	public int aggregateIndicatorValues(
						int selOUID, 
						String startDate, 
						String endDate,
						String ide_type,
						String[] indNamesList)
	{
		
		int selOULevel = getOrgUnitLevel(selOUID);
		int maxOULevel = getMaxOrgUnitLevel();
		//int aggIndValueID = getMaxIDofAggIndValue(ide_type) + 1;
		int periodTypeID = 1;
		
		List ouList = (ArrayList) getOUListForLevel(selOULevel);
		List periodIDsList = (ArrayList) getPeriodIDsList(startDate, endDate);
		List indicatorIDsList = (ArrayList) getIndicatorIDsList(ide_type,indNamesList);
		
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		
		if(ouList == null) {System.out.println("oulist is empty");}
		if(periodIDsList == null) {System.out.println("periodlist is empty");}
		if(indicatorIDsList == null) {System.out.println("indlist is empty");}
		
		int totRecordCount = 0;
		String query = "";
		try
		{
			
			if(ide_type.equals("indicatorsRadio"))
			{
				/*
				query = "SELECT id FROM aggregatedindicatorvalue " +
							"WHERE indicatorId = ? AND " +
								"periodId= ? AND " +
								"organisationUnitId  = ?";
				*/
				query = "SELECT value FROM aggregatedindicatorvalue " +
							"WHERE indicatorid = ? AND " +
									"periodid= ? AND " +
									"organisationunitid  = ?";

			}
			else
			{
				/*
				query = "SELECT id FROM aggregateddatavalue " +
							"WHERE dataElementId = ? AND " +
									"periodId= ? AND " +
									"organisationUnitId  = ?";
				*/					
				query = "SELECT value FROM aggregateddatavalue " +
							"WHERE dataelementid = ? AND " +
									"periodid= ? AND " +
									"organisationunitid  = ?";		
			}
			ps1 = con.prepareStatement(query);
			
			
			if(ide_type.equals("indicatorsRadio"))
			{
				/*
				query = "SELECT sum(value),count(*) FROM aggregatedindicatorvalue " +
							"WHERE indicatorId=? AND " +
									"periodId=? AND " +
									"organisationUnitId IN (SELECT id FROM organisationunit WHERE parent = ?)";
				*/
				query = "SELECT sum(value),count(*) FROM aggregatedindicatorvalue " +
							"WHERE indicatorid=? AND " +
									"periodid=? AND " +
									"organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = ?)";
			}
			else
			{
				if(selOULevel == maxOULevel)
				{
					/*
					query = "SELECT SUM(value),count(*) FROM datavalue " +
								"WHERE dataElement=? AND " +
										"period=? AND " +
										"source = ?";
					*/					
					query = "SELECT SUM(value),count(*) FROM datavalue " +
								"WHERE dataelementid=? AND " +
										"periodid=? AND " +
										"sourceid = ?";					
				}
				else
				{
					/*
					query = "SELECT sum(value),count(*) FROM aggregateddatavalue " +
								"WHERE dataElementId=? AND " +
										"periodId=? AND " +
										"organisationUnitId IN (SELECT id FROM organisationunit WHERE parent = ?)";
					*/
					query = "SELECT sum(value),count(*) FROM aggregateddatavalue " +
								"WHERE dataelementid=? AND " +
										"periodid=? AND " +
										"organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = ?)";
				}	
			}
			ps2 = con.prepareStatement(query);
			
			if(ide_type.equals("indicatorsRadio"))
			{
				/*
				query = "INSERT IGNORE INTO aggregatedindicatorvalue " +
							"(indicatorId,periodId,periodTypeId,organisationUnitId,level,value) " +
							"values(?,?,?,?,?,?)";
				*/
				query = "INSERT IGNORE INTO aggregatedindicatorvalue " +
							"(indicatorid,periodid,periodtypeid,organisationunitid,level,value) " +
							"VALUES(?,?,?,?,?,?)";
			}
			else
			{
				/*
				query = "INSERT IGNORE INTO aggregateddatavalue " +
							"(dataElementId,periodId,periodTypeId,organisationUnitId,level,value) " +
							"values(?,?,?,?,?,?)";
				*/
				query = "INSERT IGNORE INTO aggregateddatavalue " +
							"(dataelementid,periodid,periodtypeid,organisationunitid,level,value) " +
							"VALUES(?,?,?,?,?,?)";
			}			
			ps3 = con.prepareStatement(query);
			
			
			Iterator itForOUList = ouList.iterator();
			while(itForOUList.hasNext())
			{
				int ouId = ((Integer)itForOUList.next()).intValue(); 
				Iterator itForIndList = indicatorIDsList.iterator();
				while(itForIndList.hasNext())
				{
					int indId = ((Integer)itForIndList.next()).intValue();
					Iterator itForPeriodList = periodIDsList.iterator();
					while(itForPeriodList.hasNext())
					{
						int periodId = ((Integer)itForPeriodList.next()).intValue(); 
						
						ps1.setInt(1,indId);
						ps1.setInt(2,periodId);
						ps1.setInt(3,ouId);
						rs1 = ps1.executeQuery();
						if(rs1.next()) {}
						else
						{
							ps2.setInt(1,indId);
							ps2.setInt(2,periodId);
							ps2.setInt(3,ouId);							
							rs2 = ps2.executeQuery();
							if(rs2.next())
							{
								double aggIndValue = rs2.getDouble(1);
								int childCount = rs2.getInt(2);
								if(childCount != 0 && ide_type.equals("indicatorsRadio")) aggIndValue /= childCount;
								if(aggIndValue !=0 )
								{
									System.out.println(indId+"   "+periodId+"   "+periodTypeID+"   "+ouId+"   "+selOULevel+"   "+aggIndValue);
																		
									ps3.setInt(1,indId);
									ps3.setInt(2,periodId);
									ps3.setInt(3,periodTypeID);
									ps3.setInt(4,ouId);
									ps3.setInt(5,selOULevel);
									ps3.setDouble(6,aggIndValue);

									ps3.executeUpdate();
									
									//aggIndValueID++;
									totRecordCount++;
								}
							} // rs2 if end
						}// rs1 else end
					}// Perios List While loop end
				}// Indcator Names List while loop end
			}// ouList while loop end
		}// try block end
		catch(Exception e) { System.out.println(e.getMessage()); return -1;}
		finally
		{
			try
			{
				if(ps1 != null) ps1.close();
				if(ps2 != null) ps2.close();
				if(ps3 != null) ps3.close();
				
				if(rs1 != null) rs1.close();
				if(rs2 != null) rs2.close();
			}
			catch(Exception e){	System.out.println(e.getMessage());	return -1;}
		}		
		
		return totRecordCount;
	}// aggregateIndicatorValues end


	/*
	 * setters and getters
	 */
	
} // class end
