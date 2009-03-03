<%@ page import="java.sql.*" %>
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
    
    //For finding monthly values
    Statement st3=null;
    ResultSet rs3=null;
    
    //For finding cumulative values
    Statement st4=null;
    ResultSet rs4=null;
    
    //For finding blockphc name and id 
    Statement st5=null;
    ResultSet rs5=null;
    
    //For finding taluk name and id
    Statement st6=null;
    ResultSet rs6=null;
    
    //For finding district name and id
    Statement st7=null;
    ResultSet rs7=null;
    
    //For finding state name and id
    Statement st8=null;
    ResultSet rs8=null;
    
    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/kl_dhis2";
          

	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = Integer.parseInt( selectedId );
	

	  String startingDate  =   (String) stack.findValue( "startingPeriod" );
	  String endingDate  = 	  (String) stack.findValue( "endingPeriod" );


      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	  	  	
  	String monthlyDataElements[] = {
							
										//HOUSES
										"'JPHN/JHI_DE1'",
										"'JPHN/JHI_DE2'",
										//ELIGIBLE COUPLES
										"'JPHN/JHI_DE3'",
										"'FORM6_DE130'",
										"'JPHN/JHI_DE4'",
										//NVBDCP
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE5'",
										"'JPHN/JHI_DE6'",
										"'JPHN/JHI_DE7'",
										"'JPHN/JHI_DE8'",
										"'JPHN/JHI_DE'",
										//COMMUNICABLE DIESEASES
										"'JPHN/JHI_DE10'",
										"'JPHN/JHI_DE11'",
										"'JPHN/JHI_DE12'",
										"'JPHN/JHI_DE13'",
										"'JPHN/JHI_DE14'",
										"'JPHN/JHI_DE15'",
										"'JPHN/JHI_DE16'",
										"'JPHN/JHI_DE17'",
										"'JPHN/JHI_DE18'",
										"'JPHN/JHI_DE19'",
										"'JPHN/JHI_DE20'",
										"'JPHN/JHI_DE21'",
										"'JPHN/JHI_DE22'",
										"'JPHN/JHI_DE23'",
										"'JPHN/JHI_DE24'",
										"'JPHN/JHI_DE25'",
										"'JPHN/JHI_DE26'",
										"'JPHN/JHI_DE27'",
										"'JPHN/JHI_DE28'",
										"'JPHN/JHI_DE29'",
										"'JPHN/JHI_DE30'",
										"'JPHN/JHI_DE31'",
										"'JPHN/JHI_DE32'",
										"'JPHN/JHI_DE33'",
										"'JPHN/JHI_DE34'",
										"'JPHN/JHI_DE35'",
										"'JPHN/JHI_DE36'",
										"'JPHN/JHI_DE37'",
										//RNTCP(TB)
										"'JPHN/JHI_DE38'",
										"'JPHN/JHI_DE39'",
										"'JPHN/JHI_DE40'",
										"'JPHN/JHI_DE41'",
										"'JPHN/JHI_DE42'",
										"'JPHN/JHI_DE43'",
										"'JPHN/JHI_DE44'",
										//DIARRHOEAL DIESEASES
										"'JPHN/JHI_DE45'",
										"'JPHN/JHI_DE46'",
										"'JPHN/JHI_DE47'",
										"'JPHN/JHI_DE48'",
										"'JPHN/JHI_DE49'",
										"'JPHN/JHI_DE50'",
										//CHOLERA
										"'JPHN/JHI_DE51'",
										"'JPHN/JHI_DE52'",
										"'JPHN/JHI_DE53'",
										"'JPHN/JHI_DE54'",
										"'JPHN/JHI_DE55'",
										"'JPHN/JHI_DE56'",
										//DYSENTRY
										"'JPHN/JHI_DE57'",
										"'JPHN/JHI_DE58'",
										"'JPHN/JHI_DE59'",
										"'JPHN/JHI_DE60'",
										"'JPHN/JHI_DE61'",
										"'JPHN/JHI_DE62'",
										//PERSISTENT DIARRHOEA
										"'JPHN/JHI_DE63'",
										"'JPHN/JHI_DE64'",
										"'JPHN/JHI_DE65'",
										"'JPHN/JHI_DE66'",
										"'JPHN/JHI_DE67'",
										"'JPHN/JHI_DE68'",
										//NON COMMUNICABLE DISEASES
										"'JPHN/JHI_DE69'",
										"'JPHN/JHI_DE70'",
										"'JPHN/JHI_DE71'",
										"'JPHN/JHI_DE72'",
										"'JPHN/JHI_DE73'",
										"'JPHN/JHI_DE74'",
										//STERILIZATION
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE75'",
										"'JPHN/JHI_DE76'",
										"'JPHN/JHI_DE77'",
										"'JPHN/JHI_DE78'",
										"'JPHN/JHI_DE79'",
										"'JPHN/JHI_DE80'",
										"'JPHN/JHI_DE81'",
										"'JPHN/JHI_DE82'",
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE83'",
										"'JPHN/JHI_DE84'",
										//OP USERS
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE85'",
										"'JPHN/JHI_DE86'",
										"'JPHN/JHI_DE87'",
										"'JPHN/JHI_DE88'",
										//CC USERS
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE91'",
										"'JPHN/JHI_DE92'",
										"'JPHN/JHI_DE93'",
										"'JPHN/JHI_DE94'",
										//REPORT OF ACTIVITIES
										"'JPHN/JHI_DE97'",
										"'JPHN/JHI_DE98'",
										"'JPHN/JHI_DE99'",
										"'JPHN/JHI_DE100'",
										"'JPHN/JHI_DE101'",
										"'JPHN/JHI_DE102'",
										"'JPHN/JHI_DE103'",
										"'JPHN/JHI_DE104'",
										"'JPHN/JHI_DE105'",
										"'JPHN/JHI_DE106'",
										"'JPHN/JHI_DE107'",
										//IEC ACTIVITIES
										"'JPHN/JHI_DE108'",
										"'JPHN/JHI_DE109'",
										"'JPHN/JHI_DE110'",
										"'JPHN/JHI_DE111'",
										"'JPHN/JHI_DE112'",
										"'JPHN/JHI_DE113'",
										"'JPHN/JHI_DE114'",
										"'JPHN/JHI_DE115'",
										"'JPHN/JHI_DE116'",
										"'JPHN/JHI_DE117'",
										"'JPHN/JHI_DE118'",
										"'JPHN/JHI_DE119'",
										"'JPHN/JHI_DE120'",
										//MEDIA ACTIVITIES
										"'JPHN/JHI_DE121'",
										"'JPHN/JHI_DE122'",
										"'JPHN/JHI_DE123'",
										"'JPHN/JHI_DE124'",
										"'JPHN/JHI_DE125'",
										
										//CLINICS
										"'JPHN/JHI_DE126'",
										"'JPHN/JHI_DE127'",
										"'JPHN/JHI_DE128'",
										"'JPHN/JHI_DE129'",
										
										//VECTOR STUDY REPORT
										"'JPHN/JHI_DE130'",
										"'JPHN/JHI_DE131'",
										"'JPHN/JHI_DE132'",
										"'JPHN/JHI_DE133'",
										"'JPHN/JHI_DE134'",
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE'",
										
										//TOTAL CONTAINERS
										"'JPHN/JHI_DE135'",
										"'JPHN/JHI_DE136'",
										"'JPHN/JHI_DE137'",
										"'JPHN/JHI_DE138'",
										"'JPHN/JHI_DE139'",
										"'JPHN/JHI_DE140'",
										"'JPHN/JHI_DE141'",
										
										//VITAL STATISTICS
										"'JPHN/JHI_DE142'",
										"'JPHN/JHI_DE143'",
										"'JPHN/JHI_DE144'",
										"'JPHN/JHI_DE145'",
										
										//STOCK POSITION
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE146'",
										"'JPHN/JHI_DE147'",
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE148'",
										"'JPHN/JHI_DE149'",
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE150'",
										"'JPHN/JHI_DE151'",
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE153'",
										"'JPHN/JHI_DE152'",
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE154'",
										"'JPHN/JHI_DE155'",
										"'JPHN/JHI_DE'",
										"'JPHN/JHI_DE156'",
										"'JPHN/JHI_DE157'"
									
									};
					
 	  int monthlyValues[] = new int[monthlyDataElements.length+5]; 		
 	  int cumulativeValues[] =  new int[monthlyDataElements.length+5];
 	  
 	  int CHCID=0;
 	  int TalukID=0;
 	  int DistrictID=0;
 	  int StateID=0;
	  
	  String PHCName="";
 	  String CHCName="";
 	  String TalukName = "";
 	  String  DistrictName = "";
 	  String StateName = "";

  	  int j=1;  	 
  	  int i=1;  	 
  	  int k=0;  	 
  	  int endcount = 0;
  	  int p = 0;  	 
  	  int q = 0;
 	
  	  String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };   	
	  String query = "";	
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
        	
        	//rs1 = st1.executeQuery("select name from organisationunit  where id = "+selectedOrgUnitID);
        	rs1 = st1.executeQuery("select name from organisationunit  where organisationunitid = "+selectedOrgUnitID);        
        	if(rs1.next())        {  selectedOrgUnitName = rs1.getString(1);      }
                
        //	rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
	   	//	if(rs2.next()) 	  {  selectedDataPeriodStartDate =  rs2.getDate(1).toString();	} 		  	 
	   	
	   		//rs2=st2.executeQuery("select organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
	   		rs2=st2.executeQuery("select organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");        
			if(rs2.next())  { PHCName = rs2.getString(1);  } 
			else  {  PHCName = "";  } 
	   		   	
   		   selectedDataPeriodStartDate = startingDate;

      } //try block end
      catch(Exception e)  { out.println(e.getMessage());  }
      
     String partsOfDataPeriodStartDate[] = selectedDataPeriodStartDate.split("-");
     int lastYear = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;
     int tempForMonth1 = Integer.parseInt(partsOfDataPeriodStartDate[1]);
     int tempForYear = 0;
     if(tempForMonth1 < 4){   tempForYear = lastYear;  }
     else  {   tempForYear = lastYear + 1;   	}
     String curYearStart=tempForYear+"-04-01";
      	 	
    try
     {     
     	for(i=0;i<monthlyDataElements.length;i++)	  
			{	  
				//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in ( select id from organisationunit where id = "+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[i]+")";
				query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in ( select organisationunitid from organisationunit where organisationunitid = "+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[i]+")";
				rs3 = st3.executeQuery(query);
       			if(rs3.next()) 		{  monthlyValues[i] =  rs3.getInt(1);	} 
       			else 	{  monthlyValues[i] =  0;	} 
       			
 				// for Cumulative Performance till Current Month
           		//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+")  AND datavalue.source in ( select id from organisationunit where id ="+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[i]+")";
           		query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodtypeid = "+periodTypeID+")  AND datavalue.sourceid in ( select organisationunitid from organisationunit where organisationunitid ="+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[i]+")";			    
			    rs4=st4.executeQuery(query);
     			if(rs4.next())  {  cumulativeValues[i]= rs4.getInt(1);  } 
			    else  {  cumulativeValues[i] = 0;  } 
       				  							  
	  		}
  	} // try block end
    catch(Exception e)  { out.println(e.getMessage());  }
    finally
       {
			try
			{
  				if(rs1!=null)  rs1.close(); 		if(st1!=null)  st1.close();
  				if(rs2!=null)  rs2.close(); 		if(st2!=null)  st2.close();  				
  				if(rs3!=null)  rs3.close(); 		if(st3!=null)  st3.close();						
  				if(rs4!=null)  rs4.close(); 		if(st4!=null)  st4.close();						
				
				if(con!=null)  con.close(); 
			}
			catch(Exception e)   {  out.println(e.getMessage());   }
       } // finally block end	
       
  %>



<html>
<head>
<title>jhi/jphn</title>
</head>
<body>
<CENTER><FONT face="Arial" size="3"><b>MONTHLY WORK REPORT OF JHI/JPHN TO BE GIVEN TO HI</b></CENTER></font><br>
<table border="0" width="100%" style="border-collapse: collapse" id="table1">
	<tr>
		<td><FONT face="Arial" size="2">NAME :</FONT></td>
		<td><FONT face="Arial" size="2">REPORITING MONTH : <%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%></FONT></td>
	</tr>
	<tr>
		<td><FONT face="Arial" size="2">DESIGNATION :</FONT></td>
		<td><FONT face="Arial" size="2">WARDS :</FONT></td>
	</tr>
	<tr>
		<td><FONT face="Arial" size="2">SECTION/SUBCENTRE : <%=selectedOrgUnitName%></FONT></td>
		<td><FONT face="Arial" size="2">POPULATION :</FONT></td>
	</tr>
	<tr>
		<td><FONT face="Arial" size="2">PHC: <%=PHCName%></FONT></td>
		<td><FONT face="Arial" size="2">HOUSES&nbsp;&nbsp;&nbsp;&nbsp; ALLOTTED :<%=monthlyValues[0]%></FONT></td>
	</tr>
	<tr>
		<td><FONT face="Arial" size="2">&nbsp;</FONT></td>
		<td><font face="Arial" size="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; VISITED :<%=monthlyValues[1]%></font></td>
	</tr>
</table>
<u><FONT face="Arial" size="2"><b>Eligible couples</b></font></u>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table2">
	<tr>
		<td align="center" width="30%"><FONT face="Arial" size="2">Total EC</FONT></td>
		<td align="center" width="30%"><FONT face="Arial" size="2">EC contacted</FONT></td>
		<td align="center" width="40%"><FONT face="Arial" size="2">New EC Registered</FONT></td>
	</tr>
	<tr>
		<td align="center" width="30%"><FONT face="Arial" size="2"><%=monthlyValues[2]%>&nbsp;</font></td>
		<td align="center" width="30%"><FONT face="Arial" size="2"><%=monthlyValues[3]%>&nbsp;</font></td>
		<td align="center" width="40%"><FONT face="Arial" size="2"><%=monthlyValues[4]%>&nbsp;</font></td>
	</tr>
</table>
<u><b><FONT face="Arial" size="2">NVBDCP</FONT></b></u>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table3">
	<tr>
		<td  align="center" width="30%"colspan="4"><FONT face="Arial" size="2">Active Blood Smear Taken</FONT></td>
		<td  align="center" width="14%"colspan="2"><FONT face="Arial" size="2">Contact</FONT></td>
		<td  align="center" width="14%"colspan="2"><FONT face="Arial" size="2">Mass</FONT></td>
		<td  align="center" width="14%"colspan="2"><FONT face="Arial" size="2">
		Malaria positive cases</FONT></td>
		<td  align="center" width="14%"colspan="2"><FONT face="Arial" size="2">Tab 4AQ issued</FONT></td>
	</tr>
	<tr>
		<td align="center" width="7%"><FONT face="Arial" size="2">Target</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2">Month</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2">year</FONT></td>
		<td align="center" width="9%"><FONT face="Arial" size="2">percentage(%)</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2">Monthly</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2">Yearly</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2">Monthly</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2">Yearly</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2">Monthly</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2">Yearly</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2">Monthly</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2">Yearly</FONT></td>
	</tr>
	<tr>
		<td align="center" width="7%"><FONT face="Arial" size="2"><%=monthlyValues[5]%></FONT>&nbsp;</td>
		<td align="center" width="7%"><FONT face="Arial" size="2"><%=monthlyValues[6]%>&nbsp;</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2"><%=cumulativeValues[6]%>&nbsp;</FONT></td>
		<td align="center" width="9%"><FONT face="Arial" size="2">&nbsp;</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2"><%=monthlyValues[7]%>&nbsp;</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2"><%=cumulativeValues[7]%>&nbsp;</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2"><%=monthlyValues[8]%>&nbsp;</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2"><%=cumulativeValues[8]%>&nbsp;</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2"><%=monthlyValues[9]%>&nbsp;</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2"><%=cumulativeValues[9]%>&nbsp;</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2"><%=monthlyValues[10]%>&nbsp;</FONT></td>
		<td align="center" width="7%"><FONT face="Arial" size="2"><%=cumulativeValues[10]%>&nbsp;</FONT></td>
	</tr>
</table>
<u><b><FONT face="Arial" size="2">Communicable Diseases</FONT></b></u>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table4">
	<tr>
		<td align="center" width="5%"><FONT face="Arial" size="2">No</FONT></td>
		<td align="center" width="55%"><FONT face="Arial" size="2">Name</FONT></td>
		<td align="center" width="20%"><FONT face="Arial" size="2">Month</FONT></td>
		<td align="center" width="20%"><FONT face="Arial" size="2">Year</FONT></td>
	</tr>
	<tr>
		<td align="center" width="5%"><FONT face="Arial" size="2">1</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Measles</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[11]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[11]%>&nbsp;</FONT></td>
	</tr>
	<tr>2
		<td	align="center" width="5%"><FONT face="Arial" size="2">2</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Mumps</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[12]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[12]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">3</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Viral Hepatitis - A</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[13]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[13]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">4</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Viral Hepatitis - B</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[14]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[14]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">5</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Typhoid</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[15]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[15]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">6</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Pneumonia</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[16]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[16]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">7</FONT></td>
		<td	width="55%"><FONT face="Arial" size="2">ARI</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[17]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[17]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">8</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Poliomyelitis</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[18]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[18]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">9</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Dengue Fever</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[19]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[19]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">10</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">J.E</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[20]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[20]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">11</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Leptospirosis</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[21]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[21]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">12</FONT></td>
		<td	width="55%"><FONT face="Arial" size="2">Filariasis</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[22]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[22]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">13</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Wiels</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[23]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[23]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">14</FONT></td>
		<td	width="55%"><FONT face="Arial" size="2">Diphtheria</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[24]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[24]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">15</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Tetanus other than Neonatal</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[25]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[25]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">16</FONT></td>
		<td	width="55%"><FONT face="Arial" size="2">Neonatal Tetanus</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[26]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[26]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">17</FONT></td>
		<td	width="55%"><FONT face="Arial" size="2">Whooping Cough</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[27]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[27]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">18</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Chicken pox</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[28]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[28]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">19</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Meningococal meningitis</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[29]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[29]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">20</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Rabies (Hydrophobiea)</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[30]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[30]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">21</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2"><FONT face="Arial" size="2">Syphilis</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[31]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[31]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">22</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Genococal infection</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[32]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[32]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">23</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Guinea Worm</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[33]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[33]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">24</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Anthrax</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[34]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[34]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">25</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Diarreoha</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[35]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[35]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">26</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Dysentry</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[36]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[36]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">27</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Cholera</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[37]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[37]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td align="center" width="5%"><FONT face="Arial" size="2">28</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Other Diseases</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[38]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[38]%>&nbsp;</FONT></td>
	</tr>
</table>
<FONT face="Arial" size="2"><b>RNTCP(TB)</b></FONT></p>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table5">
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">NO</FONT></td>
		<td	align="center" width="55%"><FONT face="Arial" size="2">Details</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2">Monthly</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2">Yearly</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">1</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">New Cases Registered</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[39]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[39]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">2</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Total cases followed up upto last month</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[40]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[40]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">3</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Cases completed treatment</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[41]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[41]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">4</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Cases cured</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[42]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[42]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">5</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Remain under treatment</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[43]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[43]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">6</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Default Cases</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[44]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[44]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">7</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Death</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[45]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[45]%>&nbsp;</FONT></td>
	</tr>
</table>
<p align="left">
<FONT face="Arial" size="2"><b>Diarrhoeal Diseases&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </b></FONT>
<b><font face="Arial" size="2">Acute Watery Diarrhoea</font> </b>
</p>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table6">
	<tr>
		<td align="center" width="24%" colspan="2"><FONT face="Arial" size="2">&lt; 1 years</FONT></td>
		<td align="center" width="24%" colspan="2"><FONT face="Arial" size="2">1 - 4 years</FONT></td>
		<td align="center" width="24%" colspan="2"><FONT face="Arial" size="2">5 - years above</FONT></td>
		<td align="center" width="28%" colspan="2"><FONT face="Arial" size="2">Total</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="6%"><FONT face="Arial" size="2">Attack</FONT></td>
		<td	align="center" width="6%">Death</td>
		<td	align="center" width="6%"><font face="Arial" size="2">Attack</font></td>
		<td	align="center" width="6%">Death</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2">Attack</FONT></td>
		<td	align="center" width="6%">Death</td>
		<td	align="center" width="6%"><font face="Arial" size="2">Attack</font></td>
		<td	align="center" width="6%">Death</td>
	</tr>
	<tr>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[46]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[47]%>&nbsp;</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[48]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[49]%>&nbsp;</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[50]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[51]%>&nbsp;</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[46] + monthlyValues[48] + monthlyValues[50]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[47] + monthlyValues[49] + monthlyValues[51]%>&nbsp;</td>
	</tr>
</table>
<p align="center"><b>
<FONT face="Arial" size="2">Cholera</font>
</b></p>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table7">
	<tr>
		<td align="center" width="24%" colspan="2"><FONT face="Arial" size="2">&lt; 1 years</FONT></td>
		<td align="center" width="24%" colspan="2"><FONT face="Arial" size="2">1 - 4 years</FONT></td>
		<td align="center" width="24%" colspan="2"><FONT face="Arial" size="2">5 - years above</FONT></td>
		<td align="center" width="28%" colspan="2"><FONT face="Arial" size="2">Total</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="6%"><FONT face="Arial" size="2">Attack</FONT></td>
		<td	align="center" width="6%">Death</td>
		<td	align="center" width="6%"><font face="Arial" size="2">Attack</font></td>
		<td	align="center" width="6%">Death</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2">Attack</FONT></td>
		<td	align="center" width="6%">Death</td>
		<td	align="center" width="6%"><font face="Arial" size="2">Attack</font></td>
		<td	align="center" width="6%">Death</td>
	</tr>
	<tr>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[52]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[53]%>&nbsp;</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[54]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[55]%>&nbsp;</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[56]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[57]%>&nbsp;</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[52] + monthlyValues[54] + monthlyValues[56]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[53] + monthlyValues[55] + monthlyValues[57]%>&nbsp;</td>
	</tr>
</table>
<p align="center"><b>
<FONT face="Arial" size="2">Dysentry</font>
</b></p>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table8">
	<tr>
		<td align="center" width="24%" colspan="2"><FONT face="Arial" size="2">&lt; 1 years</FONT></td>
		<td align="center" width="24%" colspan="2"><FONT face="Arial" size="2">1 - 4 years</FONT></td>
		<td align="center" width="24%" colspan="2"><FONT face="Arial" size="2">5 - years above</FONT></td>
		<td align="center" width="28%" colspan="2"><FONT face="Arial" size="2">Total</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="6%"><FONT face="Arial" size="2">Attack</FONT></td>
		<td	align="center" width="6%">Death</td>
		<td	align="center" width="6%"><font face="Arial" size="2">Attack</font></td>
		<td	align="center" width="6%">Death</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2">Attack</FONT></td>
		<td	align="center" width="6%">Death</td>
		<td	align="center" width="6%"><font face="Arial" size="2">Attack</font></td>
		<td	align="center" width="6%">Death</td>
	</tr>
	<tr>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[58]%>&nbsp;</FONT><p>&nbsp;</td>
		<td	align="center" width="6%"><%=monthlyValues[59]%>&nbsp;</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[60]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[61]%>&nbsp;</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[62]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[63]%>&nbsp;</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[58] + monthlyValues[60] + monthlyValues[62]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[59] + monthlyValues[61] + monthlyValues[63]%>&nbsp;</td>
	</tr>
</table>
<p align="center"><b>
<FONT face="Arial" size="2">Persistent Diarrhoea</font>
</b></p>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table9">
	<tr>
		<td align="center" width="24%" colspan="2"><FONT face="Arial" size="2">&lt; 1 years</FONT></td>
		<td align="center" width="24%" colspan="2"><FONT face="Arial" size="2">1 - 4 years</FONT></td>
		<td align="center" width="24%" colspan="2"><FONT face="Arial" size="2">5 - years above</FONT></td>
		<td align="center" width="28%" colspan="2"><FONT face="Arial" size="2">Total</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="6%"><FONT face="Arial" size="2">Attack</FONT></td>
		<td	align="center" width="6%">Death</td>
		<td	align="center" width="6%"><font face="Arial" size="2">Attack</font></td>
		<td	align="center" width="6%">Death</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2">Attack</FONT></td>
		<td	align="center" width="6%">Death</td>
		<td	align="center" width="6%"><font face="Arial" size="2">Attack</font></td>
		<td	align="center" width="6%">Death</td>
	</tr>
	<tr>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[64]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[65]%>&nbsp;</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[66]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[67]%>&nbsp;</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[68]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><%=monthlyValues[69]%>&nbsp;</td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[64] + monthlyValues[66] + monthlyValues[68]%>&nbsp;</FONT></td>
		<td	align="center" width="6%"><FONT face="Arial" size="2"><%=monthlyValues[65] + monthlyValues[67] + monthlyValues[69]%>&nbsp;</FONT></td>
	</tr>
</table>

<FONT face="Arial" size="2"><b>Non - Communicable Diseases</b></font>

<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table10">
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">No</FONT></td>
		<td	align="center" width="55%"><FONT face="Arial" size="2">Disease Name</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2">Month</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2">Year</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">1</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Hyper Tension</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[70]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[70]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">2</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Diabetic Mellitus</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[71]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[71]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">3</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Cancer</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[72]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[72]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">4</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Heart Patient</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[73]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[73]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">5</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Mental Disorder</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[74]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[74]%>&nbsp;</FONT></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><FONT face="Arial" size="2">6</FONT></td>
		<td	 width="55%"><FONT face="Arial" size="2">Other Diseases</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=monthlyValues[75]%>&nbsp;</FONT></td>
		<td	align="center" width="20%"><FONT face="Arial" size="2"><%=cumulativeValues[75]%>&nbsp;</FONT></td>
	</tr>
</table>
<font face="Arial" size="2"><b>Sterilization</b></font><br><br>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table11">
	<tr>
		<td align="center"rowspan="2"><font face="Arial" size="2">Age of Acceptors</font></td>
		<td align="center"rowspan="2"><font face="Arial" size="2">Target</font></td>
		<td align="center"colspan="2"><font face="Arial" size="2">Vas</font></td>
		<td align="center"colspan="2"><font face="Arial" size="2">Mini Lapro</font></td>
		<td align="center"colspan="2"><font face="Arial" size="2">Lapro</font></td>
		<td align="center"colspan="2"><font face="Arial" size="2">PPS</font></td>
		<td align="center"colspan="2"><font face="Arial" size="2">Total</font></td>
		<td align="center"rowspan="2"><font face="Arial" size="2">%</font></td>
		<td align="center"colspan="4"><font face="Arial" size="2">IUD</font></td>
	</tr>
	<tr>	
		<td	align="center"><font face="Arial" size="2">Month</font></td>
		<td	align="center"><font face="Arial" size="2">Year</font></td>
		<td	align="center"><font face="Arial" size="2">Month</font></td>
		<td	align="center"><font face="Arial" size="2">Year</font></td>
		<td	align="center"><font face="Arial" size="2">Month</font></td>
		<td	align="center"><font face="Arial" size="2">Year</font></td>
		<td	align="center"><font face="Arial" size="2">Month</font></td>
		<td	align="center"><font face="Arial" size="2">Year</font></td>
		<td	align="center"><font face="Arial" size="2">Month</font></td>
		<td	align="center"><font face="Arial" size="2">Year</font></td>
		<td	align="center"><font face="Arial" size="2">Target</font></td>
		<td	align="center"><font face="Arial" size="2">Month</font></td>
		<td	align="center"><font face="Arial" size="2">Year</font></td>
		<td	align="center"><font face="Arial" size="2">%</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">Less than 30</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[76]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[78]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[78]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[80]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[80]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[82]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[82]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[84]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[84]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[78] + monthlyValues[80] + monthlyValues[82] + monthlyValues[84]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[78] + cumulativeValues[80] + cumulativeValues[82] + cumulativeValues[84]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[86]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[88]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[88]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">More than 30</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[77]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[79]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[79]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;<%=monthlyValues[81]%></font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[81]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[83]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[83]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[85]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[85]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[79] + monthlyValues[81] + monthlyValues[83] + monthlyValues[85]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[79] + cumulativeValues[81] + cumulativeValues[83] + cumulativeValues[85]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[87]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[89]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[89]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">Total</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[76] + monthlyValues[77]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[78] + monthlyValues[79]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[78] + cumulativeValues[79]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[80] + monthlyValues[81]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[80] + cumulativeValues[81]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[82] + monthlyValues[83]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[82] + cumulativeValues[83]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[84] + monthlyValues[85]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[84] + cumulativeValues[85]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[78] + monthlyValues[80] + monthlyValues[82] + monthlyValues[84] + monthlyValues[79] + monthlyValues[81] + monthlyValues[83] + monthlyValues[85]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[78] + cumulativeValues[80] + cumulativeValues[82] + cumulativeValues[84] + cumulativeValues[79] + cumulativeValues[81] + cumulativeValues[83] + cumulativeValues[85]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[86] + monthlyValues[87]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[88] + monthlyValues[89]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[88] + cumulativeValues[89]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
</table>
<b><font face="Arial" size="2">OP Users</b></font>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table12">
	<tr>
		<td align="center"rowspan="2"><font face="Arial" size="2">&nbsp;</font></td>
		<td align="center"rowspan="2"><font face="Arial" size="2">Target</font></td>
		<td align="center"colspan="2"><font face="Arial" size="2">During the Month</font></td>
		<td align="center">Total</td>
		<td align="center"><font face="Arial" size="2">Year</font></td>
		<td align="center"rowspan="2"><font face="Arial" size="2">%</font></td>
	</tr>
	<tr>	
		<td	align="center"><font face="Arial" size="2">Casual users</font></td>
		<td	align="center"><font face="Arial" size="2">Regular users</font></td>
		<td align="center"><font face="Arial" size="2">Users</font></td>
		<td align="center"><font face="Arial" size="2">Users</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">Less than 30 years</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[90]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[92]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[94]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[92] + monthlyValues[94]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[92] +cumulativeValues[94]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">More than 30 years</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[91]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[93]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[95]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[93] + monthlyValues[95]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[93] + cumulativeValues[95]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">Total</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[90] + monthlyValues[91]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[92] + monthlyValues[93]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[94] + monthlyValues[95]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[92] + monthlyValues[94] + monthlyValues[93] + monthlyValues[95]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[92] + cumulativeValues[94] + cumulativeValues[93] + cumulativeValues[95]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
</table>
<font face="Arial" size="2"><b>CC users</font></b>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table13">
	<tr>
		<td align="center"rowspan="2"><font face="Arial" size="2">&nbsp;</font></td>
		<td align="center"rowspan="2"><font face="Arial" size="2">Target</font></td>
		<td align="center"colspan="2"><font face="Arial" size="2">During the Month</font></td>
		<td align="center"><font face="Arial" size="2">Total</font></td>
		<td align="center"><font face="Arial" size="2">Year</font></td>
		<td align="center"rowspan="2"><font face="Arial" size="2">%</font></td>
	</tr>
	<tr>	
		<td	align="center"><font face="Arial" size="2">Casual users</font></td>
		<td	align="center"><font face="Arial" size="2">Regular users</font></td>
		<td align="center"><font face="Arial" size="2">Users</font></td>
		<td align="center"><font face="Arial" size="2">users</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">Less than 30 years</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[96]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[98]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[100]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[98] + monthlyValues[100]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[98] + cumulativeValues[100]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">More than 30 years</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[97]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[99]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[101]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[99] + monthlyValues[101]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[99] + cumulativeValues[101]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">Total</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[96] + monthlyValues[97]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[98] + monthlyValues[99]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[100] + monthlyValues[101]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[98] + monthlyValues[100] + monthlyValues[99] + monthlyValues[101]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[98] + cumulativeValues[100] + cumulativeValues[99] + cumulativeValues[101]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
</table>
<font face="Arial" size="2"><b>Reports of Activities for Prevention and control of Communicable Diseases</b></font>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table14">
	<tr>
		<td	align="center"><font face="Arial" size="2">NO</font></td>
		<td	align="center"><font face="Arial" size="2">Activities</font></td>
		<td	align="center"><font face="Arial" size="2">Monthly</font></td>
		<td	align="center"><font face="Arial" size="2">Yearly</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">1</font></td>
		<td><font face="Arial" size="2">ORS Packets Distributed</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[102]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[102]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">2</font></td>
		<td><font face="Arial" size="2">Wells Chlorinated</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[103]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[103]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">3</font></td>
		<td><font face="Arial" size="2">Water Samples Collected</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[104]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[104]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">4</font></td>
		<td><font face="Arial" size="2">Water Samples found Satisfactory</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[105]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[105]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">5</font></td>
		<td><font face="Arial" size="2">Water Samples found Unsatisfactory</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[106]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[106]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">6</font></td>
		<td><font face="Arial" size="2">Number Hotels / Tea shops inspected</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[107]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[107]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">7</font></td>
		<td><font face="Arial" size="2">Market inspected</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[108]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[108]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">8</font></td>
		<td><font face="Arial" size="2">Number of Containers breeding&nbsp; centers of mosquitoes eliminated</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[109]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[109]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">9</font></td>
		<td><font face="Arial" size="2">No of other breeding places eliminated</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[110]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[110]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">10</font></td>
		<td><font face="Arial" size="2">Number of places where fishes introduced</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[111]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[111]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">11</font></td>
		<td><font face="Arial" size="2">Number of places where larvicides applied</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[112]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[112]%>&nbsp;</font></td>
	</tr>
</table>
<b><font face="Arial" size="2">I</font></b><font face="Arial" size="2"><b>EC Activities</b></font>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table15">
	<tr>
		<td	align="center"><font face="Arial" size="2">No</font></td>
		<td	align="center"><font face="Arial" size="2">Activities</font></td>
		<td	align="center"><font face="Arial" size="2">Monthly</font></td>
		<td	align="center"><font face="Arial" size="2">Yearly</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
		<td>
		<p align="center"><font face="Arial" size="2">Health Education</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">1</font></td>
		<td><font face="Arial" size="2">Health talk given</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[113]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[113]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">2</font></td>
		<td><font face="Arial" size="2">Group discussion done</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[114]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[114]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">3</font></td>
		<td><font face="Arial" size="2">Mother's meeting conducted</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[115]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[115]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">4</font></td>
		<td><font face="Arial" size="2">No of participants in the group discussion</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[116]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[116]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">5</font></td>
		<td><font face="Arial" size="2">Public meeting conducted</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[117]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[117]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">6</font></td>
		<td><font face="Arial" size="2">Seminars</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[118]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[118]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">7</font></td>
		<td><font face="Arial" size="2">Orientation training</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[119]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[119]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">8</font></td>
		<td><font face="Arial" size="2">Mike Publicity</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[120]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[120]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">9</font></td>
		<td><font face="Arial" size="2">Bit notice distributed</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[121]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[121]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">10</font></td>
		<td><font face="Arial" size="2">Number of health awareness campaigns</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[122]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[122]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">11</font></td>
		<td><font face="Arial" size="2">No of MSS meeting held</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[123]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[123]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">12</font></td>
		<td><font face="Arial" size="2">Public complaints Obtained</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[124]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[124]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">13</font></td>
		<td><font face="Arial" size="2">Public complaints Solved</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[125]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[125]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
		<td>
		<p align="center"><font face="Arial" size="2">Media Activities</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">14</font></td>
		<td><font face="Arial" size="2">16 mm film shows</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[126]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[126]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">15</font></td>
		<td><font face="Arial" size="2">Mini exhibition</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[127]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[127]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">16</font></td>
		<td><font face="Arial" size="2">Press release</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[128]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[128]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">17</font></td>
		<td><font face="Arial" size="2">Video show</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[129]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[129]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">18</font></td>
		<td><font face="Arial" size="2">Slide projection done</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[130]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[130]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
		<td>
		<p align="center"><font face="Arial" size="2">Clinics</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">19</font></td>
		<td><font face="Arial" size="2">Immunization Camp</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[131]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[131]%>&nbsp;</font></td>
	</tr>
	<tr>	
		<td	align="center"><font face="Arial" size="2">20</font></td>
		<td><font face="Arial" size="2">Antenatal Clinics</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[132]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[132]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">21</font></td>
		<td><font face="Arial" size="2">No of medical camps conducted</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[133]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[133]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">22</font></td>
		<td><font face="Arial" size="2">No. of patients in the medical camp</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[134]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[134]%>&nbsp;</font></td>
	</tr>
	</table>
<font face="Arial" size="2"><b>Vector Study Report</b></font>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table16">
	<tr>
		<td	align="center"><font face="Arial" size="2">Place of Visited<br> (1)</font></td>
		<td	align="center"><font face="Arial" size="2">No of Houses visited<br> (2)</font></td>
		<td	align="center"><font face="Arial" size="2">No of + ve Houses<br> (3)</font></td>
		<td	align="center"><font face="Arial" size="2">No of containers checked <br>(4)</font></td>
		<td	align="center"><font face="Arial" size="2">No of +ve containers<br> (5)</font></td>
		<td	align="center"><font face="Arial" size="2">Home index <br>(6)</font></td>
		<td	align="center"><font face="Arial" size="2">Briteau index <br> (7)</font></td>
		<td	align="center"><font face="Arial" size="2">Container Index<br>(8)</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[135]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[136]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[137]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[138]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[139]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[140]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[141]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[142]%>&nbsp;</font></td>
	</tr>
</table>
<font face="Arial" size="2"><b>Total Containers</b></font>
<table border="1" width="50%" style="border-collapse: collapse" bordercolor="#000000" id="table17">
	<tr>
		<td><font face="Arial" size="2">Container Name</font></td>
		<td	align="center"><font face="Arial" size="2">No</font></td>
	</tr>
	<tr>
		<td><font face="Arial" size="2">1.Plastic Container</font>
		<font face="Arial" size="2">&nbsp;:</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[143]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td><font face="Arial" size="2">2.Metal </font>Container
		<font face="Arial" size="2">&nbsp;:</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[144]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td><font face="Arial" size="2">3.Broken clay </font>Container
		<font face="Arial" size="2">&nbsp;:</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[145]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td><font face="Arial" size="2">4.Broken Class </font>Container
		<font face="Arial" size="2">:</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[146]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td><font face="Arial" size="2">5.Cocunut Shell </font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[147]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td><font face="Arial" size="2">6.Tank </font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[148]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td><font face="Arial" size="2">7.Tyre </font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[149]%>&nbsp;</font></td>
	</tr>
</table>
<font face="Arial" size="2"><b>Vital Statistics</b></font>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table18">
	<tr>
		<td	align="center" rowspan="2"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" colspan="3"><font face="Arial" size="2">During Month</font></td>
		<td	align="center" colspan="3"><font face="Arial" size="2">During the Year</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">Male</font></td>
		<td	align="center"><font face="Arial" size="2">Female</font></td>
		<td	align="center"><font face="Arial" size="2">Total</font></td>
		<td	align="center"><font face="Arial" size="2">Male</font></td>
		<td	align="center"><font face="Arial" size="2">Female</font></td>
		<td	align="center"><font face="Arial" size="2">Total</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">Birth</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[150]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[151]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[150] + monthlyValues[151]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[150]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[151]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[150] + cumulativeValues[151]%>&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center"><font face="Arial" size="2">Death</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[152]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[153]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=monthlyValues[152] + monthlyValues[153]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[152]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[153]%>&nbsp;</font></td>
		<td	align="center"><font face="Arial" size="2"><%=cumulativeValues[152] + cumulativeValues[153]%>&nbsp;</font></td>
	</tr>
</table>
<font face="Arial" size="2"><b>Stock Position</b></font>
<table border="1" width="100%" style="border-collapse: collapse" bordercolor="#000000" id="table19">
	<tr>
		<td	align="center" width="5%"><font face="Arial" size="2">Sl.No</font></td>
		<td	align="center" width="20%"><font face="Arial" size="2">Item</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">Opening Balance</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">Receipt</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">Total</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">Issue</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">Balance</font></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><font face="Arial" size="2">1</font></td>
		<td	 width="20%"><font face="Arial" size="2">Chlora Quine</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><font face="Arial" size="2">2</font></td>
		<td	 width="20%"><font face="Arial" size="2">O.P</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><font face="Arial" size="2">3</font></td>
		<td	 width="20%"><font face="Arial" size="2">C.C</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><font face="Arial" size="2">4</font></td>
		<td	 width="20%"><font face="Arial" size="2">ORS</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><font face="Arial" size="2">5</font></td>
		<td	 width="20%"><font face="Arial" size="2">Bleaching powder</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
	<tr>
		<td	align="center" width="5%"><font face="Arial" size="2">6</font></td>
		<td	width="20%"><font face="Arial" size="2">Phenyl </font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
		<td	align="center" width="15%"><font face="Arial" size="2">&nbsp;</font></td>
	</tr>
</table>
</body>

</html>