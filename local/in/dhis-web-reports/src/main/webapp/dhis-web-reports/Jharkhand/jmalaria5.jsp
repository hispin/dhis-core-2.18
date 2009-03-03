
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
										"DE for Population",
										
										// M & Contect B.S - Coll,Exam,PV,Pf
  										"MAL_DE14","MAL_DE15","MAL_DE16","MAL_DE17",
  										
  										// Active B.S - Coll,Exam,PV,Pf
  										"MAL_DE6","MAL_DE7","MAL_DE8","MAL_DE9",
  										
  										// By Malaria Clinic - Coll,Exam,PV,Pf
  										"MAL_DE18","MAL_DE19","MAL_DE20","MAL_DE21",
  										
  										// At Sub-centre - Coll,Exam,PV,Pf
  										"MAL_DE10","MAL_DE11","MAL_DE12","MAL_DE13",
  										
  										// At PHC - Coll,Exam,PV,Pf
  										"MAL_DE22","MAL_DE23","MAL_DE24","MAL_DE25",
  										
  										// Age wise Positive - (0-1), (1-4), (5-14), (15+)
  										"MAL_DE28","MAL_DE29","MAL_DE30","MAL_DE31",  										

  										// RT done - (0-1), (1-4), (5-14), (15+)
  										"MAL_DE37","MAL_DE38","MAL_DE39","MAL_DE40",
  										  										
  										
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
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);  }
        else  {  selectedOrgUnitName = "";   }  
                
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
			<FONT face="Arial" size="3"> Monthly Report of Malaria Programme of the <%=selectedOrgUnitName%> for the month <%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%></font>	
		</b></p>
		
		<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"><b>District :&nbsp;&nbsp;<%=selectedOrgUnitName%></b></font></td>
   				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"><b>Month :&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%></b></font></td>   
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
  							<TD align="center" width="2%" rowSpan=2><B><font face="Arial" style="font-size:8pt">Sl. No.</FONT></B></TD>
   							<TD align="center" width="27%" rowSpan=2><B><font face="Arial" style="font-size:8pt">Name of PHC</FONT></B></TD>
   							<TD align="center" width="3%" rowspan="2"><B><font face="Arial" style="font-size:8pt">Popu<br>lation</FONT></B></TD>
   							<TD align="center" width="8%" colspan="4"><B><font face="Arial" style="font-size:8pt">M &amp; Contect B.S.</FONT></B></TD>
   							<TD align="center" width="8%" colspan="4"><B><font face="Arial" style="font-size:8pt">Active B.S.</FONT></B></TD>
   							<TD align="center" width="8%" colspan="4"><B><font face="Arial" style="font-size:8pt">By Malaria Clinic</FONT></B></TD>
   							<TD align="center" width="8%" colspan="4"><B><font face="Arial" style="font-size:8pt">At Sub-centre</FONT></B></TD>
   							<TD align="center" width="8%" colspan="4"><B><font face="Arial" style="font-size:8pt">At PHC</FONT></B></TD>
   							<TD align="center" width="12%" colspan="4"><B><font face="Arial" style="font-size:8pt">Total</FONT></B></TD>
   							<TD align="center" width="8%" colspan="4"><B><font face="Arial" style="font-size:8pt">Agewise Positive</FONT></B></TD>
   							<TD align="center" width="8%" colspan="4"><B><font face="Arial" style="font-size:8pt">R.T. done</FONT></B></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">Coll</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">Exa.</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">PV</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">P.f</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">Coll</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">Exa.</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">PV</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">P.f</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">Coll</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">Exa.</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">PV</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">P.f</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">Coll</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">Exa.</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">PV</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">P.f</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">Coll</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">Exa.</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">PV</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">P.f</FONT></B></TD>
   							<TD align="center" width="3%"><B><font face="Arial" style="font-size:8pt">Coll</FONT></B></TD>
   							<TD align="center" width="3%"><B><font face="Arial" style="font-size:8pt">Exa.</FONT></B></TD>
   							<TD align="center" width="3%"><B><font face="Arial" style="font-size:8pt">PV</FONT></B></TD>
   							<TD align="center" width="3%"><B><font face="Arial" style="font-size:8pt">P.f</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">(0-1)</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">(1-4)</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">(5-14)</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">(15+)</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">(0-1)</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">(1-4)</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">(5-14)</FONT></B></TD>
   							<TD align="center" width="2%"><B><font face="Arial" style="font-size:8pt">(15+)</FONT></B></TD>
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
    					<TD width="2%" HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=(count+1)%></font></B></TD>
    					<TD width="27%" HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=childOrgUnitNames.get(count)%></font></B></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[0]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[1]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[2]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[3]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[4]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[5]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[6]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[7]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[8]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[9]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[10]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[11]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[12]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[13]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[14]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[15]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[16]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[17]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[18]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[19]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[20]%></font></B></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=(tempval[1]+tempval[5]+tempval[9]+tempval[13])%></font></B></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=(tempval[2]+tempval[6]+tempval[10]+tempval[14])%></font></B></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=(tempval[3]+tempval[7]+tempval[11]+tempval[15])%></font></B></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=(tempval[4]+tempval[8]+tempval[12]+tempval[16])%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[21]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[22]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[23]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[24]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[25]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[26]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[27]%></font></B></TD>
    					<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=tempval[28]%></font></B></TD>
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
				<TD width="2%" HEIGHT="30" align="center"><FONT face="Arial" size="1">&nbsp;</font></TD>
				<TD width="27%" HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt">Total</font></b></TD>
				<TD width="3%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[0]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[1]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[2]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[3]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[4]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[5]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[6]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[7]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[8]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[9]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[10]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[11]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[12]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[13]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[14]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[15]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[16]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[17]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[18]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[19]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[20]%></font></b></TD>
				<TD width="3%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=(total[1]+total[5]+total[9]+total[13])%></font></b></TD>
				<TD width="3%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=(total[2]+total[6]+total[10]+total[14])%></font></b></TD>
				<TD width="3%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=(total[3]+total[7]+total[11]+total[15])%></font></b></TD>
				<TD width="3%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=(total[4]+total[8]+total[12]+total[16])%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[21]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[22]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[23]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[24]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[25]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[26]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[27]%></font></b></TD>
				<TD width="2%"  HEIGHT="30" align="center"><B><font face="Arial" style="font-size:8pt"><%=total[28]%></font></b></TD>
			</TR>
		</TABLE>
 	</BODY>
</HTML>