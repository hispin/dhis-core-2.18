
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

    // For finding mother phc of selected phc based on selected orgunit id
    Statement st4=null;
    ResultSet rs4=null;

    // For finding child orgunit stock position of chloroquin tablet, oral pills, contraceptive and ors packet
    Statement st5=null;
    ResultSet rs5=null;

    // For finding taluk name
    Statement st6=null;
    ResultSet rs6=null;

    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/kl_dhis2";
          
	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = 	Integer.parseInt( selectedId );
	

  	String startingDate  = (String) stack.findValue( "startingPeriod" );
  	String endingDate  = 	  (String) stack.findValue( "endingPeriod" );

      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = 	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = ""; 
  	String motherPHC="";
  	int motherPHCID=0; 	
  	String talukName="";
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		
	String query = ""; 

	String monthlyDataElements[] = {
  									  // Code No  									  
  									  "'Form6_DE'",
  									  
  									  // Population
  									  "'Form6_DE'",
  									  
  									  // FW Activity During Month - VAS
  									  "'JHI_DE30'",
  									  
  									  // FW Activity During Month - MINI
  									  "'JHI_DE36'",
  									  
  									  // FW Activity During Month - PPS
  									  "'JHI_DE31','JHI_DE35'",
  									  
  									  // FW Activity During Month - LAP
  									  "'JHI_DE32','JHI_DE33','JHI_DE36','JHI_DE37'",
  									  
  									  // FW Activity During Month - IUD
  									  "'JHI_DE38','JHI_DE39'",
  									  
  									  // FW Activity During Month - OP
  									  "'Form6_DE139','Form6_DE140'",
  									  
  									  // FW Activity During Month - CC
  									  "'JHI_DE40','JHI_DE41'",
  									  
  									  // FW Activity During Year - VAS
  									  "'JHI_DE30'",
  									  
  									  // FW Activity During Year - MINI
  									  "'JHI_DE36'",
  									  
  									  // FW Activity During Year - PPS
  									  "'JHI_DE31','JHI_DE35'",
  									  
  									  // FW Activity During Year - LAP
  									  "'JHI_DE32','JHI_DE33','JHI_DE36','JHI_DE37'",
  									  
  									  // FW Activity During Year - IUD
  									  "'JHI_DE38','JHI_DE39'",
  									  
  									  // FW Activity During Year - OP
  									  "'Form6_DE139','Form6_DE140'",
  									  
  									  // FW Activity During Year - CC
  									  "'JHI_DE40','JHI_DE41'",
  									  
  									  // OP Balance 
  									  "'JHI_DE56'",
  									  
  									  // CC Balance
  									  "'JHI_DE63'",  									    									  
									};
	
	List childOrgUnitIDs = new ArrayList();	
	List childOrgUnitNames = new ArrayList();	
		
  	int childOrgUnitCount = 0;  	
  	int count = 0;  	
  	int i=0;
  	  	
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
        
        //rs1 = st1.executeQuery("select name from organisationunit  where id = "+selectedOrgUnitID);
        rs1 = st1.executeQuery("select name from organisationunit  where organisationunitid = "+selectedOrgUnitID);        
        if(rs1.next()) {	selectedOrgUnitName = rs1.getString(1);   }
                
//      rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	   	if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	}  

	   selectedDataPeriodStartDate = startingDate;
				
	   //rs3 =  st3.executeQuery("select id,name from organisationunit where parent = "+selectedOrgUnitID);
	   rs3 =  st3.executeQuery("select organisationunitid,name from organisationunit where parentid = "+selectedOrgUnitID);
	   while(rs3.next())
		 {
		  	Integer tempInt = new Integer(rs3.getInt(1));
		 	childOrgUnitIDs.add(childOrgUnitCount,tempInt);		 	
		 	childOrgUnitNames.add(childOrgUnitCount,rs3.getString(2));		 	
		 	childOrgUnitCount++;
		 } 
		 
	   //rs4=st4.executeQuery("select organisationunit.name,organisationunit.id  FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
	   rs4=st4.executeQuery("select organisationunit.name,organisationunit.organisationunitid  FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");	
       if(rs4.next()) {	motherPHC = rs4.getString(1);  motherPHCID = rs4.getInt(2);   }	
        
	   //rs6=st6.executeQuery("select organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+motherPHCID+")");
	   rs6=st6.executeQuery("select organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+motherPHCID+")");	
       if(rs6.next()) {	talukName = rs6.getString(1);   }	 
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
		<TITLE>CONSOLIDATION REPORT OF FAMILY WELFARE ACTIVITIES</TITLE>
	</HEAD>
	<BODY>
		<BR>
		<P align="center"><FONT face="Arial" size="3"><B>CONSOLIDATION REPORT OF FAMILY WELFARE ACTIVITIES FROM THE PHC&nbsp;&nbsp;<u><%=selectedOrgUnitName%></u>
		  &nbsp;&nbsp;MOTHER PHC&nbsp;&nbsp;<u><%=motherPHC%></u><BR>	
		  	FOR THE MONTH OF&nbsp;&nbsp;<u><%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%></u>
		  PANCHAYAT&nbsp;&nbsp;<u><%=talukName%></u></B></FONT></P>
		<BR><BR>
		<TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  			<TR>
  				<TD align="center" width="2%" rowSpan=2><B><FONT face="Arial" size="2">SL. NO</FONT></B></TD>
    				<TD align="center" width="34%" rowSpan=2><B><FONT face="Arial" size="2">NAME OF PHC</FONT></B></TD>
    				<TD align="center" width="4%" rowspan="2"><FONT face="Arial" size="2"><B><BR>C<BR>O<BR>D<BR>E<BR> <BR>N<BR>O<B></font></TD>
    				<TD align="center" width="4%" rowspan="2"><FONT face="Arial" size="2"><B>P<BR>O<BR>P<BR>U<BR>L<BR>A<BR> T<BR>I<BR>O<BR>N<B></font></TD>
    				<TD align="center" width="24%" colspan="8"><B><FONT face="Arial" size="2">FW ACTIVITY DURING THE MONTH</FONT></TD>
    				<TD align="center" width="24%" colspan="8"><b><font face="Arial" size="2">FW ACTIVITY DURING THE YEAR</font></b></TD>
    				<TD align="center" width="4%"><B><FONT face="Arial" size="2">OP<BR>BALA<BR>NCE</FONT></TD>
    				<TD align="center" width="4%"><B><FONT face="Arial" size="2">CC<BR>BALA<BR>NCE</FONT></TD>
    			</TR>
  			<TR>
    				<TD align="center" width="3%"><FONT face="Arial" size="2">VAS</TD>
    				<TD align="center" width="3%"><FONT face="Arial" size="2">MINI</TD>
    				<TD align="center" width="3%"><FONT face="Arial" size="2">PPS</TD>
    				<TD align="center" width="3%"><FONT face="Arial" size="2">LAP</TD>
    				<TD align="center" width="3%"><FONT face="Arial" size="2">TOTAL</TD>
    				<TD align="center" width="3%"><FONT face="Arial" size="2">IUD</TD>
    				<TD align="center" width="3%"><FONT face="Arial" size="2">OP</TD>
    				<TD align="center" width="3%"><FONT face="Arial" size="2">CC</TD>
    				<TD align="center" width="3%"><FONT face="Arial" size="2">VAS</TD>
    				<TD align="center" width="3%"><font face="Arial" size="2">MINI</font></TD>
    				<TD align="center" width="3%"><font face="Arial" size="2">PPS</font></TD>
    				<TD align="center" width="3%"><font face="Arial" size="2">LAP</font></TD>
    				<TD align="center" width="3%"><font face="Arial" size="2">TOTAL</font></TD>
    				<TD align="center" width="3%"><font face="Arial" size="2">IUD</font></TD>
    				<TD align="center" width="3%"><font face="Arial" size="2">OP</font></TD>
    				<TD align="center" width="3%"><font face="Arial" size="2">CC</font></TD>
    				<TD align="center" width="4%">&nbsp;</TD>
    				<TD align="center" width="4%">&nbsp;</TD>
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
					 	if(i>=10 && i<=17)
					 	 {					 	 					 	 	
  							//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.source ="+currentChildID+"  AND dataelement.code in ("+monthlyDataElements[i]+")";
  							query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.sourceid ="+currentChildID+"  AND dataelement.code in ("+monthlyDataElements[i]+")";	
					 	 }  	
						else
						 {															
					 	 	 //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source = "+currentChildID+"  AND dataelement.code in ("+monthlyDataElements[i]+")";
					 	 	 query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid = "+currentChildID+"  AND dataelement.code in ("+monthlyDataElements[i]+")";
						 }		
	
						rs5 = st5.executeQuery(query);
						if(!rs5.next())  {  tempval[i] = 0;	 }
						else   {  tempval[i] = rs5.getInt(1);  }
						total[i] += tempval[i];
					 } 	
			   
				   %>			     				  		             		
  					<TR>
    					<TD width="2%" HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=(count+1)%></font></TD>
    					<TD width="34%" HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=childOrgUnitNames.get(count)%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[1]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[2]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[3]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[4]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[5]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=(tempval[2]+tempval[3]+tempval[4]+tempval[5])%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[6]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[7]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[8]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[9]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[10]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[11]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[12]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=(tempval[9]+tempval[10]+tempval[11]+tempval[12])%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[13]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[14]%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[15]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[16]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[17]%></font></TD>
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
  				
					if(con!=null)  con.close();
				 }
				catch(Exception e)   {  out.println(e.getMessage());   }
       		 } // finally block end		          		
  		%>

			<TR>
				<TD width="2%" HEIGHT="50" align="center"><FONT face="Arial" size="1">&nbsp;</font></TD>
				<TD width="34%" HEIGHT="50" align="center"><FONT face="Arial" size="2">TOTAL</font></TD>
				<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"></font></TD>
				<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[1]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[2]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[3]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[4]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[5]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(total[2]+total[3]+total[4]+total[5])%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[6]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[7]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[8]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[9]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[10]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[11]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[12]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(total[9]+total[10]+total[11]+total[12])%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[13]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[14]%></font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[15]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[16]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[17]%></font></TD>
			</TR>
			</TABLE>
 	</BODY>
</HTML>