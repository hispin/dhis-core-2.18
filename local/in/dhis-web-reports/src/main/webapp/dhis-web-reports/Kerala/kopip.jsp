
<%@ page import="java.sql.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>

<%
    Connection con=null;
            
    // For finding organisationunit name of selected Orgunit based on Orgunit id
    Statement st1=null;
    ResultSet rs1=null;
      
    // For finding start date of selected period based on period id
    Statement st2=null;
    ResultSet rs2=null;
    
   //monthly data values 
    Statement st3=null;
    ResultSet rs3=null;
    
   //yearly data values
 	Statement st4=null;
    ResultSet rs4=null;
   
        
  	String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/kl_dhis2";
          
	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = Integer.parseInt( selectedId );
	

 	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );

      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID =  	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	int i=0;
  	
  	String curYearStart="";


	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		
	
	String dataElementCodes[] = {
									"IPOP_DE1","IPOP_DE2","IPOP_DE3","IPOP_DE4","IPOP_DE5","IPOP_DE6",
	                              	"IPOP_DE7","IPOP_DE8","IPOP_DE9","IPOP_DE10","IPOP_DE11","IPOP_DE12",

									"IPOP_DE13","IPOP_DE14","IPOP_DE15","IPOP_DE16","IPOP_DE17","IPOP_DE18",
	                              	"IPOP_DE19","IPOP_DE20","IPOP_DE21","IPOP_DE22","IPOP_DE23","IPOP_DE24",

									"IPOP_DE25","IPOP_DE26","IPOP_DE27","IPOP_DE28","IPOP_DE29","IPOP_DE30",
	                              	"IPOP_DE31","IPOP_DE32","IPOP_DE33","IPOP_DE34","IPOP_DE35","IPOP_DE36",

									"IPOP_DE37","IPOP_DE38","IPOP_DE39","IPOP_DE40","IPOP_DE41","IPOP_DE42",
	                              	"IPOP_DE43","IPOP_DE44","IPOP_DE45","IPOP_DE46","IPOP_DE47","IPOP_DE48",


									"IPOP_DE49","IPOP_DE50","IPOP_DE51","IPOP_DE52","IPOP_DE53","IPOP_DE54",
	                              	"IPOP_DE55","IPOP_DE56","IPOP_DE57","IPOP_DE58","IPOP_DE59","IPOP_DE60",

									"IPOP_DE61","IPOP_DE62","IPOP_DE63","IPOP_DE64","IPOP_DE65","IPOP_DE66",
	                              	"IPOP_DE67","IPOP_DE68","IPOP_DE69","IPOP_DE70","IPOP_DE71","IPOP_DE72",

									"IPOP_DE73","IPOP_DE74","IPOP_DE75","IPOP_DE76","IPOP_DE77","IPOP_DE78",
	                              	"IPOP_DE79","IPOP_DE80","IPOP_DE81","IPOP_DE82","IPOP_DE83","IPOP_DE84",

									"IPOP_DE85","IPOP_DE86","IPOP_DE87","IPOP_DE88","IPOP_DE89","IPOP_DE90",
	                              	"IPOP_DE91","IPOP_DE92","IPOP_DE93","IPOP_DE94","IPOP_DE95","IPOP_DE96",


									"IPOP_DE97","IPOP_DE98","IPOP_DE99","IPOP_DE100","IPOP_DE101","IPOP_DE102",
	                              	"IPOP_DE103","IPOP_DE104","IPOP_DE105","IPOP_DE106","IPOP_DE107","IPOP_DE108",

									"IPOP_DE109","IPOP_DE110","IPOP_DE111","IPOP_DE112","IPOP_DE113","IPOP_DE114",
	                              	"IPOP_DE115","IPOP_DE116","IPOP_DE117","IPOP_DE118","IPOP_DE119","IPOP_DE120",

									"IPOP_DE121","IPOP_DE122","IPOP_DE123","IPOP_DE124","IPOP_DE125","IPOP_DE126",
	                              	"IPOP_DE127","IPOP_DE128","IPOP_DE129","IPOP_DE130","IPOP_DE131","IPOP_DE132",

									"IPOP_DE133","IPOP_DE134","IPOP_DE135","IPOP_DE136","IPOP_DE137","IPOP_DE138",
	                              	
                          };



	int monthlyValues[] = new int[dataElementCodes.length+5];
	int cumulativeValues[]  = new int[dataElementCodes.length+5];
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
         if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);     }
         else  {  selectedOrgUnitName = "";       }  
                
//       rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	     if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	}  

		 selectedDataPeriodStartDate = startingDate;
		
		
	   }
	   catch(Exception e)  { out.println(e.getMessage());  }
 
   		
	 	String partsOfDataPeriodStartDate[] = selectedDataPeriodStartDate.split("-");
     	int lastYear = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;
     	int tempForMonth1 = Integer.parseInt(partsOfDataPeriodStartDate[1]);
     	int tempForYear = 0;
     	if(tempForMonth1 < 4){   tempForYear = lastYear;  }
     	else  {   tempForYear = lastYear + 1;   	}
     	curYearStart=tempForYear+"-04-01";
	try
	 {
    	
    	for(i=0;i<dataElementCodes.length;i++)
 		{
 			//rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source ="+selectedOrgUnitID+" AND dataelement.code like '"+dataElementCodes[i]+"'");
 			rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid ="+selectedOrgUnitID+" AND dataelement.code like '"+dataElementCodes[i]+"'");
 			if(rs3.next())	{monthlyValues[i] = rs3.getInt(1);}
 			
// 			rs4 = st4.executeQuery("select sum(value) from datavalue where dataElement in (select id from dataelement where  code like '"+dataElementCodes[i]+"') and source in (select id from organisationunit where parent ="+selectedOrgUnitID+") and period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+")");
//			if(rs4.next()) {cumulativeValues[i] = rs4.getInt(1);}
 
// 			rs4 = st4.executeQuery("select sum(value) from datavalue where dataElement in (select id from dataelement where code like '"+dataElementCodes[i]+"') and source in (select id from organisationunit where parent ="+selectedOrgUnitID+") and period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+")");        			        		
 //			if(rs4.next())  		{  	cumulativeValues[i] =  rs4.getInt(1);		}  	  
 
 			
 		}


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



<html>

        <head>
        <title>IPOPunicable Diseases</title>
		</head>
		<body>
<center>
<font face="arial" size=3>Statement showing the OP , IP and death due to the 
Communicable diseases for the month of<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%></font></center><br>
<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%" id="AutoNumber1">
  <tr>
    <td width="100%" align="right"><font face="arial" size=3>PHC :<%=selectedOrgUnitName%></font></td>
  </tr>
</table><br><br>

<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%" id="AutoNumber2">
  <tr>
    <td width="40%" rowspan="3"	align="center"><font face="arial" size=2>Name of disease</font></td>
    <td width="30%" colspan="6"	align="center"><font face="arial" size=2>Monthly</font></td>
    <td width="30%" colspan="6"	align="center"><font face="arial" size=2>Yearly</font></td>
  </tr>
  <tr>
    <td width="10%" colspan="2" align="center"><font face="arial" size=2>OP</font></td>
    <td width="10%" colspan="2"	align="center"><font face="arial" size=2>IP</font></td>
    <td width="10%" colspan="2"	align="center"><font face="arial" size=2>Death</font></td>
    <td width="10%" colspan="2"	align="center"><font face="arial" size=2>OP</font></td>
    <td width="10%" colspan="2"	align="center"><font face="arial" size=2>IP</font></td>
    <td width="10%" colspan="2"	align="center"><font face="arial" size=2>Death</font></td>
  </tr>
  <tr>
    <td width="5%"	align="center"><font face="arial" size=2>M</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>F</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>M</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>F</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>M</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>F</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>M</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>F</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>M</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>F</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>M</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>F</font></td>
  </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Acute diarrhoea disease</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[0]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[1]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[2]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[3]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[4]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[5]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[0]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[1]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[2]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[3]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[4]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[5]%>&nbsp;</font></td>
  </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Acute watery disease</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[6]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[7]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[8]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[9]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[10]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[11]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[6]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[7]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[8]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[9]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[10]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[11]%>&nbsp;</font></td>
   </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Cholera</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[12]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[13]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[14]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[15]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[16]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[17]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[12]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[13]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[14]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[15]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[16]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[17]%>&nbsp;</font></td>
   </tr>
  <tr>
  	<td width="40%"><font face="arial" size=2>Dysentry</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[18]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[19]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[20]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[21]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[22]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[23]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[18]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[19]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[20]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[21]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[22]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[23]%>&nbsp;</font></td>
   </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Persistal Diarrhoca</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[24]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[25]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[26]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[27]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[28]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[29]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[24]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[25]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[26]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[27]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[28]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[29]%>&nbsp;</font></td>
   </tr>
  <tr>
   	<td width="40%"><font face="arial" size=2>Polio</font></td>
  	<td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[30]%></font>&nbsp;</td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[31]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[32]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[33]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[34]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[35]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[30]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[31]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[32]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[33]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[34]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[35]%>&nbsp;</font></td>
   </tr>
  <tr>
    <td width="40%"	><font face="arial" size=2>Diphteria</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[36]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[37]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[38]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[39]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[40]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[41]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[36]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[37]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[38]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[39]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[40]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[41]%>&nbsp;</font></td>
   </tr>
  <tr>
   	<td width="40%"><font face="arial" size=2>Tetanus</font></td>
   	<td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[42]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[43]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[44]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[45]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[46]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[47]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[42]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[43]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[44]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[45]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[46]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[47]%>&nbsp;</font></td>
   </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Woophing Cough</font></td>
   	<td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[48]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[49]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[50]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[51]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[52]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[53]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[48]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[49]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[50]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[51]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[52]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[53]%>&nbsp;</font></td>
     </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Measles</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[54]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[55]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[56]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[57]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[58]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[59]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[54]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[55]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[56]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[57]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[58]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[59]%>&nbsp;</font></td>
     </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Chicken pox</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[60]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[61]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[62]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[63]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[64]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[65]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[60]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[61]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[62]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[63]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[64]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[65]%>&nbsp;</font></td>
     </tr>
  <tr>
   	<td width="40%"><font face="arial" size=2>Respiratory Infection</font></td>
   	<td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[66]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[67]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[68]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[69]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[70]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[71]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[66]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[67]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[68]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[69]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[70]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[71]%>&nbsp;</font></td>
     </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Pnemonia</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[72]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[73]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[74]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[75]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[76]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[77]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[72]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[73]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[74]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[75]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[76]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[77]%>&nbsp;</font></td>
     </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Typhoid</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[78]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[79]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[80]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[81]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[82]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[83]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[78]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[79]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[80]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[81]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[82]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[83]%>&nbsp;</font></td>
     </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Dengue Fever</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[84]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[85]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[86]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[87]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[88]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[89]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[84]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[85]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[86]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[87]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[88]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[89]%>&nbsp;</font></td>
     </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Hepatitis A</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[90]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[91]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[92]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[93]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[94]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[95]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[90]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[91]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[92]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[93]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[94]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[95]%>&nbsp;</font></td>
     </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Hepatitis B</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[96]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[97]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[98]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[99]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[100]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[101]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[96]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[97]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[98]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[99]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[100]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[101]%>&nbsp;</font></td>
     </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>J.E</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[102]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[103]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[104]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[105]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[106]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[107]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[102]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[103]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[104]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[105]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[106]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[107]%>&nbsp;</font></td>
     </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Mumps</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[108]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[109]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[110]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[111]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[112]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[113]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[108]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[109]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[110]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[111]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[112]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[113]%>&nbsp;</font></td>
     </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Filaria</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[114]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[115]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[116]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[117]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[118]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[119]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[114]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[115]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[116]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[117]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[118]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[119]%>&nbsp;</font></td>
     </tr>
  <tr>
   	<td width="40%"><font face="arial" size=2>TB</font></td>
   	<td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[120]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[121]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[122]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[123]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[124]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[125]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[120]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[121]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[122]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[123]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[124]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[125]%>&nbsp;</font></td>
   </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>Malaria</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[126]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[127]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[128]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[129]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[130]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[131]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[126]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[127]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[128]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[129]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[130]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[131]%>&nbsp;</font></td>
     </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>All other Disease</font></td>
   	<td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[132]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[133]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[134]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[135]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[136]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=monthlyValues[137]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[132]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[133]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[134]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[135]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[136]%>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2><%=cumulativeValues[137]%>&nbsp;</font></td>
     </tr>
  <tr>
    <td width="40%"><font face="arial" size=2>TOTAL</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;</font></td>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;</font></td>
     </tr>
</table>

</body>

</html>