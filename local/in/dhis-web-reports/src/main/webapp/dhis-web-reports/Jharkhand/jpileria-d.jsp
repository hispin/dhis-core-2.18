
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
											// 2 - 4 Years
  											"FIL_DE1","FIL_DE2",
  											
  											// 5 - 8 Years
  											"FIL_DE3","FIL_DE4",
  											
  											// 9 - 14 Years
  											"FIL_DE5","FIL_DE6",
  											
  											// 15 - 25 Years
  											"FIL_DE7","FIL_DE8",
  											
  											// 26 - 40 Years
  											"FIL_DE9","FIL_DE10",
  											
  											// 41 - 60 Years
  											"FIL_DE11","FIL_DE12",
  											
  											// 61 Years and Above
  											"FIL_DE13","FIL_DE14",
  											
  											"FIL_DE15","FIL_DE16","FIL_DE17","FIL_DE18","FIL_DE19"
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
		<TITLE>&#2313;&#2350;&#2381;&#2352; &#2319;&#2357;&#2306; &#2354;&#2367;&#2306;&#2327; &#2309;&#2344;&#2369;&#2360;&#2366;&#2352; &#2347;&#2366;&#2312;&#2354;&#2375;&#2352;&#2367;&#2351;&#2366; &#2352;&#2379;&#2327;&#2368; &#2325;&#2366; &#2357;&#2367;&#2357;&#2352;&#2339;</TITLE>
	</HEAD>
	<BODY>
		<P align="center"><FONT face="Arial" size="3"><b>
				&#2332;&#2367;&#2354;&#2366; &#2350;&#2375;&#2306; &#2313;&#2350;&#2381;&#2352; &#2319;&#2357;&#2306; &#2354;&#2367;&#2306;&#2327; &#2309;&#2344;&#2369;&#2360;&#2366;&#2352; &#2347;&#2366;&#2312;&#2354;&#2375;&#2352;&#2367;&#2351;&#2366; &#2352;&#2379;&#2327;&#2368; &#2325;&#2366; &#2357;&#2367;&#2357;&#2352;&#2339;			
		</font></b><br></p>
			<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="3"><b>&#2332;&#2367;&#2354;&#2366; :&nbsp;&nbsp;<%=selectedOrgUnitName%></b></font>
    				</td>
    				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2"><b>&#2350;&#2366;&#2361; :&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%> </b></font>
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
				    if(count%16==0)
				     {
				       if(count != 0) {%></table><div align="right"><font face="Arial" size="1"><i>(page contd.)</i></font></div> <br><%}%>
				     </table><br>
				     <TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  						<TR>
  							<TD align="center" width="3%" rowSpan=4><b><font face="Arial" size="2">&#2325;&#2381;&#2352;.&#2360;&#2306;.</font></b></TD>
   							<TD align="center" width="24%" rowSpan=4><b><font face="Arial" size="2">&#2346;&#2381;&#2352;.&#2360;&#2381;&#2357;&#2366;.&#2325;&#2375;&#2344;&#2381;&#2342;&#2381;&#2352;&#2379;&#2306; &#2325;&#2366; &#2344;&#2366;&#2350;</font></b></TD>
   							<TD align="center" width="42%" colspan="14"><b><font face="Arial" size="2">&#2313;&#2350;&#2381;&#2352; &#2319;&#2357;&#2306; &#2354;&#2367;&#2306;&#2327;</font></b></TD>
   							<TD align="center" width="4%" rowSpan=4><b><font face="Arial" size="2">&#2325;&#2369;&#2354; &#2352;&#2379;&#2327;</font></b></TD>
   							<TD align="center" width="15%" colspan="5" rowspan="2"><b><font face="Arial" size="2">&#2346;&#2381;&#2352;&#2349;&#2366;&#2357;&#2367;&#2340; &#2309;&#2306;&#2327; &#2325;&#2375; &#2309;&#2344;&#2369;&#2360;&#2366;&#2352; (&#2352;&#2379;&#2327;&#2368; &#2360;&#2306;&#2326;&#2381;&#2351;&#2366;)</font></b></TD>
   							<TD align="center" width="6%" colspan="2" rowspan="2">&nbsp;</TD>
   							<TD align="center" width="6%" rowspan="4"><b><font face="Arial" size="2">&#2309;&#2349;&#2381;&#2351;&#2369;&#2325;&#2381;&#2340;&#2367;</font></b></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="2">2-4yrs</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="2">5-8yrs</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="2">9-14yrs</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="2">15-25yrs</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="2">26-40yrs</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="2">41-60yrs</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="2">61 & above</font></b></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">M</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">F</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">M</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">F</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">M</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">F</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">M</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">F</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">M</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">F</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">M</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">F</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">M</font></b></TD>
   							<TD align="center" width="3%" rowSpan=2><b><font face="Arial" size="2">F</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" size="2">&#2346;&#2376;&#2352;</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" size="2">&#2348;&#2366;&#2305;&#2361;</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" size="2">&#2360;&#2381;&#2340;&#2344;</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" size="2">&#2309;&#2339;&#2381;&#2337;&#2325;&#2379;&#2359;</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" size="2">&#2309;&#2344;&#2381;&#2351;</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="2">&#2325;&#2369;&#2354;</font></b></TD>
   						</TR>  		
  						<TR>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">M</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">F</font></b></TD>
   						</TR>  		
    					<TR>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>1</b></i></FONT></TD>
    						<TD width="24%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>2</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><b><i><font face="Arial" size="2">3</font></i></b></TD>
    						<TD width="3%" HEIGHT="30" align="center"><b><i><font face="Arial" size="2">4</font></i></b></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>5</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>6</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>7</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>8</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>9</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>10</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>11</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>12</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>13</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>14</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>15</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>16</b></i></FONT></TD>
    						<TD width="4%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>17</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><b><i><font face="Arial" size="2">18</font></i></b></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>19</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>20</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>21</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>22</b></i></FONT></TD>
    						<TD width="3%" HEIGHT="30" align="center"><b><i><font face="Arial" size="2">23</font></i></b></TD>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><i><b>24</b></i></FONT></TD>
    						<TD width="6%" HEIGHT="30" align="center"><b><i><font face="Arial" size="2">25</font></i></b></TD>
    					</TR>	
    						     				     
				     <%}
					Integer temp1 = (Integer) childOrgUnitIDs.get(count);
					int currentChildID = temp1.intValue(); 			
					for(i=0;i<monthlyDataElements.length;i++)
					 {			
						//rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (select id from organisationunit where parent ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'");
						rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+currentChildID+")  AND dataelement.code like '"+monthlyDataElements[i]+"'");
						//SELECT datavalue.value FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period ="+selectedDataPeriodID+" AND datavalue.source ="+currentChildID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");
						//SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period ="+selectedDataPeriodID+" AND datavalue.source in (select id from organisationunit where parent ="+currentChildID+") AND dataelement.code like '"+monthlyDataElements[i]+"'");
						//SELECT datavalue.value FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period="+selectedDataPeriodID+" AND datavalue.source="+currentChildID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");
						//SELECT sum(datavalue.value) FROM (organisationunit INNER JOIN datavalue ON organisationunit.id = datavalue.source) INNER JOIN period ON datavalue.period = period.id WHERE organisationunit.parent="+selectedOrgUnitID+" AND period.startDate='"+selectedDataPeriodStartDate+"' AND periodType="+periodTypeID+" AND datavalue.dataElement="+dataElementIDs[i])
												
						if(!rs4.next())  {  tempval[i] = 0;	 }
						else   {  tempval[i] = rs4.getInt(1);  }
						total[i] += tempval[i];
					 } 	
			   
				   %>			     				  		             		
  					
  					<TR>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=(count+1)%></font></TD>
    					<TD width="24%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=childOrgUnitNames.get(count)%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[0]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[1]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[2]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[3]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[4]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[5]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[6]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[7]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[8]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[9]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[10]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[11]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[12]%></font></TD>
    					<TD width="3%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[13]%></font></TD>
    						<TD width="4%" HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=(tempval[0]+tempval[1]+tempval[2]+tempval[3]+tempval[4]+tempval[5]+tempval[6]+tempval[7]+tempval[8]+tempval[9]+tempval[10]+tempval[11]+tempval[12]+tempval[13])%></font></TD>
    					<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[14]%></font></TD>
    					<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[15]%></font></TD>
    					<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[16]%></font></TD>
    					<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[17]%></font></TD>
    					<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=tempval[18]%></font></TD>
    						<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=(tempval[0]+tempval[2]+tempval[4]+tempval[6]+tempval[8]+tempval[10]+tempval[12])%></font></TD>
    						<TD width="3%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"><%=(tempval[1]+tempval[3]+tempval[5]+tempval[7]+tempval[9]+tempval[11]+tempval[13])%></font></TD>
    					<TD width="6%"  HEIGHT="20" align="center"><FONT face="Arial" size="1"></font></TD>
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
				<TD width="24%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><b>&#2325;&#2369;&#2354;</b></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[0]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[1]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[2]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[3]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[4]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[5]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[6]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[7]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[8]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[9]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[10]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[11]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[12]%></font></TD>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[13]%></font></TD>
					<TD width="4%" HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[0]+total[1]+total[2]+total[3]+total[4]+total[5]+total[6]+total[7]+total[8]+total[9]+total[10]+total[11]+total[12]+total[13])%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[14]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[15]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[16]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[17]%></font></TD>
				<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[18]%></font></TD>
					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[0]+total[2]+total[4]+total[6]+total[8]+total[10]+total[12])%></font></TD>
					<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[1]+total[3]+total[5]+total[7]+total[9]+total[11]+total[13])%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"></TD>
			</TR>
		</TABLE>
 	</BODY>
</HTML>