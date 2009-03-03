<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>

<%@ page session="true"%>

<%
    Connection con=null;
    
	Statement st1 = null;
    ResultSet rs1 = null;
	        
    Statement st2 = null;
    ResultSet rs2 = null;

    Statement st3 = null;
    ResultSet rs3 = null;

	PreparedStatement pst = null;
    ResultSet rs4 = null;

    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/mp_dhis2";
          

	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = Integer.parseInt( selectedId );
	//int selectedOrgUnitID = 243;
	
  	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );

	//String startingDate = "2006-01-01";
	//String endingDate = "2006-12-31";
	
	int seletedDataSetID = 240;
	int selectedPeriodID = 0;
	int periodTypeID = 3; // Yearly
	
    String partsOfDataPeriodStartDate[] = startingDate.split("-");        
  	String selectedOrgUnitName = ""; 
  	
  	Hashtable htForDataSetMembers = new Hashtable();
  	
  	
  	
%>  	 	
  	  	  	  	  	
<%

     try
     {
       	Class.forName ("com.mysql.jdbc.Driver").newInstance ();
       	con = DriverManager.getConnection (urlForConnection, userName, password);
        
        st1=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st2=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st3=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
                
        rs1 = st1.executeQuery("SELECT name FROM organisationunit WHERE organisationunitid = "+selectedOrgUnitID);
        if(rs1.next()) selectedOrgUnitName = rs1.getString(1);       
        	
       	rs2 = st2.executeQuery("SELECT datasetmembers.dataelementid,dataelement.name FROM datasetmembers INNER JOIN dataelement ON datasetmembers.dataelementid = dataelement.dataelementid WHERE datasetmembers.datasetid = "+seletedDataSetID);
       	while(rs2.next()) 
       	{
			String temp1 = ""+rs2.getInt(1);
			String temp2 = rs2.getString(2);
	       	htForDataSetMembers.put(temp1,temp2);
       	}// while block end

        rs3 = st3.executeQuery("SELECT periodid FROM period WHERE startdate LIKE '"+startingDate+"' AND periodtypeid = "+periodTypeID);
        if(rs3.next()) selectedPeriodID = rs3.getInt(1);
                	                				   		   			
     } //try block end
     catch(Exception e)  { out.println(e.getMessage());  }                 	 	      
          
%>


<html>
	<head>
		<title>SubCentre PHC PROFILE</title>
	</head>
	<body onload="onLoadFun()">		
 		<center> 
	    	<FONT face="Arial" size="3"><b><u>PHC PROFILE</u></font>
		</center>
		<br>   		    	    
		<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="left" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    			<td width="75%" align="left">
       				<font face="Arial" size="2"><b>PHC Name : <%=selectedOrgUnitName%></b></font>
   				</td>
   				<td width="25%" align="right">
       				<font face="Arial" size="2"><b>Year : <%=partsOfDataPeriodStartDate[0]%></b></font>
   				</td>   
  			</tr> 			
  		</table>
		<br><br>
		<table style="border-collapse: collapse; padding: 0;"  width="100%"  border="1">
  			<tr>
    			<td width="3%" align="left">
       				<font face="Arial" size="2">Sl. No.</font> &nbsp;
       			</td>
    			<td width="72%" align="center">
       				<font face="Arial" size="2"><b>Service</b></font>
   				</td>
   				<td width="25%" align="center">
       				<font face="Arial" size="2"><b>Value</b></font>
   				</td>   
  			</tr> 			
  		
  		
<%

     try
     {        	        
       	Enumeration keysForDataSetMembers = htForDataSetMembers.keys();
       	pst = con.prepareStatement("SELECT value FROM datavalue WHERE sourceid = ? AND periodid = ? AND dataelementid = ?");
       	
       	int count = 1;
       	while(keysForDataSetMembers.hasMoreElements())
       	{
       		String DEID = (String) keysForDataSetMembers.nextElement();
       		String DEName = (String) htForDataSetMembers.get(DEID);
       		int deid = Integer.parseInt(DEID);
       		String dataValue = "";
       		
       		pst.setInt(1,selectedOrgUnitID);
       		pst.setInt(2,selectedPeriodID);
       		pst.setInt(3,deid);
       		
			rs4 = pst.executeQuery();	
        	if(rs4.next()) dataValue = rs4.getString(1);
        	else dataValue = "";
        	if(dataValue == null) dataValue = "";       
        
       		%>
       		<tr>
	       		<td width="3%" align="left">
       				<font face="Arial" size="2"><%=count%></font> &nbsp;</td>
    			<td width="72%" align="left">
       				<font face="Arial" size="2"><%=DEName%></font> &nbsp;</td>
   				<td width="25%" align="center">
       				<font face="Arial" size="2"><%=dataValue%></font> &nbsp;</td>   
  			</tr> 			
  			<%
  			count++;
       	}// while block end
     } //try block end
     catch(Exception e)  { out.println(e.getMessage());  }                 	 	   
     finally
     {
		try
		{
  			if(st1 != null) st1.close();		if(rs1 != null) rs1.close();
  			if(st2 != null) st2.close();		if(rs2 != null) rs2.close();
  			if(st3 != null) st3.close();		if(rs3 != null) rs3.close();	
  			if(pst != null) pst.close();		if(rs4 != null) rs4.close();
				
			if(con!=null)  con.close(); 
		}
		catch(Exception e)   {  out.println(e.getMessage());   }
     } // finally block end	       
%>  		  	    
  		</table>
	</body>	
</html>