
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
   
   // For Taluk name
    Statement st5=null;
    ResultSet rs5=null;

   // For District name
    Statement st6=null;
    ResultSet rs6=null;

   // For State name
    Statement st7=null;
    ResultSet rs7=null;
    
    // For State name
    Statement st8=null;
    ResultSet rs8=null;

  
    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/kl_dhis2";
          
	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = Integer.parseInt( selectedId );
	

	  String startingDate  =   (String) stack.findValue( "startingPeriod" );
	  String endingDate  = 	  (String) stack.findValue( "endingPeriod" );

      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = 	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	String stateName = ""; 
  	String talukName = "";
  	String districtName = "";
  	String CHCName = "";
  	
  	int CHCID = 0;
  	int talukID = 0;
  	int districtID = 0;
  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String monthlyDataElements[] = {
  											
  									  	// Population
  									  	"'Form6_DE'",
  									  	
  									  	// Insecticide used
  									  	"'Form6_DE1'",

										// Period of spryaing ( Round)
										"'Form6_DE1'",

										// Targets - Rooms
										"'Form6_DE1'",

										// Targets - Cattle Sheds
										"'Form6_DE1'",
										
										// Achievements Sprayed - Rooms
										"'Form6_DE1'",
										
										// Achievements Sprayed - Cattle Sheds
										"'Form6_DE1'",
										
										// Achievements Coverage - Rooms
										"'Form6_DE1'",
										
										// Achievements Coverage - Cattle Sheds
										"'Form6_DE1'",
										
										// Positive incidence month wise - Jan
										"'Form6_DE1'",
										
										// Positive incidence month wise - Feb
										"'Form6_DE1'",
										
										// Positive incidence month wise - Mar
										"'Form6_DE1'",
										
										// Positive incidence month wise - Apr
										"'Form6_DE1'",

										// Positive incidence month wise - May
										"'Form6_DE1'",

										// Positive incidence month wise - Jun
										"'Form6_DE1'",
										
										// Positive incidence month wise - Jul
										"'Form6_DE1'",
										
										// Positive incidence month wise - Aug
										"'Form6_DE1'",
										
										// Positive incidence month wise - Sep
										"'Form6_DE1'",
										
										// Positive incidence month wise - Oct
										"'Form6_DE1'",
										
										// Positive incidence month wise - Nov
										"'Form6_DE1'",
										
										// Positive incidence month wise - Dec
										"'Form6_DE1'",
										
										// Balance of Insecticide in M.Tonnes - 50% DDT
										"'Form6_DE1'",

										// Balance of Insecticide in M.Tonnes - 75% DDT
										"'Form6_DE1'",
										
										// Balance of Insecticide in M.Tonnes - 50% BHC
										"'Form6_DE1'",

										// Balance of Insecticide in M.Tonnes - Malathion
										"'Form6_DE1'",

										// Balance of Tablets - 4 AQ
										"'Form6_DE1'",

										// Balance of Tablets - 8 AQ
										"'Form6_DE1'",

										// Balance of Tablets - Daraprim
										"'Form6_DE1'",
										
										// Balance of Tablets - Quinine
										"'Form6_DE1'"
										
																					
  											
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
        st6=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st7=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st8=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);


        //rs1 = st1.executeQuery("SELECT organisationunit.name FROM organisationunit WHERE id ="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.name FROM organisationunit WHERE organisationunitid ="+selectedOrgUnitID);        
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);     }
        else  {  selectedOrgUnitName = "";       }  
                
//        rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	    if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	}  

		selectedDataPeriodStartDate = startingDate;


		//rs8=st8.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
		rs8=st8.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")"); 
		if(rs8.next()) {  CHCID = rs8.getInt(1); CHCName = rs8.getString(2);}
		else { CHCID=0; CHCName = "";}  

		//rs5=st5.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+CHCID+")");
		rs5=st5.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+CHCID+")"); 
		if(rs5.next()) {  talukID = rs5.getInt(1); talukName = rs5.getString(2);}
		else { talukID=0; talukName = "";}  
				
		//rs6=st6.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+talukID+")");
		rs6=st6.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+talukID+")"); 
		if(rs6.next()) {  districtID = rs6.getInt(1);  districtName = rs6.getString(2); }
		else { districtID = 0; districtName = "";}  

		//rs7=st7.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+districtID+")");
		rs7=st7.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+districtID+")"); 
		if(rs7.next()) {  stateName = rs7.getString(2);}
		else { stateName = "";}  

	   	//rs3 =  st3.executeQuery("select id,name from organisationunit where parent = "+selectedOrgUnitID);
	   	rs3 =  st3.executeQuery("select organisationunitid,name from organisationunit where parentid = "+selectedOrgUnitID);
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
    int lastYear = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;
    int tempForMonth1 = Integer.parseInt(partsOfDataPeriodStartDate[1]);
    int tempForYear = 0;
    if(tempForMonth1 < 4){   tempForYear = lastYear;  }
    else  {   tempForYear = lastYear + 1;   	}
    String curYearStart=tempForYear+"-04-01";			     

%>


<HTML>
	<HEAD>
		<TITLE>MONTHLY REPORT OF MALARIA PROGRAMME</TITLE>
	</HEAD>
	<BODY>
		<P align="center"><b>
			<FONT face="Arial" size="3"><div align="right">M.F - 6</div>
				NATIONAL ANTI - MALARIA PROGRAMME <BR> 
			    MONTHLY REPORT OF MALARIA PROGRAMME <BR> (PROGRESS AND ASSESSMENT OF SPRAYING)</font></b></p>
        
        <table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
   				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"><b>
                Name of State&nbsp;:&nbsp;&nbsp;<%=stateName%></b></font></td>
   				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
                <p align="right"><font face="Arial" size="2"><b>PHC included in the area :&nbsp;&nbsp; </b></font></td>   
  			</tr>
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
   				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"><b>
                Name of District :<%=districtName%></b></font></td>
   				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
                <p align="right"><font face="Arial" size="2"><b>Total Sections : <%=childOrgUnitCount%></b></font></td>   
  			</tr>
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
   				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"><b>
                Name of Malaria Inspector :</b></font></td>
   				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
                <b><font face="Arial" size="2">Population : <%=totPopulation%></font></b><font face="Arial" size="2"><b>&nbsp;&nbsp; </b></font></td>   
  			</tr>
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
   				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"><b>Head Quarters :</b></font></td>
   				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"><b>
                &nbsp;&nbsp; </b></font></td>   
  			</tr>

		</table>
                               		
		<br>
		
		
	     <TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  						<TR>
  							<TD align="center" width="2%" rowSpan=3><b><font face="Arial"  style="font-size:8pt">Sl.No.</font></b></TD>
   							<TD align="center" width="38%" rowSpan=3><b><font face="Arial"  style="font-size:8pt">Name of Section</font></b></TD>
   							<TD align="center" width="2%" rowspan="3"><b><font face="Arial"  style="font-size:8pt">Population</b></font></TD>
   							<TD align="center" width="2%" rowspan="3"><b><font face="Arial"  style="font-size:8pt">Insecticide used</b></font></TD>
   							<TD align="center" width="2%" rowspan="3"><font face="Arial"  style="font-size:8pt"><b>Period of spraying (Round)</b></font></TD>
   							<TD align="center" width="4%" colspan="2"><font face="Arial"  style="font-size:8pt"><b>Targets</font></b></TD>
   							<TD align="center" width="8%" colspan="4"><b><font face="Arial"  style="font-size:8pt">Achievements</font></b></TD>
   							<TD align="center" width="2%" rowspan="3"><b><font face="Arial"  style="font-size:8pt">Year</font></b></TD>
   							<TD align="center" width="24%" colspan="12"><b><font face="Arial"  style="font-size:8pt">Positive incidence month wise</b></font></TD>
   							<TD align="center" width="8%" colspan="4"><b><font face="Arial"  style="font-size:8pt">Balance of insecticide in M. Tonnes</b></font></TD>
   							<TD align="center" width="8%" colspan="4"><b><b><font face="Arial"  style="font-size:8pt">Balance Tablets</font></b></TD>
    					</TR>
  						<TR>
   							<TD align="center" width=2%" rowspan="2"><b><font face="Arial"  style="font-size:8pt">Rooms</font></b></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial"  style="font-size:8pt">Cattle Sheds</b></font></TD>
   							<TD align="center" width="2%" colspan="2"><b><font face="Arial"  style="font-size:8pt">Sprayed</font></b></TD>
   							<TD align="center" width="2%" colspan="2"><b><font face="Arial"  style="font-size:8pt">Coverage</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">J</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">F</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">M</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">A</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">M</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">J</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">J</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">A</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">S</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">O</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">N</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">D</font></b></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial"  style="font-size:8pt">50% DDT</font></b></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial"  style="font-size:8pt">75% DDT</font></b></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial"  style="font-size:8pt">50% BHC</font></b></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial"  style="font-size:8pt">Malathion</font></b></TD>
                            <TD align="center" width="2%" rowspan="2"><b><font face="Arial"  style="font-size:8pt">4Aq</font></b></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial"  style="font-size:8pt">8Aq  </font></b></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" style="font-size: 8pt">Daraprim</font></b></TD>
   							<TD align="center" width="2%" rowspan="2"><b><font face="Arial" style="font-size: 8pt">Quinine</font></b></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">Rooms</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">Cattle Sheds</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">Rooms</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt">Cattle Sheds</font></b></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt"></font></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt"></font></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt"></font></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt"></font></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt"></font></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt"></font></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt"></font></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt"></font></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt"></font></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt"></font></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt"></font></TD>
   							<TD align="center" width="2%"><b><font face="Arial"  style="font-size:8pt"></font></TD>
    					</TR>
    					<TR>
    						<TD width="2%" HEIGHT="30" align="center"><b><font face="Arial"  style="font-size:8pt"><i>1</b></i></FONT></TD>
    						<TD width="38%" HEIGHT="30" align="center"><b><font face="Arial"  style="font-size:8pt"><i>2</b></i></FONT></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><font face="Arial"  style="font-size:8pt">3</TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><font face="Arial"  style="font-size:8pt">4</TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><font face="Arial"  style="font-size:8pt">5</TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><font face="Arial"  style="font-size:8pt">6</TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><font face="Arial"  style="font-size:8pt">7</TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">8</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">9</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">10</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">11</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><font face="Arial"  style="font-size:8pt"><i>12</b></i></FONT></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><b><font face="Arial"  style="font-size:8pt"><i>13</b></i></FONT></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">14</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">15</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">16</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">17</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">18</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">19</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">20</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">21</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">22</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">23</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">24</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">25</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">26</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">27</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">28</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">29</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">30</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">31</font></i></b></TD>
    						<TD width="2%"  HEIGHT="30" align="center"><i><b><font face="Arial"  style="font-size:8pt">32</font></i></b></TD>
    					</TR>	    						     				     				   			     					

			<%
			
				try
				{
			  		count = 0;
			  		while(count < childOrgUnitCount)
			   		{ 			 
						Integer temp1 = (Integer) childOrgUnitIDs.get(count);
						int currentChildID = temp1.intValue(); 			
						for(i=0;i<monthlyDataElements.length;i++)
		 				 {					
							//rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source = "+currentChildID+"  AND dataelement.code in ("+monthlyDataElements[i]+")");
							rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid = "+currentChildID+"  AND dataelement.code in ("+monthlyDataElements[i]+")");
							//SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source ="+selectedOrgUnitID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");								
							if(!rs4.next())  {  tempval[i] = 0;	 }
							else   {  tempval[i] = rs4.getInt(1);  }
							total[i] += tempval[i];		
						 } 		
						 %>	 	  	 

  						<TR>
    						<TD width="2%" HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=(count+1)%></font></TD>
    						<TD width="38%" HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=childOrgUnitNames.get(count)%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[2]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[6]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[10]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[11]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[12]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[0]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[15]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[16]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[17]%></font></TD>
    						<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=tempval[18]%></font></TD>
    					</TR>
    				<% 					
    					count++;
  				   }	 // while loop end
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
  					if(rs6!=null)  rs6.close();		if(st6!=null)  st6.close();
 					if(rs7!=null)  rs7.close();		if(st7!=null)  st7.close();  					
  					if(rs8!=null)  rs8.close();		if(st8!=null)  st8.close(); 					
 					
					if(con!=null)  con.close();
				 }
				catch(Exception e)   {  out.println(e.getMessage());   }
       		 } // finally block end		          		
  		%>


						<TR>
							<TD width="2%" HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt">&nbsp;</font></TD>
							<TD width="38%" HEIGHT="50" align="center"><b><font face="Arial"  style="font-size:8pt">Total for PHC</font></b></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
							<TD width="2%"  HEIGHT="50" align="center"><font face="Arial"  style="font-size:8pt"><%=total[0]%></font></TD>
						</TR>
				</TABLE>
 	</BODY>
</HTML>