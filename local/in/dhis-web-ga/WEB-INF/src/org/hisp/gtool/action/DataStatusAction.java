package org.hisp.gtool.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class DataStatusAction {

	Connection con = (new DBConnection()).openConnection();
	
	/* 
	 * To retrieve all orgunit names 
	 * */
	public  Hashtable getAllOrgUnitNames()
	{
		Statement st = null;
		ResultSet rs = null;
		
		//String query = "select id,name from organisationunit";
		String query = "SELECT organisationunitid,name FROM organisationunit";
		Hashtable ht = new Hashtable();
		
		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);
			
			while(rs.next())
			{
				ht.put(""+rs.getInt(1),rs.getString(2));				
			}// while loop end
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
		
		return ht;
	}// getAllOrgUnitNames end
	
	
	/* 
	 * To retrieve slected organisationunit Details - name,level 
	 * */
	public  List getSelectedOUDetails(int orgUnitID)
	{
		Statement st = null;
		ResultSet rs = null;
		
		/*
		String query = "SELECT organisationunit.shortname,organisationunitstructure.level " +
							"FROM organisationunit INNER JOIN organisationunitstructure " +
									"ON organisationunit.id = organisationunitstructure.organisationUnitId " +
                            "WHERE organisationunit.id = "+orgUnitID;
		*/
		String query = "SELECT organisationunit.shortname,orgunitstructure.level " +
							"FROM organisationunit INNER JOIN orgunitstructure " +
									"ON organisationunit.organisationunitid = orgunitstructure.organisationunitid " +
                            "WHERE organisationunit.organisationunitid = "+orgUnitID;

		List li = new ArrayList();
		
		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);
			
			if(rs.next())
			{
				li.add(0,rs.getString(1));
				li.add(1,new Integer(rs.getInt(2)));								
			}// if block end
		} // try block end
		catch(Exception e) 	{	System.out.println(e.getMessage()); return null;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){return null;}
		}// finally block end
		
		return li;
	}// getSelectedOUDetails end
	
	
	
	/* 
	 * To retrive child orgunit names and levels in an hashtable
	 * Hash table Contains 2 lists one for child orgunit names and another for levels
	*/
	public Hashtable getChildOrgUnitTree(int orgUnitID)
	{							
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rs6 = null;
		
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		PreparedStatement ps5 = null;
		PreparedStatement ps6 = null;
		
		/*
		String query = "SELECT organisationunit.id,organisationunit.shortname,organisationunitstructure.level,organisationunit.name " +
							"FROM organisationunit inner join organisationunitstructure " +
									"ON organisationunit.id = organisationunitstructure.organisationUnitId " +
							"WHERE organisationunit.parent = ? order by shortname";

		*/
		String query = "SELECT organisationunit.organisationunitid,organisationunit.shortname,orgunitstructure.level,organisationunit.name " +
							"FROM organisationunit INNER JOIN orgunitstructure " +
									"ON organisationunit.organisationunitid = orgunitstructure.organisationunitid " +
		                     "WHERE organisationunit.parentid = ? ORDER BY organisationunit.shortname";

		List listForChildIDs = new ArrayList();
		List listForChildNames = new ArrayList();
		List listForChildLevels = new ArrayList();
		List listForChildShortNames = new ArrayList();
		
		Hashtable ht = new Hashtable();
		
		try
	     {			
		   ps1 = con.prepareStatement(query);
		   ps2 = con.prepareStatement(query);
		   ps3 = con.prepareStatement(query);
		   ps4 = con.prepareStatement(query);
		   ps5 = con.prepareStatement(query);
		   ps6 = con.prepareStatement(query);
		   		   
		   ps1.setInt(1,orgUnitID);
		   		   
	       rs1 = ps1.executeQuery();
	       if(!rs1.next())  {System.out.println("No Children"); return null;}
	       else
			{
		 		do
				{
					int parent1 = rs1.getInt(1);
					listForChildIDs.add(new Integer(parent1));
					listForChildNames.add(rs1.getString(2));
					listForChildLevels.add(new Integer(rs1.getInt(3)));
					listForChildShortNames.add(rs1.getString(4));
					
					ps2.setInt(1,parent1);
					rs2 = ps2.executeQuery(); 
					if(rs2.next())
					{
						do
						{
							int parent2 = rs2.getInt(1); 
							listForChildIDs.add(new Integer(parent2));
							listForChildNames.add(rs2.getString(2));
							listForChildLevels.add(new Integer(rs2.getInt(3)));
							listForChildShortNames.add(rs2.getString(4));
							
							if(parent2==parent1) continue;
							ps3.setInt(1,parent2);
							rs3 = ps3.executeQuery(); 
							if(rs3.next())
							{
								do
								{
									int parent3 = rs3.getInt(1); 
									listForChildIDs.add(new Integer(parent3));
									listForChildNames.add(rs3.getString(2));
									listForChildLevels.add(new Integer(rs3.getInt(3)));
									listForChildShortNames.add(rs3.getString(4));
									
									ps4.setInt(1,parent3);
									rs4 = ps4.executeQuery();  
									if(rs4.next())
									{
										do
										{
											int parent4 = rs4.getInt(1); 
											listForChildIDs.add(new Integer(parent4));
											listForChildNames.add(rs4.getString(2));
											listForChildLevels.add(new Integer(rs4.getInt(3)));
											listForChildShortNames.add(rs4.getString(4));
											
											ps5.setInt(1,parent4);
											rs5 = ps5.executeQuery();  
											if(rs5.next())
											{
												do
												{
													int parent5 = rs5.getInt(1); 														
													listForChildIDs.add(new Integer(parent5));
													listForChildNames.add(rs5.getString(2));
													listForChildLevels.add(new Integer(rs5.getInt(3)));
													listForChildShortNames.add(rs5.getString(4));
													
											        ps6.setInt(1,parent5);
													rs6 = ps6.executeQuery();
											        if(rs6.next())
													{
											        	do
														{
											        		int parent6 = rs6.getInt(1); 
											        		listForChildIDs.add(new Integer(parent6));
															listForChildNames.add(rs6.getString(2));
															listForChildLevels.add(new Integer(rs6.getInt(3)));
															listForChildShortNames.add(rs6.getString(4));
														}
														while(rs6.next());
	                          						} // rs6 if end
												}
												while(rs6.next());
											} // rs5 if end
										}
										while(rs4.next());
									}// rs4 if end                        
								}
								while(rs3.next());
							} // rs3 if end
						}
						while(rs2.next()); 
					} // rs2 if end
				}
				while(rs1.next());
			} // rs1 else end
	     } // try end
		catch(Exception e) {System.out.println(e.getMessage()); return null;}
		finally
		{
			try
			{
				if(rs1!=null) rs1.close();
				if(rs2!=null) rs2.close();
				if(rs3!=null) rs3.close();
				if(rs4!=null) rs4.close();
				if(rs5!=null) rs5.close();
				if(rs6!=null) rs6.close();
				
				if(ps1!=null) ps1.close();
				if(ps2!=null) ps2.close();
				if(ps3!=null) ps3.close();
				if(ps4!=null) ps4.close();
				if(ps5!=null) ps5.close();
				if(ps6!=null) ps6.close();
				
			}
			catch(Exception e){return null;}
		}
		
		ht.put("childOUIDsList",listForChildIDs);
		ht.put("childOUNamesList",listForChildNames);
		ht.put("childOULevelsList",listForChildLevels);
		ht.put("childOUShortNamesList",listForChildShortNames);
						
		return ht; 
	} // getChildOrgUnitTree end
	
	
	/*
	 * Returns all the Parent Names of selected OrgUnit
	 */
	public List getParentOrgUnitTree(int orgUnitID,String localLang)
	{
		Statement st1 = null;
		ResultSet rs1 = null;
		Statement st2 = null;
		ResultSet rs2 = null;
		
		String displayOpt = "name";
		if(localLang == null) displayOpt = "shortname"; 
		
		/*
		String query = "SELECT idLevel1,idLevel2,idLevel3,idLevel4,idLevel5,idLevel6,idLevel7 " +
							"FROM organisationunitstructure " +
									"WHERE organisationUnitId = "+orgUnitID;				
		*/
		String query = "SELECT idlevel1,idlevel2,idlevel3,idlevel4,idlevel5,idlevel6,idlevel7 " +
							"FROM orgunitstructure " +
							"WHERE organisationunitid = "+orgUnitID;
		
		List parentNames = new ArrayList();
		parentNames.add(0,"Defalult");
		try
		{
			st1 = con.createStatement();
			st2 = con.createStatement();
			
			rs1 = st1.executeQuery(query);			
			if(rs1.next())
			{
				for(int i=1;i<=7;i++)
				{
					int tempOrgUnitID = rs1.getInt(i);
					if(tempOrgUnitID != 0)
					{
						//query = "SELECT "+displayOpt+" FROM organisationunit WHERE id="+tempOrgUnitID;
						query = "SELECT "+displayOpt+" FROM organisationunit WHERE organisationunitid="+tempOrgUnitID;
						rs2 = st2.executeQuery(query);
						if(rs2.next()) parentNames.add(i,rs2.getString(1));
						System.out.println(i+" - "+rs2.getString(1));
					}
					else break;
				}// for loop end
			}// if block end
		} // try block end
		catch(Exception e) 	{	return null;	}
		finally
		{
			try
			{
				if(rs1!=null) rs1.close();
				if(st1!=null) st1.close();
			}
			catch(Exception e){return null;}
		}// finally block end	
		return parentNames;
	}//getParentOrgUnitTree
	
	/* 
	 * To retrieve the Details of All DataSets - dataset id,
	 * 											names, 
	 * 											count of dataelements that are members of dataset
	 */
	public Hashtable getAllDataSetMemebers()
	{
		Statement st = null;
		ResultSet rs = null;
		
		/*
		String query = "SELECT dataset.id,dataset.name,COUNT(*) " +
							"FROM dataelements INNER JOIN dataset " +
								"ON dataset.id = dataelements.id " +
									"GROUP BY dataset.id";		
		*/
		String query = "SELECT dataset.datasetid,dataset.name,COUNT(*) " +
							"FROM datasetmembers INNER JOIN dataset " +
							"ON dataset.datasetid = datasetmembers.datasetid " +
							"GROUP BY dataset.datasetid";		
		
		Hashtable ht = new Hashtable();
				
		try
		{
			st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = st.executeQuery(query);
			
			while(rs.next())
			{
				String DataSetID = ""+rs.getInt(1);				
				List liForDataSets = new ArrayList();
				
				liForDataSets.add(0,rs.getString(2));
				liForDataSets.add(1,new Integer(rs.getInt(3)));
								
				ht.put(DataSetID,liForDataSets);								
			}// while loop end
		} // try block end
		catch(Exception e) 	{	System.out.println(e.getMessage()); return null;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){return null;}
		}// finally block end
		
		return ht;
	} // getAllDataSetMemebers end
	
	
	
	/* 
	 * To retrieve the Details of selected DataSets - dataset id,
	 * 												names, 
	 * 												count of dataelements that are members of dataset
	 */
	public Hashtable getSelectedDataSetMemebers(String[] dataSetIdsList)
	{
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		/*
		String query = "SELECT dataset.id,dataset.name,COUNT(*) " +
							"FROM dataelements " +
							"INNER JOIN dataset ON dataset.id = dataelements.id " +
								"WHERE dataset.id = ? GROUP BY dataset.id";
		*/
		String query = "SELECT dataset.datasetid,dataset.name,COUNT(*) " +
							"FROM datasetmembers INNER JOIN dataset " +
							"ON dataset.datasetid = datasetmembers.datasetid " +
							"WHERE dataset.datasetid = ? GROUP BY dataset.datasetid";		

		Hashtable ht = new Hashtable();
				
		try
		{
			pst = con.prepareStatement(query);
						
			int count = 0;
			while(count < dataSetIdsList.length)
			{
				int tempDSID = Integer.parseInt(dataSetIdsList[count]);
				pst.setInt(1,tempDSID);
				rs = pst.executeQuery();
				
				if(rs.next())
				{
					String DataSetID = ""+rs.getInt(1);				
					List liForDataSets = new ArrayList();
				
					liForDataSets.add(0,rs.getString(2));
					liForDataSets.add(1,new Integer(rs.getInt(3)));
								
					ht.put(DataSetID,liForDataSets);
				}	
				count++;
				
			}// while loop end
		} // try block end
		catch(Exception e) 	{	System.out.println(e.getMessage()); return null;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(pst!=null) pst.close();
			}
			catch(Exception e){return null;}
		}// finally block end
		
		return ht;
	} // getSelectedDataSetMemebers end
	
	
	
	/*
	 * To retrieve the periodDetails - id, startdate
	 */
	public List getPeriodDetails(String startDate, String endDate, int periodType)
	{
		Statement st = null;
		ResultSet rs = null;
		
		/*
		String query = "SELECT id,startdate FROM period " +
							"WHERE startdate BETWEEN '"+startDate+"' AND '"+endDate+"' " +
									"AND periodtype="+periodType+" order by startdate";
		*/
		String query = "SELECT periodid,startdate FROM period " +
							"WHERE startdate BETWEEN '"+startDate+"' AND '"+endDate+"' " +
								"AND periodtypeid="+periodType+" ORDER BY startdate";

		List li = new ArrayList();		
		
		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);
			
			while(rs.next())
			{
				List templi =  new ArrayList();
				int periodID = rs.getInt(1);
				templi.add(0,new Integer(periodID));
				templi.add(1,rs.getString(2));
								 
				li.add(templi);
			}// while loop end
		} // try block end
		catch(Exception e) 	{	System.out.println(e.getMessage()); return null;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){return null;}
		}// finally block end
				
		return li;		
	}// getPeriodDetails
	
	
	/*
	 * To get the count ie no of dataset members which have data for one orgunit for one period
	 */
	public int getStatusByPeriod(int orgUnitID,int dataSetID,int periodID)
	{
		Statement st = null;
		ResultSet rs = null;
		
		/*
		String query = "SELECT count(*) FROM datavalue " +
							"INNER JOIN dataelements ON datavalue.dataelement = dataelements.elt  " +
							"INNER JOIN period ON datavalue.period=period.id " +
								"WHERE datavalue.source="+orgUnitID+" AND datavalue.period="+periodID+" AND dataelements.id="+dataSetID+" GROUP BY period.id ORDER BY period.startdate";
		*/
		String query = "SELECT count(*) FROM datavalue " +
							"INNER JOIN datasetmembers ON datavalue.dataelementid = datasetmembers.dataelementid  " +
							"INNER JOIN period ON datavalue.periodid = period.periodid " +
							"WHERE datavalue.sourceid="+orgUnitID+" AND " +
									"datavalue.periodid="+periodID+" AND " +
									"datasetmembers.datasetid="+dataSetID+" GROUP BY period.periodid ORDER BY period.startdate";

		int dataStatus = 1;		
		
		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);
			
			if(rs.next()) dataStatus = rs.getInt(1);			
		} // try block end
		catch(Exception e) 	{	System.out.println(e.getMessage()); return 1;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){return 1;}
		}// finally block end			
		return dataStatus;				
	}// getStatusByPeriod		
	
} // class end
