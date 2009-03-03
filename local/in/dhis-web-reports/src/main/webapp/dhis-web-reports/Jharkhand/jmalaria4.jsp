
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
//	int selectedDataPeriodID = Integer.parseInt( selectedPeriodId );

  	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );

      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = 	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	String stateName = ""; 
  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String monthlyDataElements[] = {
	
											"DE For xxx",
												
  											"DE for Population",
  											
  											// Active BSC, BSE, +ve (pv + pf)
  											"MAL_DE6","MAL_DE7","MAL_DE8","MAL_DE9",

											// Mass Contact - Coll,Exam,PV,Pf
  											"MAL_DE14","MAL_DE15","MAL_DE16","MAL_DE17",
  											
  											// Passive BSC, BSE, +ve (pv + pf)
  											"MAL_DE10","MAL_DE11","MAL_DE12","MAL_DE13",
											
											// Age wise Positive - (0-1), (1-4), (5-14), (15+)
  											"MAL_DE28","MAL_DE29","MAL_DE30","MAL_DE31",  
  											
  											// RT done - (0-1), (1-4), (5-14), (15+)
	  										"MAL_DE37","MAL_DE38","MAL_DE39","MAL_DE40",
											
											// Deaths
											"MAL_DE41","MAL_DE42"
  											 
  											
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
                
//      rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
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
			    &#2346;&#2381;&#2352;&#2366;&#2341;&#2350;&#2367;&#2325; &#2360;&#2381;&#2357;&#2366;&#2360;&#2381;&#2341;&#2381;&#2351; &#2325;&#2375;&#2344;&#2381;&#2342;&#2381;&#2352; (&#2346;&#2381;&#2352;&#2366;&#2351;&#2350;&#2352;&#2368; 
        &#2361;&#2375;&#2354;&#2381;&#2341; &#2360;&#2375;&#2339;&#2381;&#2335;&#2352;, &#2346;&#2368;&#2319;&#2330;&#2360;&#2368;) &#2325;&#2368; &#2350;&#2366;&#2360;&#2367;&#2325; &#2350;&#2354;&#2375;&#2352;&#2367;&#2351;&#2366; 
        &#2325;&#2366;&#2352;&#2381;&#2351;&#2325;&#2381;&#2352;&#2350; &#2325;&#2368; &#2352;&#2367;&#2346;&#2379;&#2352;&#2381;&#2335;
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MF- 4</font></b></p>
        
        <table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2"><b>&#2352;&#2366;&#2332;&#2381;&#2351; :&nbsp;&nbsp;<%=stateName%></b></font>
    				</td>
    				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2"><b>&#2350;&#2366;&#2361; :&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%> </b></font>
    				</td>   
  			</tr>
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2"><b>&#2332;&#2367;&#2354;&#2366; :&nbsp;&nbsp;<%=selectedOrgUnitName%></b></font>
    				</td>
    				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2"><b>&#2332;&#2344;&#2360;&#2306;&#2326;&#2381;&#2351;&#2366; :&nbsp;&nbsp;<%=totPopulation%> </b></font>
    				</td>   
  			</tr>

		</table>

        
        
        
       
		
		<br>
		
		<%
			
			try
			  {
				  count = 0;
				  while(count < childOrgUnitCount)
				   { 				   
				    if(count%7==0)
				     {
				       if(count != 0) {%></table><div align="right"><font face="Arial" size="1"><i>(page contd.)</i></font></div> <br><%}%>
				     </table><br>
				     <TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  						<TR>
  							<TD align="center" width="2%" rowSpan=3><B><FONT face="Arial" size="1">&#2325;&#2381;&#2352;&#2350;&#2366;&#2306;&#2325;</FONT></B></TD>
   							<TD align="center" width="38%" rowSpan=3><b><font face="Arial" size="1">&#2346;&#2368;&#2319;&#2330;&#2360;&#2368;/&#2313;&#2346;&#2325;&#2375;&#2344;&#2381;&#2342;&#2381;&#2352; &#2325;&#2366; &#2344;&#2366;&#2350;</font></b></TD>
   							<TD align="center" width="2%" rowSpan=3><b><font face="Arial" size="1">&#2350;&#2354;&#2375;&#2352;&#2367;&#2351;&#2366; &#2325;&#2366; &#2352;&#2370;&#2346; (&#2310;&#2342;&#2367;&#2357;&#2366;&#2360;&#2368; &#2350;&#2354;&#2375; &#2346;&#2352;&#2367;&#2351;&#2379;&#2332;&#2344;&#2366; &#2350;&#2354;&#2375; &#2310;&#2342;&#2367;)</font></b></TD>
   							<TD align="center" width="2%" rowSpan=3><b><font face="Arial" size="1">&#2325;&#2369;&#2354; &#2332;&#2344;&#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</FONT></B></TD>
   							<TD align="center" width="8%" colspan="4"><b><font face="Arial" size="1">&#2360;&#2325;&#2381;&#2352;&#2367;&#2351; &#2344;&#2367;&#2327;&#2352;&#2366;&#2344;&#2368; (&#2352;&#2325;&#2381;&#2340; &#2346;&#2335;&#2381;&#2335;&#2367;&#2351;&#2366;&#2305; &#2351;&#2366; &#2360;&#2381;&#2354;&#2366;&#2311;&#2337;&#2375;)</FONT></B></TD>
   							<TD align="center" width="8%" colspan="4"><b><font face="Arial" size="1">&#2360;&#2366;&#2350;&#2370;&#2361;&#2367;&#2325;/&#2360;&#2350;&#2381;&#2346;&#2325;(&#2352;&#2325;&#2381;&#2340; &#2346;&#2335;&#2381;&#2335;&#2367;&#2351;&#2366;&#2305; &#2351;&#2366; &#2360;&#2381;&#2354;&#2366;&#2311;&#2337;&#2375;)</FONT></B></TD>
   							<TD align="center" width="8%" colspan="4"><b><font face="Arial" size="1">&#2344;&#2367;&#2359;&#2367;&#2325;&#2381;&#2352;&#2351; &#2344;&#2367;&#2327;&#2352;&#2366;&#2344;&#2366; (&#2352;&#2325;&#2381;&#2340; &#2346;&#2335;&#2381;&#2335;&#2367;&#2351;&#2366;&#2305; &#2351;&#2366; &#2360;&#2381;&#2354;&#2366;&#2311;&#2337;&#2375;)</FONT></B></TD>
   							<TD align="center" width="8%" colspan="4"><b><font face="Arial" size="1">&#2325;&#2369;&#2354; &#2332;&#2379;&#2396; (&#2352;&#2325;&#2381;&#2340; &#2346;&#2335;&#2381;&#2335;&#2367;&#2351;&#2366;&#2305; &#2351;&#2366; &#2360;&#2381;&#2354;&#2366;&#2311;&#2337;&#2375;)</FONT></B></TD>
   							<TD align="center" width="10%" colspan="4"><b><font face="Arial" size="1">&#2310;&#2351;&#2369; &#2325;&#2375; &#2309;&#2344;&#2369;&#2360;&#2366;&#2352; &#2343;&#2344;&#2366;&#2340;&#2381;&#2350;&#2325; &#2360;&#2381;&#2354;&#2366;&#2311;&#2337;&#2379;&#2306; &#2325;&#2366; &#2357;&#2367;&#2340;&#2352;&#2339;</FONT></B></TD>
   							<TD align="center" width="10%" colspan="4"><b><font face="Arial" size="1">&#2310;&#2350;&#2370;&#2354; &#2313;&#2346;&#2330;&#2366;&#2352; (&#2310;&#2352;&#2335;&#2368;)</FONT></B></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">&#2350;&#2354;&#2375;&#2352;&#2367;&#2351;&#2366; &#2360;&#2375; &#2361;&#2369;&#2312; &#2350;&#2371;&#2340;&#2381;&#2351;&#2369;</FONT></B></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">&#2354;&#2368; &#2327;&#2312;</FONT></B></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">&#2332;&#2366;&#2305;&#2330;&#2368; &#2327;&#2312;</FONT></B></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">&#2343;&#2344;&#2366;&#2340;&#2381;&#2350;&#2325;</FONT></B></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">&#2354;&#2368; &#2327;&#2312;</FONT></B></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">&#2332;&#2366;&#2305;&#2330;&#2368; &#2327;&#2312;</FONT></B></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">&#2343;&#2344;&#2366;&#2340;&#2381;&#2350;&#2325;</FONT></B></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">&#2354;&#2368; &#2327;&#2312;</FONT></B></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">&#2332;&#2366;&#2305;&#2330;&#2368; &#2327;&#2312;</FONT></B></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">&#2343;&#2344;&#2366;&#2340;&#2381;&#2350;&#2325;</font></b></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">&#2354;&#2368; &#2327;&#2312;</FONT></B></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">&#2332;&#2366;&#2305;&#2330;&#2368; &#2327;&#2312;</FONT></B></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">&#2343;&#2344;&#2366;&#2340;&#2381;&#2350;&#2325;</font></b></TD>
   							<TD align="center" width="4%" colspan="4"><b><font face="Arial" size="1">PV / PR</FONT></B></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">&#2319;&#2325; &#2357;&#2352;&#2381;&#2359; &#2360;&#2375; &#2344;&#2368;&#2330;&#2375;</font></b></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">1-4</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="1">5-15</font></b></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">15+</font></b></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">&#2350;&#2366;&#2311;&#2325;&#2381;&#2352;&#2379;&#2360;&#2381;&#2325;&#2379;&#2346;&#2367;&#2325; &#2332;&#2366;&#2305;&#2330; &#2342;&#2381;&#2357;&#2366;&#2352;&#2366; &#2344;&#2367;&#2358;&#2367;&#2330;&#2340; &#2346;&#2368;&#2319;&#2347;</FONT></B></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" size="1">&#2325;&#2375;&#2357;&#2354; &#2354;&#2325;&#2381;&#2359;&#2339; &#2310;&#2343;&#2366;&#2352;&#2367;&#2340;</FONT></B></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="2%"><b><font face="Arial" size="1">pv</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial" size="1">pf</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial" size="1">pv</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial" size="1">pf</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial" size="1">pv</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial" size="1">pf</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial" size="1">pv</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial" size="1">pf</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial" size="1">&#2319;&#2325; &#2357;&#2352;&#2381;&#2359; &#2360;&#2375; &#2344;&#2368;&#2330;&#2375;</FONT></B></TD>
   							<TD align="center" width="2%"><b><font face="Arial" size="1">1-4</FONT></B></TD>
   							<TD align="center" width="4%"><b><font face="Arial" size="1">5-15</FONT></B></TD>
   							<TD align="center" width="2%"><b><font face="Arial" size="1">15+</FONT></B></TD>
    					</TR>
    					<TR>
    						<TD width="2%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>1</b></i></FONT></TD>
    						<TD width="38%" HEIGHT="30" align="center"><b><i><font face="Arial" size="1">2</font></i></b></TD>
    						<TD width="2%" HEIGHT="30" align="center"><b><i><font face="Arial" size="1">3</font></i></b></TD>
    						<TD width="2%" HEIGHT="30" align="center"><b><i><font face="Arial" size="1">4</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>5</b></i></FONT></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">6</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center" colspan="2"><b><i><font face="Arial" size="1">7</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>8</b></i></FONT></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">9</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center" colspan="2"><b><i><font face="Arial" size="1">10</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>11</b></i></FONT></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">12</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center" colspan="2"><b><i><font face="Arial" size="1">13</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>14</b></i></FONT></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">15</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center" colspan="2"><b><i><font face="Arial" size="1">16</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>17</b></i></FONT></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">18</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">19</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">20</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>21</b></i></FONT></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">22</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">23</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">24</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>25</b></i></FONT></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">26</font></i></b></TD>
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
    					<TD width="2%" HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=(count+1)%></font></TD>
    					<TD width="38%" HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=childOrgUnitNames.get(count)%></font></TD>
    					<TD width="2%" HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[0]%></font></TD>
    					<TD width="2%" HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[1]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[2]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[3]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[4]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[5]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[6]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[7]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[8]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[9]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[10]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[11]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[12]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[13]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=(tempval[2]+tempval[6]+tempval[10])%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=(tempval[3]+tempval[7]+tempval[11])%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=(tempval[4]+tempval[8]+tempval[12])%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=(tempval[5]+tempval[9]+tempval[13])%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[14]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[15]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[16]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[17]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[18]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[19]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[20]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[21]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[22]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[23]%></font></TD>    					
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
				<TD width="40%" HEIGHT="50" align="center" colspan="2"><FONT face="Arial" size="2"><b>&#2325;&#2369;&#2354;</b></font></TD>
				<TD width="2%" HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[0]%></font></TD>
				<TD width="2%" HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[1]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[2]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[3]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[4]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[5]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[6]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[7]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[8]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[9]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[10]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[11]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[12]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[13]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(total[2]+total[6]+total[10])%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(total[3]+total[7]+total[11])%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(total[4]+total[8]+total[12])%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(total[5]+total[9]+total[13])%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[14]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[15]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[16]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[17]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[18]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[19]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[20]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[21]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[22]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[23]%></font></TD>
			</TR>
		</TABLE>
 	</BODY>
</HTML>