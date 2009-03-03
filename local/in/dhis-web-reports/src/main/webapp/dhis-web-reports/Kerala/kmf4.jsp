
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
    
    // For CHC name
    Statement st8=null;
    ResultSet rs8=null;
  
    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/kl_dhis2";
          
	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = Integer.parseInt( selectedId );
	
 
 	  String startingDate  = (String) stack.findValue( "startingPeriod" );
	  String endingDate  = (String) stack.findValue( "endingPeriod" );
 
      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = 	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	String stateName = ""; 
  	String talukName = "";
  	String districtName = "";
  	String CHCName = "";
  	
  	int CHCID = 0;
  	int talukID = 0;
  	int districtID = 0;
  	
  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String DataElements[] = {
	
											"DE for Malaria Paradigm",
											
  											//"DE for Population",
  											"SPD_DE1",
  											
  											// Active BSC, BSE, +ve
  											"MAL_DE18","MAL_DE19","MAL_DE20",

											// Mass Contact - Coll,Exam,+ve
  											"MAL_DE21","MAL_DE22","MAL_DE23",
  											
  											// Passive BSC, BSE, +ve 
  											"MAL_DE24","MAL_DE25","MAL_DE26",
											
											// Age wise Positive PV- (0-4), (5-14), (15+), 
  											"MAL_DE27","MAL_DE28","MAL_DE29",  
  											
  											// PF + MIXED - (0-4), (5-14), (15+),
  											"MAL_DE30","MAL_DE31","MAL_DE32",
											
											// INDIGENOUS CASES
											"MAL_DE33",
  											   											  											
										};
	
	List childOrgUnitIDs = new ArrayList();	
	List childOrgUnitNames = new ArrayList();	
		
  	int childOrgUnitCount = 0;  	
  	int count = 0;  	
  	int i=0;
  	int totPopulation = 0;
  	  	
	int tempval[] = new int[DataElements.length+5];
	int total[] = new int[DataElements.length+5];
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
		st8=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);



        //rs1 = st1.executeQuery("SELECT organisationunit.name FROM organisationunit WHERE id="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.name FROM organisationunit WHERE organisationunitid="+selectedOrgUnitID);        
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);    }
        else  {  selectedOrgUnitName = "";     }  
                
 //       rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	    if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	}  

		selectedDataPeriodStartDate = startingDate;


		//rs8=st8.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
		rs8=st8.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")"); 
		if(rs8.next()) {  CHCID = rs8.getInt(1); CHCName = rs8.getString(2);}
		else { CHCID=0; CHCName = "";}
		
		//rs5=st5.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+CHCID+")");
		rs5=st5.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+CHCID+")"); 
		if(rs5.next()) {  talukID = rs5.getInt(1); talukName = rs5.getString(2);}
		else { talukID=0; talukName = "";}  
				
		//rs6=st6.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+talukID+")");
		rs6=st6.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+talukID+")"); 
		if(rs6.next()) {  districtID = rs6.getInt(1);  districtName = rs6.getString(2); }
		else { districtID = 0; districtName = "";}  

		//rs7=st7.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+districtID+")");
		rs7=st7.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+districtID+")"); 
		if(rs7.next()) {  stateName = rs7.getString(2);}
		else { stateName = "";}  

	    //rs3 =  st3.executeQuery("select id,name from organisationunit where parent = "+selectedOrgUnitID);
	    rs3 =  st3.executeQuery("select organisationunitid,name from organisationunit where parentid = "+selectedOrgUnitID);
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

<html>

	<head>
	<title>MF-4</title>
	</head>
	<body>

<center>
<font face="arial" size=2>NATIONAL VECTOR BORNE DISEASE CONTROL PROGRAMME</font><br>
<font face="arial" size=2>MONTHLY REPORT OF ANTI- MALARIA PROGRAMME OF PRIMARY HEALTH CENTRE</font><br><br>
<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%" id="AutoNumber1">
  <tr>
    <td width="50%" height=30><font face="arial" size=2>Name of State :<%=stateName %></font></td>
    <td width="34%" align="right" height=30><font face="arial" size=2>MF-4</font></td>
  </tr>
  <tr>
    <td width="33%" height=30><font face="arial" size=2>Name of District :<%=districtName %></font></td>
    <td width="34%"  height=30><font face="arial" size=2>NAME OF PH CENTRE:<%=selectedOrgUnitName%></font></td>
  </tr>
  <tr>
    <td width="33%" height=30><font face="arial" size=2>Population (District)</font></td>
    <td width="34%"  height=30><font face="arial" size=2>Population:<%=totPopulation%> </font></td>
  </tr>
 
</table><br><br>
 <%
			
			try
			  {
				  count = 0;
				  while(count < childOrgUnitCount)
				   { 				   
				    if(count%15==0)
				     {%>
				       



<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%" id="AutoNumber2">
  <tr>
    <td width="2%" rowspan="3"align="center"><font face="arial" size=1>S<br>l<br>N<br>o</font></td>
    <td width="33%" rowspan="3"align="center"><font face="arial" size=1>Name of the Basic Health Section</font></td>
    <td width="4%"align="center"><font face="arial" size=1>Phase</font></td>
    <td width="4%" rowspan="3"align="center"><font face="arial" size=1>Population</font></td>
    <td width="9%" colspan="3"align="center"><font face="arial" size=1>Active</font></td>
    <td width="9%" colspan="3"align="center"><font face="arial" size=1>Mass and Contact</font></td>
    <td width="6%" colspan="2"align="center"><font face="arial" size=1>Passive</font></td>
    <td width="12%" colspan="4"align="center"><font face="arial" size=1>Total</font></td>
    <td width="18%" colspan="6"align="center"><font face="arial" size=1>Age-Wise Positive</font></td>
    <td width="3%" rowspan="3"align="center"><font face="arial" size=1>Indigenous<br>Cases</font></td>
  </tr>
  <tr>
    <td width="4%" rowspan="2"align="center"><font face="arial" size=1>Attack Consolidation Maintenance</font></td>
    <td width="6%" colspan="2"align="center"><font face="arial" size=1>Blood slides</font></td>
    <td width="3%" rowspan="2"align="center"><font face="arial" size=1>Positive</font></td>
    <td width="6%" colspan="2"align="center"><font face="arial" size=1>Blood Slides</font></td>
    <td width="3%" rowspan="2"align="center"><font face="arial" size=1>Positive</font></td>
    <td width="6%" colspan="2"align="center"><font face="arial" size=1>Blood Slides</font></td>
    <td width="3%" rowspan="2"align="center"><font face="arial" size=1>Possitive</font></td>
    <td width="6%" colspan="2"align="center"><font face="arial" size=1>Blood slides</font></td>
    <td width="3%" rowspan="2"align="center"><font face="arial" size=1>Positive</font></td>
    <td width="9%" colspan="3"align="center"><font face="arial" size=1>P.V</font></td>
    <td width="9%" colspan="3"align="center"><font face="arial" size=1>P.f. +Mixed</font></td>
  </tr>
  <tr>
    <td width="3%"align="center"><font face="arial" size=1>Collected</font></td>
    <td width="3%"align="center"><font face="arial" size=1>Examined</font></td>
    <td width="3%"align="center"><font face="arial" size=1>Collected</font></td>
    <td width="3%"align="center"><font face="arial" size=1>Examined</font></td>
    <td width="3%"align="center"><font face="arial" size=1>Collected</font></td>
    <td width="3%"align="center"><font face="arial" size=1>Examined</font></td>
    <td width="3%"align="center"><font face="arial" size=1>Collected</font></td>
    <td width="3%"align="center"><font face="arial" size=1>Examined</font></td>
    <td width="3%"align="center"><font face="arial" size=1>0-4</font></td>
    <td width="3%"align="center"><font face="arial" size=1>5-14</font></td>
    <td width="3%"align="center"><font face="arial" size=1>15+</font></td>
    <td width="3%"align="center"><font face="arial" size=1>0-4</font></td>
    <td width="3%"align="center"><font face="arial" size=1>5-14</font></td>
    <td width="3%"align="center"><font face="arial" size=1>15+</font></td>
  </tr>
  <tr>
   <td width="2%"align="center"><font face="arial" size=1>&nbsp;1</font></td>
    <td width="33%"align="center"><font face="arial" size="1">2</font></td>
    <td width="4%"align="center"><font face="arial" size="1">3</font></td>
    <td width="4%"align="center"><font face="arial" size="1">4</font></td>
    <td width="3%"align="center"><font face="arial" size="1">5</font></td>
    <td width="3%"align="center"><font face="arial" size="1">6</font></td>
    <td width="3%"align="center"><font face="arial" size="1">7</font></td>
    <td width="3%"align="center"><font face="arial" size="1">8</font></td>
    <td width="3%"align="center"><font face="arial" size="1">9</font></td>
    <td width="3%"align="center"><font face="arial" size="1">10</font></td>
    <td width="3%"align="center"><font face="arial" size="1">11</font></td>
    <td width="3%"align="center"><font face="arial" size="1">12</font></td>
    <td width="3%"align="center"><font face="arial" size="1">13</font></td>
    <td width="3%"align="center"><font face="arial" size="1">14</font></td>
    <td width="3%"align="center"><font face="arial" size="1">15</font></td>
    <td width="3%"align="center"><font face="arial" size="1">16</font></td>
    <td width="3%"align="center"><font face="arial" size="1">17</font></td>
    <td width="3%"align="center"><font face="arial" size="1">18</font></td>
    <td width="3%"align="center"><font face="arial" size="1">19</font></td>
    <td width="3%"align="center"><font face="arial" size="1">20</font></td>
    <td width="3%"align="center"><font face="arial" size="1">21</font></td>
    <td width="3%"align="center"><font face="arial" size="1">22</font></td>
    <td width="3%"align="center"><font face="arial" size="1">23</font></td>
  </tr>
     <%}
					Integer temp1 = (Integer) childOrgUnitIDs.get(count);
					int currentChildID = temp1.intValue(); 			
					for(i=0;i<DataElements.length;i++)
					 {			
						

						//rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"')  AND datavalue.source ="+currentChildID+" AND dataelement.code like '"+DataElements[i]+"'");
						rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"')  AND datavalue.sourceid ="+currentChildID+" AND dataelement.code like '"+DataElements[i]+"'");
												
						if(!rs4.next())  {  tempval[i] = 0;	 }
						else   {  tempval[i] = rs4.getInt(1);  }
						total[i] += tempval[i];
					 } 	
			   
				   %>			     
  <tr>
    <td width="2%"align="center"><font face="arial" size=1><%=(count+1)%>&nbsp;</font></td>
    <td width="33%"align="center"><font face="arial" size=1><%=childOrgUnitNames.get(count)%>&nbsp;</font></td>
    <td width="4%"align="center"><font face="arial" size=1><%=tempval[0]%>&nbsp;</font></td>
    <td width="4%"align="center"><font face="arial" size=1><%=tempval[1]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[2]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[3]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[4]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[5]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[6]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[7]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[8]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[9]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[10]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[2] + tempval[5] + tempval[8]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[3] + tempval[6] + tempval[9]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[4] + tempval[7] + tempval[10]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[11]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[12]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[13]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[14]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[15]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[16]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=tempval[17]%>&nbsp;</font></td>
  </tr>
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
  					if(rs5!=null)  rs5.close();		if(st5!=null)  st5.close();					
  					if(rs6!=null)  rs6.close();		if(st6!=null)  st6.close();					
  					if(rs7!=null)  rs7.close();		if(st7!=null)  st7.close();	
  					if(rs8!=null)  rs8.close();		if(st8!=null)  st8.close();					
  				
					if(con!=null)  con.close();
				 }
				catch(Exception e)   {  out.println(e.getMessage());   }
       		 } // finally block end		          		
  		%>

  <tr>
   <td width="2%"align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="33%"><b><font face="arial" size="1">Total For PHC</font></td>
    <td width="4%"align="center"><font face="arial" size=1><%=total[0]%>&nbsp;</font></td>
    <td width="4%"align="center"><font face="arial" size=1><%=total[1]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[2]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[3]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[4]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[5]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[6]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[7]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[8]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[9]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[10]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[2] + total[5] + total[8]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[3] + total[6] + total[9]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[4] + total[7] + total[10]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[11]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[12]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[13]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[14]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[15]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[16]%>&nbsp;</font></td>
    <td width="3%"align="center"><font face="arial" size=1><%=total[17]%>&nbsp;</font></td>
  </tr>
  </table>



</body>

</html>