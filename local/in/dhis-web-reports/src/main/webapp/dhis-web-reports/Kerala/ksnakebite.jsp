
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
      
      //for snake bite cases during the month
      Statement st3 = null;
      ResultSet rs3 = null;

      //for snake bite deaths during the month
      Statement st4 = null;
      ResultSet rs4 = null;
      
      //for snake bite cases during the year
      Statement st5 = null;
      ResultSet rs5 = null;

      //for snake bite deaths during the year
      Statement st6 = null;
      ResultSet rs6 = null;

      String userName = "dhis";           
      String password = "";           
      String urlForConnection = "jdbc:mysql://localhost/kl_dhis2";
          
	  OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  int selectedOrgUnitID = 	  Integer.parseInt( selectedId );
	

	  String startingDate  =   (String) stack.findValue( "startingPeriod" );
	  String endingDate  =   (String) stack.findValue( "endingPeriod" );

      
	  String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	  int periodTypeID = 	  Integer.parseInt( monthlyPeriodId );
	       
      
	  String selectedOrgUnitName = "";
	  String selectedDataPeriodStartDate = "";
      String selectedDataPeriodEndDate = "";
      
      String monthlyCases = "NIL";
      String yearlyCases = "NIL";
      String monthlyDeaths = "NIL";
      String yearlyDeaths = "NIL";                 

	String monthlyDataElements[] = {
  									  // Snake Bite Cases  									  
  									  "'Form6_DE'",
  									  
  									  // Snake Bite Deaths
  									  "'Form6_DE'",
  									  
									};


  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String query = "";
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
		
      } // try block end
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
		//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in ( select id from organisationunit where parent = "+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[0]+")";
		query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in ( select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[0]+")";
		rs3=st3.executeQuery(query);
		//"SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id) ON organisationunit.id = datavalue.source WHERE ((dataelement.name like 'No of cases with Snake Bite') AND ((organisationunit.parent)="+selectedOrgUnitID+") AND ((datavalue.period)="+selectedDataPeriodID+"))");
	    if(rs3.next())  { monthlyCases = ""+rs3.getInt(1);   } 

		//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in ( select id from organisationunit where parent = "+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[1]+")";
		query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in ( select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[1]+")";
		rs4=st4.executeQuery(query);
		//"SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id) ON organisationunit.id = datavalue.source WHERE ((dataelement.name like 'No of deaths with Snake Bite') AND ((organisationunit.parent)="+selectedOrgUnitID+") AND ((datavalue.period)="+selectedDataPeriodID+"))");
	    if(rs4.next())  { monthlyDeaths = ""+rs4.getInt(1);   } 



		//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.source in (select id from organisationunit where parent ="+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[0]+")";
		query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[0]+")";						 	 	 
		rs5=st5.executeQuery(query);
		//"SELECT sum(value) FROM datavalue WHERE dataElement in (select id from dataelement where name like 'No of cases with Snake Bite') and source in (select id from organisationunit where parent ="+selectedOrgUnitID+")  and period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+")");
	    if(rs5.next())  { yearlyCases = ""+rs5.getInt(1);   } 
  
		//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.source in (select id from organisationunit where parent ="+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[1]+")";
		query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[1]+")";						 	 	   
 		rs6=st6.executeQuery(query);
 		//"SELECT sum(value) FROM datavalue WHERE dataElement in (select id from dataelement where name like 'No of deaths with Snake Bite') and source in (select id from organisationunit where parent ="+selectedOrgUnitID+")  and period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+")");
	    if(rs6.next())  { yearlyDeaths = ""+rs6.getInt(1);   } 

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
					
					if(con!=null) con.close();
				}
			catch(Exception e)   {  out.println(e.getMessage());   }
       } // finally block end		  	        
%>

<HTML>
<HEAD>
   <TITLE> Report of SnakeBite Cases </TITLE>
</HEAD>
<BODY BGCOLOR="#FFFFFF" >
	<center>		
		<font face="Arial" size="3">
			Report of SnakeBite Cases of P.H.C &nbsp;&nbsp;<b><u><%=selectedOrgUnitName%></u></b><br>
			for the month &nbsp;&nbsp;<b><u><%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%></u></b> 
		</font>
	</center>
	<br><br>	
	<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
          <tr>
            <td width="50%" height=30 align="center"><font face="Arial" size=2>Snake Bite</font></td>
            <td width="25%" height=30 align="center"><font face="Arial" size=2>During the Month</font></td>
            <td width="25%" height=30 align="center"><font face="Arial" size=2>During the Year</font></td>
          </tr>
          <tr>
            <td width="50%" height=30 align="center"><font face="Arial" size=2>Cases</font></td>
            <td width="25%" height=30 align="center"><font face="Arial" size=2><%=monthlyCases%></font></td>
            <td width="25%" height=30 align="center"><font face="Arial" size=2><%=yearlyCases%></font></td>
          </tr>
          <tr>
            <td width="50%" height=30 align="center"><font face="Arial" size=2>Deaths</font></td>
            <td width="25%" height=30 align="center"><font face="Arial" size=2><%=monthlyDeaths%></font></td>
            <td width="25%" height=30 align="center"><font face="Arial" size=2><%=yearlyDeaths%></font></td>
          </tr>
    </table>
</BODY>
</HTML>