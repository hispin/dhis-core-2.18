
<%@ page import="java.sql.*,java.util.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>
<%
      Connection con=null;
      
      // for Performance in the Reporting Month
      Statement st=null;
      ResultSet rs=null;
      
      // for selected OrgUnit Name and Population
      Statement st1=null;
      ResultSet rs1=null;
      
      // for Performance in Corresponding month Last Year
      Statement st2=null;
      ResultSet rs2=null;
      
      // for Cumulative Performance till Current Month
      Statement st4=null;
      ResultSet rs4=null;
     
      // for Cumulative Performance till corresponding month of Last Year
      Statement st3=null;
      ResultSet rs3=null;
 
      // for Taluk Name and Id
      Statement st5=null;
      ResultSet rs5=null;

      // for District Name and Id
      Statement st6=null;
      ResultSet rs6=null;

     // for PHC Name and Id
      Statement st8=null;
      ResultSet rs8=null;

     // for CHC Name and Id
      Statement st9=null;
      ResultSet rs9=null;

     // for PHC Population Estimates
      Statement st10=null;
      ResultSet rs10=null;

     // for Data Period Start Date and End Date
      Statement st11=null;
      ResultSet rs11=null;
      
     // for DataElement ids based on DataElement code
      Statement st12=null;
      ResultSet rs12=null;
     
      
      String userName = "dhis";           
      String password = "";           
      String urlForConnection = "jdbc:mysql://localhost/kl_dhis2";
          
      int talukID = 0;
      String talukName = "";
      int districtID = 0; 
      String districtName = ""; 
      int CHCID = 0;
      String CHCName ="";
      int PHCID = 0;
      String PHCName ="";          
      int totPHCPopulation = -1;
      int totSCPopulation = -1;

	  OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  int selectedOrgUnitID =  Integer.parseInt( selectedId );
	
      
	  String startingDate  =  (String) stack.findValue( "startingPeriod" );
	  String endingDate  = 	  (String) stack.findValue( "endingPeriod" );

	  String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	  int periodTypeID =  Integer.parseInt( monthlyPeriodId );

	       
   	  int lastYear = 0;
      
	  String selectedOrgUnitName = "";
	  String selectedDataPeriodStartDate = "";
   	  String selectedDataPeriodEndDate = "";
	  String lastDataPeriodStartDate = "";
	  
	   String dataElementCodes[] = {

										// NEW CASES REGISTERED MONTHLY					
										"'JPHN/JHI_DE38'",
										// NEW CASES REGISTERED YEARLY
										"'JPHN/JHI_DE38'",
										// TOTAL CASES FOLLOWED UP TO LAST MONTH MONTHLY
										"'JPHN/JHI_DE39'",
										// TOTAL CASES FOLLOWED UP TO LAST MONTH YEARLY
										"'JPHN/JHI_DE39'",
										
										//CASES COMPLETED TREATEMENT MONTHLY
										"'JPHN/JHI_DE40'",
										//CASES COMPLETED TREATEMENT YEARLY
										"'JPHN/JHI_DE40'",
										
										// CASES CURED MONTHLY
										"'JPHN/JHI_DE41'",
										// CASES CURED YEARLY
										"'JPHN/JHI_DE41'",


										// REMAIN UNDER TREATMENT Monthly
										"'JPHN/JHI_DE42'",
										// REMAIN UNDER TREATMENT YEARLY
										"'JPHN/JHI_DE42'",
										
										// DEFAULT CASES MONTHLY
										"'JPHN/JHI_DE43'",
										// DEFAULT CASES YEARLY
										"'JPHN/JHI_DE43'",
										
										// DEATH MONTHLY
										"'JPHN/JHI_DE44'",
										// DEATH YEARLY
										"'JPHN/JHI_DE44'",
										
													   
			  	  				};

                 
 	int dataElementIDs[] = new int[dataElementCodes.length+5];  
	int entryNumberValues[]=  new int[dataElementCodes.length+5];	  
   	int entryValuesForLastYear[]= new int[dataElementCodes.length+5];      
   	int cumentryValuesForCurYear[]= new int[dataElementCodes.length+5];      
   	int cumentryValuesForLastYear[]= new int[dataElementCodes.length+5];     
   	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  
  	
  	String query = "";
  	
  	List childOrgUnitIDs = new ArrayList();	
	List childOrgUnitNames = new ArrayList();	
	
	int tempval[] = new int[dataElementCodes.length+5];
	int total[] = new int[dataElementCodes.length+5];

		
  	int childOrgUnitCount = 0;  	
  	int count = 0;  	
  	int i=0;
	int totpopulation=0;

%>

<%
     try
      {
        Class.forName ("com.mysql.jdbc.Driver").newInstance ();
        con = DriverManager.getConnection (urlForConnection, userName, password);
        
        st=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY); 
        st1=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st2=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st3=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st4=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st5=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY); 
        st6=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st8=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st9=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st10=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st11=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st12=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

     
        //rs1 = st1.executeQuery("SELECT organisationunit.name FROM organisationunit WHERE id ="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.name FROM organisationunit WHERE organisationunitid ="+selectedOrgUnitID);        
        if(rs1.next())  {   selectedOrgUnitName = rs1.getString(1);       }
        else   {     	selectedOrgUnitName = "";        }  
        
        
		//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate like '"+startingDate+"' and periodType = 3) AND datavalue.source in ( select id from organisationunit where parent in ( select id from organisationunit where parent ="+selectedOrgUnitID+")) AND dataelement.code in ('SPD_DE1','SPD_DE2','SPD_DE3','SPD_DE4','SPD_DE5','SPD_DE6','SPD_DE7','SPD_DE8')"; 
		query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate like '"+startingDate+"' and periodtypeid = 3) AND datavalue.sourceid in ( select organisationunitid from organisationunit where parentid in ( select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+")) AND dataelement.code in ('SPD_DE1','SPD_DE2','SPD_DE3','SPD_DE4','SPD_DE5','SPD_DE6','SPD_DE7','SPD_DE8')";
      	rs11 = st11.executeQuery(query);
		if(rs11.next())
		  {
		  totpopulation=rs11.getInt(1);
		  }



//      rs11 = st11.executeQuery("select startDate,endDate from period where id = "+selectedDataPeriodID);
//		if(rs11.next())
//		  {
//			selectedDataPeriodStartDate =  rs11.getDate(1).toString();
//			selectedDataPeriodEndDate   =  rs11.getDate(2).toString();
//		  }

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


      } // try block end
     catch(Exception e)  { out.println("exception 1 -"+e.getMessage());  }
     finally
       {
			try
				{
  					if(rs1!=null)  rs1.close();			if(st1!=null)  st1.close();
					if(rs11!=null)  rs11.close();		if(st11!=null)  st11.close();
				}
			catch(Exception e)   {  out.println("exception 2 - "+e.getMessage());   }
       } // finally block end		

	String partsOfDataPeriodStartDate[]  =  selectedDataPeriodStartDate.split("-");
	lastYear  = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;
	lastDataPeriodStartDate = lastYear+"-"+partsOfDataPeriodStartDate[1]+"-"+partsOfDataPeriodStartDate[2];
     	
	int tempForMonth1 = Integer.parseInt(partsOfDataPeriodStartDate[1]);
	int tempForYear = 0;
	 	
	if(tempForMonth1 < 4)   	{   tempForYear = lastYear;  }
 	else  {   tempForYear = lastYear + 1;   	}
 	  	   	 
 	String curYearStart = tempForYear+"-04-01";
 	String lastYearStart = (tempForYear-1)+"-04-01";
 	String lastYearEnd = lastYear+"-"+partsOfDataPeriodStartDate[1]+"-"+partsOfDataPeriodStartDate[2];
	 
    //for district, taluk, CHC names
    try
      {
	   //     rs8=st8.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");        
	//		if(rs8.next())  { PHCID = rs8.getInt(1);PHCName = rs8.getString(2);  } 
	//		else  {  PHCID = 0; PHCName = "";  } 

	//		rs9=st9.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");	
	//		if(rs9.next())  { CHCID = rs9.getInt(1);CHCName = rs9.getString(2);  } 
	//		else  {  CHCID = 0; CHCName = "";  } 

			//rs5=st5.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID +")");
			rs5=st5.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID +")");	
			if(rs5.next())  { talukID = rs5.getInt(1); talukName = rs5.getString(2);  } 
			else  {  talukID = 0; talukName = "";  } 
        
		    //rs6=st6.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+talukID+")");
		    rs6=st6.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+talukID+")"); 
		    if(rs6.next()) {  districtID = rs6.getInt(1); districtName = rs6.getString(2);}
			else {districtID = 0; districtName = "";}      

			//rs10=st10.executeQuery("SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (dataelement INNER JOIN datavalue ON dataelement.id = datavalue.dataElement) ON organisationunit.id = datavalue.source WHERE organisationunit.parent = "+PHCID+" AND dataelement.name like 'Total Population'");
			rs10=st10.executeQuery("SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (dataelement INNER JOIN datavalue ON dataelement.dataelementid = datavalue.dataelementid) ON organisationunit.organisationunitid = datavalue.sourceid WHERE organisationunit.parentid = "+PHCID+" AND dataelement.name like 'Total Population'");
			if(rs10.next()) { totPHCPopulation = rs10.getInt(1);}
			else {totPHCPopulation = 0;}      
       
       }   // try block end		 
     catch(Exception e)  { out.println(e.getMessage());  }
    
       
    %>

<html>

<head>
<title>RNTCP(TB)</title>
</head>

<body>

<FONT face="Arial" size="3"><center>Consolidation Report of RNTCP(TB) of the CHC/Block PHC <b><u><%=selectedOrgUnitName%></u> </b> the month of<b><%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%></b></font><br><br>
<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%" id="AutoNumber1">
  <tr>
    <td width="50%"><FONT face="Arial" size="2">Total Population :<%=totpopulation%></font></td>
    <td width="50%"><FONT face="Arial" size="2">District :<%=districtName%></Font></td>
  </tr>
</table><br>
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%" id="AutoNumber2">
  <tr>
    <td align="center"	width="3%" rowspan="3"><FONT face="Arial" size="2">Sl.No</Font></td>
    <td align="center"	width="27%" rowspan="3"><FONT face="Arial" size="2">Name of the Mini PHC</Font></td>
    <td align="center"	width="70%" colspan="14"><FONT face="Arial" size="2">RNTCP(TB)</Font></td>
  </tr>
  <tr>
    <td align="center"	width="10%" colspan="2"><FONT face="Arial" size="2">New Cases Registered</Font></td>
    <td align="center"	width="10%" colspan="2"><FONT face="Arial" size="2">Total Cases Followed up to last Month</Font></td>
    <td align="center"	width="10%" colspan="2"><FONT face="Arial" size="2">Cases Completed Treatment</Font></td>
    <td align="center"	width="10%" colspan="2"><FONT face="Arial" size="2">Cases Cured</Font></td>
    <td align="center"	width="10%" colspan="2"><FONT face="Arial" size="2">Remain Under Treatment</Font></td>
    <td align="center"	width="10%" colspan="2"><FONT face="Arial" size="2">Default Cases</Font></td>
    <td align="center"	width="10%" colspan="2"><FONT face="Arial" size="2">Death</Font></td>
  </tr>
  <tr>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Monthly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Yearly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Monthly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Yearly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Monthly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Yearly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Monthly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Yearly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Monthly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Yearly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Monthly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Yearly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Monthly</Font></td>
    <td align="center"	width="5%"><FONT face="Arial" size="2">Yearly</Font></td>
  </tr>
   <%
			
			try
			  {
				  count = 0;
				  while(count < childOrgUnitCount)
				   { 				   
				   	Integer temp1 = (Integer) childOrgUnitIDs.get(count);
					int currentChildID = temp1.intValue(); 			
					for(i=0;i<dataElementCodes.length;i++)
					 {			
						
						if(i==0 || i==2 || i==4 || i==6 || i==8 || i==10 || i==12)
						  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (select id from organisationunit where parent = "+currentChildID+")  AND dataelement.code in ("+dataElementCodes[i]+")";
						  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid = "+currentChildID+")  AND dataelement.code in ("+dataElementCodes[i]+")";
						else   
						  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+startingDate+"') AND datavalue.source in (select id from organisationunit where parent = "+currentChildID+")  AND dataelement.code in ("+dataElementCodes[i]+")";
						  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+startingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid = "+currentChildID+")  AND dataelement.code in ("+dataElementCodes[i]+")";
						
						rs4 = st4.executeQuery(query);
						
												
						if(!rs4.next())  {  tempval[i] = 0;	 }
						else   {  tempval[i] = rs4.getInt(1);  }
						total[i] += tempval[i];
					 } 	
		    %>

  <tr>
    <td align="center"	width="3%"><FONT face="Arial" size="2"><%=(count+1)%></Font>&nbsp;</td>
    <td width="27%"><FONT face="Arial" size="2"><%=childOrgUnitNames.get(count)%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[0]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[1]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[2]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[3]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[4]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[5]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[6]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[7]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[8]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[9]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[10]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[11]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[12]%></Font>&nbsp;</td>
    <td align="center"	width="5%"><FONT face="Arial" size="2"><%=tempval[13]%></Font>&nbsp;</td>
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
			    if(rs5!=null)  rs5.close();			if(st5!=null)  st5.close();
			    if(rs6!=null)  rs6.close();			if(st6!=null)  st6.close();
			    if(rs8!=null)  rs8.close();			if(st8!=null)  st8.close();   
			    if(rs9!=null)  rs9.close();			if(st9!=null)  st9.close();
			    if(rs10!=null)  rs10.close();			if(st10!=null)  st10.close();                                                                                                                
			  }
		catch(Exception e)   {  out.println(e.getMessage());   }
       }  // finally block end	
  		%>

  
  
  

</table>

</body>

</html>