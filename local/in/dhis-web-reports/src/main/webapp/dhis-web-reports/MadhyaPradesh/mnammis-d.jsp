
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
	int selectedOrgUnitID = 	Integer.parseInt( selectedId );
//	int selectedOrgUnitID = 	239;
	
  	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );

//  	String startingDate  = "2006-07-01";
//	String endingDate  = "2006-07-01";;
      
//	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
//	int periodTypeID = 	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = ""; 
  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String monthlyDataElements[] = {
  											"Data Element for Population",
  											
  											// Cases treated at FTD, DDC
  											"MAL_DE4","MAL_DE5",
  											
  											// Active BSC, BSE, +ve (pv + pf)
  											"MAL_DE6","MAL_DE7","MAL_DE8","MAL_DE9",
  											
  											// Passive BSC, BSE, +ve (pv + pf)
  											"MAL_DE10","MAL_DE11","MAL_DE12","MAL_DE13",
  											
  											// Clinically Diagnosed Deaths
  											"MAL_DE42",
  											
  											// RT-within 7 Days
  											"MAL_DE36",
  											
  											// RT - given to +ves
  											"MAL_DE37","MAL_DE38","MAL_DE39","MAL_DE40",
  											
											// Positive Agewise - (0-1), (1-4), (5-14), (>15)
											"MAL_DE28","MAL_DE29","MAL_DE30","MAL_DE31",
											
											// Positive - Male, Female
											"MAL_DE26","MAL_DE27",
											
											// Species
											"MAL_DE32","MAL_DE33","MAL_DE34","MAL_DE35"
																						   											
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
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);    }
        else  {  selectedOrgUnitName = "";           }  
                
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
		<TITLE>NAMMIS-BASE INPUT FORM </TITLE>
	</HEAD>
	<BODY>
		<P align="center"><FONT face="Arial" size="3"><b><u>NAMMIS-BASE INPUT FORM </u></b></font><br></p>


		<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<b><font face="Arial">District</font></b><font face="Arial" size="3"><b> :&nbsp;&nbsp;<%=selectedOrgUnitName%></b></font>
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
  							<TD align="center" width="3%" rowspan="3"><font face="Arial" style="font-size:8pt">Sl. No.</font></TD>
   							<TD align="center" width="22%" rowspan="3"><font face="Arial" style="font-size:8pt">Name of 
                            CHC</font></TD>
   							<TD align="center" width="3%" rowspan="3"><font face="Arial" style="font-size:8pt">Population</font></TD>
   							<TD align="center" width="3%" rowspan="3"><font face="Arial" style="font-size:8pt">Cases Treated at FTD</font></TD>
   							<TD align="center" width="3%" rowspan="3"><font face="Arial" style="font-size:8pt">Cases Treated at DDC</font></TD>
   							<TD align="center" width="9%" colspan="3"><font face="Arial" style="font-size:8pt">Active</font></TD>
   							<TD align="center" width="9%" colspan="3"><font face="Arial" style="font-size:8pt">Passive</font></TD>
   							<TD align="center" width="9%" colspan="3"><font face="Arial" style="font-size:8pt">Total</font></TD>
   							<TD align="center" width="3%" rowspan="3"><font face="Arial" style="font-size:8pt">Clinically Diagnosed Deaths</font></TD>
   							<TD align="center" width="6%" colspan="2"><font face="Arial" style="font-size:8pt">RT-Done</font></TD>
   							<TD align="center" width="12%" colspan="4"><font face="Arial" style="font-size:8pt">Positive Age Wise</font></TD>
   							<TD align="center" width="6%" colspan="2"><font face="Arial" style="font-size:8pt">Positive</font></TD>
   							<TD align="center" width="12%" colspan="4"><font face="Arial" style="font-size:8pt">Species</font></TD>
    					</TR>
    						     				     
  						<TR>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">BSC</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">BSE</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">+ve</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">BSC</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">BSE</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">+ve</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">BSC</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">BSE</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">+ve</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">RT- within 7 Days</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">RT- given to +ves</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">0-1</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">1-4</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">5-14</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">&gt;15</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">Male</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">Female</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">Pv</font></TD>
   							<TD align="center" width="3%" colspan="2"><font face="Arial" style="font-size:8pt">Pf</font></TD>
   							<TD align="center" width="3%" rowspan="2"><font face="Arial" style="font-size:8pt">Mix</font></TD>
    					</TR>
    						     				     
  						<TR>
   							<TD align="center" width="3%"><font face="Arial" style="font-size:8pt">R</font></TD>
   							<TD align="center" width="3%"><font face="Arial" style="font-size:8pt">Rg</font></TD>
    					</TR>
    						     				     
				     <%}
					Integer temp1 = (Integer) childOrgUnitIDs.get(count);
					int currentChildID = temp1.intValue(); 			
					for(i=0;i<monthlyDataElements.length;i++)
					 {			
						rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where organisationunitid in ( select organisationunitid from organisationunit where parentid = "+currentChildID+") or organisationunitid = "+currentChildID+") AND dataelement.code like '"+monthlyDataElements[i]+"'");
												
						if(!rs4.next())  {  tempval[i] = 0;	 }
						else   {  tempval[i] = rs4.getInt(1);  }
						total[i] += tempval[i];
					 } 	
			   
				   %>			     				  		             		
  					
  					<TR>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(count+1)%>.</font></TD>
    					<TD width="22%" HEIGHT="30" align="center"><b><FONT face="Arial" style="font-size:8pt"><%=childOrgUnitNames.get(count)%></font></b></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[0]%></font></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[1]%></font></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[2]%></font></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[3]%></font></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[4]%></font></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(tempval[5]+tempval[6])%></font></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[7]%></font></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[8]%></font></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(tempval[9]+tempval[10])%></font></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(tempval[3]+tempval[7])%></font></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(tempval[4]+tempval[8])%></font></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(tempval[5]+tempval[6]+tempval[9]+tempval[10])%></font></TD>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[11]%></font></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[12]%></font></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(tempval[13]+tempval[14]+tempval[15]+tempval[16])%></font></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[17]%></font></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[18]%></font></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[19]%></font></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[20]%></font></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[21]%></font></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[22]%></font></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[23]%></font></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[24]%></font></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[25]%></font></TD>
    					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=tempval[26]%></font></TD>
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
				<TD width="22%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><b>Total</b></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[0]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[1]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[2]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[3]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[4]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(total[5]+total[6])%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[7]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[8]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(total[9]+total[10])%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(total[3]+total[7])%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(total[4]+total[8])%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(total[5]+total[6]+total[9]+total[10])%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[11]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[12]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=(total[13]+total[14]+total[15]+total[16])%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[17]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[18]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[19]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[20]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[21]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[22]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[23]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[24]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[25]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" style="font-size:8pt"><%=total[26]%></font></TD>
			</TR>
		</TABLE>
 	</BODY>
</HTML>