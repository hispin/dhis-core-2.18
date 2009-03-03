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
    String urlForConnection = "jdbc:mysql://localhost/gj_dhis2";
          

	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = Integer.parseInt( selectedId );
	
//	String selectedPeriodId = (String) stack.findValue( "periodSelect" );
//	int selectedDataPeriodID = 	Integer.parseInt( selectedPeriodId );

  	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );

      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	  	  	
  	String monthlyDataElements[] = {
  											// DataElement Codes for 1 to 7
  											"LEP_DE1",
  											"LEP_DE2",
  											"LEP_DE3",
  											"LEP_DE4",
  											"LEP_DE5",
  											"LEP_DE6",
  											"LEP_DE7",
  											"LEP_DE8",
  											"LEP_DE9",
  											"LEP_DE10",  											
  											"LEP_DE11",
  											
  											// Blister Packs MB(A)  - Complled PHC Quantity , District Quantity
  											"LEP_DE12",
  											"LEP_DE13",
  											"LEP_DE14",
  											"LEP_DE24",
  											"LEP_DE25",
  											"LEP_DE26",
  											
  											// Blister Packs MB(C)  - Complled PHC Quantity , District Quantity
  											"LEP_DE15",
  											"LEP_DE16",
  											"LEP_DE17",
  											"LEP_DE27",
  											"LEP_DE28",
  											"LEP_DE29",
  											
  											// Blister Packs PB(A)  - Complled PHC Quantity , District Quantity
  											"LEP_DE18",
  											"LEP_DE19",
											"LEP_DE20",
											"LEP_DE30",
											"LEP_DE31",
											"LEP_DE32",
											
											// Blister Packs PB(C)  - Complled PHC Quantity , District Quantity
											"LEP_DE21",
											"LEP_DE22",
											"LEP_DE23",
											"LEP_DE33",
  											"LEP_DE34",
  											"LEP_DE35",
  											  											
										};
					
 	  int monthlyValues[] = new int[monthlyDataElements.length]; 		
 	  int cumulativeValues[] =  new int[monthlyDataElements.length];
 	  
 	  int CHCID=0;
 	  int TalukID=0;
 	  int DistrictID=0;
 	  int StateID=0;
 	  
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
        	
        	//rs1 = st1.executeQuery("select shortname from organisationunit  where id = "+selectedOrgUnitID);
        	rs1 = st1.executeQuery("select shortname from organisationunit  where organisationunitid = "+selectedOrgUnitID);        
        	if(rs1.next())        {  selectedOrgUnitName = rs1.getString(1);      }
                
//        	rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	   		if(rs2.next()) 	  {  selectedDataPeriodStartDate =  rs2.getDate(1).toString();	} 		  
	   		   	
			selectedDataPeriodStartDate = startingDate;


			//rs8=st8.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
			rs8=st8.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");	
			if(rs8.next())  { StateID = rs8.getInt(1);StateName = rs8.getString(2);  } 
			else  {  StateID = 0; StateName = "";  } 

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
				//rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source ="+selectedOrgUnitID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");
				rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid ="+selectedOrgUnitID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");				  
        		if(rs3.next()) 		{  monthlyValues[i] =  rs3.getInt(1);	}  
		  			
				//rs4 = st4.executeQuery(" select sum(value) from datavalue where dataElement in (select id from dataelement where code like '"+monthlyDataElements[i]+"') and source = "+selectedOrgUnitID+" and period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+")");
				rs4 = st4.executeQuery(" select sum(value) from datavalue where dataelementid in (select dataelementid from dataelement where code like '"+monthlyDataElements[i]+"') and sourceid = "+selectedOrgUnitID+" and periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodtypeid = "+periodTypeID+")"); 
    			if(rs4.next())  		{  	cumulativeValues[i] =  rs4.getInt(1);		}  	  
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
				if(rs5!=null)  rs5.close(); 		if(st5!=null)  st5.close();
				if(rs6!=null)  rs6.close(); 		if(st6!=null)  st6.close();
				if(rs7!=null)  rs7.close(); 		if(st7!=null)  st7.close();
				if(rs8!=null)  rs8.close(); 		if(st8!=null)  st8.close();
				
				if(con!=null)  con.close(); 
			}
			catch(Exception e)   {  out.println(e.getMessage());   }
       } // finally block end	
       
  %>

<html>
<head>
	<title>NLEP Monthly Reporting Form</title>
</head>
<body>			             
	<center>   	
   		  <font face="arial" size="3"><b>Annexure- V</b></font>
   	</center>   		
	<div align="right"><font face="arial" size="3"><b>L.F. 05</b></font></div>
	<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
	  	<tr><td width="100%" colspan="12" align="center"><font face="Arial" size="3"><b>NLEP Monthly Reporting Form<br>District / State Report</b></font><br><font face="arial" size=2>(* Delete the level District/State whichever not applicable)</font></td></tr>
	  	<tr>
	  		<td width="50%" colspan="6" ><font face="Arial" size=2>District : <%=selectedOrgUnitName%></font></td>
          	<td width="50%" colspan="6" ><font face="Arial" size=2>State : <%=StateName%></font></td>
         </tr>
  		<tr>
    			<td width="40%"  colspan="6" rowspan="2"><font face="Arial" size="2">Population of the State : </font></td>
    			<td width="20%"  colspan="2" align="center"><font face="Arial" size="2">Total</font></td>
    			<td width="20%"  colspan="2" align="center"><font face="Arial" size="2">SC</font></td>
    			<td width="20%"  colspan="2" align="center"><font face="Arial" size="2">ST</font></td>
  		</tr>
  		<tr>
    			<td width="25%"  colspan="2"><font face="Arial" size="2">&nbsp;</font></td>
    			<td width="25%"  colspan="2"><font face="Arial" size="2">&nbsp;</font></td>
    			<td width="25%"  colspan="2"><font face="Arial" size="2">&nbsp;</font></td>
  		</tr>
  		<tr>
    			<td width="50%"  colspan="6"><font face="Arial" size="2">Reporting Month :&nbsp;</font></td>
    			<td width="50%"  colspan="6"><font face="Arial" size="2">Year&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;<%=partsOfDataPeriodStartDate[0]%></font></td>
  		</tr>
		<tr>
    			<td width="3%"  ><font face="Arial" size="3">1</font></td>
    			<td width="57%"  colspan="6"><font face="Arial" size="2">No. of cases at the beginning of the month</font></td>
    			<td width="25%"  align="center" colspan="3"><font face="Arial" size="2"><%=monthlyValues[0]%></font>&nbsp;</td>
    			<td width="15%"   align="center"><font face="Arial" size="2">xxxxxxx</font></td>
  		</tr>
  		<tr>
    			<td width="3%" rowspan="4"  ><font face="Arial" size="2">2</font></td>
    			<td width="57%" rowspan="4" colspan="6"  ><font face="Arial" size="2">Total New Leprosy Cases detected in the reporting month</font></td>
    			<td width="15%"  colspan="2" >&nbsp;</td>
    			<td width="10%"  align="center" ><font face="Arial" size="2">During Reporting Month</font></td>
    			<td width="15%"  ><font face="Arial" size="2">Cumulative from 1st April</font></td>
  		</tr>
  		<tr>
    			<td width="15%"  colspan="2" ><font face="Arial" size="2">PB-</font></td>
    			<td width="10%"  align="center" ><font face="Arial" size="2"><%=monthlyValues[1]%></font>&nbsp;</td>
    			<td width="15%"   align="center" ><font face="Arial" size="2"><%=cumulativeValues[1]%></font>&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="15%"  colspan="2" ><font face="Arial" size="2">MB-</font></td>
    			<td width="10%"  align="center" ><font face="Arial" size="2"><%=monthlyValues[2]%></font>&nbsp;</td>
    			<td width="15%"   align="center" ><font face="Arial" size="2"><%=cumulativeValues[2]%></font>&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="15%"  colspan="2" > <font face="Arial" size="2">Total</font></td>
    			<td width="10%"  align="center" ><font face="Arial" size="2"><%=(monthlyValues[1]+monthlyValues[2])%></font>&nbsp;</td>
    			<td width="15%"   align="center" ><font face="Arial" size="2"><%=(cumulativeValues[1]+cumulativeValues[2])%></font>&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="3%"  rowspan="5" ><font face="Arial" size="2">3</font></td>
   				<td width="57%"  rowspan="5" colspan="6"><font face="Arial" size="2">Among the new leprosy cases detected during the month</font></td>
    			<td width="15%"  colspan="2" ><font face="Arial" size="2">Children</font></td>
    			<td width="10%"  align="center" ><font face="Arial" size="2"><%=monthlyValues[3]%></font>&nbsp;</td>
    			<td width="15%"   align="center" ><font face="Arial" size="2"><%=cumulativeValues[3]%></font>&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="15%"  colspan="2" ><font face="Arial" size="2">Female</font></td>
    			<td width="10%"  align="center" ><font face="Arial" size="2"><%=monthlyValues[4]%></font>&nbsp;</td>
    			<td width="15%"   align="center" ><font face="Arial" size="2"><%=cumulativeValues[4]%></font>&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="15%"  colspan="2" ><font face="Arial" size="2">Visible Deformity</font></td>
    			<td width="10%"  align="center" ><font face="Arial" size="2"><%=monthlyValues[5]%></font>&nbsp;</td>
    			<td width="15%"   align="center" ><font face="Arial" size="2"><%=cumulativeValues[5]%></font>&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="15%"  colspan="2" ><font face="Arial" size="2">SC</font></td>
    			<td width="10%"  align="center" ><font face="Arial" size="2"><%=monthlyValues[6]%></font>&nbsp;</td>
    			<td width="15%"   align="center" ><font face="Arial" size="2"><%=cumulativeValues[6]%></font>&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="15%"  colspan="2" ><font face="Arial" size="2">ST</font></td>
    			<td width="10%"  align="center" ><font face="Arial" size="2"><%=monthlyValues[7]%></font>&nbsp;</td>
    			<td width="15%"   align="center" ><font face="Arial" size="2"><%=cumulativeValues[7]%></font>&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="3%" rowspan="3"  ><font face="Arial" size="2">4</font></td>
    			<td width="57%"  rowspan="3" colspan="6"><font face="Arial" size="2">Number of Cases deleted in the month</font></td>
    			<td width="15%"  colspan="2" ><font face="Arial" size="2">RFT-</font></td>
    			<td width="10%"  align="center" ><font face="Arial" size="2"><%=monthlyValues[8]%></font>&nbsp;</td>
    			<td width="15%"   align="center" ><font face="Arial" size="2"><%=cumulativeValues[8]%></font>&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="15%"  colspan="2" ><font face="Arial" size="2">Others-</font></td>
    			<td width="10%"  align="center" ><font face="Arial" size="2"><%=monthlyValues[9]%></font>&nbsp;</td>
    			<td width="15%"   align="center" ><font face="Arial" size="2"><%=cumulativeValues[9]%></font>&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="15%"  colspan="2" ><font face="Arial" size="2">Total</font></td>
    			<td width="10%"  align="center"><font face="Arial" size="2"><%=(monthlyValues[8]+monthlyValues[9])%></font>&nbsp;</td>
    			<td width="15%"   align="center"><font face="Arial" size="2"><%=(cumulativeValues[8]+cumulativeValues[9])%></font>&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="3%"  ><font face="Arial" size="2">5</font></td>
    			<td width="57%"  colspan="6"><font face="Arial" size="2">Number of Cases at the end of the reporting month (1+2-4)</td>
    			<td width="25%"  align="center" colspan="3"><font face="Arial" size="2"><font face="Arial" size="2"><%=(monthlyValues[0]+monthlyValues[1]+monthlyValues[2]-monthlyValues[8]-monthlyValues[9])%></font></font>&nbsp;</td>
    			<td width="15%"  align="center" ><font face="Arial" size="2">xxxxxxx</font></td>
  		</tr>
  		<tr>
    			<td width="3%"  ><font face="Arial" size="2">6</font></td>
    			<td width="57%"  colspan="6"><font face="Arial" size="2">Number of Sub.Centres in the State</font></td>
    			<td width="25%"  colspan="3">&nbsp;</td>
    			<td width="15%"  align="center"><font face="Arial" size="2">xxxxxxx</font></td>
  		</tr>
  		<tr>
    			<td width="3%"  ><font face="Arial" size="2">7</font></td>
    			<td width="57%"  colspan="6"><font face="Arial" size="2">Number of Sub-Centres providing MDT</font></td>
    			<td width="25%"  align="center" colspan="3"><font face="Arial" size="2"><%=monthlyValues[10]%></font>&nbsp;</td>
    			<td width="15%"  align="center"><font face="Arial" size="2">xxxxxxx</font></td>
  		</tr>
  		<tr>
    			<td width="3%"  ><font face="Arial" size="2">8</font></td>
    			<td width="97%"  colspan="10"><font face="Arial" size="2">Drug Stock at the end of the reporting month (If required use extra sheets)</font></td>
   		</tr>
  		<tr>
    			<td width="3%"  rowspan="2" >&nbsp;</td>
    			<td width="22%"  align="center" colspan="2" rowspan="2"><font face="Arial" size="2">Blister Packs</font></td>
    			<td width="25%" height="13" align="center" colspan="2"><font face="Arial" size="2">Complled PHC/Dist. Stock</font></td>
    			<td width="25%" height="13" align="center" colspan="4"><font face="Arial" size="2">District/State Store Stock </font></td>
    			<td width="25%" height="13" align="center" colspan="2"><font face="Arial" size="2">Total in the District/State </font></td>
  		</tr>
  		<tr>
    			<td width="13%" height="12" align="center"><font face="Arial" size="2">Quantity</font></td>
    			<td width="12%" height="12" align="center"><font face="Arial" size="2">Expiry Date</font></td>
    			<td width="13%" height="12" align="center" colspan="2"><font face="Arial" size="2">Quantity</font></td>
    			<td width="12%" height="12" align="center" colspan="2"><font face="Arial" size="2">Expiry Date</font></td>
    			<td width="25%" height="12" align="center" colspan="2"><font face="Arial" size="2">Quantity</font></td>
  		</tr>
  		<tr>
    			<td width="3%" >&nbsp;</td>
    			<td width="22%" align="center" colspan="2" ><font face="Arial" size="2">1</font></td>
    			<td width="13%" align="center"><font face="Arial" size="2">2</font></td>
    			<td width="12%" align="center"><font face="Arial" size="2">3</font></td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2">4</font></td>
    			<td width="12%"  align="center" colspan="2"><font face="Arial" size="2">5</font></td>
    			<td width="25%" align="center" colspan="2"><font face="Arial" size="2">6</font></td>
  		</tr>
  		<tr>
    			<td width="3%"  rowspan="3" >&nbsp;</td>
    			<td width="22%"  align="center" rowspan="3" colspan="2" ><font face="Arial" size="2">MB(A)</font></td>
    			<td width="13%"  align="center"><font face="Arial" size="2"><%=monthlyValues[11]%></font>&nbsp;</td>
    			<td width="12%"  align="center">&nbsp;</td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2"><%=monthlyValues[14]%></font>&nbsp;</td>
    			<td width="12%"  align="center" colspan="2">&nbsp;</td>
    			<td width="25%"  align="center" colspan="2" rowspan="3"><font face="Arial" size="2"><%=(monthlyValues[11]+monthlyValues[12]+monthlyValues[13]+monthlyValues[14]+monthlyValues[15]+monthlyValues[16])%></font>&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="13%"  align="center"><font face="Arial" size="2"><%=monthlyValues[12]%></font>&nbsp;</td>
    			<td width="12%"  align="center">&nbsp;</td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2"><%=monthlyValues[15]%></font>&nbsp;</td>
    			<td width="12%"  align="center" colspan="2">&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="13%"  align="center"><font face="Arial" size="2"><%=monthlyValues[13]%></font>&nbsp;</td>
    			<td width="12%"  align="center">&nbsp;</td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2"><%=monthlyValues[16]%></font>&nbsp;</td>
    			<td width="12%"  align="center" colspan="2">&nbsp;</td>
  		</tr>
 		<tr>
    			<td width="3%"  rowspan="3" >&nbsp;</td>
    			<td width="22%"  align="center" rowspan="3" colspan="2" ><font face="Arial" size="2">MB(C)</font></td>
    			<td width="13%"  align="center"><font face="Arial" size="2"><%=monthlyValues[17]%></font>&nbsp;</td>
    			<td width="12%"  align="center">&nbsp;</td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2"><%=monthlyValues[20]%></font>&nbsp;</td>
    			<td width="12%"  align="center" colspan="2">&nbsp;</td>
    			<td width="25%"  align="center" colspan="2" rowspan="3"><font face="Arial" size="2"><%=(monthlyValues[17]+monthlyValues[18]+monthlyValues[19]+monthlyValues[20]+monthlyValues[21]+monthlyValues[22])%></font>&nbsp;</td>
  		</tr>
 		<tr>
    			<td width="13%"  align="center"><font face="Arial" size="2"><%=monthlyValues[18]%></font>&nbsp;</td>
    			<td width="12%"  align="center">&nbsp;</td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2"><%=monthlyValues[21]%></font>&nbsp;</td>
    			<td width="12%"  align="center" colspan="2">&nbsp;</td>
  		</tr>
 		<tr>
    			<td width="13%"  align="center"><font face="Arial" size="2"><%=monthlyValues[19]%></font>&nbsp;</td>
    			<td width="12%"  align="center">&nbsp;</td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2"><%=monthlyValues[22]%></font>&nbsp;</td>
    			<td width="12%"  align="center" colspan="2">&nbsp;</td>
  		</tr>
 		<tr>
    			<td width="3%"  rowspan="3" >&nbsp;</td>
    			<td width="22%"  align="center" rowspan="3" colspan="2" ><font face="Arial" size="2">PB(A)</font></td>
    			<td width="13%"  align="center"><font face="Arial" size="2"><%=monthlyValues[23]%></font>&nbsp;</td>
    			<td width="12%"  align="center">&nbsp;</td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2"><%=monthlyValues[26]%></font>&nbsp;</td>
    			<td width="12%"  align="center" colspan="2">&nbsp;</td>
    			<td width="25%"  align="center" colspan="2" rowspan="3"><font face="Arial" size="2"><%=(monthlyValues[23]+monthlyValues[24]+monthlyValues[25]+monthlyValues[26]+monthlyValues[27]+monthlyValues[28])%></font>&nbsp;</td>
  		</tr>
 		<tr>
    			<td width="13%"  align="center"><font face="Arial" size="2"><%=monthlyValues[24]%></font>&nbsp;</td>
    			<td width="12%"  align="center">&nbsp;</td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2"><%=monthlyValues[27]%></font>&nbsp;</td>
    			<td width="12%"  align="center" colspan="2">&nbsp;</td>
  		</tr>
 		<tr>
    			<td width="13%"  align="center"><font face="Arial" size="2"><%=monthlyValues[25]%></font>&nbsp;</td>
    			<td width="12%"  align="center">&nbsp;</td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2"><%=monthlyValues[28]%></font>&nbsp;</td>
    			<td width="12%"  align="center" colspan="2">&nbsp;</td>
  		</tr>
		<tr>
    			<td width="3%"  rowspan="3" >&nbsp;</td>
    			<td width="22%"  align="center" rowspan="3" colspan="2" ><font face="Arial" size="2">PB(C)</font></td>
    			<td width="13%"  align="center"><font face="Arial" size="2"><%=monthlyValues[29]%></font>&nbsp;</td>
    			<td width="12%"  align="center">&nbsp;</td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2"><%=monthlyValues[32]%></font>&nbsp;</td>
    			<td width="12%"  align="center" colspan="2">&nbsp;</td>
    			<td width="25%"  align="center" colspan="2" rowspan="3"><font face="Arial" size="2"><%=(monthlyValues[29]+monthlyValues[30]+monthlyValues[31]+monthlyValues[32]+monthlyValues[33]+monthlyValues[34])%></font>&nbsp;</td>
  		</tr>
		<tr>
    			<td width="13%"  align="center"><font face="Arial" size="2"><%=monthlyValues[30]%></font>&nbsp;</td>
    			<td width="12%"  align="center">&nbsp;</td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2"><%=monthlyValues[33]%></font>&nbsp;</td>
    			<td width="12%"  align="center" colspan="2">&nbsp;</td>
  		</tr>
		<tr>
    			<td width="13%"  align="center"><font face="Arial" size="2"><%=monthlyValues[31]%></font>&nbsp;</td>
    			<td width="12%"  align="center">&nbsp;</td>
    			<td width="13%"  align="center" colspan="2"><font face="Arial" size="2"><%=monthlyValues[34]%></font>&nbsp;</td>
    			<td width="12%"  align="center" colspan="2">&nbsp;</td>
  		</tr>
  		<tr>
    			<td width="100%"  colspan="11"><font face="Arial" size="2">NB: Please calculate Patient-Month Blister 
    					Packs for MB(A), MB(C), PB(A) and PB(C) Quarterly in the months of March, June, Sept and Dec and indicate the same in that respective Monthly Report.</font></td>
  		</tr>
  		<tr>
    			<td width="3%" height="30" ><font face="Arial" size="2">9</font></td>
    			<td width="72%" height="30" colspan="8"><font face="Arial" size="2">Statement of Expenditure (SOE) of DLS/SLS for the Reporting Month sent</font></td>
    			<td width="25%" align="center" height="30" colspan="3"><font face="Arial" size="2">YES / NO</font></td>
  		</tr>
  		<tr>
    			<td width="10%" height="50" colspan="2"><font face="Arial" size="2">Date</font></td>
    			<td width="45%" height="50" colspan="4">&nbsp;</td>
    			<td width="45%" height="50" colspan="5"><font face="Arial" size="2">Name and Signature of Medical Officer</font></td>
  		</tr>
  </table>			             
</body>
</html>