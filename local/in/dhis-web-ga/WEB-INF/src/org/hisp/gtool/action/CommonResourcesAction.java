package org.hisp.gtool.action;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class CommonResourcesAction {

	Connection con = (new DBConnection()).openConnection();
	
	/* 
	 * To retrieve the Details of DataElements Using Shortname
	 */
	public Hashtable getDEDetailsByAlternativeName()
	{
		Statement st = null;
		ResultSet rs = null;
		/*
		String query = "SELECT id,name,shortName,description,alternativeName,code " +
							"FROM dataelement ORDER BY shortName";
		*/
		String query = "SELECT dataelementid,name,shortname,description,alternativeName,code " +
							"FROM dataelement ORDER BY alternativeName";

		Hashtable ht = new Hashtable();	
		System.out.println("In Function");
		try
		{
			st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = st.executeQuery(query);
			
			while(rs.next())
			{
				System.out.println("In While Loop");
				String DEID = ""+rs.getInt(1);
				String DEName = rs.getString(2);
				String DEShortName = rs.getString(3);
				String DEDes = rs.getString(4);
				String DEAltName = rs.getString(5);
				String DECode = rs.getString(6);
				
				System.out.println(DEID+"   "+DEShortName);
				List liForDataElements = new ArrayList();				
				liForDataElements.add(0,DEID);
				liForDataElements.add(1,DEName);				
				liForDataElements.add(2,DEDes);
				liForDataElements.add(3,DEShortName);
				liForDataElements.add(4,DECode);
												
				ht.put(DEAltName,liForDataElements);								
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
	} // getDEDetailsByAlternativeName end
	
}
