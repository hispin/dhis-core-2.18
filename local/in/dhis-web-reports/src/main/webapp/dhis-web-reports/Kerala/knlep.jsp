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
	
  
  	  String startingDate  = (String) stack.findValue( "startingPeriod" );
	  String endingDate  = (String) stack.findValue( "endingPeriod" );
  
      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	  	  	
  	String monthlyDataElements[] = {
  											// No of Leprosy Cases at the begining of the reporting month
  											"NLEP_DE1",
  											// No of PB new Leprosy Cases detected
  											"NLEP_DE2",
  											// No of MB new Leprosy Cases detected
  											"NLEP_DE3",
  											// No of new Leprosy Cases detected in children
  											"NLEP_DE4",
  											// No of new Leprosy Cases detected in Female
  											"NLEP_DE5",
  											// No of new Leprosy Cases detected - Visible Deformity
  											"NLEP_DE6",
  											// No of new Leprosy Cases detected - SC
  											"NLEP_DE7",
  											// No of new Leprosy Cases detected - ST
  											"NLEP_DE8",
  											//No of RFT Cases deleted
  											"NLEP_DE9",
  											//No of Other Cases deleted
  											"NLEP_DE10",
  											//No of Sub-Centres providing MDT services
  											"NLEP_DE11",
  											//BLISTER PACK MB(A)1
  											"NLEP_DE12",
  											//BLISTER PACK MB(A)2
  											"NLEP_DE13",
  											//BLISTER PACK MB(A)3
  											"NLEP_DE14",
  											//BLISTER PACK MB(C)1
  											"NLEP_DE15",
  											//BLISTER PACK MB(C)2
  											"NLEP_DE16",
  											//BLISTER PACK MB(C)3
  											"NLEP_DE17",
  											//BLISTER PACK PB(A)1
  											"NLEP_DE18",
  											//BLISTER PACK PB(A)2
  											"NLEP_DE19",
  											//BLISTER PACK PB(A)3
  											"NLEP_DE20",
  											//BLISTER PACK PB(C)1
  											"NLEP_DE21",
  											//BLISTER PACK PB(C)2
  											"NLEP_DE22",
  											//BLISTER PACK PB(C)3
  											"NLEP_DE23"
										};
					
 	  int monthlyValues[] = new int[monthlyDataElements.length+5]; 		
 	  int cumulativeValues[] =  new int[monthlyDataElements.length+5];
 	  
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
 	
  	  String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September",
  									"October", "November", "December" };   	
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
                
  //      	rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	   	if(rs2.next()) 	  {  selectedDataPeriodStartDate =  rs2.getDate(1).toString();	} 		  
	
			selectedDataPeriodStartDate = startingDate;
	
	   		   	
		//rs5=st5.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
		rs5=st5.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");	
		if(rs5.next())  { CHCID = rs5.getInt(1);CHCName = rs5.getString(2);  } 
		else  {  CHCID = 0; CHCName = "";  } 

		//rs6=st6.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+CHCID+")");
		rs6=st6.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+CHCID+")");	
		if(rs6.next())  { TalukID = rs6.getInt(1);TalukName = rs6.getString(2);  } 
		else  {  TalukID = 0; TalukName = "";  } 

		//rs7=st7.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+TalukID+")");
		rs7=st7.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+TalukID+")");	
		if(rs7.next())  { DistrictID = rs7.getInt(1);DistrictName = rs7.getString(2);  } 
		else  {  DistrictID = 0; DistrictName = "";  } 

		//rs8=st8.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+DistrictID+")");
		rs8=st8.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+DistrictID+")");	
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
				//rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id INNER JOIN period ON datavalue.period = period.id INNER JOIN organisationunit ON datavalue.source = organisationunit.parent WHERE dataelement.name like '"+monthlyDataElements[i]+"' AND organisationunit.parent="+selectedOrgUnitID+" AND period.id in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') ");
				rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid INNER JOIN period ON datavalue.periodid = period.periodid INNER JOIN organisationunit ON datavalue.sourceid = organisationunit.parentid WHERE dataelement.name like '"+monthlyDataElements[i]+"' AND organisationunit.parentid="+selectedOrgUnitID+" AND period.periodid in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') ");    
        		if(rs3.next()) 		{  monthlyValues[i] =  rs3.getInt(1);	}  
		  			
				//rs4 = st4.executeQuery("select sum(value) from datavalue where dataElement in (select id from dataelement where code like '"+monthlyDataElements[i]+"') and source in (select id from organisationunit where parent ="+selectedOrgUnitID+") and period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+")");
				rs4 = st4.executeQuery("select sum(value) from datavalue where dataelementid in (select dataelementid from dataelement where code like '"+monthlyDataElements[i]+"') and sourceid in (select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+") and periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodtypeid = "+periodTypeID+")");        			        		
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
	<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
	  	<tr><td width="100%" colspan="8" align="center"><font face="Arial" size="3"><b>NLEP Monthly Reporting Form</b></font></td></tr>
	  	<tr><td width="100%" colspan="8" align="center"><font face="Arial" size="3"><b>PHC/Block PHC Report</b></font></td></tr>
	  	<tr>
    			<td width="50%" height="25" colspan="3"><font face="Arial" size="2">PHC&nbsp;&nbsp;&nbsp;&nbsp;<%=selectedOrgUnitName%></font></td>
    			<td width="50%" height="25" colspan="5"><font face="Arial" size="2">Block&nbsp;&nbsp;&nbsp;&nbsp;<%=CHCName%></font></td>
  	  	</tr>
  		<tr>
    			<td width="50%" height="25" colspan="3"><font face="Arial" size="2">District&nbsp;&nbsp;&nbsp;&nbsp;<%=DistrictName%></font></td>
    			<td width="50%" height="25" colspan="5"><font face="Arial" size="2">State&nbsp;&nbsp;&nbsp;&nbsp;<%=StateName%></font></td>
  		</tr>
  		<tr>
    			<td width="50%" height="25" colspan="3"><font face="Arial" size="2">Reporting Month&nbsp;&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%></font></td>
    			<td width="50%" height="25" colspan="5"><font face="Arial" size="2">Year&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=partsOfDataPeriodStartDate[0]%></font></td>
  		</tr>
  		<tr>
    			<td width="5%" height="25" ><font face="Arial" size="3">1</font></td>
    			<td width="55%" height="25" colspan="4"><font face="Arial" size="2">No. of cases at the beginning of the reporting month</font></td>
    			<td width="25%" height="25" colspan="2">&nbsp;</td>
    			<td width="15%" height="25"  align="center"><font face="Arial" size="2"><%=monthlyValues[0]%></font></td>
  		</tr>
  		<tr>
    			<td width="5%" rowspan="3" height="25" ><font face="Arial" size="2">2</font></td>
    			<td width="55%" rowspan="3" colspan="4" height="25" ><font face="Arial" size="2">Total New Leprosy Cases detected in the reporting month</font></td>
    			<td width="15%" height="25" >&nbsp;</td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2">During Reporting Month</font></td>
    			<td width="15%" height="25" ><font face="Arial" size="2">Cumulative from 1st April</font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" ><font face="Arial" size="2">PB-</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[1]%></font></td>
    			<td width="15%" height="25"  align="center" ><font face="Arial" size="2"><%=cumulativeValues[1]%></font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" ><font face="Arial" size="2">MB-</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[2]%></font></td>
    			<td width="15%" height="25"  align="center" ><font face="Arial" size="2"><%=cumulativeValues[2]%></font></td>
  		</tr>
  		<tr>
    			<td width="5%" height="25" rowspan="5" ><font face="Arial" size="2">3</font></td>
    				<td width="55%" height="25" rowspan="5" colspan="4"><font face="Arial" size="2">Among the new leprosy cases detected during the reporting month, number of-</font></td>
    			<td width="15%" height="25" ><font face="Arial" size="2">Children</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[3]%></font></td>
    			<td width="15%" height="25"  align="center" ><font face="Arial" size="2"><%=cumulativeValues[3]%></font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" ><font face="Arial" size="2">Female</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[4]%></font></td>
    			<td width="15%" height="25"  align="center" ><font face="Arial" size="2"><%=cumulativeValues[4]%></font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" ><font face="Arial" size="2">Visible Deformity</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[5]%></font></td>
    			<td width="15%" height="25"  align="center" ><font face="Arial" size="2"><%=cumulativeValues[5]%></font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" ><font face="Arial" size="2">SC</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[6]%></font></td>
    			<td width="15%" height="25"  align="center" ><font face="Arial" size="2"><%=cumulativeValues[6]%></font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" ><font face="Arial" size="2">ST</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[7]%></font></td>
    			<td width="15%" height="25"  align="center" ><font face="Arial" size="2"><%=cumulativeValues[7]%></font></td>
  		</tr>
  		<tr>
    			<td width="5%" rowspan="2" height="25" ><font face="Arial" size="2">4</font></td>
    			<td width="55%" height="25" rowspan="2" colspan="4"><font face="Arial" size="2">Number of Cases deleted in the reporting month</font></td>
    			<td width="15%" height="25" ><font face="Arial" size="2">RFT-</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[8]%></font></td>
    			<td width="15%" height="25"  align="center" ><font face="Arial" size="2"><%=cumulativeValues[8]%></font></td>
  		</tr>
  		<tr>
    			<td width="15%" height="25" ><font face="Arial" size="2">Others-</font></td>
    			<td width="10%" height="25" align="center" ><font face="Arial" size="2"><%=monthlyValues[9]%></font></td>
    			<td width="15%" height="25"  align="center" ><font face="Arial" size="2"><%=cumulativeValues[9]%></font></td>
  		</tr>
  		<tr>
    			<td width="5%" height="25" ><font face="Arial" size="2">5</font></td>
    			<td width="55%" height="25" colspan="4"><font face="Arial" size="2">Number of Cases at the end of the reporting month (1+2-4)</font></td>
    			<td width="25%" height="25" colspan="2" align="center"><font face="Arial" size="2"><%=(monthlyValues[1] + monthlyValues[2] - monthlyValues[4])%>&nbsp;</font></td>
    			<td width="15%" height="25" align="center"><font face="Arial" size="2"><%=(cumulativeValues[1] + cumulativeValues[2]- cumulativeValues[4])%></font></td>
  		</tr>
  		<tr>
    			<td width="5%" height="25" ><font face="Arial" size="2">6</font></td>
    			<td width="55%" height="25" colspan="4"><font face="Arial" size="2">Number of Sub-Centres providing MDT services</font></td>
    			<td width="25%" height="25" colspan="2"	align="center"><%=monthlyValues[11]%>&nbsp;</td>
    			<td width="15%" height="25" align="center"><font face="Arial" size="2"><%=cumulativeValues[11]%></font></td>
  		</tr>
  		<tr>
    			<td width="5%" height="25" ><font face="Arial" size="2">7</font></td>
    			<td width="95%" height="25" colspan="7"><font face="Arial" size="2">Leprosy Drug Stock at the end of the reporting month</font></td>
   		</tr>
  		<tr>
    			<td width="5%" height="25" >&nbsp;</td>
    			<td width="20%" height="25" align="center"><font face="Arial" size="2">Blister Pack</font></td>
    			<td width="25%" height="25" align="center"><font face="Arial" size="2">Quantity</font></td>
    			<td width="25" height="25" 	align="center" colspan="3"><font face="Arial" size="2">Expiry Date</font></td>
    			<td width="25%" height="25" align="center" colspan="2"><font face="Arial" size="2">Total Stock</font></td>
  		</tr>
  		<tr>
    			<td width="5%" height="25" rowspan="3" >&nbsp;</td>
    			<td width="20%" height="25" align="center" rowspan="3" ><font face="Arial" size="2">MB(A)</font></td>
    			<td width="25%" height="25"	align="center"><%=monthlyValues[12]%></td>
    			<td width="25" height="25" colspan="3"></td>
    			<td width="25%" height="25" colspan="2" rowspan="3"align="center"><%=monthlyValues[12] + monthlyValues[13] + monthlyValues[14]%></td>
  		</tr>
  		<tr>
    			<td width="25%" height="25"	align="center"><%=monthlyValues[13]%></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
  		<tr>
    			<td width="25%" height="25"	align="center"><%=monthlyValues[14]%></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
 		<tr>
    			<td width="5%" height="25" rowspan="3" >&nbsp;</td>
    			<td width="20%" height="25" align="center" rowspan="3" ><font face="Arial" size="2">MB(C)</font></td>
    			<td width="25%" height="25"	align="center"><%=monthlyValues[15]%></td>
    			<td width="25" height="25" colspan="3"></td>
    			<td width="25%" height="25" colspan="2" rowspan="3"align="center"><%=(monthlyValues[15] + monthlyValues[16] + monthlyValues[17])%></td>
  		</tr>
 		<tr>
    			<td width="25%" height="25"	align="center"><%=monthlyValues[16]%></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
 		<tr>
    			<td width="25%" height="25"	align="center"><%=monthlyValues[17]%></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
 		<tr>
    			<td width="5%" height="25" rowspan="3" >&nbsp;</td>
    			<td width="20%" height="25" align="center" rowspan="3" ><font face="Arial" size="2">PB(A)</font></td>
    			<td width="25%" height="25"	align="center"><%=monthlyValues[18]%></td>
    			<td width="25" height="25" colspan="3"></td>
    			<td width="25%" height="25" colspan="2" rowspan="3"align="center"><%=(monthlyValues[18] + monthlyValues[19] + monthlyValues[20])%></td>
  		</tr>
 		<tr>
    			<td width="25%" height="25"	align="center"><%=monthlyValues[19]%></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
 		<tr>
    			<td width="25%" height="25"	align="center"><%=monthlyValues[20]%></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
		<tr>
    			<td width="5%" height="25" rowspan="3" >&nbsp;</td>
    			<td width="20%" height="25" align="center" rowspan="3" ><font face="Arial" size="2">PB(C)</font></td>
    			<td width="25%" height="25"	align="center"><%=monthlyValues[21]%></td>
    			<td width="25" height="25" colspan="3"></td>
    			<td width="25%" height="25" colspan="2" rowspan="3"	align="center"><%=(monthlyValues[21] + monthlyValues[22] + monthlyValues[23])%></td>
  		</tr>
		<tr>
    			<td width="25%" height="25"	align="center"><%=monthlyValues[22]%></td>
    			<td width="25" height="25" colspan="3"></td>
  		</tr>
		<tr>
    			<td width="25%" height="25" align="center"><%=monthlyValues[23]%></td>
    			<td width="25" height="25" colspan="3"	align="center"></td>
  		</tr>
  		<tr>
    			<td width="100%" height="25" colspan="8"><font face="Arial" size="2">NB: Please calculate Patient-Month Blister 
    					Packs for MB(A), MB(C), PB(A) and PB(C) Quarterly in the months of March, June, Sept and Dec and indicate the same in that respective Monthly Report.</font></td>
  		</tr>
  		<tr>
    			<td width="5%" height="50" ><font face="Arial" size="2">Date</font></td>
    			<td width="45%" height="50" colspan="3">&nbsp;</td>
    			<td width="50%" height="50" colspan="4"><font face="Arial" size="2">Name and Signature of Medical Officer</font></td>
  		</tr>
  </table>			             
</body>
</html>