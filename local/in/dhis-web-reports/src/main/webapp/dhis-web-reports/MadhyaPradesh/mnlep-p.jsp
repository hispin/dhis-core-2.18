<%@ page import="java.sql.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>


<%@ page session="true"%>

<%
    Connection con=null;
            
    // For finding organisationunit name of selected Orgunit based on Orgunit id
    Statement st1=null;
    ResultSet rs1=null;
      
    // For finding start date of selected period based on period id
    Statement st2=null;
    ResultSet rs2=null;
    
    //For finding monthly values
    Statement st3=null;
    ResultSet rs3=null;
    
    //For finding cumulative values
    Statement st4=null;
    ResultSet rs4=null;
    
    //For finding blockphc name and id 
    Statement st5=null;
    ResultSet rs5=null;
    
    //For finding taluk name and id
    Statement st6=null;
    ResultSet rs6=null;
    
    //For finding district name and id
    Statement st7=null;
    ResultSet rs7=null;
    
    //For finding state name and id
    Statement st8=null;
    ResultSet rs8=null;
    
    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/mp_dhis2";
          

	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = Integer.parseInt( selectedId );

//int selectedOrgUnitID = 244;
	

  	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );

//  	String startingDate  = "2006-08-01";
//	String endingDate  = "2006-08-01";
      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = Integer.parseInt( monthlyPeriodId );

//int periodTypeID = 1;
      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	  	  	
  	String monthlyDataElements[] = {
  											"Lep_DE1",
  											"Lep_DE2",
  											"Lep_DE3",
  											"Lep_DE4",
  											"Lep_DE5",
  											"Lep_DE6",
  											"Lep_DE7",
  											"Lep_DE8",
  											"Lep_DE9",
  											"Lep_DE10",
  											
  											"Lep_DE11",
  											"Lep_DE12",
  											"Lep_DE13",
  											"Lep_DE14",
  											"Lep_DE15",
  											"",
										};
					
 	  int monthlyValues[] = new int[17]; 		
 	  int cumulativeValues[] =  new int[17];
 	  
 	  int CHCID=0;
 	  int TalukID=0;
 	  int DistrictID=0;
 	  int StateID=0;
 	  
 	  String CHCName="";
 	  String TalukName = "";
 	  String  DistrictName = "";
 	  String StateName = "";

  	  int j=1;  	 
  	  int i=1;  	 
  	  int k=0;  	 
  	  int endcount = 0;
  	  int p = 0;  	 
  	  int q = 0;
 	
  	  String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September",
  									"October", "November", "December" };   	
%>

<%

     try
      {
        	Class.forName ("com.mysql.jdbc.Driver").newInstance ();
        	con = DriverManager.getConnection (urlForConnection, userName, password);
        
        	st1=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        	st2=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        	st3=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        	st4=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        	st5=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        	st6=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        	st7=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        	st8=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);	
        	
        	//rs1 = st1.executeQuery("select shortname from organisationunit  where id = "+selectedOrgUnitID);
        	rs1 = st1.executeQuery("select shortname from organisationunit  where organisationunitid = "+selectedOrgUnitID);        
        	if(rs1.next())        {  selectedOrgUnitName = rs1.getString(1);      }
                
//        	rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	   		if(rs2.next()) 	  {  selectedDataPeriodStartDate =  rs2.getDate(1).toString();	} 		  

			selectedDataPeriodStartDate = startingDate;


			rs6=st6.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");	
			if(rs6.next())  { CHCID = rs6.getInt(1);CHCName = rs6.getString(2);  } 
			else  {  CHCID = 0; CHCName = "";  } 

	   		   			
			//rs7=st7.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+CHCID+")");
			rs7=st7.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+CHCID+")");	
			if(rs7.next())  { DistrictID = rs7.getInt(1);DistrictName = rs7.getString(2);  } 
			else  {  DistrictID = 0; DistrictName = "";  } 

			//rs8=st8.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+DistrictID+")");
			rs8=st8.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+DistrictID+")");	
			if(rs8.next())  { StateID = rs8.getInt(1);StateName = rs8.getString(2);  } 
			else  {  StateID = 0; StateName = "";  } 

      } //try block end
      catch(Exception e)  { out.println(e.getMessage());  }
      
     String partsOfDataPeriodStartDate[] = selectedDataPeriodStartDate.split("-");
     int lastYear = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;
     int tempForMonth1 = Integer.parseInt(partsOfDataPeriodStartDate[1]);
     int tempForYear = 0;
     if(tempForMonth1 < 4){   tempForYear = lastYear;  }
     else  {   tempForYear = lastYear + 1;   	}
     String curYearStart=tempForYear+"-04-01";
      	 	
    try
     {     
     	for(i=0;i<15;i++)	  
			{	  
				//rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source ="+selectedOrgUnitID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");
				rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid ="+selectedOrgUnitID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");       			   		
        		if(rs3.next()) 		{  monthlyValues[i] =  rs3.getInt(1);	}  
		  			
				//rs4 = st4.executeQuery(" select sum(value) from datavalue where dataElement in (select id from dataelement where code like '"+monthlyDataElements[i]+"') and source in (select id from organisationunit where parent ="+selectedOrgUnitID+") and period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+")");
				rs4 = st4.executeQuery(" select sum(value) from datavalue where dataelementid in (select dataelementid from dataelement where code like '"+monthlyDataElements[i]+"') and sourceid in (select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+") and periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodtypeid = "+periodTypeID+")");        			        		
    			if(rs4.next())  		{  	cumulativeValues[i] =  rs4.getInt(1);		}  	  
	  		}
  	} // try block end
    catch(Exception e)  { out.println(e.getMessage());  }
    finally
       {
			try
			{
  				if(rs1!=null)  rs1.close(); 		if(st1!=null)  st1.close();
  				if(rs2!=null)  rs2.close(); 		if(st2!=null)  st2.close();  				
  				if(rs3!=null)  rs3.close(); 		if(st3!=null)  st3.close();				
				if(rs4!=null)  rs4.close(); 		if(st4!=null)  st4.close();
				if(rs5!=null)  rs5.close(); 		if(st5!=null)  st5.close();
				if(rs6!=null)  rs6.close(); 		if(st6!=null)  st6.close();
				if(rs7!=null)  rs7.close(); 		if(st7!=null)  st7.close();
				if(rs8!=null)  rs8.close(); 		if(st8!=null)  st8.close();
				
				if(con!=null)  con.close(); 
			}
			catch(Exception e)   {  out.println(e.getMessage());   }
       } // finally block end	
       
  %>

<html>
<head>
	<title>NLEP Monthly Reporting Form</title>
</head>
<body>	
   <center>
      <font face="arial" size="4">
   		&#2310;&#2352;&#2379;&#2327;&#2381;&#2351;&#2350; &#2360;&#2369;&#2326;&#2360;&#2350;&#2381;&#2346;&#2342;&#2366; </font>
   		<br>
   	  <font face="arial" size="2">	
   		&#2309;&#2344;&#2369;&#2360;&#2370;&#2330;&#2367; - IV 
   		</font>
   	</center>	
	<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
	  	<tr>
          <td width="100%" colspan="9" align="center" style="border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-width: 0; "><font face="Arial" size="3"><b>NLEP  
          - &#2350;&#2366;&#2360;&#2367;&#2325; &#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2344; &#2346;&#2381;&#2352;&#2346;&#2340;&#2381;&#2352;</b></font></td></tr>
	  	<tr>
          <td width="100%" colspan="9" align="center" style="border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-width: 0; border-bottom-width: 0"><font face="Arial" size="3"><b>&#2346;&#2381;&#2352;. 
       			&#2360;&#2381;&#2357;&#2366;. &#2325;&#2375;&#2306;. / &#2346;&#2381;&#2352;&#2326;&#2339;&#2381;&#2337; &#2346;&#2381;&#2352;. 
          &#2360;&#2381;&#2357;&#2366;. &#2325;&#2375;&#2306;. &#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2344;</b></font></td></tr>
	  	<tr>
    			<td width="50%" height="25" colspan="4" style="border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 0; border-top-width: 0; border-bottom-width: 0"><font face="Arial" size="2">&#2346;&#2368; &#2319;&#2330; &#2360;&#2368;&nbsp;&nbsp;&nbsp;&nbsp;<%=selectedOrgUnitName%></font></td>
    			<td width="50%" height="25" colspan="5" style="border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 1; border-top-width: 0; border-bottom-width: 0"><font face="Arial" size="2">&#2348;&#2381;&#2354;&#2366;&#2325;&nbsp;&nbsp;&nbsp;&nbsp;<%=CHCName%></font></td>
  	  	</tr>
  		<tr>
    			<td width="50%" height="25" colspan="4" style="border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 0; border-top-width: 0; border-bottom-width: 0"><font face="Arial" size="2">&#2332;&#2367;&#2354;&#2366;&nbsp;&nbsp;&nbsp;&nbsp;<%=DistrictName%></font></td>
    			<td width="50%" height="25" colspan="5" style="border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 1; border-top-width: 0; border-bottom-width: 0"><font face="Arial" size="2">&#2352;&#2366;&#2332;&#2381;&#2351;&nbsp;&nbsp;&nbsp;&nbsp;<%=StateName%></font></td>
  		</tr>
  		<tr>
    			<td width="50%" height="25" colspan="4" style="border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 0; border-top-width: 0; border-bottom-width: 0"><font face="Arial" size="2">&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361;&nbsp;&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%></font></td>
    			<td width="50%" height="25" colspan="5" style="border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 1; border-top-width: 0; border-bottom-width: 0"><font face="Arial" size="2">&#2357;&#2352;&#2381;&#2359;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=partsOfDataPeriodStartDate[0]%></font></td>
  		</tr>
  		<tr>
    			<td width="3%" height="25" ><font face="Arial" size="2">1.</font></td>
    			<td width="57%" height="25" colspan="4"><font face="Arial" size="2">&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2346;&#2381;&#2352;&#2366;&#2352;&#2350;&#2381;&#2349; &#2361;&#2379;&#2344;&#2375; &#2325;&#2375; &#2360;&#2350;&#2351; &#2350;&#2352;&#2368;&#2332;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</font></td>
    			<td width="25%" height="25" colspan="3" bgcolor="#000000">&nbsp;</td>
    			<td width="10%" height="25"  align="center"><font face="Arial" size="2"><%=monthlyValues[0]%></font></td>
  		</tr>
  		<tr>
    			<td width="3%" rowspan="3" height="25" ><font face="Arial" size="2">2.</font></td>
    			<td width="57%" rowspan="3" colspan="4" height="25" ><font face="Arial" size="2">&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2350;&#2375;&#2306; &#2344;&#2351;&#2375; &#2325;&#2369;&#2359;&#2381;&#2336; &#2352;&#2379;&#2327;&#2367;&#2351;&#2379;&#2306; &#2325;&#2368; &#2326;&#2379;&#2332; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" colspan="2" ><font face="Arial" size="2">
                &#2346;&#2368; &#2348;&#2368; -</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[1]%></font></td>
    			<td width="10%" height="25"  align="center" bgcolor="#000000" bordercolor="#FFFFFF" ><font face="Arial" size="2"></font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" colspan="2" ><font face="Arial" size="2">
                &#2319;&#2350; &#2348;&#2368; -</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[2]%></font></td>
    			<td width="10%" height="25"  align="center" bgcolor="#000000" bordercolor="#FFFFFF" ><font face="Arial" size="2"></font></td>
  		</tr>
  		<tr>
    			<td width="3%" height="25" rowspan="5" ><font face="Arial" size="2">3.</font></td>
    			<td width="57%" height="25" rowspan="5" colspan="4"><font face="Arial" size="2">&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2325;&#2375; &#2342;&#2380;&#2352;&#2366;&#2344; &#2344;&#2351;&#2375; &#2325;&#2369;&#2359;&#2381;&#2336; &#2352;&#2379;&#2327;&#2367;&#2351;&#2379;&#2306; &#2325;&#2368; &#2326;&#2379;&#2332; &#2325;&#2375; &#2348;&#2368;&#2330; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;-</font></td>
    			<td width="15%" height="25" colspan="2" ><font face="Arial" size="2">&#2348;&#2330;&#2381;&#2330;&#2375;</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[3]%></font></td>
    			<td width="10%" height="25"  align="center" bgcolor="#000000" bordercolor="#FFFFFF" ><font face="Arial" size="2"></font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" colspan="2" ><font face="Arial" size="2">&#2350;.</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[4]%></font></td>
    			<td width="10%" height="25"  align="center" bgcolor="#000000" bordercolor="#FFFFFF" ><font face="Arial" size="2"></font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" colspan="2" ><font face="Arial" size="2">&#2342;&#2375;&#2326;&#2344;&#2375; &#2351;&#2379;&#2327;&#2381;&#2351; &#2357;&#2367;&#2325;&#2371;&#2340;&#2367;&#2351;&#2366;&#2305;</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[5]%></font></td>
    			<td width="15%" height="25"  align="center" bgcolor="#000000" bordercolor="#FFFFFF" ><font face="Arial" size="2"></font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" colspan="2" ><font face="Arial" size="2">&#2319;&#2360; &#2360;&#2368;</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[6]%></font></td>
    			<td width="15%" height="25"  align="center" bgcolor="#000000" bordercolor="#FFFFFF" ><font face="Arial" size="2"></font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" colspan="2" ><font face="Arial" size="2">&#2319;&#2360; &#2335;&#2368;</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[7]%></font></td>
    			<td width="15%" height="25"  align="center" bgcolor="#000000" bordercolor="#FFFFFF" ><font face="Arial" size="2"></font></td>
  		</tr>
  		<tr>
    			<td width="3%" rowspan="2" height="25" ><font face="Arial" size="2">4.</font></td>
    			<td width="57%" height="25" rowspan="2" colspan="4"><font face="Arial" size="2">&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2350;&#2375;&#2306; &#2352;&#2379;&#2327; &#2350;&#2369;&#2325;&#2381;&#2340; &#2325;&#2367;&#2319; &#2327;&#2319; &#2352;&#2379;&#2327;&#2367;&#2351;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</font></td>
    			<td width="15%" height="25" colspan="2" ><font face="Arial" size="2">&#2310;&#2352; &#2319;&#2347; &#2335;&#2368; -</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[8]%></font></td>
    			<td width="10%" height="25"  align="center" bgcolor="#000000" bordercolor="#FFFFFF" ><font face="Arial" size="2"></font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" colspan="2" ><font face="Arial" size="2">&#2309;&#2344;&#2381;&#2351; -</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[9]%></font></td>
    			<td width="15%" height="25"  align="center" bgcolor="#000000" bordercolor="#FFFFFF" ><font face="Arial" size="2"></font></td>
  		</tr>
  		<tr>
    			<td width="3%" height="25" ><font face="Arial" size="2">5.</font></td>
    			<td width="57%" height="25" colspan="4"><font face="Arial" size="2">&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2325;&#2375; &#2309;&#2306;&#2340; &#2350;&#2375;&#2306; &#2352;&#2379;&#2327;&#2367;&#2351;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366; (1+2+4)</td>
    			<td width="25%" height="25" colspan="3" bgcolor="#000000" bordercolor="#FFFFFF">&nbsp;</td>
    			<td width="15%" height="25" align="center" bgcolor="#FFFFFF" ><font face="Arial" size="2"><%=(monthlyValues[0]+monthlyValues[1]+monthlyValues[2]+monthlyValues[8]+monthlyValues[9])%></font></td>
  		</tr>
  		<tr>
    			<td width="3%" height="25" ><font face="Arial" size="2">6.</font></td>
    			<td width="57%" height="25" colspan="4"><font face="Arial" size="2">&#2319;&#2350; &#2337;&#2368; &#2335;&#2368; &#2360;&#2375;&#2357;&#2366; &#2313;&#2346;&#2354;&#2348;&#2381;&#2343; &#2325;&#2352;&#2366;&#2312; &#2327;&#2312; &#2313;&#2346;&#2325;&#2375;&#2306;&#2342;&#2381;&#2352;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</font></td>
    			<td width="25%" height="25" colspan="3" bgcolor="#000000" bordercolor="#FFFFFF">&nbsp;</td>
    			<td width="15%" height="25" align="center" bgcolor="#FFFFFF"><font face="Arial" size="2"><%=monthlyValues[10]%></font></td>
  		</tr>
  		<tr>
    			<td width="2%" height="25" ><font face="Arial" size="2">7.</font></td>
    			<td width="98%" height="25" colspan="8"><font face="Arial" size="2">&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2325;&#2375; &#2309;&#2344;&#2381;&#2340; &#2350;&#2375;&#2306; &#2325;&#2369;&#2359;&#2381;&#2336; &#2342;&#2357;&#2366;&#2323;&#2306; &#2325;&#2368; &#2349;&#2339;&#2381;&#2337;&#2366;&#2352;&#2339; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</font></td>
   		</tr>
  		<tr>
    			<td width="3%" height="25" >&nbsp;</td>
    			<td width="20%" height="25" align="center" colspan="2"><font face="Arial" size="2">Blister Pack</font></td>
    			<td width="25%" height="25" align="center" ><font face="Arial" size="2">Quantity</font></td>
    			<td width="25" height="25" align="center" colspan="3"><font face="Arial" size="2">Expiry Date</font></td>
    			<td width="25%" height="25" align="center" colspan="2"><font face="Arial" size="2">Total Stock</font></td>
  		</tr>
  		<tr>
    			<td width="3%" height="25" rowspan="3" >&nbsp;</td>
    			<td width="20%" height="25" align="center" rowspan="3" colspan="2" ><font face="Arial" size="2">&#2319;&#2350; &#2348;&#2368;(&#2319;)</font></td>
    			<td width="25%" height="25" ></td>
    			<td width="25" height="25" colspan="3"></td>
    			<td width="25%"  align="center" height="25" colspan="2" rowspan="3"><font face="Arial" size="2"><%=monthlyValues[11]%></font></td>
  		</tr>
  		<tr>
    			<td width="25%" height="25"></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
  		<tr>
    			<td width="25%" height="25"></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
 		<tr>
    			<td width="3%" height="25" rowspan="3" >&nbsp;</td>
    			<td width="20%" height="25" align="center" rowspan="3" colspan="2" ><font face="Arial" size="2">&#2319;&#2350; &#2348;&#2368;(&#2360;&#2368;)</font></td>
    			<td width="25%" height="25"></td>
    			<td width="25" height="25" colspan="3"></td>
    			<td width="25%" align="center" height="25" colspan="2" rowspan="3"><font face="Arial" size="2"><%=monthlyValues[12]%></font></td>
  		</tr>
 		<tr>
    			<td width="25%" height="25"></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
 		<tr>
    			<td width="25%" height="25"></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
 		<tr>
    			<td width="3%" height="25" rowspan="3" >&nbsp;</td>
    			<td width="20%" height="25" align="center" rowspan="3" colspan="2" ><font face="Arial" size="2">&#2346;&#2368; &#2348;&#2368;(&#2319;)</font></td>
    			<td width="25%" height="25"></td>
    			<td width="25" height="25" colspan="3"></td>
    			<td width="25%"  align="center" height="25" colspan="2" rowspan="3"><font face="Arial" size="2"><%=monthlyValues[13]%></font></td>
  		</tr>
 		<tr>
    			<td width="25%" height="25"></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
 		<tr>
    			<td width="25%" height="25"></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
		<tr>
    			<td width="3%" height="25" rowspan="3" >&nbsp;</td>
    			<td width="20%" height="25" align="center" rowspan="3" colspan="2" ><font face="Arial" size="2">&#2346;&#2368; &#2348;&#2368;(&#2360;&#2368;)</font></td>
    			<td width="25%" height="25"></td>
    			<td width="25" height="25" colspan="3"></td>
    			<td width="25%"  align="center" height="25" colspan="2" rowspan="3"><font face="Arial" size="2"><%=monthlyValues[14]%></font></td>
  		</tr>
		<tr>
    			<td width="25%" height="25"></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
		<tr>
    			<td width="25%" height="25"></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
  		<tr>
    			<td width="100%" height="100" valign="top" colspan="9"><font face="Arial" size="2">&#2344;&#2379;&#2335;: &#2350;&#2366;&#2361; &#2350;&#2366;&#2352;&#2381;&#2330;, &#2332;&#2370;&#2344;, &#2360;&#2367;&#2340;&#2350;&#2381;&#2348;&#2352; &#2319;&#2357;&#2306; &#2342;&#2367;&#2360;&#2350;&#2381;&#2348;&#2352; &#2350;&#2375;&#2306; &#2340;&#2381;&#2352;&#2376;&#2350;&#2366;&#2360;&#2367;&#2325; &#2352;&#2379;&#2327;&#2367;&#2351;&#2379;&#2306; &#2325;&#2368; &#2327;&#2339;&#2344;&#2366; &#2319;&#2350; &#2348;&#2368;(&#2319;),&#2319;&#2350; &#2348;&#2368;(&#2360;&#2368;),&#2346;&#2368; &#2348;&#2368;(&#2319;) &#2319;&#2357;&#2306; &#2319;&#2350; &#2348;&#2368;(&#2360;&#2368;) &#2325;&#2368; &#2325;&#2371;&#2346;&#2366; &#2325;&#2352;&#2375;&#2306; &#2332;&#2376;&#2360;&#2366; &#2360;&#2350;&#2381;&#2348;&#2343;&#2367;&#2340; &#2350;&#2366;&#2360;&#2367;&#2325; &#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2344; &#2350;&#2375;&#2306; &#2325;&#2367;&#2351;&#2366; &#2327;&#2351;&#2366; &#2361;&#2376;!</font></td>
  		</tr>
  		<tr>
    			<td width="20%" height="25" colspan="2"><font face="Arial" size="2">Date:</font></td>
    			<td width="30%" height="25" colspan="3">&nbsp;</td>
    			<td width="50%" height="25" colspan="4"><font face="Arial" size="2">
                Name and Signature of Medical Officer</font></td>
  		</tr>
  </table>			             
</body>
</html>