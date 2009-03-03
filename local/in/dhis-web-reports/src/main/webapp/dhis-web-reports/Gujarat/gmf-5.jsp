
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
  
    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/gj_dhis2";
          
	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = 	Integer.parseInt( selectedId );
	
//	String selectedPeriodId = (String) stack.findValue( "periodSelect" );
//	int selectedDataPeriodID = 	Integer.parseInt( selectedPeriodId );

  	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );

      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = 	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	String stateName = ""; 
  	String talukName = "";
  	String districtName = "";
  	
  	int talukID = 0;
  	int districtID = 0;
  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String monthlyDataElements[] = {
  											
  											// Positive - Male, Female
											"MAL_DE21","MAL_DE22",  											
  											
  											// SPECIES - Pv,Pf R,Pf RG, Others
  											"MAL_DE23",	"MAL_DE24", "MAL_DE25", "MAL_DE26",


											// RT done - (0-1), (1-4), (5-14), (15+)
	  										"MAL_DE15","MAL_DE16","MAL_DE17","MAL_DE18",
																						

											// No. of Rooms
											"MAL_DE30",
											
											// Total fever cases treated with 4Aq tabs single dose 
											"MAL_DE31",
											
											// 4Aq + 8Aq mg, Sulphadoxysulpha, RT 5 Day, 
											"MAL_DE32", "MAL_DE33", "MAL_DE34",  											  										  											
  											
  											// Balance - 4Aq, 8Aq , sulpha + pyri, quinin 
  											"MAL_DE35","MAL_DE36","MAL_DE37","MAL_DE38"
  											
										};
	
		
  	int i=0;
  	int totPopulation = 0;
  	  	
	int tempval[] = new int[monthlyDataElements.length+5];
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


        //rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE id ="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE organisationunitid ="+selectedOrgUnitID);        
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);     }
        else  {  selectedOrgUnitName = "";       }  
                
//      rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	    if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	}  

		selectedDataPeriodStartDate = startingDate;
		
		//rs5=st5.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
		rs5=st5.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")"); 
		if(rs5.next()) {  talukID = rs5.getInt(1); talukName = rs5.getString(2);}
		else { talukID=0; talukName = "";}  
				
		//rs6=st6.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+talukID+")");
		rs6=st6.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+talukID+")"); 
		if(rs6.next()) {  districtID = rs6.getInt(1);  districtName = rs6.getString(2); }
		else { districtID = 0; districtName = "";}  

		//rs7=st7.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+districtID+")");
		rs7=st7.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+districtID+")"); 
		if(rs7.next()) {  stateName = rs7.getString(2);}
		else { stateName = "";}  

		for(i=0;i<monthlyDataElements.length;i++)
		 {					
			//rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source ="+selectedOrgUnitID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");
			rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid ="+selectedOrgUnitID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");												
			if(!rs4.next())  {  tempval[i] = 0;	 }
			else   {  tempval[i] = rs4.getInt(1);  }
			
								 } 			 	  	 
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

    String partsOfDataPeriodStartDate[] = selectedDataPeriodStartDate.split("-");			     
%>


<HTML>
	<HEAD>
		<TITLE>MONTHLY REPORT OF MALARIA PROGRAMME OF PRIMARY HEALTH CENTRE</TITLE>
	</HEAD>
	<BODY>
		<P align="center"><b>
			<FONT face="Arial" size="3"><div align="right">M.F - 5</div>
			   MONTHLY REPORT OF MALARIA PROGRAMME OF PRIMARY HEALTH CENTRE - <%=selectedOrgUnitName%></font></b></p>
        
        <table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
   				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><b><font face="Arial" size="2">State&nbsp;:&nbsp;&nbsp;<%=stateName%></b></font></td>
   				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"><b>Month :&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%> </b></font></td>   
  			</tr>
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
   				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><b><font face="Arial" size="2">District&nbsp;:&nbsp;&nbsp;<%=districtName%></b></font></td>
   				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><b><font face="Arial" size="2">Population&nbsp;:&nbsp;&nbsp;<%=totPopulation%> </b></font></td>   
  			</tr>
		</table>
                               		
		<br>
		
                                   			
	     <TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  						<TR>
  							<TD align="center" width="3%" rowSpan=3><b><font face="Arial" size="1">Sl.No.</font></b></TD>
   							<TD align="center" width="40%" rowSpan=3><b><font face="Arial" size="1">Name of PHC</font></b></TD>
   							<TD align="center" width="9%" colspan="3"><b><font face="Arial" size="1">PASSIVE</font></b></TD>
   							<TD align="center" width="15%" colspan="5"><b><font face="Arial" size="1">SPECIES</font></b></TD>
   							<TD align="center" width="3%" rowspan="3"><b><font face="Arial" size="1">R.T. Given</font></b></TD>
   							<TD align="center" width="3%" rowspan="3"><b><font face="Arial" size="1">Total focal spray of rooms</font></b></TD>
   							<TD align="center" width="3%" rowspan="3"><b><font face="Arial" size="1">Total fever cases treated with 4-AQ tabs single dose W/o B.S</font></b></TD>
   							<TD align="center" width="12%" colspan="4"><font face="Arial" size="1"><b>Mass therapeutic measures</font></b></TD>
   							<TD align="center" width="12%" colspan="4"><b><font face="Arial" size="1">Balance Tablets</font></b></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" style="font-size: 8pt">Male</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial"  style="font-size:8pt">Female</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" style="font-size: 8pt">Total</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" style="font-size: 8pt">Pv</font></b></TD>
   							<TD align="center" width="3%" colspan="2"><b><font face="Arial" style="font-size: 8pt">Pf</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" style="font-size: 8pt">Others</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" style="font-size: 8pt">Total</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" style="font-size: 8pt">Single dose 4-A.Q. + 8-A.Q. 600+45 mg</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" style="font-size: 8pt">Single dose Sulpha + Pyri</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" style="font-size: 8pt">R.T. 5 days</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" style="font-size: 8pt">Total</font></b></TD>   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial"  style="font-size:8pt">4Aq</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial"  style="font-size:8pt">8Aq 7.5mg</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial"  style="font-size:8pt">8Aq 2.5mg</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" style="font-size: 8pt">Quinine</font></b></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="3%"><b><font face="Arial" size="1">R</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="1">RG</font></b></TD>
    					</TR>
    					<TR>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>1</b></i></FONT></TD>
    						<TD width="40%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>2</b></i></FONT></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>3</b></i></FONT></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">4</font></i></b></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">5</font></i></b></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>6</b></i></FONT></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">7</font></i></b></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">8</font></i></b></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">9</font></i></b></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">10</font></i></b></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>11</b></i></FONT></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>12</b></i></FONT></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>13</b></i></FONT></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>14</b></i></FONT></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">15</font></i></b></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">16</font></i></b></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">17</font></i></b></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><i><b>18</b></i></FONT></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">19</font></i></b></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">20</font></i></b></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="1">21</font></i></b></TD>
    					</TR>	
    						     				     
				   
			   
  					
  					<TR>
    					<TD width="3%" HEIGHT="50" align="center"><FONT face="Arial" size="1">1.</font></TD>
    					<TD width="40%" HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=selectedOrgUnitName%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[0]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[1]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(tempval[0]+tempval[1])%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[2]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[3]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[4]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[5]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(tempval[2]+tempval[3]+tempval[4]+tempval[5])%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(tempval[6]+tempval[7]+tempval[8]+tempval[9])%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[10]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[11]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[12]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[13]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[14]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(tempval[12]+tempval[13]+tempval[14])%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[15]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[16]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[17]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[18]%></font></TD>
    				</TR>

			<TR>
				<TD width="3%" HEIGHT="50" align="center"><FONT face="Arial" size="1">&nbsp;</font></TD>
				<TD width="40%" HEIGHT="50" align="center"><b><font face="Arial" size="1">Total</font></b></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[0]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[1]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(tempval[0]+tempval[1])%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[2]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[3]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[4]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[5]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(tempval[2]+tempval[3]+tempval[4]+tempval[5])%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(tempval[6]+tempval[7]+tempval[8]+tempval[9])%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[10]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[11]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[12]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[13]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[14]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(tempval[12]+tempval[13]+tempval[14])%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[15]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[16]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[17]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=tempval[18]%></font></TD>
			</TR>
		</TABLE>
 	</BODY>
</HTML>