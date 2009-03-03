
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
    
    // For State name
    Statement st5=null;
    ResultSet rs5=null;
    

  
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
  	String stateName = ""; 
  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String monthlyDataElements[] = {
										// Fever Cases (Male + Female)
  										"MAL_DE2","MAL_DE3",
  										
  										// B.S Collected (Active + Passive)
  										"MAL_DE6","MAL_DE10",
  										
  										// +ve (Male + Female)
  										"MAL_DE26","MAL_DE27",
  										
  										// Pv
  										"MAL_DE32",
  										
  										// Pf- R,Rg 
  										"MAL_DE33","MAL_DE34",
  										
  										// Pm
  										"MAL_DE52",
  										
  										// Mixed
  										"MAL_DE35",
  										
  										// Balance = Active BSC + Passive BSC - Active BSE - Passive BSE
  										"MAL_DE7","MAL_DE11"
  										
  										
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
        st5=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

        //rs1 = st1.executeQuery("SELECT organisationunit.shortname, datavalue.value FROM dataelement INNER JOIN (organisationunit INNER JOIN datavalue ON organisationunit.id = datavalue.source) ON dataelement.id = datavalue.dataElement WHERE datavalue.source="+selectedOrgUnitID+" AND dataelement.name like 'Total Population'");
        rs1 = st1.executeQuery("SELECT organisationunit.shortname, datavalue.value FROM dataelement INNER JOIN (organisationunit INNER JOIN datavalue ON organisationunit.organisationunitid = datavalue.sourceid) ON dataelement.dataelementid = datavalue.dataelementid WHERE datavalue.sourceid="+selectedOrgUnitID+" AND dataelement.name like 'Total Population'");        
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);    totPopulation = rs1.getInt(2);  }
        else  {  selectedOrgUnitName = "";          totPopulation = 0;    }  
                
 //       rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	    if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	}  

		selectedDataPeriodStartDate = startingDate;

		//rs5=st5.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
		rs5=st5.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")"); 
		if(rs5.next()) {  stateName = rs5.getString(2);}
		else { stateName = "";}  
				
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
			<%=selectedOrgUnitName%> &nbsp;&nbsp;&nbsp;	
				&#2350;&#2354;&#2375;&#2352;&#2367;&#2351;&#2366; &#2325;&#2306;&#2335;&#2381;&#2352;&#2379;&#2354; &#2360;&#2379;&#2360;&#2366;&#2312;&#2335;&#2368;<BR>
				&#2346;&#2381;&#2352;&#2366;&#2341;&#2350;&#2367;&#2325; &#2360;&#2381;&#2357;&#2366;&#2360;&#2341;&#2381;&#2351; &#2325;&#2375;&#2344;&#2381;&#2342;&#2381;&#2352; (&#2346;&#2381;&#2352;&#2366;&#2311;&#2350;&#2352;&#2368; &#2361;&#2376;&#2354;&#2381;&#2341; &#2360;&#2375;&#2344;&#2381;&#2335;&#2352;&#2379;&#2306;) &#2360;&#2375; &#2349;&#2375;&#2332;&#2366; &#2332;&#2366;&#2344;&#2375; &#2357;&#2366;&#2354;&#2366; &#2348;&#2330;&#2352;-&#2340;&#2366;&#2352; (&#2360;&#2375;&#2357;&#2367;&#2306;&#2327;&#2381;&#2352;&#2366;&#2350;) 
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MF- 11
			</font>	
	</b><br></p>
	
	
	<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  		<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    		<td width="40%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"><b>&#2352;&#2366;&#2332;&#2381;&#2351;&nbsp;:&nbsp;&nbsp;<%=stateName%></b></font></td>
		<td width="30%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">	&nbsp;</td>
		<td width="30%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">	&nbsp;</td>   
	</tr>  		
		<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    		<td width="40%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
    			<font face="Arial" size="2"><b>&#2332;&#2367;&#2354;&#2366; :&nbsp;&nbsp;<%=selectedOrgUnitName%></b></font>
    	</td>
    		<td width="30%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
     				<font face="Arial" size="2"><b>&#2332;&#2344;&#2360;&#2306;&#2326;&#2381;&#2351;&#2366; :&nbsp;&nbsp;<%=totPopulation%></b></font>
    	</td>
    		<td width="30%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
     				<font face="Arial" size="2"><b>&#2350;&#2366;&#2361; :&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%> </b></font>
    	</td>   
  	</tr>
</table>
			
		
		<%
			
			try
			  {
				  count = 0;
				  while(count < childOrgUnitCount)
				   { 				   
				    if(count%14==0)
				     {
				       if(count != 0) {%></table><div align="right"><font face="Arial" size="1"><i>(page contd.)</i></font></div> <br><%}%>
				     </table><br>
				     <TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  						<TR>
  							<TD align="center" width="3%" rowSpan=3><B><FONT face="Arial" size="2">&#2325;&#2381;&#2352;&#2350;&#2366;&#2306;&#2325;</FONT></B></TD>
   							<TD align="center" width="43%" rowSpan=3><B><FONT face="Arial" size="2">&#2313;&#2346;&#2325;&#2375;&#2306;&#2342;&#2381;&#2352; &#2325;&#2366; &#2344;&#2366;&#2350;</FONT></B></TD>
   							<TD align="center" width="6%" rowspan="3"><B><FONT face="Arial" size="2">&#2348;&#2369;&#2326;&#2366;&#2352; &#2325;&#2366; &#2311;&#2354;&#2366;&#2332;</FONT></B></TD>
   							<TD align="center" width="6%" rowspan="3"><B><FONT face="Arial" size="2">&#2354;&#2367;&#2319; &#2327;&#2319; &#2352;&#2325;&#2381;&#2340; &#2354;&#2375;&#2346;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</FONT></B></TD>
   							<TD align="center" width="6%" rowspan="3"><B><FONT face="Arial" size="2">&#2343;&#2344;&#2366;&#2340;&#2381;&#2350;&#2325;<br>(&#2346;&#2379;&#2332;&#2367;&#2335;&#2367;&#2357;)</FONT></B></TD>
   							<TD align="center" width="30%" colspan="5"><B><FONT face="Arial" size="2">&#2350;&#2354;&#2375;&#2352;&#2367;&#2351;&#2366; &#2346;&#2352;&#2332;&#2368;&#2357;&#2368; &#2332;&#2366;&#2340;&#2367;</FONT></B></TD>
   							<TD align="center" width="6%" rowspan="3"><B><FONT face="Arial" size="2">&#2348;&#2325;&#2366;&#2351;&#2366; &#2352;&#2325;&#2381;&#2340;&#2354;&#2375;&#2346;</FONT></B></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="6%" rowspan="2"><B><FONT face="Arial" size="2">&#2346;&#2368;&#2357;&#2368;</FONT></B></TD>
   							<TD align="center" width="6%" colspan="2"><B><FONT face="Arial" size="2">&#2346;&#2368; &#2319;&#2347;</FONT></B></TD>
   							<TD align="center" width="6%" rowspan="2"><B><FONT face="Arial" size="2">&#2346;&#2368; &#2319;&#2350;</FONT></B></TD>
   							<TD align="center" width="6%" rowspan="2"><B><FONT face="Arial" size="2">&#2350;&#2367;&#2358;&#2381;&#2352;&#2367;&#2340;</FONT></B></TD>
   						</TR>  		
  						<TR>
    						<TD align="center" width="6%"><B><FONT face="Arial" size="2">&#2310;&#2352;</FONT></B></TD>
    						<TD align="center" width="6%"><B><FONT face="Arial" size="2">&#2310;&#2352; &#2332;&#2368;</FONT></B></TD>
    					</TR>
    					<TR>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>1</b></i></FONT></TD>
    						<TD width="43%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>2</b></i></FONT></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>3</b></i></FONT></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>4</b></i></FONT></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>5</b></i></FONT></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>6</b></i></FONT></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>7</b></i></FONT></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>8</b></i></FONT></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>9</b></i></FONT></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>10</b></i></FONT></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>11</b></i></FONT></TD>
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


						
						//rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (select id from organisationunit where parent ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'");
						rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'");

						//SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period ="+selectedDataPeriodID+" AND datavalue.source in (select id from organisationunit where parent ="+currentChildID+") AND dataelement.code like '"+monthlyDataElements[i]+"'");
						//SELECT datavalue.value FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period="+selectedDataPeriodID+" AND datavalue.source="+currentChildID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");
						//SELECT sum(datavalue.value) FROM (organisationunit INNER JOIN datavalue ON organisationunit.id = datavalue.source) INNER JOIN period ON datavalue.period = period.id WHERE organisationunit.parent="+selectedOrgUnitID+" AND period.startDate='"+selectedDataPeriodStartDate+"' AND periodType="+periodTypeID+" AND datavalue.dataElement="+dataElementIDs[i])
						
						
						if(!rs4.next())  {  tempval[i] = 0;	 }
						else   {  tempval[i] = rs4.getInt(1);  }
						total[i] += tempval[i];
					 } 	
			   
				   %>			     				  		             		
  					
  					<TR>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(count+1)%></font></TD>
    					<TD width="43%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=childOrgUnitNames.get(count)%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[0]+tempval[1])%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[2]+tempval[3])%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[4]+tempval[5])%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[6]%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[7]%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[8]%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[9]%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[10]%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[2]+tempval[3]-tempval[11]-tempval[12])%></font></TD>
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
  					if(rs5!=null)  rs5.close();		if(st5!=null)  st5.close();				
  				
					if(con!=null)  con.close();
				 }
				catch(Exception e)   {  out.println(e.getMessage());   }
       		 } // finally block end		          		
  		%>

			<TR>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1">&nbsp;</font></TD>
				<TD width="43%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><b>&#2325;&#2369;&#2354;</b></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[0]+total[1])%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[2]+total[3])%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[4]+total[5])%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[6]%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[7]%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[8]%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[9]%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[10]%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[2]+total[3]-total[11]-total[12])%></font></TD>
			</TR>
		</TABLE>
 	</BODY>
</HTML>