
<%@ page import="java.sql.*,java.util.*" %>
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
          
//	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

//	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = 9;
//	Integer.parseInt( selectedId );
	

	  String startingDate  = "2006-04-01";
//	  (String) stack.findValue( "startingPeriod" );
	  String endingDate  = "2006-04-01";
//	  (String) stack.findValue( "endingPeriod" );

      
//	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = 1;
//	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = ""; 
  	String motherPHC=""; 	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String monthlyDataElements[] = {
  									  // Code No  									  
  									  "'Form6_DE'",
  									  
  									  // Population
  									  "'Form6_DE'",
  									  
  									  // NMEP - ACTIVE
  									  "'JHI_DE1'",
  									  
  									  // NMEP - PASSIVE
  									  "'Form6_DE'",
  									  
  									  // NMEP - CONTACT
  									  "'JHI_DE3'",
  									  
  									  // NMEP - MASS
  									  "'JHI_DE3'",
  									  
  									  // NMEP - TAB ISSUE
  									  "'JHI_DE2'",
  									  
  									  // NMEP - TAB BALANCE
  									  "'JHI_DE4'",
  									  
  									  // VITAL STATISTICS - BIRTH MALE
  									  "'JPHN/JHI_DE142'",
  									  
  									  // VITAL STATISTICS - BIRTH FEMALE
  									  "'JPHN/JHI_DE143'",
  									  
  									  // VITAL STATISTICS - DEATH MALE
  									  "'JPHN/JHI_DE144'",
  									  
  									  // VITAL STATISTICS - DEATH FEMALE
  									  "'JPHN/JHI_DEE145'",
  									  
  									  // IEC GROUPTALK
  									  "'JHI_DE19'",
  									  
  									  // IEC GROUP DISCUSSION
  									  "'JPHN/JHI_DE109'",
  									  
  									  // COMMUNICABLE DISEASE - CHLORINATION
  									  "'JPHN/JHI_DE98'",
  									  
  									  // COMMUNICABLE DISEASE - DIARRHOEA
  									  "'JPHN/JHI_DE34'",
  									  
  									  // COMMUNICABLE DISEASE - DYSENTRY 
  									  "'JPHN/JHI_DE35'",
  									  
  									  // COMMUNICABLE DISEASE - CHOLERA
  									  "'JPHN/JHI_DE36'",  					

  									  // COMMUNICABLE DISEASE - MUMPS
  									  "'JPHN/JHI_DE11'",  					

  									  // COMMUNICABLE DISEASE - TB
  									  "'Form6_DE'",  					

  									  // COMMUNICABLE DISEASE - MEASLES
  									  "'JPHN/JHI_DE10'",  					

  									  // COMMUNICABLE DISEASE - DENGU
  									  "'JPHN/JHI_DE18'",  					

  									  // COMMUNICABLE DISEASE - CHICKENPOX
  									  "'JPHN/JHI_DE27'",  					

  									  // COMMUNICABLE DISEASE - TYPHOID
  									  "'JPHN/JHI_DE14'",  					
  									  
   									  // COMMUNICABLE DISEASE - LEPTO
  									  "'JPHN/JHI_DE20'",  					

  									  // COMMUNICABLE DISEASE - ARI
  									  "'JPHN/JHI_DE16'",  					

									  // MSS - MEETING
  									  "'JHI_DE26'",  				
  									  
  									  // MSS - ATTENDENCE
  									  "'JHI_DE27'",  				
  									  
  									  // MOTHERS MEETING - MEETING
  									  "'JHI_DE28'",  			

  									  // MOTHERS MEETING - ATTENDENCE
  									  "'JHI_DE29'",  			
  									  
									  // ORS - ISSUE
  									  "'JPHN/JHI_DE152'",
  									  
  									  // ORS - BALANCE
  									  "'JPHN/JHI_DE153'",  			
  					
				    									  
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
%>


<HTML>
	<HEAD>
		<TITLE>CONSOLIDATION REPORT OF NMEP VITAL STATISTIC COMMUNICABLE DISEASE IEC, MSS, ORS, ISSUE ETC</TITLE>
	</HEAD>
	<BODY>
		<br>
		<b>
		<P align="center"><FONT face="Arial" size="3">CONSOLIDATION REPORT OF NMEP VITAL STATISTIC COMMUNICABLE DISEASE IEC, MSS, ORS, ISSUE ETC<BR>
			FOR THE MONTH OF&nbsp;&nbsp;<u><%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%></u>
		&nbsp;&nbsp;NAME OF PHC&nbsp;&nbsp;<u><%=selectedOrgUnitName%></u>
		&nbsp;&nbsp;MOTHER PHC&nbsp;&nbsp;<u><%=motherPHC%></u></FONT></P>
		</b>
		
		<TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  			<TR>
  				<TD align="center" width="2%" rowSpan=3><B><FONT face="Arial" size="2">SL. NO</FONT></B></TD>
   				<TD align="center" width="29%" rowSpan=3><B><FONT face="Arial" size="2">NAME &amp; DESIGNATION OF STAFF</FONT></B></TD>
   				<TD align="center" width="3%" rowspan="3"><FONT face="Arial" size="2"><BR>C<BR>O<BR>D<BR>E<BR> <BR>N<BR>O</font></TD>
				<TD align="center" width="4%" rowspan="3"><FONT face="Arial" size="2">P<BR>O<BR>P<BR>U<BR>L<BR>A<BR> T<BR>I<BR>O<BR>N</font></TD>
   				<TD align="center" width="14%" colspan="7"><B><FONT face="Arial" size="2">NMEP</FONT></TD>
   				<TD align="center" width="8%" colspan="4"><B><FONT face="Arial" size="2">VITAL STSTISTICS</FONT></TD>
   				<TD align="center" width="4%" colspan="2"><B><FONT face="Arial" size="2">IEC</FONT></TD>
   				<TD align="center" width="24%" colspan="12"><B><FONT face="Arial" size="2">COMMUNICABLE DISEASE</FONT></TD>
   				<TD align="center" width="4%" colspan="2"><B><FONT face="Arial" size="2">MISS</FONT></TD>
   				<TD align="center" width="4%" colspan="2"><B><FONT face="Arial" size="2">MOTHER'S MEETING</FONT></TD>
   				<TD align="center" width="4%" colspan="2"><B><FONT face="Arial" size="2">ORS</FONT></TD>
   			</TR>
  			<TR>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">A<BR>C<BR>T<BR>I<BR>V<BR>E</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">P<BR>A<BR>S<BR>S<BR>I<BR>V<BR>E</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">C<BR>O<BR>N<BR>T<BR>A<BR>C<BR>T</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">M<BR>A<BR>S<BR>S</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">T<BR>O<BR>T<BR>A<BR>L</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">T<BR>A<BR>B<BR> <BR>I<BR>S<BR>S<BR>U<BR>E</font></TD>
				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">T<BR>A<BR>B<BR> <BR>B<BR>A<BR>L<BR>A<BR>N<BR>C<BR>E</font></TD>
   				<TD align="center" width="2%" colspan="2"><FONT face="Arial" size="2">B<BR>I<BR>R<BR>T<BR>H</font></TD>
   				<TD align="center" width="2%" colspan="2"><FONT face="Arial" size="2">D<BR>E<BR>A<BR>T<BR>H</font></TD>
				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">G<BR>R<BR>O<BR>U<BR>P<BR> <BR>T<BR>A<BR>L<BR>K</font></TD>
				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">G<BR>R<BR>O<BR>U<BR>P<BR> <BR>D<BR>I<BR>S<BR>C<BR>U<BR>S<BR>S<BR>I<BR>O<BR>N</font></TD>
				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">C<BR>H<BR>L<BR>O<BR>R<BR>I<BR>N<BR>A<BR>T<BR>I<BR>O<BR>N</font></TD>
  				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">D<BR>I<BR>A<BR>R<BR>R<BR>H<BR>O<BR>E<BR>A</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">D<BR>Y<BR>S<BR>E<BR>N<BR>T<BR>R<BR>Y</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">C<BR>H<BR>O<BR>L<BR>E<BR>R<BR>A</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">M<BR>U<BR>M<BR>P<BR>S</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">T<BR>B</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">M<BR>E<BR>A<BR>S<BR>E<BR>L<BR>S</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">D<BR>E<BR>N<BR>G<BR>U</font></TD>
				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">C<BR>H<BR>I<BR>C<BR>K<BR>E<BR>N<BR> <BR>P<BR>O<BR>X</font></TD>
  				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">T<BR>Y<BR>P<BR>H<BR>O<BR>I<BR>D</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">L<BR>E<BR>P<BR>T<BR>O</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">A<BR>R<BR>I</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">M<BR>E<BR>E<BR>T<BR>I<BR>N<BR>G</font></TD>
				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">A<BR>T<BR>T<BR>E<BR>N<BR>D<BR>E<BR>N<BR>C<BR>E</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">M<BR>E<BR>E<BR>T<BR>I<BR>N<BR>G</font></TD>
				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">A<BR>T<BR>T<BR>E<BR>N<BR>D<BR>E<BR>N<BR>C<BR>E</font></TD>
  				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">I<BR>S<BR>S<BR>U<BR>E</font></TD>
   				<TD align="center" width="2%" rowspan="2"><FONT face="Arial" size="2">B<BR>A<BR>L<BR>A<BR>N<BR>C<BR>E</font></TD>
   			</TR>    		
  			<TR>
    			<TD align="center" width="2%"><font face="Arial" size="2">M</font></TD>
    			<TD align="center" width="2%"><font face="Arial" size="2">F</font></TD>
    			<TD align="center" width="2%"><font face="Arial" size="2">M</font></TD>
    			<TD align="center" width="2%"><font face="Arial" size="2">F</font></TD>
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
						//rs5 = st5.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source = "+currentChildID+"  AND dataelement.code in ("+monthlyDataElements[i]+")");
						rs5 = st5.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid = "+currentChildID+"  AND dataelement.code in ("+monthlyDataElements[i]+")");
						
						// "SELECT datavalue.value FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period="+selectedDataPeriodID+" AND datavalue.source="+currentChildID+" AND dataelement.name like '"+monthlyDataElements[i]+"'");
						if(!rs5.next())  {  tempval[i] = 0;	 }
						else   {  tempval[i] = rs5.getInt(1);  }
						total[i] += tempval[i];
					 } 	
			   
				   %>			     				  		             		
  					<TR>
    					<TD width="2%" HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=(count+1)%></font></TD>
    					<TD width="29%" HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=childOrgUnitNames.get(count)%></font></TD>
    					<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"></font></TD>
    					<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[1]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[2]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[3]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[4]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[5]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=(tempval[2]+tempval[3]+tempval[4]+tempval[5])%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[6]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[7]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[8]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[9]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[10]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[11]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[12]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[13]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[14]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[15]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[16]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[17]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[18]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[19]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[20]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[21]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[22]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[23]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[24]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[25]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[26]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[27]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[28]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[29]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[30]%></font></TD>
    					<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="2"><%=tempval[31]%></font></TD>
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
				<TD width="29%" HEIGHT="50" align="center"><FONT face="Arial" size="2">TOTAL</font></TD>
				<TD width="3%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"></font></TD>
				<TD width="4%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[1]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[2]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[3]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[4]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[5]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=(total[2]+total[3]+total[4]+total[5])%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[6]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[7]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[8]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[9]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[10]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[11]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[12]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[13]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[14]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[15]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[16]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[17]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[18]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[19]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[20]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[21]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[22]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[23]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[24]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[25]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[26]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[27]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[28]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[29]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[30]%></font></TD>
				<TD width="2%"  HEIGHT="50" align="center"><FONT face="Arial" size="1"><%=total[31]%></font></TD>
			</TR>
			</TABLE>
 	</BODY>
</HTML>