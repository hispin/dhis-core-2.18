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
    String urlForConnection = "jdbc:mysql://localhost/mp_dhis2";
          

	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = Integer.parseInt( selectedId );
//	int selectedOrgUnitID = 310;
	

  	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );

//  	String startingDate  = "2007-03-01";
//	String endingDate  = "2007-03-01";
      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = Integer.parseInt( monthlyPeriodId );
//int periodTypeID = 1;
      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	  	  	
  	String monthlyDataElements[] = {
  									
											// TT - 1,2,Booster 
											"Form6_DE5","Form6_DE6","Form6_DE7",
											
											// BCG Under 1 Year - Male, Female
											"Form6_DE44","Form6_DE45",
											
											// OPV Under 1 Year - 0,1,2,3 Doses Male, Female
											"Form6_DE52","Form6_DE53", "Form6_DE54","Form6_DE55", "Form6_DE56","Form6_DE57", "Form6_DE58","Form6_DE59",
											
											// DPT Under 1 Year - 1,2,3 Doses Male, Female
											"Form6_DE46","Form6_DE47", "Form6_DE48","Form6_DE49", "Form6_DE50","Form6_DE51",
											
											// Measles Under 1 Year - Male, Female
											"Form6_DE60","Form6_DE61",
											
											// Vitamin A Under 1 Year - Male,Female
											" "," ",
											
											// OPV Booster Over 1 Year - Male, Female
											"Form6_DE66","Form6_DE67",
											
											// DPT Booster Over 1 Year - Male, Female
											"Form6_DE64","Form6_DE65",
											
											// Vitamin A  Over 1 Year - 2,(3-5) Male, Female
											"Form6_DE80","Form6_DE81", "Form6_DE82","Form6_DE3",
											
											// DT - 5 Year Male, Female
											"Form6_DE70","Form6_DE71",
											
											// TT - 10 Year Male, Female
											"Form6_DE72","Form6_DE73",
											
											// TT - 16 Year Male, Female 
											"Form6_DE74","Form6_DE75"
																																																																																
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
        	
        	//rs1 = st1.executeQuery("select shortname from organisationunit  where id = "+selectedOrgUnitID);
        	rs1 = st1.executeQuery("select shortname from organisationunit  where organisationunitid = "+selectedOrgUnitID);        
        	if(rs1.next())        {  selectedOrgUnitName = rs1.getString(1);      }
                
//        	rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	   		if(rs2.next()) 	  {  selectedDataPeriodStartDate =  rs2.getDate(1).toString();	} 		  

			selectedDataPeriodStartDate = startingDate;

	   		   			
			//rs7=st7.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
			rs7=st7.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");	
			if(rs7.next())  { DistrictID = rs7.getInt(1);DistrictName = rs7.getString(2);  } 
			else  {  DistrictID = 0; DistrictName = "";  } 

			//rs8=st8.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+DistrictID+")");
			rs8=st8.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+DistrictID+")");	
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
				rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"')  AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+") AND dataelement.code like '"+monthlyDataElements[i]+"'");       			   		
        		if(rs3.next()) 		{  monthlyValues[i] =  rs3.getInt(1);	}  
		  			
				rs4 = st4.executeQuery(" select sum(value) from datavalue where dataelementid in (select dataelementid from dataelement where code like '"+monthlyDataElements[i]+"') and sourceid in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+") and periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodtypeid = "+periodTypeID+")");        			        	
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
<title>UNIVERSAL IMMUNIZATION PROGRAMME</title>
</head>
<body>
	<br>	      
	<center>
		<font face="Arial" size="2"><b>UNIVERSAL IMMUNIZATION PROGRAMME</font>
		<font face="Arial" size="1"><BR>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MONTHLY 
        PHC/SECTOR PERFORMANCE REPORT</B></font>
	</center>
	
		<font face="Arial" size="1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		MONTH : &nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%></font>
	<BR>
	<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  		<tr>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       			<font face="Arial" size="1">PHC/Sector : &nbsp;&nbsp;<%=selectedOrgUnitName%></font>
    		</td>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       			<font face="Arial" size="1">CHC : &nbsp;&nbsp;<%=DistrictName%></font>
    		</td>
  		</tr>
  		<tr>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       			<font face="Arial" size="1">Year target: Infants</font>
    		</td>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
    			<font face="Arial" size="1">Pregnant Women</font>
    		</td>
  		</tr>
  		<tr>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       			<font face="Arial" size="1">Number of Sessions: Planned</font>
    		</td>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"   height="23">
    			<font face="Arial" size="1">Actually held</font>
    		</td>
  		</tr>
  		<tr>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"   height="19">
       			<font face="Arial" size="1"></font>
    		</td>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"   height="19"></td>
  		</tr> 
  		<tr>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"   height="19">
       			<font face="Arial" size="1">Number of Sessions where vaccines received at site</font>
    		</td>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"   height="19"></td>
  		</tr> 
  		<tr>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"   height="1" colspan="2">
            	<font face="Arial" size="1">Number of Volunteers/ Sahiyya engaged to mobilise children</font>
	       	</td>
      		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"   height="19"> </td>
  		</tr>
  </table>  
  
  <br>
  <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
      <tr>
        <td width="40%" align="center" rowspan="2"><font face="Arial" size="1">Number of Sessions for which private vaccinators hired</font></td>
        <td width="15%" align="center"><font face="Arial" size="1">ANM absent</font></td>
        <td width="15%" align="center"><font face="Arial" size="1">Underserved areas</font></td>
        <td width="15%" align="center"><font face="Arial" size="1">Urban slums</font></td>
        <td width="15%" align="center"><font face="Arial" size="1">Total</font></td>
      </tr>
      <tr>
        <td width="15%" align="center"><font face="Arial" size="2">&nbsp;</font></td>
        <td width="15%" align="center"><font face="Arial" size="2"></font></td>
        <td width="15%" align="center"><font face="Arial" size="2"></font></td>
        <td width="15%" align="center"><font face="Arial" size="2"></font></td>
      </tr>
  </table>
  
 <br>
  <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
      <tr>
        <td width="40%" align="center" rowspan="2"><font face="Arial" size="1">Number of Supervisory visits undertaken by DIO</font></td>
        <td width="15%" align="center"><font face="Arial" size="1">Block PHC Level</font></td>
        <td width="15%" align="center"><font face="Arial" size="1">PHC Level</font></td>
        <td width="15%" align="center"><font face="Arial" size="1">Sub Centre</font></td>
        <td width="15%" align="center"><font face="Arial" size="1">Session Site</font></td>
      </tr>
      <tr>
        <td width="15%" align="center"><font face="Arial" size="2">&nbsp;</font></td>
        <td width="15%" align="center"><font face="Arial" size="2"></font></td>
        <td width="15%" align="center"><font face="Arial" size="2"></font></td>
        <td width="15%" align="center"><font face="Arial" size="2"></font></td>
      </tr>
  </table>
  
  <br>
  <font face="Arial" size="1"><b>(A) IMMUNISATION AND VIT. A.</b></font>
  <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
      <tr>
        <td width="10%" align="center" rowspan="4"><font face="Arial" size="1">PREGNANT WOMEN</font></td>
        <td width="16%" align="center" rowspan="4"><font face="Arial" size="1">TERANUS TOXIOD (TT)</font></td>
        <td width="10%" align="center"><font face="Arial" size="1">Doses</font></td>
        <td width="32%" align="center" colspan="4"><font face="Arial" size="1">For the Month</font></td>
        <td width="32%" align="center" colspan="4"><font face="Arial" size="1">Cumulative</font></td>
      </tr>
      <tr>
        <td width="10%" align="center"><font face="Arial" size="1">1</font></td>
        <td width="32%" align="center" colspan="4"><font face="Arial" size="1"><%=monthlyValues[0]%></font>&nbsp;</td>
        <td width="32%" align="center" colspan="4"><font face="Arial" size="1"><%=cumulativeValues[0]%></font>&nbsp;</td>
      </tr>
      <tr>
        <td width="10%" align="center"><font face="Arial" size="1">2</font></td>
        <td width="32%" align="center" colspan="4"><font face="Arial" size="1"><%=monthlyValues[1]%></font>&nbsp;</td>
        <td width="32%" align="center" colspan="4"><font face="Arial" size="1"><%=cumulativeValues[1]%></font>&nbsp;</td>
      </tr>
      <tr>
        <td width="10%" align="center"><font face="Arial" size="1">3</font></td>
        <td width="32%" align="center" colspan="4"><font face="Arial" size="1"><%=monthlyValues[2]%></font>&nbsp;</td>
        <td width="32%" align="center" colspan="4"><font face="Arial" size="1"><%=cumulativeValues[2]%></font>&nbsp;</td>
      </tr>
      <tr>
        <td width="10%" align="center" rowspan="24"><font face="Arial" size="1">C<br><br>H<br><br>I<br><br>L<br><br>D<br><br>R<br><br>E<br><br>N</font></td>
        <td width="15%" align="center" rowspan="3"><font face="Arial" size="1">Vaccines</font></td>
        <td width="11%" align="center" rowspan="3"><font face="Arial" size="1">Doses</font></td>
        <td width="32%" align="center" colspan="4"><font face="Arial" size="1">During the month</font></td>
        <td width="32%" align="center" colspan="4"><font face="Arial" size="1">Cumulative</font></td>
      </tr>
      <tr>
        <td width="16%" align="center" colspan="2"><font face="Arial" size="1">Under 1 year</font></td>
        <td width="16%" align="center" colspan="2"><font face="Arial" size="1">Over 1 year</font></td>
        <td width="16%" align="center" colspan="2"><font face="Arial" size="1">Under 1 year</font></td>
        <td width="16%" align="center" colspan="2"><font face="Arial" size="1">Over 1 year</font></td>
      </tr>
      <tr>
        <td width="8%" align="center"><font face="Arial" size="1">Male</font></td>
        <td width="8%" align="center"><font face="Arial" size="1">Female</font></td>
        <td width="8%" align="center"><font face="Arial" size="1">Male</font></td>
        <td width="8%" align="center"><font face="Arial" size="1">Female</font></td>
        <td width="8%" align="center"><font face="Arial" size="1">Male</font></td>
        <td width="8%" align="center"><font face="Arial" size="1">Female</font></td>
        <td width="8%" align="center"><font face="Arial" size="1">Male</font></td>
        <td width="8%" align="center"><font face="Arial" size="1">Female</font></td>
      </tr>
      <tr>
        <td width="15%" align="center" ><font face="Arial" size="1">BCG</font></td>
        <td width="11%" align="center" ><font face="Arial" size="1">1</font></td>
        <td width="8%"  align="center" ><font face="Arial" size="1"><%=monthlyValues[3]%></font>&nbsp;</td>
        <td width="8%"  align="center" ><font face="Arial" size="1"><%=monthlyValues[4]%></font>&nbsp;</td>
        <td width="8%"  align="center" >&nbsp;</td>
        <td width="8%"  align="center" >&nbsp;</td>
        <td width="8%"  align="center" ><font face="Arial" size="1"><%=cumulativeValues[3]%></font>&nbsp;</td>
        <td width="8%"  align="center" ><font face="Arial" size="1"><%=cumulativeValues[4]%></font>&nbsp;</td>
        <td width="8%"  align="center" >&nbsp;</td>
        <td width="8%"  align="center" >&nbsp;</td>
      </tr>
      <tr>
        <td width="15%" align="center" rowspan="4"><font face="Arial" size="1">OPV</font></td>
        <td width="11%" align="center" ><font face="Arial" size="1">0 dose</font></td>
        <td width="8%"  align="center" ><font face="Arial" size="1"><%=monthlyValues[5]%></font>&nbsp;</td>
        <td width="8%"  align="center" ><font face="Arial" size="1"><%=monthlyValues[6]%></font>&nbsp;</td>
        <td width="8%"  align="center" >&nbsp;</td>
        <td width="8%"  align="center" >&nbsp;</td>
        <td width="8%"  align="center" ><font face="Arial" size="1"><%=cumulativeValues[5]%></font>&nbsp;</td>
        <td width="8%"  align="center" ><font face="Arial" size="1"><%=cumulativeValues[6]%></font>&nbsp;</td>
        <td width="8%"  align="center" >&nbsp;</td>
        <td width="8%"  align="center" >&nbsp;</td>
      </tr>
      <tr>
        <td width="11%"  align="center" ><font face="Arial" size="1">1</font></td>
        <td width="8%"  align="center" ><font face="Arial" size="1"><%=monthlyValues[7]%></font>&nbsp;</td>
        <td width="8%"  align="center" ><font face="Arial" size="1"><%=monthlyValues[8]%></font>&nbsp;</td>
        <td width="8%"  align="center" >&nbsp;</td>
        <td width="8%"  align="center" >&nbsp;</td>
        <td width="8%"  align="center" ><font face="Arial" size="1"><%=cumulativeValues[7]%></font>&nbsp;</td>
        <td width="8%"  align="center" ><font face="Arial" size="1"><%=cumulativeValues[8]%></font>&nbsp;</td>
        <td width="8%"  align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
      </tr>
      <tr>
        <td width="11%" align="center" ><font face="Arial" size="1">2</font></td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[9]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[10]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[9]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[10]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
      </tr>
      <tr>
        <td width="11%" align="center" ><font face="Arial" size="1">3</font></td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[11]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[12]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[11]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[12]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
      </tr>
      <tr>
        <td width="15%"  align="center" rowspan="3"><font face="Arial" size="1">DPT</font></td>
        <td width="11%" align="center" ><font face="Arial" size="1">1</font></td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[13]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[14]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[13]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[14]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
      </tr>
      <tr>
        <td width="11%" align="center" ><font face="Arial" size="1">2</font></td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[15]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[16]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[15]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[16]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
      </tr>
      <tr>
        <td width="11%" align="center" ><font face="Arial" size="1">3</font></td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[17]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[18]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[17]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[18]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
      </tr>
      <tr>
        <td width="15%"  align="center" rowspan="3"><font face="Arial" size="1">Hepatitis B (where introduced)</font></td>
        <td width="11%" align="center" ><font face="Arial" size="1">1</font></td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
      </tr>
      <tr>
        <td width="11%" align="center" ><font face="Arial" size="1">2</font></td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
      </tr>
      <tr>
        <td width="11%" align="center" ><font face="Arial" size="1">3</font></td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
      </tr>
      <tr>
        <td width="15%" align="center" ><font face="Arial" size="1">Measles</font></td>
        <td width="11%" align="center" ><font face="Arial" size="1">1</font></td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[19]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[20]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[19]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[20]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
      </tr>
      <tr>
        <td width="15%" align="center" ><font face="Arial" size="1">Vitamin A</font></td>
        <td width="11%" align="center" ><font face="Arial" size="1">1</font></td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[21]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[22]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[21]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[22]%></font>&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
      </tr>
      <tr>
        <td width="15%" align="center" ><font face="Arial" size="1">OPV Booster</font></td>
        <td width="11%" align="center" >&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[23]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[24]%></font>&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[23]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[24]%></font>&nbsp;</td>
      </tr>
      <tr>
        <td width="15%" align="center" ><font face="Arial" size="1">DPT Booster</font></td>
        <td width="11%" align="center" >&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[25]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[26]%></font>&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[25]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[26]%></font>&nbsp;</td>
      </tr>
      <tr>
        <td width="15%"  align="center" rowspan="3"><font face="Arial" size="1">Vitamin A</font></td>
        <td width="11%" align="center" ><font face="Arial" size="1">2</font></td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[27]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[28]%></font>&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[27]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[28]%></font>&nbsp;</td>
      </tr>
      <tr>
        <td width="11%" align="center" ><font face="Arial" size="1">3</font></td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[29]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[30]%></font>&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[29]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[30]%></font>&nbsp;</td>
      </tr>
      <tr>
        <td width="11%" align="center" ><font face="Arial" size="1">4</font></td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"></font></td>
        <td width="8%" align="center" ><font face="Arial" size="1"></font></td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
        <td width="8%" align="center" >&nbsp;</td>
      </tr>
      <tr>
        <td width="15%" align="center" ><font face="Arial" size="1">DT ( 5 YEAR)</font></td>
        <td width="11%" align="center" ><font face="Arial" size="1">1</font></td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[31]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[32]%></font>&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[31]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[32]%></font>&nbsp;</td>
      </tr>
      <tr>
        <td width="15%" align="center" ><font face="Arial" size="1">TT (10 YEAR)</font></td>
        <td width="11%" align="center" ><font face="Arial" size="1">1</font></td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[33]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[34]%></font>&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[33]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[34]%></font>&nbsp;</td>
      </tr>
      <tr>
        <td width="15%" align="center" ><font face="Arial" size="1">TT (16 YEAR)</font></td>
        <td width="11%" align="center" ><font face="Arial" size="1">1</font></td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[35]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=monthlyValues[36]%></font>&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%"  align="center" bgcolor="#000000">&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[35]%></font>&nbsp;</td>
        <td width="8%" align="center" ><font face="Arial" size="1"><%=cumulativeValues[36]%></font>&nbsp;</td>
      </tr>
    </table>
	
	<br>
  	<font face="Arial" size="1"><b>(B) VACCINE SUPPLY (IN DOSES)</b></font>
    <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
      <tr>
        <td width="15%" align="center" ><font face="Arial" size="1">Vaccine</font></td>
        <td width="20%" align="center" ><font face="Arial" size="1">Opening Balance</font></td>
        <td width="21%" align="center" ><font face="Arial" size="1">Received during the month</font></td>
        <td width="21%" align="center" ><font face="Arial" size="1">Consumed during the month</font></td>
        <td width="23%" align="center" ><font face="Arial" size="1">Balance at the end of the month</font></td>
      </tr>
      <tr>
        <td width="15%"><font face="Arial" size="1">DPT</font></td>
        <td width="20%">&nbsp;</td>
        <td width="21%">&nbsp;</td>
        <td width="21%">&nbsp;</td>
        <td width="23%">&nbsp;</td>
      </tr>
      <tr>
        <td width="15%"><font face="Arial" size="1">OPV</font></td>
        <td width="20%">&nbsp;</td>
        <td width="21%">&nbsp;</td>
        <td width="21%">&nbsp;</td>
        <td width="23%">&nbsp;</td>
      </tr>
      <tr>
        <td width="15%"><font face="Arial" size="1">BCG</font></td>
        <td width="20%">&nbsp;</td>
        <td width="21%">&nbsp;</td>
        <td width="21%">&nbsp;</td>
        <td width="23%">&nbsp;</td>
      </tr>
      <tr>
        <td width="15%"><font face="Arial" size="1">MEASLES</font></td>
        <td width="20%">&nbsp;</td>
        <td width="21%">&nbsp;</td>
        <td width="21%">&nbsp;</td>
        <td width="23%">&nbsp;</td>
      </tr>
      <tr>
        <td width="15%"><font face="Arial" size="1">TT</font></td>
        <td width="20%">&nbsp;</td>
        <td width="21%">&nbsp;</td>
        <td width="21%">&nbsp;</td>
        <td width="23%">&nbsp;</td>
      </tr>
      <tr>
        <td width="15%"><font face="Arial" size="1">DT</font></td>
        <td width="20%">&nbsp;</td>
        <td width="21%">&nbsp;</td>
        <td width="21%">&nbsp;</td>
        <td width="23%">&nbsp;</td>
      </tr>
    </table>
</body>
</html>