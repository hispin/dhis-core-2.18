
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
  
   // For finding datavalues 
    Statement st3=null;
    ResultSet rs3=null;

    // For finding State Name based on selecte organisationunit id
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
  	
  	String stateName = "";
  	String districtName = "";
  	int districtID = 0;
  	
  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String monthlyDataElements[] = {  											
  											//Hypertension  											
  											"NC_DE1","NC_DE29", // col 3
  											"NC_DE2","NC_DE30", // col 4
  											"NC_DE57", // col 6
  											"NC_DE58", // col 7
  											"NC_DE1", // col 9
  											"NC_DE2", // col 10
  											"NC_DE29", // col 12
  											"NC_DE30", // col 13
  											"NC_DE57", // col 18
  											"NC_DE58", // col 19
  											
  											// Ischemic Heart Diseases  											
  											"NC_DE3","NC_DE31", // col 3
  											"NC_DE4","NC_DE32", // col 4
  											"NC_DE59", // col 6
  											"NC_DE60", // col 7
  											"NC_DE3", // col 9
  											"NC_DE4", // col 10
  											"NC_DE31", // col 12
  											"NC_DE32", // col 13
  											"NC_DE59", // col 18
  											"NC_DE60", // col 19

											// Cerebro Vascular Accident
											"NC_DE5","NC_DE33", // col 3
  											"NC_DE6","NC_DE34", // col 4
  											"NC_DE61", // col 6
  											"NC_DE62", // col 7
  											"NC_DE5", // col 9
  											"NC_DE6", // col 10
  											"NC_DE33", // col 12
  											"NC_DE34", // col 13
  											"NC_DE61", // col 18
  											"NC_DE62", // col 19

											// Other Neurological Disorders
											"NC_DE7","NC_DE35", // col 3
  											"NC_DE8","NC_DE36", // col 4
  											"NC_DE63", // col 6
  											"NC_DE64", // col 7
  											"NC_DE7", // col 9
  											"NC_DE8", // col 10
  											"NC_DE35", // col 12
  											"NC_DE36", // col 13
  											"NC_DE63", // col 18
  											"NC_DE64", // col 19

											// Diabetes Mellitus Type - 1
											"NC_DE9","NC_DE37", // col 3
  											"NC_DE10","NC_DE38", // col 4
  											"NC_DE65", // col 6
  											"NC_DE66", // col 7
  											"NC_DE9", // col 9
  											"NC_DE10", // col 10
  											"NC_DE37", // col 12
  											"NC_DE38", // col 13
  											"NC_DE65", // col 18
  											"NC_DE66", // col 19

											// Diabetes Mellitus Type - 2
											"NC_DE11","NC_DE39", // col 3
  											"NC_DE12","NC_DE40", // col 4
  											"NC_DE67", // col 6
  											"NC_DE68", // col 7
  											"NC_DE11", // col 9
  											"NC_DE12", // col 10
  											"NC_DE39", // col 12
  											"NC_DE40", // col 13
  											"NC_DE67", // col 18
  											"NC_DE68", // col 19
  											
											// Bronchitis
											"NC_DE13","NC_DE41", // col 3
  											"NC_DE14","NC_DE42", // col 4
  											"NC_DE69", // col 6
  											"NC_DE70", // col 7
  											"NC_DE13", // col 9
  											"NC_DE14", // col 10
  											"NC_DE41", // col 12
  											"NC_DE42", // col 13
  											"NC_DE69", // col 18
  											"NC_DE70", // col 19

											// Emphysemas
											"NC_DE15","NC_DE43", // col 3
  											"NC_DE16","NC_DE44", // col 4
  											"NC_DE71", // col 6
  											"NC_DE72", // col 7
  											"NC_DE15", // col 9
  											"NC_DE16", // col 10
  											"NC_DE43", // col 12
  											"NC_DE44", // col 13
  											"NC_DE71", // col 18
  											"NC_DE72", // col 19

											// Asthma
											"NC_DE17","NC_DE45", // col 3
  											"NC_DE18","NC_DE46", // col 4
  											"NC_DE73", // col 6
  											"NC_DE74", // col 7
  											"NC_DE17", // col 9
  											"NC_DE18", // col 10
  											"NC_DE45", // col 12
  											"NC_DE46", // col 13
  											"NC_DE73", // col 18
  											"NC_DE74", // col 19

											// Common Mental Disorders
											"NC_DE19","NC_DE47", // col 3
  											"NC_DE20","NC_DE48", // col 4
  											"NC_DE75", // col 6
  											"NC_DE76", // col 7
  											"NC_DE19", // col 9
  											"NC_DE20", // col 10
  											"NC_DE47", // col 12
  											"NC_DE48", // col 13
  											"NC_DE75", // col 18
  											"NC_DE76", // col 19

											// Severe Mental Disorders
											"NC_DE21","NC_DE49", // col 3
  											"NC_DE22","NC_DE50", // col 4
  											"NC_DE77", // col 6
  											"NC_DE78", // col 7
  											"NC_DE21", // col 9
  											"NC_DE22", // col 10
  											"NC_DE49", // col 12
  											"NC_DE50", // col 13
  											"NC_DE77", // col 18
  											"NC_DE78", // col 19

											// Accidental Injuries
											"NC_DE23","NC_DE51", // col 3
  											"NC_DE24","NC_DE52", // col 4
  											"NC_DE79", // col 6
  											"NC_DE80", // col 7
  											"NC_DE23", // col 9
  											"NC_DE24", // col 10
  											"NC_DE51", // col 12
  											"NC_DE52", // col 13
  											"NC_DE79", // col 18
  											"NC_DE80", // col 19

											// Cancer
											"NC_DE25","NC_DE53", // col 3
  											"NC_DE26","NC_DE54", // col 4
  											"NC_DE81", // col 6
  											"NC_DE82", // col 7
  											"NC_DE25", // col 9
  											"NC_DE26", // col 10
  											"NC_DE53", // col 12
  											"NC_DE54", // col 13
  											"NC_DE81", // col 18
  											"NC_DE82", // col 19

											// Snake Bite
											"NC_DE27","NC_DE55", // col 3
  											"NC_DE28","NC_DE56", // col 4
  											"NC_DE83", // col 6
  											"NC_DE84", // col 7
  											"NC_DE27", // col 9
  											"NC_DE28", // col 10
  											"NC_DE55", // col 12
  											"NC_DE56", // col 13
  											"NC_DE83", // col 18
  											"NC_DE84"  // col 19  											
									};
	
	String nonCommDisNames[] = {
								"&#2325;&#2366;&#2352;&#2381;&#2337;&#2367;&#2351;&#2379; &#2349;&#2366;&#2360;&#2381;&#2325;&#2369;&#2354;&#2352; &#2352;&#2379;&#2327;&#2368; ",
								"&#2361;&#2366;&#2312;&#2346;&#2352;&#2335;&#2375;&#2344;&#2360;&#2344;",
								"&#2311;&#2360;&#2375;&#2350;&#2367;&#2325; &#2361;&#2381;&#2352;&#2381;&#2342;&#2351; &#2352;&#2379;&#2327;",
								"&#2344;&#2381;&#2351;&#2369;&#2352;&#2379;&#2354;&#2379;&#2332;&#2367;&#2325;&#2354; &#2352;&#2379;&#2327;",
								"&#2360;&#2375;&#2352;&#2375;&#2348;&#2352;&#2379; &#2349;&#2366;&#2360;&#2381;&#2325;&#2369;&#2354;&#2352; &#2342;&#2369;&#2328;&#2352;&#2381;&#2335;&#2344;&#2366; ",
								"&#2309;&#2344;&#2381;&#2351; &#2344;&#2381;&#2351;&#2369;&#2352;&#2379;&#2354;&#2379;&#2332;&#2367;&#2325;&#2354; &#2337;&#2367;&#2360; &#2310;&#2352;&#2381;&#2337;&#2352;",
								"&#2337;&#2366;&#2351;&#2348;&#2367;&#2335;&#2367;&#2332;",
								"&#2335;&#2366;&#2312;&#2346; 1",
								"&#2335;&#2366;&#2312;&#2346; 2",
								"&#2347;&#2375;&#2347;&#2337;&#2375;",
								"&#2348;&#2381;&#2352;&#2379;&#2344;&#2325;&#2366;&#2311;&#2335;&#2367;&#2360;",
								"&#2311;&#2350;&#2381;&#2347;&#2368;&#2332;&#2350;&#2366;&#2360;",
								"&#2342;&#2350;&#2366;",
								"&#2350;&#2366;&#2344;&#2360;&#2367;&#2325; &#2309;&#2360;&#2306;&#2340;&#2369;&#2354;&#2344;",
								"&#2360;&#2366;&#2343;&#2366;&#2352;&#2339; &#2350;&#2366;&#2344;&#2360;&#2367;&#2325; &#2309;&#2360;&#2306;&#2340;&#2369;&#2354;&#2344;",
								"&#2328;&#2366;&#2340;&#2325; &#2350;&#2366;&#2344;&#2360;&#2367;&#2325; &#2309;&#2360;&#2306;&#2340;&#2369;&#2354;&#2344;",
								"&#2342;&#2369;&#2328;&#2352;&#2381;&#2335;&#2344;&#2366; &#2327;&#2381;&#2352;&#2360;&#2381;&#2340; &#2330;&#2379;&#2335;",
								"&#2325;&#2376;&#2306;&#2360;&#2352;",
								"&#2360;&#2352;&#2381;&#2346;&#2342;&#2306;&#2358;",
								"&#2351;&#2379;&#2327;"
							};	
							
	String nonCommDisNo[] = {
								"1.","1.1","1.2","2.","2.1","2.2","3.","3.1","3.2","4.","4.1","4.2","4.3","5.","5.1","5.2","6.","7.","8."," "
							};	
								
	List childOrgUnitIDs = new ArrayList();	
	List childOrgUnitNames = new ArrayList();	
		
  	int childOrgUnitCount = 0;  	
  	int count = 0;  	
  	int i=0;
  	int totPopulation = 0;
  	  	
	int tempval[] = new int[16];
	int total[] = new int[16];
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

        //rs1 = st1.executeQuery("SELECT organisationunit.shortname from organisationunit where id="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname from organisationunit where organisationunitid="+selectedOrgUnitID);        
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);   }
        else  {  selectedOrgUnitName = "";          totPopulation = 0;    }  
                
//        rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	    if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	}  		

		selectedDataPeriodStartDate = startingDate;
	    
	   		
   	 	//rs4=st4.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
   	 	rs4=st4.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")"); 
		if(rs4.next()) {  stateName = rs4.getString(2);}
		else { stateName = "";}  
			    		 	  	 
	  } // try block end
    catch(Exception e)  { out.println(e.getMessage());  }
        
	String partsOfDataPeriodStartDate[]  =  selectedDataPeriodStartDate.split("-");
    int lastYear  = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;
	String lastDataPeriodStartDate = lastYear+"-"+partsOfDataPeriodStartDate[1]+"-"+partsOfDataPeriodStartDate[2];
     	
	int tempForMonth1 = Integer.parseInt(partsOfDataPeriodStartDate[1]);
	int tempForYear = 0;
	 	
    if(tempForMonth1 < 4)   	{   tempForYear = lastYear;  }
 	else  {   tempForYear = lastYear + 1;   	}
    
    String tempForMonth2 = "";
    if(tempForMonth1-1 ==0) tempForMonth2 = "-"+(tempForMonth1-1)+"-01";
    else if(tempForMonth1-1 <= 9) {tempForMonth2 = "-0"+(tempForMonth1-1)+"-01";}
    else tempForMonth2 = "-"+(tempForMonth1-1)+"-01";  	
 
 	String curYearStart = tempForYear+"-04-01";
 	String lastYearStart = (tempForYear-1)+"-04-01";
 	String lastYearEnd = lastYear+"-"+partsOfDataPeriodStartDate[1]+"-"+partsOfDataPeriodStartDate[2];
 	String curYearEnd = ""+partsOfDataPeriodStartDate[0]+""+tempForMonth2;
 	
 	String query="";		     
%>

<html>
	<head>
		<title>Monthly Report on Non-Communicable Diseases</title>		
	</head>
	<body>		
		<center>
			<font face="arial" size="3"><b>
 				&#2327;&#2376;&#2352;-&#2360;&#2306;&#2330;&#2366;&#2352;&#2368; &#2352;&#2379;&#2327; &#2325;&#2366; &#2350;&#2366;&#2360;&#2367;&#2325; &#2360;&#2306;&#2325;&#2354;&#2367;&#2340; &#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2344; ( &#2354;&#2367;&#2306;&#2327;&#2366;&#2344;&#2369;&#2360;&#2366;&#2352; &#2310;&#2325;&#2381;&#2352;&#2366;&#2306;&#2340; &#2319;&#2357;&#2306; &#2350;&#2372;&#2340;&#2381;&#2351;&#2369;) &#2332;&#2367;&#2354;&#2366; /&#2346;&#2381;&#2352;&#2326;&#2339;&#2381;&#2337; &#2325;&#2366;
 			</b></font> 
		</center>
		<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2">1.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2332;&#2367;&#2354;&#2366; &#2325;&#2366; &#2344;&#2366;&#2350; :&nbsp;&nbsp;<%=selectedOrgUnitName%></font>
    			</td>
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2">2.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2319;&#2357;&#2306; &#2357;&#2352;&#2381;&#2359; :&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%> </font>
    			</td>   
  			</tr>
 			<tr>
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       				<font face="Arial" size="2">3.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2346;&#2381;&#2352;&#2326;&#2339;&#2381;&#2337; &#2360;&#2381;&#2340;&#2381;&#2340;&#2352; &#2346;&#2352; &#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2325;&#2352;&#2344;&#2375; &#2357;&#2366;&#2354;&#2375; &#2360;&#2306;&#2360;&#2381;&#2341;&#2366;&#2344; &#2325;&#2368; &#2325;&#2369;&#2354; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;:</font>
    			</td>
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       				<font face="Arial" size="2">4.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2350;&#2375;&#2306; &#2346;&#2381;&#2352;&#2326;&#2339;&#2381;&#2337; &#2360;&#2381;&#2340;&#2381;&#2340;&#2352; &#2346;&#2352; &#2327;&#2376;&#2352;-&#2360;&#2306;&#2330;&#2366;&#2352;&#2368; &#2352;&#2379;&#2327; &#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2344; &#2313;&#2346;&#2360;&#2381;&#2341;&#2366;&#2346;&#2367;&#2340; &#2325;&#2352;&#2344;&#2375; &#2357;&#2366;&#2354;&#2379;&#2306; &#2325;&#2368; &#2325;&#2369;&#2354; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;&nbsp;&nbsp; :</font>
    			</td>
  			</tr>  		  		
  		</table>  
		<br>
		<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
  			<tr>
    				<td width="2%"  align="center" rowspan="3"><font face="arial" size="2"><b>&#2325;&#2381;&#2352;&#2350; &#2360;&#2306;.</b></font></td>
    				<td width="18%" align="center"  rowspan="3"><font face="arial" size="2"><b>&#2327;&#2376;&#2352;-&#2360;&#2306;&#2330;&#2366;&#2352;&#2368; &#2352;&#2379;&#2327;&#2379;&#2306; &#2325;&#2366; &#2346;&#2381;&#2352;&#2325;&#2372;&#2340;&#2367;/ &#2360;&#2350;&#2370;&#2361;</td>
    				<td width="20%" align="center"  colspan="6"><font face="arial" size="2"><b>&#2327;&#2340; &#2350;&#2366;&#2361; &#2340;&#2325; &#2352;&#2379;&#2327;&#2379;&#2306; &#2319;&#2357;&#2306; &#2350;&#2372;&#2340;&#2381;&#2351;&#2369; &#2325;&#2368; &#2325;&#2369;&#2354; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</b></font></td>
    				<td width="10%" align="center"  colspan="3"><font face="arial" size="2"><b>&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2350;&#2375;&#2306; &#2348;&#2366;&#2361;&#2381;&#2351; &#2352;&#2379;&#2327;&#2367;&#2351;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</b></font></td>
    				<td width="10%" align="center"  colspan="3"><font face="arial" size="2"><b>&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2350;&#2375;&#2306; &#2309;&#2306;&#2340;: &#2352;&#2379;&#2327;&#2367;&#2351;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</b></font></td>
    				<td width="10%" align="center"  colspan="3"><font face="arial" size="2"><b>&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2350;&#2375;&#2306; &#2325;&#2369;&#2354; &#2352;&#2379;&#2327;&#2368;&#2351;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</b></font></td>
    				<td width="10%" align="center"  colspan="3"><font face="arial" size="2"><b>&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2350;&#2375;&#2306; &#2350;&#2371;&#2340;&#2381;&#2351; &#2352;&#2379;&#2327;&#2368;&#2351;&#2379;&#2306; &#2325;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</b></font></td>
    				<td width="20%" align="center"  colspan="6"><font face="arial" size="2"><b>&#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2350;&#2366;&#2361; &#2325;&#2375; &#2309;&#2306;&#2340; &#2350;&#2375;&#2306; &#2346;&#2381;&#2352;&#2340;&#2367;&#2357;&#2375;&#2342;&#2367;&#2340; &#2352;&#2379;&#2327;&#2379;&#2306; &#2319;&#2357;&#2306; &#2350;&#2372;&#2340;&#2381;&#2351;&#2369; &#2325;&#2368; &#2325;&#2369;&#2354; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</b></font></td>
  			</tr>
  			<tr>
    				<td width="10%"  align="center"  colspan="3"><font face="arial" size="2"><b>&#2352;&#2379;&#2327;&#2368;</b></font></td>
    				<td width="10%"  align="center"  colspan="3"><font face="arial" size="2"><b>&#2350;&#2372;&#2340;&#2381;&#2351;&#2369; </b></font></td>
    				<td width="10%"  align="center" colspan="3"><font face="arial" size="2"><b>&#2344;&#2351;&#2375; &#2352;&#2379;&#2327;&#2368;</b></font></td>
    				<td width="10%"  align="center" colspan="3"><font face="arial" size="2"><b>&#2344;&#2351;&#2375; &#2352;&#2379;&#2327;&#2368;</b></font></td>
    				<td width="10%"  align="center" colspan="3"><font face="arial" size="2"><b>&#2352;&#2379;&#2327;&#2368;</b></font></td>
    				<td width="10%"  align="center" colspan="3"><font face="arial" size="2"><b>&nbsp;</b></font></td>
    				<td width="10%"  align="center" colspan="3"><font face="arial" size="2"><b>&#2352;&#2379;&#2327;&#2368;</b></font></td>
    				<td width="10%"  align="center" colspan="3"><font face="arial" size="2"><b>&#2350;&#2372;&#2340;&#2381;&#2351;&#2369;</b></font></td>
  			</tr>
  			<tr>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2346;&#2369;.</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2350;.</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="2"><b>&#2351;&#2379;&#2327; </b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2346;&#2369;.</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2350;.</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="2"><b>&#2351;&#2379;&#2327; </b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2346;&#2369;.</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2350;.</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="2"><b>&#2351;&#2379;&#2327; </b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2346;&#2369;.</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2350;.</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="2"><b>&#2351;&#2379;&#2327; </b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2346;&#2369;.</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2350;.</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="2"><b>&#2351;&#2379;&#2327; </b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2346;&#2369;.</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2350;.</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="2"><b>&#2351;&#2379;&#2327; </b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2346;&#2369;.</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2350;.</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="2"><b>&#2351;&#2379;&#2327; </b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2346;&#2369;.</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="2"><b>&#2350;.</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="2"><b>&#2351;&#2379;&#2327; </b></font></td>
  			</tr>
  			<tr>
    			<td width="2%"  align="center" rowspan="2"><font face="arial" size="1"><b>1</b></font></td>
    			<td width="18%"  align="center" rowspan="2"><font face="arial" size="1"><b>2</b></font></td>
    			<td width="3%"  align="center" rowspan="2"><font face="arial" size="1"><b>3</b></font></td>
    			<td width="3%"  align="center" rowspan="2"><font face="arial" size="1"><b>4</b></font></td>
    			<td width="4%"  align="center" rowspan="2"><font face="arial" size="1"><b>5</b></font></td>
    			<td width="3%"  align="center" rowspan="2"><font face="arial" size="1"><b>6</b></font></td>
    			<td width="3%"  align="center" rowspan="2"><font face="arial" size="1"><b>7</b></font></td>
    			<td width="4%"  align="center" rowspan="2"><font face="arial" size="1"><b>8</b></font></td>
    			<td width="3%"  align="center" rowspan="2"><font face="arial" size="1"><b>9</b></font></td>
    			<td width="3%"  align="center" rowspan="2"><font face="arial" size="1"><b>10</b></font></td>
    			<td width="4%"  align="center" rowspan="2"><font face="arial" size="1"><b>11</b></font></td>
    			<td width="3%"  align="center" rowspan="2"><font face="arial" size="1"><b>12</b></font></td>
    			<td width="3%"  align="center" rowspan="2"><font face="arial" size="1"><b>13</b></font></td>
    			<td width="4%"  align="center" rowspan="2"><font face="arial" size="1"><b>14</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="1"><b>15</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="1"><b>16</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="1"><b>17</b></font></td>
    			<td width="3%" align="center" rowspan="2"><font face="arial" size="1"><b>18</b></font></td>
    			<td width="3%"  align="center" rowspan="2"><font face="arial" size="1"><b>19</b></font></td>
    			<td width="4%" align="center"  rowspan="2"><font face="arial" size="1"><b>20</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="1"><b>21</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="1"><b>22</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="1"><b>23</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="1"><b>24</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="1"><b>25</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="1"><b>26</b></font></td>
  			</tr>
  			<tr>
    			<td width="3%" align="center" ><font face="arial" size="1"><b>9+12</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="1"><b>10+13</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="1"><b>11+14</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="1"><b>3+15</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="1"><b>4+16</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="1"><b>5+17</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="1"><b>6+18</b></font></td>
    			<td width="3%" align="center" ><font face="arial" size="1"><b>7+19</b></font></td>
    			<td width="4%" align="center" ><font face="arial" size="1"><b>8+20</b></font></td>
  			</tr>
  			
  			<%			
				try
			  	{
				  count = 0;
				  int k=0;
				  while(count < 19)
				   { 				   
				    if(count==0 || count ==3 || count ==6 || count==9 || count==13)
				     {%>
				     
				     	<tr>
    						<td width="2%"><font face="arial" size="2"><%=nonCommDisNo[count]%></font>&nbsp;</td>
    						<td width="98%" colspan="25"><font face="arial" size="1"><%=nonCommDisNames[count]%></font>&nbsp;</td>
  						</tr>				     
				     <%}
				    else
				     {				     	    				     	   				     	   
				     		for(i=0;i<12;i++)
					 	 	{
					 	 	   	if(i>=0 && i<=5)
					 	 	      	 //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+curYearStart+"' and '"+curYearEnd+"' and periodType = "+periodTypeID+") AND datavalue.source in (select id from organisationunit where parent ="+selectedOrgUnitID+") AND dataelement.code like '"+monthlyDataElements[k]+"'";
					 	 	      	 query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+curYearEnd+"' and periodtypeid = "+periodTypeID+") AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+") AND dataelement.code like '"+monthlyDataElements[k]+"'";
					 	 	   	else 
					 	 	   		//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (select id from organisationunit where parent ="+selectedOrgUnitID+") AND dataelement.code like '"+monthlyDataElements[k]+"'";
					 	 	   		query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+") AND dataelement.code like '"+monthlyDataElements[k]+"'";
										   
							   	rs3 = st3.executeQuery(query);																		
								if(!rs3.next())  {  tempval[i] = 0;	 }
								else   {  tempval[i] = rs3.getInt(1);  }
								total[i] += tempval[i];
								k++;
					 	 	} // for loop end											     						     						     
				     	%>
				     
				     	<tr>
    						<td width="2%"><font face="arial" size="2"><%=nonCommDisNo[count]%></font>&nbsp;</td>
   	 						<td width="18%"><font face="arial" size="1"><%=nonCommDisNames[count]%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=(tempval[0]+tempval[1])%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=(tempval[2]+tempval[3])%></font>&nbsp;</td>
    						<td width="4%" align="center" ><font face="arial" size="2"><%=(tempval[0]+tempval[1]+tempval[2]+tempval[3])%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=tempval[4]%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=tempval[5]%></font>&nbsp;</td>
    						<td width="4%" align="center" ><font face="arial" size="2"><%=(tempval[4]+tempval[5])%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=tempval[6]%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=tempval[7]%></font>&nbsp;</td>
    						<td width="4%" align="center" ><font face="arial" size="2"><%=(tempval[6]+tempval[7])%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=tempval[8]%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=tempval[9]%></font>&nbsp;</td>
    						<td width="4%" align="center" ><font face="arial" size="2"><%=(tempval[8]+tempval[9])%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=(tempval[6]+tempval[8])%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=(tempval[7]+tempval[9])%></font>&nbsp;</td>
    						<td width="4%" align="center" ><font face="arial" size="2"><%=(tempval[6]+tempval[7]+tempval[8]+tempval[9])%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=tempval[10]%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=tempval[11]%></font>&nbsp;</td>
   	 						<td width="4%" align="center" ><font face="arial" size="2"><%=(tempval[10]+tempval[11])%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=(tempval[0]+tempval[1]+tempval[6]+tempval[8])%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=(tempval[2]+tempval[3]+tempval[7]+tempval[9])%></font>&nbsp;</td>
    						<td width="4%" align="center" ><font face="arial" size="2"><%=(tempval[0]+tempval[1]+tempval[2]+tempval[3]+tempval[6]+tempval[7]+tempval[8]+tempval[9])%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=(tempval[4]+tempval[10])%></font>&nbsp;</td>
    						<td width="3%" align="center" ><font face="arial" size="2"><%=(tempval[5]+tempval[11])%></font>&nbsp;</td>
    						<td width="4%" align="center" ><font face="arial" size="2"><%=(tempval[4]+tempval[5]+tempval[10]+tempval[11])%></font>&nbsp;</td>
  						</tr>				     				     
				     <%} // else block end	  				    				 					
    				 count++;
  				   } // while loop end
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
  		
	     	<tr>
				<td width="20%" align="center" colspan="2"><font face="arial" size="2">&#2351;&#2379;&#2327;</font></td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=(total[0]+total[1])%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=(total[2]+total[3])%></font>&nbsp;</td>
				<td width="4%" align="center" ><font face="arial" size="2"><%=(total[0]+total[1]+total[2]+total[3])%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=total[4]%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=total[5]%></font>&nbsp;</td>
				<td width="4%" align="center" ><font face="arial" size="2"><%=(total[4]+total[5])%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=total[6]%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=total[7]%></font>&nbsp;</td>
				<td width="4%" align="center" ><font face="arial" size="2"><%=(total[6]+total[7])%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=total[8]%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=total[9]%></font>&nbsp;</td>
				<td width="4%" align="center" ><font face="arial" size="2"><%=(total[8]+total[9])%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=(total[6]+total[8])%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=(total[7]+total[9])%></font>&nbsp;</td>
				<td width="4%" align="center" ><font face="arial" size="2"><%=(total[6]+total[7]+total[8]+total[9])%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=total[10]%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=total[11]%></font>&nbsp;</td>
				<td width="4%" align="center" ><font face="arial" size="2"><%=(total[10]+total[11])%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=(total[0]+total[1]+total[6]+total[8])%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=(total[2]+total[3]+total[7]+total[9])%></font>&nbsp;</td>
				<td width="4%" align="center" ><font face="arial" size="2"><%=(total[0]+total[1]+total[2]+total[3]+total[6]+total[7]+total[8]+total[9])%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=(total[4]+total[10])%></font>&nbsp;</td>
				<td width="3%" align="center" ><font face="arial" size="2"><%=(total[5]+total[11])%></font>&nbsp;</td>
				<td width="4%" align="center" ><font face="arial" size="2"><%=(total[4]+total[5]+total[10]+total[11])%></font>&nbsp;</td>
			</tr>				     				       		
		</table>
	</body>
</html>