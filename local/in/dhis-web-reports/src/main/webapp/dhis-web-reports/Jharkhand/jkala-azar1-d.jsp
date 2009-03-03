
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
  											
  											"KALA_DE1",
  											"KALA_DE2",  											
  											"KALA_DE3",

											"KALA_DE1",
  											"KALA_DE2",  											
  											"KALA_DE3"
  											
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

        //rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit  WHERE id ="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit  WHERE organisationunitid ="+selectedOrgUnitID);        
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);   }
        else  {  selectedOrgUnitName = "";    }  
                
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
		<TITLE>Monthly Report on Kala-Azar</TITLE>
	</HEAD>
	<BODY>
		<P align="center"><FONT face="Arial" size="3"><b><u>Kala-Azar Report</u></b></font><br></p>
		
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
   							<TD align="center" width="43%" rowSpan=2><b><font face="Arial" size="2">PHC</font></b></TD>
   							<TD align="center" width="18%" colspan="3"><b><font face="Arial" size="2">Report up to previous month</font></b></TD>
   							<TD align="center" width="18%" colspan="3"><b><font face="Arial" size="2">During the month</font></b></TD>
   							<TD align="center" width="18%" colspan="3"><b><font face="Arial" size="2">Progressive Total</font></b></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="6%"><b><font face="Arial" size="2">Case</font></b></TD>
   							<TD align="center" width="6%"><b><font face="Arial" size="2">Death</font></b></TD>
   							<TD align="center" width="6%"><b><font face="Arial" size="2">Treated</font></b></TD>
   							<TD align="center" width="6%"><b><font face="Arial" size="2">Case</font></b></TD>
   							<TD align="center" width="6%"><b><font face="Arial" size="2">Death</font></b></TD>
   							<TD align="center" width="6%"><b><font face="Arial" size="2">Treated</font></b></TD>
   							<TD align="center" width="6%"><b><font face="Arial" size="2">Case</font></b></TD>
   							<TD align="center" width="6%"><b><font face="Arial" size="2">Death</font></b></TD>
   							<TD align="center" width="6%"><b><font face="Arial" size="2">Treated</font></b></TD>
    					</TR>
    						     				     
				     <%}
					Integer temp1 = (Integer) childOrgUnitIDs.get(count);
					int currentChildID = temp1.intValue(); 			
					for(i=0;i<6;i++)
					 {			
						 
						 // For PHC
						 // if(i>=0 && i<=2)
					 	 //	      query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+curYearStart+"' and '"+curYearEnd+"' and periodType = "+periodTypeID+") AND datavalue.source ="+currentChildID+" AND dataelement.code like '"+monthlyDataElements[i]+"'";
					 	 //else query = "SELECT datavalue.value FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period ="+selectedDataPeriodID+" AND datavalue.source ="+currentChildID+" AND dataelement.code like '"+monthlyDataElements[i]+"'";  			


						 // For District
						 if(i>=0 && i<=2)
					 	 	      //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+curYearStart+"' and '"+curYearEnd+"' and periodType = "+periodTypeID+") AND datavalue.source in (select id from organisationunit where parent ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'";
					 	 	      query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+curYearEnd+"' and periodtypeid = "+periodTypeID+") AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'";
					 	 else 
					 	 	//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (select id from organisationunit where parent ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'";
					 	 	query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'";  			
												
						rs4 = st4.executeQuery(query);
						
						if(!rs4.next())  {  tempval[i] = 0;	 }
						else   {  tempval[i] = rs4.getInt(1);  }
						total[i] += tempval[i];
					 } 	
			   
				   %>			     				  		             		
  					
  					<TR>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=(count+1)%>.</font></TD>
    					<TD width="43%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=childOrgUnitNames.get(count)%></font></TD>
    					<TD width="6%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=tempval[0]%></font></TD>
    					<TD width="6%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=tempval[1]%></font></TD>
    					<TD width="6%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=tempval[2]%></font></TD>
    					<TD width="6%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=tempval[3]%></font></TD>
    					<TD width="6%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=tempval[4]%></font></TD>
    					<TD width="6%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=tempval[5]%></font></TD>
    					<TD width="6%"  HEIGHT="20" align="center">&nbsp;</TD>
    					<TD width="6%"  HEIGHT="20" align="center">&nbsp;</TD>
    					<TD width="6%"  HEIGHT="20" align="center">&nbsp;</TD>
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
				<TD width="43%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><b>Total</b></font></TD>
				<TD width="6%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=total[0]%></font></TD>
				<TD width="6%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=total[1]%></font></TD>
				<TD width="6%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=total[2]%></font></TD>
				<TD width="6%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=total[3]%></font></TD>
				<TD width="6%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=total[4]%></font></TD>
				<TD width="6%" HEIGHT="20" align="center"><FONT face="Arial" size="2"><%=total[5]%></font></TD>
				<TD width="6%"  HEIGHT="20" align="center"></TD>
				<TD width="6%"  HEIGHT="20" align="center">&nbsp;</TD>
				<TD width="6%"  HEIGHT="20" align="center">&nbsp;</TD>
			</TR>
		</TABLE>
 	</BODY>
</HTML>