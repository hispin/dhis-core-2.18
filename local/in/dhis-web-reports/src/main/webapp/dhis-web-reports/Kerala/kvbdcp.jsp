
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

    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/kl_dhis2";
          
	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = 	Integer.parseInt( selectedId );
	

	String startingDate  =   (String) stack.findValue( "startingPeriod" );
	String endingDate  =   (String) stack.findValue( "endingPeriod" );

      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = 	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = ""; 
  	String motherPHC=""; 	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" }; 
  	String query = ""; 		

	String monthlyDataElements[] = {
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'",
 										"'Some_DE'"
 
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

        //rs1 = st1.executeQuery("select name from organisationunit  where id = "+selectedOrgUnitID);
        rs1 = st1.executeQuery("select name from organisationunit  where organisationunitid = "+selectedOrgUnitID);        
        if(rs1.next()) {	selectedOrgUnitName = rs1.getString(1);   }
                
//        rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	   if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	}  


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
		 
	   //rs4=st4.executeQuery("select organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");	
       rs4=st4.executeQuery("select organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");
       if(rs4.next()) {	motherPHC = rs4.getString(1);   }		 
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
		<TITLE>VBDCP - Report</TITLE>
	</HEAD>
	<BODY>
		<P align="center"><FONT face="Arial" size="3"><b>VBDCP - Report of PHC&nbsp;&nbsp;<u><%=selectedOrgUnitName%></u>&nbsp;&nbsp;for the month of&nbsp;&nbsp;<u><%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%></u>
 </FONT></b></P>
		
		<TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  			<TR>
  				<TD align="center" width="2%" rowSpan=2><B><FONT face="Arial" size="2">SL. NO</FONT></B></TD>
    				<TD align="center" width="30%" rowSpan=2><B><FONT face="Arial" size="2">NAME of JHI &amp; Basic Section</FONT></B></TD>
    				<TD align="center" width="16%" colspan="4"><b><font face="Arial" size="2">Houses</font></b></TD>
    				<TD align="center" width="24%" colspan="14"><B><FONT face="Arial" size="2">B/S Collection</FONT></TD>
    				<TD align="center" width="28%" colspan="19"><b><font face="Arial" size="2">Stock Position of 4AQ</font></b></TD>
    			</TR>
  			<TR>
  					<TD align="center" width="4%"><FONT face="Arial" size="2">B<BR>a<BR>s<BR>i<BR>c<BR> <BR>S<BR>e<BR>c<BR>t<BR>i<BR>o<BR>n<BR> <BR>C<BR>o<BR>d<BR>e<BR>N<BR>o<BR></font></TD>
    				<TD align="center" width="4%"><FONT face="Arial" size="2">P<BR>o<BR>p<BR>u<BR>l<BR>a<BR>t<BR>i<BR>o<BR>n</font></TD>
    					<TD align="center" width="4%"><FONT face="Arial" size="2">A<BR>l<BR>l<BR>o<BR>t<BR>t<BR>e<BR>d<BR> <BR>H<BR>o<BR>u<BR>s<BR>e<BR>s</font></TD>
    					<TD align="center" width="4%"><FONT face="Arial" size="2">V<BR>i<BR>s<BR>i<BR>t<BR>e<BR>d<BR> <BR>H<BR>o<BR>u<BR>s<BR>e<BR>s</font></TD>
    					<TD align="center" width="4%"><FONT face="Arial" size="2">M<br>o<br>n<br>t<br>h<br>l<br>y<br> <br>T<br>a<br>r<br>g<br>e<br>t</font></TD>
    					<TD align="center" width="4%" colspan="5"><FONT face="Arial" size="2">M<br>o<br>n<br>t<br>h<br>l<br>y<br> <br>a<br>c<br>h<br>i<br>e<br>v<br>e<br>m<br>e<br>n<br>t</font></TD>
    					<TD align="center" width="4%"><FONT face="Arial" size="2">Y<br>e<br>a<br>r<br>l<br>y<br> <br>T<br>a<br>r<br>g<br>e<br>t</font></TD>
    					<TD align="center" width="4%"><FONT face="Arial" size="2">Y<br>e<br>a<br>r<br>l<br>y<br> <br>a<br>c<br>h<br>i<br>e<br>v<br>e<br>m<br>e<br>n<br>t</font></TD>
    				<TD align="center" width="4%" colspan="5"><FONT face="Arial" size="2">P<br>e<br>r<br>c<br>e<br>n<br>t<br>a<br>g<br>e</font></TD>
    					<TD align="center" width="4%"><FONT face="Arial" size="2">S<br>p<br>e<br>c<br>i<br>a<br>l<br> <br>B<br>/<br>S<br> <br>C<br>o<br>l<br>l<br>e<br>c<br>t<br>i<br>o<br>n</font></TD>
    					<TD align="center" width="4%"><p><FONT face="Arial" size="2">M<br>o<br>n<br>t<br>h<br>l<br>y<br> <br>I<br>s<br>s<br>u<br>e</font></TD>
    					<TD align="center" width="4%" colspan="5"><FONT face="Arial" size="2">Y<br>e<br>a<br>r<br>l<br>y<br> <br>I<br>s<br>s<br>u<br>e</font></TD>
    					<TD align="center" width="4%"><FONT face="Arial" size="2">O<br>p<br>e<br>n<br>i<br>n<br>g<br> <br>B<br>a<br>l<br>a<br>n<br>c<br>e</font></TD>
    				<TD align="center" width="4%" colspan="5"><FONT face="Arial" size="2">R<br>e<br>c<br>e<br>i<br>p<br>t</font></TD>
    				<TD align="center" width="4%"><FONT face="Arial" size="2">T<br>o<br>t<br>a<br>l</font></TD>
    				<TD align="center" width="4%" colspan="5"><FONT face="Arial" size="2">I<br>s<br>s<br>u<br>e</font></TD>
    				<TD align="center" width="4%"><FONT face="Arial" size="2">B<br>a<br>l<br>a<br>n<br>c<br>e</font></TD>
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
						
						if(i==6 || i==7 || i==11)
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
    					<TD width="30%" HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=childOrgUnitNames.get(count)%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[1]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[2]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[3]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="2"><%=tempval[4]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[5]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="2"><%=tempval[6]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="2"><%=tempval[7]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[8]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="2"><%=tempval[9]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="2"><%=tempval[10]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[11]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center" colspan="5"><FONT face="Arial" size="2"><%=tempval[12]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[13]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center" colspan="5"><FONT face="Arial" size="2"><%=tempval[14]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[15]%></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="2"><%=tempval[16]%></font></TD>
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
  				
					if(con!=null)  con.close();
				 }
				catch(Exception e)   {  out.println(e.getMessage());   }
       		 } // finally block end		          		
  		%>

			<TR>
				<TD width="2%" HEIGHT="50" align="center"><FONT face="Arial" size="1">&nbsp;</font></TD>
				<TD width="30%" HEIGHT="50" align="center"><FONT face="Arial" size="2">TOTAL</font></TD>
				<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"></font></TD>
				<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[1]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[2]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[3]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="2"><FONT face="Arial" size="1"><%=total[4]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="1"><%=total[5]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="2"><FONT face="Arial" size="1"><%=total[6]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="2"><FONT face="Arial" size="1"><%=total[7]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="1"><%=total[8]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="2"><FONT face="Arial" size="1"><%=total[9]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="2"><FONT face="Arial" size="1"><%=total[10]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="1"><%=total[11]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="1"><%=total[12]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="1"><%=total[13]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="1"><%=total[14]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="3"><FONT face="Arial" size="1"><%=total[15]%></font></TD>
				<TD width="4%"  HEIGHT="50" align="center" colspan="2"><FONT face="Arial" size="1"><%=total[16]%></font></TD>
			</TR>
			</TABLE>
 	</BODY>
</HTML>