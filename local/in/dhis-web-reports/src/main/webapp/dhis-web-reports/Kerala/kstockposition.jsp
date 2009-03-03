
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
    
     Statement st5=null;
     ResultSet rs5=null;
   
 	Statement st6=null;
    ResultSet rs6=null;
   
 	Statement st7=null;
    ResultSet rs7=null;
   

   
    
    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/kl_dhis2";
          
	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = 	Integer.parseInt( selectedId );
	
  
  	String startingDate  = (String) stack.findValue( "startingPeriod" );
	  String endingDate  = (String) stack.findValue( "endingPeriod" );
  
      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = 	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	
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

  	int i=0;

String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

String DataElements[] = {
	
											
  											//"CHLOROQUIN TAB OPENING BALANCE","RECEIVED","ISSUED","BALANCE",
  											"STOCK_DE1","JPHN/JHI_DE146","JPHN_JHI_DE147","STOCK_DE4",							

  											
  											// "ORAL PILLS OPENING BALANCE","RECEIVED","ISSUED","BALANCE",
  											"STOCK_DE5","JPHN/JHI_DE148","JPHN/JHI_DE149","STOCK_DE8",

											//"CONTRACEPTIVE OPENING BALANCE","RECEIVED","ISSUED","BALANCE",
  											"STOCK_DE9","JPHN/JHI_DE150","JPHN/JHI_DE95","STOCK_DE12",
  											
  											//"ORS OPENING BALANCE","RECEIVED","ISSUED","BALANCE",
  											"STOCK_DE13","JPHN/JHI_DE153","JPHN/JHI_DE152","STOCK_DE16",
  										

  											 
  											
										};
	
		List childOrgUnitIDs = new ArrayList();	
		List childOrgUnitNames = new ArrayList();	
		
  		int childOrgUnitCount = 0;  	
  		int count = 0;  	
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
        



         //rs1 = st1.executeQuery("SELECT organisationunit.name FROM organisationunit WHERE id ="+selectedOrgUnitID);
         rs1 = st1.executeQuery("SELECT organisationunit.name FROM organisationunit WHERE organisationunitid ="+selectedOrgUnitID);        
        	if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);     }
        else  {  selectedOrgUnitName = "";       }  
                
//        rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	    	if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	} 
	
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
		

			//rs6=st6.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
			rs6=st6.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");	
			if(rs6.next())  { CHCID = rs6.getInt(1);CHCName = rs6.getString(2);  } 
			else  {  CHCID = 0; CHCName = "";  } 

			//rs7=st7.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+CHCID+")");
			rs7=st7.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+CHCID+")");	
			if(rs7.next())  { talukID = rs7.getInt(1); talukName = rs7.getString(2);  } 
			else  {  talukID = 0; talukName = "";  } 
        


	    	
 } // try block end
    catch(Exception e)  { out.println(e.getMessage());  }
	
    String partsOfDataPeriodStartDate[] = selectedDataPeriodStartDate.split("-");			     
%>
 


<html>

      <head>
      <title>stock position</title>
	  </head>
	  <body>
<center>
<font face="arial" size=2>STOCK POSITION OF CHLOROQUIN TABLET , ORAL PILLS, CONTRACEPTIVE &amp; ORS PACKET<br>
INDIVIDUAL LEVEL AND PHC LEVEL FOR THE MONTH OF  <u><%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%></u>&nbsp; NAME OF PHC  <u><%=selectedOrgUnitName%></u>&nbsp; MOTHER OF PHC <u><%=CHCName %></u></font><br><br>
<%
			
			try
			  {
				  count = 0;
				  while(count < childOrgUnitCount)
				   { 				   
				    if(count%15==0)
				     {%>

<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%" id="AutoNumber1">
  <tr>
    <td width="3%" rowspan="2" align="center"><font face="arial" size=1>Sl. No</font></td>
    <td width="49%" rowspan="2" align="center"><font face="arial" size=1>Name of Staff</font></td>
    <td width="12%" colspan="4" align="center"><font face="arial" size=1>CHLOROQUINT TAB</font></td>
    <td width="12%" colspan="4" align="center"><font face="arial" size=1>ORAL PILLS</font></td>
    <td width="12%" colspan="4" align="center"><font face="arial" size=1>CONTRACEPTIVE</font></td>
    <td width="12%" colspan="4" align="center"><font face="arial" size=1>ORS</font></td>
  </tr>
  <tr>
    <td width="3%" align="center"><font face="arial" size=1>O<br>P<br>E<br>N<br>I<br>N<br>G<br> B<br>A<br>L</font></td>
    <td width="3%" align="center"><font face="arial" size=1>R<br>E<br>C<br>E<br>I<br>V<br>E<br>D</font></td>
    <td width="3%" align="center"><font face="arial" size=1>I<br>S<br>S<br>U<br>E<br>D</font></td>
    <td width="3%" align="center"><font face="arial" size=1>B<br>A<br>L<br>A<br>N<br>C<br>E</font></td>
    <td width="3%" align="center"><font face="arial" size=1>O<br>P<br>E<br>N<br>I<br>N<br>G<br>B<br>A<br>L</font></td>
    <td width="3%" align="center"><font face="arial" size=1>R<br>E<br>C<br>E<br>I<br>V<br>E<br>D</font></td>
    <td width="3%" align="center"><font face="arial" size=1>I<br>S<br>S<br>U<br>E<br>D</font></td>
    <td width="3%" align="center"><font face="arial" size=1>B<br>A<br>L<br>A<br>N<br>C<br>E</font></td>
    <td width="3%" align="center"><font face="arial" size=1>O<br>P<br>E<br>N<br>I<br>N<br>G<br>B<br>A<br>L</font></td>
    <td width="3%" align="center"><font face="arial" size=1>R<br>E<br>C<br>E<br>I<br>V<br>E<br>D</font></td>
    <td width="3%" align="center"><font face="arial" size=1>I<br>S<br>S<br>U<br>E<br>D</font></td>
    <td width="3%" align="center"><font face="arial" size=1>B<br>A<br>L<br>A<br>N<br>C<br>E</font></td>
    <td width="3%" align="center"><font face="arial" size=1>O<br>P<br>E<br>N<br>I<br>N<br>G<br>B<br>A<br>L</font></td>
    <td width="3%" align="center"><font face="arial" size=1>R<br>E<br>C<br>E<br>I<br>V<br>E<br>D</font></td>
    <td width="3%" align="center"><font face="arial" size=1>I<br>S<br>S<br>U<br>E<br>D</font></td>
    <td width="3%" align="center"><font face="arial" size=1>B<br>A<br>L<br>A<br>N<br>C<br>E</font></td>
  </tr>
   <%}
					Integer temp1 = (Integer) childOrgUnitIDs.get(count);
					int currentChildID = temp1.intValue(); 			
					for(i=0;i<DataElements.length;i++)
					 {								
						//rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source ="+currentChildID+" AND dataelement.code like '"+DataElements[i]+"'");
						rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid ="+currentChildID+" AND dataelement.code like '"+DataElements[i]+"'");
												
						if(!rs4.next())  {  tempval[i] = 0;	 }
						else   {  tempval[i] = rs4.getInt(1);  }
						total[i] += tempval[i];
					 } 	
			   
				   %>			     

  <tr>
    <td width="3%" align="center"><font face="arial" size=1><%=(count+1)%>&nbsp;</font></td>
    <td width="49%" align="center"><font face="arial" size=1><%=childOrgUnitNames.get(count)%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[0]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[1]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[2]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[3]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[4]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[5]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[6]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[7]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[8]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[9]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[10]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[11]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[12]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[13]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[14]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=tempval[15]%>&nbsp;</font></td>
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
  				
					if(con!=null)  con.close();
				 }
				catch(Exception e)   {  out.println(e.getMessage());   }
       		 } // finally block end		          		
  		%>

  <tr>
   <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="49%"><font face="arial" size="1">TOTAL</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[0]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[1]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[2]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[3]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[4]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[5]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[6]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[7]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[8]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[9]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[10]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[11]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[12]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[13]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[14]%>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1><%=total[15]%>&nbsp;</font></td>
  </tr>
  <tr>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="49%" ><font face="arial" size="1">PHC LEVEL</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
    <td width="3%" align="center"><font face="arial" size=1>&nbsp;</font></td>
  </tr>
</table>

</body>

</html>