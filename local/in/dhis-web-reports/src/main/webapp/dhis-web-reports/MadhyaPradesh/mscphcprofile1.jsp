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
	//int selectedOrgUnitID = 259;
	
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
  	String dataValue[] = new String[10];
  	int dataElementIDs[] = { 
	  							0,0,1447,1448,1448,1450,1451,1452,1453,1454,1455,1456,1457,0,1458,1459,1460,1461,
	  							1462,1463,1464,1465,1466,1467,1468,1469,1470,0,1471,1472,1473,1474,1475,0,1476,
	  							1477,1478,1479,1480,1481,1482,1483,0,1484,1485,1486,1487,1488,1489,1490,1491,1492,1493,
								1494,1495,0,0,1496,1497,1498,1499,1500,1501,0,1502,1503,1504,1505,1506,1507,1508,1509,1510,
								1511,1512,1513,1514,1515,0,1516,1517,1518,1604,1605,0,1519,1520,1521,1522,0,1523,1524,
								1525,1526,0,1527,1528,1529,1530,1531,1532,0,1533,1534,1547,1535,1536,1547,1537,1538,1547,
								1539,1540,1547,1541,1542,1547,1543,1544,1547,1545,1546,1547,0,1549,1550,0,1551,1552,1601,
								1553,1554,1601,1555,1556,1601,1557,1558,1601,1559,1560,1601,1561,1562,1601,1563,1564,1601,
								1565,1566,1601,1567,1568,1601,1569,1570,1601,1571,1572,1601,1573,1574,1601,1575,1576,1601,
								1577,1578,1601,1579,1580,1601,1581,1582,1601,1583,1584,1601,1585,1586,1601,1587,1588,1601,
								1589,1590,1601,1591,1592,1601,1593,1594,1601,1595,1596,1601,1597,1598,1601,1599,1600,1601 
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
		<title>SUBCENTRE PHC PROFILE</title>
		<script src="../dhis-web-reports/MadhyaPradesh/JDENamesForSCPHCProFile.js" type="text/javascript" language="Javascript"></script>
		<script>
			function onLoadFun()
			{
				var start = 0;
				var end = 129; 
				while(start < end)
	   	 		{	
					id="cell1"+start;   	 			  
	   	 			document.getElementById(id).innerHTML = slnoForSCPHCProfile[start];
					id="cell2"+start;   	 			  
	   	 			document.getElementById(id).innerHTML = denamesForSCPHCProfile[start];
	   	 			  	 
	   	 			start++;
	   	 		}
			}
		</script>		
	</head>
	<body onload="onLoadFun()">		
 		<center> 
	    	<FONT face="Arial" size="3"><b><u>Performa for Sub-centers on IPHS</u></font>
		</center>
		<br>   		    	    
		<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="left" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    			<td width="75%" align="left">
       				<font face="Arial" size="2"><b>SC Name : <%=selectedOrgUnitName%></b></font>
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
    			<td width="57%" align="center">
       				<font face="Arial" size="3"><b>Service</b></font>
   				</td>
   				<td width="40%" align="center" colspan="6">
       				<font face="Arial" size="3"><b>Value</b></font>
   				</td>   
  			</tr> 			
  		
  		
<%

     try
     {        	               	
       	pst = con.prepareStatement("SELECT value FROM datavalue WHERE sourceid = ? AND periodid = ? AND dataelementid = ?");
       	int count = 0;
       	int count1 = 0;
       	while(count1 < 130)
       	{
       		String id1="cell1"+count1;
 	     	String id2="cell2"+count1;
       		
       		
			if( (count1 >=0 && count1 <=42) || (count1 >=47 && count1 <=86) || (count1==90) || (count1==99) || (count1==102))
			{
       			pst.setInt(1,selectedOrgUnitID);
       			pst.setInt(2,selectedPeriodID);
       			pst.setInt(3,dataElementIDs[count++]);
       		
				rs4 = pst.executeQuery();	
        		if(rs4.next()) dataValue[0] = rs4.getString(1);
        		else dataValue[0] = "";
        		if(dataValue[0] == null) dataValue[0] = "";

//dataValue[0] = ""+dataElementIDs[count-1];
       			%>
       			<tr>
    				<td width="3%" align="center" id="<%=id1%>" name="<%=id1%>"></td>
	    			<td width="57%" align="left" id="<%=id2%>" name="<%=id2%>"></td>
   					<td width="40%" align="center" colspan="6"><font face="Arial" size="2"><%=dataValue[0]%></font>&nbsp;</td>   
	  			</tr> 			
  				<%  				
  			}// if end
  			else if(count1 >=100 && count1 <=101 )	
  			{  				
  				if(count1 == 100)
  				{
  					dataValue[0] = "<b>Other Drugs available</b>";
  					dataValue[1] = "<b>Remarks / Suggestions / Identified</b>";  					
  				}
				else
				{
  					pst.setInt(1,selectedOrgUnitID);
       				pst.setInt(2,selectedPeriodID);
       				pst.setInt(3,dataElementIDs[count++]);       		
					rs4 = pst.executeQuery();	
        			if(rs4.next()) dataValue[0] = rs4.getString(1);
        			else dataValue[0] = "";
        			if(dataValue[0] == null) dataValue[0] = ""; 
//dataValue[0] = ""+dataElementIDs[count-1];
        		
  					pst.setInt(1,selectedOrgUnitID);
       				pst.setInt(2,selectedPeriodID);
       				pst.setInt(3,dataElementIDs[count++]);       		
					rs4 = pst.executeQuery();	
        			if(rs4.next()) dataValue[1] = rs4.getString(1);
        			else dataValue[1] = "";
        			if(dataValue[1] == null) dataValue[1] = ""; 
//dataValue[1] = ""+dataElementIDs[count-1];
        			
              	}
       			%>
       			<tr>
    				<td width="3%" align="center" id="<%=id1%>" name="<%=id1%>"></td>
	    			<td width="57%" align="left" id="<%=id2%>" name="<%=id2%>"></td>
   					<td width="20%" align="center" colspan="3"><font face="Arial" size="2"><%=dataValue[0]%></font>&nbsp;</td>
   					<td width="20%" align="center" colspan="3"><font face="Arial" size="2"><%=dataValue[1]%></font>&nbsp;</td>   
	  			</tr> 			
  				<%  				
  			}
  			else if( (count1 >=87 && count1 <=89) || (count1 >=91 && count1 <=98) || (count1 >=103 && count1 <=128) )	
  			{
  				if(count1 == 87)
  				{
  					dataValue[0] = "<b>Current Availability at S/c</b>";
  					dataValue[1] = "<b>If available, area in Sq.mts</b>";
  					dataValue[2] = "<b>If available, whether staff staying or not</b>";  				
  				}
  				else if(count1 == 91)
  				{
  					dataValue[0] = "<b>Available</b>";
  					dataValue[1] = "<b>Functional</b>";
  					dataValue[2] = "<b>Remarks / Suggestions / identified gaps</b>";  				
  				} 
  				else if(count1 == 103)
  				{
  					dataValue[0] = "<b>Current availability at s/c</b>";
  					dataValue[1] = "<b>If available numbers</b>";
  					dataValue[2] = "<b>Remarks / Suggestions / Identified gaps</b>";  				
  				} 
  				else 
  				{
  					pst.setInt(1,selectedOrgUnitID);
       				pst.setInt(2,selectedPeriodID);
       				pst.setInt(3,dataElementIDs[count++]);       		
					rs4 = pst.executeQuery();	
        			if(rs4.next()) dataValue[0] = rs4.getString(1);
        			else dataValue[0] = "";
        			if(dataValue[0] == null) dataValue[0] = ""; 
//dataValue[0] = ""+dataElementIDs[count-1];
        			
        		
  					pst.setInt(1,selectedOrgUnitID);
       				pst.setInt(2,selectedPeriodID);
       				pst.setInt(3,dataElementIDs[count++]);       		
					rs4 = pst.executeQuery();	
        			if(rs4.next()) dataValue[1] = rs4.getString(1);
        			else dataValue[1] = "";
        			if(dataValue[1] == null) dataValue[1] = ""; 

//dataValue[1] = ""+dataElementIDs[count-1];
        		
        			pst.setInt(1,selectedOrgUnitID);
       				pst.setInt(2,selectedPeriodID);
       				pst.setInt(3,dataElementIDs[count++]);       		
					rs4 = pst.executeQuery();	
        			if(rs4.next()) dataValue[2] = rs4.getString(1);
        			else dataValue[2] = "";
        			if(dataValue[2] == null) dataValue[2] = "";               
//dataValue[2] = ""+dataElementIDs[count-1];
        			
        		}	
       			%>
       			<tr>
    				<td width="3%" align="center" id="<%=id1%>" name="<%=id1%>"></td>
	    			<td width="57%" align="left" id="<%=id2%>" name="<%=id2%>"></td>
   					<td width="10%" align="center" colspan="2"><font face="Arial" size="2"><%=dataValue[0]%></font>&nbsp;</td>
   					<td width="10%" align="center" colspan="2"><font face="Arial" size="2"><%=dataValue[1]%></font>&nbsp;</td>
   					<td width="10%" align="center" colspan="2"><font face="Arial" size="2"><%=dataValue[2]%></font>&nbsp;</td>   
	  			</tr> 			
  				<%  				
  			}
			else if(count1 >=43 && count1 <=46 )	
  			{
  				
  				if(count1 == 43)
  				{
  					dataValue[0] = "<b>Existing/Sctioned</b>";
  					dataValue[1] = "<b>Recommended</b>";
  					dataValue[2] = "<b>Current availability at S/C</b>";
  					dataValue[3] = "<b>Remarks / Suggestion Identified Gaps</b>";
  				} 
  				else 
  				{
  					pst.setInt(1,selectedOrgUnitID);
       				pst.setInt(2,selectedPeriodID);
       				pst.setInt(3,dataElementIDs[count++]);       		
					rs4 = pst.executeQuery();	
        			if(rs4.next()) dataValue[0] = rs4.getString(1);
        			else dataValue[0] = "";
        			if(dataValue[0] == null) dataValue[0] = ""; 
//dataValue[0] = ""+dataElementIDs[count-1];
        			
        		
  					pst.setInt(1,selectedOrgUnitID);
       				pst.setInt(2,selectedPeriodID);
       				pst.setInt(3,dataElementIDs[count++]);       		
					rs4 = pst.executeQuery();	
        			if(rs4.next()) dataValue[1] = rs4.getString(1);
        			else dataValue[1] = "";
        			if(dataValue[1] == null) dataValue[1] = ""; 
//dataValue[1] = ""+dataElementIDs[count-1];
        			
        		
        			pst.setInt(1,selectedOrgUnitID);
       				pst.setInt(2,selectedPeriodID);
       				pst.setInt(3,dataElementIDs[count++]);       		
					rs4 = pst.executeQuery();	
        			if(rs4.next()) dataValue[2] = rs4.getString(1);
        			else dataValue[2] = "";
        			if(dataValue[2] == null) dataValue[2] = "";               
//dataValue[2] = ""+dataElementIDs[count-1];
        			
       			
       				pst.setInt(1,selectedOrgUnitID);
       				pst.setInt(2,selectedPeriodID);
       				pst.setInt(3,dataElementIDs[count++]);       		
					rs4 = pst.executeQuery();	
        			if(rs4.next()) dataValue[3] = rs4.getString(1);
        			else dataValue[3] = "";
        			if(dataValue[3] == null) dataValue[3] = "";
//dataValue[3] = ""+dataElementIDs[count-1];
        			
        		}
       			%>
       			<tr>
    				<td width="3%" align="center" id="<%=id1%>" name="<%=id1%>"></td>
	    			<td width="57%" align="left" id="<%=id2%>" name="<%=id2%>"></td>
   					<td width="10%" align="center" ><font face="Arial" size="2"><%=dataValue[0]%></font>&nbsp;</td>
   					<td width="10%" align="center" colspan="2"><font face="Arial" size="2"><%=dataValue[1]%></font>&nbsp;</td>
   					<td width="10%" align="center" colspan="2"><font face="Arial" size="2"><%=dataValue[2]%></font>&nbsp;</td>
   					<td width="10%" align="center" ><font face="Arial" size="2"><%=dataValue[3]%></font>&nbsp;</td>   
	  			</tr> 			
  				<%  				
  			}

  			count1++;
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