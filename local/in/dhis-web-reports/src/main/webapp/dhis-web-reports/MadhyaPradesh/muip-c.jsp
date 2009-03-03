
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
    String urlForConnection = "jdbc:mysql://localhost/mp_dhis2";
          
	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = Integer.parseInt( selectedId );
//	int selectedOrgUnitID = 240;

	
//	String selectedPeriodId = (String) stack.findValue( "periodSelect" );
//	int selectedDataPeriodID = 	Integer.parseInt( selectedPeriodId );

  	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );

//	String startingDate  = "2006-05-01";
//	String endingDate  = "2006-05-01";

      
//	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
//	int periodTypeID = 	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = ""; 
  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String monthlyDataElements[] = {
										// Annual Target - Pregnent Women, Infant
										"xxx","xxx",
										
										// BCG ( Male + Female )
										"Form6_DE44","Form6_DE45",
										
										// DPT - 1 (Male + Female)
										"Form6_DE46","Form6_DE47",

										// DPT - 2 (Male + Female)
										"Form6_DE48","Form6_DE49",

										// DPT - 3 (Male + Female)
										"Form6_DE50","Form6_DE51",

										// DPT - Booster (Male + Female)
										"Form6_DE64","Form6_DE65",
										
										// OPV - 1 (Male + Female)
										"Form6_DE54","Form6_DE55",
										
										// OPV - 2 (Male + Female)
										"Form6_DE56","Form6_DE57",
										
										// OPV - 3 (Male + Female)
										"Form6_DE58","Form6_DE59",
										
										// Measles (Male + Female)
										"Form6_DE60","Form6_DE61",
										
										// TT - 1,2,Booster
										"Form6_DE5","Form6_DE6","Form6_DE7" 																																																	
  											
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

        //rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE id ="+selectedOrgUnitID);        
        rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE organisationunitid ="+selectedOrgUnitID);
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);     }
        else  {  selectedOrgUnitName = "";       }  
                
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
    int lyear = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;			     
%>


<HTML>
	<HEAD>
		<TITLE>UNIVERSAL IMMUNIZATION PROGRAMME</TITLE>
	</HEAD>
	<BODY>
		<P align="center"><FONT face="Arial" size="3"><b>UNIVERSAL IMMUNIZATION PROGRAMME<BR>
							PERFORMANCE OF THE YEAR <%=lyear%> - <%=partsOfDataPeriodStartDate[0]%></b></font><br></p>
			
			<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  				<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       					<b><font face="Arial" size="2">CHC</font></b><font face="Arial" size="2"><b> 
                        :&nbsp;&nbsp;<%=selectedOrgUnitName%></b></font>
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
				    if(count%16==0)
				     {
				       if(count != 0) {%></table><div align="right"><font face="Arial" size="1"><i>(page contd.)</i></font></div> <br><%}%>
				     </table><br>
				     <TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  						<TR>
  							<TD align="center" width="3%" rowSpan=2><b>
                            <font face="Arial" size="2">Sl.No</font></b></TD>
   							<TD align="center" width="41%" rowSpan=2><b>
                            <font face="Arial" size="2">PHC/Sector Name</font></b></TD>
   							<TD align="center" width="8%" colspan="2"><b><font face="Arial" size="2">ANNUAL TARGET</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="2">B.C.G.</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="2">DPT-1</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="2">DPT-2</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="2">DPT-3</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="2">DPT-B</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="2">OPV-1</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="2">OPV-2</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="2">OPV-3</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="2">Measles</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="2">T.T-1</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="2">T.T-2</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="2">T.T-3</font></b></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="4%"><b><font face="Arial" size="2">P.Women</font></b></TD>
   							<TD align="center" width="4%"><b><font face="Arial" size="2">Infant</font></b></TD>
    					</TR>
    					<TR>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>1</b></i></FONT></TD>
    						<TD width="41%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>2</b></i></FONT></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>3</b></i></FONT></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">4</font></i></b></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">5</font></i></b></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">6</font></i></b></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">7</font></i></b></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">8</font></i></b></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">9</font></i></b></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">10</font></i></b></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">11</font></i></b></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">12</font></i></b></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">13</font></i></b></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">14</font></i></b></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">15</font></i></b></TD>
    						<TD width="4%"  HEIGHT="30" align="center"><b><i><font face="Arial" size="2">16</font></i></b></TD>
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
    					<TD width="41%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=childOrgUnitNames.get(count)%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[0]%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[1]%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[2]+tempval[3])%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[4]+tempval[5])%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[6]+tempval[7])%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[8]+tempval[9])%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[10]+tempval[11])%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[12]+tempval[13])%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[14]+tempval[15])%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[16]+tempval[17])%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[18]+tempval[19])%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[20]%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[21]%></font></TD>
    					<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[22]%></font></TD>
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
				<TD width="41%" HEIGHT="30" align="center"><b>
                <font face="Arial" size="2">Total</font></b></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[0]%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[1]%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[2]+total[3])%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[4]+total[5])%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[6]+total[7])%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[8]+total[9])%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[10]+total[11])%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[12]+total[13])%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[14]+total[15])%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[16]+total[17])%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[18]+total[19])%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[20]%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[21]%></font></TD>
				<TD width="4%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[22]%></font></TD>
			</TR>
		</TABLE>
 	</BODY>
</HTML>