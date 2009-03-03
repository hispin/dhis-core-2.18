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
	

	  String startingDate  =  (String) stack.findValue( "startingPeriod" );
	  String endingDate  =   (String) stack.findValue( "endingPeriod" );


      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	  	  	
  	String monthlyDataElements[] = {
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'",
										"'Some_DE'"
										  									
		
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
				//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in ( select id from organisationunit where parent = "+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[i]+")";
				query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in ( select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+")  AND dataelement.code in ("+monthlyDataElements[i]+")";

				rs3 = st3.executeQuery(query);
				// "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id INNER JOIN period ON datavalue.period = period.id INNER JOIN organisationunit ON datavalue.source = organisationunit.parent WHERE dataelement.name like '"+monthlyDataElements[i]+"' AND organisationunit.parent="+selectedOrgUnitID+" AND period.id="+selectedDataPeriodID);        			   		
       			if(rs3.next()) 		{  monthlyValues[i] =  rs3.getInt(1);	}  		  							  
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
				
				if(con!=null)  con.close(); 
			}
			catch(Exception e)   {  out.println(e.getMessage());   }
       } // finally block end	
       
  %>

<html>
<head>
	<title>VECTOR BORN DISEASES AND COMMUNICABLE DISEASES</title>
</head>
<body>			             
	<p align="center"><font face="Arial" size="3"><b>VECTOR BORN DISEASES AND COMMUNICABLE DISEASES&nbsp;&nbsp;<U><%=selectedOrgUnitName%></U>  </b></font></p>
	<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
	  	<tr>
    			<td width="24%" height="25" rowspan="2" ALIGN="CENTER"><font face="Arial" size="2">INSTITUTION</FONT></td>
    			<td width="8%" height="13" colspan="2" ALIGN="CENTER"><font face="Arial" size="2">DENGUE</FONT></td>
    			<td width="8%" height="13" colspan="2" ALIGN="CENTER"><font face="Arial" size="2">LAPTO</FONT></td>
    			<td width="8%" height="13" colspan="2" ALIGN="CENTER"><font face="Arial" size="2">MALARIA</FONT></td>
    			<td width="8%" height="13" colspan="2" ALIGN="CENTER"><font face="Arial" size="2">DIARRHOEAL</FONT></td>
    			<td width="8%" height="13" colspan="2" ALIGN="CENTER"><font face="Arial" size="2">DYSENTRY</FONT></td>
    			<td width="8%" height="13" colspan="2" ALIGN="CENTER"><font face="Arial" size="2">MEASLES</FONT></td>
    			<td width="8%" height="13" colspan="2" ALIGN="CENTER"><font face="Arial" size="2">MUMPS</FONT></td>
    			<td width="8%" height="13" colspan="2" ALIGN="CENTER"><font face="Arial" size="2">CHICKEN POX</FONT></td>
    			<td width="12%" height="13" colspan="3" ALIGN="CENTER"><font face="Arial" size="2">IEC ACTIVITIES</FONT></td>
  		</tr>
  		<tr>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">M<BR>O<BR>N<BR>T<BR>H<BR>L<BR>Y</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">P<BR>R<BR>O<BR>G<BR>R<BR>E<BR>S<BR>S<BR>I<BR>V<BR>E</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">M<BR>O<BR>N<BR>T<BR>H<BR>L<BR>Y</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">P<BR>R<BR>O<BR>G<BR>R<BR>E<BR>S<BR>S<BR>I<BR>V<BR>E</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">M<BR>O<BR>N<BR>T<BR>H<BR>L<BR>Y</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">P<BR>R<BR>O<BR>G<BR>R<BR>E<BR>S<BR>S<BR>I<BR>V<BR>E</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">M<BR>O<BR>N<BR>T<BR>H<BR>L<BR>Y</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">P<BR>R<BR>O<BR>G<BR>R<BR>E<BR>S<BR>S<BR>I<BR>V<BR>E</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">M<BR>O<BR>N<BR>T<BR>H<BR>L<BR>Y</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">P<BR>R<BR>O<BR>G<BR>R<BR>E<BR>S<BR>S<BR>I<BR>V<BR>E</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">M<BR>O<BR>N<BR>T<BR>H<BR>L<BR>Y</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">P<BR>R<BR>O<BR>G<BR>R<BR>E<BR>S<BR>S<BR>I<BR>V<BR>E</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">M<BR>O<BR>N<BR>T<BR>H<BR>L<BR>Y</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">P<BR>R<BR>O<BR>G<BR>R<BR>E<BR>S<BR>S<BR>I<BR>V<BR>E</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">M<BR>O<BR>N<BR>T<BR>H<BR>L<BR>Y</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">P<BR>R<BR>O<BR>G<BR>R<BR>E<BR>S<BR>S<BR>I<BR>V<BR>E</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">G<BR>R<BR>O<BR>U<BR>P<BR> <BR>T<BR>A<BR>L<BR>K</FONT></td>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">W<BR>E<BR>L<BR>L<BR> <BR>C<BR>H<BR>L<BR>O<BR>R<BR>I<BR>O<BR>S<BR>A<BR>T<BR>I<BR>O<BR>N</FONT></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">O<BR>R<BR>S<BR> <BR>I<BR>S<BR>S<BR>U<BR>E<BR>D</FONT></td>
  		</tr>
  		<tr>
    			<td width="24%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=selectedOrgUnitName%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[0]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[1]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[2]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[3]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[4]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[5]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[6]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[7]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[8]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[9]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[10]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[11]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[12]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[13]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[14]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[15]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[16]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[17]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[18]%></FONT></td>
  		</tr>
  		</table>			             

<BR>
	<font face="Arial" size="3"><b>NVBDCP</b></FONT>
<BR>
	<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="40%">
	  	<tr>
    			<td width="20%" height="13" colspan="4" ALIGN="CENTER"><font face="Arial" size="2">ACTIVE</font></td>
    			<td width="20%" height="13" colspan="4" ALIGN="CENTER"><font face="Arial" size="2">PASSIVE</FONT></td>
  		</tr>
  		<tr>
    			<td width="5%" ALIGN="CENTER"><font face="Arial" size="2">T<BR>A<BR>R<BR>G<BR>E<BR>T</font></td>
    			<td width="5%" ALIGN="CENTER"><font face="Arial" size="2">M<BR>O<BR>N<BR>T<BR>H<BR>L<BR>Y</font></td>
    			<td width="5%" ALIGN="CENTER"><font face="Arial" size="2">P<BR>R<BR>O<BR>G<BR>R<BR>E<BR>S<BR>S<BR>I<BR>V<BR>E</font></td>
    			<td width="5%" ALIGN="CENTER"><font face="Arial" size="2">%</font></td>
    			<td width="5%" ALIGN="CENTER"><font face="Arial" size="2">T<BR>A<BR>R<BR>G<BR>E<BR>T</font></td>
    			<td width="5%" ALIGN="CENTER"><font face="Arial" size="2">M<BR>O<BR>N<BR>T<BR>H<BR>L<BR>Y</font></td>
    			<td width="5%" ALIGN="CENTER"><font face="Arial" size="2">P<BR>R<BR>O<BR>G<BR>R<BR>E<BR>S<BR>S<BR>I<BR>V<BR>E</font></td>
    			<td width="5%" ALIGN="CENTER"><font face="Arial" size="2">%</font></td>
  		</tr>
  		<tr>
    			<td width="5%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[19]%></FONT></td>
    			<td width="5%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[20]%></font></td>
    			<td width="5%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[21]%></font></td>
    			<td width="5%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[22]%></font></td>
    			<td width="5%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[23]%></FONT></td>
    			<td width="5%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[24]%></font></td>
    			<td width="5%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[25]%></font></td>
    			<td width="5%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[26]%></font></td>
  		</tr>
  	</table>		
  	
  	<br>
  	<p align="center"><font face="Arial" size="3"><b>RNTCP REPORT OF&nbsp;&nbsp;<U><%=selectedOrgUnitName%></U> </b></font></p>
	<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
	  	<tr>
    			<td width="24%" height="25" rowspan="2" ALIGN="CENTER"><font face="Arial" size="2">INSTITUTION</FONT></td>
    			<td width="24%" height="13" colspan="6" ALIGN="CENTER"><font face="Arial" size="2">CATEGORY-1</FONT></td>
    			<td width="24%" height="13" colspan="6" ALIGN="CENTER"><font face="Arial" size="2">CATEGORY-2</FONT></td>
    			<td width="28%" height="13" colspan="7" ALIGN="CENTER"><font face="Arial" size="2">CATEGORY-3</font></td>
  		</tr>
  		<tr>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">C &nbsp;&nbsp;<BR>A L<BR>S A<BR>E S<BR>S T<BR>&nbsp;&nbsp;&nbsp;<BR>U M<BR>P O<BR>T N<BR>O T<BR>&nbsp;&nbsp;&nbsp;&nbsp;H</font></td>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">&nbsp;&nbsp;&nbsp;R<BR>N E<BR>E G<BR>W I<BR>&nbsp;&nbsp;&nbsp;S<BR>C T<BR>A E<BR>S R<BR>E E<BR>&nbsp;&nbsp;&nbsp;D</font></td>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">&nbsp;&nbsp;&nbsp;C&nbsp;&nbsp;&nbsp;<BR>T O&nbsp;&nbsp;&nbsp;<BR>R M C<BR>E P U<BR>A L R<BR>T E E<BR>M T D<BR>E E&nbsp;&nbsp;&nbsp;<BR>N D&nbsp;&nbsp;&nbsp;<BR>T&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<BR>&nbsp;&nbsp;&nbsp;&&nbsp;&nbsp;&nbsp;</font></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">D<BR>E<BR>F<BR>A<BR>U<BR>L<BR>T</font></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">C<BR>A<BR>S<BR>E<BR>S<BR> <BR>D<BR>E<BR>A<BR>T<BR>H</font></td>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">&nbsp;&nbsp;&nbsp;U<BR>&nbsp;&nbsp;&nbsp;N<BR>T D<BR>O E<BR>T R<BR>A&nbsp;&nbsp;&nbsp;<BR>L T<BR>&nbsp;&nbsp;&nbsp;R<BR>C E<BR>A A<BR>S T<BR>E M<BR>S E<BR>&nbsp;&nbsp;&nbsp;N<BR>&nbsp;&nbsp;&nbsp;T</font></td>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">C &nbsp;&nbsp;<BR>A L<BR>S A<BR>E S<BR>S T<BR>&nbsp;&nbsp;&nbsp;<BR>U M<BR>P O<BR>T N<BR>O T<BR>&nbsp;&nbsp;&nbsp;&nbsp;H</font></td>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">&nbsp;&nbsp;&nbsp;R<BR>N E<BR>E G<BR>W I<BR>&nbsp;&nbsp;&nbsp;S<BR>C T<BR>A E<BR>S R<BR>E E<BR>&nbsp;&nbsp;&nbsp;D</font></td>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">&nbsp;&nbsp;&nbsp;C&nbsp;&nbsp;&nbsp;<BR>T O&nbsp;&nbsp;&nbsp;<BR>R M C<BR>E P U<BR>A L R<BR>T E E<BR>M T D<BR>E E&nbsp;&nbsp;&nbsp;<BR>N D&nbsp;&nbsp;&nbsp;<BR>T&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<BR>&nbsp;&nbsp;&nbsp;&&nbsp;&nbsp;&nbsp;</font></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">D<BR>E<BR>F<BR>A<BR>U<BR>L<BR>T</font></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">C<BR>A<BR>S<BR>E<BR>S<BR> <BR>D<BR>E<BR>A<BR>T<BR>H</font></td>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">&nbsp;&nbsp;&nbsp;U<BR>&nbsp;&nbsp;&nbsp;N<BR>T D<BR>O E<BR>T R<BR>A&nbsp;&nbsp;&nbsp;<BR>L T<BR>&nbsp;&nbsp;&nbsp;R<BR>C E<BR>A A<BR>S T<BR>E M<BR>S E<BR>&nbsp;&nbsp;&nbsp;N<BR>&nbsp;&nbsp;&nbsp;T</font></td>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">C &nbsp;&nbsp;<BR>A L<BR>S A<BR>E S<BR>S T<BR>&nbsp;&nbsp;&nbsp;<BR>U M<BR>P O<BR>T N<BR>O T<BR>&nbsp;&nbsp;&nbsp;&nbsp;H</font></td>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">&nbsp;&nbsp;&nbsp;R<BR>N E<BR>E G<BR>W I<BR>&nbsp;&nbsp;&nbsp;S<BR>C T<BR>A E<BR>S R<BR>E E<BR>&nbsp;&nbsp;&nbsp;D</font></td>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">&nbsp;&nbsp;&nbsp;C&nbsp;&nbsp;&nbsp;<BR>T O&nbsp;&nbsp;&nbsp;<BR>R M C<BR>E P U<BR>A L R<BR>T E E<BR>M T D<BR>E E&nbsp;&nbsp;&nbsp;<BR>N D&nbsp;&nbsp;&nbsp;<BR>T&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<BR>&nbsp;&nbsp;&nbsp;&&nbsp;&nbsp;&nbsp;</font></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">D<BR>E<BR>F<BR>A<BR>U<BR>L<BR>T</font></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">C<BR>A<BR>S<BR>E<BR>S<BR> <BR>D<BR>E<BR>A<BR>T<BR>H</font></td>
    			<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">M<BR>D<BR>R<BR> <BR>T<BR>B</font></td>
    				<td width="4%" ALIGN="CENTER"><font face="Arial" size="1">&nbsp;&nbsp;&nbsp;U<BR>&nbsp;&nbsp;&nbsp;N<BR>T D<BR>O E<BR>T R<BR>A&nbsp;&nbsp;&nbsp;<BR>L T<BR>&nbsp;&nbsp;&nbsp;R<BR>C E<BR>A A<BR>S T<BR>E M<BR>S E<BR>&nbsp;&nbsp;&nbsp;N<BR>&nbsp;&nbsp;&nbsp;T</font></td>
  		</tr>
  		<tr>
    			<td width="24%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=selectedOrgUnitName%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[27]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[28]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[29]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[30]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[31]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[32]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[33]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[34]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[35]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[36]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[37]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[38]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[39]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[40]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[41]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[42]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[43]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[44]%></FONT></td>
    			<td width="4%" height="50" ALIGN="CENTER"><font face="Arial" size="2"><%=monthlyValues[45]%></FONT></td>
  		</tr>
  	</table>			               			             
</body>
</html>