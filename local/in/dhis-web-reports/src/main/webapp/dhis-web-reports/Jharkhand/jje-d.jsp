
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
  											"de for population",
											
											// Report upto Previous Month - Cases, Deaths
  											"JE_DE1", "JE_DE2",  											
  											
  											// During the Month - Cases, Deaths
  											"JE_DE1", "JE_DE2",
  											
  											// No. of Cases Reported - (0-1) Male, (0-1) Female
  											"JE_DE3", "JE_DE4",
  											
  											// No. of Cases Reported - (1-5) Male, (1-5) Female
  											"JE_DE5", "JE_DE6",
  											
  											// No. of Cases Reported - (6-8) Male, (6-8) Female
  											"JE_DE7", "JE_DE8",
  											
  											// No. of Cases Reported - (9-14) Male, (9-14) Female
  											"JE_DE9","JE_DE10",
  											
  											// No. of Cases Reported - Above 14 Male, Above 14 Female
  											"JE_DE11","JE_DE12",
  											
  											// No. of Deaths Reported - (0-1) Male, (0-1) Female
  											"JE_DE13","JE_DE14",
  											
  											// No. of Deaths Reported - (1-5) Male, (1-5) Female
  											"JE_DE15","JE_DE16",
  											
  											// No. of Deaths Reported - (6-8) Male, (6-8) Female
  											"JE_DE17","JE_DE18",
  											
  											// No. of Deaths Reported - (9-14) Male, (9-14) Female
  											"JE_DE19","JE_DE20",

  											// No. of Deaths Reported - Above 14 Male, Above 14 Female
  											"JE_DE21","JE_DE22"  											
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

        //rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE id = "+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE organisationunitid = "+selectedOrgUnitID);        
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


<HTML>
	<HEAD>
		<TITLE>Monthly Report on Japanese Encephalitis</TITLE>
	</HEAD>
	<BODY>
		<P align="center"><FONT face="Arial" size="3"><b><u>Monthly Report on Japanese Encephalitis</u></font></b><br></p>
		
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
		
		<br>
		
		<%
			
			try
			  {
				  count = 0;
				  while(count < childOrgUnitCount)
				   { 				   
				    if(count%20==0)
				     {
				       if(count != 0) {%></table><div align="right"><font face="Arial" size="1"><i>(page contd.)</i></font></div> <br><%}%>
				     </table><br>
				     <TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  						<TR>
  							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">Sl. No.</font></b></TD>
   							<TD align="center" width="23%" rowSpan=2><b><font face="Arial" size="2">PHC</font></b></TD>
   							<TD align="center" width="4%" rowspan="2"><b><font face="Arial" size="1">Total Population</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="1">Report up to previous month</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="1">During the month</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="1">Progressive Total</font></b></TD>
   							<TD align="center" width="26%" colspan="12"><b><font face="Arial" size="2">No. of Cases reported</font></b></TD>
   							<TD align="center" width="26%" colspan="12"><b><font face="Arial" size="2">No. of Deaths reported</font></b></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="3%"><b><font face="Arial" size="1">Cases</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="1">Deaths</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="1">Cases</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="1">Deaths</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="1">Cases</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="1">Deaths</font></b></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">0-1</font></b></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">1-5</font></b></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">6-8</font></b></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">9-14</font></b></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">Above 14yrs</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="1">Total</font></b></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">0-1</font></b></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">1-5</font></b></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">6-8</font></b></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">9-14</font></b></TD>
   							<TD align="center" width="4%" colspan="2"><b><font face="Arial" size="1">Above 14yrs</font></b></TD>
   							<TD align="center" width="16%" colspan="2"><b><font face="Arial" size="1">Total</font></b></TD>
    					</TR>
    					<TR>
    						<TD width="3%" HEIGHT="20" align="center">&nbsp;</TD>
    						<TD width="23%" HEIGHT="20" align="center">&nbsp;</TD>
    						<TD width="4%" HEIGHT="20" align="center">&nbsp;</TD>
    						<TD width="3%" HEIGHT="20" align="center">&nbsp;</TD>
    						<TD width="3%" HEIGHT="20" align="center">&nbsp;</TD>
    						<TD width="3%" HEIGHT="20" align="center">&nbsp;</TD>
    						<TD width="3%" HEIGHT="20" align="center">&nbsp;</TD>
    						<TD width="3%" HEIGHT="20" align="center">&nbsp;</TD>
    						<TD width="3%" HEIGHT="20" align="center">&nbsp;</TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">M </font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">F</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">M</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">F</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">M</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">F</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">M</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">F</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">M</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">F</font></b></TD>
    						<TD width="3%" HEIGHT="20" align="center"><b><font face="Arial" size="2">M</font></b></TD>
    						<TD width="3%" HEIGHT="20" align="center"><b><font face="Arial" size="2">F</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">M</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">F</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">M</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">F</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">M</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">F</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">M</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">F</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">M</font></b></TD>
    						<TD width="2%" HEIGHT="20" align="center"><b><font face="Arial" size="2">F</font></b></TD>
    						<TD width="3%" HEIGHT="20" align="center"><b><font face="Arial" size="2">M</font></b></TD>
    						<TD width="3%" HEIGHT="20" align="center"><b><font face="Arial" size="2">F</font></b></TD>
    					</TR>	
    						     				     
				     <%}
					Integer temp1 = (Integer) childOrgUnitIDs.get(count);
					int currentChildID = temp1.intValue(); 			
					for(i=0;i<monthlyDataElements.length;i++)
					 {			
						 // For PHC
						 //if(i>=1 && i<=2)
					 	 //	      query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+curYearStart+"' and '"+curYearEnd+"' and periodType = "+periodTypeID+") AND datavalue.source ="+currentChildID+" AND dataelement.code like '"+monthlyDataElements[i]+"'";
					 	 //else query = "SELECT datavalue.value FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period ="+selectedDataPeriodID+" AND datavalue.source ="+currentChildID+" AND dataelement.code like '"+monthlyDataElements[i]+"'";  			


						 // For District
						 if(i>=1 && i<=2)
					 	 	      //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+curYearStart+"' and '"+curYearEnd+"' and periodType = "+periodTypeID+") AND datavalue.source in (select id from organisationunit where parent ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'";
					 	 	      query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+curYearEnd+"' and periodtypeid = "+periodTypeID+") AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'";
					 	 else 
					 	 	//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (select id from organisationunit where parent ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'";
					 	 	query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'";
																		
						rs4 = st4.executeQuery(query);
						//"SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period ="+selectedDataPeriodID+" AND datavalue.source in (select id from organisationunit where parent ="+currentChildID+") AND dataelement.code like '"+monthlyDataElements[i]+"'");
						//SELECT datavalue.value FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period="+selectedDataPeriodID+" AND datavalue.source="+currentChildID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");
						//SELECT sum(datavalue.value) FROM (organisationunit INNER JOIN datavalue ON organisationunit.id = datavalue.source) INNER JOIN period ON datavalue.period = period.id WHERE organisationunit.parent="+selectedOrgUnitID+" AND period.startDate='"+selectedDataPeriodStartDate+"' AND periodType="+periodTypeID+" AND datavalue.dataElement="+dataElementIDs[i])
												
						if(!rs4.next())  {  tempval[i] = 0;	 }
						else   {  tempval[i] = rs4.getInt(1);  }
						total[i] += tempval[i];
					 } 	
			   
				   %>			     				  		             		
  					
  					<TR>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=(count+1)%>.</font></TD>
    					<TD width="23%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=childOrgUnitNames.get(count)%></font></TD>
    					<TD width="4%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[0]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[1]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[2]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[3]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[4]%></font></TD>
    					<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"></font></TD>
    					<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[5]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[6]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[7]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[8]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[9]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[10]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[11]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[12]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[13]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[14]%></font></TD>
    					<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=(tempval[5]+tempval[7]+tempval[9]+tempval[11]+tempval[13])%></font></TD>
    					<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=(tempval[6]+tempval[8]+tempval[10]+tempval[12]+tempval[14])%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[15]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[16]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[17]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[18]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[19]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[20]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[21]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[22]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[23]%></font></TD>
    					<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[24]%></font></TD>
    					<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=(tempval[15]+tempval[17]+tempval[19]+tempval[21]+tempval[23])%></font></TD>
    					<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=(tempval[16]+tempval[18]+tempval[20]+tempval[22]+tempval[24])%></font></TD>
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
				<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1">&nbsp;</font></TD>
				<TD width="23%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><b>Total</b></font></TD>
				<TD width="4%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[0]%></font></TD>
				<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[1]%></font></TD>
				<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[2]%></font></TD>
				<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[3]%></font></TD>
				<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[4]%></font></TD>
				<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"></font></TD>
				<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[5]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[6]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[7]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[8]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[9]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[10]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[11]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[12]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[13]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[14]%></font></TD>
				<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=(total[5]+total[7]+total[9]+total[11]+total[13])%></font></TD>
				<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=(total[6]+total[8]+total[10]+total[12]+total[14])%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[15]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[16]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[17]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[18]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[19]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[20]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[21]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[22]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[23]%></font></TD>
				<TD width="2%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=total[24]%></font></TD>
				<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=(total[15]+total[17]+total[19]+total[21]+total[23])%></font></TD>
				<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=(total[16]+total[18]+total[20]+total[22]+total[24])%></font></TD>
			</TR>
		</TABLE>
 	</BODY>
</HTML>