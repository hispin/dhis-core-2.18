package org.hisp.gtool.action;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class GenerateHomePage {
	
	Connection con = (new DBConnection()).openConnection();
	int periodType=1;
	
	
	/** This is to get the Monthly periods - Starting dates **/
	
	public List getStartDate()
	{
		Statement st = null;
		ResultSet rs = null;
		
		List li = new ArrayList();
		try
		{
			/*
			String query = "select startdate from period where periodType ="+periodType+" order by startdate";
			*/
			String query = "select startdate from period where periodtypeid ="+periodType+" order by startdate";
			st = con.createStatement();
			rs = st.executeQuery(query);
						
			while(rs.next())
			{
				li.add(rs.getString(1));
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
			catch(Exception e){return null;}
		}// finally block end
		
		return li;					
	} //  getStartDate end
	
	
	/** This is to get the Monthly periods - Ending dates **/
	public List getEndDate()
	{
		Statement st = null;
		ResultSet rs = null;
		
		List li = new ArrayList();
		try
		{
			/*
			String query = "select enddate from period where periodType ="+periodType+" order by startdate";
			*/
			String query = "select enddate from period where periodtypeid ="+periodType+" order by startdate";
			st = con.createStatement();
			rs = st.executeQuery(query);
						
			while(rs.next())
			{
				li.add(rs.getString(1));
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
			catch(Exception e){return null;}
		}// finally block end
		return li;					
	} //  getEndDate end

	public List getIndicatorName()
	{
		Statement st = null;
		ResultSet rs = null;
		
		List li = new ArrayList();
		try
		{
			String query = "select name from indicator";
			st = con.createStatement();
			rs = st.executeQuery(query);
						
			while(rs.next())
			{
				li.add(rs.getString(1));
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
			catch(Exception e){return null;}
		}// finally block end
		
		return li;					
	} //  getIndicatorName end	
	
	
	
	public Hashtable getIndicatorGroups()
	{
		Statement st1 = null;
		ResultSet rs1 = null;
		
		Statement st2 = null;
		ResultSet rs2 = null;
		
		Statement st3 = null;
		ResultSet rs3 = null;
		
		String query = "";
		
		Hashtable ht = new Hashtable();
					
		try
		{			
			int flag = 0;
			st1 = con.createStatement();
			st2 = con.createStatement();
			st3 = con.createStatement();
			
			query = "select indicatorgroupid,name from indicatorgroup order by name";
			rs1 = st1.executeQuery(query); 
			
			//Hashtable htForIndicator = setIndicatorIDs();
			//Enumeration keysForIndicator = htForIndicator.keys();
			
			while(rs1.next())
			{				
				int igID = rs1.getInt(1);
				String igName = rs1.getString(2);
				
				List liForIGMembers = new ArrayList();
				
				if(flag==1)
				{
					flag = 0;
					List liForIGMembers1 = new ArrayList();
					rs3 = st3.executeQuery("select name from indicator order by name");
					while(rs3.next())
					{
						String igMember =  rs3.getString(1);
						liForIGMembers1.add(igMember);	
					} // while end
					ht.put("(Select All)",liForIGMembers1);
				} // if end
				
				/*
				query = "select indicator.name from indicatorgroupmembers inner join indicator on indicatorgroupmembers.indicatorid = indicator.id where indicatorgroupmembers.groupid = "+igID+" order by indicator.name";
				*/
				query = "SELECT indicator.name FROM indicatorgroupmembers " +
								"INNER JOIN indicator " +
								"ON indicatorgroupmembers.indicatorid = indicator.indicatorid " +
								"WHERE indicatorgroupmembers.indicatorgroupid = "+igID+
								" ORDER BY indicator.name";
				rs2 =  st2.executeQuery(query);
				while(rs2.next())
				{				
					String igMember =  rs2.getString(1);
					liForIGMembers.add(igMember);																									
				}// inner while loop end
				ht.put(igName,liForIGMembers);											
				
			}// outer while loop end
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
				
				if(rs3!=null) rs3.close();
				if(st3!=null) st3.close();
				
			}
			catch(Exception e){System.out.println(e.getMessage());return null;}
		}// finally block end				
		return ht;				
	}// getIndicatorGroups end

	public Hashtable getDEGroups()
	{
		int flag = 0;
		Statement st1 = null;
		ResultSet rs1 = null;
		
		Statement st2 = null;
		ResultSet rs2 = null;
		
		Statement st3 = null;
		ResultSet rs3 = null;
		
		String query = "";
		
		Hashtable ht = new Hashtable();
					
		try
		{			
			st1 = con.createStatement();
			st2 = con.createStatement();
			st3 = con.createStatement();
						
			/*
			query = "select id,name from dataelementgroup";
			*/
			query = "select dataelementgroupid,name from dataelementgroup";
			rs1 = st1.executeQuery(query); 
						
			while(rs1.next())
			{				
				int degID = rs1.getInt(1);
				String degName = rs1.getString(2);
				
				List liForDEGMembers = new ArrayList();
				
				if(flag==1)
				{
					flag = 0;
					List liForDEGMembers1 = new ArrayList();
					rs3 = st3.executeQuery("select alternativeName from dataelement order by alternativeName");
					while(rs3.next())
					{
						String igMember =  rs3.getString(1);
						liForDEGMembers1.add(igMember);	
					} // while end
					ht.put("(Select All)",liForDEGMembers1);
				} // if end
				
				
				/*
				  query = "select dataelement.shortname from degmembers inner join dataelement on degmembers.elt = dataelement.id where degmembers.id = "+degID+" order by dataelement.shortname";
				*/
				query = "SELECT dataelement.alternativeName FROM dataelementgroupmembers " +
								"INNER JOIN dataelement " +
								"ON dataelementgroupmembers.dataelementid = dataelement.dataelementid " +
								"WHERE dataelementgroupmembers.dataelementgroupid = "+degID+" ORDER BY dataelement.alternativeName";
				rs2 =  st2.executeQuery(query);
				while(rs2.next())
				{				
					String degMember =  rs2.getString(1);
					liForDEGMembers.add(degMember);																									
				}// inner while loop end
				ht.put(degName,liForDEGMembers);											
				
			}// outer while loop end
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
				
				if(rs3!=null) rs3.close();
				if(st3!=null) st3.close();
			}
			catch(Exception e){System.out.println(e.getMessage());return null;}
		}// finally block end				
		return ht;				
	}// getDEGroups end

	
}// class end
