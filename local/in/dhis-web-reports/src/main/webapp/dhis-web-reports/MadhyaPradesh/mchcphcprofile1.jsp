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
  	
  	int dataElementIDs[] = { 
0,0,657,658,660,661,662,663,664,665,0,0,666,667,668,0,0,669,670,671,672,673,674,675,676,
  							677,678,0,0,679,680,681,682,683,684,685,686,687,688,1440,0,0,689,690,691,1007,719,720,721,
  							722,0,0,692,693,694,695,696,1432,0,0,697,698,699,700,701,702,703,704,705,706,707,708,709,
  							0,0,710,711,712,713,714,715,716,717,718,0,0,723,724,725,726,1018,1019,1020,1021,727,728,729,
  							0,730,731,732,733,734,735,736,0,0,737,738,739,740,741,742,743,744,745,746,747,748,749,750,0,
  							0,751,752,0,753,754,755,756,757,758,759,760,0,761,762,763,764,765,766,767,0,0,768,769,0,770,
  							771,772,773,774,775,776,777,778,0,779,780,781,782,783,0,0,784,785,0,786,787,788,789,790,791,
  							792,793,794,0,795,796,797,798,799,800,801,0,0,0,802,803,0,804,805,0,806,807,0,808,809,0,810,
  							811,0,812,813,0,814,815,0,816,817,0,818,819,0,820,821,0,822,823,0,824,825,0,826,827,0,828,
  							829,0,830,831,0,832,833,0,834,835,0,0,0,836,837,838,0,839,840,841,0,842,843,846,0,845,849,0,
  							847,848,844,0,894,851,0,852,850,0,853,854,895,856,857,858,859,860,861,862,0,0,863,864,865,
  							866,867,868,869,870,871,872,873,874,875,0,0,1406,1407,1408,1409,1410,1411,1412,1413,1414,1415,
  							1416,1417,1418,0,0,1419,1420,1421,1422,1431,1424,1425,1426,1427,1428,1429,1430,0,0,0,886,887,888,889
  							
  							};
  	
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
		<title>PHC PROFILE</title>
		<script src="../dhis-web-reports/MadhyaPradesh/JDENamesForPHCPHCProFile.js" type="text/javascript" language="Javascript"></script>
		<script>
			function onLoadFun()
			{
				var start = 0;
				var end = <%=dataElementIDs.length%>; 
				while(start < end)
	   	 		{	
					id="cell1"+start;   	 			  
	   	 			document.getElementById(id).innerHTML = slnoForPHCPHCProfile[start];
					id="cell2"+start;   	 			  
	   	 			document.getElementById(id).innerHTML = denamesForPHCPHCProfile[start];
	   	 			  	 
	   	 			start++;
	   	 		}
			}
		</script>		
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
  				<td width="3%" align="center">
       				<font face="Arial" size="3"><b>Sl. No.</b></font>
   				</td>
    			<td width="72%" align="center">
       				<font face="Arial" size="3"><b>Service</b></font>
   				</td>
   				<td width="25%" align="center">
       				<font face="Arial" size="3"><b>Value</b></font>
   				</td>   
  			</tr> 			
  		
  		
<%

     try
     {        	               	
       	pst = con.prepareStatement("SELECT value FROM datavalue WHERE sourceid = ? AND periodid = ? AND dataelementid = ?");
       	int count = 0;
       	while(count < dataElementIDs.length)
       	{
       		String id1="cell1"+count;
 	     	String id2="cell2"+count;
       		String dataValue = "";
       		
       		pst.setInt(1,selectedOrgUnitID);
       		pst.setInt(2,selectedPeriodID);
       		pst.setInt(3,dataElementIDs[count]);
       		
			rs4 = pst.executeQuery();	
        	if(rs4.next()) dataValue = rs4.getString(1);
        	else dataValue = "";
        	if(dataValue == null) dataValue = "";       
        
       		%>
       		<tr>
    			<td width="3%" align="center" id="<%=id1%>" name="<%=id1%>">
       				<font face="Arial" size="2"><b></b></font>
   				</td>
    			<td width="72%" align="left" id="<%=id2%>" name="<%=id2%>">
       				<font face="Arial" size="2"></font> &nbsp;
       			</td>
   				<td width="25%" align="center">
       				<font face="Arial" size="2"><%=dataValue%></font> &nbsp;
       			</td>   
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