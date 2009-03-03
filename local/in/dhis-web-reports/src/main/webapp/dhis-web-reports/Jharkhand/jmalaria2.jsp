
<%@ page import="java.sql.*,java.util.*" %>
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

    // For finding child orgunit ids and names based on selected orgunit id
    Statement st3=null;
    ResultSet rs3=null;

   // For finding child orgunit 
    Statement st4=null;
    ResultSet rs4=null;

  
    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/jh_dhis2";
          
	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = Integer.parseInt( selectedId );
	
//	String selectedPeriodId = (String) stack.findValue( "periodSelect" );
//	int selectedDataPeriodID = 	Integer.parseInt( selectedPeriodId );

  	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );

      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = 	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = ""; 
  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String monthlyDataElements[] = {
  											// New OPD Cases
  											"MAL_DE1",
  											
  											// No. of Fever Cases (Male + Female)
  											"MAL_DE2","MAL_DE3",
  											
  											// 4Aq without Blood Smears
  											"MAL_DE43",
  											
  											// B.S Collected (Active + Passive)
  											"MAL_DE6","MAL_DE10",
  											
  											// +ve (Male + Female)
  											"MAL_DE26","MAL_DE27",
  											
  											// Tablets Consuming 4Aq
  											"MAL_DE45",
  											
											// Radical Treatment - [ (0-1) + (1-4) + (5-14) + (15+) ]
											"MAL_DE37","MAL_DE38","MAL_DE39","MAL_DE40",
											
											// Tablets Consuming 8Aq
											"MAL_DE46","MAL_DE47",
											
											// Stock - 4Aq, 8Aq (2.5mg + 7.5mg)
											"MAL_DE48","MAL_DE49","MAL_DE50"
  											
										};
	
	List childOrgUnitIDs = new ArrayList();	
	List childOrgUnitNames = new ArrayList();	
		
  	int childOrgUnitCount = 0;  	
  	int count = 0;  	
  	int i=0;
  	int totPopulation = 0;
  	  	
	int tempval[] = new int[monthlyDataElements.length+5];
	int total[] = new int[monthlyDataElements.length+5];
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

        //rs1 = st1.executeQuery("SELECT organisationunit.shortname  FROM organisationunit WHERE id ="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname  FROM organisationunit WHERE organisationunitid ="+selectedOrgUnitID);        
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);     }
        else  {  selectedOrgUnitName = "";           }  
                
//        rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	    if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	}  

		selectedDataPeriodStartDate = startingDate;

				
	    //rs3 =  st3.executeQuery("select id,shortname from organisationunit where parent = "+selectedOrgUnitID);
	    rs3 =  st3.executeQuery("select organisationunitid,shortname from organisationunit where parentid = "+selectedOrgUnitID);
	    while(rs3.next())
		 {
		  	Integer tempInt = new Integer(rs3.getInt(1));
		 	childOrgUnitIDs.add(childOrgUnitCount,tempInt);		 	
		 	childOrgUnitNames.add(childOrgUnitCount,rs3.getString(2));		 	
		 	childOrgUnitCount++;
		 } 
		 	  	 
	 } // try block end
    catch(Exception e)  { out.println(e.getMessage());  }

    String partsOfDataPeriodStartDate[] = selectedDataPeriodStartDate.split("-");			     
%>


<HTML>
	<HEAD>
		<TITLE>&#2350;&#2354;&#2375;&#2352;&#2367;&#2351;&#2366; &#2325;&#2306;&#2335;&#2381;&#2352;&#2379;&#2354; &#2360;&#2379;&#2360;&#2366;&#2312;&#2335;&#2368;</TITLE>
	</HEAD>
	<BODY>
		<P align="center"><b>
			<FONT face="Arial" size="3">
			<%=selectedOrgUnitName%> &nbsp;	
				&#2350;&#2354;&#2375;&#2352;&#2367;&#2351;&#2366; &#2325;&#2306;&#2335;&#2381;&#2352;&#2379;&#2354; &#2360;&#2379;&#2360;&#2366;&#2312;&#2335;&#2368;<BR>
				&#2332;&#2381;&#2357;&#2352; &#2313;&#2346;&#2330;&#2366;&#2352; &#2337;&#2367;&#2346;&#2379; &#2360;&#2361;&#2367;&#2340; &#2344;&#2367;&#2359;&#2381;&#2325;&#2381;&#2352;&#2367;&#2351; &#2309;&#2349;&#2367;&#2325;&#2352;&#2339;&#2379;&#2306; &#2346;&#2376;&#2360;&#2381;&#2360;&#2367;&#2357; &#2319;&#2332;&#2375;&#2344;&#2381;&#2360;&#2367;&#2351;&#2366;&#2305; 
        &#2309;&#2352;&#2381;&#2341;&#2366;&#2340; &#2330;&#2367;&#2325;&#2367;&#2340;&#2381;&#2360;&#2366; &#2360;&#2306;&#2360;&#2381;&#2341;&#2344;&#2379;&#2306; &#2325;&#2368; &#2350;&#2366;&#2360;&#2367;&#2325; &#2352;&#2367;&#2346;&#2379;&#2352;&#2381;&#2335;
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MF-10
			</font>	
	</b><br></p>
	<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="3"><b>District :&nbsp;&nbsp;<%=selectedOrgUnitName%></b></font>
    				</td>
    				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2"><b>Month :&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%> </b></font>
    				</td>   
  			</tr>
	</table>
	
		
		
		<%
			
			try
			  {
				  count = 0;
				  while(count < childOrgUnitCount)
				   { 				   
				    if(count%10==0)
				     {
				       if(count != 0) {%></table><div align="right"><font face="Arial" size="1"><i>(page contd.)</i></font></div> <br><%}%>
				     </table><br>
				     <TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  						<TR>
  							<TD align="center" width="3%" rowSpan=2><B><FONT face="Arial" size="2">&#2325;&#2381;&#2352;&#2350;&#2366;&#2306;&#2325;</FONT></B></TD>
   							<TD align="center" width="47%" rowSpan=2><B><FONT face="Arial" size="2">&#2319;&#2332;&#2375;&#2344;&#2381;&#2360;&#2368; / &#2319;&#2347;&#2335;&#2368;&#2337;&#2368; &#2325;&#2366; &#2344;&#2366;&#2350;</FONT></B></TD>
   							<TD align="center" width="5%" rowspan="2"><B><FONT face="Arial" size="2">&#2323;&#2346;&#2368;&#2337;&#2368;-&#2344;&#2351;&#2375; &#2352;&#2379;&#2327;&#2368;</FONT></B></TD>
   							<TD align="center" width="5%" rowspan="2"><B><FONT face="Arial" size="2">&#2348;&#2369;&#2326;&#2366;&#2352; &#2325;&#2375; &#2352;&#2379;&#2327;&#2368;&#2351;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</FONT></B></TD>
   							<TD align="center" width="5%" rowspan="2"><B><FONT face="Arial" size="2">&#2348;&#2368;&#2344;&#2366; &#2326;&#2370;&#2344; &#2325;&#2375; &#2332;&#2366;&#2305;&#2330; &#2325;&#2375; &#2350;&#2354;&#2375;&#2352;&#2367;&#2351;&#2366; &#2325;&#2368; &#2342;&#2357;&#2366; 4 &#2319;&#2325;&#2381;&#2351;&#2370; &#2360;&#2375; &#2313;&#2346;&#2330;&#2366;&#2352;&#2367;&#2340; &#2348;&#2369;&#2326;&#2366;&#2352; &#2325;&#2375; &#2350;&#2352;&#2368;&#2332;</FONT></B></TD>
   							<TD align="center" width="5%" rowspan="2"><B><FONT face="Arial" size="2">&#2354;&#2368; &#2327;&#2312; &#2360;&#2354;&#2366;&#2311;&#2337;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</FONT></B></TD>
   							<TD align="center" width="5%" rowspan="2"><B><FONT face="Arial" size="2">&#2343;&#2344;&#2366;&#2340;&#2381;&#2350;&#2325; &#2360;&#2381;&#2354;&#2366;&#2311;&#2337;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</FONT></B></TD>
   							<TD align="center" width="5%" rowspan="2"><B><FONT face="Arial" size="2">&#2313;&#2346;&#2349;&#2369;&#2325;&#2381;&#2340; (&#2325;&#2306;&#2332;&#2381;&#2351;&#2369;&#2350;&#2381;&#2337;) 4-&#2319;&#2325;&#2381;&#2351;&#2370;</FONT></B></TD>
   							<TD align="center" width="5%" rowspan="2"><B><FONT face="Arial" size="2">&#2310;&#2350;&#2370;&#2354; &#2313;&#2346;&#2330;&#2366;&#2352;&#2367;&#2340; &#2357;&#2381;&#2351;&#2325;&#2381;&#2340;&#2367;&#2351;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</FONT></B></TD>
   							<TD align="center" width="5%" rowspan="2"><B><FONT face="Arial" size="2">&#2313;&#2346;&#2349;&#2369;&#2325;&#2381;&#2340; 8-&#2319;&#2325;&#2381;&#2351;&#2370;</FONT></B></TD>
   							<TD align="center" width="10%" colspan="2"><B><FONT face="Arial" size="2">&#2348;&#2366;&#2325;&#2368; &#2348;&#2330;&#2368; &#2361;&#2369;&#2312; &#2342;&#2357;&#2366;&#2319;&#2305;</FONT></B></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="5%"><b><font face="Arial" size="2">4-&#2319;&#2325;&#2381;&#2351;&#2370;</font></b></TD>
   							<TD align="center" width="5%"><b><font face="Arial" size="2">8-&#2319;&#2325;&#2381;&#2351;&#2370; 2.5mg 7.5mg</font></b></TD>
    					</TR>
    					<TR>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>1</b></i></FONT></TD>
    						<TD width="47%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>2</b></i></FONT></TD>
    						<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>3</b></i></FONT></TD>
    						<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>4</b></i></FONT></TD>
    						<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>5</b></i></FONT></TD>
    						<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>6</b></i></FONT></TD>
    						<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>7</b></i></FONT></TD>
    						<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>8</b></i></FONT></TD>
    						<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>9</b></i></FONT></TD>
    						<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>10</b></i></FONT></TD>
    						<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>11</b></i></FONT></TD>
    						<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>12</b></i></FONT></TD>
    					</TR>	
    						     				     
				     <%}
					Integer temp1 = (Integer) childOrgUnitIDs.get(count);
					int currentChildID = temp1.intValue(); 			
					for(i=0;i<monthlyDataElements.length;i++)
					 {			
						// For PHC
						//"SELECT datavalue.value FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period ="+selectedDataPeriodID+" AND datavalue.source ="+currentChildID+" AND dataelement.code like '"+monthlyDataElements[i]+"'";
						
						// For District
						//"SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period ="+selectedDataPeriodID+" AND datavalue.source in (select id from organisationunit where parent ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'"; 


						
						//rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (select id from organisationunit where parent ="+currentChildID+") AND dataelement.code like '"+monthlyDataElements[i]+"'");
						rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+currentChildID+") AND dataelement.code like '"+monthlyDataElements[i]+"'");
						//SELECT datavalue.value FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period="+selectedDataPeriodID+" AND datavalue.source="+currentChildID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");
						//SELECT sum(datavalue.value) FROM (organisationunit INNER JOIN datavalue ON organisationunit.id = datavalue.source) INNER JOIN period ON datavalue.period = period.id WHERE organisationunit.parent="+selectedOrgUnitID+" AND period.startDate='"+selectedDataPeriodStartDate+"' AND periodType="+periodTypeID+" AND datavalue.dataElement="+dataElementIDs[i])
						
						
						if(!rs4.next())  {  tempval[i] = 0;	 }
						else   {  tempval[i] = rs4.getInt(1);  }
						total[i] += tempval[i];
					 } 	
			   
				   %>			     				  		             		
  					
  					<TR>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(count+1)%></font></TD>
    					<TD width="47%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=childOrgUnitNames.get(count)%></font></TD>
    					<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[0]%></font></TD>
    					<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[1]+tempval[2])%></font></TD>
    					<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[3]%></font></TD>
    					<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[4]+tempval[5])%></font></TD>
    					<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[6]+tempval[7])%></font></TD>
    					<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[8]%></font></TD>
    					<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[9]+tempval[10]+tempval[11]+tempval[12])%></font></TD>
    					<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[13]+tempval[14])%></font></TD>
    					<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[15]%></font></TD>
    					<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[16]+tempval[17])%></font></TD>
    				</TR>
    				<% 					
    					count++;
  				   }	// while loop end
  			  } // try block end
  			catch(Exception e)  { out.println(e.getMessage());  }
     		finally
       		 {
				try
				 {
  					if(rs1!=null)  rs1.close();		if(st1!=null)  st1.close();
  					if(rs2!=null)  rs2.close();		if(st2!=null)  st2.close();
  					if(rs3!=null)  rs3.close();		if(st3!=null)  st3.close();  					
  					if(rs4!=null)  rs4.close();		if(st4!=null)  st4.close();				
  				
					if(con!=null)  con.close();
				 }
				catch(Exception e)   {  out.println(e.getMessage());   }
       		 } // finally block end		          		
  		%>

			<TR>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1">&nbsp;</font></TD>
				<TD width="47%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><b>&#2325;&#2369;&#2354;</b></font></TD>
				<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[0]%></font></TD>
				<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[1]+total[2])%></font></TD>
				<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[3]%></font></TD>
				<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[4]+total[5])%></font></TD>
				<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[6]+total[7])%></font></TD>
				<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[8]%></font></TD>
				<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[9]+total[10]+total[11]+total[12])%></font></TD>
				<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[13]+total[14])%></font></TD>
				<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[15]%></font></TD>
				<TD width="5%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[16]+total[17])%></font></TD>
			</TR>
		</TABLE>
 	</BODY>
</HTML>