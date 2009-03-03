<%@ page import="java.sql.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>

<%
      Connection con=null;
            
      // for selected OrgUnit Name
      Statement st1 = null;
      ResultSet rs1 = null;
      
      // for Data Period Start Date and End Date
      Statement st2 = null;
      ResultSet rs2 = null;
      
      //for no. of acceptors during the month
      Statement st3 = null;
      ResultSet rs3 = null;

      //for no. of acceptors during year
      Statement st4 = null;
      ResultSet rs4 = null;
      
      Statement st5 = null;
      ResultSet rs5 = null;
      
      Statement st6 = null;
      ResultSet rs6 = null;
      
      Statement st7 = null;
      ResultSet rs7 = null;
      
      Statement st8 = null;
      ResultSet rs8 = null;
      

      
      String userName = "dhis";           
      String password = "";           
      String urlForConnection = "jdbc:mysql://localhost/kl_dhis2";
          
	  OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  int selectedOrgUnitID = 	  Integer.parseInt( selectedId );
	
//	  String selectedPeriodId = (String) stack.findValue( "periodSelect" );
//	  int selectedDataPeriodID = 	  Integer.parseInt( selectedPeriodId );
      
	  String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	  int periodTypeID = 	  Integer.parseInt( monthlyPeriodId );

	  String startingDate  = (String) stack.findValue( "startingPeriod" );
	  String endingDate  = (String) stack.findValue( "endingPeriod" );

	       
      int lastYear = 0;
      int i = 0;
      
	  String selectedOrgUnitName = "";
	  String selectedDataPeriodStartDate = "";
      String selectedDataPeriodEndDate = "";
      
      int PHCID = 0;
      int CHCID =0;
      int talukID=0;
      int districtID=0;
      
      String PHCName=" ";
      String CHCName =" ";
      String talukName=" "; 
      String districtName =" ";
      
	  String dataElementCodes[] = {
	  								"IEC_DE1",
	  								"IEC_DE2",
	  								"IEC_DE3",
	  								"IEC_DE4",
	  								"IEC_DE5",
	  								"IEC_DE6",
	  								"IEC_DE7",
	  								"IEC_DE8",
	  								"IEC_DE9",
	  								"IEC_DE10",
	  								"IEC_DE11",
	  								"IEC_DE12",
	  								"IEC_DE13",
	  								"IEC_DE14",
	  								"Form6_DE1",
	  								"IEC_DE15",
	  								"IEC_DE16 ",
	  								"IEC_DE17",
	  								"IEC_DE18",
	  								"IEC_DE19",
	  								"IEC_DE20",
	  								"IEC_DE21",
	  								"IEC_DE22",
	  								"IEC_DE23",
	  								"IEC_DE24",
	  								"IEC_DE25"
	  							  };     
	  int monthlyDataValues[] = new int[dataElementCodes.length];
	  int yearlyDataValues[]  = new int[dataElementCodes.length];
%>

<%
     try
      {
        Class.forName ("com.mysql.jdbc.Driver").newInstance ();
        con = DriverManager.getConnection (urlForConnection, userName, password);
        
        st1 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st3 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st4 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st5 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st6 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st7 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st8 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

        //rs1 = st1.executeQuery("SELECT organisationunit.name FROM organisationunit where organisationunit.id ="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.name FROM organisationunit where organisationunit.organisationunitid ="+selectedOrgUnitID);        
        if(rs1.next())  { selectedOrgUnitName = rs1.getString(1);       }
        else   {  selectedOrgUnitName = "";  }  

//        rs2 = st2.executeQuery("select startDate,endDate from period where id = "+selectedDataPeriodID);
//	    if(rs2.next())
//		  {
//		    selectedDataPeriodStartDate =  rs2.getDate(1).toString();
//			selectedDataPeriodEndDate   =  rs2.getDate(2).toString();
//		  }

		selectedDataPeriodStartDate = startingDate;

   		String partsOfDataPeriodStartDate[]  =  selectedDataPeriodStartDate.split("-");
     	lastYear  = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;     	
	 	int tempForMonth1 = Integer.parseInt(partsOfDataPeriodStartDate[1]);
	 	int tempForYear = 0;	 	
     	if(tempForMonth1 < 4)   	{   tempForYear = lastYear;  }
 	 	else  {   tempForYear = lastYear + 1;   	} 	 
 	 	String curYearStart = tempForYear+"-04-01";

			//rs3=st3.executeQuery("SELECT sum(value) FROM datavalue WHERE dataElement in (select id from dataelement where code like '"+dataElementCodes[i]+"') and source in (select id from organisationunit where parent ="+selectedOrgUnitID+")  and period in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') ");
			rs3=st3.executeQuery("SELECT sum(value) FROM datavalue WHERE dataelementid in (select dataelementid from dataelement where code like '"+dataElementCodes[i]+"') and sourceid in (select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+")  and periodid in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') ");
	    	if(rs3.next())  { monthlyDataValues[i] = rs3.getInt(1);   } 

			//rs4=st4.executeQuery("SELECT sum(value) FROM datavalue WHERE dataElement in (select id from dataelement where code like '"+dataElementCodes[i]+"') and source in (select id from organisationunit where parent ="+selectedOrgUnitID+")  and period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+")");
			rs4=st4.executeQuery("SELECT sum(value) FROM datavalue WHERE dataelementid in (select dataelementid from dataelement where code like '"+dataElementCodes[i]+"') and sourceid in (select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+")  and periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodtypeid = "+periodTypeID+")");
	    	if(rs4.next())  { yearlyDataValues[i] = rs4.getInt(1);   } 
	    	
	    	 //for district, taluk, CHC names
    
	       
			//rs8=st8.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
			rs8=st8.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");	
			if(rs8.next())  { CHCID = rs8.getInt(1);CHCName = rs8.getString(2);  } 
			else  {  CHCID = 0; CHCName = "";  } 

			//rs5=st5.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+CHCID+")");
			rs5=st5.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+CHCID+")");	
			if(rs5.next())  { talukID = rs5.getInt(1); talukName = rs5.getString(2);  } 
			else  {  talukID = 0; talukName = "";  } 
        
		    //rs6=st6.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+talukID+")");
		    rs6=st6.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+talukID+")"); 
		    if(rs6.next()) {  districtID = rs6.getInt(1); districtName = rs6.getString(2);}
			else {districtID = 0; districtName = "";}      	    	
	  } // try block end
     catch(Exception e)  { out.println(e.getMessage());  }
     finally
       {
			try
				{
  					if(rs1!=null)  rs1.close();			if(st1!=null)  st1.close();
					if(rs2!=null)  rs2.close();			if(st2!=null)  st2.close();
					if(rs3!=null)  rs3.close();			if(st3!=null)  st3.close();
					if(rs4!=null)  rs4.close();			if(st4!=null)  st4.close();
					if(rs5!=null)  rs5.close();			if(st5!=null)  st5.close();			
                    if(rs6!=null)  rs6.close();			if(st6!=null)  st6.close();
                    if(rs7!=null)  rs7.close();			if(st7!=null)  st7.close();
					if(rs8!=null)  rs8.close();			if(st8!=null)  st8.close();
										
					if(con!=null) con.close();
				}
			catch(Exception e)   {  out.println(e.getMessage());   }
       } // finally block end		  	        
%>




<html>

        <head>
        <title>IEC REPORT</title>
        </head>
  		<body>
<center>
<b>
<font face="arial" size=3>IEC REPORT</font><br><br>

</center>
<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%" id="AutoNumber1">
  <tr>
    <td width="50%" ><font face="arial" size=2>Dist :<%=districtName%> </font></td>
    <td width="50%" align="right"><font face="arial" size=2> PHC :<%=selectedOrgUnitName%></font></td>
  </tr>
</table><br><br>
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%" id="AutoNumber2">
  <tr>
    <td width="5%" 	align="center"><font face="arial" size=2>Sl.No</font></td>
    <td width="75%"	align="center"><font face="arial" size=2>Activities</font></td>
    <td width="10%"	align="center"><font face="arial" size=2>During the Month</font></td>
    <td width="10%"	align="center"><font face="arial" size=2>During the Year</font></td>
  </tr>
  <tr>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;1</font></td>
    <td width="75%"	><font face="arial" size="2">No.of M.S.S functioning</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[0]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[0]%></font>&nbsp;</td>
  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size=2>&nbsp;2</font></td>
    <td width="75%"	><font face="arial" size="2">No.of M.S.S discontinued</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[1]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[1]%></font>&nbsp;</td>
  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size="2">&nbsp;3</font></td>
    <td width="75%"	><font face="arial" size="2">No.of M.S.S continued</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[0]- monthlyDataValues[1]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[0]- yearlyDataValues[1]%></font>&nbsp;</td>
  </tr>
  <tr>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;4</font></td>
    <td width="75%"	><font face="arial" size="2">Total no.of M.S.S members old and new</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[2]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[2]%></font>&nbsp;</td>
  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size=2>&nbsp;5</font></td>
    <td width="75%"	><font face="arial" size="2">No.of M.S.S members meeting held</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[3]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[3]%></font>&nbsp;</td>
  </tr>
  <tr>
    <td width="5%"	align="center"><font face="arial" size=2>&nbsp;6</font></td>
    <td width="75%"	><font face="arial" size="2">No.of folders/Booklets/phamlets/posters distributed</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[4]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[4]%></font>&nbsp;</td>
  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size=2>&nbsp;7</font></td>
    <td width="75%"	><font face="arial" size=2>&nbsp;No.of depoholders established by M.S.S</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[5]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[5]%></font>&nbsp;</td>
  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size=2>&nbsp;8</font></td>
    <td width="75%"	><font face="arial" size="2">No.of other educational activities organised/group discussions etc</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[6]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[6]%></font>&nbsp;</td>
  </tr>
  <tr>
  <td width="5%"	align="center"><font face="arial" size=2>&nbsp;9</font></td>
    <td width="75%"	><font face="arial" size="2">Immunization Camp</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[7]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[7]%></font>&nbsp;</td>
  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size=2>&nbsp;10</font></td>
    <td width="75%"	><font face="arial" size="2">IUD Cases</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[8]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[8]%></font>&nbsp;</td>
  </tr>
  <tr>
  <td width="5%"	align="center"><font face="arial" size="2">&nbsp;11</font></td>
    <td width="75%"	><font face="arial" size="2">Sterilization Cases</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[9] + monthlyDataValues[10] + monthlyDataValues[11] + monthlyDataValues[12] + monthlyDataValues[13]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[9] + yearlyDataValues[10] + yearlyDataValues[11] + yearlyDataValues[12] + yearlyDataValues[13]%></font>&nbsp;</td>
  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size=2>&nbsp;12</font></td>
    <td width="75%"	><font face="arial" size="2">Antinatal Cases</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[14]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[14]%></font>&nbsp;</td>
  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size=2>&nbsp;13</font></td>
    <td width="75%"	><font face="arial" size="2">Distribution of iron folic aoid</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[15]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[15]%></font>&nbsp;</td>

  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size=2>&nbsp;14</font></td>
    <td width="75%"	><font face="arial" size="2">Media Activities</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[16]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[16]%></font>&nbsp;</td>
  </tr>
  <tr>
  <td width="5%"	align="center"><font face="arial" size=2>&nbsp;(a)</font></td>
    <td width="75%"	><font face="arial" size="2">Film Show</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[17]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[17]%></font>&nbsp;</td>

  </tr>
  <tr>
  <td width="5%"	align="center"><font face="arial" size=2>&nbsp;(b)</font></td>
    <td width="75%"	><font face="arial" size="2">Cinema</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[18]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[18]%></font>&nbsp;</td>
  </tr>
  <tr>
    <td width="5%"	align="center"><font face="arial" size="2">&nbsp;(c)</font></td>
    <td width="75%"	><font face="arial" size="2">Mini exhibition</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[19]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[19]%></font>&nbsp;</td>
  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size="2">&nbsp;(d)</font></td>
    <td width="75%"><font face="arial" size="2">Ward population unit seminars</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[20]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[20]%></font>&nbsp;</td>
  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size="2">&nbsp;(e)</font></td>
    <td width="75%"	><font face="arial" size="2">Video show</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[21]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[21]%></font>&nbsp;</td>
  </tr>
  <tr>
    <td width="5%"	align="center"><font face="arial" size="2">&nbsp;(f)</font></td>
    <td width="75%"	><font face="arial" size="2">Slide projection done</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[22]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[22]%></font>&nbsp;</td>
  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size="2">&nbsp;(g)</font></td>
    <td width="75%"	><font face="arial" size="2">Group discussion</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[23]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[23]%></font>&nbsp;</td>
  </tr>
  <tr>
    <td width="5%"	align="center"><font face="arial" size="2">&nbsp;(h)</font></td>
    <td width="75%"><font face="arial" size="2">Public meeting</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[24]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[24]%></font>&nbsp;</td>
  </tr>
  <tr>
   <td width="5%"	align="center"><font face="arial" size=2>&nbsp;(i)</font></td>
    <td width="75%"><font face="arial" size="2">Any fund Alloted</font></td>
    <td width="10%"	align="center"><font face="arial" size=2><%=monthlyDataValues[25]%></font>&nbsp;</td>
    <td width="10%"	align="center"><font face="arial" size=2><%=yearlyDataValues[25]%></font>&nbsp;</td>
  </tr>
</table>
</body>

</html>